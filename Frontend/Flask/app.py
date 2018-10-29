import subprocess
import requests
import json
import os
import uuid
import urllib
import numpy as np
from flask import Flask, render_template, request, redirect, url_for, flash, jsonify, session, g
from flask_bootstrap import Bootstrap
from pprint import pprint
from datetime import datetime, timedelta
from flask_jwt import JWT, jwt_required, current_identity
from werkzeug.security import safe_str_cmp
from werkzeug.utils import secure_filename
from functools import wraps
import hashlib, uuid
import configparser
import atexit
from apscheduler.scheduler import Scheduler

# GLOBAL VARIABLES
globalPath = "/BDOHarmonization/BigDataOcean-Harmonization"

GlobalURLJWT = ""
UPLOAD_FOLDER = globalPath+'/Backend/AddDatasets'
ALLOWED_EXTENSIONS = set(['nc', 'csv', 'xlsx', 'xls'])

#JWT authorization (parser and handler)
# BDO.ini where JWT token is saved
config = configparser.ConfigParser()
config.read(globalPath + '/Backend/bdodatasets/bdo.ini')

Authorization = config['DEFAULT']['AUTHORIZATION_JWT']

# Authentication JWT for APIs
# Class for security authentication JWT
class User(object):
	def __init__(self, id, username, password, salt):
		self.id = id
		self.username = username
		self.password = password
		self.salt = salt

	def __str__(self):
		return "User(id='%s')" % self.id

users = [
	User(1, 'admin', '', ''),
	User(2, 'test', '', '')
]

username_table = {u.username: u for u in users}
userid_table = {u.id: u for u in users}

app = Flask(__name__)
bootstrap = Bootstrap(app)
# configuring the path of the upload folder
app.config['UPLOAD_FOLDER'] = UPLOAD_FOLDER
app.config['SECRET_KEY'] = 'super-secret'
app.config['JWT_EXPIRATION_DELTA'] = timedelta(seconds = 3720) # Expiration time of JWT token is for 62 minutes

# Function that sync JWT token of parser tool when flask runs
def syncWhenRunFlask():
	tokenCommand = globalPath + '/Backend/bdodatasets/target/BDODatasets-bdodatasets/BDODatasets/bin/fetchJWTToken'
	print (subprocess.check_output([tokenCommand], shell="True"))
	config.read(globalPath + '/Backend/bdodatasets/bdo.ini')
	global Authorization
	Authorization = config['DEFAULT']['AUTHORIZATION_JWT']

# Thread in background to update every year the JWT Authorization Token (Parser tool)
cron = Scheduler(daemon=True)
# Explicitly kick off the background thread
cron.start()

# when server runs for the first time, it checks if one of the vocabularies does not exist and do the extraction process
if not os.path.exists(globalPath + '/Backend/AddDatasets/ontologiesN3/bdo.n3'):
	bdo = globalPath + '/Backend/bdodatasets/target/BDODatasets-bdodatasets/BDODatasets/bin/extractVocabularies bdo'
	print (subprocess.check_output([bdo], shell="True"))
	geolocbdo = globalPath + '/Backend/bdodatasets/target/BDODatasets-bdodatasets/BDODatasets/bin/extractVocabularies geolocbdo'
	print (subprocess.check_output([geolocbdo], shell="True"))
	inspire = globalPath + '/Backend/bdodatasets/target/BDODatasets-bdodatasets/BDODatasets/bin/extractVocabularies inspire'
	print (subprocess.check_output([inspire], shell="True"))
	eionet = globalPath + '/Backend/bdodatasets/target/BDODatasets-bdodatasets/BDODatasets/bin/extractVocabularies eionet'
	print (subprocess.check_output([eionet], shell="True"))

# Command = Thread in background to update every hour the JWT Authorization Token (Parser tool)
@cron.interval_schedule(seconds=3600)
def fetchEveryHour():
	syncWhenRunFlask()

# bdo, geolocbdo, inspire, eionet = Thread in background to update every year the vocabularies (Vocabulary Repository)
@cron.interval_schedule(seconds=31536000)
def fetchEveryYear():
	bdo = globalPath + '/Backend/bdodatasets/target/BDODatasets-bdodatasets/BDODatasets/bin/extractVocabularies bdo'
	print (subprocess.check_output([bdo], shell="True"))
	geolocbdo = globalPath + '/Backend/bdodatasets/target/BDODatasets-bdodatasets/BDODatasets/bin/extractVocabularies geolocbdo'
	print (subprocess.check_output([geolocbdo], shell="True"))
	inspire = globalPath + '/Backend/bdodatasets/target/BDODatasets-bdodatasets/BDODatasets/bin/extractVocabularies inspire'
	print (subprocess.check_output([inspire], shell="True"))
	eionet = globalPath + '/Backend/bdodatasets/target/BDODatasets-bdodatasets/BDODatasets/bin/extractVocabularies eionet'
	print (subprocess.check_output([eionet], shell="True"))

# Shutdown your cron thread if the web process is stopped
atexit.register(lambda: cron.shutdown(wait=False))

# Authentication JWT for APIs
def authenticate(username, password):
    user = username_table.get(username, None)
    hashed_password = hashlib.sha512(password.encode('utf-8') + user.salt.encode('utf-8')).hexdigest()
    if user and safe_str_cmp(user.password.encode('utf-8'), hashed_password.encode('utf-8')):
        return user

def identity(payload):
    user_id = payload['identity']
    return userid_table.get(user_id, None)

jwt = JWT(app, authenticate, identity)

# check if an extension is valid
def allowed_file(filename):
    return '.' in filename and \
           filename.rsplit('.', 1)[1].lower() in ALLOWED_EXTENSIONS

# Routing to index
@app.route('/')
def index():
	# Calls shell listDatasets to get all the datasets stored on jena fuseki
	command = globalPath + '/Backend/bdodatasets/target/BDODatasets-bdodatasets/BDODatasets/bin/listDatasets'
	try:
		process = subprocess.check_output([command], shell="True")
		# other column settings -> http://bootstrap-table.wenzhixin.net.cn/documentation/#column-options
		columns = [{
		"field": "identifier", # which is the field's name of data key 
		"title": "Metadata ID", # display as the table header's name
		"sortable": True,
		},{
		"field": "title", # which is the field's name of data key 
		"title": "Title", # display as the table header's name
		"sortable": True,
		},
		{
		"field": "description",
		"title": "Description",
		"sortable": True,
		}]
		# print (process.decode('utf-8'))
		parsed_output = json.loads(process.decode('utf-8'))
		data = parsed_output
		return render_template('index.html',
			data=data,
			columns=columns)
	except subprocess.CalledProcessError as e:
		print(e)
		return render_template('500.html')
	except ValueError as e:  # includes simplejson.decoder.JSONDecodeError
		print(e)
		return render_template('500.html')

@app.before_request
def get_current_user():
    g.user = None
    username = session.get('user_id')
    if username is not None:
        g.user = username

# login required decorator
def login_required(f):
    @wraps(f)
    def wrap(*args, **kwargs):
        if 'user_id' in session:
            return f(*args, **kwargs)
        else:
            flash('You need to login first.')
            return redirect(url_for('login'))
    return wrap

@app.route('/login', methods=['GET', 'POST'])
def login():
	error = None
	flag = False
	if request.method == 'POST':
		Inputusername = request.form['username'] 
		Inputpassword = request.form['password']
		for u in users:
			if u.username == Inputusername:
				hashed_password = hashlib.sha512(Inputpassword.encode('utf-8') + u.salt.encode('utf-8')).hexdigest()
				if u.password == hashed_password:
					flag = True
					break

		if flag == False:
			error = 'Invalid Credentials. Please try again.'
		else:
			session['user_id'] = Inputusername
			flash('You are log in.')
			return redirect('/')
	return render_template('login.html', error=error)

@app.route('/logout')
def logout():
    session.pop('user_id', None)
    flash('You are log out.')
    return redirect('/')

# List of files that does not have metadata
@app.route('/list')
def list():
	parsed_output = requests.get(GlobalURLJWT + 'fileHandler/file/metadata/empty', headers={'Authorization': Authorization})
	columns = [{
	"field": "id",
	"title": "FileId",
	"sortable": True,
	},
	{
	"field": "fileName",
	"title": "Title",
	"sortable": True,
	},
	{
	"field": "hdfsFullURI",
	"title": "URI",
	"sortable": True,
	},
	{
	"field": "storedAt",
	"title": "Stored Date",
	"sortable": True,
	}]
	data = json.loads(parsed_output.content.decode('utf-8'))
	if parsed_output.status_code == 200:
		return render_template('listFiles.html',
				data=data,
				columns=columns)
	else:
		return render_template('500.html')

# Routing to addMetadata form
@app.route('/addMetadata', methods=['GET', 'POST'])
@login_required
def parse():
	try:
		extractDatafromParser()

		if request.method == 'POST':
			return render_template('addMetadata.html', dataset='', idFile='')
		elif request.method == 'GET':
			idFile = request.args['idFile']
			return render_template('addMetadata.html', dataset='', idFile=idFile)
	except ValueError as e:  # includes simplejson.decoder.JSONDecodeError
		print(e)
		return render_template('500.html')

@app.route('/addMetadata/Copernicus', methods=['GET', 'POST'])
@login_required
def addCopernicus():
	try:
		extractDatafromParser()
		if request.method == 'POST':
			uri = request.form['uri']
			# if adding a Copernicus dataset, the shell suggest is called to parse the xml file and get metadata
			command = globalPath + '/Backend/bdodatasets/target/BDODatasets-bdodatasets/BDODatasets/bin/suggest "%s" Coppernicus' %uri
			try:
				process = subprocess.check_output([command], shell="True")
			except subprocess.CalledProcessError as e:
				print(e)
				return render_template('500.html')
			# metadata parsed is converted into json class datasetInfo to be used inside the html form
			parsed_output = json.loads(process.decode('utf-8'))
			dataset = datasetInfo(**parsed_output)

			return render_template('addMetadata.html', dataset=dataset, idFile='')
		elif request.method == 'GET':
			uri = request.args['uri']
			print (uri)
			# if adding a Copernicus dataset, the shell suggest is called to parse the xml file and get metadata
			command = globalPath + '/Backend/bdodatasets/target/BDODatasets-bdodatasets/BDODatasets/bin/suggest "%s" Coppernicus' %uri
			print (command)
			try:
				process = subprocess.check_output([command], shell="True")
			except subprocess.CalledProcessError as e:
				print(e)
				return render_template('500.html')
			# metadata parsed is converted into json class datasetInfo to be used inside the html form
			parsed_output = json.loads(process.decode('utf-8'))
			dataset = datasetInfo(**parsed_output)

			return render_template('addMetadata.html', dataset=dataset, idFile='')
	except ValueError as e:  # includes simplejson.decoder.JSONDecodeError
		print(e)
		return render_template('500.html')

@app.route('/addMetadata/NetCDF', methods=['GET', 'POST'])
@login_required
def addNetCDF():
	try:
		extractDatafromParser()
		if request.method == 'POST':
			file = request.files['fileNetcdf']
			if file.filename != '':
				# Verify if the file is .nc
				if file and allowed_file(file.filename):
					# Create a general filename
					filename = file.filename
					# Saving the file in the UPLOAD_FOLDER with the filename
					file.save(os.path.join(app.config['UPLOAD_FOLDER'], filename))
					path_fileNetcdf = UPLOAD_FOLDER + "/" + filename
					# print (path_fileNetcdf)
					command = globalPath + '/Backend/bdodatasets/target/BDODatasets-bdodatasets/BDODatasets/bin/suggest "%s" FileNetcdf' %path_fileNetcdf
					try:
						process = subprocess.check_output([command], shell="True")
					except subprocess.CalledProcessError as e:
						print(e)
						return render_template('500.html')
					# metadata parsed is converted into json class datasetInfo to be used inside the html form
					parsed_output = json.loads(process.decode('utf-8'))
					dataset = datasetInfo(**parsed_output)

					return render_template('addMetadata.html', dataset=dataset, idFile='')

		elif request.method == 'GET':
			file = request.args['file']
			idFile = request.args['idFile']
			command = globalPath + '/Backend/bdodatasets/target/BDODatasets-bdodatasets/BDODatasets/bin/suggest "%s" Netcdf' %file
			try:
				process = subprocess.check_output([command], shell="True")
			except subprocess.CalledProcessError as e:
				print(e)
				return render_template('500.html')
			# parsing output from maven script, to avoid log comments
			process = process.split(b'\n')
			# metadata parsed is converted into json class datasetInfo to be used inside the html form
			parsed_output = json.loads(process[1].decode('utf-8'))
			dataset = datasetInfo(**parsed_output)

			return render_template('addMetadata.html', dataset=dataset, idFile=idFile)
	except ValueError as e:  # includes simplejson.decoder.JSONDecodeError
		print(e)
		return render_template('500.html')

@app.route('/addMetadata/CSV', methods=['GET', 'POST'])
@login_required
def addCsv():
	try:
		extractDatafromParser()
		if request.method == 'POST':
			file = request.files['fileCsv']
			if file.filename != '':
				# Verify if the file is .csv
				if file and allowed_file(file.filename):
					# Create a general filename
					filename = file.filename
					# Saving the file in the UPLOAD_FOLDER with the filename
					file.save(os.path.join(app.config['UPLOAD_FOLDER'], filename))
					path_fileCsv = UPLOAD_FOLDER + "/" + filename
					# print (path_fileNetcdf)
					command = globalPath + '/Backend/bdodatasets/target/BDODatasets-bdodatasets/BDODatasets/bin/suggest "%s" FileCSV' %path_fileCsv
					try:
						process = subprocess.check_output([command], shell="True")
					except subprocess.CalledProcessError as e:
						print(e)
						return render_template('500.html')
					# metadata parsed is converted into json class datasetInfo to be used inside the html form
					parsed_output = json.loads(process.decode('utf-8'))
					dataset = datasetInfo(**parsed_output)

					return render_template('addMetadata.html', dataset=dataset, idFile='')
			
		elif request.method == 'GET':
			file = request.args['file']
			idFile = request.args['idFile']
			command = globalPath + '/Backend/bdodatasets/target/BDODatasets-bdodatasets/BDODatasets/bin/suggest "%s" CSV' %file
			try:
				process = subprocess.check_output([command], shell="True")
			except subprocess.CalledProcessError as e:
				print(e)
				return render_template('500.html')
			# parsing output from maven script, to avoid log comments
			process = process.split(b'\n')
			# metadata parsed is converted into json class datasetInfo to be used inside the html form
			parsed_output = json.loads(process[1].decode('utf-8'))
			dataset = datasetInfo(**parsed_output)

			return render_template('addMetadata.html', dataset=dataset, idFile=idFile)
	except ValueError as e:  # includes simplejson.decoder.JSONDecodeError
		print(e)
		return render_template('500.html')

@app.route('/addMetadata/EXCEL', methods=['GET', 'POST'])
@login_required
def addExcel():
	try:
		extractDatafromParser()
		if request.method == 'POST':
			file = request.files['fileExcel']
			if file.filename != '':
				# Verify if the file is excel
				if file and allowed_file(file.filename):
					# Create a general filename
					filename = file.filename
					# Saving the file in the UPLOAD_FOLDER with the filename
					file.save(os.path.join(app.config['UPLOAD_FOLDER'], filename))
					path_fileExcel = UPLOAD_FOLDER + "/" + filename
					# print (path_fileExcel)
					command = globalPath + '/Backend/bdodatasets/target/BDODatasets-bdodatasets/BDODatasets/bin/suggest "%s" FileExcel' %path_fileExcel
					try:
						process = subprocess.check_output([command], shell="True")
					except subprocess.CalledProcessError as e:
						print(e)
						return render_template('500.html')
					# metadata parsed is converted into json class datasetInfo to be used inside the html form
					parsed_output = json.loads(process.decode('utf-8'))
					dataset = datasetInfo(**parsed_output)

					return render_template('addMetadata.html', dataset=dataset, idFile='')
			
		elif request.method == 'GET':
			file = request.args['file']
			idFile = request.args['idFile']
			command = globalPath + '/Backend/bdodatasets/target/BDODatasets-bdodatasets/BDODatasets/bin/suggest "%s" Excel' %file
			try:
				process = subprocess.check_output([command], shell="True")
			except subprocess.CalledProcessError as e:
				print(e)
				return render_template('500.html')
			# parsing output from maven script, to avoid log comments
			process = process.split(b'\n')
			# metadata parsed is converted into json class datasetInfo to be used inside the html form
			parsed_output = json.loads(process[1].decode('utf-8'))
			dataset = datasetInfo(**parsed_output)

			return render_template('addMetadata.html', dataset=dataset, idFile=idFile)
	except ValueError as e:  # includes simplejson.decoder.JSONDecodeError
		print(e)
		return render_template('500.html')

# Routing to save new dataset
@app.route('/save', methods=['GET','POST'])
@login_required
def save():
	try:
		if request.method == 'POST':
			identifier = request.form['identifier']
			idFile = request.form['idFile']
			title = request.form['title']
			description = request.form['description']
			subject = request.form['tokenfield_subject']
			keywords= request.form['tokenfield_keywords']
			standards = request.form['standards']
			formats = request.form['tokenfield_format']
			language = request.form['tokenfield_language']
			homepage = request.form['homepage']
			publisher = request.form['publisher']
			source = request.form['source']
			observations = request.form['observations']
			storageTable = request.form['storageTable']
			accessRights = request.form['access_rights']
			issuedDate = request.form['issued_date']
			modifiedDate = request.form['modified_date']
			geoLocation = request.form['tokenfield_geo_loc']
			spatialWest = request.form['geo_coverageW']
			spatialEast = request.form['geo_coverageE']
			spatialSouth = request.form['geo_coverageS']
			spatialNorth = request.form['geo_coverageN']
			coordinateSystem = request.form['coordinate_sys']
			verticalCoverageFrom = request.form['vert_coverage_from']
			verticalCoverageTo = request.form['vert_coverage_to']
			verticalLevel = request.form['vertical_level']
			temporalCoverageBegin = request.form['temp_coverage_begin']
			temporalCoverageEnd = request.form['temp_coverage_end']
			timeResolution = request.form['time_reso']
			parservariable = request.form.getlist('parser_variable')
			jsonvariable = request.form.getlist('json_variable')
			unitvariable = request.form.getlist('unit_variable')
			# delete the empty elements in the list 
			parservariable[:] = [item for item in parservariable if item != '']
			parserlist = np.array(parservariable).tolist()
			jsonvariable[:] = [item for item in jsonvariable if item != '']
			jsonlist = np.array(jsonvariable).tolist()

			variables = []
			for i in range(len(parserlist)):
				variables.append(parserlist[i] + " -- " + unitvariable[i] + " -- " + jsonlist[i])

			profileName = request.form['nameProfile']

			if identifier  != "":
				check_existance = "<http://bigdataocean.eu/bdo/"+identifier+"> "+idFile
				datasetType = ""	
			else:
				identifier = str(uuid.uuid4())
				check_existance = title+">"+publisher+">"+issuedDate+">"+idFile
				datasetType = "other"

			# add the values to the datasetInfo class
			dataset = datasetInfo (identifier, title, description, subject, keywords, standards, formats, language, homepage, publisher, 
				source, observations, storageTable,
				accessRights, issuedDate, modifiedDate, geoLocation, spatialWest, spatialEast, spatialSouth, spatialNorth, 
				coordinateSystem, verticalCoverageFrom, verticalCoverageTo, verticalLevel, temporalCoverageBegin, 
				temporalCoverageEnd, timeResolution, variables, profileName)
			# create the json of the datasetInfo class
			datasetJson = json.dumps(dataset.__dict__)
			with open(globalPath+'/Backend/AddDatasets/jsonDataset.json','w') as file:
				file.write(datasetJson)
				file.close()
			path2json = globalPath + "/Backend/AddDatasets/jsonDataset.json"

				
			# Calls shell insertDataset to connect to jena fuseki and add dataset via sparql query
			command = globalPath + '/Backend/bdodatasets/target/BDODatasets-bdodatasets/BDODatasets/bin/insertDataset "%s" "%s" "%s"' %(datasetType, check_existance, path2json)
			# print(command)
			try:
				process = subprocess.check_output([command], shell="True")
				
			except subprocess.CalledProcessError as e:
				print(e)
				return render_template('500.html')
			# when the dataset is added to jena fuseki, redirects to the metadataInfo web page corresponding to the identifier
			if b'Successful' in process and not b'Error' in process:
				return redirect(url_for('metadataInfo', identifier=identifier))
			elif b'Error1' in process:
				return render_template('404.html', error='Metadata has been added but API: Profile is not being added.')
			elif b'Error2' in process:
				return render_template('404.html', error='Metadata has been added but API: Identifier is not being added to idfile.')
			elif b'Error3' in process:
				return render_template('404.html', error='URI already exists.')
	except ValueError as e:  # includes simplejson.decoder.JSONDecodeError
		print(e)
		return render_template('500.html')

# Routing to modify a corresponding dataset
@app.route('/modify/<identifier>', methods=['GET', 'POST'])
@login_required
def edit(identifier):
	try:
		extractDatafromParser()
		if request.method == 'GET':
			uri = "<http://bigdataocean.eu/bdo/"+identifier+"> \n"
			comm = globalPath + '/Backend/bdodatasets/target/BDODatasets-bdodatasets/BDODatasets/bin/getDataset "%s"' %uri
			try:
				process = subprocess.check_output([comm], shell="True")
			except subprocess.CalledProcessError as e:
				print(e)
				return render_template('500.html')
			# metadata parsed is converted into json class datasetInfo to be used inside the html form
			parsed_output = json.loads(process.decode('utf-8'))
			dataset = datasetInfo(**parsed_output)
			return render_template('editMetadata.html', dataset=dataset)
			
	except ValueError as e:  # includes simplejson.decoder.JSONDecodeError
		print(e)
		return render_template('500.html')

# Routing to save new dataset
@app.route('/edit', methods=['GET','POST'])
@login_required
def editing():
	try:
		if request.method == 'POST':
			identifier = request.form['identifier']
			title = request.form['title']
			description = request.form['description']
			subject = request.form['tokenfield_subject']
			keywords= request.form['tokenfield_keywords']
			standards = request.form['standards']
			formats = request.form['tokenfield_format']
			language = request.form['tokenfield_language']
			homepage = request.form['homepage']
			publisher = request.form['publisher']
			source = request.form['source']
			observations = request.form['observations']
			storageTable = request.form['storageTable']
			accessRights = request.form['access_rights']
			issuedDate = request.form['issued_date']
			modifiedDate = request.form['modified_date']
			geoLocation = request.form['tokenfield_geo_loc']
			spatialWest = request.form['geo_coverageW']
			spatialEast = request.form['geo_coverageE']
			spatialSouth = request.form['geo_coverageS']
			spatialNorth = request.form['geo_coverageN']
			coordinateSystem = request.form['coordinate_sys']
			verticalCoverageFrom = request.form['vert_coverage_from']
			verticalCoverageTo = request.form['vert_coverage_to']
			verticalLevel = request.form['vertical_level']
			temporalCoverageBegin = request.form['temp_coverage_begin']
			temporalCoverageEnd = request.form['temp_coverage_end']
			timeResolution = request.form['time_reso']

			parservariable = request.form.getlist('parser_variable')
			jsonvariable = request.form.getlist('json_variable')
			unitvariable = request.form.getlist('unit_variable')
			# delete the empty elements in the list 
			parservariable[:] = [item for item in parservariable if item != '']
			parserlist = np.array(parservariable).tolist()
			jsonvariable[:] = [item for item in jsonvariable if item != '']
			jsonlist = np.array(jsonvariable).tolist()

			variables = []
			for i in range(len(parserlist)):
				variables.append(parserlist[i] + " -- " + unitvariable[i] + " -- " + jsonlist[i])

			if identifier  != "":
				check_existance = "<http://bigdataocean.eu/bdo/"+identifier+"> "
				datasetType = ""	
			else:
				identifier = str(uuid.uuid4())
				check_existance = title+">"+publisher+">"+issuedDate+">"
				datasetType = "other"

			# add the values to the datasetInfo class
			dataset = datasetInfo (identifier, title, description, subject, keywords, standards, formats, language, homepage, publisher, 
				source, observations, storageTable,
				accessRights, issuedDate, modifiedDate, geoLocation, spatialWest, spatialEast, spatialSouth, spatialNorth, 
				coordinateSystem, verticalCoverageFrom, verticalCoverageTo, verticalLevel, temporalCoverageBegin, temporalCoverageEnd, 
				timeResolution, variables, "")
			# create the json of the datasetInfo class
			datasetJson = json.dumps(dataset.__dict__)
			with open(globalPath+'/Backend/AddDatasets/jsonDataset.json','w') as file:
				file.write(datasetJson)
				file.close()
			path2json = globalPath + "/Backend/AddDatasets/jsonDataset.json"

			# Calls shell insertDataset to connect to jena fuseki and add dataset via sparql query
			command = globalPath + '/Backend/bdodatasets/target/BDODatasets-bdodatasets/BDODatasets/bin/deleteDataset "%s"' %identifier
			try:
				process = subprocess.check_output([command], shell="True")
				
			except subprocess.CalledProcessError as e:
				print(e)
				return render_template('500.html')
			# when the dataset is added to jena fuseki, redirects to the metadataInfo web page corresponding to the identifier
			if b'Successful' in process:
				# Calls shell insertDataset to connect to jena fuseki and add dataset via sparql query
				command2 = globalPath + '/Backend/bdodatasets/target/BDODatasets-bdodatasets/BDODatasets/bin/insertDataset "%s" "%s" "%s"' %(datasetType, check_existance, path2json)
				# print(command)
				try:
					process = subprocess.check_output([command2], shell="True")
					
				except subprocess.CalledProcessError as e:
					print(e)
					return render_template('500.html')
				# when the dataset is added to jena fuseki, redirects to the metadataInfo web page corresponding to the identifier
				if b'Successful' in process:
					return redirect(url_for('metadataInfo',identifier=identifier))
				else:
					return render_template('404.html', error='URI already exists.')
			else:
				return render_template('404.html', error='There was an error while modifying the dataset.')

	except ValueError:  # includes simplejson.decoder.JSONDecodeError
		print(e)
		return render_template('500.html')

# Routing to delete a corresponding dataset
@app.route('/delete/<identifier>', methods=['GET', 'POST'])
@login_required
def delete(identifier):
	# Calls shell insertDataset to connect to jena fuseki and add dataset via sparql query
	command = globalPath + '/Backend/bdodatasets/target/BDODatasets-bdodatasets/BDODatasets/bin/deleteDataset "%s"' %identifier
	try:
		process = subprocess.check_output([command], shell="True")
		
	except subprocess.CalledProcessError as e:
		print(e)
		return render_template('500.html')
	# when the dataset is added to jena fuseki, redirects to the metadataInfo web page corresponding to the identifier
	if b'Successful' in process:
		return redirect(url_for('index'))
	else:
		return render_template('404.html', error='There was an error while deleting the dataset.')

# Routing to see metadata of an specific dataset
@app.route('/metadataInfo/<identifier>', methods=['GET', 'POST'])
def metadataInfo(identifier):
	try:
		if request.method == 'GET':
			extractDatafromParser()
			# Extracting subject/keywords/marineregions.json
			file = open(globalPath + '/Frontend/Flask/static/json/subject.json', 'r')
			subjectJson = json.load(file)
			file = open(globalPath + '/Frontend/Flask/static/json/keywords.json', 'r')
			keywordsJson = json.load(file)
			file = open(globalPath + '/Frontend/Flask/static/json/marineregions.json', 'r')
			geoLocationJson = json.load(file)

			uri = "<http://bigdataocean.eu/bdo/"+identifier+"> \n"
			# Calls shel getDataset to obtain all metadata of a dataset from jena fuseki
			comm = globalPath + '/Backend/bdodatasets/target/BDODatasets-bdodatasets/BDODatasets/bin/getDataset "%s"' %uri
			try:
				process = subprocess.check_output([comm], shell="True")
			except subprocess.CalledProcessError as e:
				print(e)
				return render_template('500.html')
			# metadata parsed is converted into json class datasetInfo to be used inside the html form
			parsed_output = json.loads(process.decode('utf-8'))
			dataset = datasetInfo(**parsed_output)
			# show the name -- url in subject/keywords/geoLocation
			dataset.subject = nameURL(dataset.subject, subjectJson)
			dataset.keywords = nameURL(dataset.keywords, keywordsJson)
			dataset.geoLocation = nameURL(dataset.geoLocation, geoLocationJson)
			return render_template('metadataInfo.html', dataset=dataset)
	except ValueError:  # includes simplejson.decoder.JSONDecodeError
		print(e)
		return render_template('500.html')

# show the name -- url in subject/keywords/geoLocation
def nameURL(listElement, listJson):
	token = listElement.split(", ")
	l = ""
	for t in token:
		for s in listJson:
			if s["value"] == t:
				if l == "":
					l = s["text"] + " -- " + t
				else:
					l = l + ", " + s["text"] + " -- " + t
				break
	return l

@app.route('/endpoint', methods=['GET', 'POST'])
def endpoint():
	return render_template('sparqlEndpoint.html')

#APIs
@app.route('/api', methods=['GET', 'POST'])
def api():
	extractDatafromParser()
	return render_template('api.html')

@app.route('/api/v1/dataset/list', methods=['GET'])
@jwt_required()
def listDataset():
	if request.method == 'GET':
		# Calls shell apiListDatasetByVariable to obtain the list of datasets that contains the variables
		comm = globalPath + '/Backend/bdodatasets/target/BDODatasets-bdodatasets/BDODatasets/bin/api "%s" "%s"' %("1", "")
		try:
			process = subprocess.check_output([comm], shell="True")
		except subprocess.CalledProcessError as e:
			print(e)
			return render_template('500.html')
		# metadata parsed is converted into json class datasetInfo to be used inside the html form
		parsed_output = json.loads(process.decode('utf-8'))
		return jsonify(parsed_output)

@app.route('/api/v1/dataset/searchDataset', methods=['GET'])
@jwt_required()
def searchDataset():
	if request.method == 'GET':
		# Calls shell apiListDatasetByVariable to obtain the list of datasets that contains the variables
		param = 'bdo:'+request.args['search']
		print(param)
		comm = globalPath + '/Backend/bdodatasets/target/BDODatasets-bdodatasets/BDODatasets/bin/api "%s" "%s"' %("2", param)
		try:
			process = subprocess.check_output([comm], shell="True")
		except subprocess.CalledProcessError as e:
			print(e)
			return render_template('500.html')
		# metadata parsed is converted into json class datasetInfo to be used inside the html form
		parsed_output = json.loads(process.decode('utf-8'))
		return jsonify(parsed_output)

@app.route('/api/v1/dataset/searchSubject', methods=['GET'])
@jwt_required()
def searchSubject():
	if request.method == 'GET':
		# Calls shell apiListDatasetByVariable to obtain the list of datasets that contains the variables
		comm = globalPath + '/Backend/bdodatasets/target/BDODatasets-bdodatasets/BDODatasets/bin/api "%s" "%s"' %("3", request.args['search'])
		try:
			process = subprocess.check_output([comm], shell="True")
		except subprocess.CalledProcessError as e:
			print(e)
			return render_template('500.html')
		# metadata parsed is converted into json class datasetInfo to be used inside the html form
		parsed_output = json.loads(process.decode('utf-8'))
		return jsonify(parsed_output)

@app.route('/api/v1/dataset/searchKeyword', methods=['GET'])
@jwt_required()
def searchKeyword():
	if request.method == 'GET':
		# Calls shell apiListDatasetByVariable to obtain the list of datasets that contains the variables
		comm = globalPath + '/Backend/bdodatasets/target/BDODatasets-bdodatasets/BDODatasets/bin/api "%s" "%s"' %("4", request.args['search'])
		try:
			process = subprocess.check_output([comm], shell="True")
		except subprocess.CalledProcessError as e:
			print(e)
			return render_template('500.html')
		# metadata parsed is converted into json class datasetInfo to be used inside the html form
		parsed_output = json.loads(process.decode('utf-8'))
		return jsonify(parsed_output)	

@app.route('/api/v1/dataset/searchGeoLocation', methods=['GET'])
@jwt_required()
def searchGeoLocation():
	if request.method == 'GET':
		# Calls shell apiListDatasetByVariable to obtain the list of datasets that contains the variables
		comm = globalPath + '/Backend/bdodatasets/target/BDODatasets-bdodatasets/BDODatasets/bin/api "%s" "%s"' %("5", request.args['search'])
		try:
			process = subprocess.check_output([comm], shell="True")
		except subprocess.CalledProcessError as e:
			print(e)
			return render_template('500.html')
		# metadata parsed is converted into json class datasetInfo to be used inside the html form
		parsed_output = json.loads(process.decode('utf-8'))
		return jsonify(parsed_output)	

@app.route('/api/v1/dataset/searchGeoCoverage', methods=['GET'])
@jwt_required()
def searchGeoCoverage():
	if request.method == 'GET':
		# Calls shell apiListDatasetByVariable to obtain the list of datasets that contains the variables
		param = request.args['W']+','+request.args['E']+','+request.args['S']+','+request.args['N']
		comm = globalPath + '/Backend/bdodatasets/target/BDODatasets-bdodatasets/BDODatasets/bin/api "%s" "%s"' %("6", param)
		try:
			process = subprocess.check_output([comm], shell="True")
		except subprocess.CalledProcessError as e:
			print(e)
			return render_template('500.html')
		# metadata parsed is converted into json class datasetInfo to be used inside the html form
		parsed_output = json.loads(process.decode('utf-8'))
		return jsonify(parsed_output)		

@app.route('/api/v1/dataset/searchVerticalCoverage', methods=['GET'])
@jwt_required()
def searchVerticalCoverage():
	if request.method == 'GET':
		# Calls shell apiListDatasetByVariable to obtain the list of datasets that contains the variables
		param = request.args['from']+','+request.args['to']
		comm = globalPath + '/Backend/bdodatasets/target/BDODatasets-bdodatasets/BDODatasets/bin/api "%s" "%s"' %("7", param)
		try:
			process = subprocess.check_output([comm], shell="True")
		except subprocess.CalledProcessError as e:
			print(e)
			return render_template('500.html')
		# metadata parsed is converted into json class datasetInfo to be used inside the html form
		parsed_output = json.loads(process.decode('utf-8'))
		return jsonify(parsed_output)

@app.route('/api/v1/dataset/searchTemporalCoverage', methods=['GET'])
@jwt_required()
def searchTemporalCoverage():
	if request.method == 'GET':
		# Calls shell apiListDatasetByVariable to obtain the list of datasets that contains the variables
		param = request.args['begin']+','+request.args['end']
		comm = globalPath + '/Backend/bdodatasets/target/BDODatasets-bdodatasets/BDODatasets/bin/api "%s" "%s"' %("8", param)
		try:
			process = subprocess.check_output([comm], shell="True")
		except subprocess.CalledProcessError as e:
			print(e)
			return render_template('500.html')
		# metadata parsed is converted into json class datasetInfo to be used inside the html form
		parsed_output = json.loads(process.decode('utf-8'))
		return jsonify(parsed_output)

@app.route('/api/v1/dataset/listVariables', methods=['GET'])
@jwt_required()
def listVariables():
	if request.method == 'GET':
		# Calls shell apiListDatasetByVariable to obtain the list of datasets that contains the variables
		comm = globalPath + '/Backend/bdodatasets/target/BDODatasets-bdodatasets/BDODatasets/bin/api "%s" "%s"' %("9", request.args['search'])
		try:
			process = subprocess.check_output([comm], shell="True")
		except subprocess.CalledProcessError as e:
			print(e)
			return render_template('500.html')
		# metadata parsed is converted into json class datasetInfo to be used inside the html form
		parsed_output = json.loads(process.decode('utf-8'))
		return jsonify(parsed_output)

@app.route('/api/v1/dataset/searchVariable', methods=['GET'])
@jwt_required()
def searchVariable():
	if request.method == 'GET':
		# Calls shell apiListDatasetByVariable to obtain the list of datasets that contains the variables
		comm = globalPath + '/Backend/bdodatasets/target/BDODatasets-bdodatasets/BDODatasets/bin/api "%s" "%s"' %("10", request.args['search'])
		try:
			process = subprocess.check_output([comm], shell="True")
		except subprocess.CalledProcessError as e:
			print(e)
			return render_template('500.html')
		# metadata parsed is converted into json class datasetInfo to be used inside the html form
		parsed_output = json.loads(process.decode('utf-8'))
		return jsonify(parsed_output)

# API POST for insert dataset's metadata automatically into Harmonization
# Parameters: filename,idfile,idprofile in json 
@app.route('/api/v1/dataset/insertAutomatic', methods=['POST'])
@jwt_required()
def insertAutomatic():
	if not request.json or not 'fileName' in request.json:
		abort(400)
	filename = request.json['fileName']
	idFile = request.json['idFile']
	idProfile = request.json['idProfile']
	produce = request.json['produce']
	param = filename + "," + idFile + "," + idProfile + "," + produce
	# Calls shell apiListDatasetByVariable to obtain the list of datasets that contains the variables
	comm = globalPath + '/Backend/bdodatasets/target/BDODatasets-bdodatasets/BDODatasets/bin/api "%s" "%s"' %("11", param)
	try:
		process = subprocess.check_output([comm], shell="True")
	except subprocess.CalledProcessError as e:
		return (500)
	# metadata parsed is converted into json class datasetInfo to be used inside the html form
	# return sucess 200 or error 500
	if b'Successful' and not b'Error' in process: 
		result = { 
			'fileName' : filename,  
			'idFile' : idFile,  
			'idProfile' : idProfile,  
			'produce' : produce,  
			'done' : True,  
			'message' :  'Successful!  Metadata has been added correctly' 
		} 
		return jsonify({'result':result}), 201 
	else: 
		result = { 
			'fileName' : filename,  
			'idFile' : idFile,  
			'idProfile' : idProfile,  
			'produce' : produce,  
			'done' : False,  
			'message' : 'Error!   URI already exists'  
		} 
		return jsonify({'result':result}), 500 

def extractDatafromParser():
	fileStorageTableJson = open(globalPath + "/Frontend/Flask/static/json/storageTable.json", "w+")
	JWT_output = requests.get(GlobalURLJWT + 'fileHandler/table', headers={'Authorization': Authorization})
	dataStorageTable = JWT_output.content.decode('utf-8')
	fileStorageTableJson.write(str(dataStorageTable))
	fileStorageTableJson.close()
	fileCanonicalModelJson = open(globalPath + "/Frontend/Flask/static/json/canonicalModelMongo.json", "w+")
	JWT_output1 = requests.get(GlobalURLJWT + 'fileHandler/variable', headers={'Authorization': Authorization})
	dataCanonicalModel = JWT_output1.content.decode('utf-8')
	fileCanonicalModelJson.write(str(dataCanonicalModel))
	fileCanonicalModelJson.close()

# Class for datasets parsed on shell suggest
class datasetInfo(object):
	def __init__(self, identifier, title, description, subject, keywords, standards, formats, language, homepage, publisher, 
		source, observations, storageTable,
		accessRights, issuedDate, modifiedDate, geoLocation, spatialWest, spatialEast, spatialSouth, spatialNorth, 
		coordinateSystem, verticalCoverageFrom, verticalCoverageTo, verticalLevel, temporalCoverageBegin, temporalCoverageEnd, 
		timeResolution, variable, profileName):
		self.identifier = identifier
		self.title = title
		self.description = description
		self.subject = subject
		self.keywords = keywords
		self.standards = standards
		self.formats =formats
		self.language = language
		self.homepage = homepage
		self.publisher = publisher
		self.source = source
		self.observations = observations
		self.storageTable = storageTable
		self.accessRights = accessRights
		self.issuedDate = issuedDate
		self.modifiedDate = modifiedDate
		self.geoLocation = geoLocation
		self.spatialWest = spatialWest
		self.spatialEast = spatialEast
		self.spatialSouth = spatialSouth
		self.spatialNorth = spatialNorth
		self.coordinateSystem = coordinateSystem
		self.verticalCoverageFrom = verticalCoverageFrom
		self.verticalCoverageTo = verticalCoverageTo
		self.verticalLevel = verticalLevel
		self.temporalCoverageBegin = temporalCoverageBegin
		self.temporalCoverageEnd = temporalCoverageEnd
		self.timeResolution = timeResolution
		self.variable = variable
		self.profileName = profileName

if __name__ == '__main__':
	syncWhenRunFlask()
	app.run(host='0.0.0.0')

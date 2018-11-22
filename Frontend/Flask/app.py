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
GLOBALPATH = "/BDOHarmonization/BigDataOcean-Harmonization"

GLOBALURLJWT = ""
UPLOAD_FOLDER = GLOBALPATH+'/Backend/AddDatasets'
ALLOWED_EXTENSIONS = set(['nc', 'csv', 'xlsx', 'xls'])

#JWT authorization (parser and handler)
# BDO.ini where JWT token is saved
config = configparser.ConfigParser()
config.read(GLOBALPATH + '/Backend/bdodatasets/bdo.ini')

AUTHORIZATION = config['DEFAULT']['AUTHORIZATION_JWT']

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
def syncwhenrunflask():
	tokencommand = GLOBALPATH + '/Backend/bdodatasets/target/BDODatasets-bdodatasets/BDODatasets/bin/fetchJWTToken'
	print (subprocess.check_output([tokencommand], shell="True"))
	config.read(GLOBALPATH + '/Backend/bdodatasets/bdo.ini')
	global AUTHORIZATION
	AUTHORIZATION = config['DEFAULT']['AUTHORIZATION_JWT']

# Thread in background to update every year the JWT Authorization Token (Parser tool)
cron = Scheduler(daemon=True)
# Explicitly kick off the background thread
cron.start()

# when server runs for the first time, it checks if one of the vocabularies does not exist and do the extraction process
if not os.path.exists(GLOBALPATH + '/Backend/AddDatasets/ontologiesN3/bdo.n3'):
	bdo = GLOBALPATH + '/Backend/bdodatasets/target/BDODatasets-bdodatasets/BDODatasets/bin/extractVocabularies bdo'
	print (subprocess.check_output([bdo], shell="True"))
	geolocbdo = GLOBALPATH + '/Backend/bdodatasets/target/BDODatasets-bdodatasets/BDODatasets/bin/extractVocabularies geolocbdo'
	print (subprocess.check_output([geolocbdo], shell="True"))
	inspire = GLOBALPATH + '/Backend/bdodatasets/target/BDODatasets-bdodatasets/BDODatasets/bin/extractVocabularies inspire'
	print (subprocess.check_output([inspire], shell="True"))
	eionet = GLOBALPATH + '/Backend/bdodatasets/target/BDODatasets-bdodatasets/BDODatasets/bin/extractVocabularies eionet'
	print (subprocess.check_output([eionet], shell="True"))

# Command = Thread in background to update every hour the JWT Authorization Token (Parser tool)
@cron.interval_schedule(seconds=3600)
def fetchtoken():
	syncwhenrunflask()

# bdo, geolocbdo, inspire, eionet = Thread in background to update every year the vocabularies (Vocabulary Repository)
@cron.interval_schedule(seconds=31536000)
def fetcheveryyear():
	bdo = GLOBALPATH + '/Backend/bdodatasets/target/BDODatasets-bdodatasets/BDODatasets/bin/extractVocabularies bdo'
	print (subprocess.check_output([bdo], shell="True"))
	geolocbdo = GLOBALPATH + '/Backend/bdodatasets/target/BDODatasets-bdodatasets/BDODatasets/bin/extractVocabularies geolocbdo'
	print (subprocess.check_output([geolocbdo], shell="True"))
	inspire = GLOBALPATH + '/Backend/bdodatasets/target/BDODatasets-bdodatasets/BDODatasets/bin/extractVocabularies inspire'
	print (subprocess.check_output([inspire], shell="True"))
	eionet = GLOBALPATH + '/Backend/bdodatasets/target/BDODatasets-bdodatasets/BDODatasets/bin/extractVocabularies eionet'
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
	extractdatafromhandler()
	# Calls shell listDatasets to get all the datasets stored on jena fuseki
	command = GLOBALPATH + '/Backend/bdodatasets/target/BDODatasets-bdodatasets/BDODatasets/bin/listDatasets'
	try:
		process = subprocess.check_output([command], shell="True")
		# other column settings -> http://bootstrap-table.wenzhixin.net.cn/documentation/#column-options
		columns = [{
		"field": "storageTable", # which is the field's name of data key 
		"title": "Storage Table", # display as the table header's name
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
		},
		{
		"field": "formats",
		"title": "Formats",
		"sortable": True,
		"visible": False,
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
		inputusername = request.form['username'] 
		inputpassword = request.form['password']
		for u in users:
			if u.username == inputusername:
				hashed_password = hashlib.sha512(inputpassword.encode('utf-8') + u.salt.encode('utf-8')).hexdigest()
				if u.password == hashed_password:
					flag = True
					break

		if flag == False:
			error = 'Invalid Credentials. Please try again.'
		else:
			session['user_id'] = inputusername
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
	try:
		parsed_output = requests.get(GLOBALURLJWT + 'fileHandler/file/metadata/empty', headers={'Authorization': AUTHORIZATION})
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
	except requests.exceptions.RequestException as e:
		print(e)
		return render_template('500.html', error='We could not connect with FileHandler')

# Routing to addMetadata form
@app.route('/addMetadata', methods=['GET', 'POST'])
@login_required
def parse():
	try:
		extractdatafromhandler()

		if request.method == 'POST':
			return render_template('addMetadata.html', dataset='', idFile='')
		elif request.method == 'GET':
			idfile = request.args['idFile']
			return render_template('addMetadata.html', dataset='', idFile=idfile)
	except ValueError as e:  # includes simplejson.decoder.JSONDecodeError
		print(e)
		return render_template('500.html')

@app.route('/addMetadata/Copernicus', methods=['GET', 'POST'])
@login_required
def addcopernicus():
	try:
		extractdatafromhandler()
		if request.method == 'POST':
			uri = request.form['uri']
			# if adding a Copernicus dataset, the shell suggest is called to parse the xml file and get metadata
			command = GLOBALPATH + '/Backend/bdodatasets/target/BDODatasets-bdodatasets/BDODatasets/bin/suggest "%s" Coppernicus' %uri
			try:
				process = subprocess.check_output([command], shell="True")
			except subprocess.CalledProcessError as e:
				print(e)
				return render_template('500.html')
			# metadata parsed is converted into json class DatasetInfo to be used inside the html form
			parsed_output = json.loads(process.decode('utf-8'))
			dataset = DatasetInfo(**parsed_output)

			return render_template('addMetadata.html', dataset=dataset, idFile='')
		elif request.method == 'GET':
			uri = request.args['uri']
			print (uri)
			# if adding a Copernicus dataset, the shell suggest is called to parse the xml file and get metadata
			command = GLOBALPATH + '/Backend/bdodatasets/target/BDODatasets-bdodatasets/BDODatasets/bin/suggest "%s" Coppernicus' %uri
			print (command)
			try:
				process = subprocess.check_output([command], shell="True")
			except subprocess.CalledProcessError as e:
				print(e)
				return render_template('500.html')
			# metadata parsed is converted into json class DatasetInfo to be used inside the html form
			parsed_output = json.loads(process.decode('utf-8'))
			dataset = DatasetInfo(**parsed_output)

			return render_template('addMetadata.html', dataset=dataset, idFile='')
	except ValueError as e:  # includes simplejson.decoder.JSONDecodeError
		print(e)
		return render_template('500.html')

@app.route('/addMetadata/NetCDF', methods=['GET', 'POST'])
@login_required
def addnetcdf():
	try:
		extractdatafromhandler()
		if request.method == 'POST':
			file = request.files['fileNetcdf']
			if file.filename != '':
				# Verify if the file is .nc
				if file and allowed_file(file.filename):
					# Create a general filename
					filename = file.filename
					# Saving the file in the UPLOAD_FOLDER with the filename
					file.save(os.path.join(app.config['UPLOAD_FOLDER'], filename))
					path_file_netcdf = UPLOAD_FOLDER + "/" + filename
					# print (path_file_netcdf)
					command = GLOBALPATH + '/Backend/bdodatasets/target/BDODatasets-bdodatasets/BDODatasets/bin/suggest "%s" FileNetcdf' %path_file_netcdf
					try:
						process = subprocess.check_output([command], shell="True")
					except subprocess.CalledProcessError as e:
						print(e)
						return render_template('500.html')
					# metadata parsed is converted into json class DatasetInfo to be used inside the html form
					parsed_output = json.loads(process.decode('utf-8'))
					dataset = DatasetInfo(**parsed_output)

					return render_template('addMetadata.html', dataset=dataset, idFile='')

		elif request.method == 'GET':
			file = request.args['file']
			idfile = request.args['idFile']
			command = GLOBALPATH + '/Backend/bdodatasets/target/BDODatasets-bdodatasets/BDODatasets/bin/suggest "%s" Netcdf' %file
			try:
				process = subprocess.check_output([command], shell="True")
			except subprocess.CalledProcessError as e:
				print(e)
				return render_template('500.html')
			# parsing output from maven script, to avoid log comments
			process = process.split(b'\n')
			# metadata parsed is converted into json class DatasetInfo to be used inside the html form
			parsed_output = json.loads(process[1].decode('utf-8'))
			dataset = DatasetInfo(**parsed_output)

			return render_template('addMetadata.html', dataset=dataset, idFile=idfile)
	except ValueError as e:  # includes simplejson.decoder.JSONDecodeError
		print(e)
		return render_template('500.html')

@app.route('/addMetadata/CSV', methods=['GET', 'POST'])
@login_required
def addcsv():
	try:
		extractdatafromhandler()
		if request.method == 'POST':
			file = request.files['fileCsv']
			if file.filename != '':
				# Verify if the file is .csv
				if file and allowed_file(file.filename):
					# Create a general filename
					filename = file.filename
					# Saving the file in the UPLOAD_FOLDER with the filename
					file.save(os.path.join(app.config['UPLOAD_FOLDER'], filename))
					path_file_csv = UPLOAD_FOLDER + "/" + filename
					# print (path_file_netcdf)
					command = GLOBALPATH + '/Backend/bdodatasets/target/BDODatasets-bdodatasets/BDODatasets/bin/suggest "%s" FileCSV' %path_file_csv
					try:
						process = subprocess.check_output([command], shell="True")
					except subprocess.CalledProcessError as e:
						print(e)
						return render_template('500.html')
					# metadata parsed is converted into json class DatasetInfo to be used inside the html form
					parsed_output = json.loads(process.decode('utf-8'))
					dataset = DatasetInfo(**parsed_output)

					return render_template('addMetadata.html', dataset=dataset, idFile='')
			
		elif request.method == 'GET':
			file = request.args['file']
			idfile = request.args['idFile']
			command = GLOBALPATH + '/Backend/bdodatasets/target/BDODatasets-bdodatasets/BDODatasets/bin/suggest "%s" CSV' %file
			try:
				process = subprocess.check_output([command], shell="True")
			except subprocess.CalledProcessError as e:
				print(e)
				return render_template('500.html')
			# parsing output from maven script, to avoid log comments
			process = process.split(b'\n')
			# metadata parsed is converted into json class DatasetInfo to be used inside the html form
			parsed_output = json.loads(process[1].decode('utf-8'))
			dataset = DatasetInfo(**parsed_output)

			return render_template('addMetadata.html', dataset=dataset, idFile=idfile)
	except ValueError as e:  # includes simplejson.decoder.JSONDecodeError
		print(e)
		return render_template('500.html')

@app.route('/addMetadata/EXCEL', methods=['GET', 'POST'])
@login_required
def addexcel():
	try:
		extractdatafromhandler()
		if request.method == 'POST':
			file = request.files['fileExcel']
			if file.filename != '':
				# Verify if the file is excel
				if file and allowed_file(file.filename):
					# Create a general filename
					filename = file.filename
					# Saving the file in the UPLOAD_FOLDER with the filename
					file.save(os.path.join(app.config['UPLOAD_FOLDER'], filename))
					path_file_excel = UPLOAD_FOLDER + "/" + filename
					# print (path_file_excel)
					command = GLOBALPATH + '/Backend/bdodatasets/target/BDODatasets-bdodatasets/BDODatasets/bin/suggest "%s" FileExcel' %path_file_excel
					try:
						process = subprocess.check_output([command], shell="True")
					except subprocess.CalledProcessError as e:
						print(e)
						return render_template('500.html')
					# metadata parsed is converted into json class DatasetInfo to be used inside the html form
					parsed_output = json.loads(process.decode('utf-8'))
					dataset = DatasetInfo(**parsed_output)

					return render_template('addMetadata.html', dataset=dataset, idFile='')
			
		elif request.method == 'GET':
			file = request.args['file']
			idfile = request.args['idFile']
			command = GLOBALPATH + '/Backend/bdodatasets/target/BDODatasets-bdodatasets/BDODatasets/bin/suggest "%s" Excel' %file
			try:
				process = subprocess.check_output([command], shell="True")
			except subprocess.CalledProcessError as e:
				print(e)
				return render_template('500.html')
			# parsing output from maven script, to avoid log comments
			process = process.split(b'\n')
			# metadata parsed is converted into json class DatasetInfo to be used inside the html form
			parsed_output = json.loads(process[1].decode('utf-8'))
			dataset = DatasetInfo(**parsed_output)

			return render_template('addMetadata.html', dataset=dataset, idFile=idfile)
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
			idfile = request.form['idFile']
			title = request.form['title']
			description = request.form['description']
			subject = request.form['tokenfield_subject']
			keywords= request.form['tokenfield_keywords']
			standards = request.form['standards']
			formats = request.form['tokenfield_format']
			language = request.form['tokenfield_language']
			homepage = request.form['homepage']
			publisher = request.form['publisher']
			license = request.form['license']
			source = request.form['source']
			observations = request.form['observations']
			storagetable = request.form['storageTable']
			accessrights = request.form['access_rights']
			issueddate = request.form['issued_date']
			modifieddate = request.form['modified_date']
			geolocation = request.form['tokenfield_geo_loc']
			spatialwest = request.form['geo_coverageW']
			spatialeast = request.form['geo_coverageE']
			spatialsouth = request.form['geo_coverageS']
			spatialnorth = request.form['geo_coverageN']
			coordinatesystem = request.form['coordinate_sys']
			verticalcoveragefrom = request.form['vert_coverage_from']
			verticalcoverageto = request.form['vert_coverage_to']
			verticallevel = request.form['vertical_level']
			temporalcoveragebegin = request.form['temp_coverage_begin']
			temporalcoverageend = request.form['temp_coverage_end']
			timeresolution = request.form['time_reso']
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

			profilename = request.form['nameProfile']

			if identifier  != "":
				check_existance = "<http://bigdataocean.eu/bdo/"+identifier+"> "+idfile
				datasettype = ""	
			else:
				identifier = str(uuid.uuid4())
				check_existance = idfile
				datasettype = "other"

			# add the values to the DatasetInfo class
			dataset = DatasetInfo (identifier, idfile, title, description, subject, keywords, standards, formats, language, homepage, publisher, 
				license, source, observations, storagetable,
				accessrights, issueddate, modifieddate, geolocation, spatialwest, spatialeast, spatialsouth, spatialnorth, 
				coordinatesystem, verticalcoveragefrom, verticalcoverageto, verticallevel, temporalcoveragebegin, 
				temporalcoverageend, timeresolution, variables, profilename)
			# create the json of the DatasetInfo class
			datasetjson = json.dumps(dataset.__dict__)
			with open(GLOBALPATH+'/Backend/AddDatasets/jsonDataset.json','w') as file:
				file.write(datasetjson)
				file.close()
			path2json = GLOBALPATH + "/Backend/AddDatasets/jsonDataset.json"

				
			# Calls shell insertDataset to connect to jena fuseki and add dataset via sparql query
			command = GLOBALPATH + '/Backend/bdodatasets/target/BDODatasets-bdodatasets/BDODatasets/bin/insertDataset "%s" "%s" "%s"' %(datasettype, check_existance, path2json)
			# print(command)
			try:
				process = subprocess.check_output([command], shell="True")
				
			except subprocess.CalledProcessError as e:
				print(e)
				return render_template('500.html')
			# when the dataset is added to jena fuseki, redirects to the metadataInfo web page corresponding to the identifier
			if b'Successful' in process and not b'Error' in process:
				return redirect(url_for('metadatainfo', identifier=identifier))
			elif b'Error1' in process:
				return render_template('404.html', error='Metadata has been added but API: Profile is not being added.')
			elif b'Error2' in process:
				return render_template('404.html', error='Metadata has been added but API: Identifier is not being added to idfile.')
			elif b'Error3' in process:
				return render_template('404.html', error='Metadata ID \'%s\' already exists.' %identifier)
			elif b'Error4' in process:
				return render_template('404.html', error='An error occurred inserting the metadata in Fuseki')
			else:
				return render_template('404.html', error='Sorry, a not identified error has appeared')
	except ValueError as e:  # includes simplejson.decoder.JSONDecodeError
		print(e)
		return render_template('500.html')

# Routing to modify a corresponding dataset
@app.route('/modify/<identifier>', methods=['GET', 'POST'])
@login_required
def modify(identifier):
	try:
		extractdatafromhandler()
		if request.method == 'GET':
			uri = "bdo:"+identifier
			comm = GLOBALPATH + '/Backend/bdodatasets/target/BDODatasets-bdodatasets/BDODatasets/bin/getFileDataset "%s"' %uri
			try:
				process = subprocess.check_output([comm], shell="True")
			except subprocess.CalledProcessError as e:
				print(e)
				return render_template('500.html')
			# metadata parsed is converted into json class DatasetInfo to be used inside the html form
			parsed_output = json.loads(process.decode('utf-8'))
			dataset = DatasetInfo(**parsed_output)
			return render_template('editMetadata.html', dataset=dataset)
			
	except ValueError as e:  # includes simplejson.decoder.JSONDecodeError
		print(e)
		return render_template('500.html')

# Routing to save new dataset
@app.route('/edit', methods=['GET','POST'])
@login_required
def edit():
	try:
		if request.method == 'POST':
			identifier = request.form['identifier']
			idfile = request.form['idFile']
			title = request.form['title']
			description = request.form['description']
			subject = request.form['tokenfield_subject']
			keywords= request.form['tokenfield_keywords']
			standards = request.form['standards']
			formats = request.form['tokenfield_format']
			language = request.form['tokenfield_language']
			homepage = request.form['homepage']
			publisher = request.form['publisher']
			license = request.form['license']
			source = request.form['source']
			observations = request.form['observations']
			storagetable = request.form['storageTable']
			accessrights = request.form['access_rights']
			issueddate = request.form['issued_date']
			modifieddate = request.form['modified_date']
			geolocation = request.form['tokenfield_geo_loc']
			spatialwest = request.form['geo_coverageW']
			spatialeast = request.form['geo_coverageE']
			spatialsouth = request.form['geo_coverageS']
			spatialnorth = request.form['geo_coverageN']
			coordinatesystem = request.form['coordinate_sys']
			verticalcoveragefrom = request.form['vert_coverage_from']
			verticalcoverageto = request.form['vert_coverage_to']
			verticallevel = request.form['vertical_level']
			temporalcoveragebegin = request.form['temp_coverage_begin']
			temporalcoverageend = request.form['temp_coverage_end']
			timeresolution = request.form['time_reso']

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
				check_existance = "<http://bigdataocean.eu/bdo/"+identifier+"> "+idfile
				datasettype = ""	
			else:
				identifier = str(uuid.uuid4())
				check_existance = idfile
				datasettype = "other"

			# add the values to the DatasetInfo class
			dataset = DatasetInfo (identifier, idfile, title, description, subject, keywords, standards, formats, language, homepage, publisher, 
				license, source, observations, storagetable,
				accessrights, issueddate, modifieddate, geolocation, spatialwest, spatialeast, spatialsouth, spatialnorth, 
				coordinatesystem, verticalcoveragefrom, verticalcoverageto, verticallevel, temporalcoveragebegin, temporalcoverageend, 
				timeresolution, variables, "")
			# create the json of the DatasetInfo class
			datasetjson = json.dumps(dataset.__dict__)
			with open(GLOBALPATH+'/Backend/AddDatasets/jsonDataset.json','w') as file:
				file.write(datasetjson)
				file.close()
			path2json = GLOBALPATH + "/Backend/AddDatasets/jsonDataset.json"

			# Calls shell insertDataset to connect to jena fuseki and add dataset via sparql query
			command = GLOBALPATH + '/Backend/bdodatasets/target/BDODatasets-bdodatasets/BDODatasets/bin/deleteDataset "%s" "%s"' %(identifier, True)
			try:
				process = subprocess.check_output([command], shell="True")
				
			except subprocess.CalledProcessError as e:
				print(e)
				return render_template('500.html')
			# when the dataset is added to jena fuseki, redirects to the metadataInfo web page corresponding to the identifier
			if b'Successful' in process:
				# Calls shell insertDataset to connect to jena fuseki and add dataset via sparql query
				command2 = GLOBALPATH + '/Backend/bdodatasets/target/BDODatasets-bdodatasets/BDODatasets/bin/insertDataset "%s" "%s" "%s"' %(datasettype, check_existance, path2json)
				try:
					process = subprocess.check_output([command2], shell="True")
					
				except subprocess.CalledProcessError as e:
					print(e)
					return render_template('500.html')
				# when the dataset is added to jena fuseki, redirects to the metadataInfo web page corresponding to the identifier
				if b'Successful' in process:
					return redirect(url_for('metadatainfo',identifier=identifier))
				else:
					return render_template('404.html', error='Metadata ID \'%s\' already exists.' %identifier)
			else:
				return render_template('404.html', error='There was an error while modifying the dataset.')

	except ValueError as e:  # includes simplejson.decoder.JSONDecodeError
		print(e)
		return render_template('500.html')

# Routing to delete a corresponding dataset
@app.route('/delete/<identifier>', methods=['GET', 'POST'])
@login_required
def delete(identifier):
	# Calls shell insertDataset to connect to jena fuseki and add dataset via sparql query
	command = GLOBALPATH + '/Backend/bdodatasets/target/BDODatasets-bdodatasets/BDODatasets/bin/deleteDataset "%s" "%s"' %(identifier, True)
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

# Routing to delete a corresponding dataset by storagetable
@app.route('/deleteDataset/<storage>', methods=['GET', 'POST'])
@login_required
def deleteDataset(storage):
	# Calls shell insertDataset to connect to jena fuseki and add dataset via sparql query
	command = GLOBALPATH + '/Backend/bdodatasets/target/BDODatasets-bdodatasets/BDODatasets/bin/deleteDataset "%s" "%s"' %(storage, False)
	try:
		process = subprocess.check_output([command], shell="True")
		
	except subprocess.CalledProcessError as e:
		print(e)
		return render_template('500.html')
	# when the dataset is added to jena fuseki, redirects to the metadataInfo web page corresponding to the identifier
	if b'Successful' in process:
		return redirect(url_for('index'))
	else:
		return render_template('404.html', error='There was an error while deleting the dataset with storage table "%s".' %(storage, False))

# Routing to see metadata of an specific dataset
@app.route('/metadataInfo/<identifier>', methods=['GET', 'POST'])
def metadatainfo(identifier):
	try:
		if request.method == 'GET':
			# Extracting subject/keywords/marineregions.json
			file = open(GLOBALPATH + '/Frontend/Flask/static/json/subject.json', 'r')
			subjectjson = json.load(file)
			file = open(GLOBALPATH + '/Frontend/Flask/static/json/keywords.json', 'r')
			keywordsjson = json.load(file)
			file = open(GLOBALPATH + '/Frontend/Flask/static/json/marineregions.json', 'r')
			geolocationjson = json.load(file)

			uri = "bdo:"+identifier
			# Calls shel getFileDataset to obtain all metadata of a dataset from jena fuseki
			comm = GLOBALPATH + '/Backend/bdodatasets/target/BDODatasets-bdodatasets/BDODatasets/bin/getFileDataset "%s"' %uri
			try:
				process = subprocess.check_output([comm], shell="True")
			except subprocess.CalledProcessError as e:
				print(e)
				return render_template('500.html')
			# metadata parsed is converted into json class DatasetInfo to be used inside the html form
			parsed_output = json.loads(process.decode('utf-8'))
			dataset = DatasetInfo(**parsed_output)
			if dataset.identifier != "":
				dataset.title = dataset.title.replace("_", " ")
				# show the name -- url in subject/keywords/geoLocation
				dataset.subject = nameurl(dataset.subject, subjectjson)
				dataset.keywords = nameurl(dataset.keywords, keywordsjson)
				dataset.geoLocation = nameurl(dataset.geoLocation, geolocationjson)
				return render_template('metadataInfo.html', dataset=dataset)
			else:
				return render_template('404.html', error='The metadata ID \'%s\' does not exist.' %identifier)
	except ValueError as e:  # includes simplejson.decoder.JSONDecodeError
		print(e)
		return render_template('500.html')

# show the name -- url in subject/keywords/geoLocation
def nameurl(listelement, listjson):
	token = listelement.split(", ")
	l = ""
	for t in token:
		for s in listjson:
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

# Routing to see list of file dataset that are part of a specific storage table
@app.route('/listFileDataset/<storage>')
def indexfile(storage):
	# Calls shell listDatasets to get all the datasets stored on jena fuseki
	command = GLOBALPATH + '/Backend/bdodatasets/target/BDODatasets-bdodatasets/BDODatasets/bin/listFileDatasets "%s"' %storage
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
		},
		{
		"field": "storageTable",
		"title": "Storage Table",
		"sortable": True,
		"visible": False,
		},
		{
		"field": "formats",
		"title": "Formats",
		"sortable": True,
		"visible": False,
		}]
		# print (process.decode('utf-8'))
		parsed_output = json.loads(process.decode('utf-8'))
		data = parsed_output
		return render_template('indexFileMetadata.html',
			data=data,
			columns=columns)
	except subprocess.CalledProcessError as e:
		print(e)
		return render_template('500.html')
	except ValueError as e:  # includes simplejson.decoder.JSONDecodeError
		print(e)
		return render_template('500.html')

# Routing to see metadata of an specific dataset based on storage table
@app.route('/metadataDatasetInfo/<storage>', methods=['GET', 'POST'])
def metadatadatasetinfo(storage):
	try:
		if request.method == 'GET':
			# Extracting subject/keywords/marineregions.json
			file = open(GLOBALPATH + '/Frontend/Flask/static/json/subject.json', 'r')
			subjectjson = json.load(file)
			file = open(GLOBALPATH + '/Frontend/Flask/static/json/keywords.json', 'r')
			keywordsjson = json.load(file)
			file = open(GLOBALPATH + '/Frontend/Flask/static/json/marineregions.json', 'r')
			geolocationjson = json.load(file)

			# Calls shell getDataset to obtain all metadata of a dataset from jena fuseki
			comm = GLOBALPATH + '/Backend/bdodatasets/target/BDODatasets-bdodatasets/BDODatasets/bin/getDataset "%s"' %storage
			try:
				process = subprocess.check_output([comm], shell="True")
			except subprocess.CalledProcessError as e:
				print(e)
				return render_template('500.html')
			# metadata parsed is converted into json class DatasetInfo to be used inside the html form
			parsed_output = json.loads(process.decode('utf-8'))
			dataset = DatasetInfo(**parsed_output)
			
			if dataset.storageTable != "":
				dataset.title = dataset.title.replace("_", " ")
				# show the name -- url in subject/keywords/geoLocation
				dataset.subject = nameurl(dataset.subject, subjectjson)
				dataset.keywords = nameurl(dataset.keywords, keywordsjson)
				dataset.geoLocation = nameurl(dataset.geoLocation, geolocationjson)
				return render_template('datasetMetadataInfo.html', dataset=dataset)
			else:
				return render_template('404.html', error='The storage table \'%s\' does not exist.' %storage)
	except ValueError as e:  # includes simplejson.decoder.JSONDecodeError
		print(e)
		return render_template('500.html')

#APIs
@app.route('/api', methods=['GET', 'POST'])
def api():
	return render_template('api.html')

@app.route('/api/v2/filedataset/list', methods=['GET'])
@jwt_required()
def listfiledataset():
	if request.method == 'GET':
		# Calls shell apiListDatasetByVariable to obtain the list of datasets that contains the variables
		comm = GLOBALPATH + '/Backend/bdodatasets/target/BDODatasets-bdodatasets/BDODatasets/bin/api "%s" "%s"' %("1", "")
		try:
			process = subprocess.check_output([comm], shell="True")
		except subprocess.CalledProcessError as e:
			print(e)
			return render_template('500.html')
		# metadata parsed is converted into json class DatasetInfo to be used inside the html form
		parsed_output = json.loads(process.decode('utf-8'))
		return jsonify(parsed_output)

@app.route('/api/v2/filedataset/info', methods=['GET'])
@jwt_required()
def infofiledataset():
	if request.method == 'GET':
		# Calls shell apiListDatasetByVariable to obtain the list of datasets that contains the variables
		param = 'bdo:'+request.args['id']
		print(param)
		comm = GLOBALPATH + '/Backend/bdodatasets/target/BDODatasets-bdodatasets/BDODatasets/bin/api "%s" "%s"' %("2", param)
		try:
			process = subprocess.check_output([comm], shell="True")
		except subprocess.CalledProcessError as e:
			print(e)
			return render_template('500.html')
		# metadata parsed is converted into json class DatasetInfo to be used inside the html form
		parsed_output = json.loads(process.decode('utf-8'))
		return jsonify(parsed_output)

@app.route('/api/v2/filedataset/searchSubject', methods=['GET'])
@jwt_required()
def searchsubject():
	if request.method == 'GET':
		# Calls shell apiListDatasetByVariable to obtain the list of datasets that contains the variables
		comm = GLOBALPATH + '/Backend/bdodatasets/target/BDODatasets-bdodatasets/BDODatasets/bin/api "%s" "%s"' %("3", request.args['q'])
		try:
			process = subprocess.check_output([comm], shell="True")
		except subprocess.CalledProcessError as e:
			print(e)
			return render_template('500.html')
		# metadata parsed is converted into json class DatasetInfo to be used inside the html form
		parsed_output = json.loads(process.decode('utf-8'))
		return jsonify(parsed_output)

@app.route('/api/v2/filedataset/searchKeyword', methods=['GET'])
@jwt_required()
def searchkeyword():
	if request.method == 'GET':
		# Calls shell apiListDatasetByVariable to obtain the list of datasets that contains the variables
		comm = GLOBALPATH + '/Backend/bdodatasets/target/BDODatasets-bdodatasets/BDODatasets/bin/api "%s" "%s"' %("4", request.args['q'])
		try:
			process = subprocess.check_output([comm], shell="True")
		except subprocess.CalledProcessError as e:
			print(e)
			return render_template('500.html')
		# metadata parsed is converted into json class DatasetInfo to be used inside the html form
		parsed_output = json.loads(process.decode('utf-8'))
		return jsonify(parsed_output)	

@app.route('/api/v2/filedataset/searchGeoLocation', methods=['GET'])
@jwt_required()
def searchgeolocation():
	if request.method == 'GET':
		# Calls shell apiListDatasetByVariable to obtain the list of datasets that contains the variables
		comm = GLOBALPATH + '/Backend/bdodatasets/target/BDODatasets-bdodatasets/BDODatasets/bin/api "%s" "%s"' %("5", request.args['q'])
		try:
			process = subprocess.check_output([comm], shell="True")
		except subprocess.CalledProcessError as e:
			print(e)
			return render_template('500.html')
		# metadata parsed is converted into json class DatasetInfo to be used inside the html form
		parsed_output = json.loads(process.decode('utf-8'))
		return jsonify(parsed_output)	

@app.route('/api/v2/filedataset/searchGeoCoverage', methods=['GET'])
@jwt_required()
def searchgeocoverage():
	if request.method == 'GET':
		# Calls shell apiListDatasetByVariable to obtain the list of datasets that contains the variables
		param = request.args['W']+','+request.args['E']+','+request.args['S']+','+request.args['N']
		comm = GLOBALPATH + '/Backend/bdodatasets/target/BDODatasets-bdodatasets/BDODatasets/bin/api "%s" "%s"' %("6", param)
		try:
			process = subprocess.check_output([comm], shell="True")
		except subprocess.CalledProcessError as e:
			print(e)
			return render_template('500.html')
		# metadata parsed is converted into json class DatasetInfo to be used inside the html form
		parsed_output = json.loads(process.decode('utf-8'))
		return jsonify(parsed_output)		

@app.route('/api/v2/filedataset/searchVerticalCoverage', methods=['GET'])
@jwt_required()
def searchverticalcoverage():
	if request.method == 'GET':
		# Calls shell apiListDatasetByVariable to obtain the list of datasets that contains the variables
		param = request.args['from']+','+request.args['to']
		comm = GLOBALPATH + '/Backend/bdodatasets/target/BDODatasets-bdodatasets/BDODatasets/bin/api "%s" "%s"' %("7", param)
		try:
			process = subprocess.check_output([comm], shell="True")
		except subprocess.CalledProcessError as e:
			print(e)
			return render_template('500.html')
		# metadata parsed is converted into json class DatasetInfo to be used inside the html form
		parsed_output = json.loads(process.decode('utf-8'))
		return jsonify(parsed_output)

@app.route('/api/v2/filedataset/searchTemporalCoverage', methods=['GET'])
@jwt_required()
def searchtemporalcoverage():
	if request.method == 'GET':
		# Calls shell apiListDatasetByVariable to obtain the list of datasets that contains the variables
		param = request.args['begin']+','+request.args['end']
		comm = GLOBALPATH + '/Backend/bdodatasets/target/BDODatasets-bdodatasets/BDODatasets/bin/api "%s" "%s"' %("8", param)
		try:
			process = subprocess.check_output([comm], shell="True")
		except subprocess.CalledProcessError as e:
			print(e)
			return render_template('500.html')
		# metadata parsed is converted into json class DatasetInfo to be used inside the html form
		parsed_output = json.loads(process.decode('utf-8'))
		return jsonify(parsed_output)

@app.route('/api/v2/variable/list', methods=['GET'])
@jwt_required()
def listvariable():
	if request.method == 'GET':
		# Calls shell apiListDatasetByVariable to obtain the list of datasets that contains the variables
		comm = GLOBALPATH + '/Backend/bdodatasets/target/BDODatasets-bdodatasets/BDODatasets/bin/api "%s" "%s"' %("9", request.args['id'])
		try:
			process = subprocess.check_output([comm], shell="True")
		except subprocess.CalledProcessError as e:
			print(e)
			return render_template('500.html')
		# metadata parsed is converted into json class DatasetInfo to be used inside the html form
		parsed_output = json.loads(process.decode('utf-8'))
		return jsonify(parsed_output)

@app.route('/api/v2/variable/search', methods=['GET'])
@jwt_required()
def searchvariable():
	if request.method == 'GET':
		# Calls shell apiListDatasetByVariable to obtain the list of datasets that contains the variables
		comm = GLOBALPATH + '/Backend/bdodatasets/target/BDODatasets-bdodatasets/BDODatasets/bin/api "%s" "%s"' %("10", request.args['q'])
		try:
			process = subprocess.check_output([comm], shell="True")
		except subprocess.CalledProcessError as e:
			print(e)
			return render_template('500.html')
		# metadata parsed is converted into json class DatasetInfo to be used inside the html form
		parsed_output = json.loads(process.decode('utf-8'))
		return jsonify(parsed_output)

@app.route('/api/v2/dataset/listVariables', methods=['GET'])
@jwt_required()
def listdatasetvariable():
	if request.method == 'GET':
		# Calls shell apiListDatasetByVariable to obtain the list of datasets that contains the variables
		comm = GLOBALPATH + '/Backend/bdodatasets/target/BDODatasets-bdodatasets/BDODatasets/bin/api "%s" "%s"' %("12", request.args['table'])
		try:
			process = subprocess.check_output([comm], shell="True")
		except subprocess.CalledProcessError as e:
			print(e)
			return render_template('500.html')
		# metadata parsed is converted into json class DatasetInfo to be used inside the html form
		parsed_output = json.loads(process.decode('utf-8'))
		return jsonify(parsed_output)

@app.route('/api/v2/dataset/listFileDatasets', methods=['GET'])
@jwt_required()
def listfiledatasetdataset():
	if request.method == 'GET':
		# Calls shell apiListDatasetByVariable to obtain the list of datasets that contains the variables
		comm = GLOBALPATH + '/Backend/bdodatasets/target/BDODatasets-bdodatasets/BDODatasets/bin/api "%s" "%s"' %("13", request.args['table'])
		try:
			process = subprocess.check_output([comm], shell="True")
		except subprocess.CalledProcessError as e:
			print(e)
			return render_template('500.html')
		# metadata parsed is converted into json class DatasetInfo to be used inside the html form
		parsed_output = json.loads(process.decode('utf-8'))
		return jsonify(parsed_output)

@app.route('/api/v2/filedataset/searchTitle', methods=['GET'])
@jwt_required()
def searchtitle():
	if request.method == 'GET':
		# Calls shell apiListDatasetByVariable to obtain the list of datasets that contains the variables
		comm = GLOBALPATH + '/Backend/bdodatasets/target/BDODatasets-bdodatasets/BDODatasets/bin/api "%s" "%s"' %("14", request.args['q'])
		try:
			process = subprocess.check_output([comm], shell="True")
		except subprocess.CalledProcessError as e:
			print(e)
			return render_template('500.html')
		# metadata parsed is converted into json class DatasetInfo to be used inside the html form
		parsed_output = json.loads(process.decode('utf-8'))
		return jsonify(parsed_output)

@app.route('/api/v2/filedataset/searchDescription', methods=['GET'])
@jwt_required()
def searchdescription():
	if request.method == 'GET':
		# Calls shell apiListDatasetByVariable to obtain the list of datasets that contains the variables
		comm = GLOBALPATH + '/Backend/bdodatasets/target/BDODatasets-bdodatasets/BDODatasets/bin/api "%s" "%s"' %("15", request.args['q'])
		try:
			process = subprocess.check_output([comm], shell="True")
		except subprocess.CalledProcessError as e:
			print(e)
			return render_template('500.html')
		# metadata parsed is converted into json class DatasetInfo to be used inside the html form
		parsed_output = json.loads(process.decode('utf-8'))
		return jsonify(parsed_output)

@app.route('/api/v2/dataset/info', methods=['GET'])
@jwt_required()
def infodataset():
	if request.method == 'GET':
		# Calls shell apiListDatasetByVariable to obtain the list of datasets that contains the variables
		comm = GLOBALPATH + '/Backend/bdodatasets/target/BDODatasets-bdodatasets/BDODatasets/bin/api "%s" "%s"' %("16", request.args['table'])
		try:
			process = subprocess.check_output([comm], shell="True")
		except subprocess.CalledProcessError as e:
			print(e)
			return render_template('500.html')
		# metadata parsed is converted into json class DatasetInfo to be used inside the html form
		parsed_output = json.loads(process.decode('utf-8'))
		return jsonify(parsed_output)

@app.route('/api/v2/dataset/list', methods=['GET'])
@jwt_required()
def listdataset():
	if request.method == 'GET':
		# Calls shell apiListDatasetByVariable to obtain the list of datasets that contains the variables
		comm = GLOBALPATH + '/Backend/bdodatasets/target/BDODatasets-bdodatasets/BDODatasets/bin/api "%s" "%s"' %("17", "")
		try:
			process = subprocess.check_output([comm], shell="True")
		except subprocess.CalledProcessError as e:
			print(e)
			return render_template('500.html')
		# metadata parsed is converted into json class DatasetInfo to be used inside the html form
		parsed_output = json.loads(process.decode('utf-8'))
		return jsonify(parsed_output)

# API POST for insert dataset's metadata automatically into Harmonization
# Parameters: filename,idfile,idprofile in json 
@app.route('/api/v2/filedataset/insertAutomatic', methods=['POST'])
@jwt_required()
def insertautomatic():
	if not request.json or not 'fileName' in request.json:
		abort(400)
	filename = request.json['fileName']
	idfile = request.json['idFile']
	idprofile = request.json['idProfile']
	produce = request.json['produce']
	param = filename + "," + idfile + "," + idprofile + "," + str(produce)
	# Calls shell apiListDatasetByVariable to obtain the list of datasets that contains the variables
	comm = GLOBALPATH + '/Backend/bdodatasets/target/BDODatasets-bdodatasets/BDODatasets/bin/api "%s" "%s"' %("11", param)
	try:
		process = subprocess.check_output([comm], shell="True")
	except subprocess.CalledProcessError as e:
		return (500)
	# metadata parsed is converted into json class DatasetInfo to be used inside the html form
	# return sucess 200 or error 500
	if b'Successful' and not b'Error' in process: 
		result = { 
			'fileName' : filename,  
			'idFile' : idfile,  
			'idProfile' : idprofile,  
			'produce' : produce,  
			'done' : True,  
			'message' :  'Successful!  Metadata has been added correctly' 
		} 
		return jsonify({'result':result}), 201 
	else: 
		result = { 
			'fileName' : filename,  
			'idFile' : idfile,  
			'idProfile' : idprofile,  
			'produce' : produce,  
			'done' : False,  
			'message' : 'Error!   Metadata ID already exists'  
		} 
		return jsonify({'result':result}), 500 

def extractdatafromhandler():
	try:
		jwt_output = requests.get(GLOBALURLJWT + 'fileHandler/table', headers={'Authorization': AUTHORIZATION})
		if jwt_output.status_code == requests.codes.ok:
			filestoragetablejson = open(GLOBALPATH + "/Frontend/Flask/static/json/storageTable.json", "w+")
			datastoragetable = jwt_output.content.decode('utf-8')
			filestoragetablejson.write(str(datastoragetable))
			filestoragetablejson.close()
		jwt_output1 = requests.get(GLOBALURLJWT + 'fileHandler/variable', headers={'Authorization': AUTHORIZATION})
		if jwt_output1.status_code == requests.codes.ok:
			filecanonicalmodeljson = open(GLOBALPATH + "/Frontend/Flask/static/json/canonicalModelMongo.json", "w+")
			datacanonicalmodel = jwt_output1.content.decode('utf-8')
			filecanonicalmodeljson.write(str(datacanonicalmodel))
			filecanonicalmodeljson.close()
	except requests.exceptions.RequestException as e:
		print(e)

# Class for datasets parsed on shell suggest
class DatasetInfo(object):
	def __init__(self, identifier, idFile, title, description, subject, keywords, standards, formats, language, homepage, publisher, license, 
		source, observations, storageTable, accessRights, issuedDate, modifiedDate, geoLocation, spatialWest, spatialEast, spatialSouth, spatialNorth, 
		coordinateSystem, verticalCoverageFrom, verticalCoverageTo, verticalLevel, temporalCoverageBegin, temporalCoverageEnd, 
		timeResolution, variable, profileName):
		self.identifier = identifier
		self.idFile = idFile
		self.title = title
		self.description = description
		self.subject = subject
		self.keywords = keywords
		self.standards = standards
		self.formats =formats
		self.language = language
		self.homepage = homepage
		self.publisher = publisher
		self.license = license
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
	syncwhenrunflask()
	app.run(host='0.0.0.0')

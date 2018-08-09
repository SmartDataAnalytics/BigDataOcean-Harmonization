import subprocess
import requests
import json
import os
import uuid
import urllib
from flask import Flask, render_template, request, redirect, url_for, flash, jsonify
from flask_bootstrap import Bootstrap
from pprint import pprint
import numpy as np
from datetime import datetime, timedelta
from flask_jwt import JWT, jwt_required, current_identity
from werkzeug.security import safe_str_cmp
from werkzeug.utils import secure_filename

# GLOBAL VARIABLES
globalPath = "/BDOHarmonization/BigDataOcean-Harmonization"

GlobalURLJWT = "http://212.101.173.21:8085/"
UPLOAD_FOLDER = globalPath+'/Backend/AddDatasets'
ALLOWED_EXTENSIONS = set(['nc', 'csv', 'xlsx', 'xls'])

#JWT authorization
Authorization = 'Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJiZG8iLCJleHAiOjE1NTI0ODk1ODUsInJvbGUiOiJST0xFX0FETUlOIn0.o5cZnYT3MKwfmVt06EyCMWy2qpgFPwcwZg82a3jmkNZKOVCJIbnh-LsHnEIF8BEUdj9OKrurwtknYh5ObjgLvg'

# Authentication JWT for APIs
# Class for security authentication JWT
class User(object):
	def __init__(self, id, username, password):
		self.id = id
		self.username = username
		self.password = password

	def __str__(self):
		return "User(id='%s')" % self.id

users = [
	User(1, 'admin', 'qNkYUXzGbIu4nwU3'),
	User(2, 'apiuser', 'gzGhwpqjE3Mj4eNE'),
]

username_table = {u.username: u for u in users}
userid_table = {u.id: u for u in users}

app = Flask(__name__)
bootstrap = Bootstrap(app)
# configuring the path of the upload folder
app.config['UPLOAD_FOLDER'] = UPLOAD_FOLDER
app.config['SECRET_KEY'] = 'super-secret'
app.config['JWT_EXPIRATION_DELTA'] = timedelta(days = 365) # Expiration time of JWT token is for 1 year

# Authentication JWT for APIs
def authenticate(username, password):
    user = username_table.get(username, None)
    if user and safe_str_cmp(user.password.encode('utf-8'), password.encode('utf-8')):
        return user

def identity(payload):
    user_id = payload['identity']
    return userid_table.get(user_id, None)

jwt = JWT(app, authenticate, identity)

# check if an extension is valid
def allowed_file(filename):
    return '.' in filename and \
           filename.rsplit('.', 1)[1].lower() in ALLOWED_EXTENSIONS

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
def parse():
	try:
		fileStorageTableJson = open(globalPath + "/Frontend/Flask/static/json/storageTable.json", "w+")
		JWT_output = requests.get(GlobalURLJWT + 'fileHandler/table', headers={'Authorization': Authorization})
		dataStorageTable = JWT_output.content.decode('utf-8')
		fileStorageTableJson.write(str(dataStorageTable))
		fileStorageTableJson.close()
		if request.method == 'POST':
			return render_template('addMetadata.html', dataset='', idFile='')
		elif request.method == 'GET':
			idFile = request.args['idFile']
			return render_template('addMetadata.html', dataset='', idFile=idFile)
	except ValueError as e:  # includes simplejson.decoder.JSONDecodeError
		print(e)
		return render_template('500.html')

@app.route('/addMetadata/Copernicus', methods=['GET', 'POST'])
def addCopernicus():
	try:
		fileStorageTableJson = open(globalPath + "/Frontend/Flask/static/json/storageTable.json", "w+")
		JWT_output = requests.get(GlobalURLJWT + 'fileHandler/table', headers={'Authorization': Authorization})
		dataStorageTable = JWT_output.content.decode('utf-8')
		fileStorageTableJson.write(str(dataStorageTable))
		fileStorageTableJson.close()
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
def addNetCDF():
	try:
		fileStorageTableJson = open(globalPath + "/Frontend/Flask/static/json/storageTable.json", "w+")
		JWT_output = requests.get(GlobalURLJWT + 'fileHandler/table', headers={'Authorization': Authorization})
		dataStorageTable = JWT_output.content.decode('utf-8')
		fileStorageTableJson.write(str(dataStorageTable))
		fileStorageTableJson.close()
		if request.method == 'POST':
			file = request.files['fileNetcdf']
			if file.filename != '':
				# Verify if the file is .nc
				if file and allowed_file(file.filename):
					# Create a general filename
					filename = "file.nc"
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
def addCsv():
	try:
		fileStorageTableJson = open(globalPath + "/Frontend/Flask/static/json/storageTable.json", "w+")
		JWT_output = requests.get(GlobalURLJWT + 'fileHandler/table', headers={'Authorization': Authorization})
		dataStorageTable = JWT_output.content.decode('utf-8')
		fileStorageTableJson.write(str(dataStorageTable))
		fileStorageTableJson.close()
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
def addExcel():
	try:
		fileStorageTableJson = open(globalPath + "/Frontend/Flask/static/json/storageTable.json", "w+")
		JWT_output = requests.get(GlobalURLJWT + 'fileHandler/table', headers={'Authorization': Authorization})
		dataStorageTable = JWT_output.content.decode('utf-8')
		fileStorageTableJson.write(str(dataStorageTable))
		fileStorageTableJson.close()
		if request.method == 'POST':
			file = request.files['fileExcel']
			if file.filename != '':
				# Verify if the file is .csv
				if file and allowed_file(file.filename):
					# Create a general filename
					filename = file.filename
					# Saving the file in the UPLOAD_FOLDER with the filename
					file.save(os.path.join(app.config['UPLOAD_FOLDER'], filename))
					path_fileCsv = UPLOAD_FOLDER + "/" + filename
					# print (path_fileNetcdf)
					command = globalPath + '/Backend/bdodatasets/target/BDODatasets-bdodatasets/BDODatasets/bin/suggest "%s" FileExcel' %path_fileCsv
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
			# delete the empty elements in the list 
			parservariable[:] = [item for item in parservariable if item != '']
			parserlist = np.array(parservariable).tolist()
			jsonvariable[:] = [item for item in jsonvariable if item != '']
			jsonlist = np.array(jsonvariable).tolist()
			# zip the two list in one called variables
			variables = dict(zip(parserlist, jsonlist))

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
def edit(identifier):
	try:
		fileStorageTableJson = open(globalPath + "/Frontend/Flask/static/json/storageTable.json", "w+")
		JWT_output = requests.get(GlobalURLJWT + 'fileHandler/table', headers={'Authorization': Authorization})
		dataStorageTable = JWT_output.content.decode('utf-8')
		fileStorageTableJson.write(str(dataStorageTable))
		fileStorageTableJson.close()
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
			# delete the empty elements in the list 
			parservariable[:] = [item for item in parservariable if item != '']
			parserlist = np.array(parservariable).tolist()
			jsonvariable[:] = [item for item in jsonvariable if item != '']
			jsonlist = np.array(jsonvariable).tolist()
			# zip the two list in one called variables
			variables = dict(zip(parserlist, jsonlist))
			
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
				coordinateSystem, verticalCoverageFrom, verticalCoverageTo,temporalCoverageBegin, temporalCoverageEnd, 
				verticalLevel, timeResolution, variables, "")
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
			# Extracting variablesCF_BDO.json
			file = open(globalPath + '/Frontend/Flask/static/json/variablesCF_BDO.json', 'r')
			variablesCF = json.load(file)

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
			return render_template('metadataInfo.html', dataset=dataset, variables=variablesCF)
	except ValueError:  # includes simplejson.decoder.JSONDecodeError
		print(e)
		return render_template('500.html')

@app.route('/endpoint', methods=['GET', 'POST'])
def endpoint():
	return render_template('sparqlEndpoint.html')

#APIs
@app.route('/api', methods=['GET', 'POST'])
def api():
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
		param = request.args['W']+', '+request.args['E']+', '+request.args['S']+', '+request.args['N']
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
		param = request.args['from']+',= '+request.args['to']
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
		param = request.args['begin']+',- '+request.args['end']
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
		# self.variables = variables # map<string, string>
		self.variables = variable
		self.profileName = profileName

if __name__ == '__main__':
	app.run(debug=True, host='0.0.0.0')

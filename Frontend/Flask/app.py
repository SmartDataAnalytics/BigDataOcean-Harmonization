import subprocess
import requests
import json
import os
import uuid
from flask import Flask, render_template, request, redirect, url_for, flash, jsonify
from flask_bootstrap import Bootstrap
from pprint import pprint
import numpy as np
from werkzeug.utils import secure_filename

# GLOBAL VARIABLES
globalPath = "/BDOHarmonization/BigDataOcean-Harmonization"

UPLOAD_FOLDER = globalPath+'/Backend/AddDatasets'
ALLOWED_EXTENSIONS = set(['nc'])

#JWT authorization
Authorization = 'Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJiZG8iLCJleHAiOjE1NTI0ODk1ODUsInJvbGUiOiJST0xFX0FETUlOIn0.o5cZnYT3MKwfmVt06EyCMWy2qpgFPwcwZg82a3jmkNZKOVCJIbnh-LsHnEIF8BEUdj9OKrurwtknYh5ObjgLvg'

app = Flask(__name__)
bootstrap = Bootstrap(app)
# configuring the path of the upload folder
app.config['UPLOAD_FOLDER'] = UPLOAD_FOLDER


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
		return render_template('500.html')
	except ValueError:  # includes simplejson.decoder.JSONDecodeError
		return render_template('500.html')

# List of files that does not have metadata
@app.route('/list')
def list():
	parsed_output = requests.get('http://212.101.173.21:8085/file/metadata/empty', headers={'Authorization': Authorization})
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
	"field": "dataType",
	"title": "DataType",
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
		if request.method == 'POST':
			return render_template('addMetadata.html', dataset='', idFile='')
		elif request.method == 'GET':
			idFile = request.args['idFile']
			return render_template('addMetadata.html', dataset='', idFile=idFile)
	except ValueError:  # includes simplejson.decoder.JSONDecodeError
		return render_template('500.html')

@app.route('/addMetadata/Copernicus', methods=['GET', 'POST'])
def addCopernicus():
	try:
		if request.method == 'POST':
			uri = request.form['uri']
			# if adding a Copernicus dataset, the shell suggest is called to parse the xml file and get metadata
			command = globalPath + '/Backend/bdodatasets/target/BDODatasets-bdodatasets/BDODatasets/bin/suggest "%s" Coppernicus' %uri
			try:
				process = subprocess.check_output([command], shell="True")
			except subprocess.CalledProcessError as e:
				return render_template('500.html')
			# metadata parsed is converted into json class datasetSuggest to be used inside the html form
			parsed_output = json.loads(process.decode('utf-8'))
			dataset = datasetSuggest(**parsed_output)

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
				return render_template('500.html')
			# metadata parsed is converted into json class datasetSuggest to be used inside the html form
			parsed_output = json.loads(process.decode('utf-8'))
			dataset = datasetSuggest(**parsed_output)

			return render_template('addMetadata.html', dataset=dataset, idFile='')
	except ValueError:  # includes simplejson.decoder.JSONDecodeError
		return render_template('500.html')

@app.route('/addMetadata/NetCDF', methods=['GET', 'POST'])
def addNetCDF():
	try:
		if request.method == 'POST':
			file = request.form['fileNetcdf']
			command = globalPath + '/Backend/bdodatasets/target/BDODatasets-bdodatasets/BDODatasets/bin/suggest "%s" Netcdf' %file
			try:
				process = subprocess.check_output([command], shell="True")
			except subprocess.CalledProcessError as e:
				return render_template('500.html')
			# parsing output from maven script, to avoid log comments
			process = process.split(b'\n')
			# metadata parsed is converted into json class datasetSuggest to be used inside the html form
			parsed_output = json.loads(process[1].decode('utf-8'))
			dataset = datasetSuggestNetcdf(**parsed_output)

			return render_template('addMetadata.html', dataset=dataset, idFile='')
		elif request.method == 'GET':
			file = request.args['file']
			idFile = request.args['idFile']
			command = globalPath + '/Backend/bdodatasets/target/BDODatasets-bdodatasets/BDODatasets/bin/suggest "%s" Netcdf' %file
			try:
				process = subprocess.check_output([command], shell="True")
			except subprocess.CalledProcessError as e:
				return render_template('500.html')
			# parsing output from maven script, to avoid log comments
			process = process.split(b'\n')
			# metadata parsed is converted into json class datasetSuggest to be used inside the html form
			parsed_output = json.loads(process[1].decode('utf-8'))
			dataset = datasetSuggestNetcdf(**parsed_output)

			return render_template('addMetadata.html', dataset=dataset, idFile=idFile)
	except ValueError:  # includes simplejson.decoder.JSONDecodeError
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
				check_existance = "<http://bigdataocean.eu/bdo/"+identifier+"> \n"
				datasetType = ""	
			else:
				identifier = str(uuid.uuid4())
				check_existance = title+">"+publisher+">"+issuedDate
				datasetType = "other"

			# add the values to the datasetInfo class
			dataset = datasetInfo (identifier, title, description, subject, keywords, standards, formats, language, homepage, publisher, 
				accessRights, issuedDate, modifiedDate, geoLocation, spatialWest, spatialEast, spatialSouth, spatialNorth, 
				coordinateSystem, verticalCoverageFrom, verticalCoverageTo,temporalCoverageBegin, temporalCoverageEnd, 
				verticalLevel, timeResolution, variables)
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
				return render_template('500.html')
			# when the dataset is added to jena fuseki, redirects to the metadataInfo web page corresponding to the identifier
			if b'Successful' in process:
				if idFile != '':
					result = requests.put('http://212.101.173.21:8085/file/' + idFile + 
						'/metadata/' + identifier, headers={'Authorization': Authorization})
					if result.status_code == 200:
						return redirect(url_for('metadataInfo', identifier=identifier))
					else:
						return render_template('500.html')
				else:
					return redirect(url_for('metadataInfo', identifier=identifier))
			else:
				return render_template('404.html', error='URI already exists.')
	except ValueError:  # includes simplejson.decoder.JSONDecodeError
		return render_template('500.html')

# Routing to modify a corresponding dataset
@app.route('/modify/<identifier>', methods=['GET', 'POST'])
def edit(identifier):
	try:
		if request.method == 'GET':
			uri = "<http://bigdataocean.eu/bdo/"+identifier+"> \n"
			comm = globalPath + '/Backend/bdodatasets/target/BDODatasets-bdodatasets/BDODatasets/bin/getDataset "%s"' %uri
			try:
				process = subprocess.check_output([comm], shell="True")
			except subprocess.CalledProcessError as e:
				return render_template('500.html')
			# metadata parsed is converted into json class datasetInfo to be used inside the html form
			parsed_output = json.loads(process.decode('utf-8'))
			dataset = datasetInfo(**parsed_output)
			# print(dataset.variables)
			return render_template('editMetadata.html', dataset=dataset)
			
	except ValueError:  # includes simplejson.decoder.JSONDecodeError
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
				check_existance = "<http://bigdataocean.eu/bdo/"+identifier+"> \n"
				datasetType = ""	
			else:
				identifier = str(uuid.uuid4())
				check_existance = title+">"+publisher+">"+issuedDate
				datasetType = "other"

			# add the values to the datasetInfo class
			dataset = datasetInfo (identifier, title, description, subject, keywords, standards, formats, language, homepage, publisher, 
				accessRights, issuedDate, modifiedDate, geoLocation, spatialWest, spatialEast, spatialSouth, spatialNorth, 
				coordinateSystem, verticalCoverageFrom, verticalCoverageTo,temporalCoverageBegin, temporalCoverageEnd, 
				verticalLevel, timeResolution, variables)
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
				return render_template('500.html')
			# when the dataset is added to jena fuseki, redirects to the metadataInfo web page corresponding to the identifier
			if b'Successful' in process:
				# Calls shell insertDataset to connect to jena fuseki and add dataset via sparql query
				command2 = globalPath + '/Backend/bdodatasets/target/BDODatasets-bdodatasets/BDODatasets/bin/insertDataset "%s" "%s" "%s"' %(datasetType, check_existance, path2json)
				# print(command)
				try:
					process = subprocess.check_output([command2], shell="True")
					
				except subprocess.CalledProcessError as e:
					return render_template('500.html')
				# when the dataset is added to jena fuseki, redirects to the metadataInfo web page corresponding to the identifier
				if b'Successful' in process:
					return redirect(url_for('metadataInfo',identifier=identifier))
				else:
					return render_template('404.html', error='URI already exists.')
			else:
				return render_template('404.html', error='There was an error while modifying the dataset.')

	except ValueError:  # includes simplejson.decoder.JSONDecodeError
		return render_template('500.html')

# Routing to delete a corresponding dataset
@app.route('/delete/<identifier>', methods=['GET', 'POST'])
def delete(identifier):
	# Calls shell insertDataset to connect to jena fuseki and add dataset via sparql query
	command = globalPath + '/Backend/bdodatasets/target/BDODatasets-bdodatasets/BDODatasets/bin/deleteDataset "%s"' %identifier
	try:
		process = subprocess.check_output([command], shell="True")
		
	except subprocess.CalledProcessError as e:
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
				return render_template('500.html')
			# metadata parsed is converted into json class datasetInfo to be used inside the html form
			parsed_output = json.loads(process.decode('utf-8'))
			dataset = datasetInfo(**parsed_output)
			return render_template('metadataInfo.html', dataset=dataset, variables=variablesCF)
	except ValueError:  # includes simplejson.decoder.JSONDecodeError
		return render_template('500.html')

@app.route('/endpoint', methods=['GET', 'POST'])
def endpoint():
	return render_template('sparqlEndpoint.html')

#APIs
@app.route('/api', methods=['GET', 'POST'])
def api():
	return render_template('api.html')

@app.route('/api/v1/dataset/list', methods=['GET'])
def listDataset():
	if request.method == 'GET':
		# Calls shell apiListDatasetByVariable to obtain the list of datasets that contains the variables
		comm = globalPath + '/Backend/bdodatasets/target/BDODatasets-bdodatasets/BDODatasets/bin/api "%s" "%s"' %("1", "")
		try:
			process = subprocess.check_output([comm], shell="True")
		except subprocess.CalledProcessError as e:
			return render_template('500.html')
		# metadata parsed is converted into json class datasetInfo to be used inside the html form
		parsed_output = json.loads(process.decode('utf-8'))
		return jsonify(parsed_output)

@app.route('/api/v1/dataset/searchDataset', methods=['GET'])
def searchDataset():
	if request.method == 'GET':
		# Calls shell apiListDatasetByVariable to obtain the list of datasets that contains the variables
		param = 'bdo:'+request.args['search']
		print(param)
		comm = globalPath + '/Backend/bdodatasets/target/BDODatasets-bdodatasets/BDODatasets/bin/api "%s" "%s"' %("2", param)
		try:
			process = subprocess.check_output([comm], shell="True")
		except subprocess.CalledProcessError as e:
			return render_template('500.html')
		# metadata parsed is converted into json class datasetInfo to be used inside the html form
		parsed_output = json.loads(process.decode('utf-8'))
		return jsonify(parsed_output)

@app.route('/api/v1/dataset/searchSubject', methods=['GET'])
def searchSubject():
	if request.method == 'GET':
		# Calls shell apiListDatasetByVariable to obtain the list of datasets that contains the variables
		comm = globalPath + '/Backend/bdodatasets/target/BDODatasets-bdodatasets/BDODatasets/bin/api "%s" "%s"' %("3", request.args['search'])
		try:
			process = subprocess.check_output([comm], shell="True")
		except subprocess.CalledProcessError as e:
			return render_template('500.html')
		# metadata parsed is converted into json class datasetInfo to be used inside the html form
		parsed_output = json.loads(process.decode('utf-8'))
		return jsonify(parsed_output)

@app.route('/api/v1/dataset/searchKeyword', methods=['GET'])
def searchKeyword():
	if request.method == 'GET':
		# Calls shell apiListDatasetByVariable to obtain the list of datasets that contains the variables
		comm = globalPath + '/Backend/bdodatasets/target/BDODatasets-bdodatasets/BDODatasets/bin/api "%s" "%s"' %("4", request.args['search'])
		try:
			process = subprocess.check_output([comm], shell="True")
		except subprocess.CalledProcessError as e:
			return render_template('500.html')
		# metadata parsed is converted into json class datasetInfo to be used inside the html form
		parsed_output = json.loads(process.decode('utf-8'))
		return jsonify(parsed_output)	

@app.route('/api/v1/dataset/searchGeoLocation', methods=['GET'])
def searchGeoLocation():
	if request.method == 'GET':
		# Calls shell apiListDatasetByVariable to obtain the list of datasets that contains the variables
		comm = globalPath + '/Backend/bdodatasets/target/BDODatasets-bdodatasets/BDODatasets/bin/api "%s" "%s"' %("5", request.args['search'])
		try:
			process = subprocess.check_output([comm], shell="True")
		except subprocess.CalledProcessError as e:
			return render_template('500.html')
		# metadata parsed is converted into json class datasetInfo to be used inside the html form
		parsed_output = json.loads(process.decode('utf-8'))
		return jsonify(parsed_output)	

@app.route('/api/v1/dataset/searchGeoCoverage', methods=['GET'])
def searchGeoCoverage():
	if request.method == 'GET':
		# Calls shell apiListDatasetByVariable to obtain the list of datasets that contains the variables
		param = request.args['W']+', '+request.args['E']+', '+request.args['S']+', '+request.args['N']
		comm = globalPath + '/Backend/bdodatasets/target/BDODatasets-bdodatasets/BDODatasets/bin/api "%s" "%s"' %("6", param)
		try:
			process = subprocess.check_output([comm], shell="True")
		except subprocess.CalledProcessError as e:
			return render_template('500.html')
		# metadata parsed is converted into json class datasetInfo to be used inside the html form
		parsed_output = json.loads(process.decode('utf-8'))
		return jsonify(parsed_output)		

@app.route('/api/v1/dataset/searchVerticalCoverage', methods=['GET'])
def searchVerticalCoverage():
	if request.method == 'GET':
		# Calls shell apiListDatasetByVariable to obtain the list of datasets that contains the variables
		param = request.args['from']+',= '+request.args['to']
		comm = globalPath + '/Backend/bdodatasets/target/BDODatasets-bdodatasets/BDODatasets/bin/api "%s" "%s"' %("7", param)
		try:
			process = subprocess.check_output([comm], shell="True")
		except subprocess.CalledProcessError as e:
			return render_template('500.html')
		# metadata parsed is converted into json class datasetInfo to be used inside the html form
		parsed_output = json.loads(process.decode('utf-8'))
		return jsonify(parsed_output)

@app.route('/api/v1/dataset/searchTemporalCoverage', methods=['GET'])
def searchTemporalCoverage():
	if request.method == 'GET':
		# Calls shell apiListDatasetByVariable to obtain the list of datasets that contains the variables
		param = request.args['begin']+',- '+request.args['end']
		comm = globalPath + '/Backend/bdodatasets/target/BDODatasets-bdodatasets/BDODatasets/bin/api "%s" "%s"' %("8", param)
		try:
			process = subprocess.check_output([comm], shell="True")
		except subprocess.CalledProcessError as e:
			return render_template('500.html')
		# metadata parsed is converted into json class datasetInfo to be used inside the html form
		parsed_output = json.loads(process.decode('utf-8'))
		return jsonify(parsed_output)

@app.route('/api/v1/dataset/listVariables', methods=['GET'])
def listVariables():
	if request.method == 'GET':
		# Calls shell apiListDatasetByVariable to obtain the list of datasets that contains the variables
		comm = globalPath + '/Backend/bdodatasets/target/BDODatasets-bdodatasets/BDODatasets/bin/api "%s" "%s"' %("9", request.args['search'])
		try:
			process = subprocess.check_output([comm], shell="True")
		except subprocess.CalledProcessError as e:
			return render_template('500.html')
		# metadata parsed is converted into json class datasetInfo to be used inside the html form
		parsed_output = json.loads(process.decode('utf-8'))
		return jsonify(parsed_output)

@app.route('/api/v1/dataset/searchVariable', methods=['GET'])
def searchVariable():
	if request.method == 'GET':
		# Calls shell apiListDatasetByVariable to obtain the list of datasets that contains the variables
		comm = globalPath + '/Backend/bdodatasets/target/BDODatasets-bdodatasets/BDODatasets/bin/api "%s" "%s"' %("10", request.args['search'])
		try:
			process = subprocess.check_output([comm], shell="True")
		except subprocess.CalledProcessError as e:
			return render_template('500.html')
		# metadata parsed is converted into json class datasetInfo to be used inside the html form
		parsed_output = json.loads(process.decode('utf-8'))
		return jsonify(parsed_output)

# Class for datasets parsed on shell suggest
class datasetSuggest(object):
	def __init__(self, identifier, title, description, language, homepage, publisher, 
		spatialWest, spatialEast, spatialSouth, spatialNorth, issuedDate, modifiedDate,
		coordinateSystem, verticalCoverageFrom, verticalCoverageTo, verticalLevel, temporalCoverageBegin, temporalCoverageEnd, 
		timeResolution, variable):
		self.identifier = identifier
		self.title = title
		self.description = description
		self.language = language
		self.homepage = homepage
		self.issuedDate = issuedDate
		self.modifiedDate = modifiedDate
		self.publisher = publisher
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
		self.variables = variable

# Class for datasets parsed on shell suggestNetcdf
class datasetSuggestNetcdf(object):
	def __init__(self, identifier, title, description, keywords, standards, formats, homepage, publisher, 
		spatialWest, spatialEast, spatialSouth, spatialNorth, issuedDate, modifiedDate,
		verticalCoverageFrom, verticalCoverageTo, temporalCoverageBegin, temporalCoverageEnd, 
		timeResolution, variable
		):
		self.identifier = identifier
		self.title = title
		self.description = description
		self.keywords = keywords
		self.standards = standards
		self.formats = formats
		self.homepage = homepage
		self.issuedDate = issuedDate
		self.modifiedDate = modifiedDate
		self.publisher = publisher
		self.spatialWest = spatialWest
		self.spatialEast = spatialEast
		self.spatialSouth = spatialSouth
		self.spatialNorth = spatialNorth
		self.verticalCoverageFrom = verticalCoverageFrom
		self.verticalCoverageTo = verticalCoverageTo
		self.temporalCoverageBegin = temporalCoverageBegin
		self.temporalCoverageEnd = temporalCoverageEnd
		self.timeResolution = timeResolution
		self.variables = variable

# Class for all dataset metadata
class datasetInfo(object):
	def __init__(self, identifier, title, description, subject, keywords, standards, formats, language, homepage, publisher, 
		accessRights, issuedDate, modifiedDate, geoLocation, spatialWest, spatialEast, spatialSouth, spatialNorth, 
		coordinateSystem, verticalCoverageFrom, verticalCoverageTo,temporalCoverageBegin, temporalCoverageEnd, 
		verticalLevel, timeResolution, variable
		):
		self.identifier = identifier
		self.title = title
		self.description = description
		self.subject = subject
		self.keywords= keywords
		self.standards = standards
		self.formats = formats
		self.language = language
		self.homepage = homepage
		self.publisher = publisher
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

if __name__ == '__main__':
	app.run(debug=True, host='0.0.0.0')

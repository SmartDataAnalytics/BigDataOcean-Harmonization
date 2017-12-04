import subprocess
import json
import os
import uuid
from flask import Flask, render_template, request, redirect, url_for
from flask_bootstrap import Bootstrap
from pprint import pprint
from werkzeug.utils import secure_filename

# GLOBAL VARIABLES
globalPath = "/home/jaimetrillos/Dropbox/BDO/BigDataOcean-Harmonization"
#globalPath = "/home/anatrillos/Dropbox/Documentos/BigDataOcean-Harmonization"

UPLOAD_FOLDER = globalPath+'/Backend/AddDatasets'
ALLOWED_EXTENSIONS = set(['nc'])

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
	except subprocess.CalledProcessError as e:
		return render_template('500.html')

	parsed_output = json.loads(process.decode('utf-8'))
	data = parsed_output
	return render_template('index.html',
		data=data,
		columns=columns)

# Routing to addMetadata form
@app.route('/addMetadata', methods=['GET', 'POST'])
def parse():
	if request.method == 'POST':
		uri = request.form['uri']
		file = request.files['fileNetcdf']
		if uri != "":
			# if adding a Copernicus dataset, the shell suggest is called to parse the xml file and get metadata
			command = globalPath + '/Backend/bdodatasets/target/BDODatasets-bdodatasets/BDODatasets/bin/suggest "%s"' %uri
			try:
				process = subprocess.check_output([command], shell="True")
			except subprocess.CalledProcessError as e:
				return render_template('500.html')
			# metadata parsed is converted into json class datasetSuggest to be used inside the html form
			parsed_output = json.loads(process.decode('utf-8'))
			dataset = datasetSuggest(**parsed_output)

			return render_template('addMetadata.html', dataset=dataset)
		elif file.filename != '':
			# Verify if the file is .nc
			if file and allowed_file(file.filename):
				# Create a general filename
				filename = "file.nc"
				# Saving the file in the UPLOAD_FOLDER with the filename
				file.save(os.path.join(app.config['UPLOAD_FOLDER'], filename))
				path_fileNetcdf = UPLOAD_FOLDER + "/" + filename
				# print (path_fileNetcdf)
				command = globalPath + '/Backend/bdodatasets/target/BDODatasets-bdodatasets/BDODatasets/bin/suggestNetcdf "%s"' %path_fileNetcdf
				try:
					process = subprocess.check_output([command], shell="True")
				except subprocess.CalledProcessError as e:
					return render_template('500.html')
				# metadata parsed is converted into json class datasetSuggest to be used inside the html form
				parsed_output = json.loads(process.decode('utf-8'))
				dataset = datasetSuggestNetcdf(**parsed_output)
				print (dataset)

				return render_template('addMetadata.html', dataset=dataset)
		else:
			return render_template('addMetadata.html', dataset="")

# Routing to save new dataset
@app.route('/save', methods=['GET','POST'])
def save():
	if request.method == 'POST':
		# Extracting variablesCF_BDO.json
		# file = open(globalPath + '/Frontend/static/json/variablesCF_BDO.json', 'r')
		# variablesCF = json.load(file)

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
		parservariables = list(filter(None, parservariable))
		jsonvariables = list(filter(None, jsonvariable))
		# zip the two list in one called variables
		variables = list (zip (parservariables, jsonvariables))
		
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
		print (datasetJson)
			
		# Calls shell addDataset2bdo to connect to jena fuseki and add dataset via sparql query
		command = globalPath + '/Backend/bdodatasets/target/BDODatasets-bdodatasets/BDODatasets/bin/insertDataset "%s" "%s"' %(datasetType, check_existance, datasetJson)
		try:
			process = subprocess.check_output([command], shell="True")
		except subprocess.CalledProcessError as e:
			return render_template('500.html')
		# when the dataset is added to jena fuseki, redirects to the metadataInfo web page corresponding to the identifier
		if b'Successful' in process:
			return redirect(url_for('metadataInfo',identifier=identifier))
		else:
			return render_template('500.html')

# Routing to modify a corresponding dataset
@app.route('/modify/<identifier>', methods=['GET', 'POST'])
def edit(identifier):
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
		return render_template('editMetadata.html', dataset=dataset)

# Routing to see metadata of an specific dataset
@app.route('/metadataInfo/<identifier>', methods=['GET', 'POST'])
def metadataInfo(identifier):
	if request.method == 'GET':
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
		return render_template('metadataInfo.html', dataset=dataset)

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
		verticalLevel, timeResolution, variables
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
		self.variables = variables

if __name__ == '__main__':
	app.run(debug=True)

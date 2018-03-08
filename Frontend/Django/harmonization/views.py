import subprocess
import json
from django.shortcuts import render
from pprint import pprint

# GLOBAL VARIABLES
globalPath = "/home/jaimetrillos/Dropbox/BDO/BigDataOcean-Harmonization"
#globalPath = "/home/anatrillos/Dropbox/Documentos/BigDataOcean-Harmonization"
#globalPath = "/BDOHarmonization/BigDataOcean-Harmonization"

def index(request):
	# Calls shell listDatasets to get all the datasets stored on jena fuseki
	command = globalPath + '/Backend/bdodatasets/target/BDODatasets-bdodatasets/BDODatasets/bin/listDatasets'
	try:
		process = subprocess.check_output([command], shell="True")
		# other column settings -> http://bootstrap-table.wenzhixin.net.cn/documentation/#column-options
		columns = [{
		"field": "title", # which is the field's name of data key 
		"title": "Title", # display as the table header's name
		"sortable": "True",
		},
		{
		"field": "description",
		"title": "Description",
		"sortable": "True",
		}]
		# print (process.decode('utf-8'))
		parsed_output = json.loads(process.decode('utf-8'))
		data = parsed_output

		context = {'data' : data, 'columns' : columns}
		return render(request, 'harmonization/index.html', context)
	except subprocess.CalledProcessError as e:
		return render(request, 'harmonization/500.html')
	except ValueError:  # includes simplejson.decoder.JSONDecodeError
		return render(request, 'harmonization/500.html')

def parse(request):
	try:
		if request.method == 'POST':
			uri = request.form['uri']
			file = request.files.get('fileNetcdf')
			if uri != "":
				# if adding a Copernicus dataset, the shell suggest is called to parse the xml file and get metadata
				command = globalPath + '/Backend/bdodatasets/target/BDODatasets-bdodatasets/BDODatasets/bin/suggest "%s" Coppernicus' %uri
				try:
					process = subprocess.check_output([command], shell="True")
				except subprocess.CalledProcessError as e:
					return render(request, 'harmonization/500.html')
				# metadata parsed is converted into json class datasetSuggest to be used inside the html form
				parsed_output = json.loads(process.decode('utf-8'))
				dataset = datasetSuggest(**parsed_output)

				context = {'dataset' : dataset}
				return render(request, 'harmonization/addMetadata.html', context)
			
			elif file != None :
				# Verify if the file is .nc
				if file and allowed_file(file.filename):
					# Create a general filename
					filename = "file.nc"
					# Saving the file in the UPLOAD_FOLDER with the filename
					file.save(os.path.join(app.config['UPLOAD_FOLDER'], filename))
					path_fileNetcdf = UPLOAD_FOLDER + "/" + filename
					# print (path_fileNetcdf)
					command = globalPath + '/Backend/bdodatasets/target/BDODatasets-bdodatasets/BDODatasets/bin/suggest "%s" Netcdf' %path_fileNetcdf
					try:
						process = subprocess.check_output([command], shell="True")
					except subprocess.CalledProcessError as e:
						return render(request, 'harmonization/500.html')
					# metadata parsed is converted into json class datasetSuggest to be used inside the html form
					parsed_output = json.loads(process.decode('utf-8'))
					dataset = datasetSuggestNetcdf(**parsed_output)
					# print (dataset)

					context = {'dataset' : dataset}
					return render(request, 'harmonization/addMetadata.html', context)
			else:
				context = {'dataset' : ""}
				return render(request, 'harmonization/addMetadata.html', context)
	except ValueError:  # includes simplejson.decoder.JSONDecodeError
		return render(request, 'harmonization/500.html')

def api(request):
	return render(request, 'harmonization/api.html')

def endpoint(request):
	return render(request, 'harmonization/sparqlEndpoint.html')




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
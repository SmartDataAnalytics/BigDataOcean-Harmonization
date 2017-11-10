import subprocess
import json
from flask import Flask, render_template, request
from flask_bootstrap import Bootstrap
from pprint import pprint
import os

app = Flask(__name__)
bootstrap = Bootstrap(app)

# globalPath = "/home/jaimetrillos/Dropbox/BDO/BigDataOcean-Harmonization"
globalPath = "/home/anatrillos/Dropbox/Documentos/BigDataOcean-Harmonization"

data = [{
  "title": "Hi",
  "description": "Hi"
}]
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

@app.route('/')
def index():
	return render_template('index.html',
      data=data,
      columns=columns)

@app.route('/addMetadata', methods=['GET', 'POST'])
def parse():
	if request.method == 'POST':
		uri = request.form['uri']
		if uri != "":
			command = globalPath + '/Backend/bdodatasets/target/BDODatasets-bdodatasets/BDODatasets/bin/suggest "%s"' %uri
			try:
				process = subprocess.check_output([command], shell="True")
			except subprocess.CalledProcessError as e:
				return render_template('500.html')

			parsed_output = json.loads(process.decode('utf-8'))
			dataset = datasetSuggest(**parsed_output)
			return render_template('addMetadata.html', dataset=dataset)
		else :
			return render_template('addMetadata.html', dataset="")

@app.route('/save', methods=['GET','POST'])
def save():
	if request.method == 'POST':
		uri = "<http://bigdataocean.eu/bdo/"+request.form['identifier']+">\n"
		with open(globalPath+'/Backend/AddDatasets/addNewDataset.ttl','w') as file:
		 	file.write("PREFIX dct: <http://purl.org/dc/terms/>\n")
		 	file.write("PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n")
		 	file.write("PREFIX owl: <http://www.w3.org/2002/07/owl#>\n")
		 	file.write("PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n")
		 	file.write("PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n")
		 	file.write("PREFIX foaf: <http://xmlns.com/foaf/0.1/>\n")
		 	file.write("PREFIX disco: <http://rdf-vocabulary.ddialliance.org/discovery>\n")
		 	file.write("PREFIX dcat: <https://www.w3.org/TR/vocab-dcat/>\n")
		 	file.write("PREFIX bdo: <http://bigdataocean.eu/bdo/>\n")
		 	file.write("PREFIX ids: <http://industrialdataspace/information-model/>\n")
		 	file.write("PREFIX qudt: <http://qudt.org/schema/qudt/>\n")
		 	file.write("PREFIX unit: <http://qudt.org/vocab/unit/>\n")
		 	file.write("\n")
		 	file.write("INSERT DATA {\n")
		 	file.write("bdo:"+request.form["identifier"]+" a dcat:Dataset ;\n")
		 	file.write("dct:identifier \""+request.form['identifier']+"\" ;\n")
		 	file.write("dct:title \""+request.form['title']+"\" ;\n")
		 	file.write("dct:description \""+request.form['description']+"\" ;\n")
		 	file.write("dcat:subject <"+request.form['tokenfield_subject']+"> ;\n")
		 	file.write("dcat:theme <"+request.form['tokenfield_keywords']+"> ;\n")
		 	file.write("dct:conformsTo \""+request.form['standards']+"\" ;\n")
		 	file.write("dct:format \""+request.form['tokenfield_format']+"\" ;\n")
		 	file.write("dct:language \""+request.form['tokenfield_language']+"\" ;\n")
		 	file.write("foaf:homepage \""+request.form['homepage']+"\" ;\n")
		 	file.write("dct:publisher \""+request.form['publisher']+"\" ;\n")
		 	file.write("dct:accessRights \""+request.form['access_rights']+"\" ;\n")
		 	file.write("dct:issued \""+request.form['issued_date']+"\"^^xsd:dateTime ;\n")
		 	file.write("dct:modified \""+request.form['modified_date']+"\"^^xsd:dateTime ;\n")
		 	file.write("dct:spatial \""+request.form['tokenfield_geo_loc']+"\" ;\n")
		 	file.write("dct:conformsTo \""+request.form['coordinate_sys']+"\" ;\n")
		 	file.write("bdo:verticalCoverage bdo:"+request.form['identifier']+"_VC ;\n")
		 	file.write("bdo:verticalLevel \""+request.form['vertical_level']+"\";\n")
		 	file.write("bdo:timeCoverage [ids:beginning \""+request.form['temp_coverage_begin']+"\"^^xsd:dateTime ;\n")
		 	file.write("ids:end \""+request.form['temp_coverage_end']+"\"^^xsd:dateTime] ;\n")
		 	file.write("bdo:temporalResolution \""+request.form['time_reso']+"\" .\n")
		 	#.file.write("		disco:variable .")
		 	file.write("}")
		 	file.close()
		path2TTL = globalPath + "/Backend/AddDatasets/addNewDataset.ttl"
		command = globalPath + '/Backend/bdodatasets/target/BDODatasets-bdodatasets/BDODatasets/bin/addDataset2bdo "%s" "%s"' %(uri, path2TTL)
		try:
			process = subprocess.check_output([command], shell="True")
		except subprocess.CalledProcessError as e:
			return render_template('500.html')
		if b'Successful' in process:
			return render_template('index.html',data=data,columns=columns)
		else:
			return render_template('500.html')

@app.route('/edit', methods=['GET', 'POST'])
def edit(dataset):
	if request.method == 'POST':
		return render_template('metadata.html')

@app.route('/metadataInfo', methods=['GET', 'POST'])
def metadataInfo(dataset):
	if request.method == 'GET':
		return render_template('metadataInfo.html', dataset=dataset)

class datasetSuggest(object):
	def __init__(self, identifier, title, description, language, homepage, publisher, spatialWest, spatialEast, 
		spatialSouth, spatialNorth, coordinateSystem, verticalCoverage, verticalLevel, temporalCoverageBegin, 
		temporalCoverageEnd, timeResolution, variables):
		self.identifier = identifier
		self.title = title
		self.description = description
		#self.subject = subject
		#self.keywords= keywords
		#self.standards = standards
		#self.format = format
		self.language = language
		self.homepage = homepage
		self.publisher = publisher
		#self.accessRights = accessRights
		#self.issuedDate = issuedDate
		#self.modifiedDate = modifiedDate
		#self.geoLocation = geoLocation
		self.spatialWest = spatialWest
		self.spatialEast = spatialEast
		self.spatialSouth = spatialSouth
		self.spatialNorth = spatialNorth
		self.coordinateSystem = coordinateSystem
		self.verticalCoverage = verticalCoverage
		self.verticalLevel = verticalLevel
		self.temporalCoverageBegin = temporalCoverageBegin
		self.temporalCoverageEnd = temporalCoverageEnd
		self.timeResolution = timeResolution
		self.variables = variables

if __name__ == '__main__':
	app.run(debug=True)
import subprocess
import json
from flask import Flask, render_template, request
from flask_bootstrap import Bootstrap
from pprint import pprint

app = Flask(__name__)
bootstrap = Bootstrap(app)



@app.route('/')
def index():
	return render_template('index.html')

@app.route('/metadata', methods=['GET', 'POST'])
def parse():
	if request.method == 'POST':
		uri = request.form['uri']
		# command = '/home/jaimetrillos/Documents/BDO/BigDataOcean-Harmonization/Backend/bdodatasets/target/BDODatsets-bdodatasets/BDODatsets/bin/suggest "%s"' %uri
		command = '/home/anatrillos/Documents/BigDataOcean-Harmonization/Backend/bdodatasets/target/BDODatsets-bdodatasets/BDODatsets/bin/suggest "%s"' %uri
		try:
			process = subprocess.check_output([command], shell="True")
		except subprocess.CalledProcessError as e:
			return render_template('500.html')

		parsed_output = json.loads(process.decode('utf-8'))
		dataset = datasetSuggest(**parsed_output)
		return render_template('metadata.html', dataset=dataset)

@app.route('/save', methods=['POST'])
def save():
	if request.method == 'POST':
		print ("hola")
		file = open("/home/anatrillos/Documents/BigDataOcean-Harmonization/TripleStore/addNewDataset.ttl", "w+") 		 
		file.write("@prefix dct: <http://purl.org/dc/terms/> . \n")
		file.write("@prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .\n")
		file.write("@prefix owl: <http://www.w3.org/2002/07/owl#> . \n")
		file.write("@prefix xsd: <http://www.w3.org/2001/XMLSchema#> . \n")
		file.write("@prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> . \n")
		file.write("@prefix foaf: <http://xmlns.com/foaf/0.1/> . \n")
		file.write("@prefix disco: <http://rdf-vocabulary.ddialliance.org/discovery> . \n")
		file.write("@prefix dcat: <https://www.w3.org/TR/vocab-dcat/> . \n")
		file.write("@prefix bdo: <http://bigdataocean.eu/bdo/> . \n")
		file.write("@prefix ids: <http://industrialdataspace/information-model/> . \n")
		file.write("@prefix qudt: <http://qudt.org/schema/qudt/> . \n")
		file.write("@prefix unit: <http://qudt.org/vocab/unit/> . \n") 
		file.write("bdo:"+request.form['identifier']+" a "+request.form['tokenfield_type']) 		 
		file.close()


@app.route('/edit', methods=['GET', 'POST'])
def edit(dataset):
	if request.method == 'POST':
		return render_template('metadata.html')

@app.route('/metadataInfo', methods=['GET', 'POST'])
def metadataInfo(dataset):
	if request.method == 'GET':
		return render_template('metadataInfo.html', dataset=dataset)

class datasetSuggest(object):
	def __init__(self, title, description, homepage, identifier, language, spatialWest, spatialEast, spatialSouth, spatialNorth, 
		temporal, conformsTo, publisher, accuralPeriodicity, verticalCoverage, verticalLevel, temporalResolution, gridResolution, variables):
		self.title = title
		self.description = description
		#self.type = type
		self.homepage = homepage
		self.identifier = identifier
		self.language = language
		#self.subject = subject
		#self.theme = theme
		self.spatialWest = spatialWest
		self.spatialEast = spatialEast
		self.spatialSouth = spatialSouth
		self.spatialNorth = spatialNorth
		self.temporal = temporal
		#self.issuedDate = issuedDate
		#self.modifiedDate = modifiedDate
		#self.provenance = provenance
		self.conformsTo = conformsTo
		#self.license = license
		#self.accessRights = accessRights
		self.publisher = publisher
		#self.format = format
		#self.characterEncoding = characterEncoding
		self.accuralPeriodicity = accuralPeriodicity
		#self.comment = comment
		#self.representationTecnique = representationTecnique
		self.verticalCoverage = verticalCoverage
		self.verticalLevel = verticalLevel
		self.temporalResolution = temporalResolution
		self.gridResolution = gridResolution
		self.variables = variables


if __name__ == '__main__':
	app.run(debug=True)
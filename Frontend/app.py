import subprocess
import json
from flask import Flask, render_template, request
from flask_bootstrap import Bootstrap
from pprint import pprint
import os

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
		with open('/home/anatrillos/Documents/BigDataOcean-Harmonization/TripleStore/addNewDataset.ttl','w') as file:
			file.write("@prefix adms: <http://www.w3.org/ns/adms#> . \n")
			file.write("@prefix cnt: <http://www.w3.org/2011/content#> . \n")
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
			file.write("@prefix unit: <http://qudt.org/vocab/unit/> . \n \n") 
			file.write("bdo:"+request.form['identifier']+" a <"+request.form['tokenfield_type']+"> ;\n")
			file.write("dct:title \""+request.form['title']+"\" ;\n")
			file.write("dct:description \""+request.form['description']+"\" ;\n")
			file.write("foaf:homepage \""+request.form['homepage']+"\" ;\n")
			file.write("dct:identifier \""+request.form['identifier']+"\" ;\n")
			file.write("dct:language \""+request.form['tokenfield_language']+"\" ;\n")
			file.write("dct:subject <"+request.form['tokenfield_subject']+"> ;\n")
			file.write("dcat:theme <"+request.form['tokenfield_keywords']+"> ;\n")
			file.write("dct:spatial bdo:"+request.form['identifier']+"_SC ;\n")
			file.write("dct:temporal \""+request.form['temp_coverage']+"\" ;\n")
			file.write("dct:provenance \""+request.form['provenance']+"\" ;\n")
			file.write("dct:conformsTo \""+request.form['coordinate_sys']+"\" ;\n")
			file.write("dct:license \""+request.form['license']+"\" ;\n")
			file.write("dct:accessRights \""+request.form['access_rights']+"\" ;\n")
			file.write("dct:publisher \""+request.form['publisher']+"\" ;\n")
			file.write("dct:format <"+request.form['tokenfield_format']+"> ;\n")
			file.write("cnt:characterEncoding \""+request.form['char_encoding']+"\" ;\n")
			file.write("dct:accuralPeriodicity \""+request.form['update_freq']+"\" ;\n")
			file.write("rdfs:comment \""+request.form['comment']+"\" ;\n")
			file.write("adms:representationTechnique \""+request.form['spatial_representation']+"\" ;\n")
			file.write("bdo:verticalCoverage bdo:"+request.form['identifier']+"_VC ;\n")
			file.write("bdo:temporalResolution bdo:"+request.form['identifier']+"_TR ;\n")
			file.write("bdo:gridResolution bdo:"+request.form['identifier']+"_GR .\n")
		update_fuseki = 's-put http://localhost:3030/bdoHarmonization/data default /home/anatrillos/Documents/BigDataOcean-Harmonization/AddDatasets/addNewDataset.ttl'
		try:
			process = subprocess.check_output([update_fuseki], shell="True")
		except subprocess.CalledProcessError as e:
			return 'passed'
			


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
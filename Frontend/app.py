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
		command = '/home/anatrillos/Documents/BigDataOcean-Harmonization/Backend/bdodatasets/target/BDODatsets-bdodatasets/BDODatsets/bin/suggest "%s"' %uri
		try:
			process = subprocess.check_output([command], shell="True")
		except subprocess.CalledProcessError as e:
			return render_template('500.html')

		parsed_output = json.loads(process.decode('utf-8'))
		dataset = datasetSuggest(**parsed_output)
		return render_template('metadataInfo.html', dataset=dataset)

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
		temporal, conformsTo, publisher, accuralPeriodicity, verticalCoverage, verticalLevel, temporalResolution, gridResolution):
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


if __name__ == '__main__':
	app.run(debug=True)
import unittest
from app import app
import requests
import json
import sys

# Testing the integration between BDO harmonization and JWT.

Authorization = 'Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJiZG8iLCJleHAiOjE1NTI0ODk1ODUsInJvbGUiOiJST0xFX0FETUlOIn0.o5cZnYT3MKwfmVt06EyCMWy2qpgFPwcwZg82a3jmkNZKOVCJIbnh-LsHnEIF8BEUdj9OKrurwtknYh5ObjgLvg'
identifier = "testIntegration"
idFile = "5ae051b39ac2555efd1a5926"

class TestFlask(unittest.TestCase):
	# Show the response of getting the list of datasets without metadataID.
	def test1(self):
		self.test_app = app.test_client()
		response = self.test_app.get('/list')
		self.assertEqual(response.status, "200 OK")

	# Show the response of updating the metadataID to a dataset fileID. 
	# It can fail if the test has beeing doing it outside the JWT servers.
	def test2(self):
		self.test_app = app.test_client()
		response = requests.put('http://212.101.173.21:8085/file/' + idFile + 
						'/metadata/' + identifier, headers={'Authorization': Authorization})
		self.assertEqual(response.status_code, "200 OK")

if __name__ == "__main__":
    unittest.main()
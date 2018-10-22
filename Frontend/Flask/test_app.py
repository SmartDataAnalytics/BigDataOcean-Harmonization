import unittest
from app import app
import requests
import json
import sys

# Testing the integration between BDO harmonization and JWT.

Authorization = ''
identifier = ""
idFile = ""

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
		response = requests.put('http://localhost:8085/fileHandler/file/' + idFile + 
						'/metadata/' + identifier, headers={'Authorization': Authorization})
		self.assertEqual(response.status_code, "200 OK")

if __name__ == "__main__":
    unittest.main()

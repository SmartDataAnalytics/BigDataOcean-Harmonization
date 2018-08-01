# BigDataOcean-Harmonization
Tool for harmonization of datasets in BigDataOcean

- Start fuseki, and BDOHarmonization in separate docker containers using docker-compose:
```sh 
$ docker-compose up
```

- Run the API for adding metadata automatically (Kafka)
```sh
$ ./initHarmonization
```

Then visit http://localhost:5000/
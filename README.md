# BigDataOcean Harmonization Tool
Tool for harmonization of datasets in BigDataOcean

## Start BDOHarmonization Docker
- Start BDOHarmonization using docker-compose:
```sh 
$ docker-compose up
```

## Start Fuseki Server Docker
- Pull Docker Fuseki Server 
```sh
$ docker pull stain/jena-fuseki
```

- Copy the folder Harmonization_Fuseki_TripleStore in the machine/server

- Run Docker Fuseki Server (Remember to change the path where Harmonization_Fuseki_TripleStore is stored)
```sh
$ docker run -d --name harmonization_fuseki -p 3031:3031 -e ADMIN_PASSWORD=bd0 -v /path/to/Harmonization_Fuseki_TripleStore/:/fuseki/ -it stain/jena-fuseki ./fuseki-server --port=3031
```

## Run the bash file
- Run the API for adding metadata automatically (Kafka)
```sh
$ ./initHarmonization
```

Then visit http://localhost:5000/

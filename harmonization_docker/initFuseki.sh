# Run the fuseki server with the configuration in port 3031
docker exec -it harmonization_bdoharmonization_1 bash -c 'cd /apache-jena-fuseki-3.4.0/;
    ./fuseki-server -config=/fuseki/configuration/bdoHarmonization.ttl --port=3031'
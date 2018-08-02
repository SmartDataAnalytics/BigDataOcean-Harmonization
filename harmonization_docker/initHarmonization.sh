# Run the API for Automatic Insertion (Kafka)
# It runs the insertAutomatic.sh every 15 minutes (900 seconds) 
docker exec -it harmonizationdocker_bdoharmonization_1 bash -c 'cd /BDOHarmonization/BigDataOcean-Harmonization/Backend/bdodatasets/target/BDODatasets-bdodatasets/BDODatasets/bin/;
    while true; 
    	do ./insertAutomatic;
	    sleep 900; 
	done'

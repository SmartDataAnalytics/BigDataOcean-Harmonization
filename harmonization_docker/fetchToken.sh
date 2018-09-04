# Run the API for fetching new tokenJWT (Parser and handler)
# It runs the insertAutomatic.sh every 1 year (31536000 seconds) 
docker exec -it harmonization_bdoharmonization_1 bash -c 'cd /BDOHarmonization/BigDataOcean-Harmonization/Backend/bdodatasets/target/BDODatasets-bdodatasets/BDODatasets/bin/;
    while true; 
    	do ./fetchJWTToken;
	    sleep 31536000; 
	done'
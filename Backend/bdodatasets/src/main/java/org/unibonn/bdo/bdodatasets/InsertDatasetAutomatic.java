package org.unibonn.bdo.bdodatasets;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import org.apache.hadoop.fs.Path;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.ini4j.Ini;
import org.json.simple.parser.ParseException;
import org.unibonn.bdo.bdodatasets.Constants;
import org.unibonn.bdo.connections.ConsumerCreator;
import org.unibonn.bdo.connections.HDFSFileSystem;
import org.unibonn.bdo.connections.ProducerCreator;
import org.unibonn.bdo.objects.Dataset;
import org.unibonn.bdo.objects.ProfileDataset;

import com.google.gson.Gson;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import ucar.nc2.Attribute;
import ucar.nc2.NetcdfFile;
import ucar.nc2.dataset.NetcdfDataset;

/**
 *  
 * @author Jaime M Trillos
 *
 * Receives 3 parameters: HDFS URL, idFile, idProfile 
 * This class connects with Kafka in order to add new metadata of a dataset (TOPIC: files_without_metadata)
 * returning the idFile when the metadata is correctly inserted in Fuseki (TOPIC: files_with_metadata)
 * 
 * runProducer();  insert new message TOPIC: files_with_metadata (TOPIC2)
 * runConsumer();  read messages TOPIC: files_without_metadata (TOPIC1)
 *
 */

public class InsertDatasetAutomatic {
	
	private static final String EMPTY_FIELD = "";
	private static String tokenAuthorization = ""; 
	
    public static void main(String[] args) {
    	try {
			Ini config = new Ini(new File(Constants.INITFILEPATH));
			tokenAuthorization = config.get("DEFAULT", "AUTHORIZATION_JWT");
			runConsumer();
		} catch (IOException e) {
			e.printStackTrace();
		} 
    }
    
    private static void runConsumer() {
    	Consumer<Long, String> consumer = ConsumerCreator.createConsumer();
        int noMessageFound = 0;
        while (true) {
        	Duration timeout = Duration.ofMillis(1000);
        	// 1000 is the time in milliseconds consumer will wait if no record is found at broker.
        	ConsumerRecords<Long, String> consumerRecords = consumer.poll(timeout);
        	if (consumerRecords.count() == 0) {
        		noMessageFound++;
        		if (noMessageFound > Constants.MAX_NO_MESSAGE_FOUND_COUNT)
        			// If no message found count is reached to threshold exit loop.  
        			break;
        	}
        	//for each record insert it in Harmonization tool and print in the TOPIC2 the idFile that has been successful added.
        	consumerRecords.forEach(record -> {
        		String recordValue = record.value();
        		String[] tokens = recordValue.split(",");
				String filename = tokens[0];
				String idFile = tokens[1];
				String idProfile = tokens[2];
				boolean flag = false;
				// if metadata has been inserted in Fuseki then send a message with idFile to the TOPIC2
				try {
					flag = analyseInsertDatasetAutomatic(filename,idFile,idProfile);
					if(flag) {
						System.out.println("Successful!  Metadata has been added correctly");
						runProducer(idFile);
					}else {
						System.out.println("Error!  There was an error adding the metadata");
					}
				} catch (IOException | ParseException | UnirestException e) {
					e.printStackTrace();
				}
			});
			// commits the offset of record to broker. 
			consumer.commitAsync();
		}
        consumer.close();
    }
    
    public static void runProducer(String idFile) {
    	Producer<Long, String> producer = ProducerCreator.createProducer();
    	// Send a message to TOPIC2 with the idFile
    	ProducerRecord<Long, String> record = new ProducerRecord<>(Constants.TOPIC_NAME2, idFile);
    	try {
        	RecordMetadata metadata = producer.send(record).get();
            System.out.println("Record sent with key " + idFile + " to partition " + metadata.partition()
            	+ " with offset " + metadata.offset());
        } catch (ExecutionException | InterruptedException e) {
        	System.out.println("Error in sending record");
            System.out.println(e);
        } 
    }
    
    // API: Create the metadata to an specific dataset with help of the profile.
    public static boolean analyseInsertDatasetAutomatic(String filename, String idFile, String idProfile) 
    		throws IOException, ParseException, UnirestException {
		Dataset result = new Dataset();
		boolean resultInsert = false;
		boolean resultFlag = true;
		String parameter;
		String[] splitName;
		String issuedDate = "";
		String modifiedDate = "";
		HttpResponse<JsonNode> response; //Get the profileJson
		HttpResponse<String> response1; //Put the identifier to an idFile
		String jsonProfile = "";
		
		//Get the jsonProfile by the idProfile (API)
		response = Unirest.get(Constants.HTTPJWT + "fileHandler/metadataProfile/id/" + idProfile)
			.header("Content-Type", "application/json")
			.header("Authorization", tokenAuthorization)
			.asJson();
		if(response.getStatus() == 200) {
			jsonProfile = response.getBody().getObject().toString();
		}else {
			return false;
		}
		
		//Convert json into Dataset
		result = convertProfileToDataset(jsonProfile);
		
		//Set the identifier to the dataset
		String identifier = UUID.randomUUID().toString();
		result.setIdentifier(identifier);
		
		//Extract the issuedDate and modifiedDate
		String nameExtension = new File(filename).getName();
		String[] tokens = nameExtension.split("\\.(?=[^\\.]+$)");
		String name = tokens[0];
		String extension = tokens[1];
		if(extension.equals("csv") || extension.equals("xls") || extension.equals("xlsx")) {
			//Extract the issuedDate and modifiedDate that contains the fileName iff the fileName has "_"
			if (name.contains("_")) {
				splitName = name.split("_");
				int size = splitName.length;
				issuedDate = splitName[size-2];
				modifiedDate = splitName[size-1];
				result.setIssuedDate(BdoDatasetAnalyser.convertDate(issuedDate));
				result.setModifiedDate(BdoDatasetAnalyser.convertDate(modifiedDate));
			}else {
				System.out.println(" Error!  the file name does not have issuedDate and modifiedDate");
				return false;
			}
		}else if(tokens[1].equals("nc")) {
			result = extractionDatesNetcdf(result, filename);
		}
		
		// Parameters to check if metadata already exist in Fuseki
		parameter = result.getTitle()+">"+result.getPublisher()+">"+result.getIssuedDate();
		
		// insert metadata into the system
		resultInsert = InsertNewDataset.insertDataset("other", parameter, result);
		
		// if metadata is successful added in Fuseki then send the identifier(API)
		if (resultInsert) {
			response1 = Unirest.put(Constants.HTTPJWT + "fileHandler/file/" + idFile + 
					"/metadata/" + result.getIdentifier())
					.header("Content-Type", "application/json")
					.header("Authorization", tokenAuthorization)
					.asString();
			if(response1.getStatus() == 200) {
				resultFlag = true;
			}else {
				System.out.println(" Error!  fileHandler/file/{idFile}/metadata/{identifier} returns status code = " + response.getStatus());
				resultFlag = false;
			}
		}else {
			System.out.println(" Error!  insertDataset method has return false");
			return false;
		}

		return resultFlag;
	}
    
    // Extract dates (issued, modified) and identifier from Datasets Netcdf
    public static Dataset extractionDatesNetcdf(Dataset result, String filename) throws IOException {
		//read the file
		NetcdfFile nc = null;
		try {
			HDFSFileSystem hdfsSys = new HDFSFileSystem(filename);
			Path localFile = hdfsSys.copyFile(filename,Constants.CONFIGFILEPATH+"/Backend/AddDatasets/file.nc");
			//read NetCDF file to get its metadata
			nc = NetcdfDataset.openFile(localFile.toString(), null);
			
			//find the attributes and export the issuedDate and modifiedDate
			List<Attribute> listFileMetadata = nc.getGlobalAttributes();
			if(listFileMetadata != null)
			{
				for(Attribute attr : listFileMetadata) {	
					if(attr.getShortName().equalsIgnoreCase("id")) {
						result.setIdentifier(attr.getStringValue());
					}
					if(attr.getShortName().equalsIgnoreCase("history")) {
						result.setIssuedDate(attr.getStringValue().substring(0, 19));
						if(!(result.getIssuedDate().substring(4,5).equals("-") && result.getIssuedDate().substring(7,8).equals("-") && result.getIssuedDate().substring(10,11).equals("T") && result.getIssuedDate().substring(13,14).equals(":") && result.getIssuedDate().substring(16,17).equals(":"))) {
							result.setIssuedDate(EMPTY_FIELD);
						}
					}
					if(attr.getShortName().equalsIgnoreCase("date_update")) {
						result.setModifiedDate(attr.getStringValue().substring(0, 19));
						if(!(result.getModifiedDate().substring(4,5).equals("-") && result.getModifiedDate().substring(7,8).equals("-") && result.getModifiedDate().substring(10,11).equals("T") && result.getModifiedDate().substring(13,14).equals(":") && result.getModifiedDate().substring(16,17).equals(":"))) {
							result.setModifiedDate(EMPTY_FIELD);
						}
					}
				}
			}
			
			//Delete the temporal file "file.nc"
			hdfsSys.deleteFile(Constants.CONFIGFILEPATH+"/Backend/AddDatasets/file.nc");
			
		} catch (IOException e) {
			e.printStackTrace();
		} finally { 
			if (null != nc) try {
				nc.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return result;
	}
    
    // Convert the jsonProfile into a ProfileDataset then to a Dataset
    public static Dataset convertProfileToDataset(String jsonProfileDataset) {
    	List<Map<String, String>> variablesList = new ArrayList<>();
    	Map<String, String> mMap = new HashMap<>();
		Map<String, String> variables = new HashMap<>();
		
		//Convert the json into a ProfileDataset
		ProfileDataset datasetProfile = new Gson().fromJson(jsonProfileDataset, ProfileDataset.class);
		//Extract the variables
		variablesList = datasetProfile.getVariables();
		
		// Convert variables: List<Map<String, String>> into a Map<String,String>
		for(int i = 0; i < variablesList.size(); i++) {
			mMap.putAll(variablesList.get(i));
			variables.put(mMap.get("name"),mMap.get("canonicalName"));
		}
		
		//Import all the metadata into the Dataset except identifier, issuedDate and modifiedDate
		return new Dataset("", datasetProfile.getTitle(), datasetProfile.getDescription(), 
				datasetProfile.getSubject(), datasetProfile.getKeywords(), datasetProfile.getStandards(), datasetProfile.getFormats(), datasetProfile.getLanguage(), 
				datasetProfile.getHomepage(), datasetProfile.getPublisher(), datasetProfile.getSource(), datasetProfile.getObservation(), datasetProfile.getStorageTable(), 
				datasetProfile.getAccessRights(), "", "", datasetProfile.getGeoLocation(), datasetProfile.getSpatialWest(), datasetProfile.getSpatialEast(),
				datasetProfile.getSpatialSouth(), datasetProfile.getSpatialNorth(), datasetProfile.getCoordinateSystem(), datasetProfile.getVerticalCoverageFrom(),
				datasetProfile.getVerticalCoverageTo(), datasetProfile.getVerticalLevel(), datasetProfile.getTemporalCoverageBegin(), datasetProfile.getTemporalCoverageEnd(),
				datasetProfile.getTimeResolution(), variables, "");
		
		
	}
}

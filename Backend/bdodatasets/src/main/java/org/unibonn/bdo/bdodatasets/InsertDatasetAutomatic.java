package org.unibonn.bdo.bdodatasets;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import org.apache.hadoop.fs.Path;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.ini4j.Ini;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.unibonn.bdo.connections.ConsumerCreator;
import org.unibonn.bdo.connections.HDFSFileSystem;
import org.unibonn.bdo.connections.ProducerCreator;
import org.unibonn.bdo.objects.Dataset;
import org.unibonn.bdo.objects.ProfileDataset;
import org.unibonn.bdo.objects.VariableDataset;

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
	
	private static String tokenAuthorization = ""; 
	private static final String EMPTY_FIELD = "";
	
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
    	Producer<Long, String> producer = ProducerCreator.createProducer();
        int noMessageFound = 0;
        while (true) {
        	// 1000 is the time in milliseconds consumer will wait if no record is found at broker.
        	ConsumerRecords<Long, String> consumerRecords = consumer.poll(1000);
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
						runProducer(producer, idFile);
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
    
    public static void runProducer(Producer<Long, String> producer, String idFile) {
    	// Send a message to TOPIC2 with the idFile
    	ProducerRecord<Long, String> record = new ProducerRecord<>(Constants.TOPIC_NAME2, idFile);
    	try {
        	RecordMetadata metadata = producer.send(record).get();
            System.out.println("Record sent with key " + idFile + " to partition " + metadata.partition()
            	+ " with offset " + metadata.offset());
        } catch (ExecutionException | InterruptedException e) {
        	System.out.println("Error in sending record");
            System.out.println(e);
            // Restore interrupted state...
            Thread.currentThread().interrupt();
        } 
    }
    
    // API: Create the metadata to an specific dataset with help of the profile.
    public static boolean analyseInsertDatasetAutomatic(String filename, String idFile, String idProfile) 
    		throws IOException, ParseException, UnirestException {
		Dataset result = new Dataset();
		boolean resultInsert = false;
		boolean resultFlag = true;
		String parameter;
		HttpResponse<JsonNode> response; //Get the profileJson
		HttpResponse<String> response1; //Put the identifier to an idFile
		HttpResponse<String> response2; //Update the profile
		String jsonProfile = "";
		
		//If Harmonization is using only API then tokenAuthorization is empty
		if(tokenAuthorization.isEmpty()) {
			Ini config = new Ini(new File(Constants.INITFILEPATH));
			tokenAuthorization = config.get("DEFAULT", "AUTHORIZATION_JWT");
		}
		
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
		result = convertProfileToDataset(jsonProfile, idFile);
		
		//Set the identifier to the dataset
		String identifier = UUID.randomUUID().toString();
		result.setIdentifier(identifier);
		
		//Extract the issued/modifiedDate,  TemporalCoverageBegin/End
		String nameExtension = new File(filename).getName();
		String[] tokens = nameExtension.split("\\.(?=[^\\.]+$)");
		String extension = tokens[1];
		if(extension.equals("csv") || extension.equals("xls") || extension.equals("xlsx")) {
			result.setTemporalCoverageBegin(EMPTY_FIELD);
			result.setTemporalCoverageEnd(EMPTY_FIELD);
			result = BdoDatasetAnalyser.extractDatesFileName(filename, result);
		}else if(tokens[1].equals("nc")) {
			result = extractionDatesNetcdf(result, filename);
			result = BdoDatasetAnalyser.extractDatesFileName(filename, result);
		}
		
		// Parameters to check if metadata already exist in Fuseki
		parameter = result.getIdFile();
		
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
				// Update profile TemporalCoverageBegin/End
				String updatedProfile = updateProfile(result, jsonProfile);
				response2 = Unirest.post(Constants.HTTPJWT + "fileHandler/metadataProfile/")
						.header("Content-Type", "application/json")
						.header("Authorization", tokenAuthorization)
						.body(updatedProfile)
						.asString();
				if(response2.getStatus() == 200) {
					System.out.println("Successful!	Profile is being updated");
					resultFlag = true;
				} else {
					System.out.println("Error1!   Profile is not being updated.");
				}
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
    
    // Extract dates (issued, modified, temporalCoverage begin, end) and identifier from Datasets Netcdf
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
					result = BdoDatasetAnalyser.netcdfMetadataDatesExtractor(attr, result);
				}
			}
			
			//Delete the temporal file "file.nc"
			Files.deleteIfExists(Paths.get(Constants.CONFIGFILEPATH+"/Backend/AddDatasets/file.nc"));
			
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
    public static Dataset convertProfileToDataset(String jsonProfileDataset, String idFile) {
    	List<VariableDataset> variablesList = new ArrayList<>();
    	List<String> variables = new ArrayList<>();
		
		//Convert the json into a ProfileDataset
		ProfileDataset datasetProfile = new Gson().fromJson(jsonProfileDataset, ProfileDataset.class);
		//Extract the variables
		variablesList = datasetProfile.getVariables();
		
		for (VariableDataset var : variablesList) {
			variables.add(var.getName() + " -- " + var.getUnit() + " -- " + var.getCanonicalName());
		}
		
		String subject = convertWordToLink(datasetProfile.getSubject(), "subject");
		String keywords = convertWordToLink(datasetProfile.getKeywords(), "keywords");
		String geoLoc = convertWordToLink(datasetProfile.getGeoLocation(), "marineregions");
		
		//Import all the metadata into the Dataset except identifier, issuedDate and modifiedDate
		return new Dataset("", idFile, datasetProfile.getTitle(), datasetProfile.getDescription(), 
				subject, keywords, datasetProfile.getStandards(), datasetProfile.getFormats(), datasetProfile.getLanguage(), 
				datasetProfile.getHomepage(), datasetProfile.getPublisher(), datasetProfile.getSource(), datasetProfile.getObservation(), datasetProfile.getStorageTable(), 
				datasetProfile.getLicense(), datasetProfile.getAccessRights(), "", "", geoLoc, datasetProfile.getSpatialWest(), datasetProfile.getSpatialEast(),
				datasetProfile.getSpatialSouth(), datasetProfile.getSpatialNorth(), datasetProfile.getCoordinateSystem(), datasetProfile.getVerticalCoverageFrom(),
				datasetProfile.getVerticalCoverageTo(), datasetProfile.getVerticalLevel(), datasetProfile.getTemporalCoverageBegin(), datasetProfile.getTemporalCoverageEnd(),
				datasetProfile.getTimeResolution(), variables, "");
		
	}
    
    //Profile does not care about url in subject, keywords, geographicLocation
    //Dataset needs the url
  	private static String convertWordToLink(String value, String typeValue) {
  		String result = "";
  		String jsonPath = Constants.CONFIGFILEPATH + "/Frontend/Flask/static/json/" +  typeValue + ".json";
  		if(!value.isEmpty()) {
  			try {
  				String[] tokens = value.split(", ");
  				if(!tokens[0].contains("http")) {
	  				JSONParser parser = new JSONParser();
	  				JSONArray jsonArray = (JSONArray) parser.parse(new FileReader(jsonPath));
	  				for (String token: tokens) {
	  					for(int i=0; i<jsonArray.size(); i++){
	  						JSONObject jsonObject = (JSONObject) jsonArray.get(i);
	  			            if(jsonObject.get("text").toString().equalsIgnoreCase(token.toLowerCase())) {
	  			            	if(result.equals("")) {
	  			            		result = jsonObject.get("value").toString();
	  			            	} else {
	  			            		result = result + ", " + jsonObject.get("value").toString();
	  			            	}
	  			            }
	  			            
	  					}
	  				}
  				}else {
  					result = value;
  				}
  			} catch (IOException | ParseException e) {
  				e.printStackTrace();
  			}
  		}
  		return result;
  	}
    
    // Update temporalCoverage Begin and End in ProfileDataset
    private static String updateProfile(Dataset result, String jsonProfileDataset) {
    	Gson gson  = new Gson();
    	ProfileDataset datasetProfile = new Gson().fromJson(jsonProfileDataset, ProfileDataset.class);
    	String datasetTempCovBegin = result.getTemporalCoverageBegin();
    	String datasetTempCovEnd = result.getTemporalCoverageEnd();
    	String profileTempCovBegin = datasetProfile.getTemporalCoverageBegin();
    	String profileTempCovEnd = datasetProfile.getTemporalCoverageEnd();
    	
    	if(profileTempCovBegin.equals(EMPTY_FIELD) && !datasetTempCovBegin.equals(EMPTY_FIELD)) {
    		datasetProfile.setTemporalCoverageBegin(datasetTempCovBegin);
    	} else if(!profileTempCovBegin.equals(EMPTY_FIELD) && !datasetTempCovBegin.equals(EMPTY_FIELD)){
	    	//profile value is not before dataset value
	    	if (!dateBeforedate(profileTempCovBegin,datasetTempCovBegin)) {
	    		datasetProfile.setTemporalCoverageBegin(datasetTempCovBegin);
	    	}
    	}
    	
    	if(profileTempCovEnd.equals(EMPTY_FIELD) && !datasetTempCovEnd.equals(EMPTY_FIELD)) {
    		datasetProfile.setTemporalCoverageEnd(datasetTempCovEnd);
    	} else if(!profileTempCovEnd.equals(EMPTY_FIELD) && !datasetTempCovEnd.equals(EMPTY_FIELD)){
	    	//dataset value is not before profile value
	    	if (!dateBeforedate(datasetTempCovEnd,profileTempCovEnd)) {
	    		datasetProfile.setTemporalCoverageEnd(datasetTempCovEnd);
	    	}
    	}
    	
    	// if subject, keywords, geolocation in profile has link then convert it into words
    	if(datasetProfile.getSubject().contains("http")) {
    		datasetProfile.setSubject(InsertNewDataset.convertLinkToWord(datasetProfile.getSubject(), "subject"));
    	}
    	if(datasetProfile.getKeywords().contains("http")) {
    		datasetProfile.setKeywords(InsertNewDataset.convertLinkToWord(datasetProfile.getKeywords(), "keywords"));
    	}
		if(datasetProfile.getGeoLocation().contains("http")) {
    		datasetProfile.setGeoLocation(InsertNewDataset.convertLinkToWord(datasetProfile.getGeoLocation(), "marineregions"));
		}
		
    	return gson.toJson(datasetProfile);
    }
    
    private static boolean dateBeforedate(String val1, String val2) {
    	DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        try {
        	Date date1 = format.parse(val1);
        	Date date2 = format.parse(val2);
        	if (date1.before(date2)) {
        		return true;
        	}
        } catch (java.text.ParseException e) {
        	e.printStackTrace();
        }
    	return false;
    }
    
}

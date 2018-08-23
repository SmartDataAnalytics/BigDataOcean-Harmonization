package org.unibonn.bdo.linking;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.unibonn.bdo.bdodatasets.Constants;
import org.unibonn.bdo.connections.MultipartUtility;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

/**
 *  
 * @author Jaime M Trillos
 *
 * Receives 1 parameter, the variables extracted from a specific file
 *
 */

public class SaveVariables {
	
private final static Logger log = LoggerFactory.getLogger(SaveVariables.class);
	
	public static List<String> parseListVariables (List<String> variables) {
		
		saveVariablesCSV(variables);
		
		
		linkingVariablesLimes();
		return variables;
	}
	
	// Create the csv file that contains the variables extracted from a file
	private static void saveVariablesCSV(List<String> variables) {
		try {
			FileWriter writer = new FileWriter(Constants.configFilePath+"/Backend/AddDatasets/temp.csv");
			String header = "http://xmlns.com/foaf/0.1/name";
			writer.write(header);
			writer.write("\n");
			for (int i = 0; i < variables.size(); i++) {
				writer.write(variables.get(i));
				if(i < variables.size()-1) {
					writer.write("\n");
				}
			}

		    writer.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private static void linkingVariablesLimes() {
		HttpResponse<String> response; 
		String requestId = "";
		JsonObject responseObject;
		String charset = "UTF-8";
        File uploadFile1 =new File(Constants.configFilePath + "/Backend/AddDatasets/configLimes/configVariables.xml");
        String requestURL = "http://localhost:8080/submit";
		try {
			MultipartUtility multipart = new MultipartUtility(requestURL, charset);
            multipart.addFilePart("config_file", uploadFile1);
            List<String> responseMultipart = multipart.finish();
			responseObject = new Gson().fromJson(responseMultipart.get(0), JsonObject.class);
			if(responseObject.get("success").getAsBoolean()) {
				requestId = responseObject.get("requestId").getAsString();
				response = Unirest.get("http://localhost:8080/status/" + requestId)
						.asString();
				if(response.getStatus() == 200) {
					responseObject = new Gson().fromJson(response.getBody(), JsonObject.class);
					JsonElement status = responseObject.get("status");
					responseObject = new Gson().fromJson(status, JsonObject.class);
					JsonElement code = responseObject.get("code");
					if(code.getAsInt()==2) {
						response = Unirest.get("http://localhost:8080/results/" + requestId)
								.asString();
						if(response.getStatus() == 200) {
							responseObject = new Gson().fromJson(response.getBody(), JsonObject.class);
							JsonElement availableFiles = responseObject.get("availableFiles");
							if(availableFiles.getAsJsonArray().size()>0) {
								response = Unirest.get("http://localhost:8080/result/" + requestId + "/accepted.txt")
										.asString();
								if(response.getStatus() == 200) {
									log.info(response.getBody().toString());
								}
							} else {
								log.info("There were no matching links");
							}
						}
					}
				}
			} else {
				log.error("Error!");
			}
		} catch (UnirestException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}

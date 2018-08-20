package org.unibonn.bdo.linking;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.unibonn.bdo.bdodatasets.Constants;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

public class ExtractVocabulary {
	
	private final static Logger log = LoggerFactory.getLogger(ExtractVocabulary.class);
	
	public static void main(String[] args) {
		getInfoVocabPrefix("ssn");
	}
	
	//Request API get Vocabulary Info 
	private static void getInfoVocabPrefix(String vocabPrefix) {
		HttpResponse<String> response; //Get the vocabulary
		try {
			response = Unirest.get("https://lov.linkeddata.es/dataset/lov/api/v2/vocabulary/info?vocab="+ vocabPrefix)
					//Constants.API_GET_INFO_VOCAB_REPO + vocabPrefix)
					.header("Content-Type", "application/json")
					.asString();
			if(response.getStatus() == 200) {
				log.info("Successful!  Response API getInfoVocabPrefix");
				JsonObject vocabObject = new Gson().fromJson(response.getBody(), JsonObject.class); //Convert the response into JsonObject
				JsonArray jarray = (JsonArray) vocabObject.get("versions"); // Get only the versions
				JsonElement latestsVersion = jarray.get(0); //Get the last version available
				JsonObject jobj = new Gson().fromJson(latestsVersion, JsonObject.class); //Convert the latest version into JsonObject
				String result = jobj.get("fileURL").getAsString(); // Get the fileURL
				getRDFVocabPrefix(result, vocabPrefix); 
			} else {
				log.error("Error!");
			}
		} catch (UnirestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//Request API get RDF Vocabulary
	private static void getRDFVocabPrefix(String fileURL, String fileName) {
		HttpResponse<String> response; //Get the vocabulary
		try {
			// Create new file in AddDatasets with the name of the prefix
			PrintWriter file = new PrintWriter(Constants.configFilePath+"/Backend/AddDatasets/" + fileName + ".n3"); 
			response = Unirest.get(fileURL)
					.asString();
			if(response.getStatus() == 200) {
				file.println(response.getBody()); // Save the response in the file
				file.close(); // close the file
				log.info("Successful!  Response API getRDFVocabPrefix and saved RDF data in /Backend/AddDatasets/" + fileName + ".n3");
			} else {
				log.error("Error!");
			}
		} catch (UnirestException | FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}

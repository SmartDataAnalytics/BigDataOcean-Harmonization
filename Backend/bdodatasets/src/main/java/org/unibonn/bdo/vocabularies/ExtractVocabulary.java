package org.unibonn.bdo.vocabularies;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.unibonn.bdo.bdodatasets.Constants;
import org.unibonn.bdo.objects.Ontology;
import org.unibonn.bdo.objects.VocabulariesJson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

/**
 *  
 * @author Jaime M Trillos
 *
 * Receives 1 parameter, the prefix of the vocabulary to be download.
 * Integration between Vocabulary repository and Harmonization tool.
 *
 */

public class ExtractVocabulary {
	
	private static final Logger log = LoggerFactory.getLogger(ExtractVocabulary.class);
	
	public static void main(String[] args) {
		List<Ontology> listDataOntology = new ArrayList<>();
		String vocabPrefix = args[0];
		getInfoVocabPrefix(vocabPrefix);
		switch (vocabPrefix) {
			case "bdo":
				listDataOntology = OntologyAnalyser.analyseOntology(Constants.BDO_ONTOLOGY_N3, "variables");
				break;
			case "eionet":
				listDataOntology = OntologyAnalyser.analyseOntology(Constants.EIONET_ONTOLOGY_N3, "keywords");
				break;
			case "inspire":
				listDataOntology = OntologyAnalyser.analyseOntology(Constants.INSPIRE_ONTOLOGY_N3, "subjects");
				break;
			case "geolocbdo":
				listDataOntology = OntologyAnalyser.analyseOntology(Constants.GEOLOC_ONTOLOGY_N3, "geoLocation");
				break;
			default:
			    break;
		}
		convertOntologyintoJsonVocabulary (listDataOntology, vocabPrefix);
	}
	
	//Request API get Vocabulary Info 
	private static void getInfoVocabPrefix(String vocabPrefix) {
		HttpResponse<String> response; //Get the vocabulary
		try {
			response = Unirest.get(Constants.API_GET_INFO_VOCAB_REPO + vocabPrefix)
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
			log.error(e.toString());
		}
	}
	
	//Request API get RDF Vocabulary
	private static void getRDFVocabPrefix(String fileURL, String fileName) {
		HttpResponse<String> response; //Get the vocabulary
		try {
			// Create new file in AddDatasets with the name of the prefix
			PrintWriter file = new PrintWriter(Constants.CONFIGFILEPATH+"/Backend/AddDatasets/ontologiesN3/" + fileName + ".n3"); 
			response = Unirest.get(fileURL)
					.asString();
			if(response.getStatus() == 200) {
				file.println(response.getBody()); // Save the response in the file
				log.info("Successful!  Response API getRDFVocabPrefix and saved RDF data in /Backend/AddDatasets/ontologiesN3/");
			} else {
				log.error("Error!");
			}
			file.close(); // close the file
		} catch (UnirestException | FileNotFoundException e) {
			log.error("ExtractVocabulary", e);
		} 
	}
	
	//Convert ontology into vocabulary json and save it in a json file
	private static void convertOntologyintoJsonVocabulary (List<Ontology> ontology, String topic) {
		log.info("Start creation of JSON file for FrontEnd");
		List<VocabulariesJson> listVocabulary = new ArrayList<>();
		VocabulariesJson vocab;
		String text = "";
		String value = "";
		String name = "";
		switch (topic) {
			case "eionet":
				name = "keywords";
				for(Ontology tempOnto : ontology) {
					// Initialization of variables 
					text = "";
					value = "";
					
					text = tempOnto.getLabel();
					value = tempOnto.getUri();
					vocab = new VocabulariesJson(text, value);
					listVocabulary.add(vocab);
				}
				break;
			case "inspire":
				name = "subject";
				for(Ontology tempOnto : ontology) {
					// Initialization of variables 
					text = "";
					value = "";
					
					text = tempOnto.getLabel();
					value = tempOnto.getUri();
					vocab = new VocabulariesJson(text, value);
					listVocabulary.add(vocab);
				}
				break;
			case "geolocbdo":
				name = "marineregions";
				for(Ontology tempOnto : ontology) {
					// Initialization of variables 
					text = "";
					value = "";
					
					text = tempOnto.getLabel();
					value = tempOnto.getUrl();
					vocab = new VocabulariesJson(text, value);
					listVocabulary.add(vocab);
				}
				break;
			default:
			    break;
		}
		saveFile(listVocabulary, name);
	}
	
	// Create json file in Frontend
	private static void saveFile(List<VocabulariesJson> listVocabulary, String name) {
		try {
			PrintWriter file = new PrintWriter(Constants.CONFIGFILEPATH+"/Frontend/Flask/static/json/" + name + ".json");
			Gson gson  = new GsonBuilder().setPrettyPrinting().create();
			file.println(gson.toJson(listVocabulary)); // Save the json in the file
			file.close(); // close the file
			log.info("Successful!  The json file is saved in /Frontend/Flask/static/json/");
		} catch (FileNotFoundException e) {
			log.error("ExtractVocabulary", e);
		}
	}

}
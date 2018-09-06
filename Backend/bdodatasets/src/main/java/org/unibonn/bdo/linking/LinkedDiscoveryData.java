package org.unibonn.bdo.linking;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.hadoop.fs.shell.Count;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.unibonn.bdo.bdodatasets.Constants;
import org.unibonn.bdo.connections.MultipartUtility;
import org.unibonn.bdo.objects.Ontology;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hp.hpl.jena.sparql.pfunction.library.concat;
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

public class LinkedDiscoveryData {
	
private final static Logger log = LoggerFactory.getLogger(LinkedDiscoveryData.class);
	
	public static List<String> parseListVariables (List<String> variables, String topic) {
		saveVariablesCSV(variables);
		Map<String, String> resultLimes = linkingVariablesLimes();
		List<Ontology> listDataOntology = OntologyAnalyser.analyseOntology(Constants.BDO_Ontology_N3, "variables");
		List<String> variablesLinked = formVariablesList(listDataOntology, resultLimes, variables);
		return variablesLinked;
	}
	
	// Create the csv file that contains the variables extracted from a file
	private static void saveVariablesCSV(List<String> variables) {
		try {
			FileWriter writer = new FileWriter(Constants.configFilePath+"/Backend/AddDatasets/temp2.csv");
			String header = "http://xmlns.com/foaf/0.1/name";
			writer.write(header);
			writer.write("\n");
			for (int i = 0; i < variables.size(); i++) {
				writer.write(variables.get(i));
				//log.info(variables.get(i));
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
	
	private static Map<String, String> linkingVariablesLimes() {
		Map<String, String> resultLimesMap = new HashMap<String, String>();
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
									//log.info(response.getBody().toString());
									String resultLimes = response.getBody().toString();
									resultLimesMap = stringintoMap(resultLimes);
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
		return resultLimesMap;
	}
	

	private static Map<String, String> stringintoMap(String resultLimes) {
		Map<String, String> result = new HashMap<>();
		String[] tokenNewLine = resultLimes.split("\n");
		for (String token1 : tokenNewLine) { 
			String[] tokenTab = token1.split("\t");
			String tokenKey =tokenTab[1].replaceAll("<", "").replaceAll(">", "");
			String tokenValue =tokenTab[0].replaceAll("<", "").replaceAll(">", "");
			result.put(tokenKey, tokenValue);
		}
		return result;
	}
	
	// Compare the result from limes with the data from ontologies to obtain the list<String>
	private static List<String> formVariablesList (List<Ontology> listOntology, Map<String,String> resultLimes, List<String> rawVariables){
		List<String> variablesLinked = new ArrayList<String>();
		for (String rawVar : rawVariables) {
			if (resultLimes.get(rawVar) != null) {
				for(Ontology tempOnto : listOntology) {
					if(tempOnto.getUri().equals(resultLimes.get(rawVar))) {
						variablesLinked.add(rawVar + " -- " + tempOnto.getLabel());
					}
				}
				
			} else {
				variablesLinked.add(rawVar + " -- ");
			}
		}
		return variablesLinked;
	}

}

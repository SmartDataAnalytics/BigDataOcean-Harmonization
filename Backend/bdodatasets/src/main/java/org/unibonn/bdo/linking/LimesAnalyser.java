package org.unibonn.bdo.linking;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
 * Create files, execute limes and return links
 *
 */

public class LimesAnalyser {
	
	private static final Logger log = LoggerFactory.getLogger(LimesAnalyser.class);
	
	private static String fileCSV = "";
	private static String fileConfig = "";
	
	// function that create files, execute limes and return links
	public static Map<String,String> exec(List<String> rawName, String topic){
		Map<String,String> resultLimes = new HashMap<>();
		try {
			// list only will have rawname
			if (topic.equals("variables")){
				List<String> tempRawName = new ArrayList<>();
				for(int i = 0; i < rawName.size(); i++) {
					String name = rawName.get(i).split(" -- ")[0];
					tempRawName.add(name);
				}
				rawName = tempRawName;
			}
			createCsvFile(rawName);
			createConfigFile(topic);
			resultLimes = linkingLimes();
			Files.deleteIfExists(Paths.get(fileCSV));
			Files.deleteIfExists(Paths.get(fileConfig));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return resultLimes;
	}
	
	
	// Create the csv file that contains the raw names extracted from a file or NER
	private static void createCsvFile(List<String> rawName) {
		try {
			UUID fileName = UUID.randomUUID();
			fileCSV = Constants.CONFIGFILEPATH+"/Backend/AddDatasets/" + fileName +".csv";
			FileWriter writer = new FileWriter(fileCSV);
			String header = "http://xmlns.com/foaf/0.1/name";
			writer.write(header);
			writer.write("\n");
			for (int i = 0; i < rawName.size(); i++) {
				writer.write(rawName.get(i));
				if(i < rawName.size()-1) {
					writer.write("\n");
				}
			}
		    writer.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	// Create the config file for limes
	private static void createConfigFile(String topic) {
		try {
			fileConfig = Constants.CONFIGFILEPATH+"/Backend/AddDatasets/config.xml";
			FileWriter writer = new FileWriter(fileConfig);
			writer.write(Constants.HEADER_CONFIG_LIMES_FILE);
			writer.write("\n");
			writer.write(sourceTargetConfig(topic));
			writer.write("\n");
			writer.write(Constants.FOOTER_CONFIG_LIMES_FILE);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	// Find links between csv and ontology using limes
	private static Map<String, String> linkingLimes() {
		Map<String, String> resultLimesMap = new HashMap<>();
		HttpResponse<String> response; 
		String requestId = "";
		JsonObject responseObject;
		String charset = "UTF-8";
        File uploadFile1 =new File(fileConfig);
        String requestURL = Constants.HTTPLIMES + "submit";
		try {
			MultipartUtility multipart = new MultipartUtility(requestURL, charset);
            multipart.addFilePart("config_file", uploadFile1);
            List<String> responseMultipart = multipart.finish();
			responseObject = new Gson().fromJson(responseMultipart.get(0), JsonObject.class);
			if(responseObject.get("success").getAsBoolean()) {
				requestId = responseObject.get("requestId").getAsString();
				boolean statusFlag = statusLimes(requestId);
				if (statusFlag) {
					response = Unirest.get(Constants.HTTPLIMES + "results/" + requestId)
							.asString();
					if(response.getStatus() == 200) {
						responseObject = new Gson().fromJson(response.getBody(), JsonObject.class);
						JsonElement availableFiles = responseObject.get("availableFiles");
						if(availableFiles.getAsJsonArray().size()>0) {
							response = Unirest.get(Constants.HTTPLIMES + "result/" + requestId + "/accepted.txt")
									.asString();
							if(response.getStatus() == 200) {
								String resultLimes = response.getBody();
								resultLimesMap = stringintoMap(resultLimes);
							}
						} else {
							resultLimesMap = null;
						}
					}
				} else {
					log.error("Error: The Status of LIMES is -1 (Unknown)");
				}
			} else {
				log.error("Error: LIMES has no produce the id");
			}
		} catch (UnirestException | IOException e) {
			e.printStackTrace();
		}
		return resultLimesMap;
	}
	
	// Recursive function to wait until the process of Limes has finished (status code = 2)
	private static boolean statusLimes(String id) {
		boolean flag = false;
		int code = -1;
		HttpResponse<String> response; 
		JsonObject responseObject;
		try {
			response = Unirest.get(Constants.HTTPLIMES + "status/" + id)
					.asString();
			if(response.getStatus() == 200) {
				responseObject = new Gson().fromJson(response.getBody(), JsonObject.class);
				JsonElement status = responseObject.get("status");
				responseObject = new Gson().fromJson(status, JsonObject.class);
				JsonElement jsonCode = responseObject.get("code");
				code = jsonCode.getAsInt();
				if (code == 2 ) {
					flag = true;
				}
				if(code == 0 || code == 1) {
					return statusLimes(id);
				}
			}
		} catch (UnirestException e) {
			e.printStackTrace();
		}
		
		return flag;
	}
	
	// transform response from limes into map
	private static Map<String, String> stringintoMap(String resultLimes) {
		Map<String, String> result = new HashMap<>();
		String[] tokenNewLine = resultLimes.split("\n");
		if (tokenNewLine.length > 0 && !tokenNewLine[0].isEmpty()) {
			for (String token1 : tokenNewLine) { 
				String[] tokenTab = token1.split("\t");
				String tokenKey =tokenTab[1].replaceAll("<", "").replaceAll(">", "");
				String tokenValue =tokenTab[0].replaceAll("<", "").replaceAll(">", "");
				if(result.containsKey(tokenKey)){
					result.put(tokenKey, result.get(tokenKey) + "," + tokenValue);
			    }else{
			    	result.put(tokenKey, tokenValue);
			    }
				
			}
		}else {
			result = null;
		}
		return result;
	}
	
	// create configuration of source and target for limes config
	private static String sourceTargetConfig (String topic) {
		String config = "";
		String targetconfig = "	<TARGET>\n" + 
				"		<ID>RawNames</ID>\n" + 
				"		<ENDPOINT>" + fileCSV + "</ENDPOINT>\n" + 
				"		<VAR>?x</VAR>\n" + 
				"		<PAGESIZE>1000</PAGESIZE>\n" + 
				"		<RESTRICTION>?x rdf:type http://xmlns.com/foaf/0.1/name</RESTRICTION>\n" + 
				"		<PROPERTY>http://xmlns.com/foaf/0.1/name AS lowercase</PROPERTY>\n" + 
				"		<TYPE>CSV</TYPE>\n" + 
				"	</TARGET>\n";
		switch (topic) {
			case "variables":
				config = "	<SOURCE>\n" + 
						"		<ID>OntologyRDF</ID>\n" + 
						"		<ENDPOINT>" + Constants.BDO_ONTOLOGY_N3 + "</ENDPOINT>\n" + 
						"		<VAR>?y</VAR>\n" + 
						"		<PAGESIZE>1000</PAGESIZE>\n" + 
						"		<RESTRICTION>?y a bdo:Variable</RESTRICTION>\n" + 
						"		<PROPERTY>rdfs:label AS nolang->lowercase</PROPERTY>\n" + 
						"		<PROPERTY>bdo:hasCanonicalName AS nolang->lowercase</PROPERTY>\n" + 
						"		<TYPE>N3</TYPE>\n" + 
						"	</SOURCE>\n" + targetconfig;
				config = config + "	<METRIC>\n" + 
						"		OR(Cosine(x.http://xmlns.com/foaf/0.1/name, y.rdfs:label)|0.8,Cosine(x.http://xmlns.com/foaf/0.1/name, y.bdo:hasCanonicalName)|0.8)\n" + 
						"	</METRIC>\n";
				config = config + "	<ACCEPTANCE>\n" + 
			    		"		<THRESHOLD>0.98</THRESHOLD>\n" + 
			    		"		<FILE>accepted.txt</FILE>\n" + 
			    		"		<RELATION>owl:sameAs</RELATION>\n" + 
			    		"	</ACCEPTANCE>\n" + 
			    		"	<REVIEW>\n" + 
			    		"		<THRESHOLD>0.95</THRESHOLD>\n" + 
			    		"		<FILE>reviewme.txt</FILE>\n" + 
			    		"		<RELATION>owl:sameAs</RELATION>\n" + 
			    		"	</REVIEW>";
				break;
			case "geoLocation":
				config = "	<SOURCE>\n" + 
						"		<ID>OntologyRDF</ID>\n" + 
						"		<ENDPOINT>" + Constants.GEOLOC_ONTOLOGY_N3 + "</ENDPOINT>\n" + 
						"		<VAR>?y</VAR>\n" + 
						"		<PAGESIZE>1000</PAGESIZE>\n" + 
						"		<RESTRICTION>?y a geolocbdo:GeoStandard</RESTRICTION>\n" + 
						"		<PROPERTY>rdfs:label AS nolang->lowercase</PROPERTY>\n" + 
						"		<TYPE>N3</TYPE>\n" + 
						"	</SOURCE>\n" + targetconfig;
				config = config + "	<METRIC>\n" + 
						"		Cosine(x.http://xmlns.com/foaf/0.1/name, y.rdfs:label)\n" + 
						"	</METRIC>\n";
				config = config + "	<ACCEPTANCE>\n" + 
			    		"		<THRESHOLD>0.95</THRESHOLD>\n" + 
			    		"		<FILE>accepted.txt</FILE>\n" + 
			    		"		<RELATION>owl:sameAs</RELATION>\n" + 
			    		"	</ACCEPTANCE>\n" + 
			    		"	<REVIEW>\n" + 
			    		"		<THRESHOLD>0.8</THRESHOLD>\n" + 
			    		"		<FILE>reviewme.txt</FILE>\n" + 
			    		"		<RELATION>owl:sameAs</RELATION>\n" + 
			    		"	</REVIEW>";
				break;
			case "keywords": // eionet ontology
				config = "	<SOURCE>\n" + 
						"		<ID>OntologyRDF</ID>\n" + 
						"		<ENDPOINT>" + Constants.EIONET_ONTOLOGY_N3 + "</ENDPOINT>\n" + 
						"		<VAR>?y</VAR>\n" + 
						"		<PAGESIZE>1000</PAGESIZE>\n" + 
						"		<RESTRICTION></RESTRICTION>\n" + 
						"		<PROPERTY>skos:prefLabel AS nolang->lowercase</PROPERTY>\n" + 
						"		<TYPE>N3</TYPE>\n" + 
						"	</SOURCE>\n" + targetconfig;
				config = config + "	<METRIC>\n" + 
						"		Cosine(x.http://xmlns.com/foaf/0.1/name, y.skos:prefLabel)\n" + 
						"	</METRIC>\n";
				config = config + "	<ACCEPTANCE>\n" + 
			    		"		<THRESHOLD>0.95</THRESHOLD>\n" + 
			    		"		<FILE>accepted.txt</FILE>\n" + 
			    		"		<RELATION>owl:sameAs</RELATION>\n" + 
			    		"	</ACCEPTANCE>\n" + 
			    		"	<REVIEW>\n" + 
			    		"		<THRESHOLD>0.8</THRESHOLD>\n" + 
			    		"		<FILE>reviewme.txt</FILE>\n" + 
			    		"		<RELATION>owl:sameAs</RELATION>\n" + 
			    		"	</REVIEW>";
				break;
			case "subjects": // inspire ontology
				config = "	<SOURCE>\n" + 
						"		<ID>OntologyRDF</ID>\n" + 
						"		<ENDPOINT>" + Constants.INSPIRE_ONTOLOGY_N3 + "</ENDPOINT>\n" + 
						"		<VAR>?y</VAR>\n" + 
						"		<PAGESIZE>1000</PAGESIZE>\n" + 
						"		<RESTRICTION></RESTRICTION>\n" + 
						"		<PROPERTY>dct:title AS nolang->lowercase</PROPERTY>\n" + 
						"		<TYPE>N3</TYPE>\n" + 
						"	</SOURCE>\n" + targetconfig;
				config = config + "	<METRIC>\n" + 
						"		Qgrams(x.http://xmlns.com/foaf/0.1/name, y.dct:title)\n" + 
						"	</METRIC>\n";
				config = config + "	<ACCEPTANCE>\n" + 
			    		"		<THRESHOLD>0.74</THRESHOLD>\n" + 
			    		"		<FILE>accepted.txt</FILE>\n" + 
			    		"		<RELATION>owl:sameAs</RELATION>\n" + 
			    		"	</ACCEPTANCE>\n" + 
			    		"	<REVIEW>\n" + 
			    		"		<THRESHOLD>0.7</THRESHOLD>\n" + 
			    		"		<FILE>reviewme.txt</FILE>\n" + 
			    		"		<RELATION>owl:sameAs</RELATION>\n" + 
			    		"	</REVIEW>";
				break;
			default:
				break;
		}
		return config;
	}
}

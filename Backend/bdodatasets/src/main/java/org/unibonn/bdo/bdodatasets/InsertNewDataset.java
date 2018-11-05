package org.unibonn.bdo.bdodatasets;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.ini4j.Ini;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.unibonn.bdo.connections.QueryExecutor;
import org.unibonn.bdo.objects.Dataset;
import org.unibonn.bdo.objects.ProfileDataset;
import org.unibonn.bdo.objects.VariableDataset;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

/**
 *  
 * @author Jaime M Trillos
 * @author Ana C Trillos
 *
 * Receives 3 parameters: flag ("" if contains an identifier, "other" if not contains an identifier)
 * parameter (it is the uri, or the combination of data to be queried (title>publisher>issuedDate>idFile))
 * jsonDataset (the Dataset object to be added in JSON format)
 *
 */

public class InsertNewDataset {
	
	private static String tokenAuthorization = ""; 
	
	public static void main(String[] args) throws IOException, ParseException {
		Ini config = new Ini(new File(Constants.INITFILEPATH));
		tokenAuthorization = config.get("DEFAULT", "AUTHORIZATION_JWT");
		String flag = args[0];
		String parameter = args[1];
		String jsonDataset = args[2];
		exec(flag, parameter, jsonDataset);
	}

	private static void exec(String flag, String parameter, String jsonDataset) throws IOException, ParseException {
		Dataset newDataset = convertToObjectDataset(jsonDataset);
		insertDataset(flag, parameter, newDataset);
	}
	
	public static boolean insertDataset (String flag, String parameter, Dataset newDataset) throws IOException, ParseException {
		boolean resultFlag = false;
		try {
			//construct the SPARQL query to insert dataset
			String insertQuery = "PREFIX dct: <http://purl.org/dc/terms/> \n" + 
					"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n" + 
					"PREFIX owl: <http://www.w3.org/2002/07/owl#> \n" + 
					"PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> \n" + 
					"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> \n" + 
					"PREFIX foaf: <http://xmlns.com/foaf/0.1/> \n" + 
					"PREFIX disco: <http://rdf-vocabulary.ddialliance.org/discovery#> \n" + 
					"PREFIX dcat: <https://www.w3.org/TR/vocab-dcat/> \n" + 
					"PREFIX bdo: <http://bigdataocean.eu/bdo/> \n" + 
					"PREFIX bdocm: <http://www.bigdataocean.eu/standards/canonicalmodel#> \n" +
					"PREFIX ids: <http://industrialdataspace/information-model/> \n" + 
					"PREFIX qudt: <http://qudt.org/schema/qudt/> \n" + 
					"PREFIX unit: <http://qudt.org/vocab/unit/> \n" + 
					"PREFIX ignf: <http://data.ign.fr/def/ignf#> \n" + 
					"PREFIX skos: <http://www.w3.org/2004/02/skos/core#> \n" + 
					"\n" + 
					"INSERT DATA {\n" + 
					"  bdo:VerticalCoverage a owl:Class . \n" + 
					"  bdo:timeCoverage a owl:ObjectProperty . \n" + 
					"  bdo:verticalLevel a owl:Datatypeproperty . \n" + 
					"  bdo:timeResolution a owl:Datatypeproperty . \n" + 
					"  bdo:verticalFrom a owl:ObjectProperty . \n" + 
					"  \n" + 
					"  bdo:"+newDataset.getIdentifier()+"_TC a bdo:TimeCoverage ; \n" + 
					"    ids:beginning \""+newDataset.getTemporalCoverageBegin()+"\"^^xsd:dateTime ; \n" + 
					"    ids:end \""+newDataset.getTemporalCoverageEnd()+"\"^^xsd:dateTime . \n" + 
					"    \n" + 
					"  bdo:"+newDataset.getIdentifier()+"_VC a bdo:VerticalCoverage ; \n" + 
					"    bdo:verticalFrom \""+newDataset.getVerticalCoverageFrom()+"\"^^xsd:double ; \n" + 
					"    bdo:verticalTo \""+newDataset.getVerticalCoverageTo()+"\"^^xsd:double . \n" + 
					"  \n" + 
					"  bdo:"+newDataset.getIdentifier()+"_GC a ignf:GeographicBoundingBox ; \n" + 
					"    ignf:westBoundLongitude \""+newDataset.getSpatialWest()+"\"^^xsd:double ; \n" + 
					"    ignf:eastBoundLongitude \""+newDataset.getSpatialEast()+"\"^^xsd:double ; \n" + 
					"    ignf:southBoundLatitude \""+newDataset.getSpatialSouth()+"\"^^xsd:double ; \n" + 
					"    ignf:northBoundLatitude \""+newDataset.getSpatialNorth()+"\"^^xsd:double . \n" + 
					"  \n" + 
					"  bdo:"+newDataset.getIdentifier()+" a dcat:Dataset ; \n" + 
					"    dct:identifier \""+newDataset.getIdentifier()+"\" ; \n" + 
					"    dct:title \""+newDataset.getTitle()+"\" ; \n" + 
					"    dct:description \""+newDataset.getDescription()+"\" ; \n" + 
					"    dct:Standard \""+newDataset.getStandards()+"\" ; \n" + 
					"    dct:format \""+newDataset.getFormats()+"\" ; \n" +  
					"    foaf:homepage \""+newDataset.getHomepage()+"\" ; \n" + 
					"    dct:publisher \""+newDataset.getPublisher()+"\" ; \n" + 
									
					"    dct:creator \""+newDataset.getSource()+"\" ; \n" + 
					"    rdfs:comment \""+newDataset.getObservations()+"\" ; \n" + 
					"    bdo:storageTable \""+newDataset.getStorageTable()+"\" ; \n" + 
					
					"    dct:accessRights \""+newDataset.getAccessRights()+"\" ; \n" + 
					"    dct:issued \""+newDataset.getIssuedDate()+"\"^^xsd:dateTime ; \n" + 
					"    dct:modified \""+newDataset.getModifiedDate()+"\"^^xsd:dateTime ; \n" +				
					"    bdo:GeographicalCoverage bdo:"+newDataset.getIdentifier()+"_GC ; \n" + 
					"    dct:conformsTo \""+newDataset.getCoordinateSystem()+"\" ; \n" + 
					"    bdo:verticalCoverage bdo:"+newDataset.getIdentifier()+"_VC ; \n" + 
					"    bdo:verticalLevel \""+newDataset.getVerticalLevel()+"\" ; \n" + 
					"    bdo:timeCoverage bdo:"+newDataset.getIdentifier()+"_TC ; \n" + 
					"    bdo:timeResolution \""+newDataset.getTimeResolution()+"\" ; \n" +
					"    dcat:subject " ;
			//lists the subjects
			String[] listSubject = newDataset.getSubject().split(", ");
			for(int i = 0; i < listSubject.length; i++) {
				if(i == listSubject.length-1) {
					insertQuery += "<"+listSubject[i]+"> ; \n" ;
				}else {
					insertQuery += "<"+listSubject[i]+"> , \n" ;
				}
			}
			insertQuery += "    dcat:theme ";
			//lists the keywords
			String[] listKeywords = newDataset.getKeywords().split(", ");
			for(int i = 0; i < listKeywords.length; i++) {
				if(i == listKeywords.length-1) {
					insertQuery += "<"+listKeywords[i]+"> ; \n" ;
				}else {
					insertQuery += "<"+listKeywords[i]+"> , \n" ;
				}
			}
			insertQuery +="    dct:language ";
			//lists the languages
			String[] listLang = newDataset.getLanguage().split(", ");
			for(int i = 0; i < listLang.length; i++) {
				if(i == listLang.length-1) {
					insertQuery += "\""+listLang[i]+"\" ; \n" ;
				}else {
					insertQuery += "\""+listLang[i]+"\" , \n" ;
				}
			}
			//lists the geographical locations
			String[] listGeoLoc = newDataset.getGeoLocation().split(", ");
			//if the first item of the list is not empty then insert the property...
			if(!listGeoLoc[0].equals("")) { 
				insertQuery +="    dct:spatial ";
				for(int i = 0; i < listGeoLoc.length; i++) {
					if(i == listGeoLoc.length-1) {
						insertQuery += "<"+listGeoLoc[i]+"> ; \n" ;
					}else {
						insertQuery += "<"+listGeoLoc[i]+"> , \n" ;
					}
				}
			}
			insertQuery += "    disco:variable ";
			//lists the variables
			List<String> variables = newDataset.getVariable();
			for(int i = 0; i < variables.size(); i++) {
				String name = variables.get(i).split(" -- ")[0];
				String varKey = name.replaceAll("[^a-zA-Z0-9]_", "");
				if(i == variables.size()-1) {
					insertQuery += "bdo:"+newDataset.getIdentifier()+"_"+varKey+" . \n ";
				}else {
					insertQuery += "bdo:"+newDataset.getIdentifier()+"_"+varKey+" , ";
				}
			}
			
			insertQuery += "\n";
			//create the triples for each variable
			String pathFile = Constants.CONFIGFILEPATH+"/Frontend/Flask/static/json/canonicalModelMongo.json";
			JSONParser parser = new JSONParser();
			JSONArray variablesCM = (JSONArray) parser.parse(new FileReader(pathFile));
			for(String variable : variables) {
				String[] variablesTokens = variable.split(" -- ");
		        String sameAs = null;
				/*search if the raw variable extracted from netcdf is equal to the json
				* change the value of the raw variable to the sameAs of the json (http://...)
				*/
		        boolean flagText = false;
		        for(int i=0; i<variablesCM.size(); i++){
		        	JSONObject token = (JSONObject) variablesCM.get(i);
		            String text = token.get("canonicalName").toString();
		            String url = token.get("sameAs").toString();
		            if(text.equals(variablesTokens[2]) && !url.equals("")) {
		            	sameAs = url;
		            	flagText = true;
		            	break;
		            }
		        }
				String varKey = variablesTokens[0].replaceAll("[^a-zA-Z0-9_]", "");
				// If the value of the attribute sameAs is empty or does not exist
		        if(!flagText) {
		        	sameAs = "http://www.bigdataocean.eu/standards/canonicalmodel#" + varKey;
		        }
				insertQuery += " bdo:"+newDataset.getIdentifier()+"_"+varKey+" a bdo:BDOVariable ; \n" + 
						"    dct:identifier \""+variablesTokens[0]+"\" ; \n" +
						"    skos:prefLabel \""+variablesTokens[2]+"\"@en ; \n" +
						"    bdocm:canonicalUnit \""+variablesTokens[1]+"\" ; \n" +
						"    owl:sameAs <"+sameAs+"> . \n" +
						"    \n" ;
			}
			insertQuery += "}";
			
			//if the dataset to be added is copernicus or netcdf, queries by URI
			if(flag.equals("")) {
				String []param = parameter.split(" ");
				String query = "ASK {"+param[0]+" ?p ?o}";
				
				//Query Jena Fueski to see if the URI to be added already exists
				boolean results = QueryExecutor.askQuery(query);
				// if the URI does not exists
				if(!results){
					//Add the dataset to Jena Fueski
					QueryExecutor.insertQuery(insertQuery);
					resultFlag = true;
					System.out.print("Successful");
					//Request API post and put
					requestAPIJWT(newDataset, param);
				}else{
					resultFlag = false;
					System.out.print("Error3!   URI already exists.");
				}
			//if not, queries by a selection of parameters: title, publisher and issued date
			}else if(flag.equals("other")) {
				String []param = parameter.split(">");
	
				String query = "PREFIX dct: <http://purl.org/dc/terms/>\n" +
					"PREFIX bdo: <http://bigdataocean.eu/bdo/>\n" +
					"PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n "+
					"ASK {?uri dct:title \""+param[0]+"\" ;\n" +
					"dct:publisher \""+param[1]+"\" ;\n" +
					"dct:issued \""+param[2]+"\"^^xsd:dateTime }\n" ;
	
				//Query Jena Fueski to see if the URI to be added already exists
				boolean results = QueryExecutor.askQuery(query); 
				// if the dataset does not exists
				if(!results){
					//Add the dataset to Jena Fueski
					QueryExecutor.insertQuery(insertQuery);
					resultFlag = true;
					System.out.print("Successful");
					//Request API post and put
					requestAPIJWT(newDataset, param);
				}else{
					resultFlag = false;
					System.out.print("Error3!   URI already exists.");
				}
			}
		}catch (Exception e) {
			e.fillInStackTrace();
		}
		return resultFlag;
	}

	public static Dataset convertToObjectDataset(String jsonDataset) throws FileNotFoundException {
		// Parse into JSON the Dataset instance with all metadata from a dataset
		JsonReader reader = new JsonReader(new FileReader(jsonDataset));
		
		return new Gson().fromJson(reader, Dataset.class);
		
	}
	
	// Take the Dataset of the profile and return the json
	private static String printJsonProfile(Dataset dataset){
		Gson gson  = new Gson();
		List<VariableDataset> variablesList = new ArrayList<>();
		VariableDataset vardataset;
		List<String> variables = dataset.getVariable();
		for(int i = 0; i < variables.size(); i++) {
			String[] tokens = variables.get(i).split(" -- ");
			vardataset = new VariableDataset(tokens[0], tokens[2], tokens[1]);
			variablesList.add(vardataset);
		}
		
		String subject = convertLinkToWord(dataset.getSubject(), "subject");
		String keywords = convertLinkToWord(dataset.getKeywords(), "keywords");
		String geoLoc = convertLinkToWord(dataset.getGeoLocation(), "marineregions");
		
		ProfileDataset datasetProfile = new ProfileDataset(dataset.getProfileName(), dataset.getTitle(), dataset.getDescription(), 
				subject, keywords, dataset.getStandards(), dataset.getFormats(), dataset.getLanguage(), 
				dataset.getHomepage(), dataset.getPublisher(), dataset.getSource(), dataset.getObservations(), dataset.getStorageTable(), 
				dataset.getAccessRights(), geoLoc, dataset.getSpatialWest(), dataset.getSpatialEast(),
				dataset.getSpatialSouth(), dataset.getSpatialNorth(), dataset.getCoordinateSystem(), dataset.getVerticalCoverageFrom(),
				dataset.getVerticalCoverageTo(), dataset.getVerticalLevel(), dataset.getTemporalCoverageBegin(), dataset.getTemporalCoverageEnd(),
				dataset.getTimeResolution(), variablesList, null);
		return gson.toJson(datasetProfile);
		
	}
	
	//Profile does not care about url in subject, keywords, geographicLocation
	public static String convertLinkToWord(String value, String typeValue) {
		String result = "";
		String jsonPath = Constants.CONFIGFILEPATH + "/Frontend/Flask/static/json/" +  typeValue + ".json";
		if(!value.isEmpty()) {
			try {
				String[] tokens = value.split(", ");
				JSONParser parser = new JSONParser();
				JSONArray jsonArray = (JSONArray) parser.parse(new FileReader(jsonPath));
				for (String token: tokens) {
					for(int i=0; i<jsonArray.size(); i++){
						JSONObject jsonObject = (JSONObject) jsonArray.get(i);
			            if(jsonObject.get("value").toString().equals(token)) {
			            	if(result.equals("")) {
			            		result = jsonObject.get("text").toString();
			            	} else {
			            		result = result + ", " + jsonObject.get("text").toString();
			            	}
			            }
			            
					}
				}
			} catch (IOException | ParseException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	//Request API post and put
	private static void requestAPIJWT(Dataset newDataset, String []param) {
		HttpResponse<String> response; //Post the profile
		HttpResponse<String> response1; //Put the identifier to an idFile
		if(!newDataset.getProfileName().equals("")) {
			try {
				response = Unirest.post(Constants.HTTPJWT + "fileHandler/metadataProfile/")
						.header("Content-Type", "application/json")
						.header("Authorization", tokenAuthorization)
						.body(printJsonProfile(newDataset))
						.asString();
				if(response.getStatus() == 200) {
					System.out.println("Successful!	Profile is being added");
				} else {
					System.out.println("Error1!   Profile is not being added.");
				}
			} catch (UnirestException e) {
				e.printStackTrace();
			}
		}
		// This is for InsertNewDataset with flag = "" (URI idFile)
		if(param.length == 2) {
			String idFile = param[1];
			try {
				response1 = Unirest.put(Constants.HTTPJWT + "fileHandler/file/" + idFile + 
						"/metadata/" + newDataset.getIdentifier())
						.header("Content-Type", "application/json")
						.header("Authorization", tokenAuthorization)
						.asString();
				if(response1.getStatus() == 200) {
					System.out.println("Successful!	Identifier is being added");
					
					//Send to the kafka producer the idFile TOPIC2
					InsertDatasetAutomatic.runProducer(idFile);
				} else {
					System.out.println("Error2!   Identifier is not being added.");
				}
			} catch (UnirestException e) {
				e.printStackTrace();
			}
		}
		// This is for InsertNewDataset with flag = "other" (title>publisher>issuedDate>idFile)
		else if(param.length == 4) {
			String idFile = param[3];
			try {
				response1 = Unirest.put(Constants.HTTPJWT + "fileHandler/file/" + idFile + 
						"/metadata/" + newDataset.getIdentifier())
						.header("Content-Type", "application/json")
						.header("Authorization", tokenAuthorization)
						.asString();
				if(response1.getStatus() == 200) {
					System.out.println("Successful!	Identifier is being added");
					
					//Send to the kafka producer the idFile TOPIC2
					InsertDatasetAutomatic.runProducer(idFile);
				} else {
					System.out.println("Error2!   Identifier is not being added.");
				}
			} catch (UnirestException e) {
				e.printStackTrace();
			}
		}
	}
	
}

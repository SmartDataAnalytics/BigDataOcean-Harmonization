package org.unibonn.bdo.bdodatasets;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map.Entry;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.unibonn.bdo.objects.Dataset;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;


/**
 *  
 * @author Jaime M Trillos
 * @author Ana C Trillos
 *
 * Receives 3 parameters: flag ("" if copernicus or netcdf, "other" if not copernicus or netcdf)
 * parameter (it is the uri, or the combination of data to be queried)
 * jsonDataset (the Dataset object to be added in JSON format)
 *
 */

public class InsertNewDataset {
	
	private final static Logger log = LoggerFactory.getLogger(InsertNewDataset.class);
	
	public static void main(String[] args) throws FileNotFoundException, IOException, ParseException {
		String flag = args[0];
		String parameter = args[1];
		String jsonDataset = args[2];
		//String flag = "";
		//String parameter = "<http://bigdataocean.eu/bdo/MEDSEA_ANALYSIS_mmmmFORECAST_PHY_006_013>";
		//String jsonDataset = Constants.configFilePath+"/Backend/AddDatasets/jsonDataset.json";
		exec(flag, parameter, jsonDataset);
	}

	private static void exec(String flag, String parameter, String jsonDataset) throws FileNotFoundException, IOException, ParseException {
		Dataset newDataset = convertToObjectDataset(jsonDataset);
		
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
		int j=0;
		//lists the variables
		for(Entry<String, String> var : newDataset.getVariables().entrySet()) {
			if(j == newDataset.getVariables().size()-1) {
				insertQuery += "bdo:"+newDataset.getIdentifier()+"_"+var.getKey()+" . \n ";
			}else {
				insertQuery += "bdo:"+newDataset.getIdentifier()+"_"+var.getKey()+" , ";
			}
			j++;
		}
		insertQuery += "\n";
		//create the triples for each variable
		for(Entry<String, String> var : newDataset.getVariables().entrySet()) {
			String pathFile = Constants.configFilePath+"/Frontend/Flask/static/json/variablesCF_BDO.json";
			JSONParser parser = new JSONParser();
			JSONArray variablesCF = (JSONArray) parser.parse(new FileReader(pathFile));
	        String sameAs = null;
			/*search if the keyword extracted from netcdf is equal to the json
			* change the value of the keyword variable to the value of the json (http://...)
			*/
	        for(int i=0; i<variablesCF.size(); i++){
	        	JSONObject keyword = (JSONObject) variablesCF.get(i);
	            String text = keyword.get("text").toString();
	            if(text.equals(var.getValue())) {
	            	sameAs = keyword.get("value").toString();
	            	break;
	            }
	        }
			insertQuery += " bdo:"+newDataset.getIdentifier()+"_"+var.getKey()+" a bdo:BDOVariable ; \n" + 
					"    dct:identifier \""+var.getKey()+"\" ; \n" +
					"    skos:prefLabel \""+var.getValue()+"\"@en ; \n" +
					"    owl:sameAs <"+sameAs+"> . \n" +
					"    \n" ;
		}
		insertQuery += "}";
		
		//if the dataset to be added is copernicus or netcdf, queries by URI
		if(flag.equals("")) {
			String query = "ASK {"+parameter+" ?p ?o}";
			
			//Query Jena Fueski to see if the URI to be added already exists
			boolean results = QueryExecutor.askQuery(query);
			// if the URI does not exists
			if(results == false){
				//Add the dataset to Jena Fueski
				QueryExecutor.insertQuery(insertQuery);
				System.out.print("Successful");
				//log.info("Inserting dataset successfully");
			}else{
				System.out.print(String.format("Error!   URI already exists."));
				//log.error("Error!   URI already exists.");
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
			if(results == false){
				//Add the dataset to Jena Fueski
				QueryExecutor.insertQuery(insertQuery);
				System.out.print("Successful");
				//log.info("Datasets was inserted successfully");
			}else{
				System.out.print(String.format("Error!   URI already exists."));
				//log.error("Error!   URI already exists.");
			}
		}
	}

	private static Dataset convertToObjectDataset(String jsonDataset) throws FileNotFoundException {
		// Parse into JSON the Dataset instance with all metadata from a dataset
		// System.out.println("java "+jsonDataset);
		JsonReader reader = new JsonReader(new FileReader(jsonDataset));
		
		Dataset data = new Gson().fromJson(reader, Dataset.class);
		return data;
		
	}

}

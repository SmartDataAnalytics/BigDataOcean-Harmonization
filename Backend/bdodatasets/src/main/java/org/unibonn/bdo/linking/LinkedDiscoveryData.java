package org.unibonn.bdo.linking;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.unibonn.bdo.bdodatasets.Constants;
import org.unibonn.bdo.objects.Ontology;

/**
 *  
 * @author Jaime M Trillos
 *
 * Receives 1 parameter, the variables extracted from a specific file
 *
 */

public class LinkedDiscoveryData {
	
	private final static Logger log = LoggerFactory.getLogger(LinkedDiscoveryData.class);
	
	public static List<String> parseListNames (List<String> rawNames, String topic) {
		Map<String, String> resultLimes = LimesAnalyser.exec(rawNames, topic);
		List<Ontology> listDataOntology = new ArrayList<>();
		List<String> resultLinked = new ArrayList<>();
		if(resultLimes != null) {
			switch (topic) {
				case "variables":
					listDataOntology = OntologyAnalyser.analyseOntology(Constants.BDO_Ontology_N3, topic);
					resultLinked = formVariablesList(listDataOntology, resultLimes, rawNames);
					break;
				case "keywords":
					resultLinked = formKeywordsList(resultLimes);
					break;
				case "subjects":
					resultLinked = formSubjectsList(resultLimes);
					break;
				case "geoLocation":
					listDataOntology = OntologyAnalyser.analyseOntology(Constants.GEOLOC_Ontology_N3, topic);
					resultLinked = formGeoLocationList(listDataOntology, resultLimes);
					break;
			}
		}
		return resultLinked;
	}
	
	
	// Compare the result from limes with the data from ontologies to obtain the list<String>
	private static List<String> formVariablesList (List<Ontology> listOntology, Map<String,String> resultLimes, List<String> rawVariables){
		List<String> variablesLinked = new ArrayList<String>();
		if(resultLimes.size() > 0) {
			for (String rawVar : rawVariables) {
				if (resultLimes.get(rawVar) != null) {
					for(Ontology tempOnto : listOntology) {
						if(tempOnto.getUri().equals(resultLimes.get(rawVar))) {
							variablesLinked.add(rawVar + " -- " + tempOnto.getLabel());
							break;
						}
					}
					
				} else {
					variablesLinked.add(rawVar + " -- ");
				}
			}
		} else {
			for (String rawVar : rawVariables) {
				variablesLinked.add(rawVar + " -- ");
			}
		}
		
		return variablesLinked;
	}
	
	// Create a list<String> with only url of keywords
	private static List<String> formKeywordsList (Map<String,String> resultLimes){
		List<String> keywordsLinked = new ArrayList<String>();
		String result = "";
		if(resultLimes.size() > 0) {
			for (Map.Entry<String, String> entry : resultLimes.entrySet()){
				if(result.isEmpty()) {
					result = entry.getValue();
				}else {
					result = result + "," + entry.getValue();
				}
			}
			keywordsLinked.add(result);
		}
		return keywordsLinked;
	}
	
	// Create a list<String> with only url of Subjects
	private static List<String> formSubjectsList (Map<String,String> resultLimes){
		List<String> subjectsLinked = new ArrayList<String>();
		String result = "";
		if(resultLimes.size() > 0) {
			for (Map.Entry<String, String> entry : resultLimes.entrySet()){
				if(result.isEmpty()) {
					result = entry.getValue();
				}else {
					result = result + "," + entry.getValue();
				}
			}
			subjectsLinked.add(result);
		}
		return subjectsLinked;
	}
	
	// Compare the result from limes with the data from ontologies to obtain the list<String>
	private static List<String> formGeoLocationList (List<Ontology> listOntology, Map<String,String> resultLimes){
		List<String> geoLocLinked = new ArrayList<String>();
		String result = "";
		if(resultLimes.size() > 0) {
			for(Ontology tempOnto : listOntology) {
				if(resultLimes.get(tempOnto.getLabel().toLowerCase()) != null) {
					result = tempOnto.getUrl();
					break;
				}
			}
			geoLocLinked.add(result);
		}
		return geoLocLinked;
	}

}

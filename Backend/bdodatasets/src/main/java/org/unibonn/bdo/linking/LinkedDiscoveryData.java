package org.unibonn.bdo.linking;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.unibonn.bdo.bdodatasets.Constants;
import org.unibonn.bdo.objects.Ontology;
import org.unibonn.bdo.vocabularies.OntologyAnalyser;

/**
 *  
 * @author Jaime M Trillos
 *
 * Receives 1 parameter, the variables extracted from a specific file
 *
 */

public class LinkedDiscoveryData {
	
	public static List<String> parseListNames (List<String> rawNames, String topic) {
		Map<String, String> resultLimes = LimesAnalyser.exec(rawNames, topic);
		List<Ontology> listDataOntology = new ArrayList<>();
		List<String> resultLinked = new ArrayList<>();
		if(resultLimes != null) {
			switch (topic) {
				case "variables":
					listDataOntology = OntologyAnalyser.analyseOntology(Constants.BDO_ONTOLOGY_N3, topic);
					resultLinked = formVariablesList(listDataOntology, resultLimes, rawNames);
					break;
				case "keywords":
					resultLinked = formKeywordsList(resultLimes);
					break;
				case "subjects":
					resultLinked = formSubjectsList(resultLimes);
					break;
				case "geoLocation":
					listDataOntology = OntologyAnalyser.analyseOntology(Constants.GEOLOC_ONTOLOGY_N3, topic);
					resultLinked = formGeoLocationList(listDataOntology, resultLimes);
					break;
				default:
					break;
			}
		} else {
			if (topic.equals("variables")) {
				resultLinked = formVariablesList(rawNames);
			}
		}
		return resultLinked;
	}
	
	
	// Compare the result from limes with the data from ontologies to obtain the list<String>
	private static List<String> formVariablesList (List<Ontology> listOntology, Map<String,String> resultLimes, List<String> rawVariables){
		List<String> variablesLinked = new ArrayList<>();
		if(resultLimes.size() > 0) {
			for (String rawVar : rawVariables) {
				String name = rawVar.split(" -- ")[0];
				String canonicalName = "";
				if (resultLimes.get(name) != null) {
					if(resultLimes.get(name).contains(",")) {
						String[] token = resultLimes.get(name).split(",");
						for (String t : token) {
							if(canonicalName.equals("")) {
								canonicalName = findCanonicalName(listOntology, t, true);
							} else {
								canonicalName = canonicalName + "**" + findCanonicalName(listOntology, t, true);
							}
						}
						variablesLinked.add(rawVar + " -- " + canonicalName);
					} else {
						variablesLinked.add(rawVar + " -- " + findCanonicalName(listOntology, resultLimes.get(name), false));
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
	
	private static String findCanonicalName(List<Ontology> listOntology, String valueMap, boolean flag) {
		for(Ontology tempOnto : listOntology) {
			if(tempOnto.getUri().equals(valueMap)) {
				if(flag) {
					return tempOnto.getLabel() + ":" + tempOnto.getCanonicalName();
				}else {
					return tempOnto.getCanonicalName();
				}
			}
		}
		return "";
	}
	
	// if there is no match variables in Limes return only the raw variables
	private static List<String> formVariablesList (List<String> rawVariables){
		List<String> variablesLinked = new ArrayList<>();
		for (String rawVar : rawVariables) {
			variablesLinked.add(rawVar + " -- ");
		}
		
		return variablesLinked;
	}
	
	// Create a list<String> with only url of keywords
	private static List<String> formKeywordsList (Map<String,String> resultLimes){
		List<String> keywordsLinked = new ArrayList<>();
		String result = "";
		if(resultLimes.size() > 0) {
			for (Map.Entry<String, String> entry : resultLimes.entrySet()){
				if(result.isEmpty()) {
					result = entry.getValue().replace("http://www.eionet.europa.eu/concept/", "https://www.eionet.europa.eu/gemet/en/concept/");
				}else {
					result = result + "," + entry.getValue().replace("http://www.eionet.europa.eu/concept/", "https://www.eionet.europa.eu/gemet/en/concept/");
				}
			}
			keywordsLinked.add(result);
		}
		return keywordsLinked;
	}
	
	// Create a list<String> with only url of Subjects
	private static List<String> formSubjectsList (Map<String,String> resultLimes){
		List<String> subjectsLinked = new ArrayList<>();
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
		List<String> geoLocLinked = new ArrayList<>();
		String result = "";
		if(resultLimes.size() > 0) {
			for(Ontology tempOnto : listOntology) {
				if(resultLimes.get(tempOnto.getLabel().toLowerCase()) != null) {
					if(result.isEmpty()) {
						result = tempOnto.getUrl();
					}else {
						result = result + "," + tempOnto.getUrl();
					}
				}
			}
			geoLocLinked.add(result);
		}
		return geoLocLinked;
	}

}

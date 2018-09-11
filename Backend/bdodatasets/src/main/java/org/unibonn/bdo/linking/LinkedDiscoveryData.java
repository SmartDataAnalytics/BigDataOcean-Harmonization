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
	
	public static List<String> parseListVariables (List<String> variables, String topic) {
		Map<String, String> resultLimes = LimesAnalyser.exec(variables, topic);
		List<Ontology> listDataOntology = new ArrayList<>();
		switch (topic) {
			case "variables":
				listDataOntology = OntologyAnalyser.analyseOntology(Constants.BDO_Ontology_N3, topic);
			break;
			case "keywords":
				listDataOntology = OntologyAnalyser.analyseOntology(Constants.EIONET_Ontology_N3, topic);
			break;
			case "subjects":
				listDataOntology = OntologyAnalyser.analyseOntology(Constants.INSPIRE_Ontology_N3, topic);
			break;
			case "geoLocation":
				listDataOntology = OntologyAnalyser.analyseOntology(Constants.GEOLOC_Ontology_N3, topic);
			break;
		}
		List<String> variablesLinked = formVariablesList(listDataOntology, resultLimes, variables);
		return variablesLinked;
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
	
	// Compare the result from limes with the data from ontologies to obtain the list<String>
	private static List<String> formKeywordsList (List<Ontology> listOntology, Map<String,String> resultLimes, List<String> rawVariables){
		// TODO 
		return null;
	}
	
	// Compare the result from limes with the data from ontologies to obtain the list<String>
	private static List<String> formSubjectsList (List<Ontology> listOntology, Map<String,String> resultLimes, List<String> rawVariables){
		// TODO 
		return null;
	}
	
	// Compare the result from limes with the data from ontologies to obtain the list<String>
	private static List<String> formGeoLocationList (List<Ontology> listOntology, Map<String,String> resultLimes, List<String> rawVariables){
		// TODO 
		return null;
	}

}

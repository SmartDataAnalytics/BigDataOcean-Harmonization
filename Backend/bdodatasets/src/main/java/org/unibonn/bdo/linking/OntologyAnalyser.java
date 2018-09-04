package org.unibonn.bdo.linking;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.jena.riot.RDFDataMgr;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import org.unibonn.bdo.objects.*;

public class OntologyAnalyser {
	
	public static void analyseOntology(String uriModel, String topic) {
		//Model model = RDFDataMgr.loadModel("/home/eis/Dropbox/BDO/BigDataOcean-Harmonization/Backend/AddDatasets/ontologiesN3/bdo.n3") ;
		Model model = RDFDataMgr.loadModel(uriModel) ;
		model = ModelFactory.createRDFSModel(model);
		List<Ontology> listDataOntology = new ArrayList<Ontology>();
		SPARQLRunner sparqlRunner = new SPARQLRunner(model);
		
		switch (topic) {
			case "variables":
				listDataOntology = extractDataVariables(sparqlRunner);
			break;
			case "keywords":
				listDataOntology = extractDataSubjects(sparqlRunner);
			break;
			case "subjects":
				listDataOntology = extractDataKeywords(sparqlRunner);
			break;
			case "geoLocation":
				listDataOntology = extractDataGeoLocation(sparqlRunner);
			break;
		}
	}
	
	// Extract the uri, label, canonicalName, url from the ontology
	public static List<Ontology> extractDataVariables (SPARQLRunner sparqlRunner){
		List<Ontology> listDataOntology = sparqlRunner.getListDataCanonicalModel("extractLabelUlrURICanonicalModel.sparql");
		
		return listDataOntology;
	}
	
	// Extract the uri, label from the ontology
	public static List<Ontology> extractDataSubjects (SPARQLRunner sparqlRunner){
		List<Ontology> listDataOntology = sparqlRunner.getListDataSubjects("extractLabelURISubjects.sparql");
		
		return listDataOntology;
	}
	
	// Extract the uri, label from the ontology
	public static List<Ontology> extractDataKeywords (SPARQLRunner sparqlRunner){
		List<Ontology> listDataOntology = sparqlRunner.getListDataKeywords("extractLabelURIKeywords.sparql");
		
		return listDataOntology;
	}

	// Extract the uri, label, url from the ontology
	public static List<Ontology> extractDataGeoLocation (SPARQLRunner sparqlRunner){
		List<Ontology> listDataOntology = sparqlRunner.getListDataGeoLocation("extractLabelUlrURIGeoLoc.sparql");
		
		return listDataOntology;
	}
		
	
	// Compare the result from limes with the data from ontologies to obtain the 
	public static List<String> formVariablesList (List<Ontology> listOntology, List<String> resultLimes){
		
		return null;
	}

	public static Map<String, String> stringintoMap(String resultLimes) {
		Map<String, String> result = new HashMap<>();
		String[] tokenNewLine = resultLimes.split("\n");
		for (String token1 : tokenNewLine) { 
			String[] tokenTab = token1.split("\t");
			result.put(tokenTab[0], tokenTab[1]);
		}
		return result;
	}
}

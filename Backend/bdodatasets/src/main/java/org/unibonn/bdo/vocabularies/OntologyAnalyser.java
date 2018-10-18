package org.unibonn.bdo.vocabularies;

import java.util.ArrayList;
import java.util.List;

import org.apache.jena.riot.RDFDataMgr;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

import org.unibonn.bdo.objects.*;

public class OntologyAnalyser {
	
	private OntologyAnalyser() {
		throw new IllegalStateException("OntologyAnalyser class");
	}
	
	public static List<Ontology> analyseOntology(String uriModel, String topic) {
		Model model = RDFDataMgr.loadModel(uriModel) ;
		model = ModelFactory.createRDFSModel(model);
		List<Ontology> listDataOntology = new ArrayList<>();
		SPARQLRunner sparqlRunner = new SPARQLRunner(model);
		
		switch (topic) {
			case "variables":
				listDataOntology = extractDataVariables(sparqlRunner);
				break;
			case "keywords":
				listDataOntology = extractDataKeywords(sparqlRunner);
				break;
			case "subjects":
				listDataOntology = extractDataSubjects(sparqlRunner);
				break;
			case "geoLocation":
				listDataOntology = extractDataGeoLocation(sparqlRunner);
				break;
			default:
				break;
		}
		return listDataOntology;
	}
	
	// Extract the uri, label, canonicalName, url from the ontology
	private static List<Ontology> extractDataVariables (SPARQLRunner sparqlRunner){
		return sparqlRunner.getListDataCanonicalModel("extractLabelUlrURICanonicalModel.sparql");
	}
	
	// Extract the uri, label from the ontology
	private static List<Ontology> extractDataSubjects (SPARQLRunner sparqlRunner){
		return sparqlRunner.getListDataKeywordsSubjects("extractLabelURISubjects.sparql");
	}
	
	// Extract the uri, label from the ontology
	private static List<Ontology> extractDataKeywords (SPARQLRunner sparqlRunner){
		return sparqlRunner.getListDataKeywordsSubjects("extractLabelURIKeywords.sparql");
	}

	// Extract the uri, label, url from the ontology
	private static List<Ontology> extractDataGeoLocation (SPARQLRunner sparqlRunner){
		return sparqlRunner.getListDataGeoLocation("extractLabelUrlURIGeoLoc.sparql");
	}
		
}

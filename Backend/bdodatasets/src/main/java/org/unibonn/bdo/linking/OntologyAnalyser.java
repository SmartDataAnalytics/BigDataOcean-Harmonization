package org.unibonn.bdo.linking;

import java.util.ArrayList;
import java.util.List;

import org.apache.jena.riot.RDFDataMgr;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import org.unibonn.bdo.objects.*;

public class OntologyAnalyser {
	
	public static List<Ontology> analyseOntology(String uriModel, String topic) {
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
				listDataOntology = extractDataKeywords(sparqlRunner);
				break;
			case "subjects":
				listDataOntology = extractDataSubjects(sparqlRunner);
				break;
			case "geoLocation":
				listDataOntology = extractDataGeoLocation(sparqlRunner);
				break;
		}
		return listDataOntology;
	}
	
	// Extract the uri, label, canonicalName, url from the ontology
	private static List<Ontology> extractDataVariables (SPARQLRunner sparqlRunner){
		List<Ontology> listDataOntology = sparqlRunner.getListDataCanonicalModel("extractLabelUlrURICanonicalModel.sparql");
		
		return listDataOntology;
	}
	
	// Extract the uri, label from the ontology
	private static List<Ontology> extractDataSubjects (SPARQLRunner sparqlRunner){
		List<Ontology> listDataOntology = sparqlRunner.getListDataSubjects("extractLabelURISubjects.sparql");
		
		return listDataOntology;
	}
	
	// Extract the uri, label from the ontology
	private static List<Ontology> extractDataKeywords (SPARQLRunner sparqlRunner){
		List<Ontology> listDataOntology = sparqlRunner.getListDataKeywords("extractLabelURIKeywords.sparql");
		
		return listDataOntology;
	}

	// Extract the uri, label, url from the ontology
	private static List<Ontology> extractDataGeoLocation (SPARQLRunner sparqlRunner){
		List<Ontology> listDataOntology = sparqlRunner.getListDataGeoLocation("extractLabelUrlURIGeoLoc.sparql");
		
		return listDataOntology;
	}
		
}

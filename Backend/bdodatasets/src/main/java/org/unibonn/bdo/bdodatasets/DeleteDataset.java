package org.unibonn.bdo.bdodatasets;

import org.unibonn.bdo.connections.QueryExecutor;

public class DeleteDataset {
	
	public static void main(String[] args) {
		String identifier = args[0];
		String uri = "bdo:"+identifier;
		exec(uri);

	}

	public static void exec(String uri) {
		String query = "PREFIX bdo: <http://bigdataocean.eu/bdo/>\n" + 
				"PREFIX dcat: <https://www.w3.org/TR/vocab-dcat/> \n" + 
				"PREFIX disco: <http://rdf-vocabulary.ddialliance.org/discovery#>\n" + 
				"PREFIX ignf: <http://data.ign.fr/def/ignf#>\nPREFIX bdo: <http://bigdataocean.eu/bdo/>\n" + 
				"PREFIX dcat: <http://www.w3.org/ns/dcat#>\n" + 
				"PREFIX disco: <http://rdf-vocabulary.ddialliance.org/discovery#>\n" + 
				"PREFIX ignf: <http://data.ign.fr/def/ignf#>\n" + 
				"\n" + 
				"DELETE WHERE{\n" + 
				"  "+uri+" disco:variable ?variables .\n" + 
				"  "+uri+" bdo:timeCoverage ?tc .\n" + 
				"  "+uri+" bdo:verticalCoverage ?vc .\n" + 
				"  "+uri+" bdo:GeographicalCoverage ?gc .\n" + 
				"  \n" + 
				"  ?variables a bdo:BDOVariable ;\n" + 
				"    ?p ?o .\n" + 
				"  \n" + 
				"  ?tc a bdo:TimeCoverage ;\n" + 
				"    ?a ?b .\n" + 
				"  \n" + 
				"  ?vc a bdo:VerticalCoverage ;\n" + 
				"    ?c ?d .\n" + 
				"  \n" + 
				"  ?gc a ignf:GeographicBoundingBox ;\n" + 
				"    ?e ?f .\n" + 
				"  \n" + 
				"  "+uri+" ?m ?n .\n" + 
				"}";
		
		
		QueryExecutor.deleteQuery(query);
		System.out.print("Successful");
	}

}

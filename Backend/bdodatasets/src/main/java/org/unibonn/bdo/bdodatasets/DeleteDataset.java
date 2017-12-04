package org.unibonn.bdo.bdodatasets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.RDFNode;

public class DeleteDataset {
	
	private final static Logger log = LoggerFactory.getLogger(DeleteDataset.class);
	
	public static void main(String[] args) {
		//String identifier = args[0];
		//String uri = "<http://bigdataocean.eu/bdo/MEDSEA_ANALYSIS_FORECAST_WAV_006_011>";
		String uri = "bdo:MEDSEA_ANALYSIS_FORECAST_WAV_006_011";
		exec(uri);

	}

	public static void exec(String Uri) {
		String queryVariables = "PREFIX bdo: <http://bigdataocean.eu/bdo/>\n" + 
				"PREFIX dcat: <https://www.w3.org/TR/vocab-dcat/> \n" + 
				"PREFIX disco: <http://rdf-vocabulary.ddialliance.org/discovery#>\n" + 
				"PREFIX ignf: <http://data.ign.fr/def/ignf#>\n" + 
				"SELECT ?variables {\n" +
				"	"+Uri+" disco:variable ?variables .\n" +
				"}";
		
		String query = "PREFIX bdo: <http://bigdataocean.eu/bdo/>\n" + 
				"PREFIX dcat: <https://www.w3.org/TR/vocab-dcat/> \n" + 
				"PREFIX disco: <http://rdf-vocabulary.ddialliance.org/discovery#>\n" + 
				"PREFIX ignf: <http://data.ign.fr/def/ignf#>\n" + 
				"DELETE WHERE {\n" + 
				"  "+Uri+"_VC a bdo:VerticalCoverage;\n" + 
				"    ?f ?g.\n" + 
				"  "+Uri+"_GC a ignf:GeographicBoundingBox;\n" + 
				"    ?h ?i.\n" + 
				"  "+Uri+"_TC a bdo:TimeCoverage;\n" + 
				"    ?j ?k.\n" ;
				/*"  ?d a bdo:BDOVariable;\n" + 
				"    ?l ?m.\n" + 
				"  "+Uri+" a dcat:Dataset;\n" + 
				"    ?n ?o.\n" + 
				"  \n" + 
				"}";*/
		
		ResultSet results = QueryExecutor.selectQuery(queryVariables);
		RDFNode node;
		
		while(results.hasNext()) {
			QuerySolution solution = results.nextSolution();
			node = solution.get("variables");
			query += "  <"+node.toString()+"> a bdo:BDOVariable;\n" +
					"    ?l ?m.\n";
			
		}
		query += "  "+Uri+" a dcat:Dataset;\n" + 
				"    ?n ?o.\n" + 
				"  \n" + 
				"}";
		
		System.out.println(query);
		
		QueryExecutor.deleteQuery(query);
		System.out.print("Successful");
		//log.info("Deleting variable successfully: " + query);
	}

}

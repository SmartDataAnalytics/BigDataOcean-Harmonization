package org.unibonn.bdo.bdodatasets;

import java.util.ArrayList;
import java.util.List;

import org.unibonn.bdo.connections.QueryExecutor;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;

/**
 *  
 * @author Jaime M Trillos
 * @author Ana C Trillos
 *
 * Receives 2 parameters, the URI or storage table to delete  and flag (True is uri, False is storagetable)
 *
 */

public class DeleteDataset {
	
	public static void main(String[] args) {
		String identifier = args[0];
		boolean flag = Boolean.parseBoolean(args[1]);
		exec(identifier, flag);
	}

	public static void exec(String uri, boolean flag) {
		String query = "";
		if(flag) {
			uri = "bdo:" + uri;
			query = "PREFIX bdo: <http://bigdataocean.eu/bdo/>\n" + 
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
		} else {
			List<String> listUri = searchUrisSameStorageTable(uri);
			for (String id : listUri) {
				query = "PREFIX bdo: <http://bigdataocean.eu/bdo/>\n" + 
						"PREFIX dcat: <https://www.w3.org/TR/vocab-dcat/> \n" + 
						"PREFIX disco: <http://rdf-vocabulary.ddialliance.org/discovery#>\n" + 
						"PREFIX ignf: <http://data.ign.fr/def/ignf#>\nPREFIX bdo: <http://bigdataocean.eu/bdo/>\n" + 
						"PREFIX dcat: <http://www.w3.org/ns/dcat#>\n" + 
						"PREFIX disco: <http://rdf-vocabulary.ddialliance.org/discovery#>\n" + 
						"PREFIX ignf: <http://data.ign.fr/def/ignf#>\n" + 
						"\n" + 
						"DELETE WHERE{\n" + 
						"  "+id+" disco:variable ?variables .\n" + 
						"  "+id+" bdo:timeCoverage ?tc .\n" + 
						"  "+id+" bdo:verticalCoverage ?vc .\n" + 
						"  "+id+" bdo:GeographicalCoverage ?gc .\n" + 
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
						"  "+id+" ?m ?n .\n" + 
						"}";

				QueryExecutor.deleteQuery(query);
				System.out.print("Successful");
			}
		}
	}
	
	private static List<String> searchUrisSameStorageTable(String storage){
		List<String> listUri = new ArrayList<>();
		String queryMetadata = "PREFIX dct: <http://purl.org/dc/terms/>\n" + 
				"PREFIX dcat: <https://www.w3.org/TR/vocab-dcat/>\n" + 
				"PREFIX bdo: <http://bigdataocean.eu/bdo/>\n" + 
				"SELECT ?ident \n" + 
				"WHERE{ \n" + 
				"  ?uri a dcat:Dataset ;\n" + 
				"       dct:identifier ?ident ;\n" + 
				"       bdo:storageTable '" + storage + "' .\n" + 
				"}";
		ResultSet results = QueryExecutor.selectQuery(queryMetadata);
		
		while(results.hasNext()){
			QuerySolution solution = results.nextSolution();
			listUri.add("bdo:" + solution.get("ident").toString());
		}
		return listUri;
	}

}

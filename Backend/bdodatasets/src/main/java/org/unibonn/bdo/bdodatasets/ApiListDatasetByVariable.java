package org.unibonn.bdo.bdodatasets;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.unibonn.bdo.objects.Dataset;

import com.google.gson.Gson;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.RDFNode;

/**
 *  
 * @author Jaime M Trillos
 * @author Ana C Trillos
 *
 * Receives 1 parameter, the URI of the Dataset to get all metadata from Jena Fuseki
 *
 */

public class ApiListDatasetByVariable {
	
	private final static Logger log = LoggerFactory.getLogger(GetMetadata.class);


	public static void main(String[] args) {
		String variables = args[0];
		//String variables = "sea_surface_wave_significant_height,latitude";
		exec(variables);

	}

	public static void exec(String variables) {
		String[] listV = variables.split(", ");
			
		String values = "  VALUES ?var { ";
		for(String var : listV) {
			values += "\""+var+"\"@en ";
		}
		values += "}\n";
		
		String apiQuery = "PREFIX disco: <http://rdf-vocabulary.ddialliance.org/discovery#>\n" + 
				"PREFIX skos: <http://www.w3.org/2004/02/skos/core#>\n" + 
				"PREFIX dct: <http://purl.org/dc/terms/>\n" + 
				"PREFIX dcat: <https://www.w3.org/TR/vocab-dcat/>\n" + 
				"PREFIX bdo: <http://bigdataocean.eu/bdo/>\n" + 
				"\n" + 
				"\n" + 
				"SELECT distinct ?uri ?title ?subject ?language (STR(?var) AS ?variables)\n" + 
				"WHERE {\n" + 
				"  ?uriVar a bdo:BDOVariable;\n" + 
				"      skos:prefLabel ?var.\n" + 
				values + 
				"  ?uri disco:variable ?uriVar;\n" + 
				"       dct:title ?title;\n" + 
				"       dcat:subject ?subject;\n" + 
				"       dct:language ?language.\n" + 
				"}";
		
		List<Dataset> list = new ArrayList<>();
		RDFNode node;
		// executes query on Jena Fueski to get Metadata
		ResultSet results = QueryExecutor.selectQuery(apiQuery);
		String id = null;
		int i = 0;		
		while(results.hasNext()){
			Dataset dataset = new Dataset();
			QuerySolution solution = results.nextSolution();				
			node = solution.get("uri");
			if(id != node.toString()) {
				List<String> listVar = new ArrayList<>();
				dataset.setIdentifier(node.toString());
				id = node.toString();
				node = solution.get("title");
				dataset.setTitle(node.toString());
				node = solution.get("subject");
				if(dataset.getSubject() != null)
				{
					dataset.setSubject(dataset.getSubject()+", "+node.toString());
				}else {
					dataset.setSubject(node.toString());
				}
				node = solution.get("language");
				if(dataset.getLanguage() != null)
				{
					dataset.setLanguage(dataset.getLanguage()+", "+node.toString());
				}else {
					dataset.setLanguage(node.toString());
				}
				node = solution.get("variables");
				listVar.add(node.toString());
				dataset.setVariable(listVar);
				list.add(dataset);	
				i++;
			}else {
				dataset = list.get(i-1);
				List<String> listVar = dataset.getVariable();
				node = solution.get("variables");
				listVar.add(node.toString());
			}
		}
		try {
			// Parse into JSON the Dataset instance with all metadata from a dataset
			Gson gson  = new Gson();
			System.out.print(gson.toJson(list));
			//log.info("Dataset's metadata: " + gson.toJson(dataset));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}	
}

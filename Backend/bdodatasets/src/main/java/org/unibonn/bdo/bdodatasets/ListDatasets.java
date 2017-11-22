package org.unibonn.bdo.bdodatasets;

import java.util.ArrayList;
import java.util.List;

import org.unibonn.bdo.objects.DatasetList;

import com.google.gson.Gson;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.RDFNode;

public class ListDatasets {
	
	public static void main(String[] args) {
		exec();
	}
	
	public static void exec() {
		String query = "PREFIX bdo: <http://bigdataocean.eu/bdo/>\n" + 
				"PREFIX ids: <http://industrialdataspace/information-model/>\n" + 
				"PREFIX dct: <http://purl.org/dc/terms/>\n" + 
				"SELECT DISTINCT ?uri ?identifier ?title ?description\n" + 
				"WHERE {\n" + 
				"  ?uri dct:identifier ?identifier;\n" + 
				"       dct:title ?title;\n" + 
				"       dct:description ?description.\n" + 
				"}";
		
		List<DatasetList> listDatasets = new ArrayList<>() ;
		RDFNode node;

		QueryExecution qe = QueryExecutionFactory.sparqlService(
					"http://localhost:3030/bdoHarmonization/query",query);
		ResultSet results = qe.execSelect();
		
		while (results.hasNext()) {
			DatasetList list = new DatasetList();
			QuerySolution solution = results.nextSolution();
			String ident;
			node = solution.get("identifier");
			ident = node.toString();
			node = solution.get("title");
			list.setTitle("<a href=/metadataInfo/"+ident+">"+node.toString()+"</a>");
			node = solution.get("description");
			list.setDescription(node.toString());
			listDatasets.add(list);
		}
		
		try {
			Gson gson  = new Gson();
			System.out.println(gson.toJson(listDatasets));
		} catch (Exception e) {
			e.printStackTrace();
		}
		qe.close();
	}

}

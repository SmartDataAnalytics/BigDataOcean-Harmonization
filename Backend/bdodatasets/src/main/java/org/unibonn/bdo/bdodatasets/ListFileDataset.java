package org.unibonn.bdo.bdodatasets;

import java.util.ArrayList;
import java.util.List;

import org.unibonn.bdo.connections.QueryExecutor;
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
 *  Receives 1 parameter, the storage table name of the Dataset to get all metadata from Jena Fuseki
 *
 */

public class ListFileDataset {
	
	public static void main(String[] args) {
		String storage = args[0];
		exec(storage);
	}
	
	public static void exec(String storage) {
		String query = "";
		if(!storage.equalsIgnoreCase("all_files")) {
			query = "PREFIX bdo: <http://bigdataocean.eu/bdo/>\n" + 
					"PREFIX ids: <http://industrialdataspace/information-model/>\n" + 
					"PREFIX dct: <http://purl.org/dc/terms/>\n" + 
					"SELECT DISTINCT ?uri ?identifier ?title ?description ?format\n" + 
					"WHERE {\n" + 
					"  ?uri dct:identifier ?identifier;\n" + 
					"       dct:title ?title;\n" + 
					"       bdo:storageTable '" + storage + "';\n" + 
					"       dct:format ?format;\n" + 
					"       dct:description ?description.\n" + 
					"}";
		} else {
			query = "PREFIX bdo: <http://bigdataocean.eu/bdo/>\n" + 
					"PREFIX ids: <http://industrialdataspace/information-model/>\n" + 
					"PREFIX dct: <http://purl.org/dc/terms/>\n" + 
					"SELECT DISTINCT ?uri ?identifier ?title ?description ?format ?storage\n" + 
					"WHERE {\n" + 
					"  ?uri dct:identifier ?identifier;\n" + 
					"       dct:title ?title;\n" + 
					"       bdo:storageTable ?storage;\n" + 
					"       dct:format ?format;\n" + 
					"       dct:description ?description.\n" + 
					"}";
		}
		
		List<Dataset> listDatasets = new ArrayList<>() ;
		RDFNode node;
		// executes query on Jena Fueski to get identifier, title and description of all datasets
		ResultSet results = QueryExecutor.selectQuery(query);
		
		while (results.hasNext()) {
			Dataset list = new Dataset();
			QuerySolution solution = results.nextSolution();
			String ident;
			node = solution.get("identifier");
			ident = node.toString();
			list.setIdentifier(ident);
			node = solution.get("title");
			list.setTitle("<a href=/metadataInfo/"+ident+">"+node.toString()+"</a>");
			node = solution.get("description");
			if(!storage.equalsIgnoreCase("all_files")) {
				list.setStorageTable(storage);
			}else {
				list.setStorageTable(solution.get("storage").toString());
			}
			list.setFormats(solution.get("format").toString());
			// substring of only 300 characters of the description to avoid big table
			if (node.toString().length()>=300) {
				list.setDescription(node.toString().substring(0, 300)+"...");
			}else {
				list.setDescription(node.toString());
			}
			listDatasets.add(list);
		}
		
		try {
			// Parse into JSON the list of datasets
			Gson gson  = new Gson();
			System.out.println(gson.toJson(listDatasets));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

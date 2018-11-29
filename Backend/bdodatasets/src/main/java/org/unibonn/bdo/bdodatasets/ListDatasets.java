package org.unibonn.bdo.bdodatasets;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
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
 * Does not receive any parameter, but query Jena Fuseki to get a list of all datasets
 *
 */

public class ListDatasets {
	
	public static void main(String[] args) {
		exec();
	}
	
	public static void exec() {
		String pathFile = Constants.CONFIGFILEPATH+"/Frontend/Flask/static/json/storageTable.json";
		JSONParser parser = new JSONParser();
		List<Dataset> listDatasets = new ArrayList<>();
		try {
			File file = new File(pathFile);
			if(file.exists() && file.length() > 0) {
				JSONArray storageTable = (JSONArray) parser.parse(new FileReader(pathFile));
				String storage = "";
				for(int j=0; j<storageTable.size(); j++){
					JSONObject tokenJson = (JSONObject) storageTable.get(j);
					storage = tokenJson.get("tableName").toString();
					String query = "PREFIX bdo: <http://bigdataocean.eu/bdo/>\n" + 
							"PREFIX dcat: <https://www.w3.org/TR/vocab-dcat/>\n" + 
							"PREFIX ids: <http://industrialdataspace/information-model/>\n" + 
							"PREFIX dct: <http://purl.org/dc/terms/>\n" + 
							"SELECT ?title ?description ?format\n" + 
							"WHERE {\n" + 
							"  ?uri a dcat:Dataset ;\n" + 
							"       dct:title ?title ;\n" +
							"       bdo:storageTable '" + storage + "';\n" + 
							"       dct:format ?format;\n" + 
							"       dct:description ?description.\n" + 
							"}" +
							"LIMIT 1";
					
					RDFNode node;
					// executes query on Jena Fueski to get identifier, title and description of all datasets
					ResultSet results = QueryExecutor.selectQuery(query);
					Dataset dataset = new Dataset();
					while (results.hasNext()) {
						QuerySolution solution = results.nextSolution();
						node = solution.get("title");
						dataset.setTitle("<a href=/metadataDatasetInfo/"+storage+">"+node.toString()+"</a>");
						node = solution.get("description");
						dataset.setStorageTable(storage);
						dataset.setFormats(solution.get("format").toString());
						// substring of only 300 characters of the description to avoid big table
						if (node.toString().length()>=300) {
							dataset.setDescription(node.toString().substring(0, 300)+"...");
						}else {
							dataset.setDescription(node.toString());
						}
					}
					if (dataset.getTitle() != "") {
						listDatasets.add(dataset);
					}
				}
			}
			// Parse into JSON the list of datasets
			Gson gson  = new Gson();
			System.out.println(gson.toJson(listDatasets));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

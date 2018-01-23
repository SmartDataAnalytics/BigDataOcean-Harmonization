package org.unibonn.bdo.bdodatasets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.naming.directory.SearchControls;

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
 * 
 *
 */

public class BdoApiAnalyser {
	//Case 1: List all datasets
	public static List<Dataset> apiListAllDatasets () throws IOException {
		List<Dataset> list = new ArrayList<>();
		String apiQuery = "PREFIX disco: <http://rdf-vocabulary.ddialliance.org/discovery#>\n" + 
				"PREFIX skos: <http://www.w3.org/2004/02/skos/core#>\n" + 
				"PREFIX dct: <http://purl.org/dc/terms/>\n" + 
				"PREFIX dcat: <https://www.w3.org/TR/vocab-dcat/>\n" + 
				"PREFIX bdo: <http://bigdataocean.eu/bdo/>\n" + 
				"PREFIX dbo: <http://dbpedia.org/ontology/>\n" + 
				"\n" + 
				"\n" + 
				"SELECT ?uri ?title ?subject ?keywords ?language (STR (?label) as ?variables)\n" + 
				"WHERE {\n" + 
				"  ?uri a dcat:Dataset;\n" + 
				"       dct:title ?title;\n" + 
				"       dcat:subject ?subject;\n" + 
				"       dcat:theme ?keywords;\n" + 
				"       dct:language ?language;\n" + 
				"       disco:variable ?variable.\n" + 
				"  ?variable a bdo:BDOVariable;\n" + 
				"      skos:prefLabel ?label.\n" + 
				"}";
		RDFNode node;
		ResultSet results = QueryExecutor.selectQuery(apiQuery);
		String id = null;
		int i = 0;	
		while(results.hasNext()) {
			
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
				node = solution.get("keywords");
				if(dataset.getKeywords() != null)
				{
					dataset.setKeywords(dataset.getKeywords()+", "+node.toString());
				}else {
					dataset.setKeywords(node.toString());
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
		return list;
	}
	
	public static Dataset apiListVarOfDataset (String searchParam) throws IOException {
		Dataset dataset = new Dataset();
		List<String> listVar = new ArrayList<>();
		String apiQuery = "PREFIX disco: <http://rdf-vocabulary.ddialliance.org/discovery#>\n" + 
				"PREFIX skos: <http://www.w3.org/2004/02/skos/core#>\n" + 
				"PREFIX dct: <http://purl.org/dc/terms/>\n" + 
				"PREFIX dcat: <https://www.w3.org/TR/vocab-dcat/>\n" + 
				"PREFIX bdo: <http://bigdataocean.eu/bdo/>\n" + 
				"\n" + 
				"\n" + 
				"SELECT distinct ?uri ?title (STR(?label) AS ?variables)\n" + 
				"WHERE {  \n" + 
				"  bdo:"+searchParam+" disco:variable ?variable;\n" + 
				"       dct:title ?title.\n" + 
				"  ?variable a bdo:BDOVariable;\n" + 
				"      skos:prefLabel ?label.\n" + 
				"}";
		RDFNode node;
		ResultSet results = QueryExecutor.selectQuery(apiQuery);
		while(results.hasNext()) {					
			dataset.setIdentifier("http://bigdataocean.eu/bdo/"+searchParam);
			QuerySolution solution = results.nextSolution();				
			node = solution.get("title");
			dataset.setTitle(node.toString());
			node = solution.get("variables");
			listVar.add(node.toString());
			dataset.setVariable(listVar);	
		}
		return dataset;
	}
	
	public static List<Dataset> apiListDatasetsByVar (String searchParam) throws IOException {
		List<Dataset> list = new ArrayList<>();
		String[] listV = searchParam.split(", ");
		
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
		
		RDFNode node;
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
		return list;
	}

}

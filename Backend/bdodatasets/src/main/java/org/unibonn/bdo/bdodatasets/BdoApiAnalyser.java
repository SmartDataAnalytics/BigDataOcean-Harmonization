package org.unibonn.bdo.bdodatasets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.naming.directory.SearchControls;
import javax.xml.crypto.Data;

import org.unibonn.bdo.objects.Dataset;

import com.google.gson.Gson;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
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
	
	public static Dataset apiSearchDataset (String searchParam) throws IOException {
		String queryMetadata = "PREFIX dct: <http://purl.org/dc/terms/>\n" + 
				"PREFIX dcat: <https://www.w3.org/TR/vocab-dcat/>\n" + 
				"PREFIX ignf: <http://data.ign.fr/def/ignf#>\n" + 
				"PREFIX foaf: <http://xmlns.com/foaf/0.1/>\n" + 
				"PREFIX bdo: <http://bigdataocean.eu/bdo/>\n" + 
				"PREFIX ids: <http://industrialdataspace/information-model/>\n" + 
				"SELECT ?uri ?ident ?title ?desc ?sub ?keyw ?standard ?format ?lang ?homep ?publi ?rights (STR(?issued) AS ?issuedDate)  (STR(?modified) AS ?modifiedDate) ?geoLoc ?timeReso (STR(?verFrom) AS ?vFrom) (STR(?verTo) AS ?vTo) (STR(?west) AS ?spatialWest) (STR(?east) AS ?spatialEast) (STR(?south) AS ?spatialSouth) (STR(?north) AS ?spatialNorth) (STR(?tempCovB) AS ?timeCovBeg) (STR(?tempCovE) AS ?timeCovEnd) ?vLevel ?coorSys\n" + 
				"WHERE{ \n" + 
				"  "+searchParam+" a dcat:Dataset ;\n" + 
				"       dct:identifier ?ident ;\n" + 
				"       dct:title ?title ;\n" + 
				"       dct:description ?desc ;\n" + 
				"       dcat:subject ?sub ;\n" + 
				"       bdo:verticalCoverage ?vCov ;\n" + 
				"       dcat:theme ?keyw ;\n" + 
				"       dct:Standard ?standard ;\n" + 
				"       dct:format ?format ;\n" + 
				"       dct:language ?lang ;\n" + 
				"       foaf:homepage ?homep ;\n" + 
				"       dct:publisher ?publi ;\n" + 
				"       dct:accessRights ?rights ;\n" + 
				"       dct:issued ?issued ;\n" + 
				"       dct:modified ?modified ;\n" + 
				"       bdo:timeResolution ?timeReso ;\n" + 
				"       bdo:GeographicalCoverage ?spatial ;\n" + 
				"       bdo:verticalLevel ?vLevel ;\n" + 
				"       dct:conformsTo ?coorSys ;\n" + 
				"       bdo:timeCoverage ?temp .\n" + 
				"  \n" + 
				"  ?temp a bdo:TimeCoverage;\n" + 
				"		 ids:beginning ?tempCovB ;\n" + 
				"        ids:end ?tempCovE .\n" + 
				"  \n" + 
				"  ?spatial a ignf:GeographicBoundingBox ;\n" + 
				"           ignf:westBoundLongitude ?west ;\n" + 
				"           ignf:eastBoundLongitude ?east ;\n" + 
				"           ignf:southBoundLatitude ?south ;\n" + 
				"           ignf:northBoundLatitude ?north .\n" + 
				"  \n" + 
				"  ?vCov a  bdo:VerticalCoverage ;\n" + 
				"        bdo:verticalFrom ?verFrom ;\n" + 
				"        bdo:verticalTo ?verTo .\n" + 
				" OPTIONAL {"+searchParam+" dct:spatial ?geoloc .}\n" +
				"}";
		
		Dataset dataset = new Dataset();
		RDFNode node;
		// executes query on Jena Fueski to get Metadata
		ResultSet results = QueryExecutor.selectQuery(queryMetadata);
		
		while(results.hasNext()){
			QuerySolution solution = results.nextSolution();
			node = solution.get("ident");
			dataset.setIdentifier(node.toString());
			node = solution.get("title");
			dataset.setTitle(node.toString());
			node = solution.get("desc");
			dataset.setDescription(node.toString());
			node = solution.get("sub");
			if(dataset.getSubject() != null)
			{
				dataset.setSubject(dataset.getSubject()+", "+node.toString());
			}else {
				dataset.setSubject(node.toString());
			}
			node = solution.get("keyw");
			if(dataset.getKeywords() != null)
			{
				dataset.setKeywords(dataset.getKeywords()+", "+node.toString());
			}else {
				dataset.setKeywords(node.toString());
			}
			node = solution.get("standard");
			dataset.setStandards(node.toString());
			node = solution.get("format");
			dataset.setFormats(node.toString());
			node = solution.get("lang");
			if(dataset.getLanguage() != null)
			{
				dataset.setLanguage(dataset.getLanguage()+", "+node.toString());
			}else {
				dataset.setLanguage(node.toString());
			}
			node = solution.get("homep");
			dataset.setHomepage(node.toString());
			node = solution.get("publi");
			dataset.setPublisher(node.toString());
			node = solution.get("rights");
			dataset.setAccessRights(node.toString());
			node = solution.get("issuedDate");
			dataset.setIssuedDate(node.toString());
			node = solution.get("modifiedDate");
			dataset.setModifiedDate(node.toString());
			node = solution.get("geoLoc");
			//if node = null then setGeoLocation to ""
			if(node!=null) {
				if(dataset.getGeoLocation() == null)
				{
					dataset.setGeoLocation(node.toString());
				}else {
					dataset.setGeoLocation(dataset.getGeoLocation()+", "+node.toString());				
				}
			}else {
				dataset.setGeoLocation("");
			}
			node = solution.get("vFrom");
			dataset.setVerticalCoverageFrom(node.toString());
			node = solution.get("vTo");
			dataset.setVerticalCoverageTo(node.toString());
			node = solution.get("timeReso");
			dataset.setTimeResolution(node.toString());
			node = solution.get("spatialWest");
			dataset.setSpatialWest(node.toString());
			node = solution.get("spatialEast");
			dataset.setSpatialEast(node.toString());
			node = solution.get("spatialNorth");
			dataset.setSpatialNorth(node.toString());
			node = solution.get("spatialSouth");
			dataset.setSpatialSouth(node.toString());
			node = solution.get("timeCovBeg");
			dataset.setTemporalCoverageBegin(node.toString());
			node = solution.get("timeCovEnd");
			dataset.setTemporalCoverageEnd(node.toString());
			node = solution.get("vLevel");
			dataset.setVerticalLevel(node.toString());
			node = solution.get("coorSys");
			dataset.setCoordinateSystem(node.toString());
		}
		
		List<String> listVariables = new ArrayList<>() ;
		RDFNode node2, node3;
		
		String queryVariables = "PREFIX disco: <http://rdf-vocabulary.ddialliance.org/discovery#>\n" +
				"PREFIX dct: <http://purl.org/dc/terms/>\n" +
				"PREFIX bdo: <http://bigdataocean.eu/bdo/>\n" +
				"PREFIX skos: <http://www.w3.org/2004/02/skos/core#>\n" +
				"SELECT ?uri ?identifierVariable (STR(?label) AS ?variables)\n" + 
				"WHERE {\n" +
				"  "+searchParam+" disco:variable ?variable.\n" + 
				"  ?variable a bdo:BDOVariable;\n" + 
				"      skos:prefLabel ?label.\n" + 
				"  FILTER(lang(?label) = \"en\")\n" + 
				"}";
		
		// Adding Datasetvariables -- BDOvariables in a list
		ResultSet rsVariables = QueryExecutor.selectQuery(queryVariables);
		while(rsVariables.hasNext()){
			QuerySolution solution = rsVariables.nextSolution();
			node = solution.get("variables");
			listVariables.add(node.toString());
			dataset.setVariable(listVariables);
		}
		
		dataset.setVariable(listVariables);
		return dataset;
	}
	
	public static List<Dataset> apiListDatasetByTimeCov (String searchParam) throws IOException {
		String[] listTime = searchParam.split(",- ");
		List<Dataset> list = new ArrayList<>();
		String apiQuery;
		if(listTime.length > 1) {
			apiQuery = "PREFIX disco: <http://rdf-vocabulary.ddialliance.org/discovery#>\n" + 
					"PREFIX skos: <http://www.w3.org/2004/02/skos/core#>\n" + 
					"PREFIX dct: <http://purl.org/dc/terms/>\n" + 
					"PREFIX dcat: <https://www.w3.org/TR/vocab-dcat/>\n" + 
					"PREFIX bdo: <http://bigdataocean.eu/bdo/>\n" + 
					"PREFIX ids: <http://industrialdataspace/information-model/>\n" + 
					"PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n" + 
					"\n" + 
					"SELECT distinct ?uri ?title ?lang (STR (?timeC_start) AS ?start) (STR (?timeC_end) AS ?end) ?subject\n" + 
					"WHERE {  \n" + 
					"  ?uri dct:title ?title;\n" + 
					"       dct:language ?lang;\n" + 
					"       dcat:subject ?subject;\n" + 
					"       bdo:timeCoverage ?timeC.\n" + 
					"  ?timeC a bdo:TimeCoverage;\n" + 
					"         ids:beginning ?timeC_start;\n" + 
					"         ids:end ?timeC_end.\n" + 
					"  FILTER (?timeC_start >= \""+listTime[0]+"\"^^xsd:dateTime)\n" +
					"  FILTER (?timeC_end < \""+listTime[1]+"\"^^xsd:dateTime)\n" + 
					"}";
		}else {
			apiQuery = "PREFIX disco: <http://rdf-vocabulary.ddialliance.org/discovery#>\n" + 
					"PREFIX skos: <http://www.w3.org/2004/02/skos/core#>\n" + 
					"PREFIX dct: <http://purl.org/dc/terms/>\n" + 
					"PREFIX dcat: <https://www.w3.org/TR/vocab-dcat/>\n" + 
					"PREFIX bdo: <http://bigdataocean.eu/bdo/>\n" + 
					"PREFIX ids: <http://industrialdataspace/information-model/>\n" + 
					"PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n" + 
					"\n" + 
					"SELECT distinct ?uri ?title ?lang (STR (?timeC_start) AS ?start) (STR (?timeC_end) AS ?end) ?subject\n" + 
					"WHERE {  \n" + 
					"  ?uri dct:title ?title;\n" + 
					"       dct:language ?lang;\n" + 
					"       dcat:subject ?subject;\n" + 
					"       bdo:timeCoverage ?timeC.\n" + 
					"  ?timeC a bdo:TimeCoverage;\n" + 
					"         ids:beginning ?timeC_start;\n" + 
					"         ids:end ?timeC_end.\n" + 
					"  FILTER (?timeC_start >= \""+listTime[0]+"\"^^xsd:dateTime)\n }" ;
		}

		RDFNode node;
		ResultSet results = QueryExecutor.selectQuery(apiQuery);
		//ResultSetFormatter.out(results);
		while(results.hasNext()){			
			Dataset dataset = new Dataset();
			QuerySolution solution = results.nextSolution();				
			node = solution.get("uri");
			List<String> listVar = new ArrayList<>();
			dataset.setIdentifier(node.toString());
			node = solution.get("title");
			dataset.setTitle(node.toString());
			node = solution.get("subject");
			if(dataset.getSubject() != null)
			{
				dataset.setSubject(dataset.getSubject()+", "+node.toString());
			}else {
				dataset.setSubject(node.toString());
			}
			node = solution.get("lang");
			if(dataset.getLanguage() != null)
			{
				dataset.setLanguage(dataset.getLanguage()+", "+node.toString());
			}else {
				dataset.setLanguage(node.toString());
			}
			node = solution.get("start");
			dataset.setTemporalCoverageBegin(node.toString());
			node = solution.get("end");
			dataset.setTemporalCoverageEnd(node.toString());
			list.add(dataset);
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

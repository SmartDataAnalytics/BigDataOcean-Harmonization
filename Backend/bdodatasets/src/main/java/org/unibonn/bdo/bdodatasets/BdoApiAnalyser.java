package org.unibonn.bdo.bdodatasets;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.unibonn.bdo.connections.QueryExecutor;
import org.unibonn.bdo.objects.DatasetApi;
import org.unibonn.bdo.objects.VariableDataset;

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
	public static List<DatasetApi> apiListAllDatasets (){
		List<DatasetApi> list = new ArrayList<>();
		String apiQuery = "PREFIX disco: <http://rdf-vocabulary.ddialliance.org/discovery#>\n" + 
				"PREFIX skos: <http://www.w3.org/2004/02/skos/core#>\n" + 
				"PREFIX dct: <http://purl.org/dc/terms/>\n" + 
				"PREFIX dcat: <https://www.w3.org/TR/vocab-dcat/>\n" + 
				"PREFIX bdo: <http://bigdataocean.eu/bdo/>\n" + 
				"PREFIX bdocm: <http://www.bigdataocean.eu/standards/canonicalmodel#>\n" +
				"PREFIX dbo: <http://dbpedia.org/ontology/>\n" + 
				"\n" + 
				"\n" + 
				"SELECT ?uri ?title ?subject ?keywords ?language ?nameVariable (STR (?label) as ?canonicalVariable) ?unit\n" + 
				"WHERE {\n" + 
				"  ?uri a dcat:Dataset;\n" + 
				"       dct:title ?title;\n" + 
				"       dcat:subject ?subject;\n" + 
				"       dcat:theme ?keywords;\n" + 
				"       dct:language ?language;\n" + 
				"       disco:variable ?variable.\n" + 
				"  ?variable a bdo:BDOVariable;\n" + 
				"      dct:identifier ?nameVariable;\n" +
				"      skos:prefLabel ?label.\n" + 
				"  OPTIONAL { ?variable bdocm:canonicalUnit ?unit } \n" +
				"}";
		RDFNode node;
		ResultSet results = QueryExecutor.selectQuery(apiQuery);
		String id = null;
		int i = 0;	
		while(results.hasNext()) {
			
			DatasetApi dataset = new DatasetApi();
			QuerySolution solution = results.nextSolution();				
			node = solution.get("uri");
			List<VariableDataset> variables = new ArrayList<>();
			VariableDataset varData;
			if(id != node.toString()) {
				dataset.setIdentifier(node.toString());
				id = node.toString();
				node = solution.get("title");
				dataset.setTitle(node.toString());
				node = solution.get("subject");
				if(dataset.getSubject() != null)
				{
					dataset.setSubject(dataset.getSubject()+", "+InsertNewDataset.convertLinkToWord(node.toString(), "subject"));
				}else {
					dataset.setSubject(InsertNewDataset.convertLinkToWord(node.toString(), "subject"));
				}
				node = solution.get("keywords");
				if(dataset.getKeywords() != null)
				{
					dataset.setKeywords(dataset.getKeywords()+", "+InsertNewDataset.convertLinkToWord(node.toString(), "keywords"));
				}else {
					dataset.setKeywords(InsertNewDataset.convertLinkToWord(node.toString(), "keywords"));
				}
				node = solution.get("language");
				if(dataset.getLanguage() != null)
				{
					dataset.setLanguage(dataset.getLanguage()+", "+node.toString());
				}else {
					dataset.setLanguage(node.toString());
				}
				varData = unitVariableisNull(solution);
				variables.add(varData);
				dataset.setVariables(variables);
				list.add(dataset);	
				i++;
			}else {
				dataset = list.get(i-1);
				variables = dataset.getVariables();
				varData = unitVariableisNull(solution);
				variables.add(varData);
				
			}
		}
		return list;
	}
	
	public static DatasetApi apiSearchDataset (String searchParam) {
		String queryMetadata = "PREFIX dct: <http://purl.org/dc/terms/>\n" + 
				"PREFIX dcat: <https://www.w3.org/TR/vocab-dcat/>\n" + 
				"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> \n" + 
				"PREFIX ignf: <http://data.ign.fr/def/ignf#>\n" + 
				"PREFIX foaf: <http://xmlns.com/foaf/0.1/>\n" + 
				"PREFIX bdo: <http://bigdataocean.eu/bdo/>\n" + 
				"PREFIX ids: <http://industrialdataspace/information-model/>\n" + 
				"SELECT ?uri ?ident ?title ?desc ?sub ?keyw ?standard ?format ?lang ?homep "
				+ "?publi ?rights (STR(?issued) AS ?issuedDate)  (STR(?modified) AS ?modifiedDate) "
				+ "?geoLoc ?timeReso (STR(?verFrom) AS ?vFrom) (STR(?verTo) AS ?vTo) (STR(?west) AS ?spatialWest) (STR(?east) AS ?spatialEast) "
				+ "(STR(?south) AS ?spatialSouth) (STR(?north) AS ?spatialNorth) (STR(?tempCovB) AS ?timeCovBeg) (STR(?tempCovE) AS ?timeCovEnd) "
				+ "?vLevel ?coorSys ?source ?observation ?storageTable\n" + 
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
				"       dct:creator ?source ; \n" +
				"       rdfs:comment ?observation ; \n" +
				"       bdo:storageTable ?storageTable ; \n" +
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
		
		DatasetApi dataset = new DatasetApi();
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
				dataset.setSubject(dataset.getSubject()+", "+InsertNewDataset.convertLinkToWord(node.toString(), "subject"));
			}else {
				dataset.setSubject(InsertNewDataset.convertLinkToWord(node.toString(), "subject"));
			}
			node = solution.get("keyw");
			if(dataset.getKeywords() != null)
			{
				dataset.setKeywords(dataset.getKeywords()+", "+InsertNewDataset.convertLinkToWord(node.toString(), "keywords"));
			}else {
				dataset.setKeywords(InsertNewDataset.convertLinkToWord(node.toString(), "keywords"));
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
			node = solution.get("source");
			dataset.setSource(node.toString());
			node = solution.get("observation");
			dataset.setObservations(node.toString());
			node = solution.get("storageTable");
			dataset.setStorageTable(node.toString());
			node = solution.get("geoLoc");
			//if node = null then setGeoLocation to ""
			if(node!=null) {
				if(dataset.getGeoLocation() == null)
				{
					dataset.setGeoLocation(InsertNewDataset.convertLinkToWord(node.toString(), "marineregions"));
				}else {
					dataset.setGeoLocation(dataset.getGeoLocation()+", "+InsertNewDataset.convertLinkToWord(node.toString(), "marineregions"));				
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

		List<VariableDataset> variables = new ArrayList<>();
		
		String queryVariables = "PREFIX disco: <http://rdf-vocabulary.ddialliance.org/discovery#>\n" +
				"PREFIX dct: <http://purl.org/dc/terms/>\n" +
				"PREFIX bdo: <http://bigdataocean.eu/bdo/>\n" +
				"PREFIX bdocm: <http://www.bigdataocean.eu/standards/canonicalmodel#>\n" +
				"PREFIX skos: <http://www.w3.org/2004/02/skos/core#>\n" +
				"SELECT ?nameVariable (STR(?label) AS ?canonicalVariable) ?unit\n" + 
				"WHERE {\n" +
				"  "+searchParam+" disco:variable ?variable.\n" + 
				"  ?variable a bdo:BDOVariable;\n" + 
				"      dct:identifier ?nameVariable;\n" +
				"      skos:prefLabel ?label.\n" + 
				"  OPTIONAL { ?variable bdocm:canonicalUnit ?unit } \n" +
				"  FILTER(lang(?label) = \"en\")\n" + 
				"}";
		
		// Adding Datasetvariables -- BDOvariables in a list
		ResultSet rsVariables = QueryExecutor.selectQuery(queryVariables);
		VariableDataset varData;
		while(rsVariables.hasNext()){
			QuerySolution solution = rsVariables.nextSolution();
			varData = unitVariableisNull(solution);
			variables.add(varData);
			dataset.setVariables(variables);
		}
		
		dataset.setVariables(variables);
		return dataset;
	}
	
	public static List<DatasetApi> apiSearchSubjects(String searchParam) {
		String searchParamLink = convertWordToLink(searchParam, "subject");
		String[] listSubject = searchParamLink.split(",");
		String values = "  VALUES ?subject { ";
		for(String sub : listSubject) {
			values += "<"+sub+"> ";
		}
		values += "}\n";
		List<DatasetApi> list = new ArrayList<>();
		String apiQuery = "PREFIX disco: <http://rdf-vocabulary.ddialliance.org/discovery#>\n" + 
				"PREFIX skos: <http://www.w3.org/2004/02/skos/core#>\n" + 
				"PREFIX dct: <http://purl.org/dc/terms/>\n" + 
				"PREFIX dcat: <https://www.w3.org/TR/vocab-dcat/>\n" + 
				"PREFIX bdo: <http://bigdataocean.eu/bdo/>\n" + 
				"PREFIX ids: <http://industrialdataspace/information-model/>\n" + 
				"PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n" + 
				"\n" + 
				"SELECT DISTINCT ?uri ?title ?lang ?subject\n" + 
				"WHERE {  \n" + 
				"  ?uri dct:title ?title;\n" + 
				"       dct:language ?lang;\n" + 
				"       dcat:subject ?subject.\n" +
				values +
				"}";
		ResultSet results = QueryExecutor.selectQuery(apiQuery);
		while(results.hasNext()) {
			DatasetApi dataset = new DatasetApi();
			QuerySolution solution = results.nextSolution();				
			dataset.setIdentifier(solution.get("uri").toString());
			dataset.setTitle(solution.get("title").toString());
			dataset.setLanguage(solution.get("lang").toString());
			dataset.setSubject(InsertNewDataset.convertLinkToWord(solution.get("subject").toString(), "subject"));
			list.add(dataset);
		}
		return list;
	}

	public static List<DatasetApi> apiSearchKeywords(String searchParam) {
		String searchParamLink = convertWordToLink(searchParam, "keywords");
		String[] listKeywords = searchParamLink.split(",");
		String values = "  VALUES ?keywords { ";
		for(String key : listKeywords) {
			values += "<"+key+"> ";
		}
		values += "}\n";
		List<DatasetApi> list = new ArrayList<>();
		String apiQuery = "PREFIX disco: <http://rdf-vocabulary.ddialliance.org/discovery#>\n" + 
				"PREFIX skos: <http://www.w3.org/2004/02/skos/core#>\n" + 
				"PREFIX dct: <http://purl.org/dc/terms/>\n" + 
				"PREFIX dcat: <https://www.w3.org/TR/vocab-dcat/>\n" + 
				"PREFIX bdo: <http://bigdataocean.eu/bdo/>\n" + 
				"PREFIX ids: <http://industrialdataspace/information-model/>\n" + 
				"PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n" + 
				"\n" + 
				"SELECT DISTINCT ?uri ?title ?lang ?keywords\n" + 
				"WHERE {  \n" + 
				"  ?uri dct:title ?title;\n" + 
				"       dct:language ?lang;\n" + 
				"       dcat:theme ?keywords.\n" +
				values +
				"}";
		ResultSet results = QueryExecutor.selectQuery(apiQuery);
		while(results.hasNext()) {
			DatasetApi dataset = new DatasetApi();
			QuerySolution solution = results.nextSolution();				
			dataset.setIdentifier(solution.get("uri").toString());
			dataset.setTitle(solution.get("title").toString());
			dataset.setLanguage(solution.get("lang").toString());
			dataset.setKeywords(InsertNewDataset.convertLinkToWord(solution.get("keywords").toString(),"keywords"));
			list.add(dataset);
		}
		return list;
	}

	public static List<DatasetApi> apiSearchGeoLoc(String searchParam) {
		String searchParamLink = convertWordToLink(searchParam, "marineregions");
		String[] listGeoLoc = searchParamLink.split(",");
		String values = "  VALUES ?geo_loc { ";
		for(String geoLoc : listGeoLoc) {
			values += "<"+geoLoc+"> ";
		}
		values += "}\n";
		List<DatasetApi> list = new ArrayList<>();
		String apiQuery = "PREFIX disco: <http://rdf-vocabulary.ddialliance.org/discovery#>\n" + 
				"PREFIX skos: <http://www.w3.org/2004/02/skos/core#>\n" + 
				"PREFIX dct: <http://purl.org/dc/terms/>\n" + 
				"PREFIX dcat: <https://www.w3.org/TR/vocab-dcat/>\n" + 
				"PREFIX bdo: <http://bigdataocean.eu/bdo/>\n" + 
				"PREFIX ids: <http://industrialdataspace/information-model/>\n" + 
				"PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n" + 
				"\n" + 
				"SELECT DISTINCT ?uri ?title ?lang ?geo_loc ?subject\n" + 
				"WHERE {  \n" + 
				"  ?uri dct:title ?title;\n" + 
				"       dct:language ?lang;\n" + 
				"       dct:spatial ?geo_loc;\n" + 
				"       dcat:subject ?subject." +
				values +
				"}";
		ResultSet results = QueryExecutor.selectQuery(apiQuery);
		while(results.hasNext()) {
			DatasetApi dataset = new DatasetApi();
			QuerySolution solution = results.nextSolution();				
			dataset.setIdentifier(solution.get("uri").toString());
			dataset.setTitle(solution.get("title").toString());
			dataset.setLanguage(solution.get("lang").toString());
			dataset.setSubject(InsertNewDataset.convertLinkToWord(solution.get("subject").toString(), "subject"));
			dataset.setGeoLocation(InsertNewDataset.convertLinkToWord(solution.get("geo_loc").toString(), "marineregions"));
			list.add(dataset);
		}
		return list;
	}
	
	public static List<DatasetApi> apisearchGeoCoverage(String searchParam) {
		String[] listGeoLoc = searchParam.split(",");
		List<String> newList = new ArrayList<>();
		for (int i = 0; i<listGeoLoc.length; i++) {
			double number = Double.parseDouble(listGeoLoc[i]);
			if (number >= 0) {
				newList.add(""+(number+1.0));
			}else {
				newList.add(listGeoLoc[i]);
				listGeoLoc[i] = ""+(number-1.0);
			}
		}
		List<DatasetApi> list = new ArrayList<>();
		String apiQuery = "PREFIX disco: <http://rdf-vocabulary.ddialliance.org/discovery#>\n" + 
				"PREFIX skos: <http://www.w3.org/2004/02/skos/core#>\n" + 
				"PREFIX dct: <http://purl.org/dc/terms/>\n" + 
				"PREFIX dcat: <https://www.w3.org/TR/vocab-dcat/>\n" + 
				"PREFIX bdo: <http://bigdataocean.eu/bdo/> \n" + 
				"PREFIX ids: <http://industrialdataspace/information-model/>\n" + 
				"PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n" + 
				"PREFIX ignf: <http://data.ign.fr/def/ignf#>\n" + 
				"\n" + 
				"SELECT DISTINCT ?uri ?title ?lang ?geo_loc ?subject (STR(?east) AS ?streast) (STR(?west) AS ?strwest) "
				+ "(STR(?south) AS ?strsouth) (STR(?north) AS ?strnorth) \n" + 
				"WHERE { \n" + 
				"  	?uri dct:title ?title;\n" + 
				"	   dct:language ?lang;\n" + 
				"	   dct:spatial ?geo_loc;\n" + 
				"	   bdo:GeographicalCoverage ?geocov;\n" + 
				"	   dcat:subject ?subject.\n" + 
				"    ?geocov a ignf:GeographicBoundingBox;\n" + 
				"	   ignf:eastBoundLongitude ?east;\n" + 
				"	   ignf:northBoundLatitude ?north;\n" + 
				"	   ignf:southBoundLatitude ?south;\n" + 
				"	   ignf:westBoundLongitude ?west.\n" + 
				"  FILTER ((?east>="+listGeoLoc[1]+") && (?north>="+listGeoLoc[3]+") && (?south>="+listGeoLoc[2]+") && (?west>="+listGeoLoc[0]+") || \n"+
				"  (?east<="+newList.get(1)+") && (?north<="+newList.get(3)+") && (?south<="+newList.get(2)+") && (?west<="+newList.get(0)+"))\n" + 
				"}";
		ResultSet results = QueryExecutor.selectQuery(apiQuery);
		while(results.hasNext()) {
			DatasetApi dataset = new DatasetApi();
			QuerySolution solution = results.nextSolution();				
			dataset.setIdentifier(solution.get("uri").toString());
			dataset.setTitle(solution.get("title").toString());
			dataset.setLanguage(solution.get("lang").toString());
			dataset.setSubject(InsertNewDataset.convertLinkToWord(solution.get("subject").toString(), "subject"));
			dataset.setSpatialEast(solution.get("streast").toString());
			dataset.setSpatialNorth(solution.get("strnorth").toString());
			dataset.setSpatialSouth(solution.get("strsouth").toString());
			dataset.setSpatialWest(solution.get("strwest").toString());
			list.add(dataset);
		}
		return list;
	}

	public static List<DatasetApi> apiListDatasetByVertCov (String searchParam){
		String[] listVert = searchParam.split(",");
		List<DatasetApi> list = new ArrayList<>();
		String apiQuery = "PREFIX disco: <http://rdf-vocabulary.ddialliance.org/discovery#>\n" + 
				"PREFIX skos: <http://www.w3.org/2004/02/skos/core#>\n" + 
				"PREFIX dct: <http://purl.org/dc/terms/>\n" + 
				"PREFIX dcat: <https://www.w3.org/TR/vocab-dcat/>\n" + 
				"PREFIX bdo: <http://bigdataocean.eu/bdo/>\n" + 
				"PREFIX ids: <http://industrialdataspace/information-model/>\n" + 
				"PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n" + 
				"\n" + 
				"SELECT distinct ?uri ?title ?lang (STR (?vertC_from) AS ?from) (STR (?vertC_to) AS ?to) ?subject\n" + 
				"WHERE {  \n" + 
				"  ?uri dct:title ?title;\n" + 
				"       dct:language ?lang;\n" + 
				"       dcat:subject ?subject;\n\n" + 
				"       bdo:verticalCoverage ?vertC.\n" + 
				"  ?vertC a bdo:VerticalCoverage;\n" + 
				"         bdo:verticalFrom ?vertC_from;\n" + 
				"         bdo:verticalTo ?vertC_to.\n" +
				"  FILTER (?vertC_from >= \""+listVert[0]+"\"^^xsd:double)\n" +
				"  FILTER (?vertC_to <= \""+listVert[1]+"\"^^xsd:double)\n" + 
				"}";
		
		RDFNode node;
		ResultSet results = QueryExecutor.selectQuery(apiQuery);
		while(results.hasNext()){			
			DatasetApi dataset = new DatasetApi();
			QuerySolution solution = results.nextSolution();				
			node = solution.get("uri");
			dataset.setIdentifier(node.toString());
			node = solution.get("title");
			dataset.setTitle(node.toString());
			node = solution.get("subject");
			if(dataset.getSubject() != null)
			{
				dataset.setSubject(dataset.getSubject()+", "+ InsertNewDataset.convertLinkToWord(node.toString(), "subject"));
			}else {
				dataset.setSubject(InsertNewDataset.convertLinkToWord(node.toString(), "subject"));
			}
			node = solution.get("lang");
			if(dataset.getLanguage() != null)
			{
				dataset.setLanguage(dataset.getLanguage()+", "+node.toString());
			}else {
				dataset.setLanguage(node.toString());
			}
			node = solution.get("from");
			dataset.setVerticalCoverageFrom(node.toString());
			node = solution.get("to");
			dataset.setVerticalCoverageTo(node.toString());
			list.add(dataset);
		}
		return list;
	}

	
	public static List<DatasetApi> apiListDatasetByTimeCov (String searchParam){
		String[] listTime = searchParam.split(",");
		List<DatasetApi> list = new ArrayList<>();
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
		while(results.hasNext()){			
			DatasetApi dataset = new DatasetApi();
			QuerySolution solution = results.nextSolution();				
			node = solution.get("uri");
			dataset.setIdentifier(node.toString());
			node = solution.get("title");
			dataset.setTitle(node.toString());
			node = solution.get("subject");
			if(dataset.getSubject() != null)
			{
				dataset.setSubject(dataset.getSubject()+", "+InsertNewDataset.convertLinkToWord(node.toString(), "subject"));
			}else {
				dataset.setSubject(InsertNewDataset.convertLinkToWord(node.toString(), "subject"));
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
	
	public static DatasetApi apiListVarOfDataset (String searchParam) {
		DatasetApi dataset = new DatasetApi();
		List<VariableDataset> variables = new ArrayList<>();
		String apiQuery = "PREFIX disco: <http://rdf-vocabulary.ddialliance.org/discovery#>\n" + 
				"PREFIX skos: <http://www.w3.org/2004/02/skos/core#>\n" + 
				"PREFIX dct: <http://purl.org/dc/terms/>\n" + 
				"PREFIX dcat: <https://www.w3.org/TR/vocab-dcat/>\n" + 
				"PREFIX bdo: <http://bigdataocean.eu/bdo/>\n" + 
				"PREFIX bdocm: <http://www.bigdataocean.eu/standards/canonicalmodel#>\n" +
				"\n" + 
				"\n" + 
				"SELECT distinct ?uri ?title ?nameVariable (STR(?label) AS ?canonicalVariable) ?unit\n" + 
				"WHERE {  \n" + 
				"  bdo:"+searchParam+" disco:variable ?variable;\n" + 
				"       dct:title ?title.\n" + 
				"  ?variable a bdo:BDOVariable;\n" + 
				"      dct:identifier ?nameVariable;\n" +
				"      skos:prefLabel ?label.\n" + 
				"  OPTIONAL { ?variable bdocm:canonicalUnit ?unit } \n" +
				"}";
		RDFNode node;
		ResultSet results = QueryExecutor.selectQuery(apiQuery);
		VariableDataset varData;
		while(results.hasNext()) {					
			dataset.setIdentifier("http://bigdataocean.eu/bdo/"+searchParam);
			QuerySolution solution = results.nextSolution();				
			node = solution.get("title");
			dataset.setTitle(node.toString());
			varData = unitVariableisNull(solution);
			variables.add(varData);
			dataset.setVariables(variables);
		}
		return dataset;
	}
	
	public static DatasetApi apiListVarOfDatasetStorage (String searchParam) {
		DatasetApi dataset = new DatasetApi();
		List<VariableDataset> variables = new ArrayList<>();
		String apiQuery = "PREFIX disco: <http://rdf-vocabulary.ddialliance.org/discovery#>\n" + 
				"PREFIX skos: <http://www.w3.org/2004/02/skos/core#>\n" + 
				"PREFIX dct: <http://purl.org/dc/terms/>\n" + 
				"PREFIX dcat: <https://www.w3.org/TR/vocab-dcat/>\n" + 
				"PREFIX bdo: <http://bigdataocean.eu/bdo/>\n" + 
				"PREFIX bdocm: <http://www.bigdataocean.eu/standards/canonicalmodel#>\n" +
				"\n" + 
				"\n" + 
				"SELECT distinct ?uri ?nameVariable (STR(?label) AS ?canonicalVariable) ?unit\n" + 
				"WHERE {  \n" + 
				"  ?uri disco:variable ?variable;\n" + 
				"       bdo:storageTable '"+searchParam+"'.\n" + 
				"  ?variable a bdo:BDOVariable;\n" + 
				"      dct:identifier ?nameVariable;\n" +
				"      skos:prefLabel ?label.\n" + 
				"  OPTIONAL { ?variable bdocm:canonicalUnit ?unit } \n" +
				"}" +
				"ORDER BY ?uri";
		ResultSet results = QueryExecutor.selectQuery(apiQuery);
		VariableDataset varData;
		String uri;
		while(results.hasNext()) {			
			QuerySolution solution = results.nextSolution();	
			uri = solution.get("uri").toString();
			if(dataset.getIdentifier().isEmpty() || dataset.getIdentifier().equals(uri)) {
				dataset.setIdentifier(uri);			
				dataset.setStorageTable(searchParam);;
				varData = unitVariableisNull(solution);
				variables.add(varData);
				dataset.setVariables(variables);
			} else {
				break;
			}
		}
		dataset.setIdentifier(null);
		return dataset;
	}
	
	public static List<DatasetApi> apiListDatasetsByVar (String searchParam) {
		List<DatasetApi> list = new ArrayList<>();
		String[] listV = searchParam.split(",");
		
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
				"PREFIX bdocm: <http://www.bigdataocean.eu/standards/canonicalmodel#>\n" +
				"\n" + 
				"\n" + 
				"SELECT distinct ?uri ?title ?subject ?language ?nameVariable (STR(?var) AS ?canonicalVariable) ?unit\n" + 
				"WHERE {\n" + 
				"  ?uriVar a bdo:BDOVariable;\n" + 
				"      dct:identifier ?nameVariable;\n "+
				"      skos:prefLabel ?var.\n" + 
				"  OPTIONAL { ?uriVar bdocm:canonicalUnit ?unit } \n" +
				values + 
				"  ?uri disco:variable ?uriVar;\n" + 
				"       dct:title ?title;\n" + 
				"       dcat:subject ?subject;\n" + 
				"       dct:language ?language.\n" + 
				"}\n" +
				"ORDER BY ?uri";
		
		RDFNode node;
		ResultSet results = QueryExecutor.selectQuery(apiQuery);
		
		String id = null;
		int i = 0;		
		while(results.hasNext()){
			
			DatasetApi dataset = new DatasetApi();
			QuerySolution solution = results.nextSolution();	
			List<VariableDataset> variables = new ArrayList<>();
			VariableDataset varData;			
			node = solution.get("uri");
			if(id != node.toString()) {
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
				varData = unitVariableisNull(solution);
				variables.add(varData);
				dataset.setVariables(variables);
				list.add(dataset);	
				i++;
			}else {
				dataset = list.get(i-1);
				dataset.setVariables(variables);
				varData = unitVariableisNull(solution);
				variables.add(varData);
			}
		}
		return list;
	}
	
	public static List<DatasetApi> apiSearchStorageTable (String searchParam){
		List<DatasetApi> list = new ArrayList<>();
		String apiQuery = "PREFIX disco: <http://rdf-vocabulary.ddialliance.org/discovery#> \n" + 
				"PREFIX skos: <http://www.w3.org/2004/02/skos/core#> \n" + 
				"PREFIX dct: <http://purl.org/dc/terms/> \n" + 
				"PREFIX dcat: <https://www.w3.org/TR/vocab-dcat/> \n" + 
				"PREFIX bdo: <http://bigdataocean.eu/bdo/> \n" + 
				"PREFIX bdocm: <http://www.bigdataocean.eu/standards/canonicalmodel#>\n" + 
				"PREFIX dbo: <http://dbpedia.org/ontology/> \n" + 
				"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" + 
				"PREFIX ids: <http://industrialdataspace/information-model/>\n" + 
				"PREFIX ignf: <http://data.ign.fr/def/ignf#>\n" + 
				"SELECT ?uri ?title ?desc ?publi ?rights ?timeReso (STR(?verFrom) AS ?vFrom) (STR(?verTo) AS ?vTo) (STR(?west) AS ?spatialWest) (STR(?east) AS ?spatialEast) (STR(?south) AS ?spatialSouth) (STR(?north) AS ?spatialNorth) (STR(?tempCovB) AS ?timeCovBeg) (STR(?tempCovE) AS ?timeCovEnd) ?vLevel ?coorSys ?nameVariable (STR (?label) as ?canonicalVariable) ?unit \n" + 
				"WHERE { \n" + 
				"  ?uri a dcat:Dataset; \n" + 
				"       dct:title ?title; \n" + 
				"       dct:description ?desc ;\n" + 
				"       dct:publisher ?publi ;\n" + 
				"       bdo:storageTable '"+searchParam+"' ;\n" + 
				"       dct:accessRights ?rights ; \n" + 
				"       bdo:timeResolution ?timeReso ;\n" + 
				"       bdo:GeographicalCoverage ?spatial ;\n" + 
				"       rdfs:comment ?observation ; \n" + 
				"       bdo:verticalLevel ?vLevel ;\n" + 
				"       dct:conformsTo ?coorSys ;\n" + 
				"       bdo:timeCoverage ?temp ;\n" + 
				"       bdo:verticalCoverage ?vCov ;\n" + 
				"       disco:variable ?variable.\n" + 
				"  ?temp a bdo:TimeCoverage;\n" + 
				"		 ids:beginning ?tempCovB ;\n" + 
				"        ids:end ?tempCovE .\n" + 
				"  ?spatial a ignf:GeographicBoundingBox ;\n" + 
				"           ignf:westBoundLongitude ?west ;\n" + 
				"           ignf:eastBoundLongitude ?east ;\n" + 
				"           ignf:southBoundLatitude ?south ;\n" + 
				"           ignf:northBoundLatitude ?north .\n" + 
				"  ?vCov a  bdo:VerticalCoverage ;\n" + 
				"        bdo:verticalFrom ?verFrom ;\n" + 
				"        bdo:verticalTo ?verTo .\n" + 
				"  ?variable a bdo:BDOVariable; \n" + 
				"      dct:identifier ?nameVariable;\n" + 
				"      skos:prefLabel ?label. \n" + 
				"  OPTIONAL { ?variable bdocm:canonicalUnit ?unit } \n" + 
				"}" +
				"ORDER BY ?uri";
		RDFNode node;
		ResultSet results = QueryExecutor.selectQuery(apiQuery);
		String id = null;
		int i = 0;	
		while(results.hasNext()) {
			
			DatasetApi dataset = new DatasetApi();
			QuerySolution solution = results.nextSolution();				
			node = solution.get("uri");
			List<VariableDataset> variables = new ArrayList<>();
			VariableDataset varData;
			if(id != node.toString()) {
				dataset.setIdentifier(node.toString());
				id = node.toString();
				node = solution.get("title");
				dataset.setTitle(node.toString());
				dataset.setDescription(solution.get("desc").toString());
				dataset.setPublisher(solution.get("publi").toString());
				dataset.setAccessRights(solution.get("rights").toString());
				dataset.setStorageTable(searchParam);
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
				varData = unitVariableisNull(solution);
				variables.add(varData);
				dataset.setVariables(variables);
				list.add(dataset);	
				i++;
			}else {
				dataset = list.get(i-1);
				variables = dataset.getVariables();
				varData = unitVariableisNull(solution);
				variables.add(varData);
				
			}
		}
		return list;
	}
	
	public static List<DatasetApi> apiSearchTitle (String searchParam){
		List<DatasetApi> list = new ArrayList<>();
		String apiQuery = "PREFIX disco: <http://rdf-vocabulary.ddialliance.org/discovery#> \n" + 
				"PREFIX skos: <http://www.w3.org/2004/02/skos/core#> \n" + 
				"PREFIX dct: <http://purl.org/dc/terms/> \n" + 
				"PREFIX dcat: <https://www.w3.org/TR/vocab-dcat/> \n" + 
				"PREFIX bdo: <http://bigdataocean.eu/bdo/> \n" + 
				"PREFIX bdocm: <http://www.bigdataocean.eu/standards/canonicalmodel#>\n" + 
				"PREFIX dbo: <http://dbpedia.org/ontology/> \n" + 
				"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" + 
				"PREFIX ids: <http://industrialdataspace/information-model/>\n" + 
				"PREFIX ignf: <http://data.ign.fr/def/ignf#>\n" + 
				"SELECT ?uri ?title ?desc ?publi ?rights ?timeReso ?storage (STR(?verFrom) AS ?vFrom) (STR(?verTo) AS ?vTo) (STR(?west) AS ?spatialWest) (STR(?east) AS ?spatialEast) (STR(?south) AS ?spatialSouth) (STR(?north) AS ?spatialNorth) (STR(?tempCovB) AS ?timeCovBeg) (STR(?tempCovE) AS ?timeCovEnd) ?vLevel ?coorSys ?nameVariable (STR (?label) as ?canonicalVariable) ?unit \n" + 
				"WHERE { \n" + 
				"  ?uri a dcat:Dataset; \n" + 
				"       dct:title ?title; \n" + 
				"       dct:description ?desc ;\n" + 
				"       dct:publisher ?publi ;\n" + 
				"       bdo:storageTable ?storage ;\n" + 
				"       dct:accessRights ?rights ; \n" + 
				"       bdo:timeResolution ?timeReso ;\n" + 
				"       bdo:GeographicalCoverage ?spatial ;\n" + 
				"       rdfs:comment ?observation ; \n" + 
				"       bdo:verticalLevel ?vLevel ;\n" + 
				"       dct:conformsTo ?coorSys ;\n" + 
				"       bdo:timeCoverage ?temp ;\n" + 
				"       bdo:verticalCoverage ?vCov ;\n" + 
				"       disco:variable ?variable.\n" + 
				"  ?temp a bdo:TimeCoverage;\n" + 
				"		 ids:beginning ?tempCovB ;\n" + 
				"        ids:end ?tempCovE .\n" + 
				"  ?spatial a ignf:GeographicBoundingBox ;\n" + 
				"           ignf:westBoundLongitude ?west ;\n" + 
				"           ignf:eastBoundLongitude ?east ;\n" + 
				"           ignf:southBoundLatitude ?south ;\n" + 
				"           ignf:northBoundLatitude ?north .\n" + 
				"  ?vCov a  bdo:VerticalCoverage ;\n" + 
				"        bdo:verticalFrom ?verFrom ;\n" + 
				"        bdo:verticalTo ?verTo .\n" + 
				"  ?variable a bdo:BDOVariable; \n" + 
				"      dct:identifier ?nameVariable;\n" + 
				"      skos:prefLabel ?label. \n" + 
				"  OPTIONAL { ?variable bdocm:canonicalUnit ?unit } \n" + 
				"  FILTER regex(?title, '"+ searchParam +"', 'i') \n" + 
				"}" +
				"ORDER BY ?uri";
		RDFNode node;
		ResultSet results = QueryExecutor.selectQuery(apiQuery);
		String id = null;
		int i = 0;	
		while(results.hasNext()) {
			
			DatasetApi dataset = new DatasetApi();
			QuerySolution solution = results.nextSolution();				
			node = solution.get("uri");
			List<VariableDataset> variables = new ArrayList<>();
			VariableDataset varData;
			if(id != node.toString()) {
				dataset.setIdentifier(node.toString());
				id = node.toString();
				node = solution.get("title");
				dataset.setTitle(node.toString());
				dataset.setDescription(solution.get("desc").toString());
				dataset.setPublisher(solution.get("publi").toString());
				dataset.setAccessRights(solution.get("rights").toString());
				dataset.setStorageTable(searchParam);
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
				varData = unitVariableisNull(solution);
				variables.add(varData);
				dataset.setVariables(variables);
				list.add(dataset);	
				i++;
			}else {
				dataset = list.get(i-1);
				variables = dataset.getVariables();
				varData = unitVariableisNull(solution);
				variables.add(varData);
				
			}
		}
		return list;
	}
	
	public static List<DatasetApi> apiSearchDescription (String searchParam){
		List<DatasetApi> list = new ArrayList<>();
		String apiQuery = "PREFIX disco: <http://rdf-vocabulary.ddialliance.org/discovery#> \n" + 
				"PREFIX skos: <http://www.w3.org/2004/02/skos/core#> \n" + 
				"PREFIX dct: <http://purl.org/dc/terms/> \n" + 
				"PREFIX dcat: <https://www.w3.org/TR/vocab-dcat/> \n" + 
				"PREFIX bdo: <http://bigdataocean.eu/bdo/> \n" + 
				"PREFIX bdocm: <http://www.bigdataocean.eu/standards/canonicalmodel#>\n" + 
				"PREFIX dbo: <http://dbpedia.org/ontology/> \n" + 
				"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" + 
				"PREFIX ids: <http://industrialdataspace/information-model/>\n" + 
				"PREFIX ignf: <http://data.ign.fr/def/ignf#>\n" + 
				"SELECT ?uri ?title ?desc ?publi ?rights ?timeReso ?storage (STR(?verFrom) AS ?vFrom) (STR(?verTo) AS ?vTo) (STR(?west) AS ?spatialWest) (STR(?east) AS ?spatialEast) (STR(?south) AS ?spatialSouth) (STR(?north) AS ?spatialNorth) (STR(?tempCovB) AS ?timeCovBeg) (STR(?tempCovE) AS ?timeCovEnd) ?vLevel ?coorSys ?nameVariable (STR (?label) as ?canonicalVariable) ?unit \n" + 
				"WHERE { \n" + 
				"  ?uri a dcat:Dataset; \n" + 
				"       dct:title ?title; \n" + 
				"       dct:description ?desc ;\n" + 
				"       dct:publisher ?publi ;\n" + 
				"       bdo:storageTable ?storage ;\n" + 
				"       dct:accessRights ?rights ; \n" + 
				"       bdo:timeResolution ?timeReso ;\n" + 
				"       bdo:GeographicalCoverage ?spatial ;\n" + 
				"       rdfs:comment ?observation ; \n" + 
				"       bdo:verticalLevel ?vLevel ;\n" + 
				"       dct:conformsTo ?coorSys ;\n" + 
				"       bdo:timeCoverage ?temp ;\n" + 
				"       bdo:verticalCoverage ?vCov ;\n" + 
				"       disco:variable ?variable.\n" + 
				"  ?temp a bdo:TimeCoverage;\n" + 
				"		 ids:beginning ?tempCovB ;\n" + 
				"        ids:end ?tempCovE .\n" + 
				"  ?spatial a ignf:GeographicBoundingBox ;\n" + 
				"           ignf:westBoundLongitude ?west ;\n" + 
				"           ignf:eastBoundLongitude ?east ;\n" + 
				"           ignf:southBoundLatitude ?south ;\n" + 
				"           ignf:northBoundLatitude ?north .\n" + 
				"  ?vCov a  bdo:VerticalCoverage ;\n" + 
				"        bdo:verticalFrom ?verFrom ;\n" + 
				"        bdo:verticalTo ?verTo .\n" + 
				"  ?variable a bdo:BDOVariable; \n" + 
				"      dct:identifier ?nameVariable;\n" + 
				"      skos:prefLabel ?label. \n" + 
				"  OPTIONAL { ?variable bdocm:canonicalUnit ?unit } \n" + 
				"  FILTER regex(?desc, '"+ searchParam +"', 'i') \n" + 
				"}" +
				"ORDER BY ?uri";
		RDFNode node;
		ResultSet results = QueryExecutor.selectQuery(apiQuery);
		String id = null;
		int i = 0;	
		while(results.hasNext()) {
			
			DatasetApi dataset = new DatasetApi();
			QuerySolution solution = results.nextSolution();				
			node = solution.get("uri");
			List<VariableDataset> variables = new ArrayList<>();
			VariableDataset varData;
			if(id != node.toString()) {
				dataset.setIdentifier(node.toString());
				id = node.toString();
				node = solution.get("title");
				dataset.setTitle(node.toString());
				dataset.setDescription(solution.get("desc").toString());
				dataset.setPublisher(solution.get("publi").toString());
				dataset.setAccessRights(solution.get("rights").toString());
				dataset.setStorageTable(searchParam);
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
				varData = unitVariableisNull(solution);
				variables.add(varData);
				dataset.setVariables(variables);
				list.add(dataset);	
				i++;
			}else {
				dataset = list.get(i-1);
				variables = dataset.getVariables();
				varData = unitVariableisNull(solution);
				variables.add(varData);
				
			}
		}
		return list;
	}
	
	private static VariableDataset unitVariableisNull (QuerySolution solution) {
		VariableDataset varData;
		if (solution.get("unit") == null) {
			varData = new VariableDataset(solution.get("nameVariable").toString(), solution.get("canonicalVariable").toString(), "");
		} else  {
			varData = new VariableDataset(solution.get("nameVariable").toString(), solution.get("canonicalVariable").toString(), solution.get("unit").toString());
		}
		return varData;
	}
	
	private static String convertWordToLink(String value, String typeValue) {
  		String result = "";
  		String jsonPath = Constants.CONFIGFILEPATH + "/Frontend/Flask/static/json/" +  typeValue + ".json";
  		if(!value.isEmpty()) {
  			try {
  				String[] tokens = value.split(",");
  				if(!tokens[0].contains("http")) {
	  				JSONParser parser = new JSONParser();
	  				JSONArray jsonArray = (JSONArray) parser.parse(new FileReader(jsonPath));
	  				for (String token: tokens) {
	  					for(int i=0; i<jsonArray.size(); i++){
	  						JSONObject jsonObject = (JSONObject) jsonArray.get(i);
	  			            if(jsonObject.get("text").toString().equals(token)) {
	  			            	if(result.equals("")) {
	  			            		result = jsonObject.get("value").toString();
	  			            	} else {
	  			            		result = result + "," + jsonObject.get("value").toString();
	  			            	}
	  			            }
	  			            
	  					}
	  				}
  				}else {
  					result = value;
  				}
  			} catch (IOException | ParseException e) {
  				e.printStackTrace();
  			}
  		}
  		return result;
  	}

}

package org.unibonn.bdo.bdodatasets;

import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
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
 * Methods for RESTful APIs
 *
 */

public class BdoApiAnalyser {
	//Case 1: List all datasets
	public static List<DatasetApi> apiListAllFileDatasets (){
		List<DatasetApi> list = new ArrayList<>();
		String apiQuery = "PREFIX disco: <http://rdf-vocabulary.ddialliance.org/discovery#>\n" + 
				"PREFIX skos: <http://www.w3.org/2004/02/skos/core#>\n" + 
				"PREFIX dct: <http://purl.org/dc/terms/>\n" + 
				"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> \n" + 
				"PREFIX ids: <http://industrialdataspace/information-model/>\n" + 
				"PREFIX dcat: <https://www.w3.org/TR/vocab-dcat/>\n" + 
				"PREFIX bdo: <http://bigdataocean.eu/bdo/>\n" + 
				"PREFIX bdocm: <http://www.bigdataocean.eu/standards/canonicalmodel#>\n" +
				"PREFIX dbo: <http://dbpedia.org/ontology/>\n" + 
				"SELECT ?uri ?ident ?title ?desc ?storageTable  \n" + 
				"WHERE {\n" + 
				"  ?uri a dcat:Dataset;\n" + 
				"       dct:identifier ?ident ;\n" + 
				"       dct:title ?title;\n" + 
				"       dct:description ?desc;\n" + 
				"       bdo:storageTable ?storageTable.\n" + 
				"}" + 
				"ORDER BY ?uri";
		ResultSet results = QueryExecutor.selectQuery(apiQuery);
		while(results.hasNext()) {
			DatasetApi dataset = new DatasetApi();
			QuerySolution solution = results.nextSolution();	
			dataset.setIdentifier(solution.get("ident").toString());
			dataset.setTitle(solution.get("title").toString());
			dataset.setDescription(solution.get("desc").toString());
			dataset.setStorageTable(solution.get("storageTable").toString());
			list.add(dataset);
		}
		return list;
	}
	
	public static DatasetApi apiInfoFileDataset (String searchParam) {
		String queryMetadata = "PREFIX dct: <http://purl.org/dc/terms/>\n" + 
				"PREFIX dcat: <https://www.w3.org/TR/vocab-dcat/>\n" + 
				"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> \n" + 
				"PREFIX ignf: <http://data.ign.fr/def/ignf#>\n" + 
				"PREFIX foaf: <http://xmlns.com/foaf/0.1/>\n" + 
				"PREFIX bdo: <http://bigdataocean.eu/bdo/>\n" + 
				"PREFIX ids: <http://industrialdataspace/information-model/>\n" + 
				"SELECT ?ident ?title ?desc ?standard ?format ?homep "
				+ "?publi ?rights (STR(?issued) AS ?issuedDate)  (STR(?modified) AS ?modifiedDate) "
				+ "?timeReso (STR(?verFrom) AS ?vFrom) (STR(?verTo) AS ?vTo) (STR(?west) AS ?spatialWest) "
				+ "(STR(?east) AS ?spatialEast) (STR(?south) AS ?spatialSouth) (STR(?north) AS ?spatialNorth) "
				+ "(STR(?tempCovB) AS ?timeCovBeg) (STR(?tempCovE) AS ?timeCovEnd) ?vLevel ?coorSys ?source "
				+ "?observation ?storageTable ?license\n" + 
				"WHERE{ \n" + 
				"  "+searchParam+" a dcat:Dataset ;\n" + 
				"       dct:identifier ?ident ;\n" + 
				"       dct:title ?title ;\n" + 
				"       dct:description ?desc ;\n" + 
				"       bdo:verticalCoverage ?vCov ;\n" + 
				"       dct:Standard ?standard ;\n" + 
				"       dct:format ?format ;\n" + 
				"       foaf:homepage ?homep ;\n" + 
				"       dct:publisher ?publi ;\n" + 
				"       dct:license ?license ; \n" + 
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
			node = solution.get("standard");
			dataset.setStandards(node.toString());
			node = solution.get("format");
			dataset.setFormats(node.toString());
			node = solution.get("homep");
			dataset.setHomepage(node.toString());
			node = solution.get("publi");
			dataset.setPublisher(node.toString());
			node = solution.get("license");
			dataset.setLicense(node.toString());
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
		
		if (results.getRowNumber() > 0) {
			dataset = getSubject(searchParam, dataset);
			dataset = getKeywords(searchParam, dataset);
			dataset = getGeoLoc(searchParam, dataset);
			dataset = getLanguage(searchParam, dataset);
			dataset = getVariables(searchParam, dataset);
		}
		
		return dataset;
	}
	
	public static List<DatasetApi> apiSearchSubjects(String searchParam) {
		String[] token = searchParam.split(" -- ");
		String searchParamLink = convertWordToLink(token[0], "subject");
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
				"SELECT DISTINCT ?uri ?ident ?title ?subject ?storage\n" + 
				"WHERE {  \n" + 
				"  ?uri dct:title ?title;\n" + 
				"       dct:identifier ?ident ;\n" + 
				"       bdo:storageTable ?storage ;\n" + 
				"       dcat:subject ?subject.\n" +
				values +
				"}" + 
				"ORDER BY ?uri \n" + 
				"LIMIT " + token[1];
		ResultSet results = QueryExecutor.selectQuery(apiQuery);
		RDFNode node;
		String id = "";
		while(results.hasNext()) {
			DatasetApi dataset = new DatasetApi();
			QuerySolution solution = results.nextSolution();
			if (!id.equals(solution.get("uri").toString())) {
				id = solution.get("uri").toString();
				dataset.setIdentifier(solution.get("ident").toString());
				dataset.setTitle(solution.get("title").toString());
				dataset.setStorageTable(solution.get("storage").toString());
				node = solution.get("subject");
				dataset.setSubject(InsertNewDataset.convertLinkToWord(node.toString(), "subject"));
				list.add(dataset);
			} else {
				int size = list.size();
				dataset = list.get(size-1);
				node = solution.get("subject");
				dataset.setSubject(dataset.getSubject()+", "+InsertNewDataset.convertLinkToWord(node.toString(), "subject"));
				list.remove(size-1);
				list.add(dataset);
			}
		}
		return list;
	}

	public static List<DatasetApi> apiSearchKeywords(String searchParam) {
		String[] token = searchParam.split(" -- ");
		String searchParamLink = convertWordToLink(token[0], "keywords");
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
				"SELECT DISTINCT ?uri ?ident ?title ?keywords ?storage \n" + 
				"WHERE {  \n" + 
				"  ?uri dct:title ?title;\n" + 
				"       dct:identifier ?ident ;\n" + 
				"       bdo:storageTable ?storage ;\n" + 
				"       dcat:theme ?keywords.\n" +
				values +
				"}" + 
				"ORDER BY ?uri \n" + 
				"LIMIT " + token[1];
		ResultSet results = QueryExecutor.selectQuery(apiQuery);
		RDFNode node;
		String id = "";
		while(results.hasNext()) {
			DatasetApi dataset = new DatasetApi();
			QuerySolution solution = results.nextSolution();
			if (!id.equals(solution.get("uri").toString())) {
				id = solution.get("uri").toString();
				dataset.setIdentifier(solution.get("ident").toString());
				dataset.setTitle(solution.get("title").toString());
				dataset.setStorageTable(solution.get("storage").toString());
				node = solution.get("keywords");
				dataset.setKeywords(InsertNewDataset.convertLinkToWord(node.toString(), "keywords"));
				list.add(dataset);
			} else {
				int size = list.size();
				dataset = list.get(size-1);
				node = solution.get("keywords");
				dataset.setKeywords(dataset.getKeywords()+", "+InsertNewDataset.convertLinkToWord(node.toString(), "keywords"));
				list.remove(size-1);
				list.add(dataset);
			}
		}
		return list;
	}

	public static List<DatasetApi> apiSearchGeoLoc(String searchParam) {
		String[] token = searchParam.split(" -- ");
		String searchParamLink = convertWordToLink(token[0], "marineregions");
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
				"SELECT DISTINCT ?uri ?ident ?title ?geo_loc ?storage \n" + 
				"WHERE {  \n" + 
				"  ?uri dct:title ?title;\n" + 
				"       dct:identifier ?ident ;\n" + 
				"       bdo:storageTable ?storage ;\n" + 
				"       dct:spatial ?geo_loc." +
				values +
				"}" + 
				"ORDER BY ?uri \n" + 
				"LIMIT " + token[1];
		ResultSet results = QueryExecutor.selectQuery(apiQuery);
		String id = "";
		while(results.hasNext()) {
			DatasetApi dataset = new DatasetApi();
			QuerySolution solution = results.nextSolution();
			if (!id.equals(solution.get("uri").toString())) {
				id = solution.get("uri").toString();
				dataset.setIdentifier(solution.get("ident").toString());
				dataset.setTitle(solution.get("title").toString());
				dataset.setStorageTable(solution.get("storage").toString());
				dataset.setGeoLocation(InsertNewDataset.convertLinkToWord(solution.get("geo_loc").toString(), "marineregions"));
				list.add(dataset);
			} else {
				int size = list.size();
				dataset = list.get(size-1);
				dataset.setGeoLocation(dataset.getGeoLocation()+", "+InsertNewDataset.convertLinkToWord(solution.get("geo_loc").toString(), "marineregions"));
				list.remove(size-1);
				list.add(dataset);
			}
		}
		return list;
	}
	
	public static List<DatasetApi> apiSearchGeoCoverage(String searchParam) {
		String[] token = searchParam.split(" -- ");
		String[] listGeoLoc = token[0].split(",");
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
				"SELECT DISTINCT ?uri ?ident ?title (STR(?east) AS ?streast) (STR(?west) AS ?strwest) "
				+ "(STR(?south) AS ?strsouth) (STR(?north) AS ?strnorth) ?storage\n" + 
				"WHERE { \n" + 
				"  	?uri dct:title ?title;\n" + 
				"        dct:identifier ?ident ;\n" + 
				"        bdo:storageTable ?storage ;\n" + 
				"	     bdo:GeographicalCoverage ?geocov.\n" + 
				"   ?geocov a ignf:GeographicBoundingBox;\n" + 
				"	   ignf:eastBoundLongitude ?east;\n" + 
				"	   ignf:northBoundLatitude ?north;\n" + 
				"	   ignf:southBoundLatitude ?south;\n" + 
				"	   ignf:westBoundLongitude ?west.\n" + 
				"  FILTER ((?west>="+listGeoLoc[0]+" && ?east<="+listGeoLoc[1]+") && (?north<="+listGeoLoc[3]+" && ?south>="+listGeoLoc[2]+")) \n"+
				"}" + 
				"ORDER BY ?uri \n" + 
				"LIMIT " + token[1];
		ResultSet results = QueryExecutor.selectQuery(apiQuery);
		while(results.hasNext()) {
			DatasetApi dataset = new DatasetApi();
			QuerySolution solution = results.nextSolution();
			dataset.setIdentifier(solution.get("ident").toString());
			dataset.setTitle(solution.get("title").toString());
			dataset.setStorageTable(solution.get("storage").toString());
			dataset.setSpatialEast(solution.get("streast").toString());
			dataset.setSpatialNorth(solution.get("strnorth").toString());
			dataset.setSpatialSouth(solution.get("strsouth").toString());
			dataset.setSpatialWest(solution.get("strwest").toString());
			list.add(dataset);
			
		}
		return list;
	}

	public static List<DatasetApi> apiSearchVerticalCoverage (String searchParam){
		String[] token = searchParam.split(" -- ");
		String[] listVert = token[0].split(",");
		List<DatasetApi> list = new ArrayList<>();
		String apiQuery = "PREFIX disco: <http://rdf-vocabulary.ddialliance.org/discovery#>\n" + 
				"PREFIX skos: <http://www.w3.org/2004/02/skos/core#>\n" + 
				"PREFIX dct: <http://purl.org/dc/terms/>\n" + 
				"PREFIX dcat: <https://www.w3.org/TR/vocab-dcat/>\n" + 
				"PREFIX bdo: <http://bigdataocean.eu/bdo/>\n" + 
				"PREFIX ids: <http://industrialdataspace/information-model/>\n" + 
				"PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n" + 
				"\n" + 
				"SELECT distinct ?uri ?ident ?title (STR (?vertC_from) AS ?from) (STR (?vertC_to) AS ?to) ?storage \n" + 
				"WHERE {  \n" + 
				"  ?uri dct:title ?title;\n" + 
				"       dct:identifier ?ident ;\n" + 
				"       bdo:storageTable ?storage ;\n" + 
				"       bdo:verticalCoverage ?vertC.\n" + 
				"  ?vertC a bdo:VerticalCoverage;\n" + 
				"         bdo:verticalFrom ?vertC_from;\n" + 
				"         bdo:verticalTo ?vertC_to.\n" +
				"  FILTER (?vertC_from >= \""+listVert[0]+"\"^^xsd:double)\n" +
				"  FILTER (?vertC_to <= \""+listVert[1]+"\"^^xsd:double)\n" + 
				"}" + 
				"ORDER BY ?uri \n" + 
				"LIMIT " + token[1];
		
		ResultSet results = QueryExecutor.selectQuery(apiQuery);
		while(results.hasNext()){			
			DatasetApi dataset = new DatasetApi();
			QuerySolution solution = results.nextSolution();
			dataset.setIdentifier(solution.get("ident").toString());
			dataset.setTitle(solution.get("title").toString());
			dataset.setStorageTable(solution.get("storage").toString());
			dataset.setVerticalCoverageFrom(solution.get("from").toString());
			dataset.setVerticalCoverageTo(solution.get("to").toString());
			list.add(dataset);
		}
		return list;
	}

	public static List<DatasetApi> apiSearchTemporalCoverage (String searchParam){
		String[] token = searchParam.split(" -- ");
		String[] listTime = token[0].split(",");
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
					"SELECT distinct ?uri ?ident ?title (STR (?timeC_start) AS ?start) (STR (?timeC_end) AS ?end) ?storage \n" + 
					"WHERE {  \n" + 
					"  ?uri dct:title ?title;\n" +  
					"       dct:identifier ?ident ;\n" + 
					"       bdo:storageTable ?storage ;\n" + 
					"       bdo:timeCoverage ?timeC.\n" + 
					"  ?timeC a bdo:TimeCoverage;\n" + 
					"         ids:beginning ?timeC_start;\n" + 
					"         ids:end ?timeC_end.\n" + 
					"  FILTER (?timeC_start >= \""+listTime[0]+"\"^^xsd:dateTime)\n" +
					"  FILTER (?timeC_end < \""+listTime[1]+"\"^^xsd:dateTime)\n" + 
					"}" + 
					"ORDER BY ?uri \n" + 
					"LIMIT " + token[1];
		}else {
			apiQuery = "PREFIX disco: <http://rdf-vocabulary.ddialliance.org/discovery#>\n" + 
					"PREFIX skos: <http://www.w3.org/2004/02/skos/core#>\n" + 
					"PREFIX dct: <http://purl.org/dc/terms/>\n" + 
					"PREFIX dcat: <https://www.w3.org/TR/vocab-dcat/>\n" + 
					"PREFIX bdo: <http://bigdataocean.eu/bdo/>\n" + 
					"PREFIX ids: <http://industrialdataspace/information-model/>\n" + 
					"PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n" + 
					"\n" + 
					"SELECT distinct ?uri ?ident ?title (STR (?timeC_start) AS ?start) (STR (?timeC_end) AS ?end) ?storage \n" + 
					"WHERE {  \n" + 
					"  ?uri dct:title ?title;\n" + 
					"       dct:identifier ?ident ;\n" + 
					"       bdo:storageTable ?storage ;\n" + 
					"       bdo:timeCoverage ?timeC.\n" + 
					"  ?timeC a bdo:TimeCoverage;\n" + 
					"         ids:beginning ?timeC_start;\n" + 
					"         ids:end ?timeC_end.\n" + 
					"  FILTER (?timeC_start >= \""+listTime[0]+"\"^^xsd:dateTime)\n }"  + 
					"ORDER BY ?uri \n" + 
					"LIMIT " + token[1];
		}

		ResultSet results = QueryExecutor.selectQuery(apiQuery);
		while(results.hasNext()){			
			DatasetApi dataset = new DatasetApi();
			QuerySolution solution = results.nextSolution();		
			dataset.setIdentifier(solution.get("ident").toString());
			dataset.setTitle(solution.get("title").toString());
			dataset.setStorageTable(solution.get("storage").toString());
			dataset.setTemporalCoverageBegin(solution.get("start").toString());
			dataset.setTemporalCoverageEnd(solution.get("end").toString());
			list.add(dataset);
		}
		return list;
	}
	
	public static DatasetApi apiListFileDatasetVariables (String searchParam) {
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
				"SELECT distinct ?uri ?ident ?title ?nameVariable (STR(?label) AS ?canonicalVariable) ?unit ?storage \n" + 
				"WHERE {  \n" + 
				"  bdo:"+searchParam+" disco:variable ?variable;\n" + 
				"       dct:identifier ?ident ;\n" + 
				"       bdo:storageTable ?storage ;\n" + 
				"       dct:title ?title.\n" + 
				"  ?variable a bdo:BDOVariable;\n" + 
				"      dct:identifier ?nameVariable;\n" +
				"      skos:prefLabel ?label;\n" + 
				"      bdocm:canonicalUnit ?unit. \n" +
				"}";
		ResultSet results = QueryExecutor.selectQuery(apiQuery);
		VariableDataset varData;
		while(results.hasNext()) {					
			QuerySolution solution = results.nextSolution();			
			dataset.setIdentifier(solution.get("ident").toString());	
			dataset.setTitle(solution.get("title").toString());
			dataset.setStorageTable(solution.get("storage").toString());
			varData = unitVariableisNull(solution);
			variables.add(varData);
			dataset.setVariables(variables);
		}
		return dataset;
	}
	
	public static DatasetApi apiListDatasetVariables (String searchParam) {
		DatasetApi dataset = new DatasetApi();
		List<VariableDataset> variables = new ArrayList<>();
		String uri = getUrifromStorageTable(searchParam);
		if(uri != null) {
			String apiQuery = "PREFIX disco: <http://rdf-vocabulary.ddialliance.org/discovery#>\n" + 
					"PREFIX skos: <http://www.w3.org/2004/02/skos/core#>\n" + 
					"PREFIX dct: <http://purl.org/dc/terms/>\n" + 
					"PREFIX dcat: <https://www.w3.org/TR/vocab-dcat/>\n" + 
					"PREFIX bdo: <http://bigdataocean.eu/bdo/>\n" + 
					"PREFIX bdocm: <http://www.bigdataocean.eu/standards/canonicalmodel#>\n" +
					"\n" + 
					"\n" + 
					"SELECT ?title ?nameVariable (STR(?label) AS ?canonicalVariable) ?unit\n" + 
					"WHERE {  \n" + 
					"  bdo:" + uri + " disco:variable ?variable ;\n" + 
					"       dct:title ?title.\n" + 
					"  ?variable a bdo:BDOVariable;\n" + 
					"      dct:identifier ?nameVariable;\n" +
					"      skos:prefLabel ?label;\n" + 
					"      bdocm:canonicalUnit ?unit. \n" +
					"}";
			ResultSet results = QueryExecutor.selectQuery(apiQuery);
			VariableDataset varData;
			while(results.hasNext()) {			
				QuerySolution solution = results.nextSolution();
				if(dataset.getIdentifier().isEmpty() || dataset.getIdentifier().equals(uri)) {
					dataset.setIdentifier(uri);		
					dataset.setTitle(solution.get("title").toString());
					dataset.setStorageTable(searchParam);
					varData = unitVariableisNull(solution);
					variables.add(varData);
					dataset.setVariables(variables);
				} else {
					break;
				}
			}
			dataset.setIdentifier(null);
		}
		return dataset;
	}
	
	private static String getUrifromStorageTable(String storage) {
		String query = "PREFIX disco: <http://rdf-vocabulary.ddialliance.org/discovery#>\n" + 
				"PREFIX skos: <http://www.w3.org/2004/02/skos/core#>\n" + 
				"PREFIX dct: <http://purl.org/dc/terms/>\n" + 
				"PREFIX dcat: <https://www.w3.org/TR/vocab-dcat/>\n" + 
				"PREFIX bdo: <http://bigdataocean.eu/bdo/>\n" + 
				"PREFIX bdocm: <http://www.bigdataocean.eu/standards/canonicalmodel#>\n" +
				"\n" + 
				"\n" + 
				"SELECT ?ident \n" + 
				"WHERE {  \n" + 
				"  ?uri dct:identifier ?ident ;\n" + 
				"       bdo:storageTable '"+storage+"'.\n" + 
				"}" +
				"LIMIT 1";
		ResultSet results = QueryExecutor.selectQuery(query);
		while(results.hasNext()) {			
			QuerySolution solution = results.nextSolution();	
			return solution.get("ident").toString();
		}
		return null;
	}
	
	public static List<DatasetApi> apiSearchVariable (String searchParam) {
		String[] token = searchParam.split(" -- ");
		List<DatasetApi> list = new ArrayList<>();
		String[] listV = token[0].split(",");
		
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
				"SELECT distinct ?uri ?ident ?title ?nameVariable (STR(?var) AS ?canonicalVariable) ?unit ?storage \n" + 
				"WHERE {\n" + 
				"  ?uriVar a bdo:BDOVariable;\n" + 
				"      dct:identifier ?nameVariable;\n "+
				"      skos:prefLabel ?var;\n" + 
				"      bdocm:canonicalUnit ?unit. \n" +
				values + 
				"  ?uri disco:variable ?uriVar;\n" + 
				"       dct:identifier ?ident ;\n" + 
				"       bdo:storageTable ?storage ;\n" + 
				"       dct:title ?title.\n" + 
				"}\n" +
				"ORDER BY ?uri \n" +
				"LIMIT " + token[1];
		
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
				dataset.setIdentifier(solution.get("ident").toString());
				id = node.toString();
				dataset.setTitle(solution.get("title").toString());
				dataset.setStorageTable(solution.get("storage").toString());
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
				dataset.setVariables(variables);
			}
		}
		return list;
	}
	
	public static List<DatasetApi> apiListFileDatasetofDataset (String searchParam){
		String[] token = searchParam.split(" -- ");
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
				"SELECT ?uri ?ident ?title ?desc \n" + 
				"WHERE { \n" + 
				"  ?uri a dcat:Dataset; \n" + 
				"       dct:identifier ?ident ;\n" + 
				"       dct:title ?title; \n" + 
				"       dct:description ?desc ;\n" + 
				"       bdo:storageTable '" + token[0] +"' .\n" + 
				"}ORDER BY ?uri \n" + 
				"LIMIT " + token[1];
		ResultSet results = QueryExecutor.selectQuery(apiQuery);
		while(results.hasNext()) {
			DatasetApi dataset = new DatasetApi();
			QuerySolution solution = results.nextSolution();
			dataset.setIdentifier(solution.get("ident").toString());
			dataset.setTitle(solution.get("title").toString());
			dataset.setDescription(solution.get("desc").toString());
			dataset.setStorageTable(token[0]);
			list.add(dataset);
		}
		return list;
	}
	
	public static List<DatasetApi> apiSearchTitle (String searchParam){
		String[] token = searchParam.split(" -- ");
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
				"SELECT DISTINCT ?uri ?ident ?title ?desc ?storage  \n" + 
				"WHERE { \n" + 
				"  ?uri a dcat:Dataset; \n" + 
				"       dct:identifier ?ident ;\n" + 
				"       dct:title ?title; \n" + 
				"       dct:description ?desc ;\n" + 
				"       bdo:storageTable ?storage .\n" + 
				"  FILTER regex(?title, '"+ token[0] +"', 'i') \n" + 
				"}" +
				"ORDER BY ?uri \n" +
				"LIMIT " + token[1];
		ResultSet results = QueryExecutor.selectQuery(apiQuery);
		while(results.hasNext()) {
			DatasetApi dataset = new DatasetApi();
			QuerySolution solution = results.nextSolution();	
			dataset.setIdentifier(solution.get("ident").toString());
			dataset.setTitle(solution.get("title").toString());
			dataset.setDescription(solution.get("desc").toString());
			dataset.setStorageTable(solution.get("storage").toString());
			list.add(dataset);	
		}
		return list;
	}
	
	public static List<DatasetApi> apiSearchDescription (String searchParam){
		String[] token = searchParam.split(" -- ");
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
				"SELECT ?uri ?ident ?title ?desc ?storage \n" + 
				"WHERE { \n" + 
				"  ?uri a dcat:Dataset; \n" + 
				"       dct:identifier ?ident ;\n" + 
				"       dct:title ?title; \n" + 
				"       dct:description ?desc ;\n" + 
				"       bdo:storageTable ?storage .\n" + 
				"  FILTER regex(?desc, '"+ token[0] +"', 'i') \n" + 
				"}" +
				"ORDER BY ?uri \n" +
				"LIMIT " + token[1];
		ResultSet results = QueryExecutor.selectQuery(apiQuery);
		while(results.hasNext()) {
			DatasetApi dataset = new DatasetApi();
			QuerySolution solution = results.nextSolution();	
			dataset.setIdentifier(solution.get("ident").toString());
			dataset.setTitle(solution.get("title").toString());
			dataset.setDescription(solution.get("desc").toString());
			dataset.setStorageTable(solution.get("storage").toString());
			list.add(dataset);	
		}
		return list;
	}
	
	public static DatasetApi apiInfoDataset (String searchParam) {
		String uri = "";
		String queryMetadata = "PREFIX dct: <http://purl.org/dc/terms/>\n" + 
				"PREFIX dcat: <https://www.w3.org/TR/vocab-dcat/>\n" + 
				"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> \n" + 
				"PREFIX ignf: <http://data.ign.fr/def/ignf#>\n" + 
				"PREFIX foaf: <http://xmlns.com/foaf/0.1/>\n" + 
				"PREFIX bdo: <http://bigdataocean.eu/bdo/>\n" + 
				"PREFIX ids: <http://industrialdataspace/information-model/>\n" + 
				"SELECT ?ident ?title ?desc ?standard ?format ?homep ?publi ?rights ?license "
				+ "?timeReso (STR(?verFrom) AS ?vFrom) (STR(?verTo) AS ?vTo) (STR(?west) AS ?spatialWest) "
				+ "(STR(?east) AS ?spatialEast) (STR(?south) AS ?spatialSouth) (STR(?north) AS ?spatialNorth) "
				+ "?vLevel ?coorSys ?source ?observation \n" + 
				"WHERE{ \n" + 
				"  ?uri a dcat:Dataset ;\n" + 
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
				"       dct:license ?license ; \n" + 
				"       dct:accessRights ?rights ;\n" + 
				"       bdo:timeResolution ?timeReso ;\n" + 
				"       bdo:GeographicalCoverage ?spatial ;\n" + 
				"       dct:creator ?source ; \n" +
				"       rdfs:comment ?observation ; \n" +
				"       bdo:storageTable '"+searchParam+"' ; \n" +
				"       bdo:verticalLevel ?vLevel ;\n" + 
				"       dct:conformsTo ?coorSys .\n" + 
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
				"}" + 
				"LIMIT 1";
		
		DatasetApi dataset = new DatasetApi();
		dataset.setIdentifier(null);
		RDFNode node;
		// executes query on Jena Fueski to get Metadata
		ResultSet results = QueryExecutor.selectQuery(queryMetadata);
		try {
			while(results.hasNext()){
				QuerySolution solution = results.nextSolution();
				uri = "bdo:" + solution.get("ident").toString();
				node = solution.get("title");
				dataset.setTitle(node.toString());
				node = solution.get("desc");
				dataset.setDescription(node.toString());
				node = solution.get("standard");
				dataset.setStandards(node.toString());
				node = solution.get("format");
				dataset.setFormats(node.toString());
				node = solution.get("homep");
				dataset.setHomepage(node.toString());
				node = solution.get("publi");
				dataset.setPublisher(node.toString());
				node = solution.get("license");
				dataset.setLicense(node.toString());
				node = solution.get("rights");
				dataset.setAccessRights(node.toString());
				node = solution.get("source");
				dataset.setSource(node.toString());
				node = solution.get("observation");
				dataset.setObservations(node.toString());
				dataset.setStorageTable(searchParam);
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
				node = solution.get("vLevel");
				dataset.setVerticalLevel(node.toString());
				node = solution.get("coorSys");
				dataset.setCoordinateSystem(node.toString());
			}

			if (results.getRowNumber() > 0) {
				dataset = getTemporalCoverage(searchParam, dataset);
				dataset = getSubject(uri, dataset);
				dataset = getKeywords(uri, dataset);
				dataset = getGeoLoc(uri, dataset);
				dataset = getLanguage(uri, dataset);
				dataset = getVariables(uri, dataset);
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		return dataset;
	}
	
	public static List<DatasetApi> apiListAllDatasets (){
		String pathFile = Constants.CONFIGFILEPATH+"/Frontend/Flask/static/json/storageTable.json";
		JSONParser parser = new JSONParser();
		List<DatasetApi> list = new ArrayList<>();
		try {
			JSONArray storageTable = (JSONArray) parser.parse(new FileReader(pathFile));
			String storage = "";
			for(int j=0; j<storageTable.size(); j++){
				JSONObject tokenJson = (JSONObject) storageTable.get(j);
				storage = tokenJson.get("tableName").toString();
				DatasetApi dataset = new DatasetApi();
				dataset = apiInfoDataset(storage);
				if (dataset.getTitle() != null) {
					list.add(dataset);
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	
	public static List<DateTime> sortListDateTime(List<DateTime> list) {
		list.sort((o1,o2) -> o1.compareTo(o2));
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
	  			            if(jsonObject.get("text").toString().equalsIgnoreCase(token.toLowerCase())) {
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
	
	private static DatasetApi getSubject(String uri, DatasetApi dataset) {
		String query = "PREFIX dct: <http://purl.org/dc/terms/>\n" + 
				"PREFIX dcat: <https://www.w3.org/TR/vocab-dcat/>\n" + 
				"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> \n" + 
				"PREFIX ignf: <http://data.ign.fr/def/ignf#>\n" + 
				"PREFIX foaf: <http://xmlns.com/foaf/0.1/>\n" + 
				"PREFIX bdo: <http://bigdataocean.eu/bdo/>\n" + 
				"PREFIX ids: <http://industrialdataspace/information-model/>\n" + 
				"SELECT ?sub  \n" + 
				"WHERE{ \n" + 
				"  "+uri+" a dcat:Dataset ;\n"+ 
				"          dcat:subject ?sub." +
				"}";
		ResultSet results = QueryExecutor.selectQuery(query);
		while(results.hasNext()){
			QuerySolution solution = results.nextSolution();
			if(dataset.getSubject() != null)
			{
				dataset.setSubject(dataset.getSubject()+", "+InsertNewDataset.convertLinkToWord(solution.get("sub").toString(), "subject"));
			}else {
				dataset.setSubject(InsertNewDataset.convertLinkToWord(solution.get("sub").toString(), "subject"));
			}
		}
		return dataset;
	}
	
	private static DatasetApi getKeywords(String uri, DatasetApi dataset) {
		String query = "PREFIX dct: <http://purl.org/dc/terms/>\n" + 
				"PREFIX dcat: <https://www.w3.org/TR/vocab-dcat/>\n" + 
				"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> \n" + 
				"PREFIX ignf: <http://data.ign.fr/def/ignf#>\n" + 
				"PREFIX foaf: <http://xmlns.com/foaf/0.1/>\n" + 
				"PREFIX bdo: <http://bigdataocean.eu/bdo/>\n" + 
				"PREFIX ids: <http://industrialdataspace/information-model/>\n" + 
				"SELECT ?keyw  \n" + 
				"WHERE{ \n" + 
				"  "+uri+" a dcat:Dataset ;\n"+ 
				"          dcat:theme ?keyw." +
				"}";
		ResultSet results = QueryExecutor.selectQuery(query);
		while(results.hasNext()){
			QuerySolution solution = results.nextSolution();
			if(dataset.getKeywords() != null)
			{
				dataset.setKeywords(dataset.getKeywords()+", "+InsertNewDataset.convertLinkToWord(solution.get("keyw").toString(), "keywords"));
			}else {
				dataset.setKeywords(InsertNewDataset.convertLinkToWord(solution.get("keyw").toString(), "keywords"));
			}
		}
		return dataset;
	}
	
	private static DatasetApi getGeoLoc(String uri, DatasetApi dataset) {
		String query = "PREFIX dct: <http://purl.org/dc/terms/>\n" + 
				"PREFIX dcat: <https://www.w3.org/TR/vocab-dcat/>\n" + 
				"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> \n" + 
				"PREFIX ignf: <http://data.ign.fr/def/ignf#>\n" + 
				"PREFIX foaf: <http://xmlns.com/foaf/0.1/>\n" + 
				"PREFIX bdo: <http://bigdataocean.eu/bdo/>\n" + 
				"PREFIX ids: <http://industrialdataspace/information-model/>\n" + 
				"SELECT ?geoLoc  \n" + 
				"WHERE{ \n" + 
				"  "+uri+" a dcat:Dataset .\n"+ 
				"OPTIONAL {"+uri+" dct:spatial ?geoLoc .}" +
				"}";
		ResultSet results = QueryExecutor.selectQuery(query);
		while(results.hasNext()){
			QuerySolution solution = results.nextSolution();
			//if node = null then setGeoLocation to ""
			if(solution.get("geoLoc")!=null) {
				if(dataset.getGeoLocation() == null)
				{
					dataset.setGeoLocation(InsertNewDataset.convertLinkToWord(solution.get("geoLoc").toString(), "marineregions"));
				}else {
					dataset.setGeoLocation(dataset.getGeoLocation()+", "+InsertNewDataset.convertLinkToWord(solution.get("geoLoc").toString(), "marineregions"));				
				}
			}else {
				dataset.setGeoLocation("");
			}
		}
		return dataset;
	}
	
	private static DatasetApi getLanguage(String uri, DatasetApi dataset) {
		String query = "PREFIX dct: <http://purl.org/dc/terms/>\n" + 
				"PREFIX dcat: <https://www.w3.org/TR/vocab-dcat/>\n" + 
				"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> \n" + 
				"PREFIX ignf: <http://data.ign.fr/def/ignf#>\n" + 
				"PREFIX foaf: <http://xmlns.com/foaf/0.1/>\n" + 
				"PREFIX bdo: <http://bigdataocean.eu/bdo/>\n" + 
				"PREFIX ids: <http://industrialdataspace/information-model/>\n" + 
				"SELECT ?lang  \n" + 
				"WHERE{ \n" + 
				"  "+uri+" a dcat:Dataset ;\n"+ 
				"          dct:language ?lang ." +
				"}";
		ResultSet results = QueryExecutor.selectQuery(query);
		while(results.hasNext()){
			QuerySolution solution = results.nextSolution();
			if(dataset.getLanguage() != null)
			{
				dataset.setLanguage(dataset.getLanguage()+", "+solution.get("lang").toString());
			}else {
				dataset.setLanguage(solution.get("lang").toString());
			}
		}
		return dataset;
	}
	
	private static DatasetApi getTemporalCoverage(String storage, DatasetApi dataset) {
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		List<DateTime> tCBegin = new ArrayList<>();
		List<DateTime> tCEnd = new ArrayList<>();
		try {
			String query = "PREFIX bdo: <http://bigdataocean.eu/bdo/>\n" + 
					"PREFIX ids: <http://industrialdataspace/information-model/>\n" + 
					"PREFIX dcat: <https://www.w3.org/TR/vocab-dcat/>\n" + 
					"SELECT (STR(?tempCovB) AS ?timeCovBeg) (STR(?tempCovE) AS ?timeCovEnd)\n" + 
					"WHERE {\n" + 
					"  ?uri a dcat:Dataset ;\n" + 
					"       bdo:storageTable '" + storage + "' ; \n" + 
					"  	   bdo:timeCoverage ?temp .\n" + 
					"  ?temp a bdo:TimeCoverage;\n" + 
					"		ids:beginning ?tempCovB ;\n" + 
					"       ids:end ?tempCovE .\n" + 
					"}";
			ResultSet results = QueryExecutor.selectQuery(query);
			while(results.hasNext()){
				QuerySolution solution = results.nextSolution();
				dataset.setTemporalCoverageBegin(solution.get("timeCovBeg").toString());
				if(!solution.get("timeCovBeg").toString().equals("")) {
					tCBegin.add(new DateTime(format.parse(solution.get("timeCovBeg").toString())));
				}
				if(!solution.get("timeCovEnd").toString().equals("")) {
					tCEnd.add(new DateTime(format.parse(solution.get("timeCovEnd").toString())));
				}
				dataset.setTemporalCoverageEnd(solution.get("timeCovEnd").toString());
			}
			if (results.getRowNumber() > 0) {
				// Search the real temporal coverage begin of the dataset
				if(!tCBegin.isEmpty()) {
					tCBegin = BdoApiAnalyser.sortListDateTime(tCBegin);
					dataset.setTemporalCoverageBegin(format.format(tCBegin.get(0).toDate()));
				}
				// Search the real temporal coverage end of the dataset
				if(!tCEnd.isEmpty()) {
					tCEnd = BdoApiAnalyser.sortListDateTime(tCEnd);
					int size = tCEnd.size() - 1;
					dataset.setTemporalCoverageEnd(format.format(tCEnd.get(size).toDate()));
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return dataset;
	}
	
	private static DatasetApi getVariables (String uri, DatasetApi dataset) {
		List<VariableDataset> variables = new ArrayList<>();
		
		String queryVariables = "PREFIX disco: <http://rdf-vocabulary.ddialliance.org/discovery#>\n" +
				"PREFIX dct: <http://purl.org/dc/terms/>\n" +
				"PREFIX bdo: <http://bigdataocean.eu/bdo/>\n" +
				"PREFIX bdocm: <http://www.bigdataocean.eu/standards/canonicalmodel#>\n" +
				"PREFIX skos: <http://www.w3.org/2004/02/skos/core#>\n" +
				"SELECT ?nameVariable (STR(?label) AS ?canonicalVariable) ?unit\n" + 
				"WHERE {\n" +
				"  "+uri+" disco:variable ?variable.\n" + 
				"  ?variable a bdo:BDOVariable;\n" + 
				"      dct:identifier ?nameVariable;\n" +
				"      skos:prefLabel ?label;\n" + 
				"      bdocm:canonicalUnit ?unit. \n" +
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

}

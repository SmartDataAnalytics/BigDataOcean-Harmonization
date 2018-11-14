package org.unibonn.bdo.bdodatasets;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.unibonn.bdo.connections.QueryExecutor;
import org.unibonn.bdo.objects.Dataset;

import com.google.gson.Gson;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.RDFNode;

/**
 *  
 * @author Jaime M Trillos
 *
 * Receives 1 parameter, the storage table of the Dataset to get all metadata from Jena Fuseki
 *
 */

public class GetDatasetMetadata {
	
	public static void main(String[] args) {
		String storage = args[0];
		exec(storage);
	}

	public static void exec(String storage) {
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		Dataset dataset = new Dataset();
		List<DateTime> tCBegin = new ArrayList<>();
		List<DateTime> tCEnd = new ArrayList<>();
		String uri = "";
		try {
			if (askUriExist(storage)){
				String queryMetadata = "PREFIX dct: <http://purl.org/dc/terms/>\n" + 
						"PREFIX dcat: <https://www.w3.org/TR/vocab-dcat/>\n" + 
						"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> \n" + 
						"PREFIX ignf: <http://data.ign.fr/def/ignf#>\n" + 
						"PREFIX foaf: <http://xmlns.com/foaf/0.1/>\n" + 
						"PREFIX bdo: <http://bigdataocean.eu/bdo/>\n" + 
						"PREFIX ids: <http://industrialdataspace/information-model/>\n" + 
						"SELECT ?uri ?ident ?title ?desc ?standard ?format ?homep "
						+ "?publi ?rights (STR(?issued) AS ?issuedDate)  (STR(?modified) AS ?modifiedDate) "
						+ "?timeReso (STR(?verFrom) AS ?vFrom) (STR(?verTo) AS ?vTo) (STR(?west) AS ?spatialWest) "
						+ "(STR(?east) AS ?spatialEast) (STR(?south) AS ?spatialSouth) (STR(?north) AS ?spatialNorth) "
						+ "(STR(?tempCovB) AS ?timeCovBeg) (STR(?tempCovE) AS ?timeCovEnd) ?vLevel ?coorSys ?source "
						+ "?observation \n" + 
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
						"       dct:accessRights ?rights ;\n" + 
						"       dct:issued ?issued ;\n" + 
						"       dct:modified ?modified ;\n" + 
						"       bdo:timeResolution ?timeReso ;\n" + 
						"       bdo:GeographicalCoverage ?spatial ;\n" + 
						"       dct:creator ?source ; \n" +
						"       rdfs:comment ?observation ; \n" +
						"       bdo:storageTable '" + storage + "' ; \n" +
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
				
				// executes query on Jena Fueski to get Metadata
				ResultSet results = QueryExecutor.selectQuery(queryMetadata);
				while(results.hasNext()){
					QuerySolution solution = results.nextSolution();
					uri = "bdo:" + solution.get("ident").toString();
					dataset.setIdentifier(solution.get("ident").toString());
					dataset.setTitle(solution.get("title").toString());
					dataset.setDescription(solution.get("desc").toString());
					dataset.setStandards(solution.get("standard").toString());
					dataset.setFormats(solution.get("format").toString());
					dataset.setHomepage(solution.get("homep").toString());
					dataset.setPublisher(solution.get("publi").toString());
					dataset.setAccessRights(solution.get("rights").toString());
					dataset.setSource(solution.get("source").toString());
					dataset.setObservations(solution.get("observation").toString());
					dataset.setStorageTable(storage);
					dataset.setVerticalCoverageFrom(solution.get("vFrom").toString());
					dataset.setVerticalCoverageTo(solution.get("vTo").toString());
					dataset.setTimeResolution(solution.get("timeReso").toString());
					dataset.setSpatialWest(solution.get("spatialWest").toString());
					dataset.setSpatialEast(solution.get("spatialEast").toString());
					dataset.setSpatialNorth(solution.get("spatialNorth").toString());
					dataset.setSpatialSouth(solution.get("spatialSouth").toString());
					dataset.setTemporalCoverageBegin(solution.get("timeCovBeg").toString());
					if(!solution.get("timeCovBeg").toString().equals("")) {
						tCBegin.add(new DateTime(format.parse(solution.get("timeCovBeg").toString())));
					}
					if(!solution.get("timeCovEnd").toString().equals("")) {
						tCEnd.add(new DateTime(format.parse(solution.get("timeCovEnd").toString())));
					}
					dataset.setTemporalCoverageEnd(solution.get("timeCovEnd").toString());
					dataset.setVerticalLevel(solution.get("vLevel").toString());
					dataset.setCoordinateSystem(solution.get("coorSys").toString());
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
					
					dataset = getSubject(uri, dataset);
					dataset = getKeywords(uri, dataset);
					dataset = getGeoLoc(uri, dataset);
					dataset = getLanguage(uri, dataset);
					dataset = getVariables(uri, dataset);
				}
			}else {
				List<String> listVariables = new ArrayList<>() ;
				dataset.setVariable(listVariables);
			}
			// Parse into JSON the Dataset instance with all metadata from a dataset
			Gson gson  = new Gson();
			System.out.println(gson.toJson(dataset));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static Dataset getSubject(String uri, Dataset dataset) {
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
			if(dataset.getSubject() != "")
			{
				dataset.setSubject(dataset.getSubject()+", "+solution.get("sub").toString());
			}else {
				dataset.setSubject(solution.get("sub").toString());
			}
		}
		return dataset;
	}
	
	private static Dataset getKeywords(String uri, Dataset dataset) {
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
			if(dataset.getKeywords() != "")
			{
				dataset.setKeywords(dataset.getKeywords()+", "+solution.get("keyw").toString());
			}else {
				dataset.setKeywords(solution.get("keyw").toString());
			}
		}
		return dataset;
	}
	
	private static Dataset getGeoLoc(String uri, Dataset dataset) {
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
				if(dataset.getGeoLocation() == "")
				{
					dataset.setGeoLocation(solution.get("geoLoc").toString());
				}else {
					dataset.setGeoLocation(dataset.getGeoLocation()+", "+solution.get("geoLoc").toString());				
				}
			}else {
				dataset.setGeoLocation("");
			}
		}
		return dataset;
	}
	
	private static Dataset getLanguage(String uri, Dataset dataset) {
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
			if(dataset.getLanguage() != "" && !dataset.getLanguage().equals("eng"))
			{
				dataset.setLanguage(dataset.getLanguage()+", "+solution.get("lang").toString());
			}else {
				dataset.setLanguage(solution.get("lang").toString());
			}
		}
		return dataset;
	}
	
	private static Dataset getVariables (String uri, Dataset dataset) {
		List<String> listVariables = new ArrayList<>() ;
		RDFNode node2;
		RDFNode node3;
		RDFNode node4;
		RDFNode node5;
		
		String queryVariables = "PREFIX dct: <http://purl.org/dc/terms/>\n" +
				"PREFIX bdo: <http://bigdataocean.eu/bdo/>\n" +
				"PREFIX bdocm: <http://www.bigdataocean.eu/standards/canonicalmodel#>\n" +
				"PREFIX skos: <http://www.w3.org/2004/02/skos/core#>\n" +
				"PREFIX owl: <http://www.w3.org/2002/07/owl#> \n" + 
				"SELECT ?uri ?identifierVariable (STR(?prefLabel) AS ?label) ?unit ?url\n" + 
				"WHERE {\n" + 
				"  "+uri+" ?predicate ?object .\n" + 
				"  ?object a bdo:BDOVariable ;\n" + 
				"        dct:identifier ?identifierVariable ;\n" + 
				"        owl:sameAs ?url ;\n" + 
				"        skos:prefLabel ?prefLabel .\n" + 
				"  OPTIONAL { ?object bdocm:canonicalUnit ?unit } \n" + 
				"  FILTER(lang(?prefLabel) = \"en\")\n" + 
				"}";
		
		// Adding Datasetvariables -- BDOvariables in a list
		ResultSet rsVariables = QueryExecutor.selectQuery(queryVariables);
		while(rsVariables.hasNext()){
			QuerySolution solution = rsVariables.nextSolution();
			node2 = solution.get("identifierVariable");
			node3 = solution.get("label");
			node4 = solution.get("unit");
			node5 = solution.get("url");

			if(node4 == null) {
				listVariables.add(node2.toString() + " --  -- "+ node3.toString()+ " -- "+ node5.toString());
			} else {
				listVariables.add(node2.toString() + " -- "+ node4.toString() + " -- "+ node3.toString()+ " -- "+ node5.toString());
			}
			
		}
		
		dataset.setVariable(listVariables);
		return dataset;
	}
	
	//if true then exist, otherwise does not exist
	private static boolean askUriExist(String storage) {
		String query = "PREFIX dct: <http://purl.org/dc/terms/>\n" + 
				"PREFIX dcat: <https://www.w3.org/TR/vocab-dcat/>\n" + 
				"PREFIX bdo: <http://bigdataocean.eu/bdo/>\n" + 
				"ASK { \n" + 
				"  ?uri a dcat:Dataset ;\n"+ 
				"          bdo:storageTable '" + storage + "' ." +
				"}";
		return QueryExecutor.askQuery(query); 
	}
}

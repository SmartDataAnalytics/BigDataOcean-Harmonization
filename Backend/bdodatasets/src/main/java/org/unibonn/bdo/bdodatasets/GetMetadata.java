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
 * Receives 1 parameter, the URI of the Dataset to get all metadata from Jena Fuseki
 *
 */

public class GetMetadata {
	
	public static void main(String[] args) {
		String uri = args[0];
		exec(uri);

	}

	public static void exec(String uri) {
		Dataset dataset = new Dataset();
		if (askUriExist(uri)){
			String queryMetadata = "PREFIX dct: <http://purl.org/dc/terms/>\n" + 
					"PREFIX dcat: <https://www.w3.org/TR/vocab-dcat/>\n" + 
					"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> \n" + 
					"PREFIX ignf: <http://data.ign.fr/def/ignf#>\n" + 
					"PREFIX foaf: <http://xmlns.com/foaf/0.1/>\n" + 
					"PREFIX bdo: <http://bigdataocean.eu/bdo/>\n" + 
					"PREFIX ids: <http://industrialdataspace/information-model/>\n" + 
					"SELECT ?ident ?title ?desc ?standard ?format ?homep ?license "
					+ "?publi ?rights (STR(?issued) AS ?issuedDate)  (STR(?modified) AS ?modifiedDate) "
					+ "?timeReso (STR(?verFrom) AS ?vFrom) (STR(?verTo) AS ?vTo) (STR(?west) AS ?spatialWest) "
					+ "(STR(?east) AS ?spatialEast) (STR(?south) AS ?spatialSouth) (STR(?north) AS ?spatialNorth) "
					+ "(STR(?tempCovB) AS ?timeCovBeg) (STR(?tempCovE) AS ?timeCovEnd) ?vLevel ?coorSys ?source "
					+ "?observation ?storageTable ?idFile \n" + 
					"WHERE{ \n" + 
					"  "+uri+" a dcat:Dataset ;\n" + 
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
					"       OPTIONAL {"+uri+" bdo:idFile ?idFile }\n" + 
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
				dataset.setIdentifier(solution.get("ident").toString());
				if(solution.get("idFile") != null) {
					dataset.setIdFile(solution.get("idFile").toString());
				} else {
					dataset.setIdFile("");
				}
				dataset.setTitle(solution.get("title").toString());
				dataset.setDescription(solution.get("desc").toString());
				dataset.setStandards(solution.get("standard").toString());
				dataset.setFormats(solution.get("format").toString());
				dataset.setHomepage(solution.get("homep").toString());
				dataset.setPublisher(solution.get("publi").toString());
				dataset.setLicense(solution.get("license").toString());
				dataset.setAccessRights(solution.get("rights").toString());
				dataset.setIssuedDate(solution.get("issuedDate").toString());
				dataset.setModifiedDate(solution.get("modifiedDate").toString());
				dataset.setSource(solution.get("source").toString());
				dataset.setObservations(solution.get("observation").toString());
				dataset.setStorageTable(solution.get("storageTable").toString());
				dataset.setVerticalCoverageFrom(solution.get("vFrom").toString());
				dataset.setVerticalCoverageTo(solution.get("vTo").toString());
				dataset.setTimeResolution(solution.get("timeReso").toString());
				dataset.setSpatialWest(solution.get("spatialWest").toString());
				dataset.setSpatialEast(solution.get("spatialEast").toString());
				dataset.setSpatialNorth(solution.get("spatialNorth").toString());
				dataset.setSpatialSouth(solution.get("spatialSouth").toString());
				dataset.setTemporalCoverageBegin(solution.get("timeCovBeg").toString());
				dataset.setTemporalCoverageEnd(solution.get("timeCovEnd").toString());
				dataset.setVerticalLevel(solution.get("vLevel").toString());
				dataset.setCoordinateSystem(solution.get("coorSys").toString());
			}
			
			dataset = getSubject(uri, dataset);
			dataset = getKeywords(uri, dataset);
			dataset = getGeoLoc(uri, dataset);
			dataset = getLanguage(uri, dataset);
			dataset = getVariables(uri, dataset);
		}else {
			List<String> listVariables = new ArrayList<>() ;
			dataset.setVariable(listVariables);
		}
		
		try {
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
				"PREFIX disco: <http://rdf-vocabulary.ddialliance.org/discovery#> \n" + 
				"SELECT ?uri ?identifierVariable (STR(?prefLabel) AS ?label) ?unit ?url\n" + 
				"WHERE {\n" + 
				"  "+uri+" disco:variable ?object .\n" + 
				"  ?object a bdo:BDOVariable ;\n" + 
				"        dct:identifier ?identifierVariable ;\n" + 
				"        owl:sameAs ?url ;\n" + 
				"        skos:prefLabel ?prefLabel ;\n" + 
				"        bdocm:canonicalUnit ?unit . \n" + 
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
	private static boolean askUriExist(String uri) {
		String query = "PREFIX dct: <http://purl.org/dc/terms/>\n" + 
				"PREFIX dcat: <https://www.w3.org/TR/vocab-dcat/>\n" + 
				"PREFIX bdo: <http://bigdataocean.eu/bdo/>\n" + 
				"ASK { \n" + 
				"  "+uri+" a dcat:Dataset ;\n"+ 
				"          dct:language ?lang ." +
				"}";
		return QueryExecutor.askQuery(query); 
	}
}

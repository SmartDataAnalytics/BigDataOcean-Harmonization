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
				+ "?observation ?storageTable\n" + 
				"WHERE{ \n" + 
				"  "+uri+" a dcat:Dataset ;\n" + 
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
				"}";
		
		Dataset dataset = new Dataset();
		dataset.setLanguage("");
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
		
		dataset = getSubject(uri, dataset);
		dataset = getKeywords(uri, dataset);
		dataset = getGeoLoc(uri, dataset);
		dataset = getLanguage(uri, dataset);
		dataset = getVariables(uri, dataset);
		
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
			if(dataset.getLanguage() != "")
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
}

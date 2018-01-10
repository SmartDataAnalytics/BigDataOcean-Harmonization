package org.unibonn.bdo.bdodatasets;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
	
	private final static Logger log = LoggerFactory.getLogger(GetMetadata.class);


	public static void main(String[] args) {
		String uri = args[0];
		//String uri = "<http://bigdataocean.eu/bdo/MEDSEA_ANALYSIS_FORECAST_PHY_006_013>";
		exec(uri);

	}

	public static void exec(String Uri) {
		
		String queryMetadata = "PREFIX dct: <http://purl.org/dc/terms/>\n" + 
				"PREFIX dcat: <https://www.w3.org/TR/vocab-dcat/>\n" + 
				"PREFIX ignf: <http://data.ign.fr/def/ignf#>\n" + 
				"PREFIX foaf: <http://xmlns.com/foaf/0.1/>\n" + 
				"PREFIX bdo: <http://bigdataocean.eu/bdo/>\n" + 
				"PREFIX ids: <http://industrialdataspace/information-model/>\n" + 
				"SELECT ?uri ?ident ?title ?desc ?sub ?keyw ?standard ?format ?lang ?homep ?publi ?rights (STR(?issued) AS ?issuedDate)  (STR(?modified) AS ?modifiedDate) ?geoLoc ?timeReso (STR(?verFrom) AS ?vFrom) (STR(?verTo) AS ?vTo) (STR(?west) AS ?spatialWest) (STR(?east) AS ?spatialEast) (STR(?south) AS ?spatialSouth) (STR(?north) AS ?spatialNorth) (STR(?tempCovB) AS ?timeCovBeg) (STR(?tempCovE) AS ?timeCovEnd) ?vLevel ?coorSys\n" + 
				"WHERE{ \n" + 
				"  "+Uri+" a dcat:Dataset ;\n" + 
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
				" OPTIONAL {"+Uri+" dct:spatial ?geoloc .}\n" +
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
		
		List<String> listVaraibles = new ArrayList<>() ;
		RDFNode node2, node3;
		
		String queryVariables = "PREFIX dct: <http://purl.org/dc/terms/>\n" +
				"PREFIX bdo: <http://bigdataocean.eu/bdo/>\n" +
				"PREFIX skos: <http://www.w3.org/2004/02/skos/core#>\n" +
				"SELECT ?uri ?identifierVariable (STR(?prefLabel) AS ?label)\n" + 
				"WHERE {\n" + 
				"  "+Uri+" ?predicate ?object .\n" + 
				"  ?object a bdo:BDOVariable ;\n" + 
				"        dct:identifier ?identifierVariable ;\n" + 
				"        skos:prefLabel ?prefLabel .\n" + 
				"  FILTER(lang(?prefLabel) = \"en\")\n" + 
				"}";
		
		// Adding Datasetvariables -- BDOvariables in a list
		ResultSet rsVariables = QueryExecutor.selectQuery(queryVariables);
		while(rsVariables.hasNext()){
			QuerySolution solution = rsVariables.nextSolution();
			node2 = solution.get("identifierVariable");
			node3 = solution.get("label");
			listVaraibles.add(node2.toString() + " -- "+ node3.toString());
		}
		
		dataset.setVariable(listVaraibles);
		
		/*List<String> listVaraibles = new ArrayList<>() ;
		List<String> listVaraiblesBDO = new ArrayList<>() ;
		RDFNode node2;
		
		String queryVariables = "PREFIX dct: <http://purl.org/dc/terms/>\n" +
				"PREFIX bdo: <http://bigdataocean.eu/bdo/>\n" +
				"PREFIX skos: <http://www.w3.org/2004/02/skos/core#>\n" +
				"SELECT ?uri ?identifierVariable (STR(?prefLabel) AS ?label)\n" + 
				"WHERE {\n" + 
				"  "+Uri+" ?predicate ?object .\n" + 
				"  ?object a bdo:BDOVariable ;\n" + 
				"        dct:identifier ?identifierVariable ;\n" + 
				"        skos:prefLabel ?prefLabel .\n" + 
				"  FILTER(lang(?prefLabel) = \"en\")\n" + 
				"}";
		
		ResultSet rsVariables = QueryExecutor.selectQuery(queryVariables);
		while(rsVariables.hasNext()){
			QuerySolution solution = rsVariables.nextSolution();
			node2 = solution.get("identifierVariable");
			listVaraibles.add(node2.toString());
			node2 = solution.get("label");
			listVaraiblesBDO.add(node2.toString());
		}
		Map<String,String> map = new LinkedHashMap<String,String>();
		for (int i = 0; i < listVaraibles.size(); i++) {
			map.put(listVaraibles.get(i), listVaraiblesBDO.get(i));
		}
		dataset.setVariables(map);*/
		
		try {
			// Parse into JSON the Dataset instance with all metadata from a dataset
			Gson gson  = new Gson();
			System.out.println(gson.toJson(dataset));
			//log.info("Dataset's metadata: " + gson.toJson(dataset));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

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
				"SELECT ?uri ?ident ?title ?desc ?sub ?keyw ?standard ?format ?lang ?homep "
				+ "?publi ?rights (STR(?issued) AS ?issuedDate)  (STR(?modified) AS ?modifiedDate) "
				+ "?geoLoc ?timeReso (STR(?verFrom) AS ?vFrom) (STR(?verTo) AS ?vTo) (STR(?west) AS ?spatialWest) "
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
				" OPTIONAL {"+uri+" dct:spatial ?geoLoc .}\n" +
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
			node = solution.get("sub");
			if(dataset.getSubject() != "")
			{
				dataset.setSubject(dataset.getSubject()+", "+node.toString());
			}else {
				dataset.setSubject(node.toString());
			}
			node = solution.get("keyw");
			if(dataset.getKeywords() != "")
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
			if(dataset.getLanguage() != "")
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
				if(dataset.getGeoLocation() == "")
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
		
		//avoid duplicates in subject, keywords, Language, geolocation
		dataset.setSubject(avoidDuplicate(dataset.getSubject()));
		dataset.setKeywords(avoidDuplicate(dataset.getKeywords()));
		dataset.setLanguage(avoidDuplicate(dataset.getLanguage()));
		dataset.setGeoLocation(avoidDuplicate(dataset.getGeoLocation()));
		
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
		
		try {
			// Parse into JSON the Dataset instance with all metadata from a dataset
			Gson gson  = new Gson();
			System.out.println(gson.toJson(dataset));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static String avoidDuplicate(String value) {
		String result = "";
		if(!value.isEmpty()) {
			String[] myArray = value.split(", ");
			int dups = 0; // represents number of duplicate numbers

		    for (int i = 1; i < myArray.length; i++) 
		    {
		        // if number in array after current number in array is the same
		        if (myArray[i].equals(myArray[i - 1]))
		            dups++; // add one to number of duplicates
		    }

		    // create return array (with no duplicates) 
		    // and subtract the number of duplicates from the original size (no NPEs)
		    String[] returnArray = new String[myArray.length - dups];

		    returnArray[0] = myArray[0]; // set the first positions equal to each other
		                                 // because it's not iterated over in the loop

		    int count = 1; // element count for the return array

		    for (int i = 1; i < myArray.length; i++)
		    {
		        // if current number in original array is not the same as the one before
		        if (!myArray[i].equals(myArray[i-1])) 
		        {
		           returnArray[count] = myArray[i]; // add the number to the return array
		           count++; // continue to next element in the return array
		        }
		    }
			
		    for (String val : returnArray) {
		    	if (result.equals("")) {
		    		result = val;
		    	} else {
		    		result = result + ", " + val;
		    	}
		    }
		}
		return result;
	}
}

package org.unibonn.bdo.bdodatasets;

import org.unibonn.bdo.objects.Dataset;

import com.google.gson.Gson;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.RDFNode;

/**
 *  
 * @author Jaime M Trillos
 * @author Ana C Trillos
 *
 */

public class GetMetadata {


	public static void main(String[] args) {
		String uri = args[0];
		//String path2File = args[1];
		//String uri = "bdo:MEDSEA_ANALYSIS_FORECAST_WAV_006_011";
		//String path2File = "/home/anatrillos/Dropbox/Documentos/BigDataOcean-Harmonization/Backend/AddDatasets/addNewDataset.ttl";
		exec(uri);

	}

	public static void exec(String Uri) {
		
		String query = "PREFIX dct: <http://purl.org/dc/terms/>\n";
		query += "PREFIX dcat: <https://www.w3.org/TR/vocab-dcat/>\n";
		query += "PREFIX ignf: <http://data.ign.fr/def/ignf#>\n";
		query += "PREFIX foaf: <http://xmlns.com/foaf/0.1/>\n";
		query += "PREFIX bdo: <http://bigdataocean.eu/bdo/>\n";
		query += "SELECT ?uri ?ident ?title ?desc ?sub ?keyw ?standard ?format ?lang ?homep ?publi ?rights (STR(?issued) AS ?issuedDate) (STR(?modified) AS ?modifiedDate) ?geoLoc ?timeReso (STR(?verFrom) AS ?vFrom) (STR(?verTo) AS ?vTo) (STR(?west) AS ?spatialWest) (STR(?east) AS ?spatialEast) (STR(?south) AS ?spatialSouth) (STR(?north) AS ?spatialNorth)\n";
		query += "WHERE{\n";
		query += Uri+" a dcat:Dataset;\n";
		query += "dct:identifier ?ident;\n";
		query += "dct:title ?title;\n";
		query += "dct:description ?desc;\n";
		query += "dcat:subject ?sub;\n";
		query += "bdo:verticalCoverage ?vCov;\n";
		query += "dcat:theme ?keyw;\n";
		query += "dct:conformsTo ?standard;\n";
		query += "dct:format ?format;\n";
		query += "dct:language ?lang;\n";
		query += "foaf:homepage ?homep;\n";
		query += "dct:publisher ?publi;\n";
		query += "dct:accessRights ?rights;\n";
		query += "dct:issued ?issued;\n";
		query += "dct:modified ?modified;\n";
		query += "dct:spatial ?geoLoc;\n";
		query += "bdo:timeResolution ?timeReso;\n";
		query += "bdo:GeographicalCoverage ?spatial.\n";		
		query += "?spatial a ignf:GeographicBoundingBox;\n";
		query += "ignf:westBoundLongitude ?west;\n";
		query += "ignf:eastBoundLongitude ?east;\n";
		query += "ignf:southBoundLatitude ?south;\n";
		query += "ignf:northBoundLatitude ?north.\n";
		query += "?vCov a  bdo:VerticalCoverage;\n";
		query += "bdo:verticalFrom ?verFrom;\n";
		query += "bdo:verticalTo ?verTo.\n";
		query += "} LIMIT 1\n";
		
		Dataset dataset = new Dataset();
		RDFNode node;

		QueryExecution qe = QueryExecutionFactory.sparqlService(
					"http://localhost:3030/bdoHarmonization/query",query);
		ResultSet results = qe.execSelect();
		
		while(results.hasNext()){
			QuerySolution solution = results.nextSolution();
			node = solution.get("ident");
			dataset.setIdentifier(node.toString());
			node = solution.get("title");
			dataset.setTitle(node.toString());
			node = solution.get("desc");
			dataset.setDescription(node.toString());
			node = solution.get("sub");
			dataset.setSubject(node.toString());
			node = solution.get("keyw");
			dataset.setKeywords(node.toString());
			node = solution.get("standard");
			dataset.setStandards(node.toString());
			node = solution.get("format");
			dataset.setFormat(node.toString());
			node = solution.get("lang");
			dataset.setLanguage(node.toString());
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
			dataset.setGeoLocation(node.toString());
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
		}
		
		try {
			Gson gson  = new Gson();
			System.out.println(gson.toJson(dataset));
		} catch (Exception e) {
			e.printStackTrace();
		}
		qe.close();
	}
}

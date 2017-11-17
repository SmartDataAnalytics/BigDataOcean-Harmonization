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
		//String uri = "<http://bigdataocean.eu/bdo/MEDSEA_ANALYSIS_FORECAST_WAV_006_011>";
		//String path2File = "/home/anatrillos/Dropbox/Documentos/BigDataOcean-Harmonization/Backend/AddDatasets/addNewDataset.ttl";
		exec(uri);

	}

	public static void exec(String Uri) {
		
		String query = "PREFIX dct: <http://purl.org/dc/terms/>\n";
		query += "PREFIX dcat: <https://www.w3.org/TR/vocab-dcat/>\n";
		query += "select ?uri ?ident ?title ?desc ?sub ?keyw ?standard ?format ?lang ?homep ?publi ?rights ?issued ?modified ?geoLoc ?timeReso ?vFrom ?vTo\n";
		query += "where{\n";
		query += Uri+" a dcat:Dataset;\n";
		query += "<http://purl.org/dc/terms/identifier> ?ident;\n";
		query += "<http://purl.org/dc/terms/title> ?title;\n";
		query += "<http://purl.org/dc/terms/description> ?desc;\n";
		query += "<https://www.w3.org/TR/vocab-dcat/subject> ?sub;\n";
		query += "<http://bigdataocean.eu/bdo/verticalCoverage> ?vCov;\n";
		query += "<https://www.w3.org/TR/vocab-dcat/theme> ?keyw;\n";
		query += "<http://purl.org/dc/terms/conformsTo> ?standard;\n";
		query += "<http://purl.org/dc/terms/format> ?format;\n";
		query += "<http://purl.org/dc/terms/language> ?lang;\n";
		query += "<http://xmlns.com/foaf/0.1/homepage> ?homep;\n";
		query += "<http://purl.org/dc/terms/publisher> ?publi;\n";
		query += "<http://purl.org/dc/terms/accessRights> ?rights;\n";
		query += "<http://purl.org/dc/terms/issued> ?issued;\n";
		query += "<http://purl.org/dc/terms/modified> ?modified;\n";
		query += "<http://purl.org/dc/terms/spatial> ?geoLoc.\n";
		query += "?vCov a  <http://bigdataocean.eu/bdo/VerticalCoverage>;\n";
		query += "<http://bigdataocean.eu/bdo/verticalFrom> ?vFrom;\n";
		query += "<http://bigdataocean.eu/bdo/verticalTo> ?vTo.\n";
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
			node = solution.get("issued");
			dataset.setIssuedDate(node.toString());
			node = solution.get("modified");
			dataset.setModifiedDate(node.toString());
			node = solution.get("geoLoc");
			dataset.setGeoLocation(node.toString());
			node = solution.get("vFrom");
			dataset.setVerticalCoverageFrom(node.toString());
			node = solution.get("vTo");
			dataset.setVerticalCoverageTo(node.toString());
		}
		
		try {
			Gson gson  =new Gson();
			System.out.println(gson.toJson(dataset));
		} catch (Exception e) {
			e.printStackTrace();
		}
		qe.close();
	}
}

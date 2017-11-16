package org.unibonn.bdo.bdodatasets;

import org.unibonn.bdo.objects.Dataset;

import com.google.gson.Gson;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.RDFNode;

/**
 *  
 * @author Jaime M Trillos
 * @author Ana C Trillos
 *
 */

public class GetDatasetFromFuseki {


	public static void main(String[] args) {
		//String uri = args[0];
		//String path2File = args[1];
		String uri = "<http://bigdataocean.eu/bdo/MEDSEA_ANALYSIS_FORECAST_WAV_006_011>";
		//String path2File = "/home/anatrillos/Dropbox/Documentos/BigDataOcean-Harmonization/Backend/AddDatasets/addNewDataset.ttl";
		exec(uri);

	}

	public static void exec(String Uri) {
		
		Dataset dataset = new Dataset();
		int i = 0;
		QueryExecution qc = QueryExecutionFactory.sparqlService(
				"http://localhost:3030/bdoHarmonization/query","SELECT (count(distinct ?p) as ?prop) WHERE{"+Uri+" ?p ?o}");
		ResultSet count = qc.execSelect();
		QuerySolution countSol = count.nextSolution();
		System.out.println(countSol);		
		int num = Integer.parseInt(countSol.toString().replaceAll("\\D+",""));
		
		String[][] result = new String[num][2];
		QueryExecution qe = QueryExecutionFactory.sparqlService(
					"http://localhost:3030/bdoHarmonization/query","SELECT ?p ?o WHERE{"+Uri+" ?p ?o}");
		ResultSet results = qe.execSelect();
		ResultSetFormatter.out(System.out, results);
		
		while(results.hasNext()){
			QuerySolution solution = results.nextSolution();
			RDFNode node = solution.get("p");
			result[i][0] = node.toString();
			
			RDFNode node2 = solution.get("o");
			result[i][1] = node2.toString();
			i++;
		}
		
		for(int j=0; j<result.length; j++){
			if(result[j][0].matches("http://purl.org/dc/terms/identifier")){
				dataset.setIdentifier(result[j][1]);
			}else if(result[j][0].matches("http://purl.org/dc/terms/title")){
				dataset.setTitle(result[j][1]);
			}else if(result[j][0].matches("http://purl.org/dc/terms/description")){
				dataset.setDescription(result[j][1]);
			}else if(result[j][0].matches("https://www.w3.org/TR/vocab-dcat/subject")){
				dataset.setSubject(result[j][1]);
			}else if(result[j][0].matches("https://www.w3.org/TR/vocab-dcat/theme")){
				dataset.setKeywords(result[j][1]);
			}else if(result[j][0].matches("http://purl.org/dc/terms/conformsTo")){
				dataset.setStandards(result[j][1]);
			}else if(result[j][0].matches("http://purl.org/dc/terms/format")){
				dataset.setFormat(result[j][1]);
			}else if(result[j][0].matches("http://purl.org/dc/terms/language")){
				dataset.setLanguage(result[j][1]);
			}else if(result[j][0].matches("http://xmlns.com/foaf/0.1/homepage")){
				dataset.setHomepage(result[j][1]);
			}else if(result[j][0].matches("http://purl.org/dc/terms/publisher")){
				dataset.setPublisher(result[j][1]);
			}else if(result[j][0].matches("http://purl.org/dc/terms/accessRights")){
				dataset.setAccessRights(result[j][1]);
			}else if(result[j][0].matches("http://purl.org/dc/terms/issued")){
				dataset.setIssuedDate(result[j][1]);
			}else if(result[j][0].matches("http://purl.org/dc/terms/modified")){
				dataset.setModifiedDate(result[j][1]);
			}else if(result[j][0].matches("http://purl.org/dc/terms/spatial")){
				dataset.setGeoLocation(result[j][1]);
			}else if(result[j][0].matches("<http://bigdataocean.eu/bdo/temporalResolution")){
				dataset.setTimeResolution(result[j][1]);
			}
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

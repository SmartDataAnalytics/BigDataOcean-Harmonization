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

public class GetDatasetFromFuseki {


	public static void main(String[] args) {
		//String uri = args[0];
		//String path2File = args[1];
		String uri = "<http://bigdataocean.eu/bdo/MEDSEA_ANALYSIS_FORECAST_WAV_006_011>";
		//String path2File = "/home/anatrillos/Dropbox/Documentos/BigDataOcean-Harmonization/Backend/AddDatasets/addNewDataset.ttl";
		exec(uri);

	}

	public static void exec(String Uri) {
		
		String[][] result = new String[30][2];
		Dataset dataset = new Dataset();
		QueryExecution qe = QueryExecutionFactory.sparqlService(
					"http://localhost:3030/bdoHarmonization/query","SELECT ?p ?o WHERE{"+Uri+" ?p ?o}");
		ResultSet results = qe.execSelect();
		int i = 0;
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

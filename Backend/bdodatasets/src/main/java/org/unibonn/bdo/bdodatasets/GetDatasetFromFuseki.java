package org.unibonn.bdo.bdodatasets;

import java.util.ArrayList;

import org.unibonn.bdo.objects.Dataset;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;

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

		String property = "p";
		String object = "o";
		ArrayList<Resource> result = new ArrayList<Resource>();
		Dataset datasetMetadata = new Dataset();
		QueryExecution qe = QueryExecutionFactory.sparqlService(
					"http://localhost:3030/bdoHarmonization/query","SELECT ?p ?o WHERE{"+Uri+" ?p ?o}");
		ResultSet model = qe.execSelect();
		while(model.hasNext()){
			RDFNode n = model.next().get(property);
			if(n == null || !n.isResource()) continue;
			result.add(n.asResource());
		}
		for(int i=0; i<result.size(); i++){
		}
		System.out.println(result);
		qe.close();
		

		
		/*//Query the collection, dump output
		QueryExecution qe = QueryExecutionFactory.sparqlService(
	                "http://localhost:3030/bdoHarmonization/query", "SELECT * WHERE {"+Uri+" ?p ?o}");
        ResultSet results = qe.execSelect();
                
        // write to a ByteArrayOutputStream
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        ResultSetFormatter.outputAsJSON(outputStream, results);

        // and turn that into a String
        String json = new String(outputStream.toByteArray());

        System.out.println(json);
        qe.close();*/
	}
}

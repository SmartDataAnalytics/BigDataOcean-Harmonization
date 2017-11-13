package org.unibonn.bdo.bdodatasets;


import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.rdf.model.Model;

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
		String string = null;
		QueryExecution qe = QueryExecutionFactory.sparqlService(
					"http://localhost:3030/bdoHarmonization/query","CONSTRUCT WHERE{"+Uri+" ?p ?o}");
		Model model = qe.execConstruct();
		
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

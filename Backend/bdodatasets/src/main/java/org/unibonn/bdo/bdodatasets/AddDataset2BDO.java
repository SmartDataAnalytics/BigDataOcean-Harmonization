package org.unibonn.bdo.bdodatasets;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.UUID;

import javax.xml.ws.Response;

import org.omg.CORBA.RepositoryIdHelper;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.update.UpdateExecutionFactory;
import com.hp.hpl.jena.update.UpdateFactory;
import com.hp.hpl.jena.update.UpdateProcessor;

/**
 *  
 * @author Jaime M Trillos
 * @author Ana C Trillos
 *
 */

public class AddDataset2BDO {

	public static void main(String[] args) {
		String uri = args[0];
		String path2File = args[1];
		/*String uri = "<http://bigdataocean.eu/bdo/MEDSEA_ANALYSIS_FORECAST_WAV_006_011>";
		String path2File = "/home/anatrillos/Dropbox/Documentos/BigDataOcean-Harmonization/Backend/AddDatasets/addNewDataset.ttl";*/
		exec(uri, path2File);

	}

	public static void exec(String Uri, String path2File) {
		String dataset = null;
		String line;
		
		//Query TDB to see if the URI to be added already exists
		QueryExecution qe = QueryExecutionFactory.sparqlService(
				"http://localhost:3030/bdoHarmonization/query", "ASK {"+Uri+" ?p ?o}");
		boolean results = qe.execAsk();
		qe.close();

		if(results == false){
			try {
				// FileReader reads text files in the default encoding.
				FileReader fileReader = new FileReader(path2File);
	
				// Always wrap FileReader in BufferedReader.
				BufferedReader bufferedReader = new BufferedReader(fileReader);
	
				while((line = bufferedReader. readLine()) != null) {
					if (dataset == null){
						dataset = line;
					}else{
						dataset += line;
					}
				}   
	
				// Always close files.
				bufferedReader.close();         
			}
			catch(FileNotFoundException ex) {
				System.out.println(
						"Unable to open file '" + 
								path2File + "'");                
			}
			catch(IOException ex) {
				System.out.println(
						"Error reading file '" 
								+ path2File + "'");  
			}

			//Add a new book to the collection
			UpdateProcessor upp = UpdateExecutionFactory.createRemote(
					UpdateFactory.create(String.format(dataset)), 
					"http://localhost:3030/bdoHarmonization/update");
			upp.execute();
			System.out.print("Successful");
		}else{
			System.out.print(String.format("Error!   URI already exists."));
		}
		
		//Query the collection, dump output
		/*QueryExecution qe = QueryExecutionFactory.sparqlService(
	                "http://localhost:3030/bdoHarmonization/query", "SELECT * WHERE {"+Uri+" ?p ?o}");
        ResultSet results = qe.execSelect();
        ResultSetFormatter.out(System.out, results);
        qe.close();*/

	}
}

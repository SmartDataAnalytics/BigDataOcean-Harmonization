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
 * Receives 2 parameters: uri and the path of the ttl file to be added on Jena Fueski
 * 
 */

public class AddCopernicus2BDO {

	public static void main(String[] args) {
		String uri = args[0];
		String path2File = args[1];
		exec(uri, path2File);

	}

	public static void exec(String Uri, String path2File) {
		
		String query = "ASK {"+Uri+" ?p ?o}";
		
		//Query Jena Fueski to see if the URI to be added already exists
		boolean results = QueryExecutor.askQuery(query);
		// if the URI does not exists
		if(results == false){
			String dataset = readFile(path2File);

			//Add the dataset to Jena Fueski
			QueryExecutor.insertQuery(dataset);
			System.out.print("Successful");
		}else{
			System.out.print(String.format("Error!   URI already exists."));
		}
	}
	
	public static String readFile(String path2File) {
		String dataset = null;
		String line;
		
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
			// Close file.
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
		return dataset;
	}
}

package org.unibonn.bdo.bdodatasets;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 *  
 * @author Jaime M Trillos
 * @author Ana C Trillos
 *
 * Receives 2 parameters: uri and the path of the ttl file to be added on Jena Fueski
 * 
 */

public class AddDataset2BDO {

	public static void main(String[] args) {
		String parameters = args[0];
		String path2File = args[1];
		exec(parameters, path2File);

	}

	public static void exec(String parameters, String path2File) {
		String []param = parameters.split(">");

		String query = "PREFIX dct: <http://purl.org/dc/terms/>\n" +
			"PREFIX bdo: <http://bigdataocean.eu/bdo/>\n" +
			"PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n "+
			"ASK {?uri dct:title \""+param[0]+"\" ;\n" +
			"dct:publisher \""+param[1]+"\" ;\n" +
			"dct:issued \""+param[2]+"\"^^xsd:dateTime }\n" ;

		//Query Jena Fueski to see if the URI to be added already exists
		boolean results = QueryExecutor.askQuery(query); 
		// if the dataset does not exists
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

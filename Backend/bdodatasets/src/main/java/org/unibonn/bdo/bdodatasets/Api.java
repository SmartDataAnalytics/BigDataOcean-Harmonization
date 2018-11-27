package org.unibonn.bdo.bdodatasets;

import java.io.IOException;
import java.util.List;

import org.apache.kafka.clients.producer.Producer;
import org.json.simple.parser.ParseException;
import org.unibonn.bdo.connections.ProducerCreator;
import org.unibonn.bdo.objects.DatasetApi;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mashape.unirest.http.exceptions.UnirestException;

/**
 *  
 * @author Jaime M Trillos
 * @author Ana C Trillos
 *
 * Receives 1 parameter, the URI of the Dataset to get all metadata from Jena Fuseki
 *
 */

public class Api {

	public static void main(String[] args) {
		int apiNumber = Integer.parseInt(args[0]);
		String searchParam = args[1];
		exec(apiNumber, searchParam);

	}

	public static void exec(int apiNumber, String searchParam) {
		List<DatasetApi> list;
		Gson gson  = new GsonBuilder().setPrettyPrinting().create();
		switch(apiNumber) {
			case 1:
				try {
					list = BdoApiAnalyser.apiListAllDatasets();
					// Parse into JSON the Dataset instance with all metadata from a dataset
					System.out.print(gson.toJson(list));
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
				
			case 2:
				try {
					DatasetApi dataset = BdoApiAnalyser.apiSearchDataset(searchParam);
					// Parse into JSON the Dataset instance with all metadata from a dataset
					System.out.print(gson.toJson(dataset));
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
				
			case 3:
				try {
					list = BdoApiAnalyser.apiSearchSubjects(searchParam);
					// Parse into JSON the Dataset instance with all metadata from a dataset
					System.out.print(gson.toJson(list));
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			
			case 4:
				try {
					list = BdoApiAnalyser.apiSearchKeywords(searchParam);
					// Parse into JSON the Dataset instance with all metadata from a dataset
					System.out.print(gson.toJson(list));
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
				
			case 5:
				try {
					list = BdoApiAnalyser.apiSearchGeoLoc(searchParam);
					// Parse into JSON the Dataset instance with all metadata from a dataset
					System.out.print(gson.toJson(list));
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			
			case 6:
				try {
					list = BdoApiAnalyser.apisearchGeoCoverage(searchParam);
					// Parse into JSON the Dataset instance with all metadata from a dataset
					System.out.print(gson.toJson(list));
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			
			case 7:
				list = BdoApiAnalyser.apiListDatasetByVertCov(searchParam);
				// Parse into JSON the Dataset instance with all metadata from a dataset
				System.out.print(gson.toJson(list));
				break;
			
			case 8:				
				list = BdoApiAnalyser.apiListDatasetByTimeCov(searchParam);
				// Parse into JSON the Dataset instance with all metadata from a dataset
				System.out.print(gson.toJson(list));
				break;	
			
			case 9:
				try {
					DatasetApi dataset = BdoApiAnalyser.apiListVarOfDataset(searchParam);
					// Parse into JSON the Dataset instance with all metadata from a dataset
					System.out.print(gson.toJson(dataset));
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
				
			case 10:
				list = BdoApiAnalyser.apiListDatasetsByVar(searchParam);
				// Parse into JSON the Dataset instance with all metadata from a dataset
				System.out.print(gson.toJson(list));
				break;	
				
			case 11:
				try {
					// Insert Dataset's metadata automatically
					String[] parameters = searchParam.split(",");
					String filename = parameters[0];
					String idFile = parameters[1];
					String idProfile = parameters[2];
					Boolean flagProduce = Boolean.parseBoolean(parameters[3]);
					Producer<Long, String> producer = null;
					if(flagProduce) {
						producer = ProducerCreator.createProducer();
					}
					InsertDatasetAutomatic.analyseInsertDatasetAutomatic(filename, idFile, idProfile, producer);
				} catch (IOException | ParseException | UnirestException e) {
					e.printStackTrace();
				}
				break;	
			case 12:
				try {
					DatasetApi dataset = BdoApiAnalyser.apiListVarOfDatasetStorage(searchParam);
					// Parse into JSON the Dataset instance with all metadata from a dataset
					System.out.print(gson.toJson(dataset));
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;	
			case 13:
				try {
					list = BdoApiAnalyser.apiSearchStorageTable(searchParam);
					// Parse into JSON the Dataset instance with all metadata from a dataset
					System.out.print(gson.toJson(list));
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;	
			case 14:
				try {
					list = BdoApiAnalyser.apiSearchTitle(searchParam);
					// Parse into JSON the Dataset instance with all metadata from a dataset
					System.out.print(gson.toJson(list));
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;	
			case 15:
				try {
					list = BdoApiAnalyser.apiSearchDescription(searchParam);
					// Parse into JSON the Dataset instance with all metadata from a dataset
					System.out.print(gson.toJson(list));
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;	
			case 16:
				try {
					DatasetApi dataset = BdoApiAnalyser.apiSearchDatasetStorage(searchParam);
					// Parse into JSON the Dataset instance with all metadata from a dataset
					System.out.print(gson.toJson(dataset));
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;		
			case 17:
				try {
					list = BdoApiAnalyser.apiListAllAggregatedDatasets();
					// Parse into JSON the Dataset instance with all metadata from a dataset
					System.out.print(gson.toJson(list));
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;	
			default:
				break;
		}
	}	
}

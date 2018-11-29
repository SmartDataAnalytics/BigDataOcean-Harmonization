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
				// filedataset/list
				try {
					list = BdoApiAnalyser.apiListAllFileDatasets();
					// Parse into JSON the Dataset instance with all metadata from a dataset
					System.out.print(gson.toJson(list));
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
				
			case 2:
				// filedataset/info
				try {
					DatasetApi dataset = BdoApiAnalyser.apiInfoFileDataset(searchParam);
					// Parse into JSON the Dataset instance with all metadata from a dataset
					System.out.print(gson.toJson(dataset));
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
				
			case 3:
				// filedataset/searchSubject
				try {
					list = BdoApiAnalyser.apiSearchSubjects(searchParam);
					// Parse into JSON the Dataset instance with all metadata from a dataset
					System.out.print(gson.toJson(list));
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			
			case 4:
				// filedataset/searchKeyword
				try {
					list = BdoApiAnalyser.apiSearchKeywords(searchParam);
					// Parse into JSON the Dataset instance with all metadata from a dataset
					System.out.print(gson.toJson(list));
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
				
			case 5:
				// filedataset/searchGeoLocation
				try {
					list = BdoApiAnalyser.apiSearchGeoLoc(searchParam);
					// Parse into JSON the Dataset instance with all metadata from a dataset
					System.out.print(gson.toJson(list));
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			
			case 6:
				// filedataset/searchGeoCoverage
				try {
					list = BdoApiAnalyser.apiSearchGeoCoverage(searchParam);
					// Parse into JSON the Dataset instance with all metadata from a dataset
					System.out.print(gson.toJson(list));
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			
			case 7:
				// filedataset/searchVerticalCoverage
				list = BdoApiAnalyser.apiSearchVerticalCoverage(searchParam);
				// Parse into JSON the Dataset instance with all metadata from a dataset
				System.out.print(gson.toJson(list));
				break;
			
			case 8:			
				// filedataset/searchTemporalCoverage
				list = BdoApiAnalyser.apiSearchTemporalCoverage(searchParam);
				// Parse into JSON the Dataset instance with all metadata from a dataset
				System.out.print(gson.toJson(list));
				break;	
			
			case 9:
				// variable/list
				try {
					DatasetApi dataset = BdoApiAnalyser.apiListFileDatasetVariables(searchParam);
					// Parse into JSON the Dataset instance with all metadata from a dataset
					System.out.print(gson.toJson(dataset));
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
				
			case 10:
				// variable/search
				list = BdoApiAnalyser.apiSearchVariable(searchParam);
				// Parse into JSON the Dataset instance with all metadata from a dataset
				System.out.print(gson.toJson(list));
				break;	
				
			case 11:
				// filedataset/insertAutomatic
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
			        if(producer != null) {
			        	producer.close();
			        }
				} catch (IOException | ParseException | UnirestException e) {
					e.printStackTrace();
				}
				break;	
			case 12:
				// dataset/listVariables
				try {
					DatasetApi dataset = BdoApiAnalyser.apiListDatasetVariables(searchParam);
					// Parse into JSON the Dataset instance with all metadata from a dataset
					System.out.print(gson.toJson(dataset));
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;	
			case 13:
				// dataset/listFileDatasets
				try {
					list = BdoApiAnalyser.apiListFileDatasetofDataset(searchParam);
					// Parse into JSON the Dataset instance with all metadata from a dataset
					System.out.print(gson.toJson(list));
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;	
			case 14:
				// filedataset/searchTitle
				try {
					list = BdoApiAnalyser.apiSearchTitle(searchParam);
					// Parse into JSON the Dataset instance with all metadata from a dataset
					System.out.print(gson.toJson(list));
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;	
			case 15:
				// filedataset/searchDescription
				try {
					list = BdoApiAnalyser.apiSearchDescription(searchParam);
					// Parse into JSON the Dataset instance with all metadata from a dataset
					System.out.print(gson.toJson(list));
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;	
			case 16:
				// dataset/info
				try {
					DatasetApi dataset = BdoApiAnalyser.apiInfoDataset(searchParam);
					// Parse into JSON the Dataset instance with all metadata from a dataset
					System.out.print(gson.toJson(dataset));
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;		
			case 17:
				// dataset/list
				try {
					list = BdoApiAnalyser.apiListAllDatasets();
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

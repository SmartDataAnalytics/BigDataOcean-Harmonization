package org.unibonn.bdo.bdodatasets;

import java.io.IOException;
import java.util.List;

import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.unibonn.bdo.objects.Dataset;

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
	
	private final static Logger log = LoggerFactory.getLogger(GetMetadata.class);


	public static void main(String[] args) {
		int apiNumber = Integer.parseInt(args[0]);
		String searchParam = args[1];
		//int apiNumber = 11;
		//String searchParam = "";
		//String searchParam = "-6000,= -2";
		//String searchParam = "http://inspire.ec.europa.eu/metadata-codelist/TopicCategory/oceans, http://inspire.ec.europa.eu/metadata-codelist/TopicCategory/climatologyMeteorologyAtmosphere";
		//String searchParam = "-17.1, 36.2, 30, 45.98";
		//String searchParam = "hdfs://212.101.173.50:9000/user/bdo/maretec/2017091300_20181131T125959_20181231T125959.csv,5ae051b39ac2555efd1a5926,5b61735247d7470001722ddf,false";
		exec(apiNumber, searchParam);

	}

	public static void exec(int apiNumber, String searchParam) {
		switch(apiNumber) {
			case 1:
				try {
					List<Dataset> list = BdoApiAnalyser.apiListAllDatasets();
					// Parse into JSON the Dataset instance with all metadata from a dataset
					Gson gson  = new GsonBuilder().setPrettyPrinting().create();
					System.out.print(gson.toJson(list));
					//log.info("Dataset's metadata: " + gson.toJson(dataset));
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
				
			case 2:
				try {
					Dataset dataset = BdoApiAnalyser.apiSearchDataset(searchParam);
					// Parse into JSON the Dataset instance with all metadata from a dataset
					Gson gson  = new GsonBuilder().setPrettyPrinting().create();
					System.out.print(gson.toJson(dataset));
					//log.info("Dataset's metadata: " + gson.toJson(dataset));
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
				
			case 3:
				try {
					List<Dataset> list = BdoApiAnalyser.apiSearchSubjects(searchParam);
					// Parse into JSON the Dataset instance with all metadata from a dataset
					Gson gson  = new GsonBuilder().setPrettyPrinting().create();
					System.out.print(gson.toJson(list));
					//log.info("Dataset's metadata: " + gson.toJson(dataset));
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			
			case 4:
				try {
					List<Dataset> list = BdoApiAnalyser.apiSearchKeywords(searchParam);
					// Parse into JSON the Dataset instance with all metadata from a dataset
					Gson gson  = new GsonBuilder().setPrettyPrinting().create();
					System.out.print(gson.toJson(list));
					//log.info("Dataset's metadata: " + gson.toJson(dataset));
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
				
			case 5:
				try {
					List<Dataset> list = BdoApiAnalyser.apiSearchGeoLoc(searchParam);
					// Parse into JSON the Dataset instance with all metadata from a dataset
					Gson gson  = new GsonBuilder().setPrettyPrinting().create();
					System.out.print(gson.toJson(list));
					//log.info("Dataset's metadata: " + gson.toJson(dataset));
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			
			case 6:
				try {
					List<Dataset> list = BdoApiAnalyser.apisearchGeoCoverage(searchParam);
					// Parse into JSON the Dataset instance with all metadata from a dataset
					Gson gson  = new GsonBuilder().setPrettyPrinting().create();
					System.out.print(gson.toJson(list));
					//log.info("Dataset's metadata: " + gson.toJson(dataset))
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			
			case 7:
				try {
					List<Dataset> list = BdoApiAnalyser.apiListDatasetByVertCov(searchParam);
					// Parse into JSON the Dataset instance with all metadata from a dataset
					Gson gson  = new GsonBuilder().setPrettyPrinting().create();
					System.out.print(gson.toJson(list));
					//log.info("Dataset's metadata: " + gson.toJson(dataset))
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			
			case 8:				
				try {
					List<Dataset> list = BdoApiAnalyser.apiListDatasetByTimeCov(searchParam);
					// Parse into JSON the Dataset instance with all metadata from a dataset
					Gson gson  = new GsonBuilder().setPrettyPrinting().create();
					System.out.print(gson.toJson(list));
					//log.info("Dataset's metadata: " + gson.toJson(dataset))
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;	
			
			case 9:
				try {
					Dataset dataset = BdoApiAnalyser.apiListVarOfDataset(searchParam);
					// Parse into JSON the Dataset instance with all metadata from a dataset
					Gson gson  = new GsonBuilder().setPrettyPrinting().create();
					System.out.print(gson.toJson(dataset));
					//log.info("Dataset's metadata: " + gson.toJson(dataset));
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
				
			case 10:
				try {
					List<Dataset> list = BdoApiAnalyser.apiListDatasetsByVar(searchParam);
					// Parse into JSON the Dataset instance with all metadata from a dataset
					Gson gson  = new GsonBuilder().setPrettyPrinting().create();
					System.out.print(gson.toJson(list));
					//log.info("Dataset's metadata: " + gson.toJson(dataset))
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;	
				
			case 11:
				try {
					// Insert Dataset's metadata automatically
					String[] parameters = searchParam.split(",");
					String filename = parameters[0];
					String idFile = parameters[1];
					String idProfile = parameters[2];
					Boolean produce = Boolean.parseBoolean(parameters[3]);
					Boolean response = InsertDatasetAutomatic.analyseInsertDatasetAutomatic(filename, idFile, idProfile);
					if(response && produce) {
						// if insertion was successful and produce = true then send message to TOPIC2
						InsertDatasetAutomatic.runProducer(idFile);
					}
				} catch (IOException | ParseException | UnirestException | java.text.ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;	
		}
	}	
}

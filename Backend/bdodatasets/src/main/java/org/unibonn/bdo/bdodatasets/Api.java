package org.unibonn.bdo.bdodatasets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.unibonn.bdo.objects.Dataset;

import com.google.gson.Gson;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.RDFNode;

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
		//int apiNumber = 6;
		//String searchParam = "0,- 2";
		//String searchParam = "http://inspire.ec.europa.eu/metadata-codelist/TopicCategory/oceans, http://inspire.ec.europa.eu/metadata-codelist/TopicCategory/climatologyMeteorologyAtmosphere";
		//String searchParam = "-18, 36, 30, 45";
		exec(apiNumber, searchParam);

	}

	public static void exec(int apiNumber, String searchParam) {
		switch(apiNumber) {
			case 1:
				try {
					List<Dataset> list = BdoApiAnalyser.apiListAllDatasets();
					// Parse into JSON the Dataset instance with all metadata from a dataset
					Gson gson  = new Gson();
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
					Gson gson  = new Gson();
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
					Gson gson  = new Gson();
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
					Gson gson  = new Gson();
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
					Gson gson  = new Gson();
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
					Gson gson  = new Gson();
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
					Gson gson  = new Gson();
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
					Gson gson  = new Gson();
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
					Gson gson  = new Gson();
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
					Gson gson  = new Gson();
					System.out.print(gson.toJson(list));
					//log.info("Dataset's metadata: " + gson.toJson(dataset))
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;				
		}
		
	}	
}

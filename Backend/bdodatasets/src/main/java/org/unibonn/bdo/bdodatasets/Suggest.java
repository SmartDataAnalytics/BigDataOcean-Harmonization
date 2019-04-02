package org.unibonn.bdo.bdodatasets;

import org.unibonn.bdo.objects.Dataset;
import org.unibonn.bdo.yandex.Translate;

import com.google.gson.Gson;

/**
 *  
 * @author Jaime M Trillos
 * @author Ana C Trillos
 *
 * Receives 2 parameter, 
 * 1. the URI/homepage or file (.nc, .csv, .xlsx) of the dataset to be parsed
 * 2. type of dataset (Coppernicus, Netcdf, CSV, Excel)
 * 
 */

public class Suggest {

	public static void main(String[] args) {
		Translate.setKey(Constants.YANDEX_API_KEY);
		String uriFile = args[0];
		String type = args[1];
		exec(uriFile, type);
	}
	
	public static void exec(String datasetUri, String type) {
		try {
			if(type.equals("Coppernicus")) {
				Dataset result = BdoDatasetAnalyser.analyseDatasetURI(datasetUri);
				Gson gson  =new Gson();
				System.out.println(gson.toJson(result));
			}else if (type.equals("Netcdf")) {
				Dataset result = BdoDatasetAnalyser.analyseDatasetNetcdf(datasetUri);
				Gson gson  =new Gson();
				System.out.println(gson.toJson(result));
			}else if (type.equals("FileNetcdf")) {
				Dataset result = BdoDatasetAnalyser.analyseDatasetFileNetcdf(datasetUri);
				Gson gson  =new Gson();
				System.out.println(gson.toJson(result));
			}else if (type.equals("FileCSV")) {
				Dataset result = BdoDatasetAnalyser.analyseDatasetFileCsv(datasetUri);
				Gson gson  =new Gson();
				System.out.println(gson.toJson(result));
			}else if (type.equals("CSV")) {
				Dataset result = BdoDatasetAnalyser.analyseDatasetCsv(datasetUri);
				Gson gson  =new Gson();
				System.out.println(gson.toJson(result));
			}else if (type.equals("FileExcel")) {
				Dataset result = BdoDatasetAnalyser.analyseDatasetFileExcel(datasetUri);
				Gson gson  =new Gson();
				System.out.println(gson.toJson(result));
			}else if (type.equals("Excel")) {
				Dataset result = BdoDatasetAnalyser.analyseDatasetExcel(datasetUri);
				Gson gson  =new Gson();
				System.out.println(gson.toJson(result));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

package org.unibonn.bdo.bdodatasets;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.unibonn.bdo.objects.Dataset;

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
	
	private final static Logger log = LoggerFactory.getLogger(Suggest.class);

	public static void main(String[] args) throws IOException {
		String uri_file = args[0];
		String type = args[1];
		//String uri_file = "http://cmems-resources.cls.fr/?option=com_csw&view=details&tab=info&product_id=GLOBAL_ANALYSIS_FORECAST_PHY_001_024&format=xml";
		//String type = "Coppernicus";
		//String type = "FileCSV";
		//String uri_file = "/home/eis/Dropbox/BDO/NESTER/anek_history_20180101T102300_20180806T131000.csv";
		//String uri_file = "hdfs://212.101.173.50:9000/user/bdo/maretec/2017091300.nc";
		//String uri_file = "hdfs://212.101.173.50:9000/user/bdo/numerical/dataset-ibi-analysis-forecast-wav-005-005-hourly_1516980716514.nc";
		//String uri_file = "hdfs://212.101.173.50:9000/user/bdo/buoy/IR_TS_MO_6200192.nc";
		//String uri_file = "/home/eis/Dropbox/BDO/NESTER/IR_TS_MO_6200192.nc";
		//String type = "FileNetcdf";
		//String uri_file = "/home/jaimetrillos/Dropbox/BDO/NESTER/KRITI_JADE_M1_FORMATTED_20180101T102300_20180806T131000.xlsx";
		//String type = "FileExcel";
		exec(uri_file, type);
	}
	
	public static void exec(String Dataseturi, String type) {
		try {
			if(type.equals("Coppernicus")) {
				Dataset result = BdoDatasetAnalyser.analyseDatasetURI(Dataseturi);
				Gson gson  =new Gson();
				System.out.println(gson.toJson(result));
				//log.info("Suggest of coppernicus: "+gson.toJson(result));
			}else if (type.equals("Netcdf")) {
				Dataset result = BdoDatasetAnalyser.analyseDatasetNetcdf(Dataseturi);
				Gson gson  =new Gson();
				System.out.println(gson.toJson(result));
				//log.info("Suggest of netcdf: "+gson.toJson(result));
			}else if (type.equals("FileNetcdf")) {
				Dataset result = BdoDatasetAnalyser.analyseDatasetFileNetcdf(Dataseturi);
				Gson gson  =new Gson();
				System.out.println(gson.toJson(result));
				//log.info("Suggest of netcdf file: "+gson.toJson(result));
			}else if (type.equals("FileCSV")) {
				Dataset result = BdoDatasetAnalyser.analyseDatasetFileCsv(Dataseturi);
				Gson gson  =new Gson();
				System.out.println(gson.toJson(result));
				//log.info("Suggest of csv file: "+gson.toJson(result));
			}else if (type.equals("CSV")) {
				Dataset result = BdoDatasetAnalyser.analyseDatasetCsv(Dataseturi);
				Gson gson  =new Gson();
				System.out.println(gson.toJson(result));
				//log.info("Suggest of csv: "+gson.toJson(result));
			}else if (type.equals("FileExcel")) {
				Dataset result = BdoDatasetAnalyser.analyseDatasetFileExcel(Dataseturi);
				Gson gson  =new Gson();
				System.out.println(gson.toJson(result));
				//log.info("Suggest of excel file: "+gson.toJson(result));
			}else if (type.equals("Excel")) {
				Dataset result = BdoDatasetAnalyser.analyseDatasetExcel(Dataseturi);
				Gson gson  =new Gson();
				System.out.println(gson.toJson(result));
				//log.info("Suggest of excel: "+gson.toJson(result));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

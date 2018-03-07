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
 * 1. the URI/homepage or file (.nc) of the dataset to be parsed
 * 2. type of dataset (Coppernicus, Netcdf)
 * 
 */

public class Suggest {
	
	private final static Logger log = LoggerFactory.getLogger(Suggest.class);

	public static void main(String[] args) throws IOException {
		//System.out.println( args[0] );   
		String uri_file = args[0];
		String type = args[1];
		//String uri_file = "http://cmems-resources.cls.fr/?option=com_csw&view=details&tab=info&product_id=GLOBAL_ANALYSIS_FORECAST_PHY_001_024&format=xml";
		//String type = "Coppernicus";
		//String uri_file = "hdfs://212.101.173.50:9000/user/bdo/maretec/2017091300.nc";
		//String uri_file = "hdfs://212.101.173.50:9000/user/bdo/numerical/dataset-ibi-analysis-forecast-wav-005-005-hourly_1516980716514.nc";
		//String uri_file = "hdfs://212.101.173.50:9000/user/bdo/buoy/IR_TS_MO_6200192.nc";
		//String uri_file = "hdfs://212.101.173.50:9000/user/bdo/forecast/20170827_hi-HCMR-WAVES-POSEIDON-AEG-b20170827_FC01-fv01.00.nc";
		//String type = "Netcdf";
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
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

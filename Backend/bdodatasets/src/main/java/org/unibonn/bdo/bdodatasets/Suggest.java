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
 * Receives 1 parameter, the URI/homepage of the dataset to be parsed (only Copernicus dataset)
 *
 */

public class Suggest {
	
	private final static Logger log = LoggerFactory.getLogger(Suggest.class);

	public static void main(String[] args) throws IOException {
		//System.out.println( args[0] );   
		String uri = args[0];
		//String uri = "http://cmems-resources.cls.fr/?option=com_csw&view=details&tab=info&product_id=MEDSEA_ANALYSIS_FORECAST_WAV_006_011&format=xml";
		exec(uri);
	}
	
	public static void exec(String Dataseturi) {
		try {
			Dataset result = BdoDatasetAnalyser.analyseDatasetURI(Dataseturi);
			Gson gson  =new Gson();
			System.out.println(gson.toJson(result));
			//log.info("Suggest of coppernicus: "+gson.toJson(result));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

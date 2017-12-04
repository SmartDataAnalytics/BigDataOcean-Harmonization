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
 * Receives 1 parameter, .nc file to be parsed (only NetCDF dataset)
 *
 */

public class SuggestNetcdf {
	
	private final static Logger log = LoggerFactory.getLogger(SuggestNetcdf.class);
	
	public static void main(String[] args) throws IOException {
		//System.out.println( args[0] );   
		String file = args[0];
		//String file = "/home/jaimetrillos/Dropbox/BDO/BigDataOcean-Harmonization/Backend/AddDatasets/file.nc";
		exec(file);
	}
	
	public static void exec(String Dataseturi) {
		try {
			Dataset result = BdoDatasetAnalyser.analyseDatasetNetcdf(Dataseturi);
			Gson gson  =new Gson();
			System.out.println(gson.toJson(result));
			//log.info("Suggest of netcdf: "+gson.toJson(result));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

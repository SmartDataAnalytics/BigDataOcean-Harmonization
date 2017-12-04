package org.unibonn.bdo.bdodatasets;

import java.io.IOException;

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
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

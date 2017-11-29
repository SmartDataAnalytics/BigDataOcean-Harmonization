package org.unibonn.bdo.bdodatasets;

import java.io.IOException;

import org.unibonn.bdo.objects.DatasetSuggest;

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
		//String file = args[0];
		String file = "/home/anatrillos/Dropbox/Documentos/BigDataOcean-Harmonization/Backend/AddDatasets/IR_TS_MO_6200192.nc";
		exec(file);
	}
	
	public static void exec(String Dataseturi) {
		try {
			DatasetSuggest result = BdoDatasetAnalyser.analyseDatasetNetcdf(Dataseturi);
			Gson gson  =new Gson();
			System.out.println(gson.toJson(result));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

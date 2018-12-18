package org.unibonn.bdo.bdodatasets;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jaime M Trillos
 * 
 * Performance test for Suggest metadata using LIMES and NER tools
 */

public class SuggestTestPerformance {
	
	private final static Logger log = LoggerFactory.getLogger(SuggestTestPerformance.class);
	
	@Test
	public void performanceTest1() {
		long time = 0;
		long before = System.currentTimeMillis();
		log.info("<Suggest Performance Test 1>");
		log.info("<Copernicus Dataset>");
		Suggest.main(new String[] {"http://resources.marine.copernicus.eu/?option=com_csw&view=details&tab=info&product_id=GLOBAL_ANALYSIS_FORECAST_PHY_001_024&format=xml", "Coppernicus"});
		long after = System.currentTimeMillis();
		time = after - before;
		convertMilisecondsToMinSec(time);
		log.info("</Suggest Performance Test 1>");
		log.info("------------------");
	}
	
	@Test
	public void performanceTest2() {
		long time = 0;
		long before = System.currentTimeMillis();
		log.info("<Suggest Performance Test 2>");
		log.info("<NETCDF Dataset>");
		Suggest.main(new String[] {"/home/eis/Documents/Datasets/mercatorbiomer4v1r1_glo_mean_20160709_R20160720.nc", "FileNetcdf"});
		long after = System.currentTimeMillis();
		time = after - before;
		convertMilisecondsToMinSec(time);
		log.info("</Suggest Performance Test 2>");
		log.info("------------------");
	}
	
	@Test
	public void performanceTest3() {
		long time = 0;
		long before = System.currentTimeMillis();
		log.info("<Suggest Performance Test 3>");
		log.info("<NETCDF Dataset>");
		Suggest.main(new String[] {"/home/eis/Documents/Datasets/GRIDONE_2D.nc", "FileNetcdf"});
		long after = System.currentTimeMillis();
		time = after - before;
		convertMilisecondsToMinSec(time);
		log.info("</Suggest Performance Test 3>");
		log.info("------------------");
	}
	
	@Test
	public void performanceTest4() {
		long time = 0;
		long before = System.currentTimeMillis();
		log.info("<Suggest Performance Test 4>");
		log.info("<NETCDF Dataset>");
		Suggest.main(new String[] {"/home/eis/Documents/Datasets/AR_201206_PR_CT_MYO_AR_58GS.nc", "FileNetcdf"});
		long after = System.currentTimeMillis();
		time = after - before;
		convertMilisecondsToMinSec(time);
		log.info("</Suggest Performance Test 4>");
		log.info("------------------");
	}
	
	@Test
	public void performanceTest5() {
		long time = 0;
		long before = System.currentTimeMillis();
		log.info("<Suggest Performance Test 5>");
		log.info("<CSV Dataset>");
		Suggest.main(new String[] {"/home/eis/Documents/Datasets/anek_history_20180101T102300_20180806T131000.csv", "FileCSV"});
		long after = System.currentTimeMillis();
		time = after - before;
		convertMilisecondsToMinSec(time);
		log.info("</Suggest Performance Test 5>");
		log.info("------------------");
	}

	@Test
	public void performanceTest6() {
		long time = 0;
		long before = System.currentTimeMillis();
		log.info("<Suggest Performance Test 6>");
		log.info("<EXCEL Dataset>");
		Suggest.main(new String[] {"/home/eis/Documents/Datasets/forecast_nc.xlsx", "FileExcel"});
		long after = System.currentTimeMillis();
		time = after - before;
		convertMilisecondsToMinSec(time);
		log.info("</Suggest Performance Test 6>");
		log.info("------------------");
	}

	// Convert miliseconds to minutes and seconds
	private static void convertMilisecondsToMinSec(long milisecond) {
        long minutes = (milisecond / 1000) / 60;
        long seconds = (milisecond / 1000) % 60;
        log.info("Time spend = " + minutes + " minutes and " + seconds + " seconds.");
	}
}

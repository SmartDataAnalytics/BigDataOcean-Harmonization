package org.unibonn.bdo.bdodatasets;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jaime M Trillos
 * 
 * Performance test for Suggest copernicus metadata using LIMES and NER tools
 * 
 */

public class SuggestTestCopernicusPerformance {
	
	private final static Logger log = LoggerFactory.getLogger(SuggestTestCopernicusPerformance.class);
	
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
		log.info("<Copernicus Dataset>");
		Suggest.main(new String[] {"http://resources.marine.copernicus.eu/?option=com_csw&view=details&tab=info&product_id=MEDSEA_ANALYSIS_FORECAST_WAV_006_017&format=xml", "Coppernicus"});
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
		log.info("<Copernicus Dataset>");
		Suggest.main(new String[] {"http://resources.marine.copernicus.eu/?option=com_csw&view=details&tab=info&product_id=ARCTIC_ANALYSIS_FORECAST_PHYS_002_001_a&format=xml", "Coppernicus"});
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
		log.info("<Copernicus Dataset>");
		Suggest.main(new String[] {"http://resources.marine.copernicus.eu/?option=com_csw&view=details&tab=info&product_id=BALTICSEA_ANALYSIS_FORECAST_PHY_003_006&format=xml", "Coppernicus"});
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
		log.info("<Copernicus Dataset>");
		Suggest.main(new String[] {"http://resources.marine.copernicus.eu/?option=com_csw&view=details&tab=info&product_id=NORTHWESTSHELF_ANALYSIS_FORECAST_PHY_004_013&format=xml", "Coppernicus"});
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
		log.info("<Copernicus Dataset>");
		Suggest.main(new String[] {"http://resources.marine.copernicus.eu/?option=com_csw&view=details&tab=info&product_id=IBI_ANALYSIS_FORECAST_PHYS_005_001&format=xml", "Coppernicus"});
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

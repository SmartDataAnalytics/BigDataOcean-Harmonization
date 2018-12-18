package org.unibonn.bdo.bdodatasets;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.unibonn.bdo.objects.Dataset;

/**
 * @author Jaime M Trillos
 * 
 * Unit test for Suggest metadata using LIMES and NER tools
 */

public class SuggestTest {
	
	private final static Logger log = LoggerFactory.getLogger(SuggestTest.class);
	private static Dataset result = null;
	private static String subjectResult = null;
	private static String keywordsResult = null;
	private static String geoLocationResult = null;
	private static List<String> variableResult = null;
	private static String[] token = null;
	
	@Test
	public void test1() {
		log.info("------------------");
		log.info("<Suggest Test 1>");
		log.info("<Copernicus Dataset>");
		try {
			result = BdoDatasetAnalyser.analyseDatasetURI("http://resources.marine.copernicus.eu/?option=com_csw&view=details&tab=info&product_id=GLOBAL_ANALYSIS_FORECAST_PHY_001_024&format=xml");
			subjectResult = result.getSubject();
			keywordsResult = result.getKeywords();
			geoLocationResult = result.getGeoLocation();
			token = subjectResult.split(",");
			assertEquals(1, token.length);
			token = keywordsResult.split(",");
			assertEquals(17, token.length);
			token = geoLocationResult.split(",");
			assertEquals(2, token.length);
			variableResult = result.getVariable();
			for (String l : variableResult) {
				token = splitVariablesbyCharacter(l, ",");
				assertEquals(false, token[2].equals(""));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		log.info("</Suggest Test 1>");
	}
	
	@Test
	public void test2() {
		log.info("<Suggest Test 2>");
		log.info("<NETCDF Dataset>");
		try {
			result = BdoDatasetAnalyser.analyseDatasetFileNetcdf("/home/eis/Documents/Datasets/mercatorbiomer4v1r1_glo_mean_20160709_R20160720.nc");
			subjectResult = result.getSubject();
			keywordsResult = result.getKeywords();
			geoLocationResult = result.getGeoLocation();
			token = subjectResult.split(",");
			assertEquals(1, token.length);
			token = keywordsResult.split(",");
			assertEquals(3, token.length);
			token = geoLocationResult.split(",");
			assertEquals(1, token.length);
			variableResult = result.getVariable();
			for (String l : variableResult) {
				token = splitVariablesbyCharacter(l, ",");
				assertEquals(false, token[2].equals(""));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		log.info("</Suggest Test 2>");
	}

	@Test
	public void test3() {
		log.info("<Suggest Test 3>");
		log.info("<NETCDF Dataset>");
		try {
			result = BdoDatasetAnalyser.analyseDatasetFileNetcdf("/home/eis/Documents/Datasets/GRIDONE_2D.nc");
			subjectResult = result.getSubject();
			keywordsResult = result.getKeywords();
			geoLocationResult = result.getGeoLocation();
			token = subjectResult.split(",");
			assertEquals(1, token.length);
			token = keywordsResult.split(",");
			assertEquals(4, token.length);
			assertEquals("", geoLocationResult);
			variableResult = result.getVariable();
			for (String l : variableResult) {
				token = splitVariablesbyCharacter(l, ",");
				assertEquals(false, token[2].equals(""));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		log.info("</Suggest Test 3>");
	}

	@Test
	public void test4() {
		log.info("<Suggest Test 4>");
		log.info("<NETCDF Dataset>");
		try {
			result = BdoDatasetAnalyser.analyseDatasetFileNetcdf("/home/eis/Documents/Datasets/AR_201206_PR_CT_MYO_AR_58GS.nc");
			subjectResult = result.getSubject();
			keywordsResult = result.getKeywords();
			geoLocationResult = result.getGeoLocation();
			token = subjectResult.split(",");
			assertEquals(1, token.length);
			token = keywordsResult.split(",");
			assertEquals(3, token.length);
			token = geoLocationResult.split(",");
			assertEquals(1, token.length);
			variableResult = result.getVariable();
			for (String l : variableResult) {
				token = splitVariablesbyCharacter(l, ",");
				assertEquals(false, token[2].equals(""));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		log.info("</Suggest Test 4>");
	}

	@Test
	public void test5() {
		log.info("<Suggest Test 5>");
		log.info("<CSV Dataset>");
		result = BdoDatasetAnalyser.analyseDatasetFileCsv("/home/eis/Documents/Datasets/anek_history_20180101T102300_20180806T131000.csv");
		subjectResult = result.getSubject();
		keywordsResult = result.getKeywords();
		geoLocationResult = result.getGeoLocation();
		assertEquals("", subjectResult);
		token = keywordsResult.split(",");
		assertEquals(1, token.length);
		assertEquals("", geoLocationResult);
		variableResult = result.getVariable();
		for (String l : variableResult) {
			token = splitVariablesbyCharacter(l, ",");
			assertEquals(false, token[2].equals(""));
		}
		log.info("</Suggest Test 5>");
	}
	
	@Test
	public void test6() {
		log.info("<Suggest Test 6>");
		log.info("<EXCEL Dataset>");
		try {
			result = BdoDatasetAnalyser.analyseDatasetFileExcel("/home/eis/Documents/Datasets/forecast_nc.xlsx");
			subjectResult = result.getSubject();
			keywordsResult = result.getKeywords();
			geoLocationResult = result.getGeoLocation();
			assertEquals("", subjectResult);
			token = keywordsResult.split(",");
			assertEquals(1, token.length);
			assertEquals("", geoLocationResult);
			variableResult = result.getVariable();
			for (String l : variableResult) {
				token = splitVariablesbyCharacter(l, ",");
				assertEquals(false, token[2].equals(""));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		log.info("</Suggest Test 6>");
		log.info("------------------");
	}
	
	// recursive method to extract the single values of each variable
	private static String[] splitVariablesbyCharacter(String text, String reg) {
		String[] list = text.split(reg);
		for(String t : list) {
			if(t.contains(" -- ")) {
				return splitVariablesbyCharacter(t, " -- ");
			}else {
				return list;
			}
		}
		return null;
	}
}

package org.unibonn.bdo.bdodatasets;

import org.unibonn.bdo.objects.DatasetApi;
import org.unibonn.bdo.objects.VariableDataset;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.junit.After;

/**
 * @author Jaime M Trillos
 * @author Ana C Trillos
 * 
 * Unit test for simple App for APIs.
 */
public class ApiTest {
	
	private final static Logger log = LoggerFactory.getLogger(ApiTest.class);
	
	private List<DatasetApi> testList;
	private DatasetApi datasetTest;
	private List<VariableDataset> listVar = new ArrayList<>();
	private boolean flag = false;

	@Before
	public void setUp() throws Exception {
		testList = new ArrayList<>();
		datasetTest = new DatasetApi();
		VariableDataset varData;
		varData = new VariableDataset("sea_surface_wave_significant_height", "sea_surface_wave_significant_height", "");
		listVar.add(varData);
		varData = new VariableDataset("sea_surface_wave_period_at_variance_spectral_density_maximum", "sea_surface_wave_period_at_variance_spectral_density_maximum", "");
		listVar.add(varData);
		varData = new VariableDataset("sea_surface_wave_mean_period_from_variance_spectral_density_inverse_frequency_moment", "sea_surface_wave_mean_period_from_variance_spectral_density_inverse_frequency_moment", "");
		listVar.add(varData);
		varData = new VariableDataset("sea_surface_wave_mean_period_from_variance_spectral_density_second_frequency_moment", "sea_surface_wave_mean_period_from_variance_spectral_density_second_frequency_moment", "");
		listVar.add(varData);
		varData = new VariableDataset("sea_surface_wave_from_direction", "sea_surface_wave_from_direction", "");
		listVar.add(varData);
		varData = new VariableDataset("sea_surface_wind_wave_significant_height", "sea_surface_wind_wave_significant_height", "");
		listVar.add(varData);
		varData = new VariableDataset("sea_surface_wind_wave_mean_period", "sea_surface_wind_wave_mean_period", "");
		listVar.add(varData);
		varData = new VariableDataset("sea_surface_wind_wave_from_direction", "sea_surface_wind_wave_from_direction", "");
		listVar.add(varData);
		varData = new VariableDataset("sea_surface_primary_swell_wave_significant_height", "sea_surface_primary_swell_wave_significant_height", "");
		listVar.add(varData);
		varData = new VariableDataset("sea_surface_primary_swell_wave_mean_period", "sea_surface_primary_swell_wave_mean_period", "");
		listVar.add(varData);
		varData = new VariableDataset("sea_surface_primary_swell_wave_from_direction", "sea_surface_primary_swell_wave_from_direction", "");
		listVar.add(varData);
		varData = new VariableDataset("sea_surface_secondary_swell_wave_from_direction", "sea_surface_secondary_swell_wave_from_direction", "");
		listVar.add(varData);
		varData = new VariableDataset("sea_surface_secondary_swell_wave_mean_period", "sea_surface_secondary_swell_wave_mean_period", "");
		listVar.add(varData);
		varData = new VariableDataset("sea_surface_secondary_swell_wave_significant_height", "sea_surface_secondary_swell_wave_significant_height", "");
		listVar.add(varData);
		varData = new VariableDataset("sea_surface_wave_from_direction_at_variance_spectral_density_maximum", "sea_surface_wave_from_direction_at_variance_spectral_density_maximum", "");
		listVar.add(varData);
		varData = new VariableDataset("sea_surface_wave_stokes_drift_x_velocity", "sea_surface_wave_stokes_drift_x_velocity", "");
		listVar.add(varData);
		varData = new VariableDataset("sea_surface_wave_stokes_drift_y_velocity","sea_surface_wave_stokes_drift_y_velocity", "");
		listVar.add(varData);
	}

	@Test
	public void test1() {
		log.info("Start testing API # 1");
		testList = BdoApiAnalyser.apiListAllFileDatasets();
		if (testList != null) {
			flag = true;
		}
		assertTrue(flag);
		log.info("End!");
	}
	
	@Test
	public void test2() {
		log.info("Start testing API # 2");
		datasetTest = BdoApiAnalyser.apiInfoFileDataset("bdo:MEDSEA_ANALYSIS_FORECAST_WAV_006_011");
		assertEquals("MEDSEA_ANALYSIS_FORECAST_WAV_006_011", datasetTest.getIdentifier());
		log.info("End!");
	}
	
	@Test
	public void test3() {
		log.info("Start testing API # 3");
		testList = BdoApiAnalyser.apiSearchSubjects("Oceans -- 10");
		if (testList != null) {
			flag = true;
		}
		assertTrue(flag);
		log.info("End!");
	}
	
	@Test
	public void test4() {
		log.info("Start testing API # 4");
		testList = BdoApiAnalyser.apiSearchKeywords("Mediterranean Sea -- 10");
		if (testList != null) {
			flag = true;
		}
		assertTrue(flag);
		log.info("End!");
	}
	
	@Test
	public void test5() {
		log.info("Start testing API # 5");
		testList = BdoApiAnalyser.apiSearchGeoLoc("Mediterranean Sea -- 10");
		if (testList != null) {
			flag = true;
		}
		assertTrue(flag);
		log.info("End!");
	}
	
	@Test
	public void test6() {
		log.info("Start testing API # 6");
		testList = BdoApiAnalyser.apiSearchGeoCoverage("-17.1,36.2,30,45.98 -- 10");
		if (testList != null) {
			flag = true;
		}
		assertTrue(flag);
		log.info("End!");
	}
	
	@Test
	public void test7() {
		log.info("Start testing API # 7");
		testList = BdoApiAnalyser.apiSearchVerticalCoverage("-6000,0 -- 10");
		if (testList != null) {
			flag = true;
		}
		assertTrue(flag);
		log.info("End!");		
	}
	
	@Test
	public void test8() {
		log.info("Start testing API # 8");
		testList = BdoApiAnalyser.apiSearchTemporalCoverage("2016-08-01T00:00:00 -- 10");
		if (testList != null) {
			flag = true;
		}
		assertTrue(flag);
		log.info("End!");		
	}
	
	@Test
	public void test9() {
		log.info("Start testing API # 9");
		datasetTest = BdoApiAnalyser.apiListFileDatasetVariables("MEDSEA_ANALYSIS_FORECAST_WAV_006_011");
		List<VariableDataset> varData = datasetTest.getVariables();
		assertEquals(listVar.get(0).getCanonicalName(), varData.get(0).getCanonicalName());		
	}
	
	@Test
	public void test10() {
		log.info("Start testing API # 10");
		testList = BdoApiAnalyser.apiSearchVariable("longitude,latitude -- 10");
		if (testList != null) {
			flag = true;
		}
		assertTrue(flag);
		log.info("End!");		
	}
	
	@After
	public void destroy() {
		testList.clear();
	}
	
}

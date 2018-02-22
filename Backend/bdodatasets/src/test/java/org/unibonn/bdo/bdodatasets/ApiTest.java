package org.unibonn.bdo.bdodatasets;

import org.unibonn.bdo.objects.Dataset;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.After;

public class ApiTest {
	
	private List<Dataset> testList;
	private Dataset datasetTest;
	private String searchParam;
	private List<String> listVar = new ArrayList<>();

	@Before
	public void setUp() throws Exception {
		testList = new ArrayList<>();
		datasetTest = new Dataset();
		listVar.add("sea_surface_wave_significant_height");
		listVar.add("sea_surface_wave_period_at_variance_spectral_density_maximum");
		listVar.add("sea_surface_wave_mean_period_from_variance_spectral_density_inverse_frequency_moment");
		listVar.add("sea_surface_wave_mean_period_from_variance_spectral_density_second_frequency_moment");
		listVar.add("sea_surface_wave_from_direction");
		listVar.add("sea_surface_wind_wave_significant_height");
		listVar.add("sea_surface_wind_wave_mean_period");
		listVar.add("sea_surface_wind_wave_from_direction");
		listVar.add("sea_surface_primary_swell_wave_significant_height");
		listVar.add("sea_surface_primary_swell_wave_mean_period");
		listVar.add("sea_surface_primary_swell_wave_from_direction");
		listVar.add("sea_surface_secondary_swell_wave_from_direction");
		listVar.add("sea_surface_secondary_swell_wave_mean_period");
		listVar.add("sea_surface_secondary_swell_wave_significant_height");
		listVar.add("sea_surface_wave_from_direction_at_variance_spectral_density_maximum");
		listVar.add("sea_surface_wave_stokes_drift_x_velocity");
		listVar.add("sea_surface_wave_stokes_drift_y_velocity");
		listVar.add("delayed_mode_or_real_time_data");
	}

	@Test
	public void test1() {
		try {
			testList = BdoApiAnalyser.apiListAllDatasets();
			assertEquals(3, testList.size());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void test2() {
		try {
			datasetTest = BdoApiAnalyser.apiSearchDataset("bdo:MEDSEA_ANALYSIS_FORECAST_WAV_006_011");
			assertEquals("MEDSEA_ANALYSIS_FORECAST_WAV_006_011", datasetTest.getIdentifier());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void test3() {
		testList = BdoApiAnalyser.apiSearchSubjects("http://inspire.ec.europa.eu/metadata-codelist/TopicCategory/oceans");
		assertEquals(2, testList.size());
	}
	
	@Test
	public void test4() {
		testList = BdoApiAnalyser.apiSearchKeywords("https://www.eionet.europa.eu/gemet/en/concept/14844");
		assertEquals(2, testList.size());
	}
	
	@Test
	public void test5() {
		testList = BdoApiAnalyser.apiSearchGeoLoc("http://marineregions.org/mrgid/1905");
		assertEquals(2, testList.size());
	}
	
	@Test
	public void test6() {
		testList = BdoApiAnalyser.apisearchGeoCoverage("-17.1, 36.2, 30, 45.98");
		assertEquals(1, testList.size());
	}
	
	@Test
	public void test7() {
		try {
			testList = BdoApiAnalyser.apiListDatasetByVertCov("-6000,= 0");
			assertEquals(2, testList.size());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	@Test
	public void test8() {
		try {
			testList = BdoApiAnalyser.apiListDatasetByTimeCov("2016-08-01T00:00:00,- ");
			assertEquals(1, testList.size());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	@Test
	public void test9() {
		try {
			datasetTest = BdoApiAnalyser.apiListVarOfDataset("MEDSEA_ANALYSIS_FORECAST_WAV_006_011");
			assertEquals(listVar, datasetTest.getVariable());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	@Test
	public void test10() {
		try {
			testList = BdoApiAnalyser.apiListDatasetsByVar("sea_surface_wave_significant_height, latitude");
			assertEquals(2, testList.size());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	@After
	public void destroy() {
		testList.clear();
	}

}

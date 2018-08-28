package org.unibonn.bdo.bdodatasets;

import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.json.simple.parser.ParseException;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.unibonn.bdo.objects.Dataset;

import com.mashape.unirest.http.exceptions.UnirestException;

/**
 * @author Jaime M Trillos
 * 
 * Unit test for insert metadata automatic using Kafka Connections.
 */

public class InsertAutomaticTest {
	private final static Logger log = LoggerFactory.getLogger(InsertAutomaticTest.class);
	
	String filename = "hdfs://212.101.173.50:9000/user/bdo/maretec/2017091300_20181131T125959_20181231T125959.csv";
	String idFile = "5ae051b39ac2555efd1a5926";
	String idProfile = "5b84217272a0920001e3d714";
	String jsonProfile = "{\"id\":\"5b7ffb5972a0920001014a47\",\"profileName\":\"hcmr_insitu\",\"title\":\"Med Sea - NRT in situ Observations\",\"description\":\"HCMR INSITU\",\"subject\":\"http://inspire.ec.europa.eu/metadata-codelist/TopicCategory/climatologyMeteorologyAtmosphere\",\"keywords\":\"https://www.eionet.europa.eu/gemet/en/concept/406\",\"standards\":\"CF-1.6 OceanSITES-Manual-1.2 Copernicus-InSituTAC-SRD-1.3 Copernicus-InSituTAC-ParametersList-3.0.0\",\"formats\":\"NetCDF\",\"language\":\"eng\",\"homepage\":\"http://www.hcmr.gr\",\"publisher\":\"OceanSITES\",\"source\":\"hcmr\",\"observation\":\"timeseries\",\"storageTable\":\"hcmr_timeseries\",\"accessRights\":\"\",\"geoLocation\":\"\",\"spatialWest\":\"25.1307\",\"spatialEast\":\"25.1307\",\"spatialSouth\":\"35.7263\",\"spatialNorth\":\"35.7263\",\"coordinateSystem\":\"\",\"verticalCoverageFrom\":\"-3.0\",\"verticalCoverageTo\":\"1000.0\",\"verticalLevel\":\"\",\"temporalCoverageBegin\":\"2018-07-01T00:00:00\",\"temporalCoverageEnd\":\"2018-07-31T21:00:00\",\"timeResolution\":\"daily\",\"variables\":[{\"name\":\"VTPK_QC\",\"canonicalName\":\"sea_surface_wave_period_at_variance_spectral_density_maximum_quality_flag\"},{\"name\":\"PSAL\",\"canonicalName\":\"sea_water_practical_salinity\"},{\"name\":\"VHM0\",\"canonicalName\":\"sea_surface_wave_significant_height\"},{\"name\":\"HCSP\",\"canonicalName\":\"sea_water_speed\"},{\"name\":\"VMDR\",\"canonicalName\":\"sea_surface_wave_from_direction\"},{\"name\":\"VMDR_QC\",\"canonicalName\":\"sea_surface_wave_from_direction_quality_flag\"},{\"name\":\"VTM02_QC\",\"canonicalName\":\"sea_surface_wave_mean_period_from_variance_spectral_density_second_frequency_moment_quality_flag\"},{\"name\":\"TEMP_QC\",\"canonicalName\":\"sea_water_temperature_quality_flag\"},{\"name\":\"PSAL_QC\",\"canonicalName\":\"sea_water_practical_salinity_quality_flag\"},{\"name\":\"ATMS_QC\",\"canonicalName\":\"air_pressure_at_sea_level_quality_flag\"},{\"name\":\"PHPH_DM\",\"canonicalName\":\"sea_water_ph_reported_on_total_scale_processing_method\"},{\"name\":\"VMDR_DM\",\"canonicalName\":\"sea_surface_wave_from_direction_processing_method\"},{\"name\":\"DOX1\",\"canonicalName\":\"volume_fraction_of_oxygen_in_sea_water\"},{\"name\":\"VHM0_DM\",\"canonicalName\":\"sea_surface_wave_significant_height_processing_method\"},{\"name\":\"VTPK\",\"canonicalName\":\"sea_surface_wave_period_at_variance_spectral_density_maximum\"},{\"name\":\"LONGITUDE\",\"canonicalName\":\"longitude\"},{\"name\":\"WDIR_QC\",\"canonicalName\":\"wind_from_direction_quality_flag\"},{\"name\":\"PRES_DM\",\"canonicalName\":\"sea_water_pressure_processing_method\"},{\"name\":\"HCSP_QC\",\"canonicalName\":\"sea_water_speed_quality_flag\"},{\"name\":\"DRYT_QC\",\"canonicalName\":\"air_temperature_quality_flag\"},{\"name\":\"FLU2\",\"canonicalName\":\"mass_concentration_of_chlorophyll_a_fluorescence_in_sea_water\"},{\"name\":\"GPS_LONGITUDE\",\"canonicalName\":\"automatically_measured_longitude\"},{\"name\":\"PHPH_QC\",\"canonicalName\":\"sea_water_ph_reported_on_total_scale_quality_flag\"},{\"name\":\"VTM02_DM\",\"canonicalName\":\"sea_surface_wave_mean_period_from_variance_spectral_density_second_frequency_moment_processing_method\"},{\"name\":\"HCDT\",\"canonicalName\":\"direction_of_sea_water_velocity\"},{\"name\":\"TUR4\",\"canonicalName\":\"sea_water_turbidity\"},{\"name\":\"DEPH_DM\",\"canonicalName\":\"manually_entered_depth_processing_method\"},{\"name\":\"PRES_QC\",\"canonicalName\":\"sea_water_pressure_quality_flag\"},{\"name\":\"DEPH\",\"canonicalName\":\"manually_entered_depth\"},{\"name\":\"TEMP\",\"canonicalName\":\"sea_water_temperature\"},{\"name\":\"HCDT_QC\",\"canonicalName\":\"direction_of_sea_water_velocity_quality_flag\"},{\"name\":\"WSPD_QC\",\"canonicalName\":\"wind_speed_quality_flag\"},{\"name\":\"DOX1_QC\",\"canonicalName\":\"volume_fraction_of_oxygen_in_sea_water_quality_flag\"},{\"name\":\"VZMX_QC\",\"canonicalName\":\"sea_surface_wave_maximum_height_quality_flag\"},{\"name\":\"VZMX\",\"canonicalName\":\"sea_surface_wave_maximum_height\"},{\"name\":\"ATMS\",\"canonicalName\":\"air_pressure_at_sea_level\"},{\"name\":\"GPS_LATITUDE\",\"canonicalName\":\"automatically_measured_latitude\"},{\"name\":\"DOX1_DM\",\"canonicalName\":\"volume_fraction_of_oxygen_in_sea_water_processing_method\"},{\"name\":\"GSPD\",\"canonicalName\":\"wind_speed_of_gust\"},{\"name\":\"TIME_QC\",\"canonicalName\":\"time_quality_flag\"},{\"name\":\"GSPD_DM\",\"canonicalName\":\"wind_speed_of_gust_processing_method\"},{\"name\":\"PSAL_DM\",\"canonicalName\":\"sea_water_practical_salinity_processing_method\"},{\"name\":\"POSITION_QC\",\"canonicalName\":\"position_quality_flag\"},{\"name\":\"TEMP_DM\",\"canonicalName\":\"sea_water_temperature_processing_method\"},{\"name\":\"DRYT\",\"canonicalName\":\"air_temperature\"},{\"name\":\"HCDT_DM\",\"canonicalName\":\"direction_of_sea_water_velocity_processing_method\"},{\"name\":\"WDIR\",\"canonicalName\":\"wind_from_direction\"},{\"name\":\"VTPK_DM\",\"canonicalName\":\"sea_surface_wave_period_at_variance_spectral_density_maximum_processing_method\"},{\"name\":\"VZMX_DM\",\"canonicalName\":\"sea_surface_wave_maximum_height_processing_method\"},{\"name\":\"VTM02\",\"canonicalName\":\"sea_surface_wave_mean_period_from_variance_spectral_density_second_frequency_moment\"},{\"name\":\"ATMS_DM\",\"canonicalName\":\"air_pressure_at_sea_level_processing_method\"},{\"name\":\"PRES\",\"canonicalName\":\"sea_water_pressure\"},{\"name\":\"WSPD\",\"canonicalName\":\"wind_speed\"},{\"name\":\"LATITUDE\",\"canonicalName\":\"latitude\"},{\"name\":\"WDIR_DM\",\"canonicalName\":\"wind_from_direction_processing_method\"},{\"name\":\"GPS_POSITION_QC\",\"canonicalName\":\"automatically_measured_position_quality_flag\"},{\"name\":\"GSPD_QC\",\"canonicalName\":\"wind_speed_of_gust_quality_flag\"},{\"name\":\"WSPD_DM\",\"canonicalName\":\"wind_speed_processing_method\"},{\"name\":\"DRYT_DM\",\"canonicalName\":\"air_temperature_processing_method\"},{\"name\":\"TUR4_QC\",\"canonicalName\":\"sea_water_turbidity_quality_flag\"},{\"name\":\"FLU2_QC\",\"canonicalName\":\"mass_concentration_of_chlorophyll_a_fluorescence_in_sea_water_quality_flag\"},{\"name\":\"TUR4_DM\",\"canonicalName\":\"sea_water_turbidity_processing_method\"},{\"name\":\"PHPH\",\"canonicalName\":\"sea_water_ph_reported_on_total_scale\"},{\"name\":\"VHM0_QC\",\"canonicalName\":\"sea_surface_wave_significant_height_quality_flag\"},{\"name\":\"FLU2_DM\",\"canonicalName\":\"mass_concentration_of_chlorophyll_a_fluorescence_in_sea_water_processing_method\"},{\"name\":\"TIME\",\"canonicalName\":\"time\"},{\"name\":\"HCSP_DM\",\"canonicalName\":\"sea_water_speed_processing_method\"},{\"name\":\"DEPH_QC\",\"canonicalName\":\"manually_entered_depth_quality_flag\"}]}";
	private boolean flag = false;
	
	//Testing if insert metadata with filename, idFile and idProfile into harmonization tool
	@Test
	public void test1() {
		try {
			log.info("Start testing if insert metadata with filename, idFile and idProfile into harmonization tool");
			Boolean response = InsertDatasetAutomatic.analyseInsertDatasetAutomatic(filename, idFile, idProfile);
			if (response) {
				flag = true;
			}
			assertTrue(flag);
			log.info("End!");
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnirestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (java.text.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//Testing if convert correctly the jsonprofile into Dataset object
	@Test
	public void test2(){
		try {
			log.info("Start testing if convert correctly the jsonprofile into Dataset object");
			Dataset data = InsertDatasetAutomatic.convertProfileToDataset(jsonProfile);
			if (data != null) {
				if (data.getTitle().equals("Med Sea - NRT in situ Observations")) {
					flag = true;
				}
			}
			assertTrue(flag);
			log.info("End!");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

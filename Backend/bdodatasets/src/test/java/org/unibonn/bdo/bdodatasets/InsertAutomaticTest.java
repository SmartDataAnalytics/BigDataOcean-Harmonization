package org.unibonn.bdo.bdodatasets;

import static org.junit.Assert.assertTrue;

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
	
	String filename = "";
	String idFile = "";
	String idProfile = "";
	String jsonProfile = "";
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
		} catch (ParseException | IOException | UnirestException e) {
			e.printStackTrace();
		}
	}
	
	//Testing if convert correctly the jsonprofile into Dataset object
	@Test
	public void test2(){
		log.info("Start testing if convert correctly the jsonprofile into Dataset object");
		Dataset data = InsertDatasetAutomatic.convertProfileToDataset(jsonProfile);
		if (data != null) {
			if (data.getTitle().equals("Med Sea - NRT in situ Observations")) {
				flag = true;
			}
		}
		assertTrue(flag);
		log.info("End!");
	}
}

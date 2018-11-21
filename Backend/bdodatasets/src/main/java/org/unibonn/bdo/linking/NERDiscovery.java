package org.unibonn.bdo.linking;

import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import edu.stanford.nlp.ie.NERClassifierCombiner;
import edu.stanford.nlp.io.IOUtils;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.util.Triple;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.apache.log4j.LogManager;
import org.unibonn.bdo.bdodatasets.Constants;

/**
 *  
 * @author Jaime M Trillos
 *
 * Create file, execute Stanford NER and return list of entities
 *
 */

public class NERDiscovery {
	
	private static String fileTxt = "";
	
	// function that create file, execute Stanford NER and list of entities
	public static List<String> exec(String rawDescription){
		// Delete logs from edu.standford
		LogManager.getLogger("edu.stanford").setLevel(org.apache.log4j.Level.OFF);
		List<String> resultNER = new ArrayList<>();
		try {
			createTxtFile(rawDescription);
			resultNER = extractEntities();
			Files.deleteIfExists(Paths.get(fileTxt));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resultNER;
	}
	
	// Create the csv file that contains the raw description extracted from a dataset file
	private static void createTxtFile(String text) {
		try {
			fileTxt = Constants.CONFIGFILEPATH + "/Backend/AddDatasets/tempNERBDO.txt";
			FileWriter writer = new FileWriter(fileTxt);
			writer.write(text);
		    writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	// Find entities of the txt file
	private static List<String> extractEntities() {
		AbstractSequenceClassifier<CoreLabel> classifier;
		List<String> responseList = new ArrayList<>();
		try {
			classifier = new NERClassifierCombiner(true, false, false, Constants.CONFIGFILEPATH + "/Backend/AddDatasets/classifiers/english.all.3class.distsim.crf.ser.gz", Constants.CONFIGFILEPATH + "/Backend/AddDatasets/classifiers/bdo_model_ner.ser.gz");

			String fileContents = IOUtils.slurpFile(fileTxt);
			List<Triple<String, Integer, Integer>> list = classifier.classifyToCharacterOffsets(fileContents);
			for (Triple<String, Integer, Integer> item : list) {
				String text = fileContents.substring(item.second(), item.third()).toLowerCase();
				if(text.split(" ").length >= 3) {
					for(String m : text.split(" ")) {
						responseList.add(m);
					}
				}else {
					responseList.add(fileContents.substring(item.second(), item.third()).toLowerCase());
				}
			}
			responseList = removeDuplicates(responseList);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return responseList;
	}
	
	
	// Delete duplicate entities
	private static List<String> removeDuplicates(List<String> list) {
	    return new ArrayList<>(new HashSet<>(list));
	}

}

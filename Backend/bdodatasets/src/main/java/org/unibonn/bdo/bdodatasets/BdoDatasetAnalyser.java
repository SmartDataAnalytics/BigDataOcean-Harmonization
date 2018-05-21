package org.unibonn.bdo.bdodatasets;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.hadoop.fs.Path;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;
import org.unibonn.bdo.objects.Dataset;

import ucar.nc2.Attribute;
import ucar.nc2.NetcdfFile;
import ucar.nc2.Variable;
import ucar.nc2.dataset.NetcdfDataset;

import org.unibonn.bdo.bdodatasets.Constants;

/*import ucar.netcdf.Attribute;
import ucar.netcdf.NetcdfFile;*/

/**
 * 
 * @author Jaime M Trillos
 * @author Ana C Trillos
 *
 * Parse XML file from Copernicus dataset and NetCDF to obtain Metadata
 *
 */

public class BdoDatasetAnalyser {

	public static Dataset analyseDatasetURI(String datasetURI) throws IOException {
		Dataset result = new Dataset();

		String delims; //Delimiters for extracting some part of the text
		String[] tokens; //list of pieces of information
		String title;
		String description;
		String identifier;
		String issued;
		String language;
		String coordinateSystem;
		String spatialWestBoundLongitude;
		String spatialEastBoundLongitude;
		String spatialSouthBoundLatitude;
		String spatialNorthBoundLatitude;
		String temporalCoverageBegin;
		String temporalCoverageEnd;
		String publisher;
		String verticalCoverage;
		String verticalCoverageFrom;
		String verticalCoverageTo;
		String verticalLevel;
		String timeResolution;
		Elements variablesElements;
		List<String> variables = new ArrayList<>();

		//Read the URI and return the HTML/XML document
//		Document doc = Jsoup.connect(datasetURI).get();
		Document doc = Jsoup.parse(new URL(datasetURI).openStream(), "UTF-8", "", Parser.xmlParser());

		//Search parent tag 
		Element item = doc.getElementsByTag("gmd:identificationInfo").first();

		//Extract values from tags
		title = item.getElementsByTag("gmd:title").first().text();
		String totaldescription = item.getElementsByTag("gmd:abstract").first().text();

		delims = "'''";
		tokens = totaldescription.split(delims);
		description = tokens[2];

		identifier = item.getElementsByTag("gmd:alternateTitle").first().text();
		issued = item.getElementsByTag("gmd:date").first().text()+"T00:00:00";
		language = item.getElementsByTag("gmd:language").first().text();
		spatialWestBoundLongitude = item.getElementsByTag("gmd:westBoundLongitude").first().text();
		spatialEastBoundLongitude = item.getElementsByTag("gmd:eastBoundLongitude").first().text();
		spatialSouthBoundLatitude = item.getElementsByTag("gmd:southBoundLatitude").first().text();
		spatialNorthBoundLatitude = item.getElementsByTag("gmd:northBoundLatitude").first().text();

		Element item2 = item.getElementsByTag("gml:timePeriod").first();
		temporalCoverageBegin = item2.getElementsByTag("gml:beginPosition").first().text();
		temporalCoverageBegin += "T00:00:00";
		temporalCoverageEnd = item2.getElementsByTag("gml:endPosition").first().text();
		if(temporalCoverageEnd.length()>0) {
			temporalCoverageEnd += "T00:00:00";
		}

		verticalCoverage = item.getElementsByTag("gmd:EX_VerticalExtent").text();

		delims = " ";
		tokens = verticalCoverage.split(delims);
		verticalCoverageFrom = tokens[0];
		verticalCoverageTo = tokens[1];

		Element item3 = doc.getElementsByTag("gmd:referenceSystemInfo").first();
		coordinateSystem = item3.getElementsByTag("gmd:code").text();

		Element item5 = doc.getElementsByTag("gmd:contact").first();
		publisher = item5.getElementsByTag("gmd:organisationName").text();

		Element item6 = doc.getElementsByTag("gmd:contentInfo").first();
		timeResolution = item6.getElementsByTag("gmd:dimension").first().text();

		delims = ": ";
		tokens = timeResolution.split(delims);
		timeResolution = tokens[1];

		verticalLevel = item6.getElementsByTag("gmd:dimension").next().text();

		delims = ": ";
		tokens = verticalLevel.split(delims);
		verticalLevel = tokens[1];

		Element item7 = doc.getElementsByTag("gmd:descriptiveKeywords").get(2);
		variablesElements = item7.getElementsByTag("gmx:Anchor");

		String replace = variablesElements.text().replaceAll(" ", ",");
		
		delims = ",geonetwork.thesaurus.external.parameter.myocean.ocean-variables";
		tokens = replace.split(delims);
		delims = tokens[0];
		String[] list = delims.split(",");
		for (int i=0; i<list.length; i++) {
			variables.add(list[i]);
		}

		result.setTitle(title);
		result.setDescription(description);
		result.setHomepage(datasetURI);
		result.setIdentifier(identifier);
		result.setIssuedDate(issued);
		result.setModifiedDate(issued);
		result.setLanguage(language);
		result.setCoordinateSystem(coordinateSystem);
		result.setSpatialWest(spatialWestBoundLongitude);
		result.setSpatialEast(spatialEastBoundLongitude);
		result.setSpatialSouth(spatialSouthBoundLatitude);
		result.setSpatialNorth(spatialNorthBoundLatitude);
		result.setPublisher(publisher);
		result.setTemporalCoverageBegin(temporalCoverageBegin);
		result.setTemporalCoverageEnd(temporalCoverageEnd);
		result.setVerticalCoverageFrom(verticalCoverageFrom);
		result.setVerticalCoverageTo(verticalCoverageTo);
		result.setTimeResolution(timeResolution);
		result.setVerticalLevel(verticalLevel);
		result.setVariable(variables);

		return result;
	}

	public static Dataset analyseDatasetNetcdf(String filename) throws IOException, ParseException {
		Dataset result = new Dataset();
		String title = "";
		String description = "";
		String identifier = "";
		String issued = "";
		String modified = "";
		String keywords = "";
		String homepage = "";
		String standards = "";
		String format = "";
		String spatialWestBoundLongitude = "";
		String spatialEastBoundLongitude = "";
		String spatialSouthBoundLatitude = "";
		String spatialNorthBoundLatitude = "";
		String temporalCoverageBegin = "";
		String temporalCoverageEnd = "";
		String publisher = "";
		String verticalCoverageFrom = "";
		String verticalCoverageTo = "";
		String timeResolution = "";
		List<String> variables = new ArrayList<>();
		List<String> listVariables = new ArrayList<>();

		//read the file
		NetcdfFile nc = null;
		try {
			HDFSFileSystem hdfsSys = new HDFSFileSystem(filename);
			Path localFile = hdfsSys.copyFile(filename,Constants.configFilePath+"/Backend/AddDatasets/file.nc");
			//read NetCDF file to get its metadata
			nc = NetcdfDataset.openFile(localFile.toString(), null);
			
			List<Variable> allVariables;
			
			//find the attributes and export the information to create the DatasetSuggest
			List<Attribute> listFileMetadata = nc.getGlobalAttributes();
			if(listFileMetadata != null)
			{
				for(Attribute attr : listFileMetadata) {					
					if(attr.getShortName().toLowerCase().equals("id")) {
						identifier = attr.getStringValue();
					}
					if(attr.getShortName().toLowerCase().equals("title")) {
						title = attr.getStringValue();
					}
					if(attr.getShortName().toLowerCase().equals("summary")) {
						description = attr.getStringValue();
						if (description.length() == 1) {
							description = "";
						}
					}
					if(attr.getShortName().toLowerCase().equals("area")) {
						keywords = attr.getStringValue();
					}
					if(attr.getShortName().toLowerCase().equals("conventions")) {
						standards = attr.getStringValue();
					}
					format = "NetCDF";
					if(attr.getShortName().toLowerCase().equals("institution_references")) {
						homepage = attr.getStringValue();
					}
					if(attr.getShortName().toLowerCase().equals("naming_authority")) {
						publisher = attr.getStringValue();
					}
					if(attr.getShortName().toLowerCase().equals("history")) {
						issued = attr.getStringValue().substring(0, 19);
						if(!(issued.substring(4,5).equals("-") && issued.substring(7,8).equals("-") && issued.substring(10,11).equals("T") && issued.substring(13,14).equals(":") && issued.substring(16,17).equals(":"))) {
							issued = "";
						}
					}
					if(attr.getShortName().toLowerCase().equals("date_update")) {
						modified = attr.getStringValue().substring(0, 19);
						if(!(modified.substring(4,5).equals("-") && modified.substring(7,8).equals("-") && modified.substring(10,11).equals("T") && modified.substring(13,14).equals(":") && modified.substring(16,17).equals(":"))) {
							modified = "";
						}
					}
					if(attr.getShortName().toLowerCase().equals("geospatial_lon_min") || attr.getShortName().toLowerCase().equals("longitude_min")) {
						spatialWestBoundLongitude = attr.getValues().toString().replaceAll(" ", "");
					}
					if(attr.getShortName().toLowerCase().equals("geospatial_lon_max") || attr.getShortName().toLowerCase().equals("longitude_max")) {
						spatialEastBoundLongitude = attr.getValues().toString().replaceAll(" ", "");
					}
					if(attr.getShortName().toLowerCase().equals("geospatial_lat_min") || attr.getShortName().toLowerCase().equals("latitude_min")) {
						spatialSouthBoundLatitude = attr.getValues().toString().replaceAll(" ", "");
					}
					if(attr.getShortName().toLowerCase().equals("geospatial_lat_max") || attr.getShortName().toLowerCase().equals("latitude_max")) {
						spatialNorthBoundLatitude = attr.getValues().toString().replaceAll(" ", "");
					}
					if(attr.getShortName().toLowerCase().equals("geospatial_vertical_min")) {
						verticalCoverageFrom = attr.getStringValue();
						if (verticalCoverageFrom.length() == 1) {
							verticalCoverageFrom = "";
						}
					}				
					if(attr.getShortName().toLowerCase().equals("geospatial_vertical_max")) {
						verticalCoverageTo = attr.getStringValue();
						if (verticalCoverageTo.length() == 1) {
							verticalCoverageTo = "";
						}
					}				
					if(attr.getShortName().toLowerCase().equals("time_coverage_start")) {
						temporalCoverageBegin = attr.getStringValue().substring(0, 19);
						if(!(temporalCoverageBegin.substring(4,5).equals("-") && temporalCoverageBegin.substring(7,8).equals("-") && temporalCoverageBegin.substring(10,11).equals("T") && temporalCoverageBegin.substring(13,14).equals(":") && temporalCoverageBegin.substring(16,17).equals(":"))) {
							temporalCoverageBegin = "";
						}
					}
					if(attr.getShortName().toLowerCase().equals("time_coverage_end")) {
						temporalCoverageEnd = attr.getStringValue().substring(0, 19);
						if(!(temporalCoverageEnd.substring(4,5).equals("-") && temporalCoverageEnd.substring(7,8).equals("-") && temporalCoverageEnd.substring(10,11).equals("T") && temporalCoverageEnd.substring(13,14).equals(":") && temporalCoverageEnd.substring(16,17).equals(":"))) {
							temporalCoverageEnd = "";
						}
					}
					if(attr.getShortName().toLowerCase().equals("update_interval")) {
						timeResolution = attr.getStringValue();
					}
				}
				
				//return a list with all variables
				allVariables = nc.getVariables();
				for (int i=0; i<allVariables.size(); i++) {
					//select only the standard_name of the variables
					Attribute standard_name = allVariables.get(i).findAttribute("standard_name");
			    	if(standard_name !=null) {
			    		if(standard_name.getStringValue() != "" && !standard_name.getStringValue().startsWith(" ")) {
			    			//add the standard_name value if it is not null or ""
			    			variables.add(standard_name.getStringValue());
			    		}
			    	}
			    }
				//Verify and delete if there are duplicates
				HashSet<String> hs = new HashSet<String>();
				hs.addAll(variables);
				variables.clear();
				variables.addAll(hs);
				
				//Extracting the array of keywords find in the json file
				JSONParser parser = new JSONParser();
				JSONArray variablesCF = (JSONArray) parser.parse(new FileReader(Constants.configFilePath+"/Frontend/Flask/static/json/variablesCF_BDO.json"));
				
				for(int j=0; j<variables.size(); j++) {
					boolean flag = false;
					for(int i=0; i<variablesCF.size(); i++){
			        	JSONObject keyword = (JSONObject) variablesCF.get(i);
			            String text = keyword.get("text").toString();
			            if(text.equals(variables.get(j))) {
			            	listVariables.add(variables.get(j).toString() + " -- "+ keyword.get("text").toString());
			            	flag = true;
			            	break;
			            }		            	
			        }
					if(flag == false) {
						listVariables.add(variables.get(j).toString() + " -- "+ "");
					}
				}
				
				JSONArray keywordsArray = (JSONArray) parser.parse(new FileReader(Constants.configFilePath+"/Frontend/Flask/static/json/keywords.json"));
	            
				/*search if the keyword extracted from netcdf is equal to the json
				* change the value of the keyword variable to the value of the json (http://...)
				*/
				for(int i=0; i<keywordsArray.size(); i++){
	            	boolean flag = false;
	                JSONObject keyword = (JSONObject) keywordsArray.get(i);
	                String text = keyword.get("text").toString();
	                if(text.equals(keywords)) {
	                	keywords = keyword.get("value").toString();
	                	flag = true;
	                	break;
	                }
	                if(flag == false) {
	                	keywords = "";
	                }
	            }
	            
				result.setIdentifier(identifier);
				result.setTitle(title);
				result.setDescription(description);
				result.setKeywords(keywords);
				result.setStandards(standards);
				result.setFormats(format);
				result.setHomepage(homepage);
				result.setPublisher(publisher);
				result.setIssuedDate(issued);
				result.setModifiedDate(modified);
				result.setSpatialWest(spatialWestBoundLongitude);
				result.setSpatialEast(spatialEastBoundLongitude);
				result.setSpatialSouth(spatialSouthBoundLatitude);
				result.setSpatialNorth(spatialNorthBoundLatitude);
				result.setVerticalCoverageFrom(verticalCoverageFrom);
				result.setVerticalCoverageTo(verticalCoverageTo);
				result.setTemporalCoverageBegin(temporalCoverageBegin);
				result.setTemporalCoverageEnd(temporalCoverageEnd);
				result.setTimeResolution(timeResolution);
				result.setVariable(listVariables);
			}
			//Delete the temporal file "file.nc"
			hdfsSys.deleteFile(Constants.configFilePath+"/Backend/AddDatasets/file.nc");
			
		} catch (IOException ioe) {

		} finally { 
			if (null != nc) try {
				nc.close();
			} catch (IOException ioe) {

			}
		}

		return result;
	}
	
	public static Dataset analyseDatasetFileNetcdf(String filename) throws IOException, ParseException {
		Dataset result = new Dataset();
		String title = "";
		String description = "";
		String identifier = "";
		String issued = "";
		String modified = "";
		String keywords = "";
		String homepage = "";
		String standards = "";
		String format = "";
		String spatialWestBoundLongitude = "";
		String spatialEastBoundLongitude = "";
		String spatialSouthBoundLatitude = "";
		String spatialNorthBoundLatitude = "";
		String temporalCoverageBegin = "";
		String temporalCoverageEnd = "";
		String publisher = "";
		String verticalCoverageFrom = "";
		String verticalCoverageTo = "";
		String timeResolution = "";
		List<String> variables = new ArrayList<>();
		List<String> listVariables = new ArrayList<>() ;

		//read the file
		NetcdfFile nc = null;
		try {
			nc = NetcdfDataset.openFile(filename, null);
			
			List<Variable> allVariables;
			
			//find the attributes and export the information to create the DatasetSuggest
			List<Attribute> listFileMetadata = nc.getGlobalAttributes();
			if(listFileMetadata != null)
			{
				for(Attribute attr : listFileMetadata) {					
					if(attr.getShortName().toLowerCase().equals("id")) {
						identifier = attr.getStringValue();
					}
					if(attr.getShortName().toLowerCase().equals("title")) {
						title = attr.getStringValue();
					}
					if(attr.getShortName().toLowerCase().equals("summary")) {
						description = attr.getStringValue();
						if (description.length() == 1) {
							description = "";
						}
					}
					if(attr.getShortName().toLowerCase().equals("area")) {
						keywords = attr.getStringValue();
					}
					if(attr.getShortName().toLowerCase().equals("conventions")) {
						standards = attr.getStringValue();
					}
					format = "NetCDF";
					if(attr.getShortName().toLowerCase().equals("institution_references")) {
						homepage = attr.getStringValue();
					}
					if(attr.getShortName().toLowerCase().equals("naming_authority")) {
						publisher = attr.getStringValue();
					}
					if(attr.getShortName().toLowerCase().equals("history")) {
						issued = attr.getStringValue().substring(0, 19);
						if(!(issued.substring(4,5).equals("-") && issued.substring(7,8).equals("-") && issued.substring(10,11).equals("T") && issued.substring(13,14).equals(":") && issued.substring(16,17).equals(":"))) {
							issued = "";
						}
					}
					if(attr.getShortName().toLowerCase().equals("date_update")) {
						modified = attr.getStringValue().substring(0, 19);
						if(!(modified.substring(4,5).equals("-") && modified.substring(7,8).equals("-") && modified.substring(10,11).equals("T") && modified.substring(13,14).equals(":") && modified.substring(16,17).equals(":"))) {
							modified = "";
						}
					}
					if(attr.getShortName().toLowerCase().equals("geospatial_lon_min") || attr.getShortName().toLowerCase().equals("longitude_min")) {
						spatialWestBoundLongitude = attr.getValues().toString().replace(" ", "");
					}
					if(attr.getShortName().toLowerCase().equals("geospatial_lon_max") || attr.getShortName().toLowerCase().equals("longitude_max")) {
						spatialEastBoundLongitude = attr.getValues().toString().replace(" ", "");
					}
					if(attr.getShortName().toLowerCase().equals("geospatial_lat_min") || attr.getShortName().toLowerCase().equals("latitude_min")) {
						spatialSouthBoundLatitude = attr.getValues().toString().replace(" ", "");
					}
					if(attr.getShortName().toLowerCase().equals("geospatial_lat_max") || attr.getShortName().toLowerCase().equals("latitude_max")) {
						spatialNorthBoundLatitude = attr.getValues().toString().replace(" ", "");
					}
					if(attr.getShortName().toLowerCase().equals("geospatial_vertical_min")) {
						verticalCoverageFrom = attr.getStringValue();
						if (verticalCoverageFrom.length() == 1) {
							verticalCoverageFrom = "";
						}
					}				
					if(attr.getShortName().toLowerCase().equals("geospatial_vertical_max")) {
						verticalCoverageTo = attr.getStringValue();
						if (verticalCoverageTo.length() == 1) {
							verticalCoverageTo = "";
						}
					}				
					if(attr.getShortName().toLowerCase().equals("time_coverage_start")) {
						temporalCoverageBegin = attr.getStringValue().substring(0, 19);
						if(!(temporalCoverageBegin.substring(4,5).equals("-") && temporalCoverageBegin.substring(7,8).equals("-") && temporalCoverageBegin.substring(10,11).equals("T") && temporalCoverageBegin.substring(13,14).equals(":") && temporalCoverageBegin.substring(16,17).equals(":"))) {
							temporalCoverageBegin = "";
						}
					}
					if(attr.getShortName().toLowerCase().equals("time_coverage_end")) {
						temporalCoverageEnd = attr.getStringValue().substring(0, 19);
						if(!(temporalCoverageEnd.substring(4,5).equals("-") && temporalCoverageEnd.substring(7,8).equals("-") && temporalCoverageEnd.substring(10,11).equals("T") && temporalCoverageEnd.substring(13,14).equals(":") && temporalCoverageEnd.substring(16,17).equals(":"))) {
							temporalCoverageEnd = "";
						}
					}
					if(attr.getShortName().toLowerCase().equals("update_interval")) {
						timeResolution = attr.getStringValue();
					}
				}
				
				//return a list with all variables
				allVariables = nc.getVariables();
				for (int i=0; i<allVariables.size(); i++) {
					//select only the standard_name of the variables
					Attribute standard_name = allVariables.get(i).findAttribute("standard_name");
			    	if(standard_name != null) {
			    		if(standard_name.getStringValue() != "" && !standard_name.getStringValue().startsWith(" ")) {
			    			//add the standard_name value if it is not null or ""
			    			variables.add(standard_name.getStringValue());
			    		}
			    	}
			    }
				//Verify and delete if there are duplicates
				HashSet<String> hs = new HashSet<String>();
				hs.addAll(variables);
				variables.clear();
				variables.addAll(hs);
				
				
				//Extracting the array of keywords and CF variable find in the json file
				JSONParser parser = new JSONParser();
				JSONArray variablesCF = (JSONArray) parser.parse(new FileReader(Constants.configFilePath+"/Frontend/Flask/static/json/variablesCF_BDO.json"));
				
				for(int j=0; j<variables.size(); j++) {
					boolean flag = false;
					for(int i=0; i<variablesCF.size(); i++){
			        	JSONObject keyword = (JSONObject) variablesCF.get(i);
			            String text = keyword.get("text").toString();
			            if(text.equals(variables.get(j))) {
			            	listVariables.add(variables.get(j).toString() + " -- "+ keyword.get("text").toString());
			            	flag = true;
			            	break;
			            }		            	
			        }
					if(flag == false) {
						listVariables.add(variables.get(j).toString() + " -- "+ "");
					}
				}
				
				
				JSONArray keywordsArray = (JSONArray) parser.parse(new FileReader(Constants.configFilePath+"/Frontend/Flask/static/json/keywords.json"));
	            
				/*search if the keyword extracted from netcdf is equal to the json
				* change the value of the keyword variable to the value of the json (http://...)
				*/
	            for(int i=0; i<keywordsArray.size(); i++){
	            	boolean flag = false;
	                JSONObject keyword = (JSONObject) keywordsArray.get(i);
	                String text = keyword.get("text").toString();
	                if(text.equals(keywords)) {
	                	keywords = keyword.get("value").toString();
	                	flag = true;
	                	break;
	                }
	                if(flag == false) {
	                	keywords = "";
	                }
	            }
	            
				result.setIdentifier(identifier);
				result.setTitle(title);
				result.setDescription(description);
				result.setKeywords(keywords);
				result.setStandards(standards);
				result.setFormats(format);
				result.setHomepage(homepage);
				result.setPublisher(publisher);
				result.setIssuedDate(issued);
				result.setModifiedDate(modified);
				result.setSpatialWest(spatialWestBoundLongitude);
				result.setSpatialEast(spatialEastBoundLongitude);
				result.setSpatialSouth(spatialSouthBoundLatitude);
				result.setSpatialNorth(spatialNorthBoundLatitude);
				result.setVerticalCoverageFrom(verticalCoverageFrom);
				result.setVerticalCoverageTo(verticalCoverageTo);
				result.setTemporalCoverageBegin(temporalCoverageBegin);
				result.setTemporalCoverageEnd(temporalCoverageEnd);
				result.setTimeResolution(timeResolution);
				result.setVariable(listVariables);
			}
			//Delete the temporal file "file.nc"
			Files.deleteIfExists(Paths.get(Constants.configFilePath+"/Backend/AddDatasets/file.nc"));
			
		} catch (IOException ioe) {

		} finally { 
			if (null != nc) try {
				nc.close();
			} catch (IOException ioe) {

			}
		}

		return result;
	}

}

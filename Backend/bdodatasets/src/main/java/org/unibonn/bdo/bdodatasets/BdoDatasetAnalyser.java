package org.unibonn.bdo.bdodatasets;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.hadoop.fs.Path;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
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

import au.com.bytecode.opencsv.CSVReader;
import ucar.nc2.Attribute;
import ucar.nc2.NetcdfFile;
import ucar.nc2.Variable;
import ucar.nc2.dataset.NetcdfDataset;

import org.unibonn.bdo.connections.HDFSFileSystem;

/**
 * 
 * @author Jaime M Trillos
 * @author Ana C Trillos
 *
 * Parse XML file from Copernicus datase, NetCDF and CSV to obtain Metadata
 *
 */

public class BdoDatasetAnalyser {
	
	private static final String EMPTY_FIELD = "";

	public static Dataset analyseDatasetURI(String datasetURI) throws IOException {
		Dataset result = new Dataset();

		String delims; //Delimiters for extracting some part of the text
		String[] tokens; //list of pieces of information
		String title;
		String description;
		String identifier;
		String issued;
		String language;
		String license;
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
		Elements observationsElements;
		Elements variablesElements;
		String observations;
		List<String> variables;

		//Read the URI and return the HTML/XML document
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
		issued = item.getElementsByTag("gmd:date").first().text() + "T00:00:00";
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
		
		license = item.getElementsByTag("gmd:useLimitation").text();

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
		
		//obtaining the corresponding variable name from the standard CF
		List<String> listVariables = new ArrayList<>();
		for (String itemVariable : list) {
			listVariables.add(itemVariable + " -- "+ EMPTY_FIELD) ;
		}
		variables = parserDatasetVariables(listVariables);
		
		Element item8 = doc.getElementsByTag("gmd:descriptiveKeywords").get(1);
		observationsElements = item8.getElementsByTag("gmx:Anchor");
		
		replace = observationsElements.text().replaceAll(" ", ",");
		
		delims = ",geonetwork.thesaurus.local.discipline.myocean.discipline";
		tokens = replace.split(delims);
		observations = tokens[0]; 

		result.setTitle(title);
		result.setDescription(description);
		result.setHomepage(datasetURI);
		result.setIdentifier(identifier);
		result.setIssuedDate(issued);
		result.setModifiedDate(issued);
		result.setLanguage(language);
		result.setLicense(license);
		result.setFormats("XML");
		result.setObservations(observations);
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

	public static Dataset analyseDatasetNetcdf(String filename) throws IOException {
		Dataset result = new Dataset();
		String keywords = EMPTY_FIELD;
		List<String> listVariables = new ArrayList<>() ;

		//read the file
		NetcdfFile nc = null;
		try {
			HDFSFileSystem hdfsSys = new HDFSFileSystem(filename);
			Path localFile = hdfsSys.copyFile(filename,Constants.CONFIGFILEPATH+"/Backend/AddDatasets/file.nc");
			//read NetCDF file to get its metadata
			nc = NetcdfDataset.openFile(localFile.toString(), null);
			
			result = netcdMetadatExtractor(nc);
			
			listVariables = result.getVariable();
			//obtaining the corresponding variable name from the standard CF
			result.setVariable(parserDatasetVariables(listVariables));
			
			keywords = result.getKeywords();
			//obtaining the corresponding linked data for keywords
			result.setKeywords(parserDatasetKeywords(keywords));
			
			//extract issued/modified date from filename
			result = extractDatesFileName(filename, result);
			
			//Delete the temporal file "file.nc"
			Files.deleteIfExists(Paths.get(Constants.CONFIGFILEPATH+"/Backend/AddDatasets/file.nc"));
			
		} catch (IOException e) {
			e.printStackTrace();
		} finally { 
			if (null != nc) try {
				nc.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return result;
	}
	
	public static Dataset analyseDatasetFileNetcdf(String filename) throws IOException {
		Dataset result = new Dataset();
		String keywords = EMPTY_FIELD;
		List<String> listVariables = new ArrayList<>() ;

		//read the file
		NetcdfFile nc = null;
		try {
	    	String name = new File(filename).getName();
			nc = NetcdfDataset.openFile(filename, null);
			
			result = netcdMetadatExtractor(nc);
			
			listVariables = result.getVariable();
			//obtaining the corresponding variable name from the standard CF
			result.setVariable(parserDatasetVariables(listVariables));
			
			keywords = result.getKeywords();
			//obtaining the corresponding linked data for keywords
			result.setKeywords(parserDatasetKeywords(keywords));
			
			//extract issued/modified date from filename and/or temporalCoverageBegin/End from fileName
			result = extractDatesFileName(filename, result);
			
			//Delete the temporal file
			Files.deleteIfExists(Paths.get(Constants.CONFIGFILEPATH+"/Backend/AddDatasets/" +  name));
			
		} catch (IOException e) {
			e.printStackTrace();
		} finally { 
			if (null != nc) try {
				nc.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return result;
	}
	
	public static Dataset analyseDatasetCsv(String filename) throws IOException {
		Dataset result = new Dataset();
		
		//read the file
		String name = new File(filename).getName();
		
		HDFSFileSystem hdfsSys = new HDFSFileSystem(filename);
		Path localFile = hdfsSys.copyFile(filename,Constants.CONFIGFILEPATH+"/Backend/AddDatasets/" + name);
		
		result = analyseDatasetFileCsv(localFile.toString());

		return result;
	}
	
	public static Dataset analyseDatasetFileCsv(String filename) throws IOException {
		Dataset result = new Dataset();
		List<String> listVariables = new ArrayList<>() ;
		String nameExtension;
		
		//Get the name of the file with extension
		nameExtension = new File(filename).getName();
		result = extractDatesFileName(filename, result);
		
		result.setFormats("CSV");
		
		//Read the first line of the csv where the header (variables) are separated by semicolon
	    CSVReader reader = new CSVReader(new FileReader(filename), ';');
	    
	    //Read CSV line by line and use the string array as you want
	    String[] nextLine;
	    nextLine = reader.readNext();
	    //Verify if the file has ; delimiter
	    if(nextLine.length == 1) {
	    	reader = new CSVReader(new FileReader(filename), ',');
		    nextLine = reader.readNext();
	    }
	    
	    for(int i = 0; i<nextLine.length; i++) {
	    	String var = removeUTF8BOM(nextLine[i]);
	    	listVariables.add(var + " -- "+ EMPTY_FIELD);
	    }
	    reader.close();

		//obtaining the corresponding variable name from the standard CF
		result.setVariable(parserDatasetVariables(listVariables));
		
		//Delete the temporal file
		Files.deleteIfExists(Paths.get(Constants.CONFIGFILEPATH+"/Backend/AddDatasets/" + nameExtension));
		
		return result;
	}
	
	public static Dataset analyseDatasetExcel(String filename) throws IOException {
		Dataset result = new Dataset();
		
		//read the file
		String name = new File(filename).getName();
		
		HDFSFileSystem hdfsSys = new HDFSFileSystem(filename);
		Path localFile = hdfsSys.copyFile(filename,Constants.CONFIGFILEPATH+"/Backend/AddDatasets/" + name);
		
		result = analyseDatasetFileExcel(localFile.toString());

		return result;
	}
	
	public static Dataset analyseDatasetFileExcel(String filename) throws IOException {
		Dataset result = new Dataset();
		List<String> listExcelVariables = new ArrayList<>() ;
		List<String> listVariables = new ArrayList<>() ;
		String nameExtension;
		
		//Get the name of the file with extension
		nameExtension = new File(filename).getName();
		
		result = extractDatesFileName(filename, result);
		
		result.setFormats("Excel");
		
		InputStream inp = null;
		Workbook wb = null;
		try {
			if(filename.endsWith(".xls")) {
				wb = WorkbookFactory.create(new File(filename));
			} else {
				inp = new FileInputStream(filename);
				wb = WorkbookFactory.create(inp);
			}
	        listExcelVariables = extractVariablesExcel(wb.getSheetAt(0));
	        if (inp != null) {
	        	inp.close();
	        }
	        for(String attr : listExcelVariables) {
	        	listVariables.add(attr + " -- " + EMPTY_FIELD);
	        }
		} catch (InvalidFormatException e) {
			e.printStackTrace();
		}
		
		//obtaining the corresponding variable name from the standard CF
		result.setVariable(parserDatasetVariables(listVariables));
		
		//Delete the temporal file
		Files.deleteIfExists(Paths.get(Constants.CONFIGFILEPATH+"/Backend/AddDatasets/" + nameExtension));
		
		return result;
	}

	// Set Title and/or Issued/Modified date and/or TemporalCoverageBegin/End from filename 
    public static Dataset extractDatesFileName(String filename, Dataset result) {
    	String nameExtension = new File(filename).getName();
		String[] tokens = nameExtension.split("\\.(?=[^\\.]+$)");
		String name = tokens[0];
		
		//Only for CSV and Excel files
		if(result.getTitle().equals(EMPTY_FIELD)) {
			result.setTitle(name.replace("_", " "));
		}
		
		if (name.contains("_")) {
			String[] splitName = name.split("_");
			int size = splitName.length;
			if(size > 2) {
				String issuedDate = splitName[size-2];
				String modifiedDate = splitName[size-1];
				if(issuedDate.length() > 14) {
					if(issuedDate.substring(8,9).equals("T")) {
						if(result.getIssuedDate().equals(EMPTY_FIELD)) {
							result.setIssuedDate(convertDate(issuedDate));
						}
						if(result.getTemporalCoverageBegin().equals(EMPTY_FIELD)) {
							result.setTemporalCoverageBegin(convertDate(issuedDate));
						}
					}
				}
				if(modifiedDate.length() > 14) {
					if(modifiedDate.substring(8,9).equals("T")) {
						if(result.getModifiedDate().equals(EMPTY_FIELD)) {
							result.setModifiedDate(convertDate(modifiedDate));
						}
						if(result.getTemporalCoverageEnd().equals(EMPTY_FIELD)) {
							result.setTemporalCoverageEnd(convertDate(modifiedDate));
						}
					}
				}
			}
		}
		return result;
    }
	
	// Extract the raw variables of the excel file
	public static List<String> extractVariablesExcel(Sheet sheet) {
        Row row = null;
        row = sheet.getRow(0);
		List<String> listVariables = new ArrayList<>() ;
        for (int j = 0; j < row.getLastCellNum(); j++) {
        	listVariables.add(row.getCell(j).toString());
        }
        return listVariables;
    }
	
	public static Dataset netcdMetadatExtractor(NetcdfFile nc) {
		Dataset result = new Dataset();
		List<Variable> allVariables;
		List<String> variables = new ArrayList<>();
		
		//find the attributes and export the information to create the DatasetSuggest
		List<Attribute> listFileMetadata = nc.getGlobalAttributes();
		if(listFileMetadata != null)
		{
			for(Attribute attr : listFileMetadata) {					
				if(attr.getShortName().equalsIgnoreCase("id")) {
					result.setIdentifier(attr.getStringValue());
				}
				if(attr.getShortName().equalsIgnoreCase("title")) {
					result.setTitle(attr.getStringValue().replace("_", " "));
				}
				if(attr.getShortName().equalsIgnoreCase("summary") || attr.getShortName().equalsIgnoreCase("comment")) {
					result.setDescription(attr.getStringValue());
					if (result.getDescription().length() == 1) {
						result.setDescription(EMPTY_FIELD);
					}
				}
				if(attr.getShortName().equalsIgnoreCase("area")) {
					result.setKeywords(attr.getStringValue());
				}
				if(attr.getShortName().equalsIgnoreCase("conventions")) {
					result.setStandards(attr.getStringValue());
				}
				result.setFormats("NetCDF");
				if(attr.getShortName().equalsIgnoreCase("institution_references")) {
					result.setHomepage(attr.getStringValue());
				}
				if(attr.getShortName().equalsIgnoreCase("naming_authority")) {
					result.setPublisher(attr.getStringValue());
				}
				if(attr.getShortName().equalsIgnoreCase("licence") || attr.getShortName().equalsIgnoreCase("license")) {
					result.setLicense(attr.getStringValue());
				}
				if(attr.getShortName().equalsIgnoreCase("geospatial_lon_min") || attr.getShortName().equalsIgnoreCase("longitude_min")) {
					result.setSpatialWest(attr.getValues().toString().replace(" ", EMPTY_FIELD));
				}
				if(attr.getShortName().equalsIgnoreCase("geospatial_lon_max") || attr.getShortName().equalsIgnoreCase("longitude_max")) {
					result.setSpatialEast(attr.getValues().toString().replace(" ", EMPTY_FIELD));
				}
				if(attr.getShortName().equalsIgnoreCase("geospatial_lat_min") || attr.getShortName().equalsIgnoreCase("latitude_min")) {
					result.setSpatialSouth(attr.getValues().toString().replace(" ", EMPTY_FIELD));
				}
				if(attr.getShortName().equalsIgnoreCase("geospatial_lat_max") || attr.getShortName().equalsIgnoreCase("latitude_max")) {
					result.setSpatialNorth(attr.getValues().toString().replace(" ", EMPTY_FIELD));
				}
				if(attr.getShortName().equalsIgnoreCase("geospatial_vertical_min")) {
					result.setVerticalCoverageFrom(attr.getStringValue());
					if (result.getVerticalCoverageFrom().equals(" ")) {
						result.setVerticalCoverageFrom(EMPTY_FIELD);
					}
				}				
				if(attr.getShortName().equalsIgnoreCase("geospatial_vertical_max")) {
					result.setVerticalCoverageTo(attr.getStringValue());
					if (result.getVerticalCoverageTo().equals(" ")) {
						result.setVerticalCoverageTo(EMPTY_FIELD);
					}
				}				
				if(attr.getShortName().equalsIgnoreCase("update_interval")) {
					result.setTimeResolution(attr.getStringValue());
				}
				if(attr.getShortName().equalsIgnoreCase("cdm_data_type")) {
					result.setObservations(attr.getStringValue());
				}
				if(attr.getShortName().equalsIgnoreCase("source")) {
					result.setSource(attr.getStringValue());
				}
				// Extract issued, modified, temporalCoverage begin and end dates
				result = netcdfMetadataDatesExtractor(attr, result);
				
			}
			
			//return a list with all variables
			allVariables = nc.getVariables();
			for (int i=0; i<allVariables.size(); i++) {
				//select only the (raw) name of the variables
				String standardName = allVariables.get(i).getShortName();
				String unit = allVariables.get(i).getUnitsString();
				if(unit == null) {
					unit = EMPTY_FIELD;
				} else if(unit.contains("since")) {
					String[] token = unit.split("since");
					unit = token[0];
				} else if (unit.contains("%")) {
					unit = "percent";
				}
				variables.add(standardName + " -- "+ unit);
		    }
			//Verify and delete if there are duplicates
			HashSet<String> hs = new HashSet<>();
			hs.addAll(variables);
			variables.clear();
			variables.addAll(hs);
			result.setVariable(variables);
		}
		if(result.getModifiedDate().isEmpty()) {
			result.setModifiedDate(result.getTemporalCoverageEnd());
		}
		return result;
		
	}
	
	// Extract issued, modified, temporalCoverage begin and end dates
	public static Dataset netcdfMetadataDatesExtractor(Attribute attr, Dataset result) {
		if(attr.getShortName().equalsIgnoreCase("history") || attr.getShortName().equalsIgnoreCase("date_created")) {
			if(result.getIssuedDate().isEmpty()) {
				result.setIssuedDate(returnCorrectDateFormat(attr.getStringValue()));
			}
		}
		if(attr.getShortName().equalsIgnoreCase("date_update")) {
			if(result.getModifiedDate().isEmpty()) {
				result.setModifiedDate(returnCorrectDateFormat(attr.getStringValue()));
			}
		}
		if(attr.getShortName().equalsIgnoreCase("time_coverage_start") || attr.getShortName().equalsIgnoreCase("field_date")) {
			result.setTemporalCoverageBegin(returnCorrectDateFormat(attr.getStringValue()));
			if(attr.getShortName().equalsIgnoreCase("field_date")) {
				result.setIssuedDate(returnCorrectDateFormat(attr.getStringValue()));
			}
		}
		if(attr.getShortName().equalsIgnoreCase("time_coverage_end") || attr.getShortName().equalsIgnoreCase("bulletin_date")) {
			result.setTemporalCoverageEnd(returnCorrectDateFormat(attr.getStringValue()));
			if(attr.getShortName().equalsIgnoreCase("bulletin_date")) {
				result.setModifiedDate(returnCorrectDateFormat(attr.getStringValue()));
			}
		}
		return result;
	}
	
	private static String returnCorrectDateFormat(String value) {
		String date = "";
		if(value.length() > 1) {
			if (value.contains("Z")) { // 2017-05-04T00:20:02Z
				date = value.split("Z")[0];
			}else if (value.contains(" ")){ // 2016/07/20 08:25:14
				date = value.split(" ")[0] + "T" + value.split(" ")[1];
			}else {
				date = value;
			}
			if(date.length() > 16) {
				if(date.substring(4,5).equals("-") && date.substring(7,8).equals("-") && date.substring(10,11).equals("T") && date.substring(13,14).equals(":") && date.substring(16,17).equals(":")) {
					return date;
				} else if(date.substring(4,5).equals("/") && date.substring(7,8).equals("/") && date.substring(10,11).equals("T") && date.substring(13,14).equals(":") && date.substring(16,17).equals(":")) {
					return date.replaceAll("/", "-");
				} else if(date.substring(8,9).equals("-") && date.substring(11,12).equals(":") && date.substring(14,15).equals(":")) {
					date = date.replaceAll("-", "T");
					return convertDateTime(date);
				}
			}
		}
		return EMPTY_FIELD;
	}
	
	private static List<String> parserDatasetVariables (List<String> variables) {
		List<String> listVariables = new ArrayList<>() ;
		//Extracting the array of canonical model find in the json file
		JSONParser parser = new JSONParser();
		JSONArray variablesCM;
		try {
			File file = new File(Constants.CONFIGFILEPATH+"/Frontend/Flask/static/json/canonicalModelMongo.json");
			if(file.exists() && file.length() > 0) {
				variablesCM = (JSONArray) parser.parse(new FileReader(Constants.CONFIGFILEPATH+"/Frontend/Flask/static/json/canonicalModelMongo.json"));
				for(int j=0; j<variables.size(); j++) {
					
					boolean flag = false;
					for(int i=0; i<variablesCM.size(); i++){
			        	JSONObject keyword = (JSONObject) variablesCM.get(i);
			        	String canonicalName = keyword.get("canonicalName").toString();
			            String name = keyword.get("name").toString();
			            String rawName = variables.get(j).split(" -- ")[0];
			            if(canonicalName.equalsIgnoreCase(rawName) || name.equalsIgnoreCase(rawName)) {
			            	listVariables.add(variables.get(j) + " -- "+ keyword.get("canonicalName"));
			            	flag = true;
			            	break;
			            }		            	
			        }
					
					if(!flag) {
						listVariables.add(variables.get(j) + " -- "+ EMPTY_FIELD);
					}
				}
			} else {
				for(int j=0; j<variables.size(); j++) {
					listVariables.add(variables.get(j) + " -- "+ EMPTY_FIELD);
				}
			}
		} catch (IOException | ParseException e) {
			e.printStackTrace();
		}
		return listVariables;
	}
	
	private static String parserDatasetKeywords (String keywords) {
		JSONParser parser = new JSONParser();
		JSONArray keywordsArray;
		try {
			keywordsArray = (JSONArray) parser.parse(new FileReader(Constants.CONFIGFILEPATH+"/Frontend/Flask/static/json/keywords.json"));
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
	            if(!flag) {
	            	keywords = EMPTY_FIELD;
	            }
	        }
		} catch (IOException | ParseException e) {
			e.printStackTrace();
		}
		return keywords;
		
	}
	
	// Convert yyyyMMddTHHmmss into yyyy-MM-ddTHH:mm:ss
    public static String convertDate(String date){
    	String newDate = "";    	
    	String[] tokens = date.split("T");
    	String yMd = tokens[0];
    	String hms = tokens[1];
    	
    	String year = yMd.substring(0, 4);
    	String month = yMd.substring(4, 6);
    	String day = yMd.substring(6, 8);
    	
    	String hour = hms.substring(0, 2);
    	String minute = hms.substring(2, 4);
    	String second = hms.substring(4, 6);
    	
    	newDate = year + "-" + month + "-" + day  + "T" + hour + ":" + minute + ":" + second;
    	
    	return newDate;
    }
    
    // Convert yyyyMMddTHH:mm:ss into yyyy-MM-ddTHH:mm:ss
    public static String convertDateTime(String date) {
    	String newDate = "";    	
    	String[] tokens = date.split("T");
    	String yMd = tokens[0];
    	String hms = tokens[1];
    	
    	String year = yMd.substring(0, 4);
    	String month = yMd.substring(4, 6);
    	String day = yMd.substring(6, 8);
    	
    	tokens = hms.split(":");
    	
    	String hour = tokens[0];
    	String minute = tokens[1];
    	String second = tokens[2];
    	
    	newDate = year + "-" + month + "-" + day  + "T" + hour + ":" + minute + ":" + second;
    	
    	return newDate;
    }
    
    // Remove the char "\uFEFF" that starts in the variables
    private static String removeUTF8BOM(String s) {
        if (s.startsWith("\uFEFF")) {
            s = s.substring(1);
        }
        return s;
    }

}

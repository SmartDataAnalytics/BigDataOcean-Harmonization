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

import org.unibonn.bdo.bdodatasets.Constants;
import org.unibonn.bdo.connections.HDFSFileSystem;
import org.unibonn.bdo.linking.LinkedDiscoveryData;
import org.unibonn.bdo.linking.NERDiscovery;

/**
 * 
 * @author Jaime M Trillos
 * @author Ana C Trillos
 *
 * Parse XML file from Copernicus datase, NetCDF, CSV and XML to obtain Metadata
 *
 */

public class BdoDatasetAnalyser {
	
	private static final String EMPTY_FIELD = "";
	private static List<String> listNER = new ArrayList<>();
	
	private BdoDatasetAnalyser() {
		throw new IllegalStateException("BdoDatasetAnalyser class");
	}
	
	// Extract metadata from copernicus html (xml)
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
		Elements observationsElements;
		Elements variablesElements;
		String observations;
		List<String> variables = new ArrayList<>();
		String rawDescription = "";
		List<String> listSubjects = new ArrayList<>();
		List<String> listKeywords = new ArrayList<>();
		List<String> listGeoLocation = new ArrayList<>();

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
		
		rawDescription = title + " " + totaldescription;
		if (rawDescription.length() > 2) {
			listNER = NERDiscovery.exec(rawDescription);
			if(listNER.size() > 0) {
				listKeywords = LinkedDiscoveryData.parseListNames(listNER, "keywords");
				listGeoLocation = LinkedDiscoveryData.parseListNames(listNER, "geoLocation");
				listSubjects = LinkedDiscoveryData.parseListNames(listNER, "subjects");
			}
		}
		
		if(listSubjects.size() > 0 && listSubjects.get(0) != "") {
			result.setSubject(listSubjects.get(0));
		}
		
		if(listKeywords.size() > 0 && listKeywords.get(0) != "") {
			result.setKeywords(listKeywords.get(0));
		}
		
		if(listGeoLocation.size() > 0 && listGeoLocation.get(0) != "") {
			result.setGeoLocation(listGeoLocation.get(0));
		}

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
		
		//obtaining the corresponding variable name from the standard CF
		List<String> listVariables = new ArrayList<>();
		for (String itemVariable : list) {
			listVariables.add(itemVariable + " -- " + EMPTY_FIELD);
		}
		variables = LinkedDiscoveryData.parseListNames(listVariables, "variables");
		
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

	// Extract metadata from netcdf (hdfs url)
	public static Dataset analyseDatasetNetcdf(String filename) throws IOException {
		Dataset result = new Dataset();
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
			result.setVariable(LinkedDiscoveryData.parseListNames(listVariables, "variables"));
			
			//Delete the temporal file "file.nc"
			hdfsSys.deleteFile(Constants.CONFIGFILEPATH+"/Backend/AddDatasets/file.nc");
			
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
	
	// Extract metadata from file netcdf (local)
	public static Dataset analyseDatasetFileNetcdf(String filename) throws IOException {
		Dataset result = new Dataset();
		List<String> listVariables = new ArrayList<>() ;

		//read the file
		NetcdfFile nc = null;
		try {
			nc = NetcdfDataset.openFile(filename, null);
			
			result = netcdMetadatExtractor(nc);
			
			listVariables = result.getVariable();
			//obtaining the corresponding variable name from the standard CF
			result.setVariable(LinkedDiscoveryData.parseListNames(listVariables, "variables"));
			
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
	
	// Extract metadata from CSV (hdfs url)
	public static Dataset analyseDatasetCsv(String filename) {
		Dataset result = new Dataset();
		
		//read the file
		String name = new File(filename).getName();
		
		HDFSFileSystem hdfsSys = new HDFSFileSystem(filename);
		Path localFile = hdfsSys.copyFile(filename,Constants.CONFIGFILEPATH+"/Backend/AddDatasets/" + name);
		
		result = analyseDatasetFileCsv(localFile.toString());
		
		//Delete the temporal file
		hdfsSys.deleteFile(Constants.CONFIGFILEPATH+"/Backend/AddDatasets/" + name);

		return result;
	}
	
	// Extract metadata from file CSV (local)
	public static Dataset analyseDatasetFileCsv(String filename) {
		Dataset result = new Dataset();
		List<String> listVariables = new ArrayList<>() ;
		String nameExtension;
		List<String> listSubjects = new ArrayList<>();
		List<String> listKeywords = new ArrayList<>();
		List<String> listGeoLocation = new ArrayList<>();
		
		//Get the name of the file with extension
		nameExtension = new File(filename).getName();
		String[] tokens = nameExtension.split("\\.(?=[^\\.]+$)");
		//Get only the name
		String name = tokens[0];
		result = extractDatesFiles(name, result);
		
		if (name.length() > 2) {
			listNER = NERDiscovery.exec(name);
			if(listNER.size() > 0) {
				listKeywords = LinkedDiscoveryData.parseListNames(listNER, "keywords");
				listGeoLocation = LinkedDiscoveryData.parseListNames(listNER, "geoLocation");
				listSubjects = LinkedDiscoveryData.parseListNames(listNER, "subjects");
			}
		}
		
		if(listSubjects.size() > 0 && listSubjects.get(0) != "") {
			result.setSubject(listSubjects.get(0));
		}
		
		if(listKeywords.size() > 0 && listKeywords.get(0) != "") {
			result.setKeywords(listKeywords.get(0));
		}
		
		if(listGeoLocation.size() > 0 && listGeoLocation.get(0) != "") {
			result.setGeoLocation(listGeoLocation.get(0));
		}
		
		result.setFormats("CSV");
		
		//Read the first line of the csv where the header (variables) are separated by semicolon
	    CSVReader reader;
		try {
			reader = new CSVReader(new FileReader(filename), ';');
		    
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
		    	listVariables.add(var + " -- " + EMPTY_FIELD);
		    }
		    reader.close();

			//obtaining the corresponding variable name from the standard CF
			result.setVariable(LinkedDiscoveryData.parseListNames(listVariables, "variables"));
			
			//Delete the temporal file
			Files.deleteIfExists(Paths.get(Constants.CONFIGFILEPATH+"/Backend/AddDatasets/" + nameExtension));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	// Extract metadata from Excel (hdfs url)
	public static Dataset analyseDatasetExcel(String filename) throws IOException {
		Dataset result = new Dataset();
		
		//read the file
		String name = new File(filename).getName();
		
		HDFSFileSystem hdfsSys = new HDFSFileSystem(filename);
		Path localFile = hdfsSys.copyFile(filename,Constants.CONFIGFILEPATH+"/Backend/AddDatasets/" + name);
		
		result = analyseDatasetFileExcel(localFile.toString());
		
		//Delete the temporal file
		hdfsSys.deleteFile(Constants.CONFIGFILEPATH+"/Backend/AddDatasets/" + name);

		return result;
	}
	
	// Extract metadata from file Excel (local)
	public static Dataset analyseDatasetFileExcel(String filename) throws IOException {
		Dataset result = new Dataset();
		List<String> listExcelVariables = new ArrayList<>() ;
		List<String> listVariables = new ArrayList<>() ;
		String nameExtension;
		List<String> listSubjects = new ArrayList<>();
		List<String> listKeywords = new ArrayList<>();
		List<String> listGeoLocation = new ArrayList<>();
		
		//Get the name of the file with extension
		nameExtension = new File(filename).getName();
		String[] tokens = nameExtension.split("\\.(?=[^\\.]+$)");
		//Get only the name
		String name = tokens[0];
		result = extractDatesFiles(name, result);
		
		if (name.length() > 2) {
			listNER = NERDiscovery.exec(name);
			if(listNER.size() > 0) {
				listKeywords = LinkedDiscoveryData.parseListNames(listNER, "keywords");
				listGeoLocation = LinkedDiscoveryData.parseListNames(listNER, "geoLocation");
				listSubjects = LinkedDiscoveryData.parseListNames(listNER, "subjects");
			}
		}
		
		if(listSubjects.size() > 0 && listSubjects.get(0) != "") {
			result.setSubject(listSubjects.get(0));
		}
		
		if(listKeywords.size() > 0 && listKeywords.get(0) != "") {
			result.setKeywords(listKeywords.get(0));
		}
		
		if(listGeoLocation.size() > 0 && listGeoLocation.get(0) != "") {
			result.setGeoLocation(listGeoLocation.get(0));
		}
		
		result.setFormats("Excel");
		
		InputStream inp = null;
		inp = new FileInputStream(filename);
		try {
			Workbook wb = WorkbookFactory.create(inp);
	        listExcelVariables = extractVariablesExcel(wb.getSheetAt(0));
	        inp.close();
	        for(String attr : listExcelVariables) {
	        	listVariables.add(attr + " -- " + EMPTY_FIELD);
	        }
		} catch (InvalidFormatException e) {
			e.printStackTrace();
		}
		
		//obtaining the corresponding variable name from the standard CF
		result.setVariable(LinkedDiscoveryData.parseListNames(listVariables, "variables"));
		
		//Delete the temporal file
		Files.deleteIfExists(Paths.get(Constants.CONFIGFILEPATH+"/Backend/AddDatasets/" + nameExtension));
		
		return result;
	}

	//Extract the issuedDate and modifiedDate that contains the name iff the name has "_"
	public static Dataset extractDatesFiles(String name, Dataset result) {
		result.setTitle(name);
		if (name.contains("_")) {
			String[] splitName = name.split("_");
			int size = splitName.length;
			if(size > 2) {
				String issuedDate = splitName[size-2];
				String modifiedDate = splitName[size-1];
				if(issuedDate.length() > 14) {
					if(issuedDate.substring(8,9).equals("T")) {
						result.setIssuedDate(convertDate(issuedDate));
					}
				}
				if(issuedDate.length() > 14) {
					if(modifiedDate.substring(8,9).equals("T")) {
						result.setModifiedDate(convertDate(modifiedDate));
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
        System.out.println();
        return listVariables;
    }
	
	// Extract metadata from netcdf
	public static Dataset netcdMetadatExtractor(NetcdfFile nc) {
		Dataset result = new Dataset();
		String title = "";
		String description = "";
		String geoLocation = "";
		String rawDescription = "";
		String issuedDate = "";
		String modifiedDate = "";
		String temporalCoverageBegin = "";
		String temporalCoverageEnd = "";
		List<Variable> allVariables;
		List<String> variables = new ArrayList<>();
		List<String> listSubjects = new ArrayList<>();
		List<String> listKeywords = new ArrayList<>();
		List<String> listGeoLocation = new ArrayList<>();
		
		//find the attributes and export the information to create the DatasetSuggest
		List<Attribute> listFileMetadata = nc.getGlobalAttributes();
		if(listFileMetadata != null)
		{
			for(Attribute attr : listFileMetadata) {					
				if(attr.getShortName().equalsIgnoreCase("id")) {
					result.setIdentifier(attr.getStringValue());
				}
				if(attr.getShortName().equalsIgnoreCase("title")) {
					title = attr.getStringValue();
					result.setTitle(title);
				}
				if(attr.getShortName().equalsIgnoreCase("summary") || attr.getShortName().equalsIgnoreCase("comment")) {
					description = attr.getStringValue();
					result.setDescription(description);
					if (description.length() == 1) {
						result.setDescription(EMPTY_FIELD);
					}
				}
				
				if(attr.getShortName().equalsIgnoreCase("area")) {
					geoLocation = attr.getStringValue();
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
				if(attr.getShortName().equalsIgnoreCase("history") || attr.getShortName().equalsIgnoreCase("date_created")) {
					if(attr.getStringValue().length() > 1) {
						if (attr.getStringValue().contains("Z")) { // 2017-05-04T00:20:02Z
							issuedDate = attr.getStringValue().split("Z")[0];
						}else if (attr.getStringValue().contains(" ")){ // 2016/07/20 08:25:14
							issuedDate = attr.getStringValue().split(" ")[0] + "T" + attr.getStringValue().split(" ")[1];
						}else {
							issuedDate = attr.getStringValue();
						}
						if(issuedDate.length() > 16) {
							if(issuedDate.substring(4,5).equals("-") && issuedDate.substring(7,8).equals("-") && issuedDate.substring(10,11).equals("T") && issuedDate.substring(13,14).equals(":") && issuedDate.substring(16,17).equals(":")) {
								result.setIssuedDate(issuedDate);
							} else if(issuedDate.substring(4,5).equals("/") && issuedDate.substring(7,8).equals("/") && issuedDate.substring(10,11).equals("T") && issuedDate.substring(13,14).equals(":") && issuedDate.substring(16,17).equals(":")) {
								issuedDate = issuedDate.replaceAll("/", "-");
								result.setIssuedDate(issuedDate);
							} else if(issuedDate.substring(8,9).equals("-") && issuedDate.substring(11,12).equals(":") && issuedDate.substring(14,15).equals(":")) {
								issuedDate = issuedDate.replaceAll("-", "T");
								issuedDate = convertDateTime(issuedDate);
								result.setIssuedDate(issuedDate);
							} else {
								result.setIssuedDate(EMPTY_FIELD);
							}
						} else {
							result.setIssuedDate(EMPTY_FIELD);
						}
					} else {
						result.setIssuedDate(EMPTY_FIELD);
					}
				}
				if(attr.getShortName().equalsIgnoreCase("date_update")) {
					if(attr.getStringValue().length() > 1) {
						if (attr.getStringValue().contains("Z")) {
							modifiedDate = attr.getStringValue().split("Z")[0];
						} else if (attr.getStringValue().contains(" ")) {
							modifiedDate = attr.getStringValue().split(" ")[0] + "T" + attr.getStringValue().split(" ")[1];
						} else {
							modifiedDate = attr.getStringValue();
						}
						if(modifiedDate.substring(4,5).equals("-") && modifiedDate.substring(7,8).equals("-") && modifiedDate.substring(10,11).equals("T") && modifiedDate.substring(13,14).equals(":") && modifiedDate.substring(16,17).equals(":")) {
							result.setModifiedDate(modifiedDate);
						} else if(modifiedDate.substring(4,5).equals("/") && modifiedDate.substring(7,8).equals("/") && modifiedDate.substring(10,11).equals("T") && modifiedDate.substring(13,14).equals(":") && modifiedDate.substring(16,17).equals(":")) {
							modifiedDate = modifiedDate.replaceAll("/", "-");
							result.setModifiedDate(modifiedDate);
						} else if(modifiedDate.substring(8,9).equals("-") && modifiedDate.substring(11,12).equals(":") && modifiedDate.substring(14,15).equals(":")) {
							modifiedDate = modifiedDate.replaceAll("-", "T");
							modifiedDate = convertDateTime(modifiedDate);
							result.setModifiedDate(modifiedDate);
						} else {
							result.setModifiedDate(EMPTY_FIELD);
						}
					} else {
						result.setModifiedDate(EMPTY_FIELD);
					}
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
				if(attr.getShortName().equalsIgnoreCase("time_coverage_start")) {
					if(attr.getStringValue().length() > 1) {
						if (attr.getStringValue().contains("Z")) {
							temporalCoverageBegin = attr.getStringValue().split("Z")[0];
						} else if (attr.getStringValue().contains(" ")) {
							temporalCoverageBegin = attr.getStringValue().split(" ")[0] + "T" + attr.getStringValue().split(" ")[1];
						} else {
							temporalCoverageBegin = attr.getStringValue();
						}
						if(temporalCoverageBegin.substring(4,5).equals("-") && temporalCoverageBegin.substring(7,8).equals("-") && temporalCoverageBegin.substring(10,11).equals("T") && temporalCoverageBegin.substring(13,14).equals(":") && temporalCoverageBegin.substring(16,17).equals(":")) {
							result.setTemporalCoverageBegin(temporalCoverageBegin);
						} else if(temporalCoverageBegin.substring(4,5).equals("/") && temporalCoverageBegin.substring(7,8).equals("/") && temporalCoverageBegin.substring(10,11).equals("T") && temporalCoverageBegin.substring(13,14).equals(":") && temporalCoverageBegin.substring(16,17).equals(":")) {
							temporalCoverageBegin = temporalCoverageBegin.replaceAll("/", "-");
							result.setTemporalCoverageBegin(temporalCoverageBegin);
						} else if(temporalCoverageBegin.substring(8,9).equals("-") && temporalCoverageBegin.substring(11,12).equals(":") && temporalCoverageBegin.substring(14,15).equals(":")) {
							temporalCoverageBegin = temporalCoverageBegin.replaceAll("-", "T");
							temporalCoverageBegin = convertDateTime(temporalCoverageBegin);
							result.setTemporalCoverageBegin(temporalCoverageBegin);
						} else {
							result.setTemporalCoverageBegin(EMPTY_FIELD);
						}
					} else {
						result.setTemporalCoverageBegin(EMPTY_FIELD);
					}
				}
				if(attr.getShortName().equalsIgnoreCase("time_coverage_end")) {
					if(attr.getStringValue().length() > 1) {
						if (attr.getStringValue().contains("Z")) {
							temporalCoverageEnd = attr.getStringValue().split("Z")[0];
						} else if (attr.getStringValue().contains(" ")) {
							temporalCoverageEnd = attr.getStringValue().split(" ")[0] + "T" + attr.getStringValue().split(" ")[1];
						} else {
							temporalCoverageEnd = attr.getStringValue();
						}
						if(temporalCoverageEnd.substring(4,5).equals("-") && temporalCoverageEnd.substring(7,8).equals("-") && temporalCoverageEnd.substring(10,11).equals("T") && temporalCoverageEnd.substring(13,14).equals(":") && temporalCoverageEnd.substring(16,17).equals(":")) {
							result.setTemporalCoverageEnd(temporalCoverageEnd);
						} else if(temporalCoverageEnd.substring(4,5).equals("/") && temporalCoverageEnd.substring(7,8).equals("/") && temporalCoverageEnd.substring(10,11).equals("T") && temporalCoverageEnd.substring(13,14).equals(":") && temporalCoverageEnd.substring(16,17).equals(":")) {
							temporalCoverageEnd = temporalCoverageEnd.replaceAll("/", "-");
							result.setTemporalCoverageEnd(temporalCoverageEnd);
						} else if(temporalCoverageEnd.substring(8,9).equals("-") && temporalCoverageEnd.substring(11,12).equals(":") && temporalCoverageEnd.substring(14,15).equals(":")) {
							temporalCoverageEnd = temporalCoverageEnd.replaceAll("-", "T");
							temporalCoverageEnd = convertDateTime(temporalCoverageEnd);
							result.setTemporalCoverageEnd(temporalCoverageEnd);
						} else {
							result.setTemporalCoverageEnd(EMPTY_FIELD);
						}
					} else {
						result.setTemporalCoverageEnd(EMPTY_FIELD);
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
			}
			
			rawDescription = title + " " + description + " " + geoLocation;
			if (rawDescription.length() > 2) {
				listNER = NERDiscovery.exec(rawDescription);
				if(listNER.size() > 0) {
					listKeywords = LinkedDiscoveryData.parseListNames(listNER, "keywords");
					listGeoLocation = LinkedDiscoveryData.parseListNames(listNER, "geoLocation");
					listSubjects = LinkedDiscoveryData.parseListNames(listNER, "subjects");
				}
			}
			
			if(listSubjects.size() > 0 && listSubjects.get(0) != "") {
				result.setSubject(listSubjects.get(0));
			}
			
			if(listKeywords.size() > 0 && listKeywords.get(0) != "") {
				result.setKeywords(listKeywords.get(0));
			}
			
			if(listGeoLocation.size() > 0 && listGeoLocation.get(0) != "") {
				result.setGeoLocation(listGeoLocation.get(0));
			}
			
			//return a list with all variables
			allVariables = nc.getVariables();
			for (int i=0; i<allVariables.size(); i++) {
				//select only the (raw) name of the variables
				String standardName = allVariables.get(i).getShortName();
				String unit = allVariables.get(i).getUnitsString();
				if(unit == null) {
					unit = EMPTY_FIELD;
				} else if (unit.contains("since")) {
					String[] token = unit.split("since");
					unit = token[0];
				} else if (unit.contains("%")) {
					unit = "percent";
				}
				variables.add(standardName + " -- " + unit);
		    }
			//Verify and delete if there are duplicates
			HashSet<String> hs = new HashSet<>();
			hs.addAll(variables);
			variables.clear();
			variables.addAll(hs);
			result.setVariable(variables);
		}
		return result;
		
	}
	
	// Convert yyyyMMddTHHmmss into yyyy-MM-ddTHH:mm:ss
    public static String convertDate(String date) {
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
    public static String removeUTF8BOM(String s) {
        if (s.startsWith("\uFEFF")) {
            s = s.substring(1);
        }
        return s;
    }

}

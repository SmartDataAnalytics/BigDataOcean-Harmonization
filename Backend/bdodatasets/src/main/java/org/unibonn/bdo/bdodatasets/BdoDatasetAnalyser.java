package org.unibonn.bdo.bdodatasets;


import java.util.List;
import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.unibonn.bdo.objects.DatasetSuggest;

import thredds.catalog.ThreddsMetadata.Variables;
import ucar.nc2.Attribute;
import ucar.nc2.NetcdfFile;
import ucar.nc2.Variable;
import ucar.nc2.dataset.NetcdfDataset;

/*import ucar.netcdf.Attribute;
import ucar.netcdf.NetcdfFile;*/

/**
 * 
 * @author Jaime M Trillos
 * @author Ana C Trillos
 *
 * Parse XML file from Copernicus dataset to get Metadata
 *
 */

public class BdoDatasetAnalyser {

	public static DatasetSuggest analyseDatasetURI(String datasetURI) throws IOException {
		DatasetSuggest result = new DatasetSuggest();
		
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
		String variables;
		
		//Read the URI and return the HTML/XML document
		Document doc = Jsoup.connect(datasetURI).get();
		
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
        
        delims = variablesElements.text().replaceAll(" ", ",");
    	variables = delims;
    	
    	delims = ",geonetwork.thesaurus.external.parameter.myocean.ocean-variables";
    	tokens = variables.split(delims);
    	variables = tokens[0];
        
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
        result.setVariables(variables);
        
        return result;
	}
	
	public static DatasetSuggest analyseDatasetNetcdf(String filename) throws IOException {
		DatasetSuggest result = new DatasetSuggest();
		String title;
		String description;
		String identifier;
		String issued;
		String modified;
		String keywords;
		String homepage;
		String standards;
		String format;
		String spatialWestBoundLongitude;
		String spatialEastBoundLongitude;
		String spatialSouthBoundLatitude;
		String spatialNorthBoundLatitude;
		String temporalCoverageBegin;
		String temporalCoverageEnd;
		String publisher;
		String verticalCoverageFrom;
		String verticalCoverageTo;
		String timeResolution;
		
		//read the file
		 NetcdfFile nc = null;
		  try {
		    nc = NetcdfDataset.openFile(filename, null);
		    
		    List<Attribute> attribute;
		    List<Variable> attribute1;
		    attribute = nc.getGlobalAttributes();
		    System.out.println(nc.findGlobalAttribute("naming_authority"));
		    /*for (int i=0; i<attribute.size(); i++) {
		    	System.out.println(attribute.get(i)+"\n");
		    }*/
		    attribute1 = nc.getVariables();
		    /*for (int i=0; i<attribute1.size(); i++) {
		    	System.out.println(attribute1.get(i)+"\n");
		    }*/
		    
		    
		    
		  } catch (IOException ioe) {
		    
		  } finally { 
		    if (null != nc) try {
		      nc.close();
		    } catch (IOException ioe) {
		      
		    }
		  }
		
		/*NetcdfFile nc = new NetcdfFile(filename, true);
		
		//find the attributes and export the information to create the DatasetSuggest
	    Attribute attribute;
	    attribute = nc.getAttribute("id");
	    identifier = attribute.getStringValue();
	    attribute = nc.getAttribute("title");
	    title = attribute.getStringValue();
	    attribute = nc.getAttribute("summary");
	    description = attribute.getStringValue();
	    attribute = nc.getAttribute("area");
	    keywords = attribute.getStringValue();
	    attribute = nc.getAttribute("Conventions");
	    standards = attribute.getStringValue();
	    format = "NetCDF";
	    attribute = nc.getAttribute("institution_references");
	    homepage = attribute.getStringValue();
	    attribute = nc.getAttribute("naming_authority");
	    publisher = attribute.getStringValue();
	    attribute = nc.getAttribute("history");
	    issued = attribute.getStringValue();
	    attribute = nc.getAttribute("date_update");
	    modified = attribute.getStringValue();
	    attribute = nc.getAttribute("geospatial_lon_min");
	    spatialWestBoundLongitude = attribute.getStringValue();
	    attribute = nc.getAttribute("geospatial_lon_max");
	    spatialEastBoundLongitude = attribute.getStringValue();
	    attribute = nc.getAttribute("geospatial_lat_min");
	    spatialSouthBoundLatitude = attribute.getStringValue();
	    attribute = nc.getAttribute("geospatial_lat_max");
	    spatialNorthBoundLatitude = attribute.getStringValue();
	    attribute = nc.getAttribute("geospatial_vertical_min");
	    verticalCoverageFrom = attribute.getStringValue();
	    attribute = nc.getAttribute("geospatial_vertical_max");
	    verticalCoverageTo = attribute.getStringValue();
	    attribute = nc.getAttribute("time_coverage_start");
	    temporalCoverageBegin = attribute.getStringValue();
	    attribute = nc.getAttribute("time_coverage_end");
	    temporalCoverageEnd = attribute.getStringValue();
	    attribute = nc.getAttribute("update_interval");
	    timeResolution = attribute.getStringValue();

        result.setIdentifier(identifier);
	    result.setTitle(title);
        result.setDescription(description);
        result.setKeywords(keywords);
        result.setStandards(standards);
        result.setFormat(format);
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
        result.setTimeResolution(timeResolution);*/
		
		return result;
	}
	
}

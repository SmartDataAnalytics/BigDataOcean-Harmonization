package org.unibonn.bdo.bdodatasets;


import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.unibonn.bdo.objects.DatasetSuggest;

/**
 * 
 * @author Jaime M Trillos
 * @author Ana C Trillos
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
	
	

}

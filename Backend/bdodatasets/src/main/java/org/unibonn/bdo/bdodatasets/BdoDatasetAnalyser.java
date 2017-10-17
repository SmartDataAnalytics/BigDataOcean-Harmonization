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
		String spatialWestBoundLongitude;
		String spatialEastBoundLongitude;
		String spatialSouthBoundLatitude;
		String spatialNorthBoundLatitude;
		String temporal;
		String conformsTo;
		String publisher;
		String accuralPeriodicity;
		String verticalCoverage;
		String verticalLevel;
		String tempResolution;
		String gridResolution;
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
    	String beginPosition = item2.getElementsByTag("gml:beginPosition").first().text();
		String endPosition = item2.getElementsByTag("gml:endPosition").first().text();
		
		if(endPosition.length()>0){
    		temporal = "from " + beginPosition + " to " + endPosition;
    	}else {
    		temporal = "from " + beginPosition + " to Present";
    	}
    	
    	accuralPeriodicity = item.getElementsByTag("gmd:maintenanceNote").first().text();
    	verticalCoverage = item.getElementsByTag("gmd:EX_VerticalExtent").text();
    	
    	delims = " ";
    	tokens = verticalCoverage.split(delims);
    	verticalCoverage = "from " + tokens[0] + " to " + tokens[1];
    	
    	Element item3 = doc.getElementsByTag("gmd:referenceSystemInfo").first();
    	conformsTo = item3.getElementsByTag("gmd:code").text();
    	
    	Element item4 = doc.getElementsByTag("gmd:spatialRepresentationInfo").first();
        gridResolution = item4.getElementsByTag("gmd:axisDimensionProperties").text();
        
        delims = " ";
    	tokens = gridResolution.split(delims);
    	gridResolution = tokens[0] + "degree x " + tokens[1] + "degree";
        
        Element item5 = doc.getElementsByTag("gmd:contact").first();
        publisher = item5.getElementsByTag("gmd:organisationName").text();
        
        Element item6 = doc.getElementsByTag("gmd:contentInfo").first();
        tempResolution = item6.getElementsByTag("gmd:dimension").first().text();
        
        delims = ": ";
    	tokens = tempResolution.split(delims);
    	tempResolution = tokens[1];
        
        verticalLevel = item6.getElementsByTag("gmd:dimension").next().text();
        
        delims = ": ";
    	tokens = verticalLevel.split(delims);
    	verticalLevel = tokens[1];
    	
    	Element item7 = doc.getElementsByTag("gmd:descriptiveKeywords").get(2);
        variablesElements = item7.getElementsByTag("gmx:Anchor");
        
        delims = variablesElements.text().replaceAll(" ", ", ");
    	variables = delims;
    	
    	delims = ", geonetwork.thesaurus.external.parameter.myocean.ocean-variables";
    	tokens = variables.split(delims);
    	variables = tokens[0];
        
        result.setTitle(title);
        result.setDescription(description);
        result.setHomepage(datasetURI);
        result.setIdentifier(identifier);
        result.setLanguage(language);
        result.setSpatialWest(spatialWestBoundLongitude);
        result.setSpatialEast(spatialEastBoundLongitude);
        result.setSpatialSouth(spatialSouthBoundLatitude);
        result.setSpatialNorth(spatialNorthBoundLatitude);
        result.setTemporal(temporal);
        result.setConformsTo(conformsTo);
        result.setPublisher(publisher);
        result.setAccuralPeriodicity(accuralPeriodicity);
        result.setVerticalCoverage(verticalCoverage);
        result.setVerticalLevel(verticalLevel);
        result.setTemporalResolution(tempResolution);
        result.setGridResolution(gridResolution);
        result.setVariables(variables);
        
        return result;
	}
	
	

}

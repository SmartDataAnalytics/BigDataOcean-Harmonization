package org.unibonn.bdo.objects;

import java.util.List;
import java.util.Map;

/**
 *  
 * @author Jaime M Trillos
 *
 * Object of the Profile Dataset Metadata
 * Dataset constructor for Profiles (NOT contain identifier, issuedDate, modifiedDate)
 *
 */

public class ProfileDataset {

	private String title; //title of Dataset
	private String description; //description of Dataset
	private String subject; //tokenField_subject
	private String keywords; //tokenField_keywords
	private String standards; //standards
	private String formats; //tokenField_format
	private String language; //tokenField_language
	private String homepage; //URI of Dataset
	private String publisher; //publisher of Dataset
	private String source; //source of the Dataset (HCMR, ANEX, XMILE...)
	private String observations; //Comments of the Dataset (Insitu, Timeseries...)
	private String storageTable; //TableName used by parser tool
	private String accessRights; //access_rights 
	private String geoLocation; //tokenField_geoLocation
	private String spatialWest; //geo_coverageW
	private String spatialEast; //geo_coverageE
	private String spatialSouth; //geo_coverageS
	private String spatialNorth; //geo_coverageN
	private String coordinateSystem; //coordinate_sys
	private String verticalCoverageFrom; //vertical_coverage
	private String verticalCoverageTo; //vertical_coverage
	private String verticalLevel; //vertical_level
	private String temporalCoverageBegin; //temp_coverage_begin
	private String temporalCoverageEnd; //temp_coverage_end
	private String timeResolution; //time_reso
	private List<Map<String, String>> variables; //List of maping variables
	private String profileName;
	
	public ProfileDataset(){
		
	}
	
	public ProfileDataset(String profileName, String title, String description, String subject, String keywords,
			String standards, String formats, String language, String homepage, String publisher, 
			String source, String observations, String storageTable, 
			String accessRights, String geoLocation, String spatialWest, String spatialEast,
			String spatialSouth, String spatialNorth, String coordinateSystem, String verticalCoverageFrom,
			String verticalCoverageTo, String verticalLevel, String temporalCoverageBegin, String temporalCoverageEnd,
			String timeResolution, List<Map<String, String>> variables) {
		this.profileName = profileName;
		this.title = title;
		this.description = description;
		this.subject = subject;
		this.keywords = keywords;
		this.standards = standards;
		this.formats = formats;
		this.language = language;
		this.homepage = homepage;
		this.publisher = publisher;
		this.source = source;
		this.observations = observations;
		this.storageTable = storageTable;
		this.accessRights = accessRights;
		this.geoLocation = geoLocation;
		this.spatialWest = spatialWest;
		this.spatialEast = spatialEast;
		this.spatialSouth = spatialSouth;
		this.spatialNorth = spatialNorth;
		this.coordinateSystem = coordinateSystem;
		this.verticalCoverageFrom = verticalCoverageFrom;
		this.verticalCoverageTo = verticalCoverageTo;
		this.verticalLevel = verticalLevel;
		this.temporalCoverageBegin = temporalCoverageBegin;
		this.temporalCoverageEnd = temporalCoverageEnd;
		this.timeResolution = timeResolution;
		this.variables = variables;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getObservations() {
		return observations;
	}

	public void setObservations(String observations) {
		this.observations = observations;
	}

	public String getStorageTable() {
		return storageTable;
	}

	public void setStorageTable(String storageTable) {
		this.storageTable = storageTable;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getKeywords() {
		return keywords;
	}

	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}

	public String getStandards() {
		return standards;
	}

	public void setStandards(String standards) {
		this.standards = standards;
	}

	public String getFormats() {
		return formats;
	}

	public void setFormats(String formats) {
		this.formats = formats;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getHomepage() {
		return homepage;
	}

	public void setHomepage(String homepage) {
		this.homepage = homepage;
	}

	public String getPublisher() {
		return publisher;
	}

	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}

	public String getAccessRights() {
		return accessRights;
	}

	public void setAccessRights(String accessRights) {
		this.accessRights = accessRights;
	}

	public String getGeoLocation() {
		return geoLocation;
	}

	public void setGeoLocation(String geoLocation) {
		this.geoLocation = geoLocation;
	}

	public String getSpatialWest() {
		return spatialWest;
	}

	public void setSpatialWest(String spatialWest) {
		this.spatialWest = spatialWest;
	}

	public String getSpatialEast() {
		return spatialEast;
	}

	public void setSpatialEast(String spatialEast) {
		this.spatialEast = spatialEast;
	}

	public String getSpatialSouth() {
		return spatialSouth;
	}

	public void setSpatialSouth(String spatialSouth) {
		this.spatialSouth = spatialSouth;
	}

	public String getSpatialNorth() {
		return spatialNorth;
	}

	public void setSpatialNorth(String spatialNorth) {
		this.spatialNorth = spatialNorth;
	}

	public String getCoordinateSystem() {
		return coordinateSystem;
	}

	public void setCoordinateSystem(String coordinateSystem) {
		this.coordinateSystem = coordinateSystem;
	}

	public String getVerticalCoverageFrom() {
		return verticalCoverageFrom;
	}

	public void setVerticalCoverageFrom(String verticalCoverageFrom) {
		this.verticalCoverageFrom = verticalCoverageFrom;
	}

	public String getVerticalCoverageTo() {
		return verticalCoverageTo;
	}

	public void setVerticalCoverageTo(String verticalCoverageTo) {
		this.verticalCoverageTo = verticalCoverageTo;
	}

	public String getVerticalLevel() {
		return verticalLevel;
	}

	public void setVerticalLevel(String verticalLevel) {
		this.verticalLevel = verticalLevel;
	}

	public String getTemporalCoverageBegin() {
		return temporalCoverageBegin;
	}

	public void setTemporalCoverageBegin(String temporalCoverageBegin) {
		this.temporalCoverageBegin = temporalCoverageBegin;
	}

	public String getTemporalCoverageEnd() {
		return temporalCoverageEnd;
	}

	public void setTemporalCoverageEnd(String temporalCoverageEnd) {
		this.temporalCoverageEnd = temporalCoverageEnd;
	}

	public String getTimeResolution() {
		return timeResolution;
	}

	public void setTimeResolution(String timeResolution) {
		this.timeResolution = timeResolution;
	}

	public String getProfileName() {
		return profileName;
	}

	public void setProfileName(String profileName) {
		this.profileName = profileName;
	}	
	
	public List<Map<String, String>> getVariables() {
		return variables;
	}

	public void setVariableList(List<Map<String, String>> variables) {
		this.variables = variables;
	}
	
}


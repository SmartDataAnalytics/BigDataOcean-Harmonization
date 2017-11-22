package org.unibonn.bdo.objects;

/**
 * @author Jaime M Trillos
 * @author Ana C Trillos
 *
 * Represents an Dataset: contains the metadata obtained by Jsoup
 * 
 */
public class DatasetSuggest {
	
	private String identifier; //identifier of Dataset
	private String title; //title of Dataset
	private String description; //description of Dataset
	private String subject; //tokenField_subject
	private String keywords; //tokenField_keywords
	private String standards; //standards
	private String format; //tokenField_format
	private String language; //tokenField_language
	private String homepage; //URI of Dataset
	private String publisher; //publisher of Dataset
	private String accessRights; //access_rights 
	private String issuedDate; //issued_Date (AAAA-MM-DDThh:mm:ssZ)
	private String modifiedDate; //modifierd_Data (AAAA-MM-DDThh:mm:ssZ)
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
	private String variables; //variables
	
	public String getIdentifier() {
		return identifier;
	}
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
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
	public String getFormat() {
		return format;
	}
	public void setFormat(String format) {
		this.format = format;
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
	public String getIssuedDate() {
		return issuedDate;
	}
	public void setIssuedDate(String issuedDate) {
		this.issuedDate = issuedDate;
	}
	public String getModifiedDate() {
		return modifiedDate;
	}
	public void setModifiedDate(String modifiedDate) {
		this.modifiedDate = modifiedDate;
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
	public String getVariables() {
		return variables;
	}
	public void setVariables(String variables) {
		this.variables = variables;
	}
}

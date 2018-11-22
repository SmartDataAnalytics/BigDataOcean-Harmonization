package org.unibonn.bdo.objects;

import java.io.Serializable;
import java.util.List;

/**
 *  
 * @author Jaime M Trillos
 * @author Ana C Trillos
 *
 * Object of the Dataset Metadata
 *
 */

public class Dataset  implements Serializable {
	
	private static final long serialVersionUID = 2130077493645125759L;

	private String identifier; //identifier of Dataset
	private String idFile; //idFile from BDO Backbone
	private String title; //title of Dataset
	private String description; //description of Dataset
	private String subject; //tokenField_subject
	private String keywords; //tokenField_keywords
	private String standards; //standards
	private String formats; //tokenField_format
	private String language; //tokenField_language
	private String homepage; //URI of Dataset
	private String publisher; //publisher of Dataset
	private String license; //license of Dataset
	private String source; //source of the Dataset (HCMR, ANEX, XMILE...)
	private String observations; //Comments of the Dataset (Insitu, Timeseries...)
	private String storageTable; //TableName used by parser tool
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
	private List<String> variable; //dataset variables
	private String profileName;
	
	public Dataset(){
		this.identifier = "";
		this.idFile = "";
		this.title = "";
		this.description = "";
		this.subject = "";
		this.keywords = "";
		this.standards = "";
		this.formats = "";
		this.language = "eng";
		this.homepage = "";
		this.publisher = "";
		this.license = "";
		this.source = "";
		this.observations = "";
		this.storageTable = "";
		this.accessRights = "";
		this.issuedDate = "";
		this.modifiedDate = "";
		this.geoLocation = "";
		this.spatialWest = "";
		this.spatialEast = "";
		this.spatialSouth = "";
		this.spatialNorth = "";
		this.coordinateSystem = "";
		this.verticalCoverageFrom = "";
		this.verticalCoverageTo = "";
		this.verticalLevel = "";
		this.temporalCoverageBegin = "";
		this.temporalCoverageEnd = "";
		this.timeResolution = "";
		this.profileName = "";
	}
	
	//Dataset constructor for List<String> variable
	public Dataset(String identifier, String idFile, String title, String description, String subject, String keywords,
			String standards, String formats, String language, String homepage, String publisher, 
			String source, String observations, String storageTable, String license,
			String accessRights, String issuedDate, String modifiedDate, String geoLocation, String spatialWest, String spatialEast,
			String spatialSouth, String spatialNorth, String coordinateSystem, String verticalCoverageFrom,
			String verticalCoverageTo, String verticalLevel, String temporalCoverageBegin, String temporalCoverageEnd,
			String timeResolution, List<String> variable, String profileName) {
		this.identifier = identifier;
		this.idFile = idFile;
		this.title = title;
		this.description = description;
		this.subject = subject;
		this.keywords = keywords;
		this.standards = standards;
		this.formats = formats;
		this.language = language;
		this.homepage = homepage;
		this.publisher = publisher;
		this.license = license;
		this.source = source;
		this.observations = observations;
		this.storageTable = storageTable;
		this.accessRights = accessRights;
		this.issuedDate = issuedDate;
		this.modifiedDate = modifiedDate;
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
		this.variable = variable;
		this.profileName = profileName;
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

	public List<String> getVariable() {
		return variable;
	}

	public void setVariable(List<String> variable) {
		this.variable = variable;
	}

	public String getProfileName() {
		return profileName;
	}

	public void setProfileName(String profileName) {
		this.profileName = profileName;
	}

	public String getLicense() {
		return license;
	}

	public void setLicense(String license) {
		this.license = license;
	}

	public String getIdFile() {
		return idFile;
	}

	public void setIdFile(String idFile) {
		this.idFile = idFile;
	}	
	
}


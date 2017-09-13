package org.unibonn.bdo.objects;

/**
 * Represents an Dataset: contains the metadata obtained by Jsoup
 * 
 * @author Jaime M Trillos
 * @author Ana C Trillos
 *
 */
public class DatasetSuggest {
	
	private String title; //Dataset Title
	private String description; //Abstract of Dataset
	private String type; //Data resource type
	private String homepage; //URI
	private String identifier; //Unique source identifier
	private String language; //Data source language
	private String subject; //Topics related to the Dataset
	private String theme; //Keywords related to the Dataset
	private String spatialWest; //Geographic location covered by Dataset (West Coordinate)
	private String spatialEast; //Geographic location covered by Dataset (East Coordinate)
	private String spatialSouth; //Geographic location covered by Dataset (South Coordinate)
	private String spatialNorth; //Geographic location covered by Dataset (North Coordinate)
	private String temporal; //Temporal coverage of Dataset
	private String issuedDate; //Date of publication of the Dataset
	private String modifiedDate; //Date of last review of the Dataset
	private String provenance; //Provenance of Dataset
	private String conformsTo; //Coordinate reference system, Temporal reference system
	private String license; //Conditions for access and use
	private String accessRights; //Limitations on public access
	private String publisher; //Responsible party
	private String format; // Encoding
	private String characterEncoding; //Character encoding used in the Dataset
	private String accuralPeriodicity; //Denotes encoding used in the Dataset 
	private String comment; //Spatial resolution of the Dataset
	private String representationTecnique; //Spatial representation type
	private String verticalCoverage; //Water covered by the Dataset
	private String verticalLevel; //Vertical level of water covered by the Dataset
	private String temporalResolution; //Granularity of measurements
	private String gridResolution; //Horizontal/spatial grid resolution
	
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
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getHomepage() {
		return homepage;
	}
	public void setHomepage(String homepage) {
		this.homepage = homepage;
	}
	public String getIdentifier() {
		return identifier;
	}
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}
	public String getLanguage() {
		return language;
	}
	public void setLanguage(String language) {
		this.language = language;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getTheme() {
		return theme;
	}
	public void setTheme(String theme) {
		this.theme = theme;
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
	public String getTemporal() {
		return temporal;
	}
	public void setTemporal(String temporal) {
		this.temporal = temporal;
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
	public String getProvenance() {
		return provenance;
	}
	public void setProvenance(String provenance) {
		this.provenance = provenance;
	}
	public String getConformsTo() {
		return conformsTo;
	}
	public void setConformsTo(String conformsTo) {
		this.conformsTo = conformsTo;
	}
	public String getLicense() {
		return license;
	}
	public void setLicense(String license) {
		this.license = license;
	}
	public String getAccessRights() {
		return accessRights;
	}
	public void setAccessRights(String accessRights) {
		this.accessRights = accessRights;
	}
	public String getPublisher() {
		return publisher;
	}
	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}
	public String getFormat() {
		return format;
	}
	public void setFormat(String format) {
		this.format = format;
	}
	public String getCharacterEncoding() {
		return characterEncoding;
	}
	public void setCharacterEncoding(String characterEncoding) {
		this.characterEncoding = characterEncoding;
	}
	public String getAccuralPeriodicity() {
		return accuralPeriodicity;
	}
	public void setAccuralPeriodicity(String accuralPeriodicity) {
		this.accuralPeriodicity = accuralPeriodicity;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public String getRepresentationTecnique() {
		return representationTecnique;
	}
	public void setRepresentationTecnique(String representationTecnique) {
		this.representationTecnique = representationTecnique;
	}
	public String getVerticalCoverage() {
		return verticalCoverage;
	}
	public void setVerticalCoverage(String verticalCoverage) {
		this.verticalCoverage = verticalCoverage;
	}
	public String getVerticalLevel() {
		return verticalLevel;
	}
	public void setVerticalLevel(String verticalLevel) {
		this.verticalLevel = verticalLevel;
	}
	public String getTemporalResolution() {
		return temporalResolution;
	}
	public void setTemporalResolution(String temporalResolution) {
		this.temporalResolution = temporalResolution;
	}
	public String getGridResolution() {
		return gridResolution;
	}
	public void setGridResolution(String gridResolution) {
		this.gridResolution = gridResolution;
	}

}

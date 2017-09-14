/**
 * Represents a Dataset: contains the metadata
 *
 * @author Jaime M Trillos
 * @author Ana C Trillos
 *
 */

var bdo_datasetsSchema  = {
    title: {type : String}, //Dataset Title
    description: {type : String}, //Abstract of Dataset
    type: {type : String}, //Data resource type
    homepage: {type : String}, //URI
    identifier: {type : String}, //Unique source identifier
    language: {type : String}, //Data source language
    subject: {type : String}, //Topics related to the Dataset
    theme: {type : String}, //Keywords related to the Dataset
    spatialWest: {type : String}, //Geographic location covered by Dataset (West Coordinate)
    spatialEast: {type : String}, //Geographic location covered by Dataset (East Coordinate)
    spatialSouth: {type : String}, //Geographic location covered by Dataset (South Coordinate)
    spatialNorth: {type : String}, //Geographic location covered by Dataset (North Coordinate)
    spatialNorth: {type : String}, //Geographic location covered by Dataset (North Coordinate)
    temporal: {type : String}, //Temporal coverage of Dataset
    issuedDate: {type : Date}, //Date of publication of the Dataset
    modifiedDate: {type : Date}, //Date of last review of the Dataset
    provenance: {type : String}, //Provenance of Dataset
    conformsTo: {type : String}, //Coordinate reference system, Temporal reference system
    license: {type : String}, //Conditions for access and use
    accessRights: {type : String}, //Limitations on public access
    publisher: {type : String}, //Responsible party
    format: {type : String}, // Encoding
    characterEncoding: {type : String}, //Character encoding used in the Dataset
    accuralPeriodicity: {type : String}, //Denotes encoding used in the Dataset
    comment: {type : String}, //Spatial resolution of the Dataset
    representationTecnique: {type : String}, //Spatial representation type
    verticalCoverage: {type : String}, //Water covered by the Dataset
    verticalLevel: {type : String}, //Vertical level of water covered by the Dataset
    temporalResolution: {type : String}, //Granularity of measurements
    gridResolution: {type : String} //Horizontal/spatial grid resolution
}
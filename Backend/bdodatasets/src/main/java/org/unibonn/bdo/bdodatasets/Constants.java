package org.unibonn.bdo.bdodatasets;

public class Constants {
	public static final String CONFIGFILEPATH="/BDOHarmonization/BigDataOcean-Harmonization";
    public static final String CONFIGVOLUMEPATH="/dataHarmonization";
	public static final String INITFILEPATH = CONFIGFILEPATH + "/Backend/bdodatasets/bdo.ini";
    public static final String BDO_ONTOLOGY_N3 = CONFIGVOLUMEPATH + "/ontologiesN3/bdo.n3";
    public static final String GEOLOC_ONTOLOGY_N3 = CONFIGVOLUMEPATH + "/ontologiesN3/geolocbdo.n3";
    public static final String INSPIRE_ONTOLOGY_N3 = CONFIGVOLUMEPATH + "/ontologiesN3/inspire.n3";
    public static final String EIONET_ONTOLOGY_N3 = CONFIGVOLUMEPATH + "/ontologiesN3/eionet.n3";
    public static final String HTTPJWT = "";
    public static final String HTTPLIMES = "http://localhost:8080/";
	public static final String HTTPFUSEKI = "http://localhost:3031/bdoHarmonization/";
	public static final String API_GET_INFO_VOCAB_REPO = "http://localhost:3333/dataset/bdo/api/v2/vocabulary/info?vocab=";
    public static final String KAFKA_BROKERS = "localhost:9092";
    public static final Integer MESSAGE_COUNT=1000;
    public static final String CLIENT_ID="client1";
    public static final String TOPIC_NAME1="files_without_metadata";
    public static final String TOPIC_NAME2="files_with_metadata";
    public static final String GROUP_ID_CONFIG="consumerGroup1";
    public static final Integer MAX_NO_MESSAGE_FOUND_COUNT=100;
    public static final String OFFSET_RESET_LATEST="latest";
    public static final String OFFSET_RESET_EARLIER="earliest";
    public static final Integer MAX_POLL_RECORDS=1;
    
    public static final String HEADER_CONFIG_LIMES_FILE = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
            "<!DOCTYPE LIMES SYSTEM \"limes.dtd\">\n" + 
            "<LIMES>\n" + 
            "   <PREFIX>\n" + 
            "       <NAMESPACE>http://www.w3.org/1999/02/22-rdf-syntax-ns#</NAMESPACE>\n" + 
            "       <LABEL>rdf</LABEL>\n" + 
            "   </PREFIX>\n" + 
            "   <PREFIX>\n" + 
            "       <NAMESPACE>http://www.w3.org/2000/01/rdf-schema#</NAMESPACE>\n" + 
            "       <LABEL>rdfs</LABEL>\n" + 
            "   </PREFIX>\n" + 
            "   <PREFIX>\n" + 
            "       <NAMESPACE>http://www.w3.org/2002/07/owl#</NAMESPACE>\n" + 
            "       <LABEL>owl</LABEL>\n" + 
            "   </PREFIX>\n" + 
            "   <PREFIX>\n" + 
            "       <NAMESPACE>http://purl.org/dc/terms/</NAMESPACE>\n" + 
            "       <LABEL>dct</LABEL>\n" + 
            "   </PREFIX>\n" + 
            "   <PREFIX>\n" + 
            "       <NAMESPACE>http://www.w3.org/2001/XMLSchema#</NAMESPACE>\n" + 
            "       <LABEL>xsd</LABEL>\n" + 
            "   </PREFIX>\n" + 
            "   <PREFIX>\n" + 
            "       <NAMESPACE>http://www.w3.org/2004/02/skos/core#</NAMESPACE>\n" + 
            "       <LABEL>skos</LABEL>\n" + 
            "   </PREFIX>\n" + 
            "   <PREFIX>\n" + 
            "       <NAMESPACE>http://www.bigdataocean.eu/standards/canonicalmodel#</NAMESPACE>\n" + 
            "       <LABEL>bdo</LABEL>\n" + 
            "   </PREFIX>\n" + 
            "   <PREFIX>\n" + 
            "       <NAMESPACE>http://www.bigdataocean.eu/standards/geographiclocation#</NAMESPACE>\n" + 
            "       <LABEL>geolocbdo</LABEL>\n" + 
            "   </PREFIX>\n" + 
            "   <PREFIX>\n" + 
            "       <NAMESPACE>http://xmlns.com/foaf/0.1/</NAMESPACE>\n" + 
            "       <LABEL>foaf</LABEL>\n" + 
            "   </PREFIX>";
    public static final String FOOTER_CONFIG_LIMES_FILE = "   <EXECUTION>\n" + 
            "       <REWRITER>default</REWRITER>\n" + 
            "       <PLANNER>default</PLANNER>\n" + 
            "       <ENGINE>default</ENGINE>\n" + 
            "   </EXECUTION>\n" + 
            "   <OUTPUT>TAB</OUTPUT>\n" + 
            "</LIMES>";
}

package org.unibonn.bdo.bdodatasets;

public class Constants {
	public static final String CONFIGFILEPATH="/BDOHarmonization/BigDataOcean-Harmonization";
	public static final String INITFILEPATH = CONFIGFILEPATH + "/Backend/bdodatasets/bdo.ini";
    public static final String HTTPJWT = "";
	public static final String HTTPFUSEKI = "http://localhost:3031/bdoHarmonization/";
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
    public static final String YANDEX_API_KEY="";
}

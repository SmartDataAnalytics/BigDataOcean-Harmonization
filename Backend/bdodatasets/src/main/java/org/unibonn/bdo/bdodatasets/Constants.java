package org.unibonn.bdo.bdodatasets;

public class Constants {
	public static String configFilePath="/BDOHarmonization/BigDataOcean-Harmonization";
	public static String INITFILEPATH = configFilePath + "/Backend/bdodatasets/bdo.ini";
	public static String HTTPJWT = "";
	public static String HTTPFUSEKI = "localhost:3031";
	public static String KAFKA_BROKERS = "localhost:9092";
    public static Integer MESSAGE_COUNT=1000;
    public static String CLIENT_ID="client1";
    public static String TOPIC_NAME1="files_without_metadata";
    public static String TOPIC_NAME2="files_with_metadata";
    public static String GROUP_ID_CONFIG="consumerGroup1";
    public static Integer MAX_NO_MESSAGE_FOUND_COUNT=100;
    public static String OFFSET_RESET_LATEST="latest";
    public static String OFFSET_RESET_EARLIER="earliest";
    public static Integer MAX_POLL_RECORDS=1;
}

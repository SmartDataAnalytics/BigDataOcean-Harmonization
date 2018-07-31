package org.unibonn.bdo.bdodatasets;

public class Constants {
	public static String configFilePath="/BDOHarmonization/BigDataOcean-Harmonization";
	public static String tokenAuthorization = "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJiZG8iLCJleHAiOjE1NTI0ODk1ODUsInJvbGUiOiJST0xFX0FETUlOIn0.o5cZnYT3MKwfmVt06EyCMWy2qpgFPwcwZg82a3jmkNZKOVCJIbnh-LsHnEIF8BEUdj9OKrurwtknYh5ObjgLvg";
	public static String HTTPJWT = "http://212.101.173.21:8085/";
	
	public static String KAFKA_BROKERS = "10.24.10.7:9092,10.24.10.5:9092,10.24.10.11:9092,10.24.10.12:9092,10.24.10.15:9092";
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

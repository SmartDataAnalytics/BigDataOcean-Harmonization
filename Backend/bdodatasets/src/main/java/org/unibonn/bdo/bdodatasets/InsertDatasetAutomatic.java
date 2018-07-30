package org.unibonn.bdo.bdodatasets;

import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.json.simple.parser.ParseException;
import org.unibonn.bdo.bdodatasets.Constants;
import org.unibonn.bdo.connections.ConsumerCreator;
import org.unibonn.bdo.connections.ProducerCreator;
import org.unibonn.bdo.objects.Dataset;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

/**
 *  
 * @author Jaime M Trillos
 *
 * Receives 3 parameters: HDFS URL, idFile, idProfile 
 * This class connects with Kafka in order to add new metadata of a dataset (TOPIC: files_without_metadata)
 * returning the idFile when the metadata is correctly inserted in Fuseki (TOPIC: files_with_metadata)
 * 
 * runProducer();  insert new message TOPIC: files_with_metadata (TOPIC2)
 * runConsumer();  read messages TOPIC: files_without_metadata (TOPIC1)
 *
 */

public class InsertDatasetAutomatic {
	
    public static void main(String[] args) {
      runConsumer();
    }
    
    public static void runConsumer() {
        Consumer<Long, String> consumer = ConsumerCreator.createConsumer();
        int noMessageFound = 0;
        while (true) {
          ConsumerRecords<Long, String> consumerRecords = consumer.poll(1000);
          // 1000 is the time in milliseconds consumer will wait if no record is found at broker.
          if (consumerRecords.count() == 0) {
              noMessageFound++;
              if (noMessageFound > Constants.MAX_NO_MESSAGE_FOUND_COUNT)
                // If no message found count is reached to threshold exit loop.  
                break;
              else
                  continue;
          }
          //print each record. 
          consumerRecords.forEach(record -> {
        	  String recordValue = record.value();
        	  String[] tokens = recordValue.split(",");
        	  String filename = tokens[0];
        	  String idFile = tokens[1];
        	  String idProfile = tokens[2];
        	  boolean flag = false;
        	  try {
				flag = analyseInsertDatasetAutomatic(filename,idFile,idProfile);
				if(flag) {
					runProducer(idFile);
				}
				
				
			  } catch (IOException | ParseException | UnirestException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			  }
           });
          // commits the offset of record to broker. 
           consumer.commitAsync();
        }
        consumer.close();
    }
    
    public static void runProducer(String idFile) {
    	Producer<Long, String> producer = ProducerCreator.createProducer();
    	ProducerRecord<Long, String> record = new ProducerRecord<Long, String>(Constants.TOPIC_NAME2,
    			idFile);
    	try {
        	RecordMetadata metadata = producer.send(record).get();
                    System.out.println("Record sent with key " + idFile + " to partition " + metadata.partition()
                    + " with offset " + metadata.offset());
        } catch (ExecutionException e) {
        	System.out.println("Error in sending record");
            System.out.println(e);
        } catch (InterruptedException e) {
            System.out.println("Error in sending record");
            System.out.println(e);
        }
    }
    
    public static boolean analyseInsertDatasetAutomatic(String filename, String idFile, String idProfile) throws IOException, ParseException, UnirestException {
		Dataset result = new Dataset();
		boolean resultInsert = false;
		boolean resultFlag = true;
		String parameter;
		String[] splitName;
		String identifier = UUID.randomUUID().toString();
		String issuedDate;
		String modifiedDate;
		HttpResponse<JsonNode> response;
		
		//extract the metadata that contains the fileName
		String name = new File(filename).getName();
		splitName = name.split("_");
		issuedDate = splitName[1];
		modifiedDate = splitName[2];
		String[] tokens = modifiedDate.split("\\.(?=[^\\.]+$)");
		
		//HDFSFileSystem hdfsSys = new HDFSFileSystem(filename);
		//Path localFile = hdfsSys.copyFile(filename,Constants.configFilePath+"/Backend/AddDatasets/" + name);
		
		//Get the jsonProfile by the idProfile (API)
		response = Unirest.get(Constants.HTTPJWT + "fileHandler/metadataProfile/id/" + idProfile)
			.header("Content-Type", "application/json")
			.header("Authorization", Constants.tokenAuthorization)
			.asJson();
		String jsonProfile = response.getBody().getObject().toString();
		
		//Convert json into Dataset
		result = InsertNewDataset.convertToObjectDataset(jsonProfile);
		
		result.setIdentifier(identifier);
		result.setIssuedDate(issuedDate);
		result.setModifiedDate(tokens[0]);
		
		parameter = result.getTitle()+">"+result.getPublisher()+">"+result.getIssuedDate();

		resultInsert = InsertNewDataset.insertDataset("other", parameter, result);
		
		if (resultInsert) {
			response = Unirest.put(Constants.HTTPJWT + "fileHandler/file/" + idFile + 
					"/metadata/" + identifier)
					.header("Content-Type", "application/json")
					.header("Authorization", Constants.tokenAuthorization)
					.asJson();
			if(response.getStatus() == 200) {
				resultFlag = true;
			}else {
				resultFlag = false;
			}
		}
		
		//Delete the temporal file
		//hdfsSys.deleteFile(Constants.configFilePath+"/Backend/AddDatasets/" + name);

		return resultFlag;
	}
}

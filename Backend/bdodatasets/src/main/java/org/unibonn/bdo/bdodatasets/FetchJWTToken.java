package org.unibonn.bdo.bdodatasets;

import java.io.File;
import java.io.IOException;

import org.ini4j.Ini;
import org.ini4j.InvalidFileFormatException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

public class FetchJWTToken {
	
	private final static Logger log = LoggerFactory.getLogger(FetchJWTToken.class);
	private static Ini config;
	
	public static void main(String[] args){
		try {
			config = new Ini(new File(Constants.INITFILEPATH));
			exec();
		} catch (InvalidFileFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static void exec() {
		log.info("START mechanism of fetch new JWT Token");
		
		//get the values necessary to do the HttpResponse
		String url = config.get("CONFIG", "ENDPOINT");
		String username = config.get("CONFIG", "USERNAME");
		String password = config.get("CONFIG", "PASSWORD");
		
		try {
			HttpResponse<String> response = Unirest.post(url)
					  .header("Content-Type", "application/json")
					  .body("{\"username\": \"" + username + "\", \"password\": \"" + password + "\"}")
					  .asString();
			if (response.getStatus() == 200) {
				// Add the new token to the config.ini file
				String headerAuthorization = response.getHeaders().getFirst("Authorization");
				//System.out.print(headerAuthorization);
				config.put("DEFAULT", "AUTHORIZATION_JWT", headerAuthorization);
				config.store();
				log.info("The new token has been saved in the config.ini file");
			}else {
				log.error("An error has occured when calling HttpResponse");
			}
			log.info("END");
		} catch (UnirestException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

package org.unibonn.bdo.bdodatasets;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.update.UpdateExecutionFactory;
import com.hp.hpl.jena.update.UpdateFactory;
import com.hp.hpl.jena.update.UpdateProcessor;

public class QueryExecutor {
	
	public static boolean askQuery(String query) {
		QueryExecution qe = QueryExecutionFactory.sparqlService(
				Constants.HTTPFUSEKI + "query", query);
		boolean results = qe.execAsk();
		return results;
	}
	
	public static void insertQuery(String dataset) {
		UpdateProcessor upp = UpdateExecutionFactory.createRemote(
				UpdateFactory.create(String.format(dataset)), 
				Constants.HTTPFUSEKI + "update");
		upp.execute();
	}
	
	public static ResultSet selectQuery(String query) {
		QueryExecution qe = QueryExecutionFactory.sparqlService(
				Constants.HTTPFUSEKI + "query",query);
		ResultSet results = qe.execSelect();
		return results;
	}
	
	public static void deleteQuery(String query) {
		UpdateProcessor upp = UpdateExecutionFactory.createRemote(
				UpdateFactory.create(String.format(query)), 
				Constants.HTTPFUSEKI + "update");
		upp.execute();		
	}
	
}

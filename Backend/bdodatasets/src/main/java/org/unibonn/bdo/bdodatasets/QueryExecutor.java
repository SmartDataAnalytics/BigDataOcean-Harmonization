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
				"http://localhost:3030/bdoHarmonization/query", query);
		boolean results = qe.execAsk();
		return results;
		
	}
	
	public static void insertQuery(String dataset) {
		UpdateProcessor upp = UpdateExecutionFactory.createRemote(
				UpdateFactory.create(String.format(dataset)), 
				"http://localhost:3030/bdoHarmonization/update");
		upp.execute();
	}
	
	public static ResultSet selectQuery(String query) {
		QueryExecution qe = QueryExecutionFactory.sparqlService(
				"http://localhost:3030/bdoHarmonization/query",query);
		ResultSet results = qe.execSelect();
		return results;
	}
	
	
}

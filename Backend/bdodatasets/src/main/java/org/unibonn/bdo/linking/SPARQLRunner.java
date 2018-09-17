package org.unibonn.bdo.linking;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.unibonn.bdo.objects.Ontology;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.DatasetFactory;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.QuerySolutionMap;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.util.FileUtils;

/**
 * Convenience class for running SPARQL queries stored in ontologies files
 * 
 * @author Jaime M Trillos
 */

public class SPARQLRunner {
	private final Dataset dataset;
	private final String subfolder;
	
	public SPARQLRunner(Model model) {
		this(DatasetFactory.create(model),null);
	}
	
	public SPARQLRunner(Dataset dataset) {
		this(dataset,null);
	}
	
	public SPARQLRunner(Model model, String subfolder) {
		this(DatasetFactory.create(model),subfolder);
	}
	
	public SPARQLRunner(Dataset dataset, String subfolder) {
		this.dataset = dataset;
		this.subfolder = subfolder;
	}
	
	public List<Ontology> getListDataCanonicalModel(String queryFile) {
		Ontology onto;
		RDFNode uri;
		RDFNode label;
		RDFNode url;
		RDFNode canonicalName;
		String bdoURL;
		String token;
		Query query = getQuery(queryFile);
		QuerySolutionMap args = new QuerySolutionMap();
		ArrayList<Ontology> result = new ArrayList<Ontology>();
		ResultSet rs = QueryExecutionFactory.create(query, dataset, args).execSelect();
		while (rs.hasNext()) {
			// Initialization of variables 
			uri = null;
			label = null;
			url = null;
			canonicalName = null;
			bdoURL = "http://bigdataocean.eu/bdo/cf/parameter/";
			token = "";
			
			QuerySolution solution = rs.next();
			uri = solution.get("uri");
			label = solution.get("label");
			canonicalName = solution.get("canonicalName");
			
			if(solution.contains("url")) {
				url = solution.get("url");
			}
			if(url != null) {
				onto = new Ontology(uri.toString(), label.toString(), url.toString(), canonicalName.toString());
			} else {
				token = label.toString().replaceAll(" ", "_");
				bdoURL = bdoURL + token;
				onto = new Ontology(uri.toString(), label.toString(), bdoURL, canonicalName.toString());
			}
			result.add(onto);
			
		}
		return result;
	}
	
	public List<Ontology> getListDataKeywords(String queryFile) {
		Ontology onto;
		RDFNode uri;
		RDFNode label;
		Query query = getQuery(queryFile);
		QuerySolutionMap args = new QuerySolutionMap();
		ArrayList<Ontology> result = new ArrayList<Ontology>();
		ResultSet rs = QueryExecutionFactory.create(query, dataset, args).execSelect();
		while (rs.hasNext()) {
			// Initialization of variables 
			uri = null;
			label = null;
			
			QuerySolution solution = rs.next();
			uri = solution.get("uri");
			label = solution.get("label");
			onto = new Ontology(uri.toString(), label.toString());
			result.add(onto);
			
		}
		return result;
	}
	
	public List<Ontology> getListDataSubjects(String queryFile) {
		Ontology onto;
		RDFNode uri;
		RDFNode label;
		Query query = getQuery(queryFile);
		QuerySolutionMap args = new QuerySolutionMap();
		ArrayList<Ontology> result = new ArrayList<Ontology>();
		ResultSet rs = QueryExecutionFactory.create(query, dataset, args).execSelect();
		while (rs.hasNext()) {
			// Initialization of variables 
			uri = null;
			label = null;
			
			QuerySolution solution = rs.next();
			uri = solution.get("uri");
			label = solution.get("label");
			onto = new Ontology(uri.toString(), label.toString());
			result.add(onto);
			
		}
		return result;
	}
	
	public List<Ontology> getListDataGeoLocation(String queryFile) {
		Ontology onto;
		RDFNode uri;
		RDFNode label;
		RDFNode url;
		Query query = getQuery(queryFile);
		QuerySolutionMap args = new QuerySolutionMap();
		ArrayList<Ontology> result = new ArrayList<Ontology>();
		ResultSet rs = QueryExecutionFactory.create(query, dataset, args).execSelect();
		while (rs.hasNext()) {
			// Initialization of variables 
			uri = null;
			label = null;
			url = null;
			
			QuerySolution solution = rs.next();
			uri = solution.get("uri");
			label = solution.get("label");
			url = solution.get("url");
			onto = new Ontology(uri.toString(), label.toString(), url.toString());
			result.add(onto);
			
		}
		return result;
	}

	private Query getQuery(String filename) {
		if (!queryCache.containsKey(filename)) {
			try {
				return QueryFactory.create(FileUtils.readWholeFileAsUTF8(
						SPARQLRunner.class.getResourceAsStream("/queries/"+ (subfolder==null? "":subfolder+"/") + filename)));
			} catch (IOException ex) {
				System.out.println(filename);
				throw new RuntimeException(ex);
			}
		}
		return queryCache.get(filename);
	}
	private static final Map<String,Query> queryCache = new HashMap<String,Query>();
	
}

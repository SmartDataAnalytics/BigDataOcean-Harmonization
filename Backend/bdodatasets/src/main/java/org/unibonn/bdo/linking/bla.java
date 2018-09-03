package org.unibonn.bdo.linking;

import org.apache.jena.riot.RDFDataMgr;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

public class bla {
	
	public static void main(String[] args) {
		Model model = RDFDataMgr.loadModel("/home/eis/Dropbox/BDO/BigDataOcean-Harmonization/Backend/AddDatasets/ontologiesN3/bdo.n3") ;
		model = ModelFactory.createRDFSModel(model);
		
		//SPARQLRunner sparqlRunner = new SPARQLRunner(model);
	}
}

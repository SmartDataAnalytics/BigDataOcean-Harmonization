package org.unibonn.bdo.bdodatasets;

import java.io.IOException;
import java.util.List;

import com.hp.hpl.jena.rdf.model.Statement;

/**
 *  
 * @author Jaime M Trillos
 * @author Ana C Trillos
 *
 */

public class AddTriples2BDO {
	
	public static TDBConnection tdb = null;
	public static String model = "BDO_Harmonization";
	public static String uri = "http://bigdataocean.eu/bdo/";

	public static void main(String[] args) throws IOException {
		tdb = new TDBConnection();
		/*String subject = args[0];
		String property = args[1];
		String object = args[2];*/
		String subject = uri+"prueba1";
		String property = "rdf:type";
		String object = "dct:Dataset";
		
		tdb.loadModel(model, "/home/anatrillos/Documents/BigDataOcean-Harmonization/TripleStore/bdo_harmonization.ttl");
		//tdb.addStatement( graph, subject, property, object );
		
		List<Statement> result = tdb.getStatements( model, null, "http://industrialdataspace/information-model/latitude", null);
		System.out.println( model + " size: " + result.size() + "\n\t" + result );
		
		
		
		tdb.close();
	}

}

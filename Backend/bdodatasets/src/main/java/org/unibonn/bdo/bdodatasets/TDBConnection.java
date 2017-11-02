package org.unibonn.bdo.bdodatasets;

import java.util.ArrayList;
import java.util.List;

import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;
import org.apache.jena.riot.RDFLanguages;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.ReadWrite;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Selector;
import com.hp.hpl.jena.rdf.model.SimpleSelector;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.sparql.core.DatasetGraph;
import com.hp.hpl.jena.tdb.TDBFactory;
import com.hp.hpl.jena.util.FileManager;

import arq.cmd.CmdException;
import arq.cmdline.ModLangOutput;

/**
 *  
 * @author Jaime M Trillos
 * @author Ana C Trillos
 *
 */

public class TDBConnection {
	
	private Dataset bdo_harmonization;
	static ModLangOutput modLangOutput = new ModLangOutput() ;
	
	public TDBConnection (){
		bdo_harmonization = TDBFactory.createDataset( "/home/anatrillos/Documents/BigDataOcean-Harmonization/TripleStore/" );
	}
	
	public void loadModel( String modelName, String path )
	{
		Model model = null;
		
		bdo_harmonization.begin( ReadWrite.WRITE );
		try
		{
			model = bdo_harmonization.getDefaultModel();
			FileManager.get().readModel( model, path );
			bdo_harmonization.commit();
		}
		finally
		{
			bdo_harmonization.end();
		}
	}
	
	public void addStatement( String modelName, String subject, String property, String object )
	{
		Model model = null;
		
		bdo_harmonization.begin( ReadWrite.WRITE );
		try
		{
			model = bdo_harmonization.getNamedModel( modelName );
			
			Statement stmt = model.createStatement
							 ( 	
								model.createResource( subject ), 
								model.createProperty( property ), 
								model.createResource( object ) 
							 );
			
			model.add( stmt );
			bdo_harmonization.commit();
		}
		finally
		{
			if( model != null ) model.close();
			bdo_harmonization.end();
		}
	}
	
	public List<Statement> getStatements( String modelName, String subject, String property, String object )
	{
		List<Statement> results = new ArrayList<Statement>();
			
		Model model = null;
			
		bdo_harmonization.begin( ReadWrite.READ );
		try
		{
			model = bdo_harmonization.getNamedModel( modelName );
				
			Selector selector = new SimpleSelector(
						( subject != null ) ? model.createResource( subject ) : (Resource) null, 
						( property != null ) ? model.createProperty( property ) : (Property) null,
						( object != null ) ? model.createResource( object ) : (RDFNode) null
						);
				
			StmtIterator it = model.listStatements( selector );
			{
				while( it.hasNext() )
				{
					Statement stmt = it.next(); 
					results.add( stmt );
				}
			}
				
			bdo_harmonization.commit();
		}
		finally
		{
			if( model != null ) model.close();
			bdo_harmonization.end();
		}
			
		return results;
	}
	
	public void removeStatement( String modelName, String subject, String property, String object )
	{
		Model model = null;
		
		bdo_harmonization.begin( ReadWrite.WRITE );
		try
		{
			model = bdo_harmonization.getNamedModel( modelName );
			
			Statement stmt = model.createStatement
							 ( 	
								model.createResource( subject ), 
								model.createProperty( property ), 
								model.createResource( object ) 
							 );
					
			model.remove( stmt );
			bdo_harmonization.commit();
		}
		finally
		{
			if( model != null ) model.close();
			bdo_harmonization.end();
		}
	}
	
	public void close()
	{
		bdo_harmonization.close();
	}
}

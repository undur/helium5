package is.rebbi.wo.search;

import java.io.IOException;

import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;

import er.extensions.eof.ERXEC;
import is.rebbi.core.search.Indexable;
import is.rebbi.wo.definitions.EntityViewDefinition;
import is.rebbi.wo.util.USEOUtilities;
import is.rebbi.wo.util.USMassiveOperation;

public class IndexCreatorEOF {

	/**
	 * Generates the index. If an index already exists, it will be deleted and a new one created in it's stead.
	 */
	public static void createIndex() {
		IndexWriterConfig config = new IndexWriterConfig( Indexer.getAnalyzer() );

		try( IndexWriter writer = new IndexWriter( Indexer.indexDirectory(), config ); ) {
			config.setOpenMode( OpenMode.CREATE );

			for( String entityName : Indexer.entityNamesToIndex() ) {
				EntityViewDefinition def = EntityViewDefinition.get( entityName );
				Indexer.logger.info( "Indexing entity: " + entityName );

				USMassiveOperation o = new USMassiveOperation();
				USMassiveOperation.Operation handler = new USMassiveOperation.Operation() {

					@Override
					public void handleObject( Object object ) {
						try {
							Indexer.addRecord( writer, ((Indexable)object).indexRecord() );
						}
						catch( IOException e ) {
							throw new RuntimeException( "Failed to index object", e );
						}
					}
				};

				o.start( ERXEC.newEditingContext(), USEOUtilities.classForEntityNamed( entityName ), null, 500, handler );

				Indexer.logger.info( "Finished indexing entity: " + entityName );
			}

		}
		catch( Exception e ) {
			Indexer.logger.error( "Failed to perform indexing", e );
		}
	}
}
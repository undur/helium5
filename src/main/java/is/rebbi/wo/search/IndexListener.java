package is.rebbi.wo.search;

import java.io.IOException;

import org.apache.cayenne.query.SelectQuery;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;

import is.rebbi.core.search.Indexable;
import is.rebbi.wo.cayenne.USCayenne;
import is.rebbi.wo.definitions.EntityViewDefinition;

public class IndexListener {

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

				USCayenne.newContext().iterate( new SelectQuery<>( def.entityClass() ), object -> {
					try {
						Indexer.addRecord( writer, ((Indexable)object).indexRecord() );
					}
					catch( IOException e ) {
						throw new RuntimeException( "Failed to index object", e );
					}
				} );

				Indexer.logger.info( "Finished indexing entity: " + entityName );
			}

		}
		catch( Exception e ) {
			Indexer.logger.error( "Failed to perform indexing", e );
		}
	}
}
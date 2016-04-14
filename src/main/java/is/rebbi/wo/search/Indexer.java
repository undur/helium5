package is.rebbi.wo.search;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.cayenne.query.PrefetchTreeNode;
import org.apache.cayenne.query.SelectQuery;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.PrefixQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSComparator;
import com.webobjects.foundation.NSMutableArray;

import er.extensions.foundation.ERXArrayUtilities;
import is.rebbi.core.search.IndexMoreInfo;
import is.rebbi.core.search.IndexRecord;
import is.rebbi.core.search.Indexable;
import is.rebbi.wo.cayenne.USCayenne;
import is.rebbi.wo.definitions.EntityViewDefinition;
import is.rebbi.wo.util.SWSettings;

/**
 * Creates and maintains the index.
 */

public class Indexer {

	static final Logger logger = LoggerFactory.getLogger( Indexer.class );

	private static Directory _indexDirectory;
	private static IndexSearcher _indexSearcher;

	static final Analyzer getAnalyzer() {
		return new LowercaseAnalyzer();
	}

	/*
	 * Fields in the Lucene index.
	 */
	private static final String F_UNIQUE_ID = "uniqueID";
	private static final String F_TARGET_ENTITY_NAME = "targetEntityName";
	private static final String F_TARGET_ID = "targetID";
	private static final String F_NAME = "name";
	private static final String F_TEXT = "text";
	private static final String F_HIDDEN_TEXT = "hiddenText";

	/**
	 * @return The index used to store the files.
	 * @throws IOException If the index location is invalid.
	 */
	static Directory indexDirectory() throws IOException {
		if( _indexDirectory == null ) {
			_indexDirectory = FSDirectory.open( new File( SWSettings.indexPath() ).toPath() );
		}

		return _indexDirectory;
	}

	/**
	 * @return The index used to store the files.
	 * @throws IOException If the index location is invalid.
	 */
	private static IndexSearcher indexSearcher() throws IOException {

		if( _indexSearcher == null ) {
			IndexReader reader = DirectoryReader.open( indexDirectory() );
			_indexSearcher = new IndexSearcher( reader );
		}

		IndexReader readerIfChanged = DirectoryReader.openIfChanged( (DirectoryReader)_indexSearcher.getIndexReader() );

		if( readerIfChanged != null ) {
			_indexSearcher = new IndexSearcher( readerIfChanged );
		}

		return _indexSearcher;
	}

	/**
	 * Generates the index. If an index already exists, it will be deleted and a new one created in it's stead.
	 */
	public static void createIndex() {
		IndexWriterConfig config = new IndexWriterConfig( Indexer.getAnalyzer() );

		try( IndexWriter writer = new IndexWriter( Indexer.indexDirectory(), config ); ) {
			config.setOpenMode( OpenMode.CREATE );
			for( String entityName : Indexer.entityNamesToIndex() ) {
				EntityViewDefinition def = EntityViewDefinition.get( entityName );
				logger.info( "Indexing entity: " + entityName );

				SelectQuery<Object> query = new SelectQuery<>( def.entityClass() );
				List<String> keyPathsToPrefetch = ((Indexable)def.entityClass().newInstance()).keyPathsToPrefetchBeforeIndexing();

				for( String keyPath : keyPathsToPrefetch ) {
					query.addPrefetch( PrefetchTreeNode.withPath( keyPath, PrefetchTreeNode.JOINT_PREFETCH_SEMANTICS ) );
				}

				query.setFetchLimit( 1000 );

				USCayenne.newContext().iterate( query, object -> {
					try {
						Indexer.addRecord( writer, ((Indexable)object).indexRecord() );
					}
					catch( IOException e ) {
						throw new RuntimeException( "Failed to index object", e );
					}
				} );

				logger.info( "Finished indexing entity: " + entityName );
			}
			System.out.println( "Done" );
		}
		catch( Exception e ) {
			logger.error( "Failed to perform indexing", e );
		}
	}

	public static void updateRecord( IndexRecord indexRecord ) {
		logger.debug( "Updating record: " + indexRecord );
		deleteRecord( indexRecord.uniqueID() );
		addRecord( indexRecord );
	}

	public static void deleteRecord( String uniqueID ) {

		IndexWriterConfig config = new IndexWriterConfig( getAnalyzer() );

		try( IndexWriter writer = new IndexWriter( indexDirectory(), config ); ) {
			config.setOpenMode( OpenMode.CREATE_OR_APPEND );
			Term term = new Term( F_UNIQUE_ID, uniqueID );
			logger.debug( "Deleting record using term: " + term );
			writer.deleteDocuments( term );
		}
		catch( Exception e ) {
			logger.error( "An error occurred while deleting an index record", e );
		}
	}

	private static void addRecord( IndexRecord record ) {

		IndexWriterConfig config = new IndexWriterConfig( getAnalyzer() );

		try( IndexWriter writer = new IndexWriter( indexDirectory(), config ); ) {
			config.setOpenMode( OpenMode.CREATE_OR_APPEND );
			addRecord( writer, record );
		}
		catch( Exception e ) {
			logger.error( "An error occurred while adding an index record", e );
		}
	}

	/**
	 * Adds a single record to the index.
	 */
	private static void addRecord( IndexWriter writer, IndexRecord record ) throws CorruptIndexException, IOException {

		logger.debug( "Adding new index record:" + record );

		if( record == null ) {
			throw new RuntimeException( "[record] is null, this must never happen. Check your code." );
		}

		Document doc = new Document();

		Field uniqueIDField = new Field( F_UNIQUE_ID, record.uniqueID(), Field.Store.YES, Field.Index.NOT_ANALYZED );
		Field entityNameField = new Field( F_TARGET_ENTITY_NAME, record.targetEntityName(), Field.Store.YES, Field.Index.NOT_ANALYZED );
		Field targetIDField = new Field( F_TARGET_ID, String.valueOf( record.targetID() ), Field.Store.YES, Field.Index.NOT_ANALYZED );

		Field nameField = new Field( F_NAME, record.name() == null ? "" : record.name(), Field.Store.YES, Field.Index.ANALYZED );
		nameField.setBoost( 2 );

		Field textField = new Field( F_TEXT, record.text() == null ? "" : record.text(), Field.Store.YES, Field.Index.ANALYZED );
		textField.setBoost( 1 );

		Field hiddenTextField = new Field( F_HIDDEN_TEXT, record.hiddenText() == null ? "" : record.hiddenText(), Field.Store.YES, Field.Index.ANALYZED );
		textField.setBoost( 1 );

		doc.add( uniqueIDField );
		doc.add( entityNameField );
		doc.add( targetIDField );
		doc.add( nameField );
		doc.add( textField );
		doc.add( hiddenTextField );

		if( record instanceof IndexMoreInfo ) {
			for( Entry<String, String> entry : ((IndexMoreInfo)record).additionalData().entrySet() ) {
				Field f = new Field( entry.getKey(), entry.getValue() == null ? "" : record.hiddenText(), Field.Store.YES, Field.Index.ANALYZED );
				doc.add( f );
			}
		}

		writer.addDocument( doc );
	}

	/**
	 * Perform a search on the index.
	 */
	public static List<IndexRecord> search( String queryString ) {

		try {
			QueryParser queryParser = new MultiFieldQueryParser( new String[] { F_NAME, F_TEXT, F_HIDDEN_TEXT }, getAnalyzer() );
			queryParser.setDefaultOperator( QueryParser.Operator.AND );
			Query query = queryParser.parse( queryString );

			ScoreDoc[] hits = indexSearcher().search( query, null, 2000 ).scoreDocs;

			List<IndexRecord> results = new ArrayList<>();

			for( int i = 0; i < hits.length; ++i ) {
				Document doc = indexSearcher().doc( hits[i].doc );
				String name = doc.get( F_NAME );
				String text = doc.get( F_TEXT );
				String entityName = doc.get( F_TARGET_ENTITY_NAME );
				String targetID = doc.get( F_TARGET_ID );

				IndexRecord record = IndexRecord.create( entityName, targetID );
				record.setName( name );
				record.setText( text );
				results.add( record );
			}

			return results;
		}
		catch( Exception e ) {
			e.printStackTrace();
			return new ArrayList<>();
		}
	}

	/**
	 * Perform a search on the index.
	 */
	public static List<String> autocomplete( String searchString ) {
		Set<String> results = new HashSet<>();
		searchString = searchString.toLowerCase();

		try {
			Query query = new PrefixQuery( new Term( F_NAME, searchString ) );
			ScoreDoc[] hits = indexSearcher().search( query, null, 2000 ).scoreDocs;

			for( int i = 0; i < hits.length; ++i ) {
				Document doc = indexSearcher().doc( hits[i].doc );
				String name = doc.get( F_NAME );

				if( name.toLowerCase().startsWith( searchString ) ) {
					results.add( name );
				}
			}
		}
		catch( Exception e ) {
			logger.error( "An error occurred during autocomplete: " + e );
		}

		NSArray<String> resultArray = new NSArray<>( results );

		try {
			resultArray = resultArray.sortedArrayUsingComparator( NSComparator.AscendingCaseInsensitiveStringComparator );
		}
		catch( Exception e ) {
			logger.error( "An error occurred while sorting", e );
		}

		return resultArray;
	}

	public static NSArray<IndexRecord> results( String queryString ) {
		NSMutableArray<IndexRecord> results = new NSMutableArray<>();
		ERXArrayUtilities.addObjectsFromArrayWithoutDuplicates( results, Indexer.search( queryString ) );
		return results;
	}

	/**
	 * @return A list of all entities to index.
	 */
	public static List<String> entityNamesToIndex() {
		List<String> entityNames = new ArrayList<>();

		for( EntityViewDefinition evd : EntityViewDefinition.all() ) {
			Class c = evd.entityClass();

			if( c != null ) {
				boolean isIndexable = Indexable.class.isAssignableFrom( c );

				if( isIndexable ) {
					entityNames.add( evd.name() );
				}
			}
		}

		return entityNames;
	}
}
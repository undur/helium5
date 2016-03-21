package is.rebbi.wo.search;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Stofnun Árna Magnússonar gaf árið 2009 út gagnasafn sem kallast "BÍN" (skammstöfun fyrir Beyginarlýsing íslensks nútímamáls).
 * Í þessu gagnasafni er að finna skilgreiningar á því hvernig öll (eða a.m.k. flest) orð í íslensku beygjast.
 *
 * Þessum einfalda klasa er ætlað að veita Java-forritum einfaldan aðgang að gögnunum sem er að finna í
 * BÍN-grunninum. Til að nota klasann þarf að setja upp Lucene-leitarvélina.
 *
 * http://en.wikipedia.org/wiki/Icelandic_language
 */

public class IcelandicInflector {

	private static Logger logger = LoggerFactory.getLogger( IcelandicInflector.class );

	private static IcelandicInflector _sharedInstance;

	/*
	 * Fields stored in the index.
	 */
	private static final String F_TYPE = "type";
	private static final String F_WORD = "word";
	private static final String F_CATEGORY = "category";
	private static final String F_MODIFIED_WORD = "modifiedWord";
	private static final String F_MODIFIED_WORD_LENGTH = "modifiedWordLength";
	private static final String F_MODIFIERS = "modifiers";

	/*
	 * Properties (documented in corresponding accessor methods).
	 */
	private static final String P_SOURCE_PATH = IcelandicInflector.class.getSimpleName() + ".sourceFilePath";
	private static final String P_INDEX_PATH = IcelandicInflector.class.getSimpleName() + ".indexPath";

	/*
	 * Lazily initialized variables (documented in corresponding accessor methods).
	 */
	private String _sourceFilePath;
	private String _indexPath;
	private Directory _indexDirectory;
	private IndexSearcher _indexSearcher;

	private static final Analyzer getAnalyzer() {
		return new LowercaseAnalyzer();
	}

	public static IcelandicInflector sharedInstance() {
		if( _sharedInstance == null ) {
			_sharedInstance = new IcelandicInflector();
		}

		return _sharedInstance;
	}

	/**
	 * Constructs an instance that will locate files using properties.
	 */
	public IcelandicInflector() {
		this( null, null );
	}

	/**
	 * Constructs an instance using the specified file locations.
	 *
	 * @param indexPath The location of the index on disk.
	 * @param sourceFilePath The BÍN-source file to read data from.
	 */
	public IcelandicInflector( String indexPath, String sourceFilePath ) {
		_indexPath = indexPath;
		_sourceFilePath = sourceFilePath;
		checkEnvironment();
	}

	/**
	 * Confirm that the environment is ready for use (properties/variables all set).
	 */
	private void checkEnvironment() {
		if( indexPath() == null ) {
			String message = Util.varString( "No path set for storing the index. Please set the property '{}' or set the 'indexPath' variable.", P_INDEX_PATH );
			throw new RuntimeException( message );
		}

		if( !indexFile().exists() ) {
			logger.info( "Note that no index exist at the specified location '{}'. An index will now be created by reading data from '{}'.", indexPath(), sourceFilePath() );

			if( sourceFilePath() == null ) {
				throw new RuntimeException( Util.varString( "Indexing failed: No source file path was specified. Please set the property '{}' or set the 'sourceFilePath' variable", P_SOURCE_PATH ) );
			}

			if( !sourceFile().exists() ) {
				throw new RuntimeException( Util.varString( "Indexing failed: No source file was found to read from at '{}'", sourceFilePath() ) );
			}

			createIndex();
		}
	}

	/**
	 * @return The location of the BÍN source file on disk ("SHsnid.csv")
	 */
	private String sourceFilePath() {
		if( _sourceFilePath == null ) {
			_sourceFilePath = System.getProperty( P_SOURCE_PATH );

			if( _sourceFilePath == null ) {
				throw new RuntimeException( "_sourceFilePath must not be null." );
			}
		}

		return _sourceFilePath;
	}

	/**
	 * @return The location of the index folder on disk.
	 */
	private String indexPath() {
		if( _indexPath == null ) {
			_indexPath = System.getProperty( P_INDEX_PATH );

			if( _indexPath == null ) {
				throw new RuntimeException( "_indexPath must not be null." );
			}
		}

		return _indexPath;
	}

	private File sourceFile() {
		return new File( sourceFilePath() );
	}

	private File indexFile() {
		return new File( indexPath() );
	}

	/**
	 * @return The index used to store the files.
	 * @throws IOException If the index location is invalid.
	 */
	private Directory indexDirectory() throws IOException {
		if( _indexDirectory == null ) {
			_indexDirectory = FSDirectory.open( indexFile().toPath() );
		}

		return _indexDirectory;
	}

	/**
	 * @return The index used to store the files.
	 * @throws IOException If the index location is invalid.
	 */
	private IndexSearcher indexSearcher() throws IOException {
		if( _indexSearcher == null ) {
			IndexReader reader = DirectoryReader.open( indexDirectory() );
			_indexSearcher = new IndexSearcher( reader );
		}

		return _indexSearcher;
	}

	/**
	 * Generates the index. If an index already exists, it will be deleted and a new one created in it's stead.
	 */
	public void createIndex() {
		logger.info( "Starting index creation. Reading source file from {}", sourceFilePath() );

		File sourceFile = sourceFile();

		try( BufferedReader sourceFileReader = new BufferedReader( new FileReader( sourceFile ) ) ; ) {

			logger.info( "Input file size: " + sourceFile.length() );

			IndexWriterConfig config = new IndexWriterConfig( getAnalyzer() );
			config.setOpenMode( OpenMode.CREATE );
			IndexWriter writer = new IndexWriter( indexDirectory(), config );

			int currentLineIndex = 0;
			String line = null;

			while( (line = sourceFileReader.readLine()) != null ) {
				String[] record = line.split( ";" );
				addWordDefinition( writer, record[0], record[2], record[3], record[4], record[5] );
				if( ++currentLineIndex % 100000 == 0 ) {
					logger.info( "Reading line {}: {},{},{},{},{}", new Object[] { String.valueOf( currentLineIndex ), record[0], record[2], record[3], record[4], record[5] } );
				}
			}

			logger.info( "Finished reading words, almost done. Please wait for a few seconds while I finish up..." );
			writer.close();
			logger.info( "Finished creating index. A total of " + currentLineIndex + " word forms were indexed. IcelandicInflector is now ready for use." );
		}
		catch( Exception e ) {
			logger.error( "An error occurred while creating the index", e );
		}
	}

	/**
	 * Adds a single word definition to the index.
	 */
	private static void addWordDefinition( IndexWriter indexWriter, String word, String type, String category, String modifiedWord, String modifiers ) throws CorruptIndexException, IOException {
		Document doc = new Document();
		doc.add( field( F_WORD, word ) );
		doc.add( field( F_TYPE, type ) );
		doc.add( field( F_CATEGORY, category ) );
		doc.add( field( F_MODIFIED_WORD, modifiedWord ) );
		doc.add( field( F_MODIFIED_WORD_LENGTH, String.valueOf( modifiedWord.length() ) ) );
		doc.add( field( F_MODIFIERS, modifiers ) );
		indexWriter.addDocument( doc );
	}

	private static Field field( String name, String value ) {
		return new Field( name, value, Field.Store.YES, Field.Index.ANALYZED );
		//		return new StringField( name, value, Field.Store.YES );
	}

	/**
	 * @return A phrase where each word is modified using the given modifier string. Capitalization is maintained.
	 */
	public String phrase( String phrase, String modifierString ) {
		String[] words = phrase.split( " " );

		StringBuilder b = new StringBuilder();

		for( String word : words ) {
			String modifiedWord = word( word, modifierString );

			if( Util.isCapitalized( word ) ) {
				modifiedWord = Util.capitalize( modifiedWord );
			}

			b.append( modifiedWord );
			b.append( " " );
		}

		return b.toString();
	}

	public boolean exists( String word ) {
		try {
			String queryString = F_MODIFIED_WORD + ":" + word;
			Query query = new QueryParser( F_MODIFIED_WORD, getAnalyzer() ).parse( queryString );
			ScoreDoc[] hits = indexSearcher().search( query, null, 1000 ).scoreDocs;
			return hits.length > 0;
		}
		catch( ParseException e ) {
			throw new RuntimeException( "An error occurred while parsing your query", e );
		}
		catch( IOException e ) {
			throw new RuntimeException( "An IO error occurred while checking if a word exists", e );
		}
	}

	/**
	 * @return Every possible form of a given word.
	 *
	 * @param word The word to search for, nominative singular
	 * @param type Type of word (optional)
	 */
	public List<String> formsOfWord( String word, String type ) {
		List<String> results = new ArrayList<>();

		try {
			IndexSearcher searcher = indexSearcher();
			String queryString = F_WORD + ":" + word;

			if( type != null ) {
				queryString += " AND " + F_TYPE + ":" + type;
			}

			Query query = new QueryParser( F_WORD, getAnalyzer() ).parse( queryString );
			ScoreDoc[] hits = searcher.search( query, null, 1000 ).scoreDocs;

			Set<String> modifiedWords = new HashSet<>();

			for( int i = 0; i < hits.length; ++i ) {
				Document doc = searcher.doc( hits[i].doc );
				String modifiedWord = doc.get( F_MODIFIED_WORD );
				modifiedWords.add( modifiedWord );
			}

			results = new ArrayList<>( modifiedWords );
		}
		catch( Exception e ) {
			logger.error( "Failed to find forms of word: " + word, e );
		}

		Collections.sort( results );
		return results;
	}

	/**
	 * @return Every possibly form of a given word.
	 *
	 * @param word The word to search for, nominative singular
	 * @param type Type of word (optional)
	 */
	public List<String> formsOfWordSearchingEveryForm( String word, String type ) {

		List<String> results = new ArrayList<>();
		IndexSearcher searcher = null;

		try {
			String queryString = F_MODIFIED_WORD + ":" + word;

			if( type != null ) {
				queryString += " AND " + F_TYPE + ":" + type;
			}

			Query query = new QueryParser( F_MODIFIED_WORD, getAnalyzer() ).parse( queryString );
			searcher = indexSearcher();
			ScoreDoc[] hits = searcher.search( query, null, 1000 ).scoreDocs;
			Set<String> modifiedWords = new HashSet<>();

			for( int i = 0; i < hits.length; ++i ) {
				Document doc = searcher.doc( hits[i].doc );
				String fetchedWord = doc.get( F_WORD );
				modifiedWords.addAll( formsOfWord( fetchedWord, null ) );
			}

			results = new ArrayList<>( modifiedWords );
		}
		catch( Exception e ) {
			logger.error( "Failed to find forms of word: " + word, e );
		}

		Collections.sort( results );
		return results;
	}

	/**
	 * @return The modified form of a word. Null if no matching word is found .
	 */
	public String word( String word, String modifierString ) {

		String result = null;

		try {
			IndexSearcher searcher = indexSearcher();
			String queryString = F_WORD + ":" + word + " AND " + F_MODIFIERS + ":" + modifierString;
			Query q = new QueryParser( F_MODIFIED_WORD, getAnalyzer() ).parse( queryString );
			TopScoreDocCollector collector = TopScoreDocCollector.create( 1 );
			searcher.search( q, collector );
			ScoreDoc[] hits = collector.topDocs().scoreDocs;

			for( int i = 0; i < hits.length; ++i ) {
				Document d = searcher.doc( hits[i].doc );
				String modifiedWord = d.get( F_MODIFIED_WORD );
				result = modifiedWord;
			}
		}
		catch( Exception e ) {
			e.printStackTrace();
		}

		return result;
	}

	/**
	 * @return The modified form of a word. Null if no matching word is found .
	 */
	public List<String> matching( String word, Integer maxLength, int maxMatches, boolean unique ) {

		Collection<String> results = null;

		if( unique ) {
			results = new HashSet<>();
		}
		else {
			results = new ArrayList<>();
		}

		try {
			IndexSearcher searcher = indexSearcher();
			String queryString = F_MODIFIED_WORD + ":" + word;

			if( maxLength != null ) {
				queryString += " AND " + F_MODIFIED_WORD_LENGTH + ":\"" + maxLength + "\"";
			}

			QueryParser p = new QueryParser( F_MODIFIED_WORD, getAnalyzer() );
			p.setAllowLeadingWildcard( true );
			Query q = p.parse( queryString );
			TopScoreDocCollector collector = TopScoreDocCollector.create( maxMatches );
			searcher.search( q, collector );
			ScoreDoc[] hits = collector.topDocs().scoreDocs;

			for( int i = 0; i < hits.length; ++i ) {
				Document d = searcher.doc( hits[i].doc );
				String modifiedWord = d.get( F_MODIFIED_WORD );
				results.add( modifiedWord );
			}
		}
		catch( Exception e ) {
			e.printStackTrace();
		}

		if( results instanceof Set ) {
			results = new ArrayList<>( results );
		}

		Collections.sort( (List<String>)results );

		return (List<String>)results;
	}

	/**
	 * An inner class containing some utility methods.
	 * Methods here should't really be part of this class file
	 * but are included nonetheless to minimize dependencies.
	 */
	private static class Util {

		/**
		 * Indicates if the given word is capitalized.
		 * Generic utility method.
		 *
		 * @return true only if a string starting with an upper case letter.
		 */
		private static boolean isCapitalized( String string ) {

			if( string == null || string.length() == 0 ) {
				return false;
			}

			String original = string.substring( 0, 1 );
			String converted = original.toUpperCase();
			return original.equals( converted );
		}

		/**
		 * Capitalizes the given string.
		 *
		 * @return The given string, with the first letter of the first word in upper case.The rest of the string is left unmodified.
		 */
		private static String capitalize( String s ) {

			if( s == null || s.length() == 0 ) {
				return s;
			}

			return s.substring( 0, 1 ).toUpperCase() + s.substring( 1 );
		}

		/**
		 * Primitive templating.
		 */
		private static final String varString( Object message, Object... objects ) {

			String returnString = null;

			if( message != null ) {
				returnString = message.toString();

				if( objects != null ) {
					String VARIABLE = "\\{\\}";
					for( int i = 0; i < objects.length; i++ ) {
						Object replacement = objects[i];

						if( replacement == null ) {
							replacement = "null";
						}

						returnString = returnString.replaceFirst( VARIABLE, replacement.toString() );
					}
				}

			}

			return returnString;
		}
	}
}
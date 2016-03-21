package is.rebbi.wo.search;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.lucene.queryparser.flexible.standard.QueryParserUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Intelligently constructs a Lucene search from Icelandic input.
 */

public class SearchTermConstructor {

	private static final Logger logger = LoggerFactory.getLogger( SearchTermConstructor.class );

	/**
	 * The string to search for.
	 */
	private String _inputString;

	/**
	 * The parsed list of all words.
	 */
	private List<Part> _parts;

	public SearchTermConstructor( String inputString ) {
		setInputString( inputString );
	}

	private String inputString() {
		return _inputString;
	}

	private void setInputString( String value ) {
		_inputString = value;
	}

	/**
	 * @return Quoted sentences in the search input.
	 */
	private List<String> sentences() {
		List<String> result = new ArrayList<>();
		Pattern p = Pattern.compile( "\"(.*?)\"", Pattern.DOTALL );
		Matcher m = p.matcher( inputString() );

		while( m.find() ) {
			String sentence = m.group();
			String substring = sentence.substring( 1, sentence.length() - 1 ); // Removing the quotes
			result.add( substring );
		}

		return result;
	}

	/**
	 * @return All the words in the search input string.
	 */
	private List<String> words() {
		List<String> result = new ArrayList<>();

		String[] split = inputString().split( " " );

		boolean inSentence = false;

		for( String s : split ) {
			if( s.startsWith( "\"" ) ) {
				inSentence = true;
			}

			if( !inSentence ) {
				result.add( s );
			}

			if( s.endsWith( "\"" ) ) {
				inSentence = false;
			}
		}

		return result;
	}

	private List<String> wordsInflected() {
		List<String> results = new ArrayList<>();

		for( String word : words() ) {
			List<String> inflectedWords = IcelandicInflector.sharedInstance().formsOfWordSearchingEveryForm( word, null );

			for( String inflectedWord : inflectedWords ) {
				if( !results.contains( inflectedWord ) ) {
					results.add( inflectedWord );
				}
			}
		}

		return results;
	}

	private List<String> sentencesInflected() {
		List<String> results = new ArrayList<>();

		for( String word : words() ) {
			List<String> inflectedWords = IcelandicInflector.sharedInstance().formsOfWordSearchingEveryForm( word, null );

			for( String inflectedWord : inflectedWords ) {
				if( !results.contains( inflectedWord ) ) {
					results.add( inflectedWord );
				}
			}
		}

		return results;
	}

	public String searchString() {
		List<String> words = wordsInflected();
		List<String> sentences = sentences();

		StringBuilder b = new StringBuilder();

		for( int i = words.size() - 1; i >= 0; i-- ) {
			b.append( words.get( i ) );
			b.append( " " );
		}

		for( int i = sentences.size() - 1; i >= 0; i-- ) {
			b.append( "\"" + sentences.get( i ) + "\"" );
			b.append( " " );
		}

		return b.toString();
	}

	public static void main( String[] argv ) {
		System.setProperty( "IcelandicInflector.indexPath", "/Users/hugi/Documents/Verkefni/ISMUS/index_bin" );
		SearchTermConstructor s = new SearchTermConstructor( "möðrudalur \"Jón Stefánsson\" orgel \"Hugi Þórðarson\"" );

		logger.info( "Words: " + s.words() );
		logger.info( "Words inflected: " + s.wordsInflected() );

		logger.info( "Sentences: " + s.sentences() );
		logger.info( "Sentences inflected: " + s.sentencesInflected() );
		logger.info( "SearchString: " + s.searchString() );
	}

	private static boolean isQuoted( String s ) {
		return s != null && s.length() > 1 && s.startsWith( "\"" ) && s.endsWith( "\"" );
	}

	public static String constructQueryString( String searchString, String entityName, boolean useInflection ) {

		if( searchString == null && entityName == null ) {
			return null;
		}

		if( isQuoted( searchString ) ) {
			searchString = QueryParserUtil.escape( searchString.substring( 1, searchString.length() - 1 ) );
			searchString = "\"" + searchString + "\"";
		}
		else {
			searchString = QueryParserUtil.escape( searchString );
		}

		if( searchString.startsWith( "\"" ) && searchString.endsWith( "\"" ) ) {
			return searchString;
		}

		List<String> originalTerms = Arrays.asList( searchString.split( " " ) );

		StringBuilder b = new StringBuilder();

		int termSize = originalTerms.size();

		if( useInflection ) {
			for( String term : originalTerms ) {
				List<String> forms = null;

				if( shouldInflect( term ) ) {
					forms = IcelandicInflector.sharedInstance().formsOfWordSearchingEveryForm( term, null );
				}

				if( forms == null || forms.isEmpty() ) {
					forms = Arrays.asList( term );
				}

				int i = forms.size();

				b.append( "(" );
				for( String form : forms ) {
					b.append( form );

					if( --i > 0 ) {
						b.append( " OR " );
					}
				}
				b.append( ")" );

				if( --termSize > 0 ) {
					b.append( " AND " );
				}
			}
		}
		else {
			if( searchString != null ) {
				b.append( searchString );
			}
		}

		if( entityName != null ) {
			b.append( " entityName:" );
			b.append( entityName );
		}

		return b.toString();
	}

	private static boolean shouldInflect( String term ) {
		return !term.contains( "*" );
	}

	private static class Part {

		private String _original;
		private List<String> _inflections;

		public Part( String original, List<String> inflections ) {
			setOriginal( original );
			setInflections( inflections );
		}

		public String original() {
			return _original;
		}

		public void setOriginal( String value ) {
			_original = value;
		}

		public List<String> inflections() {
			return _inflections;
		}

		public void setInflections( List<String> value ) {
			_inflections = value;
		}

		@Override
		public String toString() {
			return "Part [_original=" + _original + ", _inflections=" + _inflections + "]";
		}
	}
}
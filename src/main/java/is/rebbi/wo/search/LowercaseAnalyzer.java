package is.rebbi.wo.search;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.core.WhitespaceTokenizer;

/**
 * Just lowercases a field's value
 */

public final class LowercaseAnalyzer extends Analyzer {

	@Override
	protected TokenStreamComponents createComponents( final String fieldName ) {
		final Tokenizer src = new WhitespaceTokenizer();
		TokenStream tok = new LowerCaseFilter( src );
		return new TokenStreamComponents( src, tok );
	}
}
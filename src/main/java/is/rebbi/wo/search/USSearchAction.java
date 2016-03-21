package is.rebbi.wo.search;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WORequest;
import com.webobjects.foundation.NSArray;

import er.extensions.appserver.ERXDirectAction;
import is.rebbi.wo.search.components.USSearchPage;
import is.rebbi.wo.util.USHTTPUtilities;

public class USSearchAction extends ERXDirectAction {

	private static final Logger logger = LoggerFactory.getLogger( USSearchAction.class );

	public USSearchAction( WORequest r ) {
		super( r );
	}

	public WOActionResults searchAction() {

		String searchString = request().stringFormValueForKey( "searchString_field" );

		if( searchString == null ) {
			searchString = "";
		}

		boolean useInflection = true;

		String queryString = SearchTermConstructor.constructQueryString( searchString, null, useInflection );

		USSearchPage nextPage = pageWithName( USSearchPage.class );
		nextPage.setSearchString( searchString );
		nextPage.setQueryString( queryString );
		nextPage.setUseInflection( useInflection );
		nextPage.setResults( new NSArray<>( Indexer.results( queryString ) ) );
		return nextPage;
	}

	public WOActionResults searchRedirectionAction() {
		String searchString = request().stringFormValueForKey( "searchString_field" );

		if( searchString != null ) {
			try {
				searchString = URLEncoder.encode( searchString, "UTF-8" );
			}
			catch( UnsupportedEncodingException e ) {
				logger.error( "Failed to encode searchString: " + searchString, e );
			}
		}

		String url = urlForSearchString( searchString );
		return USHTTPUtilities.redirectTemporary( url );
	}

	private String urlForSearchString( String searchString ) {
		StringBuilder b = new StringBuilder();
		b.append( "http://" );
		b.append( USHTTPUtilities.host( request() ) );
		b.append( "/search/" + searchString );
		return b.toString();
	}
}
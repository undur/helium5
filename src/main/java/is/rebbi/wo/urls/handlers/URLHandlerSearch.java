package is.rebbi.wo.urls.handlers;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WOContext;

import is.rebbi.wo.search.USSearchAction;

public class URLHandlerSearch extends URLHandler {

	public URLHandlerSearch( String url, WOContext context ) {
		super( url, context );
	}

	@Override
	public WOActionResults generateResponse() {
		String searchString = path().getString( 1 );
		return USSearchAction.search( searchString, context() );
	}
}
package is.rebbi.wo.urls.handlers;

import java.util.function.BiFunction;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WOContext;

import is.rebbi.wo.search.USSearchAction;

public class URLHandlerSearch implements URLHandler {

	@Override
	public String pattern() {
		return "/search/";
	}

	@Override
	public BiFunction<String, WOContext, WOActionResults> execute() {
		return ( url, context ) -> {
			String searchString = url.substring( pattern().length() );
			return USSearchAction.search( searchString, context );
		};
	}
}
package is.rebbi.wo.urls.handlers;

import java.util.function.BiFunction;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WOContext;

import er.extensions.appserver.ERXApplication;
import is.rebbi.wo.components.USListPageView;
import is.rebbi.wo.definitions.EntityViewDefinition;

public class URLHandlerSearch implements URLHandler {

	private static final String SEARCH_PREFIX = "/search/";

	@Override
	public String prefix() {
		return SEARCH_PREFIX;
	}

	@Override
	public BiFunction<String, WOContext, WOActionResults> execute() {
		return ( url, context ) -> {
			String entityIdentifier = url.substring( SEARCH_PREFIX.length() );

			EntityViewDefinition t = null;

			if( entityIdentifier.startsWith( "entity-" ) ) {
				entityIdentifier = entityIdentifier.substring( "entity-".length(), entityIdentifier.length() );
				t = EntityViewDefinition.get( entityIdentifier );
			}
			else {
				t = EntityViewDefinition.definitionForURLPrefix( entityIdentifier );
			}

			USListPageView nextPage = ERXApplication.erxApplication().pageWithName( USListPageView.class, context );
			nextPage.setSelectedViewDefinition( t );
			return nextPage;
		};
	}
}
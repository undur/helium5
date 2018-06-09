package is.rebbi.wo.urls.handlers;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WOContext;

import er.extensions.appserver.ERXApplication;
import is.rebbi.wo.components.USListPageView;
import is.rebbi.wo.definitions.EntityViewDefinition;

public class URLHandlerList extends URLHandler {

	public URLHandlerList( String url, WOContext context ) {
		super( url, context );
	}

	@Override
	public WOActionResults generateResponse() {
		String entityIdentifier = path().getString( 1 );

		EntityViewDefinition t = null;

		if( entityIdentifier.startsWith( "entity-" ) ) {
			entityIdentifier = entityIdentifier.substring( "entity-".length(), entityIdentifier.length() );
			t = EntityViewDefinition.get( entityIdentifier );
		}
		else {
			t = EntityViewDefinition.definitionForURLPrefix( entityIdentifier );
		}

		USListPageView nextPage = ERXApplication.erxApplication().pageWithName( USListPageView.class, context() );
		nextPage.setSelectedViewDefinition( t );
		return nextPage;
	}
}
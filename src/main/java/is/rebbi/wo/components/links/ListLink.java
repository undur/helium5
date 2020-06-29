package is.rebbi.wo.components.links;

import com.webobjects.appserver.WOContext;

import er.extensions.appserver.ERXApplication;
import er.extensions.components.ERXStatelessComponent;
import is.rebbi.wo.definitions.EntityViewDefinition;
import is.rebbi.wo.urls.USURLProvider;

/**
 * Link to list of objects.
 */

public class ListLink extends ERXStatelessComponent {

	public ListLink( WOContext context ) {
		super( context );
	}

	private String entityName() {
		return (String)valueForBinding( "entityName" );
	}

	public String url() {
		return urlForListInContext( entityName(), context() );
	}

	/**
	 * @return The URL for viewing the default list of the specified entity.
	 */
	private static String urlForListInContext( String entityName, WOContext context ) {
		String url = "/l/" + EntityViewDefinition.get( entityName ).urlPrefix();

		if( ERXApplication.erxApplication().isDevelopmentMode() ) {
			url = USURLProvider.makeURLDeveloperFriendly( url, context );
		}

		return url;
	}

}
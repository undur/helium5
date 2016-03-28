package is.rebbi.wo.components.links;

import com.webobjects.appserver.WOContext;

import er.extensions.components.ERXStatelessComponent;
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
		return USURLProvider.urlForListInContext( entityName(), context() );
	}
}
package is.rebbi.wo.components.links;

import com.webobjects.appserver.WOContext;

import er.extensions.components.ERXStatelessComponent;
import er.extensions.routes.RouteTable;
import is.rebbi.wo.util.USSettings;

public class RouteLink extends ERXStatelessComponent {

	public RouteLink( WOContext context ) {
		super( context );
	}

	public String url() {
		return stringValueForBinding( "url" );
	}

	public String href() {

		if( !USSettings.generateFriendlyURLs() ) {
			return RouteTable.urlForDevelopment( url(), context() );
		}

		return url();
	}
}
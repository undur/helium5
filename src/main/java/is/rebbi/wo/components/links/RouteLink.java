package is.rebbi.wo.components.links;

import com.webobjects.appserver.WOContext;

import er.extensions.components.ERXStatelessComponent;

public class RouteLink extends ERXStatelessComponent {

	public RouteLink( WOContext context ) {
		super( context );
	}

	public String url() {
		return stringValueForBinding( "url" );
	}

	public String href() {
		return url();
	}
}
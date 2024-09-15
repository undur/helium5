package is.rebbi.wo.components.links;

import com.webobjects.appserver.WOContext;

import er.extensions.components.ERXStatelessComponent;
import is.rebbi.wo.urls.USURLProvider;
import is.rebbi.wo.util.USSettings;

public class RouteLink extends ERXStatelessComponent {

	public RouteLink( WOContext context ) {
		super( context );
	}

	public String url() {
		return stringValueForBinding( "url" );
	}

	public String href() {

		if( USSettings.generateFriendlyURLs() ) {
			return url();
		}

		return USURLProvider.urlForDevelopment( url(), context() );
	}
}
package is.rebbi.wo.components.links;

import com.webobjects.appserver.WOContext;

import er.extensions.components.ERXComponent;
import is.rebbi.wo.urls.USURLProvider;
import is.rebbi.wo.util.SWSettings;

public class RouteLink extends ERXComponent {

	public String url;

	public RouteLink( WOContext context ) {
		super( context );
	}

	public String href() {

		if( SWSettings.generateFriendlyURLs() ) {
			return url;
		}

		return USURLProvider.urlForDevelopment( url, context() );
	}
}
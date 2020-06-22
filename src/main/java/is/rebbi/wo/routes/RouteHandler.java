package is.rebbi.wo.routes;

import com.webobjects.appserver.WOActionResults;

public abstract class RouteHandler {

	private String _url;

	public RouteHandler( final String url ) {
		this._url = url;
	}

	public abstract WOActionResults generateResponse();
}
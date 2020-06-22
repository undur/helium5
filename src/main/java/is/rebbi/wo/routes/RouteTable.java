package is.rebbi.wo.routes;

import java.util.HashMap;
import java.util.Map;

public class RouteTable {

	private Map<String,RouteHandler> _routes = new HashMap<>();

	public void map( final String routeString, final RouteHandler routeHandler ) {
		_routes.put( routeString, routeHandler );
	}
}
package is.rebbi.wo.urls;

import com.webobjects.appserver.WOContext;
import com.webobjects.foundation.NSMutableDictionary;

import er.extensions.components.ERXComponent;

/**
 * Handle some standard system URLs
 */

public class USStaticURLs {

	private static NSMutableDictionary<String, URLMaker> _urlMakers;

	private static NSMutableDictionary<String, ? extends URLMaker> urlMakers() {
		if( _urlMakers == null ) {
			_urlMakers = new NSMutableDictionary<>();
			register( "/staging", new DirectActionURLMaker( "InspectAction" ) );
			register( "/login", new DirectActionURLMaker( "SWDirectAction/login" ) );
			register( "/kerfi", new DirectActionURLMaker( "SWDirectAction/login" ) );
			register( "/apidoc", new DirectActionURLMaker( "USController/apidoc" ) );
		}

		return _urlMakers;
	}

	private static void register( String url, URLMaker urlMaker ) {
		_urlMakers.put( url, urlMaker );
	}

	public static String url( String url, WOContext context ) {

		URLMaker urlMaker = urlMakers().objectForKey( url );

		if( urlMaker == null ) {
			return null;
		}

		return urlMaker.url( context );
	}

	private static interface URLMaker {
		public String url( WOContext context );
	}

	private static class DirectActionURLMaker implements URLMaker {

		private String _directActionName;

		public DirectActionURLMaker( String directActionName ) {
			_directActionName = directActionName;
		}

		@Override
		public String url( WOContext context ) {
			return context.directActionURLForActionNamed( _directActionName, null );
		}
	}

	private static class ComponentURLMaker implements URLMaker {

		private Class<? extends ERXComponent> _componentClass;

		public ComponentURLMaker( Class<? extends ERXComponent> componentClass ) {
			_componentClass = componentClass;
		}

		@Override
		public String url( WOContext context ) {
			//			return ERXApplication.erxApplication().pageWithName( _componentClass ).context().
			return null;
		}
	}
}
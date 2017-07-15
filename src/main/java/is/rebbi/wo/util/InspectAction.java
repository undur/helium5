package is.rebbi.wo.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WORequest;
import com.webobjects.foundation.NSDictionary;

import er.extensions.appserver.ERXDirectAction;
import is.rebbi.wo.cayenne.USCayenne;
import is.rebbi.wo.components.USListPageView;
import is.rebbi.wo.components.admin.USLoginPage;
import is.rebbi.wo.definitions.EntityViewDefinition;
import is.rebbi.wo.search.USSearchAction;
import is.rebbi.wo.urls.URLProviderCayenne;
import is.rebbi.wo.urls.USStaticURLs;

/**
 * Main entry point into the system.
 */

public class InspectAction extends ERXDirectAction {

	private static final Logger logger = LoggerFactory.getLogger( InspectAction.class );

	private static final String INSPECTION_PREFIX = "/i/";
	private static final String LIST_PREFIX = "/l/";
	private static final String SEARCH_PREFIX = "/search/";
	public static final String PASSWORD_RESET_REQUEST_PREFIX = "/passwordResetRequest/";

	public InspectAction( WORequest r ) {
		super( r );
	}

	/**
	 * @return A page for inspecting the specified object.
	 */
	public WOActionResults handlerAction() {
		String url = url();

		logger.info( "Handling URL: {}", url );

		String redirectURL = USStaticURLs.url( url, context() );

		if( redirectURL != null ) {
			return USHTTPUtilities.redirectTemporary( redirectURL );
		}

		if( url.startsWith( INSPECTION_PREFIX ) ) {
			EntityViewDefinition def = URLProviderCayenne.viewDefinitionFromURL( url );

			Object object = URLProviderCayenne.objectFromURL( USCayenne.defaultObjectContext( session() ), url );

			if( object == null ) {
				return response404();
			}

			return Inspection.inspectObjectInContext( object, context() );
		}

		if( url.startsWith( LIST_PREFIX ) ) {
			String entityIdentifier = url.substring( LIST_PREFIX.length() );

			EntityViewDefinition t = null;

			if( entityIdentifier.startsWith( "entity-" ) ) {
				entityIdentifier = entityIdentifier.substring( "entity-".length(), entityIdentifier.length() );
				t = EntityViewDefinition.get( entityIdentifier );
			}
			else {
				t = EntityViewDefinition.definitionForURLPrefix( entityIdentifier );
			}

			USListPageView nextPage = pageWithName( USListPageView.class );
			nextPage.setSelectedViewDefinition( t );
			return nextPage;
		}

		if( url.startsWith( SEARCH_PREFIX ) ) {
			String afterPrefix = url.substring( SEARCH_PREFIX.length() );
			logger.info( "searchString: " + afterPrefix );
			String directActionName = USSearchAction.class.getSimpleName() + "/search";
			NSDictionary<String, Object> params = new NSDictionary<>( afterPrefix, "searchString_field" );
			String searchURL = context().directActionURLForActionNamed( directActionName, params );
			return USHTTPUtilities.redirectTemporary( searchURL );
		}

		if( url.startsWith( PASSWORD_RESET_REQUEST_PREFIX ) ) {
			String afterPrefix = url.substring( PASSWORD_RESET_REQUEST_PREFIX.length() );
			NSDictionary<String, Object> params = new NSDictionary<>( afterPrefix, "key" );
			String searchURL = context().directActionURLForActionNamed( "SWPasswordResetAction" /* FIXME: SWPasswordResetAction.class.getSimpleName() */, params );
			return USHTTPUtilities.redirectTemporary( searchURL );
		}

		return response404();
	}

	/**
	 * @return The requested URL, either from a URL parameter or Apache's 404 handler
	 */
	protected String url() {
		String url = request().stringFormValueForKey( "url" );

		if( url == null ) {
			url = USHTTPUtilities.redirectURL( request() );
		}

		return url;
	}

	private WOActionResults response404() {
		return USHTTPUtilities.statusResponse( 404, "Nothing found at: " + url() );
	}

	public WOActionResults loginAction() {
		return pageWithName( USLoginPage.class );
	}
}
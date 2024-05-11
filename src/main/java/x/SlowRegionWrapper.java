package x;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.webobjects.appserver.WOApplication;
import com.webobjects.appserver.WOAssociation;
import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WOElement;
import com.webobjects.appserver.WORequest;
import com.webobjects.appserver.WORequestHandler;
import com.webobjects.appserver.WOResponse;
import com.webobjects.appserver._private.WODynamicGroup;
import com.webobjects.foundation.NSDictionary;
import com.webobjects.foundation.NSNotification;
import com.webobjects.foundation.NSNotificationCenter;
import com.webobjects.foundation.NSSelector;

import er.extensions.appserver.ERXWOContext;
import er.extensions.foundation.ERXUtilities;

/**
 * A dynamic element that allows you to wrap segments of a web page
 * with <wo:SlowRegionWrapper>[something slow]</wo:SlowRegionWrapper>.
 *
 * Rendering of the wrapped content/regions will get deferred, executed
 * concurrently, and the content shown in the UI once rendering is complete
 *
 * TODO: Create a separate element "SlowRegionPlaceholder" that can be embedded within this one
 * 			for content to be displayed while content is being fetched on the client side.
 *
 * TODO: Allow the [serverSide] binding to take three values; yes/no/auto.
 * 			"auto" meaning that if the future is done rendering when the response is returned,
 * 			just append the content on the server side and skip the script stuff.
 *
 * TODO: We could add to that by adding a threshold, for example you might want to allow waiting for
 * 			two seconds to append on the serverSide, if the Future is still being rendered
 * 			when [threshold] time expires, we skip to client side rendering.
 */

public class SlowRegionWrapper extends WODynamicGroup {

	/**
	 * Container class for regions we're going to render.
	 */
	public record SlowRegion( boolean isServerSide, Future<WOResponse> responseFuture ) {};

	/**
	 * Element name of the wrapper element. Defaults to 'div'
	 */
	private final WOAssociation _elementNameAssociation;

	/**
	 * Indicate whether the rendered content should be appended on the server side, or on the client side (using JS)
	 */
	private final WOAssociation _serverSideAssociation;

	/**
	 * Additional associations, stored for adding as attributes to the wrapper element's tag
	 */
	private final Map<String, WOAssociation> _associations;

	/**
	 * Executor for processing the slow regions
	 */
	private static final ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

	public SlowRegionWrapper( String name, NSDictionary<String, WOAssociation> associations, WOElement template ) {
		super( name, associations, template );
		_associations = associations;
		_serverSideAssociation = _associations.remove( "serverSide" );
		_elementNameAssociation = _associations.remove( "elementName" );
	}

	@Override
	public void appendToResponse( final WOResponse originalResponse, final WOContext originalContext ) {

		// Grab hold of the current elementID before we start fudging with everything
		final String currentElementID = originalContext.elementID();

		// Clone the context (contexts are very stateful and don't like to be used concurrently. At. All)
		final WOContext contextClone = (WOContext)originalContext.clone();

		// Cloning apparently doesn't copy over the current component
		contextClone._setCurrentComponent( originalContext.component() );

		// We're going to have to use the same contextID as the original for proper construction of component URLs.
		// Lord only knows how immensely we're fudging with the WO framework's mind by doing this.
		setPrivateField( contextClone, "_contextID", originalContext.contextID() );

		// The key we use to store the rendered sub-response for later retrieval by the request handler
		// We're lazily using a UUID to ensure uniqueness between pages/sessions, should probably use a combination of sessionID/contextID instead
		final String subResponseStorageKey = currentElementID + UUID.randomUUID();

		// Start processing our "subtemplate" and stash it as a Future<WOResponse> for later retrieval by the slow region request handler.
		SlowRegionRequestHandler.slowResponses.put(
				subResponseStorageKey,
				new SlowRegion( serverSide( originalContext ),
						executor.submit( () -> {
							// Ensure thread storage is properly storing our cloned context
							ERXWOContext.setCurrentContext( contextClone );

							// Create a response to store our results
							// We're working from/cloning the actual current response to ensure we have the same headers etc.
							final WOResponse subResponse = (WOResponse)originalResponse.clone();

							// Wipe the new response clean for rendering our subtemplate
							subResponse.setContent( "" );

							super.appendToResponse( subResponse, contextClone );

							return subResponse;
						} ) ) );

		if( !serverSide( originalContext ) ) {
			// Finally, append our "placeholder" element's HTML (and JS to load the "subtemplate") to the main template
			String elementName;

			if( _elementNameAssociation != null ) {
				elementName = (String)_elementNameAssociation.valueInComponent( originalContext.component() );
			}
			else {
				elementName = "div";
			}

			final String elementID = "slow_" + currentElementID.replace( '.', '_' );

			final Map<String, Object> attributes = new HashMap<>();
			attributes.put( "id", elementID );

			_associations.forEach( ( bindingName, association ) -> {
				attributes.put( bindingName, association.valueInComponent( originalContext.component() ) );
			} );

			final StringBuilder attributesString = new StringBuilder();

			for( Entry<String, Object> entry : attributes.entrySet() ) {
				attributesString.append( " %s=\"%s\"".formatted( entry.getKey(), entry.getValue() ) );
			}

			// Append the "placeholder element"
			originalResponse.appendContentString( "<%s%s></%s>\n".formatted( elementName, attributesString, elementName ) );

			// Append the "loading script"
			final String url = originalContext.urlWithRequestHandlerKey( SlowRegionRequestHandler.REQUEST_HANDLER_KEY, subResponseStorageKey, null );
			final String xhttpVarName = "xhttp_" + elementID;

			String scriptString = """
					<script>
						const ${xhttpVarName} = new XMLHttpRequest();
						${xhttpVarName}.open("GET", "${url}", true);
						${xhttpVarName}.onload = (e) => {
							document.getElementById("${elementID}").innerHTML = ${xhttpVarName}.responseText;
						}
						${xhttpVarName}.send();
					</script>
					""";

			scriptString = scriptString.replace( "${url}", url );
			scriptString = scriptString.replace( "${xhttpVarName}", xhttpVarName );
			scriptString = scriptString.replace( "${elementID}", elementID );

			originalResponse.appendContentString( scriptString );
		}
		else {
			originalResponse.appendContentString( subResponseStorageKey );
			SlowRegion slowRegion = SlowRegionRequestHandler.slowResponses.remove( subResponseStorageKey );
			addSlowRegion( originalResponse, subResponseStorageKey, slowRegion );
		}
	}

	/**
	 * @return true if the rendered region should be appended to the response on the server side (at the end of the R-R loop)
	 */
	private boolean serverSide( final WOContext context ) {
		if( _serverSideAssociation == null ) {
			return false;
		}

		return _serverSideAssociation.booleanValueInComponent( context.component() );
	}

	/**
	 * Add a region intended to be appended on the server side to the given WOResponse
	 */
	private static void addSlowRegion( WOResponse response, String elementID, SlowRegion slowRegion ) {
		Map<String, SlowRegion> slowRegions = slowRegions( response );
		slowRegions.put( elementID, slowRegion );
	}

	/**
	 * Regions of the WOResponse intended to be appended on the server side
	 */
	private static Map<String, SlowRegion> slowRegions( WOResponse response ) {
		Map<String, SlowRegion> slowRegions = (Map<String, SlowRegion>)response.userInfoForKey( "slowRegions" );

		if( slowRegions == null ) {
			slowRegions = new ConcurrentHashMap<>();
			response.setUserInfoForKey( slowRegions, "slowRegions" );
		}

		return slowRegions;
	}

	/**
	 * Implemented only to set contextID
	 */
	private static void setPrivateField( Object object, String fieldName, Object value ) {
		try {
			Field field = WOContext.class.getDeclaredField( fieldName );
			field.setAccessible( true );
			field.set( object, value );
		}
		catch( NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e ) {
			throw new RuntimeException( "If this exception is thrown, you deserve it.", e );
		}
	}

	/**
	 * Container object for a single method that appends server side regions to a response at the end of the R-R loop
	 */
	public static class ResponseRewriter {
		public void applicationDidHandleRequest( NSNotification n ) {

			final WOResponse response = (WOResponse)n.object();

			for( Entry<String, SlowRegion> entry : slowRegions( response ).entrySet() ) {
				try {
					final SlowRegion region = entry.getValue();
					final String responseKey = entry.getKey();
					response.setContent( response.contentString().replace( responseKey, region.responseFuture().get().contentString() ) );
				}
				catch( InterruptedException | ExecutionException e ) {
					throw new RuntimeException( e );
				}
			}
		}
	}

	public static ResponseRewriter rewriterInstance = new ResponseRewriter();

	/**
	 * Just a shortcut to register the request handler with the application
	 */
	public static void register() {
		// For client side rendering of slow responses
		WOApplication.application().registerRequestHandler( new SlowRegionWrapper.SlowRegionRequestHandler(), SlowRegionRequestHandler.REQUEST_HANDLER_KEY );

		// For server side rendering of slow responses
		final NSSelector<Void> selector = ERXUtilities.notificationSelector( "applicationDidHandleRequest" );
		NSNotificationCenter.defaultCenter().addObserver( rewriterInstance, selector, WOApplication.ApplicationDidDispatchRequestNotification, null );
	}

	public static class SlowRegionRequestHandler extends WORequestHandler {

		private static final String REQUEST_HANDLER_KEY = "slow-region";

		public static final Map<String, SlowRegion> slowResponses = new ConcurrentHashMap<>();

		@Override
		public WOResponse handleRequest( WORequest request ) {
			final String elementID = request._uriDecomposed().requestHandlerPath();

			try {
				return slowResponses.remove( elementID ).responseFuture().get();
			}
			catch( InterruptedException | ExecutionException e ) {
				throw new RuntimeException( e );
			}
		}
	}
}
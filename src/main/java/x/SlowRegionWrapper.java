package x;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
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

public class SlowRegionWrapper extends WODynamicGroup {

	/**
	 * Element name of the wrapper displayed (defaults to 'div')
	 */
	private final WOAssociation _elementNameAssociation;

	/**
	 * Store the additional associations to add to the wrapper tag
	 */
	private final Map<String, WOAssociation> _associations;

	/**
	 * Created to process all our slow regions
	 */
	private static final ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

	public SlowRegionWrapper( String name, NSDictionary<String, WOAssociation> associations, WOElement template ) {
		super( name, associations, template );
		_associations = associations;
		_elementNameAssociation = _associations.remove( "elementName" );
	}

	/**
	 * Just a quick way to register the request handler with the application
	 */
	public static void register() {
		WOApplication.application().registerRequestHandler( new SlowRegionWrapper.SlowRegionRequestHandler(), SlowRegionRequestHandler.REQUEST_HANDLER_KEY );
	}

	@Override
	public void appendToResponse( WOResponse response, WOContext context ) {

		// Grab hold of the current elementID before we start fudging with everything
		final String currentElementID = context.elementID();

		// Clone the context (contexts are very stateful and don't like to be used concurrently. At. All)
		final WOContext contextClone = (WOContext)context.clone();

		// Cloning apparently doesn't copy over the current component
		contextClone._setCurrentComponent( context.component() );

		// We're going to have to use the same contextID as the original for proper construction of component URLs.
		// Lord only knows how immensely we're fudging with the framework's mind by doing this.
		try {
			Field field = WOContext.class.getDeclaredField( "_contextID" );
			field.setAccessible( true );
			field.set( contextClone, context.contextID() );
		}
		catch( NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e ) {
			throw new RuntimeException( "If this exception is thrown, you deserve it.", e );
		}

		// Start processing our "subtemplate" and stash it as a Future<WOResponse> for later retrieval by the slow region request handler.
		SlowRegionRequestHandler.responses.put(
				currentElementID,
				executor.submit( () -> {
					final WOResponse responseClone = (WOResponse)response.clone();
					responseClone.setContent( "" );
					super.appendToResponse( responseClone, contextClone );
					return responseClone;
				} ) );

		String elementName;

		if( _elementNameAssociation != null ) {
			elementName = (String)_elementNameAssociation.valueInComponent( context.component() );
		}
		else {
			elementName = "div";
		}

		final String elementID = "slow_" + currentElementID.replace( '.', '_' );

		final Map<String, Object> attributes = new HashMap<>();
		attributes.put( "id", elementID );

		_associations.forEach( ( bindingName, association ) -> {
			attributes.put( bindingName, association.valueInComponent( context.component() ) );
		} );

		final StringBuilder attributesString = new StringBuilder();

		for( Entry<String, Object> entry : attributes.entrySet() ) {
			attributesString.append( "%s=\"%s\"".formatted( entry.getKey(), entry.getValue() ) );
		}

		final String uri = context.urlWithRequestHandlerKey( SlowRegionRequestHandler.REQUEST_HANDLER_KEY, currentElementID, null );
		final String uriJSVariableName = "uri_" + elementID;
		final String xhttpJSVariableName = "xhttp_" + elementID;

		response.appendContentString( "<%s %s></%s>".formatted( elementName, attributesString, elementName ) );
		response.appendContentString( "\n" );
		response.appendContentString( "<script>\n" );
		response.appendContentString( "var %s = \"%s\";\n".formatted( uriJSVariableName, uri ) );
		response.appendContentString( "const %s = new XMLHttpRequest();\n".formatted( xhttpJSVariableName ) );
		response.appendContentString( "%s.open(\"GET\", %s, true);\n".formatted( xhttpJSVariableName, uriJSVariableName ) );
		//		response.appendContentString( "\n" );
		//		response.appendContentString( "console.log( \"Requested URL: \" + url );" );
		//		response.appendContentString( "\n" );
		//		response.appendContentString( "console.log( \"Received content: \" + xhttp.responseText )" );
		response.appendContentString( "%s.onload = (e) => {\n".formatted( xhttpJSVariableName ) );
		response.appendContentString( "document.getElementById('%s').innerHTML = %s.responseText;\n".formatted( elementID, xhttpJSVariableName ) );
		response.appendContentString( "}\n" );
		response.appendContentString( "%s.send();\n".formatted( xhttpJSVariableName ) );
		response.appendContentString( "</script>\n" );
	}

	public static class SlowRegionRequestHandler extends WORequestHandler {

		private static final String REQUEST_HANDLER_KEY = "slow-region";

		public static final Map<String, Future<WOResponse>> responses = new ConcurrentHashMap<>();

		@Override
		public WOResponse handleRequest( WORequest request ) {
			final String elementID = request._uriDecomposed().requestHandlerPath();

			try {
				return responses.remove( elementID ).get();
			}
			catch( InterruptedException | ExecutionException e ) {
				throw new RuntimeException( e );
			}
		}
	}
}
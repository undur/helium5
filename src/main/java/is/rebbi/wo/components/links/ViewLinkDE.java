package is.rebbi.wo.components.links;

import com.webobjects.appserver.WOAssociation;
import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WOElement;
import com.webobjects.appserver.WOResponse;
import com.webobjects.appserver._private.WODynamicGroup;
import com.webobjects.foundation.NSDictionary;

import is.rebbi.wo.urls.USURLProvider;

/**
 * Work in progress to convert the ViewLink to a Dynamic Element. Could significantly enhance performance for large components 
 */

public class ViewLinkDE extends WODynamicGroup {

	private final WOAssociation _objectAssociation;
	private final WOAssociation _disabledAssociation;

	public ViewLinkDE( String aName, NSDictionary<String, WOAssociation> associations, WOElement template ) {
		super( aName, associations, template );
		_objectAssociation = associations.objectForKey( "object" );
		
		if( _objectAssociation == null ) {
			throw new IllegalArgumentException( "[object] is a required binding" );
		}

		_disabledAssociation = associations.objectForKey( "disabled" );
	}
	
	@Override
	public void appendToResponse( final WOResponse response, final WOContext context ) {
		final Object object = _objectAssociation.valueInComponent( context.component() );
		final boolean disabled = _disabledAssociation != null && _disabledAssociation.booleanValueInComponent( context.component() );

		final String url = USURLProvider.urlForObjectInContext( object, context );

		if( !disabled ) {
			response.appendContentString( "<a href=\"" );
			response.appendContentString( url );
			response.appendContentString( "\">" );
		}

		appendChildrenToResponse(response, context);
		
		if( !disabled ) {
			response.appendContentString( "</a>" );
		}
	}
}
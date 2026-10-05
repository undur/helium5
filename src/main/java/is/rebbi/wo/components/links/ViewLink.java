package is.rebbi.wo.components.links;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.cayenne.Persistent;

import com.webobjects.appserver.WOAssociation;
import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WOElement;
import com.webobjects.appserver.WOResponse;
import com.webobjects.appserver._private.WODynamicElementCreationException;
import com.webobjects.appserver._private.WODynamicGroup;
import com.webobjects.foundation.NSDictionary;

import er.extensions.foundation.ERXValueUtilities;
import is.rebbi.wo.objectroutes.urls.URLProviders;

/**
 * A link to an object's page: {@code <wo:ViewLink object="$book">…</wo:ViewLink>}.
 *
 * Disabled when there's no object, when it's an object not yet committed (it has no URL), or when {@code disabled} is
 * true: its content alone, or with {@code class} bound, its content in a span of that class and {@code disabled}.
 *
 * A dynamic element, so a page listing thousands of objects makes no component for each link.
 */

public class ViewLink extends WODynamicGroup {

	private final WOAssociation _object;
	private final WOAssociation _disabled;

	/**
	 * Every other binding, rendered as an attribute of the tag (class, id, style, target, title, data-…)
	 */
	private final Map<String, WOAssociation> _attributes = new LinkedHashMap<>();

	public ViewLink( final String name, final NSDictionary<String, WOAssociation> associations, final WOElement template ) {
		super( name, null, template );

		_object = associations.objectForKey( "object" );

		if( _object == null ) {
			throw new WODynamicElementCreationException( "<ViewLink> 'object' is a required binding" );
		}

		_disabled = associations.objectForKey( "disabled" );

		for( final String key : associations.allKeys() ) {
			if( !key.equals( "object" ) && !key.equals( "disabled" ) ) {
				_attributes.put( key, associations.objectForKey( key ) );
			}
		}
	}

	@Override
	public void appendToResponse( final WOResponse response, final WOContext context ) {
		final WOComponent component = context.component();
		final Object object = _object.valueInComponent( component );

		if( isDisabled( object, component ) ) {
			final WOAssociation cssClass = _attributes.get( "class" );

			// Disabled with a class: the content in a span the page can style as disabled
			if( cssClass != null && cssClass.valueInComponent( component ) != null ) {
				response.appendContentString( "<span" );
				appendAttributes( response, component, " disabled" );
				response.appendContentString( ">" );
				appendChildrenToResponse( response, context );
				response.appendContentString( "</span>" );
			}
			else {
				appendChildrenToResponse( response, context );
			}

			return;
		}

		response.appendContentString( "<a" );
		appendAttributes( response, component, "" );
		appendAttribute( response, "href", URLProviders.urlForObject( object ) );
		response.appendContentString( ">" );
		appendChildrenToResponse( response, context );
		response.appendContentString( "</a>" );
	}

	/**
	 * Appends the attributes the element was given, the class with the suffix added
	 */
	private void appendAttributes( final WOResponse response, final WOComponent component, final String classSuffix ) {
		for( final Map.Entry<String, WOAssociation> attribute : _attributes.entrySet() ) {
			final Object value = attribute.getValue().valueInComponent( component );
			appendAttribute( response, attribute.getKey(), value != null && attribute.getKey().equals( "class" ) ? value + classSuffix : value );
		}
	}

	/**
	 * @return true if the link can't go anywhere: no object, one not yet committed (no URL), or disabled by its binding
	 */
	private boolean isDisabled( final Object object, final WOComponent component ) {

		if( object == null ) {
			return true;
		}

		// Objects that haven't been committed to the DB have no URL
		if( object instanceof Persistent p && p.getObjectId().isTemporary() ) {
			return true;
		}

		return _disabled != null && ERXValueUtilities.booleanValue( _disabled.valueInComponent( component ) );
	}

	/**
	 * Appends an attribute, its value escaped, unless the value is null
	 */
	private static void appendAttribute( final WOResponse response, final String name, final Object value ) {
		if( value != null ) {
			response.appendContentString( " " );
			response.appendContentString( name );
			response.appendContentString( "=\"" );
			response.appendContentHTMLAttributeValue( value.toString() );
			response.appendContentString( "\"" );
		}
	}
}

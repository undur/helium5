package x;

import com.webobjects.appserver.WOAssociation;
import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WOElement;
import com.webobjects.appserver.WOResponse;
import com.webobjects.appserver._private.WODynamicGroup;
import com.webobjects.foundation.NSDictionary;

/**
 * Simply serves as a region to contain placeholder content for client-side rendered regions
 */

public class SlowRegionPlaceholder extends WODynamicGroup {

	public SlowRegionPlaceholder( String aName, NSDictionary<String, WOAssociation> someAssociations, WOElement template ) {
		super( aName, someAssociations, template );
	}

	/**
	 * Overridden to ensure we're not rendering the placeholder's content when returning the rendered response
	 */
	@Override
	public void appendToResponse( WOResponse aResponse, WOContext aContext ) {
		if( !"true".equals( aContext.userInfoForKey( "isSlowResponse" ) ) ) {
			super.appendToResponse( aResponse, aContext );
		}
	}
}
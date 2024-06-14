package is.rebbi.wo.interfaces;

import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WOContext;

public interface HasSelectedObjectPage<E> {

	public default E selectedObject() {
		return (E)context().userInfo().get( "selectedObject" );
	}

	public default void setSelectedObject( E value ) {
		if( value == null ) {
			context().userInfo().remove( "selectedObject" );
		}
		else {
			context().userInfo().put( "selectedObject", value );
		}
	}

	public default WOComponent callingComponent() {
		return (WOComponent)context().userInfo().get( "callingComponent" );
	}

	public default void setCallingComponent( WOComponent value ) {
		if( value == null ) {
			context().userInfo().remove( "callingComponent" );
		}
		else {
			context().userInfo().put( "callingComponent", value );
		}
	}

	public WOContext context();
}
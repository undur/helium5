package is.rebbi.wo.menu;

import org.apache.cayenne.DataObject;

import com.webobjects.appserver.WOActionResults;

import er.extensions.appserver.ERXWOContext;
import is.rebbi.wo.interfaces.HasSelectedObjectPage;
import is.rebbi.wo.urls.USURLProvider;
import is.rebbi.wo.util.Inspection;

public class USMenuItemViewObject extends USMenuItem {

	private DataObject _object;
	private Class<? extends HasSelectedObjectPage> _viewComponentClass;

	public DataObject object() {
		return _object;
	}

	public void setObject( DataObject newObject ) {
		_object = newObject;
	}

	public static USMenuItemViewObject create( String name, String iconClasses, DataObject object ) {
		return create( name, iconClasses, object, null );
	}

	public static USMenuItemViewObject create( String name, String iconClasses, DataObject object, Class<? extends HasSelectedObjectPage> viewComponentClass ) {
		USMenuItemViewObject item = new USMenuItemViewObject();
		item.setName( name );
		item.setIconClasses( iconClasses );
		item.setObject( object );
		item._viewComponentClass = viewComponentClass;
		return item;
	}

	/**
	 * FIXME: This should be removed once we are using theis to invoke operations and operations have routes.
	 */
	@Override
	public WOActionResults action() {
		if( _viewComponentClass != null ) {
			return Inspection.inspectObjectInContextUsingComponent( object(), ERXWOContext.currentContext(), _viewComponentClass );
		}

		return Inspection.inspectObjectInContext( object(), ERXWOContext.currentContext() );
	}

	@Override
	public String url() {
		if( _viewComponentClass != null ) {
			return null; // FIXME: FIX to allow custom view components.
		}

		return USURLProvider.urlForObjectInContext( object(), ERXWOContext.currentContext() );
	}
}
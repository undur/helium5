package is.rebbi.wo.menu;

import org.apache.cayenne.DataObject;

import com.webobjects.appserver.WOActionResults;

import er.extensions.appserver.ERXWOContext;
import is.rebbi.wo.util.Inspection;

public class USMenuItemViewObject extends USMenuItem {

	private DataObject _object;

	public DataObject object() {
		return _object;
	}

	public void setObject( DataObject newObject ) {
		_object = newObject;
	}

	public static USMenuItemViewObject create( String name, String iconClasses, DataObject object ) {
		USMenuItemViewObject item = new USMenuItemViewObject();
		item.setName( name );
		item.setIconClasses( iconClasses );
		item.setObject( object );
		return item;
	}

	@Override
	public WOActionResults action() {
		return Inspection.inspectObjectInContext( object(), ERXWOContext.currentContext() );
	}
}
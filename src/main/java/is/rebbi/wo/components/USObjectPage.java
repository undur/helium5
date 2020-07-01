package is.rebbi.wo.components;

import com.webobjects.appserver.WOContext;

import er.extensions.components.ERXComponent;
import is.rebbi.wo.interfaces.HasSelectedObjectPage;

public class USObjectPage<E> extends ERXComponent implements HasSelectedObjectPage<E> {

	private E _selectedObject;

	public USObjectPage( WOContext context ) {
		super( context );
	}

	@Override
	public E selectedObject() {
		return _selectedObject;
	}

	public void setSelectedObject( E value ) {
		_selectedObject = value;
	}
}
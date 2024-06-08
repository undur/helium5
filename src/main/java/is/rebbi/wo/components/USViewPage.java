package is.rebbi.wo.components;

import org.apache.cayenne.DataObject;
import org.apache.cayenne.ObjectContext;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WOContext;

import is.rebbi.wo.interfaces.HasSelectedObjectPage;
import jambalaya.Jambalaya;
import jambalaya.definitions.EntityDefinition;

/**
 * Common functionality for client and admin side components.
 */

public abstract class USViewPage<E> extends USBaseComponent implements HasSelectedObjectPage<E> {

	/**
	 * The component to return to in case of action cancellation.
	 */
	private WOComponent _componentToReturnTo;

	/**
	 * Currently selected object.
	 */
	private E _selectedObject;

	private ObjectContext _oc;

	public USViewPage( WOContext context ) {
		super( context );
	}

	public EntityDefinition viewDefinition() {
		return EntityDefinition.get( selectedObject().getClass() );
	}

	public WOComponent callingComponent() {
		return _componentToReturnTo;
	}

	public void setCallingComponent( WOComponent value ) {
		_componentToReturnTo = value;
	}

	public WOActionResults saveChangesAndReturn() {
		saveChanges();
		return returnToCallingComponent();
	}

	public WOActionResults saveChanges() {
		((DataObject)selectedObject()).getObjectContext().commitChanges();
		return null;
	}

	public WOActionResults deleteObject() {
		((DataObject)selectedObject()).getObjectContext().deleteObject( selectedObject() );
		saveChanges();
		return returnToCallingComponent();
	}

	/**
	 * @return The component instance that invoked this component.
	 */
	public WOActionResults returnToCallingComponent() {
		callingComponent().ensureAwakeInContext( context() );
		return callingComponent();
	}

	protected ObjectContext oc() {
		if( _oc == null ) {
			if( selectedObject() != null ) {
				_oc = ((DataObject)selectedObject()).getObjectContext();
			}
			else {
				_oc = Jambalaya.newContext();
			}
		}

		return _oc;
	}

	@Override
	public E selectedObject() {
		E selectedObjectFromBinding = (E)valueForBinding( "selectedObject" );

		if( selectedObjectFromBinding != null && !selectedObjectFromBinding.equals( _selectedObject ) ) {
			_selectedObject = selectedObjectFromBinding;
		}

		return _selectedObject;
	}

	public void setSelectedObject( E value ) {
		_selectedObject = value;
	}
}
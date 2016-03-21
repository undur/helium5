package is.rebbi.wo.components;

import org.apache.cayenne.DataObject;
import org.apache.cayenne.ObjectContext;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WOContext;
import com.webobjects.eocontrol.EOEditingContext;

import er.extensions.eof.ERXGenericRecord;
import is.rebbi.wo.cayenne.USCayenne;
import is.rebbi.wo.definitions.EntityViewDefinition;
import is.rebbi.wo.interfaces.HasSelectedObjectPage;
import is.rebbi.wo.util.USEOUtilities;

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

	private EOEditingContext _ec;

	private ObjectContext _oc;

	public USViewPage( WOContext context ) {
		super( context );
	}

	public EntityViewDefinition viewDefinition() {
		return EntityViewDefinition.get( selectedObject().getClass() );
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
		if( isCayenne() ) {
			oc().commitChanges();
		}
		else {
			ec().saveChanges();

			if( USEOUtilities.isNested( ec() ) ) {
				EOEditingContext parent = (EOEditingContext)ec().parentObjectStore();
				parent.saveChanges();
			}
		}

		return null;
	}

	public WOActionResults deleteObject() {
		if( isCayenne() ) {
			oc().deleteObject( selectedObject() );
		}
		else {
			ec().deleteObject( ((ERXGenericRecord)selectedObject()) );
		}

		saveChanges();
		return returnToCallingComponent();
	}

	public boolean isCayenne() {
		return viewDefinition().isCayenneEntity();
	}

	/**
	 * @return The component instance that invoked this component.
	 */
	public WOActionResults returnToCallingComponent() {
		callingComponent().ensureAwakeInContext( context() );
		return callingComponent();
	}

	protected EOEditingContext ec() {
		if( _ec == null ) {
			if( selectedObject() != null ) {
				_ec = ((ERXGenericRecord)selectedObject()).editingContext();
			}
			else {
				_ec = session().defaultEditingContext();
			}
		}

		return _ec;
	}

	protected ObjectContext oc() {
		if( _oc == null ) {
			if( selectedObject() != null ) {
				_oc = ((DataObject)selectedObject()).getObjectContext();
			}
			else {
				_oc = USCayenne.newContext();
			}
		}

		return _oc;
	}

	@Override
	public E selectedObject() {
		E newSelectedObject = (E)valueForBinding( "selectedObject" );

		if( newSelectedObject != null && !newSelectedObject.equals( _selectedObject ) ) {
			_selectedObject = newSelectedObject;
		}

		return _selectedObject;
	}

	public void setSelectedObject( E value ) {
		_selectedObject = value;
	}
}
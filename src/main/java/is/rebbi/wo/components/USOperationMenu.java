package is.rebbi.wo.components;

import java.util.List;

import org.apache.cayenne.PersistentObject;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WOContext;

import er.extensions.components.ERXNonSynchronizingComponent;
import is.rebbi.wo.operations.DataObjectOperation;
import is.rebbi.wo.operations.DataObjectOperations;

public class USOperationMenu extends ERXNonSynchronizingComponent {

	public DataObjectOperation currentOperation;

	public USOperationMenu( WOContext context ) {
		super( context );
	}

	public PersistentObject selectedObject() {
		return (PersistentObject)valueForBinding( "selectedObject" );
	}

	public List<DataObjectOperation> operations() {
		return DataObjectOperations.operations();
	}

	public WOActionResults selectOperation() {
		return currentOperation.execute().apply( selectedObject(), context() );
	}

	public String currentOperationURL() {
		return currentOperation.urlFunction().apply( selectedObject(), context() );
	}

	public boolean showCurrentOperation() {
		return currentOperation.show().apply( selectedObject(), context() );
	}

	public String currentOperationClass() {
		return "glyphicon glyphicon-" + currentOperation.iconName();
	}
}
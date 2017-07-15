package is.rebbi.wo.operations;

import java.util.ArrayList;
import java.util.List;

public class DataObjectOperations {

	private static List<DataObjectOperation> _operations;

	public static List<DataObjectOperation> operations() {
		if( _operations == null ) {
			_operations = new ArrayList<>();
			_operations.add( new EditOperation() );
			_operations.add( new ViewOperation() );
			_operations.add( new EditGenericOperation() );
			_operations.add( new ViewGenericOperation() );
		}

		return _operations;
	}

	public static void addOperation( DataObjectOperation operation ) {
		operations().add( operation );
	}
}
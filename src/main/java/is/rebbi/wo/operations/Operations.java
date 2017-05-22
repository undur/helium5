package is.rebbi.wo.operations;

import java.util.ArrayList;
import java.util.List;

public class Operations {

	private static List<Operation> _operations;

	public static List<Operation> operations() {
		if( _operations == null ) {
			_operations = new ArrayList<>();
			_operations.add( new EditOperation() );
			_operations.add( new ViewOperation() );
			_operations.add( new EditGenericOperation() );
			_operations.add( new ViewGenericOperation() );
		}

		return _operations;
	}

	public static void addOperation( Operation operation ) {
		operations().add( operation );
	}
}
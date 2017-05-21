package is.rebbi.wo.operations;

import java.util.ArrayList;
import java.util.List;

public class Operations {

	private static List<Operation> _operations;

	public static List<Operation> operations() {
		if( _operations == null ) {
			_operations = new ArrayList<>();
			addOperation( new EditOperation() );
			addOperation( new GenericViewingOperation() );
		}

		return _operations;
	}

	private static void addOperation( Operation operation ) {
		operations().add( operation );
	}
}
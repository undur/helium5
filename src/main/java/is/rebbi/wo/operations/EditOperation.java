package is.rebbi.wo.operations;

import java.util.function.BiFunction;

import org.apache.cayenne.PersistentObject;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WOContext;

import is.rebbi.wo.objectroutes.Inspection;

public class EditOperation implements DataObjectOperation {

	@Override
	public String name() {
		return "Breyta";
	}

	@Override
	public String iconName() {
		return "pencil";
	}

	@Override
	public BiFunction<PersistentObject, WOContext, Boolean> show() {
		return ( object, context ) -> {
			return object != null;
		};
	}

	@Override
	public BiFunction<PersistentObject, WOContext, WOActionResults> execute() {
		return ( object, context ) -> {
			return Inspection.editObjectInContext( object, context );
		};
	}
}
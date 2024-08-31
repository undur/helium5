package is.rebbi.wo.operations;

import java.util.function.BiFunction;

import org.apache.cayenne.PersistentObject;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WOContext;

import is.rebbi.wo.util.Inspection;

public class EditGenericOperation implements DataObjectOperation {

	@Override
	public String name() {
		return "Breyta (almenn útgáfa)";
	}

	@Override
	public String iconName() {
		return "pencil";
	}

	@Override
	public BiFunction<PersistentObject, WOContext, WOActionResults> execute() {
		return ( object, context ) -> {
			return Inspection.editObjectInContextUsingGenericComponent( object, context );
		};
	}

	@Override
	public BiFunction<PersistentObject, WOContext, Boolean> show() {
		return ( object, context ) -> {
			return object != null;
		};
	}
}
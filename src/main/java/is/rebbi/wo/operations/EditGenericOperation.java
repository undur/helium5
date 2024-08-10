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
		return ( dataObject, context ) -> {
			return Inspection.editObjectInContextUsingGenericComponent( dataObject, context );
		};
	}

	@Override
	public BiFunction<PersistentObject, WOContext, Boolean> show() {
		return ( dataObject, context ) -> {
			return dataObject != null;
		};
	}
}
package is.rebbi.wo.operations;

import java.util.function.BiFunction;

import org.apache.cayenne.PersistentObject;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WOContext;

import is.rebbi.wo.util.Inspection;

public class ViewGenericOperation implements DataObjectOperation {

	@Override
	public String name() {
		return "Skoða (almenn útgáfa)";
	}

	@Override
	public String iconName() {
		return "eye-open";
	}

	@Override
	public BiFunction<PersistentObject, WOContext, WOActionResults> execute() {
		return ( object, context ) -> {
			return Inspection.inspectObjectInContextUsingGenericComponent( object, context );
		};
	}

	@Override
	public BiFunction<PersistentObject, WOContext, Boolean> show() {
		return ( object, context ) -> {
			return object != null;
		};
	}
}
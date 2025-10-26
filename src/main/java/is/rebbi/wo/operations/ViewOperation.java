package is.rebbi.wo.operations;

import java.util.function.BiFunction;

import org.apache.cayenne.PersistentObject;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WOContext;

import is.rebbi.wo.objectroutes.Inspection;
import is.rebbi.wo.urls.USURLProvider;

public class ViewOperation implements DataObjectOperation {

	@Override
	public String name() {
		return "Skoða";
	}

	@Override
	public String iconName() {
		return "eye-open";
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
			return Inspection.inspectObjectInContext( object, context );
		};
	}

	@Override
	public BiFunction<PersistentObject, WOContext, String> urlFunction() {
		return ( object, context ) -> {
			return USURLProvider.urlForObjectInContext( object, context );
		};
	}
}
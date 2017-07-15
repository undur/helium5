package is.rebbi.wo.operations;

import java.util.function.BiFunction;

import org.apache.cayenne.DataObject;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WOContext;

import is.rebbi.wo.urls.USURLProvider;
import is.rebbi.wo.util.Inspection;

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
	public BiFunction<DataObject, WOContext, WOActionResults> execute() {
		return ( dataObject, context ) -> {
			return Inspection.inspectObjectInContext( dataObject, context );
		};
	}

	@Override
	public BiFunction<DataObject, WOContext, Boolean> show() {
		return ( dataObject, context ) -> {
			return dataObject != null;
		};
	}

	@Override
	public BiFunction<DataObject, WOContext, String> urlFunction() {
		return ( dataObject, context ) -> {
			return USURLProvider.urlForObjectInContext( dataObject, context );
		};
	}
}
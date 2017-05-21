package is.rebbi.wo.operations;

import java.util.function.BiFunction;

import org.apache.cayenne.DataObject;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WOContext;

import is.rebbi.wo.util.Inspection;

public class EditOperation implements Operation {

	@Override
	public String name() {
		return "Opna í stjórnkerfi";
	}

	@Override
	public String iconName() {
		return "pencil";
	}

	@Override
	public BiFunction<DataObject, WOContext, WOActionResults> execute() {
		return ( dataObject, context ) -> {
			return Inspection.editObjectInContext( dataObject, context );
		};
	}

	@Override
	public BiFunction<DataObject, WOContext, Boolean> show() {
		return ( dataObject, context ) -> {
			return dataObject != null;
		};
	}
}
package is.rebbi.wo.components.admin;

import com.webobjects.appserver.WOContext;

import is.rebbi.wo.components.USViewPage;
import is.rebbi.wo.objectroutes.InspectionUtil;

public class USEditLook extends USViewPage {

	public USEditLook( WOContext context ) {
		super( context );
	}

	/**
	 * @return Name of WOComponent to wrap around content when editing objects.
	 */
	public String editLookName() {
		return InspectionUtil.editLookNameInContext( context() );
	}
}
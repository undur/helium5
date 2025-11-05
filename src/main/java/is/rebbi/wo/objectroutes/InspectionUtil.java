package is.rebbi.wo.objectroutes;

import com.webobjects.appserver.WOContext;

import is.rebbi.wo.components.USStandardLook;
import is.rebbi.wo.util.USSettings;

public class InspectionUtil {

	/**
	 * @return Name of WOComponent to wrap around content when editing objects.
	 */
	public static String editLookNameInContext( final WOContext context ) {
		String lookName = context.request().stringFormValueForKey( "look" );

		if( lookName != null ) {
			return lookName;
		}

		lookName = USSettings.defaultEditLookName();

		if( lookName == null ) {
			lookName = USStandardLook.class.getSimpleName();
		}

		return lookName;
	}

	/**
	 * @return Name of WOComponent to wrap around content when viewing objects.
	 */
	public static String lookNameInContext( final WOContext context ) {
		String lookName = context.request().stringFormValueForKey( "look" );

		if( lookName != null ) {
			return lookName;
		}

		lookName = USSettings.defaultLookName();

		if( lookName == null ) {
			lookName = USStandardLook.class.getSimpleName();
		}

		return lookName;
	}
}
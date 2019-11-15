package is.rebbi.wo.components.value;

import java.util.List;

import org.apache.cayenne.DataObject;

import com.webobjects.appserver.WOContext;
import com.webobjects.foundation.NSKeyValueCoding;
import com.webobjects.foundation.NSKeyValueCodingAdditions;

import er.extensions.components.ERXStatelessComponent;
import is.rebbi.core.humanreadable.HumanReadableUtils;
import is.rebbi.core.util.ListUtilities;

public class USHumanReadableArray extends ERXStatelessComponent {

	public int currentIndex;
	public Object currentObject;

	public USHumanReadableArray( WOContext context ) {
		super( context );
	}

	public String valueWhenEmpty() {
		return stringValueForBinding( "valueWhenEmpty" );
	}

	private String keyPath() {
		return (String)valueForBinding( "keyPath" );
	}

	private boolean forceLowercase() {
		return booleanValueForBinding( "forceLowercase" );
	}

	public List<?> objects() {
		return (List<?>)valueForBinding( "objects" );
	}

	public String lastSeparator() {
		String lastSeparator = stringValueForBinding( "lastSeparator" );

		if( lastSeparator == null ) {
			lastSeparator = "og";
		}

		return lastSeparator;
	}

	public boolean hasObjects() {
		return ListUtilities.hasObjects( objects() );
	}

	public Object currentString() {

		Object value = null;

		if( keyPath() != null ) {
			if( currentObject != null && !(currentObject instanceof NSKeyValueCoding.Null) ) {
				value = NSKeyValueCodingAdditions.Utility.valueForKeyPath( currentObject, keyPath() );
			}
		}
		else {
			value = HumanReadableUtils.toStringHuman( currentObject );
		}

		if( forceLowercase() && (value instanceof String) ) {
			if( value != null && currentIndex > 0 ) {
				value = ((String)value).toLowerCase();
			}
		}

		return value;
	}

	/**
	 * The separator shown between records
	 */
	public String separator() {
		if( currentIndex == objects().size() - 1 ) {
			return "";
		}

		if( currentIndex == objects().size() - 2 ) {
			return " " + lastSeparator() + " ";
		}

		return ", ";
	}

	public boolean isInspectable() {
		return currentObject instanceof DataObject;
	}
}
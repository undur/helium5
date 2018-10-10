package is.rebbi.wo.components.admin;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.Format;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.cayenne.DataObject;
import org.apache.cayenne.map.ObjAttribute;
import org.apache.cayenne.map.ObjEntity;
import org.apache.cayenne.map.ObjRelationship;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WOContext;
import com.webobjects.foundation.NSData;
import com.webobjects.foundation.NSKeyValueCoding;

import is.rebbi.wo.components.USViewPage;
import is.rebbi.wo.util.USHTTPUtilities;
import jambalaya.CayenneUtils;

/**
 * Generic object edit page.
 */

public class USEditPageGeneric<E extends DataObject> extends USViewPage<E> {

	public ObjAttribute currentAttribute;
	public ObjRelationship currentRelationship;
	public String filename;

	public USEditPageGeneric( WOContext context ) {
		super( context );
	}

	/**
	 * FIXME: Migrate to a utility class.
	 *
	 * @return A list of attributes
	 */
	public List<ObjAttribute> attributes() {
		ArrayList<ObjAttribute> attributes = new ArrayList<>( entity().getAttributes() );

		ObjAttribute modificationDate = entity().getAttribute( "modificationDate" );

		if( modificationDate != null ) {
			attributes.remove( modificationDate );
		}

		ObjAttribute creationDate = entity().getAttribute( "creationDate" );

		if( creationDate != null ) {
			attributes.remove( creationDate );
		}

		ObjAttribute uniqueID = entity().getAttribute( "uniqueID" );

		if( uniqueID != null ) {
			attributes.remove( uniqueID );
		}

		return attributes;
	}

	public List<ObjRelationship> relationships() {
		return new ArrayList<>( entity().getRelationships() );
	}

	private ObjEntity entity() {
		return selectedObject().getObjectContext().getEntityResolver().getObjEntity( selectedObject() );
	}

	public void setCurrentAttributeValue( Object value ) {
		if( value != null ) {
			if( attributeIsData() ) {
				NSKeyValueCoding.Utility.takeValueForKey( selectedObject(), ((NSData)value).bytes(), currentAttribute.getName() );
			}
			else {
				NSKeyValueCoding.Utility.takeValueForKey( selectedObject(), value, currentAttribute.getName() );
			}
		}

		if( value == null ) {
			if( !attributeIsData() ) {
				NSKeyValueCoding.Utility.takeValueForKey( selectedObject(), value, currentAttribute.getName() );
			}
		}
	}

	public Object currentAttributeValue() {
		return NSKeyValueCoding.Utility.valueForKey( selectedObject(), currentAttribute.getName() );
	}

	public String currentEditComponentName() {
		return null;
	}

	public boolean attributeIsInteger() {
		return CayenneUtils.attributeIsInteger( currentAttribute ) || CayenneUtils.attributeIsLong( currentAttribute );
	}

	public boolean attributeIsDecimal() {
		return CayenneUtils.attributeIsDecimal( currentAttribute );
	}

	private boolean attributeIsString() {
		return CayenneUtils.attributeIsString( currentAttribute );
	}

	public boolean attributeIsShortString() {
		return attributeIsString() && !attributeIsLongString();
	}

	/**
	 * FIXME: We're assuming long strings for certain field names here
	 */
	public boolean attributeIsLongString() {
		boolean isLong = "text".equals( currentAttribute.getName() ) || "history".equals( currentAttribute.getName() ) || "testText".equals( currentAttribute.getName() ) || "expectedResult".equals( currentAttribute.getName() ) || "jsonRequestText".equals( currentAttribute.getName() );
		return attributeIsString() && isLong;
	}

	public boolean attributeIsLocalDate() {
		return CayenneUtils.attributeIsLocalDate( currentAttribute );
	}

	public boolean attributeIsLocalDateTime() {
		return CayenneUtils.attributeIsLocalDateTime( currentAttribute );
	}

	public boolean attributeIsDate() {
		return CayenneUtils.attributeIsDate( currentAttribute );
	}

	public boolean attributeIsData() {
		return CayenneUtils.attributeIsData( currentAttribute );
	}

	public boolean attributeIsBoolean() {
		return CayenneUtils.attributeIsBoolean( currentAttribute );
	}

	public WOActionResults download() {
		return USHTTPUtilities.responseWithDataAndMimeType( "file.bin", (NSData)currentAttributeValue(), "octet/stream", true );
	}

	/**
	 * FIXME: We're always using an Icelandic numerical format here.
	 */
	public Format decimalFormat() {
		DecimalFormatSymbols symbols = new DecimalFormatSymbols( new Locale( "is" ) );
		symbols.setGroupingSeparator( '.' );
		symbols.setMonetaryDecimalSeparator( ',' );
		return new DecimalFormat( "##.####", symbols );
	}
}
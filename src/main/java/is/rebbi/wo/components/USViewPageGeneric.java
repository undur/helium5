package is.rebbi.wo.components;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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

import is.rebbi.core.formatters.FormatterWrapperNullSafe;
import is.rebbi.core.humanreadable.HumanReadableUtils;
import is.rebbi.wo.util.USHTTPUtilities;
import jambalaya.CayenneUtils;

/**
 * Generic object edit page.
 */

public class USViewPageGeneric<E extends DataObject> extends USViewPage<E> {

	public ObjAttribute currentAttribute;
	public ObjRelationship currentRelationship;
	public String filename;
	public DataObject currentObject;

	public USViewPageGeneric( WOContext context ) {
		super( context );
	}

	public String currentObjectHumanReadable() {
		return HumanReadableUtils.toStringHuman( currentObject );
	}

	/**
	 * @return A list of attributes
	 */
	public List<ObjAttribute> attributes() {
		final ArrayList<ObjAttribute> attributes = new ArrayList<>( entity().getAttributes() );
		Collections.sort( attributes, Comparator.comparing( ObjAttribute::getName ) );
		return attributes;
	}

	public List<ObjRelationship> relationships() {
		final ArrayList<ObjRelationship> relationships = new ArrayList<>( entity().getRelationships() );
		Collections.sort( relationships, Comparator.comparing( ObjRelationship::getName ) );
		return relationships;
	}

	private ObjEntity entity() {
		return selectedObject().getObjectContext().getEntityResolver().getObjEntity( selectedObject() );
	}

	public void setCurrentAttributeValue( Object value ) {
		if( value != null ) {
			NSKeyValueCoding.Utility.takeValueForKey( selectedObject(), value, currentAttribute.getName() );
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

	public Object currentRelationshipValue() {
		return NSKeyValueCoding.Utility.valueForKey( selectedObject(), currentRelationship.getName() );
	}

	public Object currentRelationshipValueHumanReadable() {
		return HumanReadableUtils.toStringHuman( currentRelationshipValue() );
	}

	public String currentEditComponentName() {
		return null;
	}

	public boolean attributeIsInteger() {
		return CayenneUtils.attributeIsInteger( currentAttribute );
	}

	public boolean attributeIsDecimal() {
		return CayenneUtils.attributeIsDecimal( currentAttribute );
	}

	public boolean attributeIsString() {
		return CayenneUtils.attributeIsString( currentAttribute );
	}

	public boolean attributeIsNumeric() {
		try {
			return java.lang.Number.class.isAssignableFrom( currentAttribute.getJavaClass() );
		}
		catch( Exception e ) {
			throw new RuntimeException( "Error while attempting to check if an attribute is numeric", e );
		}
	}

	public boolean attributeIsShortString() {
		return attributeIsString() && !attributeIsLongString();
	}

	/**
	 * FIXME: We're assuming long strings for certain field names here
	 */
	public boolean attributeIsLongString() {
		boolean isLong = "text".equals( currentAttribute.getName() ) || "history".equals( currentAttribute.getName() );
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

	public Format dateFormatterWithTime() {
		SimpleDateFormat format = new SimpleDateFormat( "dd.MM.yyyy HH:mm" );
		return new FormatterWrapperNullSafe( format );
	}
}
package is.rebbi.wo.components;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.Format;
import java.util.Locale;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WOContext;
import com.webobjects.eoaccess.EOAttribute;
import com.webobjects.eoaccess.EORelationship;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSData;
import com.webobjects.foundation.NSKeyValueCoding;

import er.extensions.eof.ERXGenericRecord;
import er.extensions.foundation.ERXArrayUtilities;
import is.rebbi.wo.util.USEOUtilities;
import is.rebbi.wo.util.USHTTPUtilities;

/**
 * Generic object edit page.
 */

public class USEditPageGenericEOF extends USViewPage {

	public EOAttribute currentAttribute;
	public EORelationship currentRelationship;
	public String filename;

	public USEditPageGenericEOF( WOContext context ) {
		super( context );
	}

	@Override
	public ERXGenericRecord selectedObject() {
		return (ERXGenericRecord)super.selectedObject();
	}

	public NSArray<EOAttribute> attributes() {
		NSArray<EOAttribute> attributes = USEOUtilities.attributes( selectedObject().entity() );
		EOAttribute creationDate = selectedObject().entity().attributeNamed( "creationDate" );

		if( creationDate != null ) {
			attributes = ERXArrayUtilities.arrayMinusObject( attributes, creationDate );
		}

		EOAttribute modificationDate = selectedObject().entity().attributeNamed( "modificationDate" );

		if( modificationDate != null ) {
			attributes = ERXArrayUtilities.arrayMinusObject( attributes, modificationDate );
		}

		return attributes;
	}

	public NSArray<EORelationship> relationships() {
		return USEOUtilities.relationships( selectedObject().entity() );
	}

	public void setCurrentAttributeValue( Object value ) {
		if( value != null ) {
			NSKeyValueCoding.Utility.takeValueForKey( selectedObject(), value, currentAttribute.name() );
		}

		if( value == null ) {
			if( !attributeIsData() ) {
				NSKeyValueCoding.Utility.takeValueForKey( selectedObject(), value, currentAttribute.name() );
			}
		}
	}

	public Object currentAttributeValue() {
		return selectedObject().valueForKey( currentAttribute.name() );
	}

	public String currentEditComponentName() {
		return null;
	}

	public boolean attributeIsInteger() {
		return USEOUtilities.attributeIsInteger( currentAttribute );
	}

	public boolean attributeIsDecimal() {
		return USEOUtilities.attributeIsDecimal( currentAttribute );
	}

	private boolean attributeIsString() {
		return USEOUtilities.attributeIsString( currentAttribute );
	}

	public boolean attributeIsShortString() {
		return attributeIsString() && !attributeIsLongString();
	}

	// FIXME: We're assuming long strings for certain field names here
	public boolean attributeIsLongString() {
		boolean isLong = "text".equals( currentAttribute.name() ) || "history".equals( currentAttribute.name() );
		return attributeIsString() && isLong;
	}

	public boolean attributeIsDate() {
		return USEOUtilities.attributeIsTimestamp( currentAttribute );
	}

	public boolean attributeIsData() {
		return USEOUtilities.attributeIsData( currentAttribute );
	}

	public boolean attributeIsBoolean() {
		return USEOUtilities.attributeIsBoolean( currentAttribute );
	}

	public WOActionResults download() {
		return USHTTPUtilities.responseWithDataAndMimeType( "file.bin", (NSData)currentAttributeValue(), "octet/stream", true );
	}

	// FIXME: We're always using an Icelandic numerical format here.
	public Format decimalFormat() {
		DecimalFormatSymbols symbols = new DecimalFormatSymbols( new Locale( "is" ) );
		symbols.setGroupingSeparator( '.' );
		symbols.setMonetaryDecimalSeparator( ',' );
		return new DecimalFormat( "##.####", symbols );
	}
}
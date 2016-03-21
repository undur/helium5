package is.rebbi.wo.components;

import java.text.Format;
import java.util.Locale;

import com.ibm.icu.text.DecimalFormat;
import com.ibm.icu.text.DecimalFormatSymbols;
import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WOContext;
import com.webobjects.eoaccess.EOAttribute;
import com.webobjects.eoaccess.EORelationship;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSData;

import er.extensions.eof.ERXGenericRecord;
import is.rebbi.wo.definitions.AttributeViewDefinition;
import is.rebbi.wo.definitions.EntityViewDefinition;
import is.rebbi.wo.util.HumanReadableUtils;
import is.rebbi.wo.util.USEOUtilities;
import is.rebbi.wo.util.USHTTPUtilities;

/**
 * Generic object view page.
 */

public class USViewPageGenericEOF extends USViewPage {

	public EOAttribute currentAttribute;
	public EORelationship currentRelationship;
	public String filename;

	public ERXGenericRecord currentObject;

	public USViewPageGenericEOF( WOContext context ) {
		super( context );
	}

	@Override
	public ERXGenericRecord selectedObject() {
		return (ERXGenericRecord)super.selectedObject();
	}

	public NSArray<EOAttribute> attributes() {
		return USEOUtilities.attributes( selectedObject().entity() );
	}

	public NSArray<EORelationship> relationships() {
		return USEOUtilities.relationships( selectedObject().entity() );
	}

	public Object currentAttributeValue() {
		return selectedObject().valueForKey( currentAttribute.name() );
	}

	public Object currentRelationshipValue() {
		return selectedObject().valueForKey( currentRelationship.name() );
	}

	public boolean attributeIsNumeric() {
		return USEOUtilities.attributeIsNumeric( currentAttribute );
	}

	public boolean attributeIsString() {
		return USEOUtilities.attributeIsString( currentAttribute );
	}

	public boolean attributeIsTimestamp() {
		return USEOUtilities.attributeIsTimestamp( currentAttribute );
	}

	public boolean attributeIsData() {
		return USEOUtilities.attributeIsData( currentAttribute );
	}

	public WOActionResults download() {
		return USHTTPUtilities.responseWithDataAndMimeType( "file.bin", (NSData)currentAttributeValue(), "octet/stream", true );
	}

	public String currentAttributeName() {
		AttributeViewDefinition meta = EntityViewDefinition.get( selectedObject().entityName() ).attributeNamed( currentAttribute.name() );

		if( meta != null ) {
			return meta.icelandicName();
		}

		return currentAttribute.name();
	}

	public String currentRelationshipName() {
		AttributeViewDefinition meta = EntityViewDefinition.get( selectedObject().entityName() ).attributeNamed( currentRelationship.name() );

		if( meta != null ) {
			return meta.icelandicName();
		}

		return currentRelationship.name();
	}

	public boolean showOriginalName() {
		return !currentAttributeName().equals( currentAttribute.name() );
	}

	// FIXME: We're always using an Icelandic number format here.
	public Format decimalFormat() {
		DecimalFormatSymbols symbols = new DecimalFormatSymbols( new Locale( "is" ) );
		symbols.setMonetaryGroupingSeparator( '.' );
		symbols.setMonetaryDecimalSeparator( ',' );
		return new DecimalFormat( "##.####", symbols );
	}

	public Object currentRelationshipValueHumanReadable() {
		ERXGenericRecord eo = (ERXGenericRecord)selectedObject().valueForKey( currentRelationship.name() );

		if( eo != null ) {
			return HumanReadableUtils.toStringHuman( eo );
		}

		return null;
	}

	public String currentObjectHumanReadable() {
		return HumanReadableUtils.toStringHuman( currentObject );
	}
}
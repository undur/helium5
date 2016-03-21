package is.rebbi.wo.components.relationships.eof;

import com.webobjects.appserver.WOContext;
import com.webobjects.eoaccess.EORelationship;

import er.extensions.components.ERXNonSynchronizingComponent;
import er.extensions.eof.ERXGenericRecord;

public class USRelationshipEOF extends ERXNonSynchronizingComponent {

	public USRelationshipEOF( WOContext context ) {
		super( context );
	}

	public ERXGenericRecord object() {
		return (ERXGenericRecord)valueForBinding( "object" );
	}

	public String key() {
		return stringValueForBinding( "key" );
	}

	public Boolean disableObjectSelection() {
		return (Boolean)valueForBinding( "disableObjectSelection" );
	}

	public EORelationship relationship() {
		return object().entity().relationshipNamed( key() );
	}
}
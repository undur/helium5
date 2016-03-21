package is.rebbi.wo.util;

import com.webobjects.eoaccess.EOAttribute;
import com.webobjects.eoaccess.EOEntity;
import com.webobjects.eoaccess.EOModelGroup;
import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.eocontrol.EOEnterpriseObject;
import com.webobjects.eocontrol.EOGlobalID;
import com.webobjects.eocontrol.EOKeyGlobalID;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSMutableArray;

import er.extensions.eof.ERXGenericRecord;
import er.extensions.eof.ERXKeyGlobalID;

public class PKSerializerEOF {

	private static final String PK_ELEMENT_SEPARATOR = "|";

	public static String serialize( EOEnterpriseObject eo ) {
		return serialize( ((ERXGenericRecord)eo).permanentGlobalID() );
	}

	protected static String serialize( EOGlobalID gid ) {

		//		if( gid.isTemporary() ) {
		//			return null;
		//			We might want to look into this further; ie. what to do when the EO does not have a PK.
		//			throw new IllegalArgumentException( "We currently only support serialization of EOKeyGlobalIDs, but you provided a " + gid.getClass() );
		//		}

		ERXKeyGlobalID keyGID = ERXKeyGlobalID.globalIDForGID( (EOKeyGlobalID)gid );
		return keyGID.keyValuesArray().componentsJoinedByString( PK_ELEMENT_SEPARATOR );
	}

	public static ERXGenericRecord eo( EOEditingContext ec, String entityName, String string ) {

		EOEntity entity = EOModelGroup.defaultGroup().entityNamed( entityName );

		if( entity == null ) {
			return null;
		}

		EOGlobalID gid = PKSerializerEOF.deSerialize( entity, string );
		ERXGenericRecord eo = (ERXGenericRecord)ec.faultForGlobalID( gid, ec );
		return eo;
	}

	private static EOGlobalID deSerialize( EOEntity entity, String string ) {
		NSArray<?> array = NSArray.componentsSeparatedByString( string, PK_ELEMENT_SEPARATOR );
		array = coercePKValues( entity, array );
		Object[] values = array.toArray();
		ERXKeyGlobalID gid = new ERXKeyGlobalID( entity.name(), values );
		return gid.globalID();
	}

	private static NSArray<?> coercePKValues( EOEntity entity, NSArray<?> values ) {

		if( entity == null ) {
			throw new IllegalArgumentException( "[entity] cannot be null" );
		}

		if( values == null ) {
			throw new IllegalArgumentException( "you must provide an array of PK values" );
		}

		NSMutableArray results = new NSMutableArray();

		for( int i = 0; i < entity.primaryKeyAttributes().count(); i++ ) {
			EOAttribute attribute = entity.primaryKeyAttributes().objectAtIndex( i );
			Object value = values.get( i );

			if( "java.lang.Number".equals( attribute.className() ) || "i".equals( attribute.valueType() ) ) {
				value = Integer.valueOf( (String)value );
			}

			results.addObject( value );
		}

		return results;
	}
}
package is.rebbi.wo.util;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webobjects.eoaccess.EOAttribute;
import com.webobjects.eoaccess.EOEntity;
import com.webobjects.eoaccess.EOJoin;
import com.webobjects.eoaccess.EOModelGroup;
import com.webobjects.eoaccess.EORelationship;
import com.webobjects.eoaccess.EOUtilities;
import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.eocontrol.EOEnterpriseObject;
import com.webobjects.eocontrol.EOFetchSpecification;
import com.webobjects.eocontrol.EOKeyValueQualifier;
import com.webobjects.eocontrol.EOOrQualifier;
import com.webobjects.eocontrol.EOQualifier;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSBundle;
import com.webobjects.foundation.NSData;
import com.webobjects.foundation.NSDictionary;
import com.webobjects.foundation.NSMutableArray;
import com.webobjects.foundation.NSSelector;
import com.webobjects.foundation.NSTimestamp;

import er.extensions.eof.ERXEOControlUtilities;
import er.extensions.eof.ERXEnterpriseObject;
import er.extensions.qualifiers.ERXAndQualifier;
import er.extensions.qualifiers.ERXTrueQualifier;
import is.rebbi.core.util.ListUtilities;
import is.rebbi.core.util.StringUtilities;

/**
 * EOF related utility classes.
 */

public class USEOUtilities {

	private static final Logger logger = LoggerFactory.getLogger( USEOUtilities.class );

	/**
	 * No instances created, ever.
	 */
	private USEOUtilities() {}

	/**
	 * Fetches an object matching the key-value pair.
	 */
	public static EOEnterpriseObject objectMatchingKeyAndValue( EOEditingContext ec, String entityName, String attributeName, Object value ) {

		if( value == null ) {
			return null;
		}

		EOQualifier q = new EOKeyValueQualifier( attributeName, EOQualifier.QualifierOperatorEqual, value );
		EOFetchSpecification fs = new EOFetchSpecification( entityName, q, null );
		fs.setFetchLimit( 1 );
		NSArray<EOEnterpriseObject> fetched = ec.objectsWithFetchSpecification( fs );

		if( ListUtilities.hasObjects( fetched ) ) {
			return fetched.objectAtIndex( 0 );
		}

		return null;
	}

	/**
	 * Creates a qualifier that qualifies between two dates:
	 * - from and including the first one
	 * - up to and NOT including the second one.
	 *
	 * @param keyPath The keypath to create the qualifier for.
	 * @param dateFrom First date. If null, assumes infinity in to the future.
	 * @param dateFrom Last date. If null, assumes infinity in to the past.
	 */
	public static EOQualifier qualifierBetweenDates( String keyPath, NSTimestamp dateFrom, NSTimestamp dateTo ) {
		return qualifierBetween( keyPath, dateFrom, dateTo, true, false );
	}

	/**
	 * Creates a qualifier that qualifies between two values.
	 *
	 * @param keyPath The keyPath to create the qualifier for.
	 * @param from First date. If null, assumes infinity in to the future.
	 * @param from Last date. If null, assumes infinity in to the past.
	 */
	public static EOQualifier qualifierBetween( String keyPath, Object from, Object to, boolean includeFrom, boolean includeTo ) {
		NSMutableArray<EOQualifier> qualArr = new NSMutableArray<>();

		if( from != null ) {
			NSSelector op;

			if( includeFrom ) {
				op = EOQualifier.QualifierOperatorGreaterThanOrEqualTo;
			}
			else {
				op = EOQualifier.QualifierOperatorGreaterThan;
			}

			qualArr.addObject( new EOKeyValueQualifier( keyPath, op, from ) );
		}

		if( to != null ) {
			NSSelector op;

			if( includeTo ) {
				op = EOQualifier.QualifierOperatorLessThanOrEqualTo;
			}
			else {
				op = EOQualifier.QualifierOperatorLessThan;
			}

			qualArr.addObject( new EOKeyValueQualifier( keyPath, op, to ) );
		}

		return new ERXAndQualifier( qualArr );
	}

	/**
	 * @return The letters of the alphabet that words in the given key for the given entity start with in uppercase.
	 */
	public static NSArray<String> firstLettersForKeyPathInEntity( EOEditingContext ec, String keyPath, String entityName, EOQualifier q ) {

		EOFetchSpecification fs = new EOFetchSpecification( entityName, q, null );
		fs.setFetchesRawRows( true );
		fs.setRawRowKeyPaths( new NSArray<>( keyPath ) );

		NSArray<NSDictionary> fetched = ec.objectsWithFetchSpecification( fs );
		return USArrayUtilities.firstLettersForKeyPathInArray( keyPath, fetched );
	}

	/**
	 * @return The specified object, null if no id is specified.
	 */
	public static <E extends ERXEnterpriseObject> E objectWithPK( EOEditingContext ec, String entityName, Object id ) {

		if( id == null ) {
			return null;
		}

		return (E)ERXEOControlUtilities.objectWithPrimaryKeyValue( ec, entityName, id, NSArray.emptyArray() );
	}

	/**
	 * @return True if the given EC is nested in another EC.
	 */
	public static boolean isNested( EOEditingContext ec ) {
		return ec.parentObjectStore() instanceof EOEditingContext;
	}

	public static boolean attributeIsBoolean( EOAttribute attribute ) {
		return Boolean.class.getName().equals( attribute.valueTypeClassName() );
	}

	public static boolean attributeIsData( EOAttribute attribute ) {
		return NSData.class.getName().equals( attribute.valueTypeClassName() );
	}

	public static boolean attributeIsTimestamp( EOAttribute attribute ) {
		return NSTimestamp.class.getName().equals( attribute.valueTypeClassName() );
	}

	public static boolean attributeIsString( EOAttribute attribute ) {
		return String.class.getName().equals( attribute.valueTypeClassName() );
	}

	public static boolean attributeIsDecimal( EOAttribute attribute ) {
		return BigDecimal.class.getName().equals( attribute.valueTypeClassName() );
	}

	public static boolean attributeIsInteger( EOAttribute attribute ) {

		if( attributeIsDecimal( attribute ) ) {
			return false;
		}

		try {
			Class clazz = Class.forName( attribute.valueTypeClassName() );
			return java.lang.Number.class.isAssignableFrom( clazz );
		}
		catch( Exception e ) {
			throw new RuntimeException( "Error while attempting to check if an attribute is numeric", e );
		}
	}

	public static boolean attributeIsNumeric( EOAttribute attribute ) {
		try {
			Class clazz = Class.forName( attribute.valueTypeClassName() );
			return java.lang.Number.class.isAssignableFrom( clazz );
		}
		catch( Exception e ) {
			throw new RuntimeException( "Error while attempting to check if an attribute is numeric", e );
		}
	}

	/**
	 * @return A qualifier suitable for use in search for all objects in the given entity.
	 */
	public static EOQualifier allQualifier( EOEditingContext ec, String searchString, String entityName ) {

		if( !StringUtilities.hasValue( searchString ) ) {
			return new ERXTrueQualifier();
		}

		EOEntity entity = EOUtilities.entityNamed( ec, entityName );
		return allQualifier( searchString, entity );
	}

	/**
	 * @return A qualifier suitable for use in search for all objects in the given entity.
	 */
	public static EOQualifier allQualifier( String searchString, EOEntity entity ) {

		if( searchString == null ) {
			return null;
		}

		NSMutableArray<EOQualifier> a = new NSMutableArray<>();

		NSArray<EOAttribute> attributes = attributes( entity );

		for( EOAttribute attribute : attributes ) {
			if( attributeIsString( attribute ) ) {
				a.addObject( new EOKeyValueQualifier( attribute.name(), EOQualifier.QualifierOperatorCaseInsensitiveLike, "*" + searchString + "*" ) );
			}

			if( attributeIsNumeric( attribute ) && StringUtilities.isDigitsOnly( searchString ) ) {
				a.addObject( new EOKeyValueQualifier( attribute.name(), EOQualifier.QualifierOperatorEqual, new BigDecimal( searchString ) ) );
			}
		}

		return new EOOrQualifier( a );
	}

	/**
	 * @return True if the given attribute is used in a join.
	 */
	private static boolean usedInJoin( EOAttribute attribute ) {
		EOEntity entity = attribute.entity();

		for( EORelationship relationship : entity.relationships() ) {
			for( EOJoin j : relationship.joins() ) {
				if( j.sourceAttribute().equals( attribute ) ) {
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * @return Attributes of the given entity.
	 */
	public static NSArray<EOAttribute> attributes( EOEntity entity ) {
		NSMutableArray<EOAttribute> attributes = new NSMutableArray<>();

		for( EOAttribute attribute : entity.attributes() ) {
			boolean isClassProperty = entity.classProperties().containsObject( attribute );
			boolean isPrimaryKey = !attribute._isPrimaryKeyClassProperty();
			boolean isForeignKey = usedInJoin( attribute );

			if( isClassProperty && isPrimaryKey && !isForeignKey ) {
				attributes.add( attribute );
			}
		}

		return attributes;
	}

	/**
	 * @return Relationships of the given entity.
	 */
	public static NSArray<EORelationship> relationships( EOEntity entity ) {
		NSMutableArray<EORelationship> relationships = new NSMutableArray<>();

		for( EORelationship relationship : entity.relationships() ) {
			if( entity.classProperties().contains( relationship ) ) {
				relationships.add( relationship );
			}
		}

		return relationships;
	}

	/**
	 * @return The class associated with the given entity name.
	 */
	public static Class classForEntityNamed( String entityName ) {
		EOEntity entity = EOModelGroup.defaultGroup().entityNamed( entityName );

		if( entity == null ) {
			logger.error( "No entity found named: " + entityName );
		}

		Class entityClass = null;

		if( entity != null ) {
			String className = entity.className();

			try {
				entityClass = NSBundle.mainBundle()._classWithName( className );
			}
			catch( Exception e ) {
				logger.error( "Failed to instantiate class: {}", className, e );
			}
		}

		return entityClass;
	}
}
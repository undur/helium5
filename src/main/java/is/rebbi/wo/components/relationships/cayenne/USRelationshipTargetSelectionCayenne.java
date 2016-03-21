package is.rebbi.wo.components.relationships.cayenne;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.cayenne.DataObject;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.map.EntityResolver;
import org.apache.cayenne.map.ObjAttribute;
import org.apache.cayenne.map.ObjEntity;
import org.apache.cayenne.map.ObjRelationship;
import org.apache.cayenne.query.PrefetchTreeNode;
import org.apache.cayenne.query.SelectQuery;
import org.apache.cayenne.util.CayenneMapEntry;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WOContext;
import com.webobjects.foundation.NSKeyValueCoding;
import com.webobjects.foundation.NSKeyValueCodingAdditions;

import er.extensions.components.ERXComponent;
import is.rebbi.wo.cayenne.USCayenne;
import is.rebbi.wo.components.USViewPage;
import is.rebbi.wo.definitions.AttributeViewDefinition;
import is.rebbi.wo.definitions.EntityViewDefinition;

public class USRelationshipTargetSelectionCayenne extends USViewPage {

	public String searchString;

	public ERXComponent callingComponent;
	public DataObject currentObject;
	public DataObject object;
	public String key;
	public AttributeViewDefinition currentAttribute;
	public boolean saveOnSelect;

	public USRelationshipTargetSelectionCayenne( WOContext context ) {
		super( context );
	}

	public String sourceEntityName() {
		return EntityViewDefinition.icelandicName( object.getObjectId().getEntityName() );
	}

	public String destinationEntityName() {
		return EntityViewDefinition.icelandicName( relationship().getTargetEntityName() );
	}

	public String destinationEntityNamePlural() {
		return EntityViewDefinition.icelandicNamePlural( relationship().getTargetEntityName() );
	}

	public ObjRelationship relationship() {
		ObjectContext oc = object.getObjectContext();
		return oc.getEntityResolver().getObjEntity( object.getObjectId().getEntityName() ).getRelationship( key );
	}

	public List<?> objects() {
		SelectQuery<?> query = new SelectQuery<>( relationship().getTargetEntityName() );

		for( String keyPath : keyPathsToPrefetch() ) {
			query.addPrefetch( PrefetchTreeNode.withPath( keyPath, PrefetchTreeNode.JOINT_PREFETCH_SEMANTICS ) );
		}

		return object.getObjectContext().select( query );
	}

	private List<String> keyPathsToPrefetch() {
		Set<String> l = new HashSet<>();

		for( String keyPath : keyPathsToShow() ) {
			l.addAll( relationshipsInKeyPath( keyPath ) );
		}

		return new ArrayList<>( l );
	}

	private List<String> relationshipsInKeyPath( String keyPath ) {
		EntityResolver entityResolver = USCayenne.serverRuntime().getDataDomain().getEntityResolver();
		ObjEntity entity = entityResolver.getObjEntity( relationship().getTargetEntityName() );

		List<String> relationships = new ArrayList<>();

		StringBuilder b = new StringBuilder();

		for( Iterator<CayenneMapEntry> it = entity.resolvePathComponents( keyPath ); it.hasNext(); ) {
			CayenneMapEntry next = it.next();

			if( next instanceof ObjRelationship ) {

				if( b.length() > 0 ) {
					b.append( "." );
				}

				b.append( next.getName() );
				relationships.add( b.toString() );
			}
		}

		return relationships;
	}

	public List<String> keyPathsToShow() {
		List<AttributeViewDefinition> attributesToShow = selectedViewDefinition().attributesToShow();

		if( !attributesToShow.isEmpty() ) {
			return attributesToShow.stream().map( AttributeViewDefinition::name ).collect( Collectors.toList() );
		}
		else {
			return USCayenne.serverRuntime().getDataDomain().getEntityResolver().getObjEntity( selectedViewDefinition().entityClass() ).getAttributes().stream().map( ObjAttribute::getName ).collect( Collectors.toList() );
		}
	}

	private EntityViewDefinition selectedViewDefinition() {
		return EntityViewDefinition.get( relationship().getTargetEntityName() );
	}

	public WOActionResults cancel() {
		callingComponent.ensureAwakeInContext( context() );
		return callingComponent;
	}

	public WOActionResults selectObject() {

		if( relationship().isToMany() ) {
			object.addToManyTarget( key, currentObject, true );
		}
		else {
			NSKeyValueCoding.Utility.takeValueForKey( object, currentObject, key );
		}

		if( saveOnSelect ) {
			object.getObjectContext().commitChanges();
		}

		return callingComponent;
	}

	public Object currentValue() {
		return NSKeyValueCodingAdditions.Utility.valueForKeyPath( currentObject, currentAttribute.name() );
	}

	@Override
	public EntityViewDefinition viewDefinition() {
		return EntityViewDefinition.get( relationship().getTargetEntityName() );
	}
}
package is.rebbi.wo.components;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.cayenne.DataObject;
import org.apache.cayenne.map.ObjEntity;
import org.apache.cayenne.map.ObjRelationship;
import org.apache.cayenne.query.Ordering;
import org.apache.cayenne.util.CayenneMapEntry;

import com.webobjects.appserver.WOContext;
import com.webobjects.foundation.NSKeyValueCodingAdditions;

import er.extensions.components.ERXNonSynchronizingComponent;
import is.rebbi.core.util.ListUtilities;
import is.rebbi.wo.definitions.EntityViewDefinition;

public class USEmbeddedList extends ERXNonSynchronizingComponent {

	public Object currentObject;
	public String currentKeyPath;
	private List<Ordering> _orderings;

	public USEmbeddedList(WOContext context) {
		super( context );
	}

	public List<Ordering> orderings() {
		if( _orderings == null ) {
			_orderings = new ArrayList<>();
			_orderings.add( initialOrdering() );
		}

		return _orderings;
	}

	public void setOrderings( List<Ordering> orderings ) {
		_orderings = orderings;
	}

	private Ordering initialOrdering() {
		return new Ordering( keyPaths().get( 0 ) );
	}

	public List<String> keyPaths() {
		return (List<String>)valueForBinding( "keyPaths" );
	}

	public List<DataObject> objects() {
		List objects = (List)valueForBinding( "objects" );
		Ordering.orderList( objects, orderings() );
		return objects;
	}

	public Object currentValue() {
		return NSKeyValueCodingAdditions.Utility.valueForKeyPath( currentObject, currentKeyPath );
	}

	public String currentKeyPathName() {
		if( !ListUtilities.hasObjects( objects() ) ) {
			return null;
		}

		return entityViewDefinition().attributeNamed( currentKeyPath ).icelandicName();
	}

	private EntityViewDefinition entityViewDefinition() {
		return EntityViewDefinition.get( firstObject().getObjectId().getEntityName() );
	}

	private DataObject firstObject() {
		return objects().get( 0 );
	}

	public boolean noSortOnCurrentKeyPath() {
		ObjEntity entity = firstObject().getObjectContext().getEntityResolver().getObjEntity( entityViewDefinition().entityClass() );

		CayenneMapEntry last = null;

		for( Iterator<CayenneMapEntry> it = entity.resolvePathComponents( currentKeyPath ); it.hasNext(); ) {
			last = it.next();
		}

		if( last instanceof ObjRelationship ) {
			if( ((ObjRelationship)last).isToMany() ) {
				return true;
			}
		}

		return false;
	}
}
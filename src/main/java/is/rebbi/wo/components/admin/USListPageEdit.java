package is.rebbi.wo.components.admin;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.PersistentObject;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.exp.property.PropertyFactory;
import org.apache.cayenne.map.ObjAttribute;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.cayenne.query.Ordering;
import org.apache.cayenne.query.PrefetchTreeNode;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WOContext;
import com.webobjects.foundation.NSKeyValueCodingAdditions;

import er.extensions.appserver.ERXWOContext;
import is.rebbi.core.util.StringUtilities;
import is.rebbi.wo.components.USBaseComponent;
import is.rebbi.wo.util.Inspection;
import jambalaya.CayenneUtils;
import jambalaya.Jambalaya;
import jambalaya.definitions.AttributeDefinition;
import jambalaya.definitions.EntityDefinition;

public class USListPageEdit extends USBaseComponent {

	/**
	 * The Object Context to fetch into.
	 */
	private ObjectContext _oc;

	/**
	 * A generic search string, used to generate a qualifier.
	 */
	public String searchString;

	/**
	 * The selected entityViewDefinition
	 */
	private Class _entityClass;

	/**
	 * the object currently being iterated over in lists.
	 */
	public PersistentObject currentObject;

	/**
	 * The keyPath currently being iterated over in lists.
	 */
	public String currentKeyPath;

	/**
	 * Index of currently selected batch.
	 */
	public int currentBatchIndex = 0;

	/**
	 * Size of each batch.
	 */
	public int batchSize = 200;

	/**
	 * Estimated number of objects returned from the query;
	 */
	private Long _numberOfObjects;

	/**
	 * Orderings to use when sorting the objects.
	 */
	private List<Ordering> _orderings;

	/**
	 * A list of selected objects in the UI
	 */
	public Set<PersistentObject> selectedObjects = new HashSet<>();

	public USListPageEdit( WOContext context ) {
		super( context );
	}

	public List<Ordering> orderings() {
		if( _orderings == null ) {
			_orderings = new ArrayList<>();

			//			FIXME: This is messing with us, figure out why (remember the whole "Dictionary" thing?
			//			if( initialOrdering() != null ) {
			//				_orderings.add( initialOrdering() );
			//			}
		}

		return _orderings;
	}

	public WOActionResults edit() {
		return Inspection.editObjectInContext( currentObject, context() );
	}

	protected ObjectContext oc() {
		if( _oc == null ) {
			_oc = Jambalaya.newContext();
		}

		return _oc;
	}

	public boolean hasMultipleBatches() {
		return numberOfBatches() > 1;
	}

	public Integer currentBatchIndexForDisplay() {
		return currentBatchIndex + 1;
	}

	public void setCurrentBatchIndexForDisplay( Integer value ) {
		currentBatchIndex = value - 1;
	}

	public Integer firstObjectIndexForDisplay() {
		return firstObjectIndex() + 1;
	}

	public List<Integer> batches() {
		return IntStream.rangeClosed( 1, (int)numberOfBatches() ).boxed().collect( Collectors.toList() );
	}

	/**
	 * @return An expression based on the user's input
	 */
	private Expression expression() {

		if( !StringUtilities.hasValue( searchString ) ) {
			return null;
		}

		final Expression fromEntity = CayenneUtils.allExpression( oc(), searchString, entityClass() );
		final Expression fromKeyPaths = CayenneUtils.allExpression( oc(), searchString, entityClass(), keyPathsToShow() );
		final Expression e = ExpressionFactory.or( fromEntity, fromKeyPaths );

		return e;
	}

	public long numberOfObjects() {
		if( _numberOfObjects == null ) {
			// CHECKME: I'm actually not sure why we need to cast here?
			_numberOfObjects = (Long)ObjectSelect
					.query( entityClass() )
					.column( PropertyFactory.COUNT )
					.where( expression() )
					.selectOne( oc() );
		}

		return _numberOfObjects;
	}

	public long numberOfBatches() {
		long numberOfBatches = numberOfObjects() / batchSize;

		if( numberOfObjects() % batchSize != 0 ) {
			numberOfBatches++;
		}

		return numberOfBatches;
	}

	public int firstObjectIndex() {
		return currentBatchIndex * batchSize;
	}

	public int lastObjectIndex() {
		return firstObjectIndex() + batchSize;
	}

	public List<?> objects() {
		ObjectSelect<?> query = ObjectSelect.query( entityClass() );

		query.limit( batchSize );
		query.offset( firstObjectIndex() );

		for( String keyPath : CayenneUtils.keyPathsToPrefetch( oc(), entityDefinition().entityClass(), keyPathsToShow() ) ) {
			query.prefetch( PrefetchTreeNode.withPath( keyPath, PrefetchTreeNode.DISJOINT_BY_ID_PREFETCH_SEMANTICS ) );
		}

		query.where( expression() );
		query.orderBy( orderings() ); // FIXME: This is currently changing the query. Need to find out what's happening.

		_numberOfObjects = null;
		return query.select( oc() );
	}

	public EntityDefinition entityDefinition() {
		return EntityDefinition.get( entityClass() );
	}

	public void setEntityClass( Class value ) {
		_entityClass = value;
	}

	public Class entityClass() {
		return _entityClass;
	}

	public Object currentValue() {
		return NSKeyValueCodingAdditions.Utility.valueForKeyPath( currentObject, currentKeyPath );
	}

	public List<String> keyPathsToShow() {
		List<AttributeDefinition> attributesToShow = entityDefinition().attributesToShow();

		if( !attributesToShow.isEmpty() ) {
			return attributesToShow.stream().map( AttributeDefinition::name ).collect( Collectors.toList() );
		}
		else {
			return oc().getEntityResolver().getObjEntity( entityClass() ).getAttributes().stream().map( ObjAttribute::getName ).collect( Collectors.toList() );
		}
	}

	private Ordering initialOrdering() {

		if( keyPathsToShow().isEmpty() ) {
			return null;
		}

		return new Ordering( keyPathsToShow().get( 0 ) );
	}

	public WOActionResults search() {
		currentBatchIndex = 0;
		return null;
	}

	public String currentKeyPathDisplayName() {
		return currentAttributeViewDefinition().icelandicName();
	}

	public AttributeDefinition currentAttributeViewDefinition() {
		return entityDefinition().attributeNamed( currentKeyPath );
	}

	public WOActionResults createObject() {
		ObjectContext childContext = Jambalaya.newContext( oc() );
		Object object = childContext.newObject( entityClass() );
		WOActionResults nextPage = Inspection.editObjectInContext( object, context() );
		return nextPage;
	}

	public boolean currentIsSelected() {
		return selectedObjects.contains( currentObject );
	}

	public void setCurrentIsSelected( boolean value ) {
		if( value ) {
			if( !selectedObjects.contains( currentObject ) ) {
				selectedObjects.add( currentObject );
			}
		}
		else {
			if( selectedObjects.contains( currentObject ) ) {
				selectedObjects.remove( currentObject );
			}
		}
	}

	public WOActionResults deleteSelectedObjects() {
		oc().deleteObjects( selectedObjects );
		oc().commitChanges();

		selectedObjects = new HashSet<>();

		return search();
	}

	public String currentCheckboxID() {
		return ((ERXWOContext)context()).safeElementID();
	}

	public WOActionResults toggleSelected() {
		setCurrentIsSelected( !currentIsSelected() );
		return null;
	}

	public String currentTRClass() {
		return currentIsSelected() ? "danger" : null;
	}
}
package is.rebbi.wo.components;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.cayenne.DataObject;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.map.ObjAttribute;
import org.apache.cayenne.query.Ordering;
import org.apache.cayenne.query.PrefetchTreeNode;
import org.apache.cayenne.query.SelectQuery;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WOContext;
import com.webobjects.foundation.NSKeyValueCodingAdditions;

import er.extensions.appserver.ERXWOContext;
import is.rebbi.core.util.StringUtilities;
import is.rebbi.wo.cayenne.USCayenne;
import is.rebbi.wo.definitions.AttributeViewDefinition;
import is.rebbi.wo.definitions.EntityViewDefinition;
import is.rebbi.wo.util.Inspection;
import jambalaya.CayenneUtils;
import jambalaya.Jambalaya;

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
	private EntityViewDefinition _selectedViewDefinition;

	/**
	 * the object currently being iterated over in lists.
	 */
	public DataObject currentObject;

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
	public Set<DataObject> selectedObjects = new HashSet<>();

	public USListPageEdit( WOContext context ) {
		super( context );
	}

	public List<Ordering> orderings() {
		if( _orderings == null ) {
			_orderings = new ArrayList<>();
			_orderings.add( initialOrdering() );
		}

		return _orderings;
	}

	public WOActionResults edit() {
		return Inspection.editObjectInContext( currentObject, context() );
	}

	protected ObjectContext oc() {
		if( _oc == null ) {
			_oc = USCayenne.defaultObjectContext( session() );
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

		Expression fromEntity = CayenneUtils.allQualifier( oc(), searchString, selectedViewDefinition().entityClass() );
		Expression fromKeyPaths = CayenneUtils.allExpression( oc(), searchString, selectedViewDefinition().entityClass(), keyPathsToShow() );
		Expression e = ExpressionFactory.or( fromEntity, fromKeyPaths );

		e = CayenneUtils.convertToOuter( e );

		return e;
	}

	public long numberOfObjects() {
		if( _numberOfObjects == null ) {
			_numberOfObjects = CayenneUtils.count( oc(), selectedViewDefinition().entityClass(), expression() );
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
		SelectQuery<?> query = new SelectQuery<>( selectedViewDefinition().entityClass() );
		query.setFetchLimit( batchSize );
		query.setFetchOffset( firstObjectIndex() );

		for( String keyPath : CayenneUtils.keyPathsToPrefetch( oc(), selectedViewDefinition().entityClass(), keyPathsToShow() ) ) {
			query.addPrefetch( PrefetchTreeNode.withPath( keyPath, PrefetchTreeNode.DISJOINT_BY_ID_PREFETCH_SEMANTICS ) );
		}

		query.setQualifier( expression() );
		query.addOrderings( orderings() );

		_numberOfObjects = null;
		return oc().select( query );
	}

	public EntityViewDefinition selectedViewDefinition() {
		return _selectedViewDefinition;
	}

	public void setSelectedViewDefinition( EntityViewDefinition value ) {
		_selectedViewDefinition = value;
	}

	public Object currentValue() {
		return NSKeyValueCodingAdditions.Utility.valueForKeyPath( currentObject, currentKeyPath );
	}

	public List<String> keyPathsToShow() {
		List<AttributeViewDefinition> attributesToShow = selectedViewDefinition().attributesToShow();

		if( !attributesToShow.isEmpty() ) {
			return attributesToShow.stream().map( AttributeViewDefinition::name ).collect( Collectors.toList() );
		}
		else {
			return Jambalaya.serverRuntime().getDataDomain().getEntityResolver().getObjEntity( selectedViewDefinition().entityClass() ).getAttributes().stream().map( ObjAttribute::getName ).collect( Collectors.toList() );
		}
	}

	private Ordering initialOrdering() {
		return new Ordering( keyPathsToShow().get( 0 ) );
	}

	public WOActionResults search() {
		currentBatchIndex = 0;
		return null;
	}

	public String currentKeyPathDisplayName() {
		return currentAttributeViewDefinition().icelandicName();
	}

	public AttributeViewDefinition currentAttributeViewDefinition() {
		return selectedViewDefinition().attributeNamed( currentKeyPath );
	}

	public WOActionResults createObject() {
		Object object = oc().newObject( selectedViewDefinition().entityClass() );
		oc().commitChanges();
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
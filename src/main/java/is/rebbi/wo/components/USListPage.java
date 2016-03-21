package is.rebbi.wo.components;

import java.util.List;
import java.util.stream.Collectors;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WOContext;
import com.webobjects.eoaccess.EOAttribute;
import com.webobjects.eoaccess.EODatabaseDataSource;
import com.webobjects.eoaccess.EORelationship;
import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.eocontrol.EOFetchSpecification;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSMutableArray;

import er.extensions.appserver.ERXDisplayGroup;
import er.extensions.batching.ERXBatchingDisplayGroup;
import er.extensions.eof.ERXGenericRecord;
import er.extensions.jdbc.ERXJDBCUtilities;
import is.rebbi.wo.definitions.AttributeViewDefinition;
import is.rebbi.wo.definitions.EntityViewDefinition;
import is.rebbi.wo.util.Inspection;
import is.rebbi.wo.util.USArrayUtilities;
import is.rebbi.wo.util.USEOUtilities;

public class USListPage extends USBaseComponent {

	public ERXDisplayGroup dg;
	public String searchString;

	private EntityViewDefinition _selectedViewDefinition;
	public ERXGenericRecord currentObject;
	public String currentAttributeName;

	public int currentColumnIndex;

	public USListPage( WOContext context ) {
		super( context );
	}

	@Override
	public void ensureAwakeInContext( WOContext context ) {
		super.ensureAwakeInContext( context );
		resetDG();
	}

	public EntityViewDefinition selectedViewDefinition() {
		return _selectedViewDefinition;
	}

	public void setSelectedViewDefinition( EntityViewDefinition value ) {
		_selectedViewDefinition = value;
		resetDG();
	}

	private boolean databaseSupportsLimitExpressions() {
		return !ERXJDBCUtilities.databaseProductName( selectedViewDefinition().entity().model() ).equals( "Informix" );
	}

	public void resetDG() {
		if( databaseSupportsLimitExpressions() ) {
			dg = new ERXBatchingDisplayGroup<>();
		}
		else {
			dg = new ERXDisplayGroup<>();
		}

		EODatabaseDataSource ds = new EODatabaseDataSource( ec(), selectedViewDefinition().entity().name() );
		EOFetchSpecification fetchSpecification = ds.fetchSpecification();
		fetchSpecification.setPrefetchingRelationshipKeyPaths( keyPathsToPrefetch() );
		dg.setDataSource( ds );
		dg.setNumberOfObjectsPerBatch( 100 );
		dg.setSortOrderings( selectedViewDefinition().defaultSortOrderings().mutableClone() );

		if( searchString != null ) {
			dg.setQualifier( USEOUtilities.allQualifier( searchString, selectedViewDefinition().entity() ) );
		}

		dg.qualifyDataSource();
	}

	private NSArray<String> keyPathsToPrefetch() {
		NSMutableArray<String> keyPathsToPrefetch = new NSMutableArray<>();

		for( String keyPath : keyPathsToShow() ) {
			for( String partialKeyPath : allElementsOfKeyPath( keyPath ) ) {
				EORelationship relationship = selectedViewDefinition().entity()._relationshipForPath( partialKeyPath );

				if( relationship != null ) {
					keyPathsToPrefetch.addObject( partialKeyPath );
				}
			}
		}

		return keyPathsToPrefetch;
	}

	private static NSArray<String> allElementsOfKeyPath( String keyPath ) {
		NSMutableArray<String> paths = new NSMutableArray<>();

		NSMutableArray<String> components = NSArray.componentsSeparatedByString( keyPath, "." ).mutableClone();

		paths.add( components.get( 0 ) );
		components.remove( 0 );

		while( !components.isEmpty() ) {
			paths.add( "." + components.get( 0 ) );
			components.remove( 0 );
		}

		return paths;
	}

	private EOEditingContext ec() {
		return session().defaultEditingContext();
	}

	public Object currentValue() {
		return currentObject.valueForKeyPath( currentAttributeName );
	}

	public List<String> keyPathsToShow() {
		List<AttributeViewDefinition> attributesToShow = selectedViewDefinition().attributesToShow();

		if( USArrayUtilities.hasObjects( attributesToShow ) ) {
			return attributesToShow.stream().map( AttributeViewDefinition::name ).collect( Collectors.toList() );
		}
		else {
			List<EOAttribute> attributes = USEOUtilities.attributes( selectedViewDefinition().entity() );
			return attributes.stream().map( EOAttribute::name ).collect( Collectors.toList() );
		}
	}

	public String currentAttributeDisplayName() {
		if( currentAttributeViewDefinition() != null ) {
			return currentAttributeViewDefinition().icelandicName();
		}

		return currentAttributeName;
	}

	public AttributeViewDefinition currentAttributeViewDefinition() {
		return selectedViewDefinition().attributeNamed( currentAttributeName );
	}

	public WOActionResults createObject() {
		WOActionResults nextPage = Inspection.createAndEditObject( ec(), selectedViewDefinition().entity().name(), context() );
		ec().saveChanges();
		return nextPage;
	}

	public boolean isNotFirst() {
		return currentColumnIndex > 0;
	}
}
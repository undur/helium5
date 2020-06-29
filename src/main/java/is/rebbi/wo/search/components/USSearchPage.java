package is.rebbi.wo.search.components;

import java.util.ArrayList;
import java.util.List;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WOContext;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSMutableArray;
import com.webobjects.foundation.NSMutableSet;

import er.extensions.appserver.ERXApplication;
import er.extensions.appserver.ERXDisplayGroup;
import is.rebbi.core.search.IndexRecord;
import is.rebbi.wo.components.USBaseComponent;
import is.rebbi.wo.definitions.EntityViewDefinition;
import is.rebbi.wo.search.Indexer;
import is.rebbi.wo.search.USSearchAction;

public class USSearchPage extends USBaseComponent {

	public int index;
	public ERXDisplayGroup<IndexRecord> dg;

	public List<EntityViewDefinition> definitionsToExclude = new ArrayList<>();
	public EntityViewDefinition currentViewDefinition;

	/**
	 * The search string entered by the user.
	 */
	private String _searchString;

	/**
	 * The actual query string submitted to Lucene.
	 */
	private String _queryString;

	/**
	 * Indicates if we want to use the inflexer.
	 */
	private Boolean _useInflection;

	public IndexRecord currentRecord;

	public USSearchPage(WOContext context) {
		super( context );
	}

	public EntityViewDefinition currentDefinition() {
		return EntityViewDefinition.get( currentRecord.targetEntityName() );
	}

	public NSArray<IndexRecord> results() {
		return dg.displayedObjects();
	}

	public NSArray<IndexRecord> resultsFiltered() {
		NSMutableArray<IndexRecord> a = new NSMutableArray<>();

		for( IndexRecord r : results() ) {
			if( !definitionsToExclude.contains( EntityViewDefinition.get( r.targetEntityName() ) ) ) {
				a.add( r );
			}
		}

		return a;
	}

	public void setResults( NSArray<IndexRecord> value ) {
		if( dg == null ) {
			dg = new ERXDisplayGroup<>();
			dg.setObjectArray( value );
		}
	}

	public boolean hasSearched() {
		return searchString() != null;
	}

	public boolean multipleResults() {
		return results().count() > 1;
	}

	public String searchActionName() {
		StringBuilder b = new StringBuilder();
		b.append( USSearchAction.class.getSimpleName() );
		b.append( "/" );

		if( ERXApplication.isDevelopmentModeSafe() ) {
			b.append( "search" );
		}
		else {
			b.append( "searchRedirection" );
		}

		return b.toString();
	}

	public List<String> autoCompletes() {
		if( searchString() != null && searchString().length() > 1 ) {
			return Indexer.autocomplete( searchString() );
		}

		return new ArrayList<>();
	}

	public String searchString() {
		return _searchString;
	}

	public void setSearchString( String value ) {
		_searchString = value;
	}

	public String queryString() {
		return _queryString;
	}

	public void setQueryString( String value ) {
		_queryString = value;
	}

	public Boolean useInflection() {
		String s = context().request().stringFormValueForKey( "allar-ordmyndir" );
//		_useInflection = USUtilities.booleanFromObject( s );
//		return _useInflection;
		throw new RuntimeException( "Handle the parameter" );
	}

	public void setUseInflection( Boolean value ) {
		_useInflection = value;
	}

	public NSArray<EntityViewDefinition> viewDefinitions() {
		NSMutableSet<EntityViewDefinition> a = new NSMutableSet<>();

		for( IndexRecord r : results() ) {
			EntityViewDefinition vd = EntityViewDefinition.get( r.targetEntityName() );

			if( vd != null ) {
				a.addObject( vd );
			}
		}

		return a.allObjects();
	}

	public WOActionResults toggle() {
		List<EntityViewDefinition> allExceptCurrent = new ArrayList<>( viewDefinitions() );
		allExceptCurrent.remove( currentViewDefinition );

		if( definitionsToExclude.isEmpty() ) {
			definitionsToExclude = new ArrayList<>( viewDefinitions() );
			definitionsToExclude.remove( currentViewDefinition );
		}
		else if( definitionsToExclude.equals( allExceptCurrent ) ) {
			definitionsToExclude = new ArrayList<>();
		}
		else {
			if( definitionsToExclude.contains( currentViewDefinition ) ) {
				definitionsToExclude.remove( currentViewDefinition );
			}
			else {
				definitionsToExclude.add( currentViewDefinition );
			}
		}

		return null;
	}

	public String viewDefinitionClass() {
		StringBuilder b = new StringBuilder();

		b.append( "label" );

		if( !definitionsToExclude.contains( currentViewDefinition ) ) {
			b.append( " label-success" );
		}
		else {
			b.append( " label-default" );
		}

		return b.toString();
	}

	public Integer shortcut() {
		return showShortcut() ? index + 1 : null;
	}

	public String shortcutClass() {
		return showShortcut() ? "ttip" : null;
	}

	private boolean showShortcut() {
		return index < 9;
	}
}
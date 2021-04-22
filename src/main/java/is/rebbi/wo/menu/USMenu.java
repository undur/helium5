package is.rebbi.wo.menu;

import java.util.ArrayList;
import java.util.List;

import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSComparator;
import com.webobjects.foundation.NSComparator.ComparisonException;
import com.webobjects.foundation.NSMutableSet;

import is.rebbi.wo.components.admin.USLoggingConfigurationPage;
import is.rebbi.wo.components.admin.USSystemInfoPage;
import is.rebbi.wo.components.admin.USTaskRunnerPage;
import is.rebbi.wo.components.admin.USViewDefinitionOverviewPage;
import jambalaya.definitions.EntityDefinition;

public class USMenu {

	private static final String UNCATEGORIZED_CATEGORY_NAME = "Uncategorized";

	/**
	 * Menu items in the root of the menu.
	 */
	private List<USMenuItem> _rootItems = new ArrayList<>();

	/**
	 * Default menu
	 */
	private static USMenu _defaultMenu;

	public List<USMenuItem> rootItems() {
		return _rootItems;
	}

	public static USMenu defaultMenu() {
		if( _defaultMenu == null ) {
			_defaultMenu = new USMenu();
		}

		return _defaultMenu;
	}

	public USMenuItem addChild( final USMenuItem item ) {
		rootItems().add( item );
		return item;
	}

	public void addDatabaseMenuItem() {
		addChild( databaseMenuItem() );
	}

	public void addSystemMenuItem() {
		addChild( systemMenuItem() );
	}

	private static USMenuItem systemMenuItem() {
		final USMenuItem mi = USMenuItemContainer.create( "Kerfi", "fa fa-wrench sidebar-nav-icon" );
		mi.addChild( USMenuItemPage.create( "Aðgerðir", null, USTaskRunnerPage.class ) );
		mi.addChild( USMenuItemPage.create( "Birting", null, USViewDefinitionOverviewPage.class ) );
		mi.addChild( USMenuItemPage.create( "Umhverfi", null, USSystemInfoPage.class ) );
		mi.addChild( USMenuItemPage.create( "Loggar", null, USLoggingConfigurationPage.class ) );
		return mi;
	}

	private static USMenuItemPage databaseMenuItem() {
		USMenuItemPage dataTablesLevel = USMenuItemPage.create( "Gagnagrunnur", "fa fa-database sidebar-nav-icon", null );

		for( String categoryName : categoryNames() ) {
			USMenuItemPage categoryLevel = USMenuItemPage.create( categoryName, null, null );
			dataTablesLevel.addChild( categoryLevel );

			for( EntityDefinition e : viewDefinitions( categoryName ) ) {
				categoryLevel.addChild( USMenuItemEntity.create( e.name(), null ) );
			}
		}

		return dataTablesLevel;
	}

	private static List<EntityDefinition> viewDefinitions( String currentCategoryName ) {
		return new ArrayList<>();
		/*
		EOQualifier q = null;

		if( currentCategoryName.equals( UNCATEGORIZED_CATEGORY_NAME ) ) {
			q = new EOKeyValueQualifier( "categoryName", EOQualifier.QualifierOperatorEqual, null );
		}
		else {
			q = new EOKeyValueQualifier( "categoryName", EOQualifier.QualifierOperatorEqual, currentCategoryName );
		}

		List<EntityDefinition> a = filteredArrayWithQualifier( EntityDefinition.all(), q );
		Collections.sort( a, new USGenericComparator( "icelandicName", true, true ) );
		return a;
		*/
	}

	private static NSArray<String> categoryNames() {
		NSMutableSet<String> results = new NSMutableSet<>();

		for( EntityDefinition d : EntityDefinition.all() ) {
			String categoryName = d.categoryName();

			if( categoryName == null ) {
				categoryName = UNCATEGORIZED_CATEGORY_NAME;
			}

			results.addObject( categoryName );
		}

		try {
			return results.allObjects().sortedArrayUsingComparator( NSComparator.AscendingStringComparator );
		}
		catch( ComparisonException e ) {
			throw new RuntimeException( "Fucking sorting, how does it work!" );
		}
	}
}
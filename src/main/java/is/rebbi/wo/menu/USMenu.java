package is.rebbi.wo.menu;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;

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

	private static USMenuItemContainer databaseMenuItem() {
		USMenuItemContainer dataTablesLevel = USMenuItemContainer.create( "Gagnagrunnur", "fa fa-database sidebar-nav-icon" );

		for( String categoryName : categoryNames() ) {
			USMenuItemPage categoryLevel = USMenuItemPage.create( categoryName, null, null );
			dataTablesLevel.addChild( categoryLevel );

			for( EntityDefinition e : viewDefinitions( categoryName ) ) {
				categoryLevel.addChild( USMenuItemEntity.create( e.name(), null ) );
			}
		}

		return dataTablesLevel;
	}

	private static List<EntityDefinition> viewDefinitions( final String currentCategoryName ) {
		Expression e = null;

		if( currentCategoryName.equals( UNCATEGORIZED_CATEGORY_NAME ) ) {
			e = ExpressionFactory.matchExp( "categoryName", null );
		}
		else {
			e = ExpressionFactory.matchExp( "categoryName", currentCategoryName );
		}

		List<EntityDefinition> a = e.filterObjects( EntityDefinition.all() );
		Collections.sort( a, Comparator.comparing( EntityDefinition::icelandicName ) );
		return a;
	}

	private static List<String> categoryNames() {
		final Set<String> categorySet = new HashSet<>();

		for( EntityDefinition d : EntityDefinition.all() ) {
			String categoryName = d.categoryName();

			if( categoryName == null ) {
				categoryName = UNCATEGORIZED_CATEGORY_NAME;
			}

			categorySet.add( categoryName );
		}

		return new ArrayList<>( categorySet );
	}
}
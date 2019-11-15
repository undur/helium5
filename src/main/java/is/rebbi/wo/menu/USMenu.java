package is.rebbi.wo.menu;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.webobjects.eocontrol.EOKeyValueQualifier;
import com.webobjects.eocontrol.EOQualifier;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSComparator;
import com.webobjects.foundation.NSComparator.ComparisonException;
import com.webobjects.foundation.NSMutableSet;

import er.extensions.components.ERXComponent;
import is.rebbi.wo.components.admin.USLoggingConfigurationPage;
import is.rebbi.wo.components.admin.USSystemInfoPage;
import is.rebbi.wo.components.admin.USTaskRunnerPage;
import is.rebbi.wo.components.admin.USViewDefinitionOverviewPage;
import is.rebbi.wo.definitions.EntityViewDefinition;
import is.rebbi.wo.util.USGenericComparator;

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

	private static USMenuItemPage databaseMenuItem() {
		USMenuItemPage dataTablesLevel = USMenuItemPage.create( "Gagnagrunnur", "fa fa-database sidebar-nav-icon", null );

		for( String categoryName : categoryNames() ) {
			USMenuItemPage categoryLevel = USMenuItemPage.create( categoryName, null, null );
			dataTablesLevel.addChild( categoryLevel );

			for( EntityViewDefinition e : viewDefinitions( categoryName ) ) {
				categoryLevel.addChild( USMenuItemEntity.create( e.name(), null ) );
			}
		}

		return dataTablesLevel;
	}

	public static List<EntityViewDefinition> viewDefinitions( String currentCategoryName ) {
		EOQualifier q = null;

		if( currentCategoryName.equals( UNCATEGORIZED_CATEGORY_NAME ) ) {
			q = new EOKeyValueQualifier( "categoryName", EOQualifier.QualifierOperatorEqual, null );
		}
		else {
			q = new EOKeyValueQualifier( "categoryName", EOQualifier.QualifierOperatorEqual, currentCategoryName );
		}

		List<EntityViewDefinition> a = filteredArrayWithQualifier( EntityViewDefinition.all(), q );
		Collections.sort( a, new USGenericComparator( "icelandicName", true, true ) );
		return a;
	}

	private static <E> List<E> filteredArrayWithQualifier( List<E> list, EOQualifier qualifier ) {
		if( list == null ) {
			return new ArrayList<>();
		}
		if( (qualifier == null) || (qualifier._isEmpty()) ) {
			return list;
		}
		List<E> filteredList = new ArrayList<>( list.size() );
		for( Iterator<E> iterator = list.iterator() ; iterator.hasNext() ; ) {
			E object = iterator.next();
			if( qualifier.evaluateWithObject( object ) ) {
				filteredList.add( object );
			}
		}
		return filteredList;
	}

	public static NSArray<String> categoryNames() {
		NSMutableSet<String> results = new NSMutableSet<>();

		for( EntityViewDefinition d : EntityViewDefinition.all() ) {
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

	public static void addSystemMenuItems() {
		defaultMenu().addChild( databaseMenuItem() );
		defaultMenu().addChild( systemMenuItem() );
	}

	private static USMenuItemPage systemMenuItem() {
		USMenuItemPage systemItem = USMenuItemPage.create( "Kerfi", "fa fa-wrench sidebar-nav-icon", null );
		systemItem.addChild( USMenuItemPage.create( "Aðgerðir", null, USTaskRunnerPage.class ) );
		systemItem.addChild( USMenuItemPage.create( "Birting", null, USViewDefinitionOverviewPage.class ) );
		systemItem.addChild( USMenuItemPage.create( "Umhverfi", null, USSystemInfoPage.class ) );
		systemItem.addChild( USMenuItemPage.create( "Loggar", null, USLoggingConfigurationPage.class ) );
		return systemItem;
	}

	public USMenuItem addAtTop( USMenuItem item ) {
		rootItems().add( 0, item );
		return item;
	}

	@Deprecated
	public USMenuItem addAtBottom( USMenuItem item ) {
		rootItems().add( item );
		return item;
	}

	public USMenuItem addChild( USMenuItem item ) {
		rootItems().add( item );
		return item;
	}

	@Deprecated
	public USMenuItem addAtBottom( String name, String iconClasses, Class<? extends ERXComponent> pageClass ) {
		return addAtBottom( USMenuItemPage.create( name, iconClasses, pageClass ) );
	}

	public void clear() {
		_rootItems = new ArrayList<>();
	}
}
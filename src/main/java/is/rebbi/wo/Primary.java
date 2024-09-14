package is.rebbi.wo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import is.rebbi.wo.components.admin.USLoggingConfigurationPage;
import is.rebbi.wo.components.admin.USSystemInfoPage;
import is.rebbi.wo.components.admin.USTaskRunnerPage;
import is.rebbi.wo.components.admin.USViewDefinitionOverviewPage;
import is.rebbi.wo.menu.USMenu;
import is.rebbi.wo.menu.USMenuItem;
import is.rebbi.wo.menu.USMenuItemContainer;
import is.rebbi.wo.menu.USMenuItemEntity;
import is.rebbi.wo.menu.USMenuItemPage;
import is.rebbi.wo.util.SessionManager;
import is.rebbi.wo.util.SoftUser;
import jambalaya.definitions.EntityDefinition;

public class Primary {

	private static final Logger logger = LoggerFactory.getLogger( Primary.class );

	private static final String UNCATEGORIZED_CATEGORY_NAME = "Uncategorized";

	static {
		System.out.println( "==== Initializing Helium ====" );
		logger.info( "Initializing Helium" );
		SoftUser.Manager.register();
		SessionManager.register();
	}

	/**
	 * Default Helium menu
	 */
	private static USMenu _heliumMenu;

	public static USMenu heliumMenu() {
		if( _heliumMenu == null ) {
			_heliumMenu = new USMenu();
		}

		return _heliumMenu;
	}

	public static String frameworkBundleName() {
		return "helium5";
	}

	public static USMenuItem systemMenuItem() {
		final USMenuItem mi = USMenuItemContainer.create( "System", "fa fa-wrench sidebar-nav-icon" );
		mi.addChild( USMenuItemPage.create( "Tasks", null, USTaskRunnerPage.class ) );
		mi.addChild( USMenuItemPage.create( "View definitions", null, USViewDefinitionOverviewPage.class ) );
		mi.addChild( USMenuItemPage.create( "Environment", null, USSystemInfoPage.class ) );
		mi.addChild( USMenuItemPage.create( "Logging", null, USLoggingConfigurationPage.class ) );
		return mi;
	}

	public static USMenuItemContainer databaseMenuItem() {
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
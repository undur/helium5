package is.rebbi.wo.util;

import org.apache.cayenne.DataObject;
import org.apache.cayenne.ObjectContext;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WOContext;

import er.extensions.appserver.ERXApplication;
import er.extensions.appserver.ERXWOContext;
import is.rebbi.wo.components.USEditPageGenericCayenne;
import is.rebbi.wo.components.USEditWrapper;
import is.rebbi.wo.components.USListPageCayenne;
import is.rebbi.wo.components.USViewPage;
import is.rebbi.wo.components.USViewPageGenericCayenne;
import is.rebbi.wo.components.USViewWrapper;
import is.rebbi.wo.definitions.EntityViewDefinition;
import is.rebbi.wo.interfaces.HasSelectedObjectPage;

/**
 * Central class for the inspection stuff.
 */

public class Inspection {

	/**
	 * @return The given object opened in the default view page.
	 */
	public static WOActionResults inspectObjectInContext( Object object, WOContext context ) {
		Class<? extends HasSelectedObjectPage> componentClass = EntityViewDefinition.viewComponentClass( object.getClass() );

		if( componentClass != null ) {
			return inspectObjectInContextUsingComponent( object, context, componentClass );
		}

		return inspectObjectInContextUsingGenericComponent( object, context );
	}

	/**
	 * @return The given object opened in the default edit page.
	 */
	public static WOActionResults editObjectInContext( Object object, WOContext context ) {
		Class<? extends USViewPage> componentClass = EntityViewDefinition.editComponentClass( object.getClass() );

		if( componentClass != null ) {
			return editObjectInContextUsingComponent( object, context, componentClass );
		}

		return editObjectInContextUsingGenericComponent( object, context );
	}

	public static WOActionResults openListPage( Class entityClass ) {
		EntityViewDefinition viewDefinition = EntityViewDefinition.get( entityClass );
		USListPageCayenne nextPage = ERXApplication.erxApplication().pageWithName( USListPageCayenne.class );
		nextPage.setSelectedViewDefinition( viewDefinition );
		return nextPage;
	}

	/**
	 * Takes an object of a supported type and returns an inspection page for it.
	 */
	public static WOActionResults inspectObjectInContextUsingComponent( Object object, WOContext context, Class<? extends HasSelectedObjectPage> componentClass ) {
		return openObjectUsingWrapperAndComponent( object, componentClass, USViewWrapper.class );
	}

	public static WOActionResults editObjectInContextUsingComponent( Object object, WOContext context, Class<? extends HasSelectedObjectPage> componentClass ) {
		return openObjectUsingWrapperAndComponent( object, componentClass, USEditWrapper.class );
	}

	private static WOActionResults openObjectUsingWrapperAndComponent( Object object, Class<? extends HasSelectedObjectPage> componentClass, Class<? extends USViewWrapper> wrapperClass ) {
		WOContext context = ERXWOContext.currentContext();
		USViewWrapper nextPage = ERXApplication.erxApplication().pageWithName( wrapperClass, context );
		nextPage.setDisplayComponentName( componentClass.getSimpleName() );
		nextPage.setSelectedObject( object );
		nextPage.setCallingComponent( context.page() );
		return nextPage;
	}

	public static <A extends DataObject> WOActionResults createAndEditObject( ObjectContext ec, String entityName, WOContext context ) {
		Class<?> javaClass = ec.getEntityResolver().getObjEntity( entityName ).getJavaClass();
		DataObject eo = (DataObject)ec.newObject( javaClass );
		// ec.processRecentChanges();
		return editObjectInContext( eo, context );
	}

	public static WOActionResults editObjectInContextUsingGenericComponent( Object selectedObject, WOContext context ) {
		Class<? extends HasSelectedObjectPage> pageClass = null;

		if( selectedObject instanceof DataObject ) {
			pageClass = USEditPageGenericCayenne.class;
		}

		return editObjectInContextUsingComponent( selectedObject, context, pageClass );
	}

	public static WOActionResults inspectObjectInContextUsingGenericComponent( Object selectedObject, WOContext context ) {
		Class<? extends HasSelectedObjectPage> pageClass = null;

		if( selectedObject instanceof DataObject ) {
			pageClass = USViewPageGenericCayenne.class;
		}

		return inspectObjectInContextUsingComponent( selectedObject, context, pageClass );
	}
}
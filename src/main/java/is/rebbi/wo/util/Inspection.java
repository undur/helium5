package is.rebbi.wo.util;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

import org.apache.cayenne.DataObject;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WOContext;

import er.extensions.appserver.ERXApplication;
import er.extensions.appserver.ERXWOContext;
import is.rebbi.wo.components.USViewPageGeneric;
import is.rebbi.wo.components.USViewWrapper;
import is.rebbi.wo.components.admin.USEditPageGeneric;
import is.rebbi.wo.components.admin.USEditWrapper;
import is.rebbi.wo.components.admin.USListPageEdit;
import is.rebbi.wo.interfaces.HasSelectedObjectPage;

public class Inspection {

	public static class InspectionRoute {

		private static final Map<Class, InspectionRoute> _inspectionRoutes = new HashMap<>();

		public static final Map<Class, InspectionRoute> inspectionRoutes() {
			return _inspectionRoutes;
		}

		public static void add( Class entityClass, String urlPrefix, BiFunction<DataObject, WOContext, WOActionResults> viewFunction ) {
			final InspectionRoute ir = new InspectionRoute();
			ir.setEntityClass( entityClass );
			ir.setUrlPrefix( urlPrefix );
			ir._viewFunction = viewFunction;
			inspectionRoutes().put( entityClass, ir );
		}

		public static void add( Class entityClass, String urlPrefix, Class viewComponentClass, Class editComponentClass ) {
			final InspectionRoute ir = new InspectionRoute();
			ir.setEntityClass( entityClass );
			ir.setUrlPrefix( urlPrefix );
			ir.setViewComponentClass( viewComponentClass );
			ir.setEditComponentClass( editComponentClass );
			inspectionRoutes().put( entityClass, ir );
		}

		private Class _entityClass;

		/**
		 * Prefix used in URLs to access objects of this type.
		 */
		private String _urlPrefix;

		/**
		 * Class of component used to view objects if this type.
		 */
		private Class _viewComponentClass;

		/**
		 * Class of component used to view objects if this type.
		 */
		private BiFunction<DataObject, WOContext, WOActionResults> _viewFunction;

		/**
		 * Class of component used to edit objects if this type.
		 */
		private Class _editComponentClass;

		public Class entityClass() {
			return _entityClass;
		}

		public void setEntityClass( final Class value ) {
			_entityClass = value;
		}

		public String urlPrefix() {
			return _urlPrefix;
		}

		public void setUrlPrefix( String value ) {
			_urlPrefix = value;
		}

		public Class viewComponentClass() {
			return _viewComponentClass;
		}

		public void setViewComponentClass( Class value ) {
			_viewComponentClass = value;
		}

		public Class editComponentClass() {
			return _editComponentClass;
		}

		public void setEditComponentClass( Class value ) {
			_editComponentClass = value;
		}

		/**
		 * @return The definition for the given URL prefix.
		 */
		public static InspectionRoute forURLPrefix( String urlPrefix ) {
			for( InspectionRoute o : inspectionRoutes().values() ) {
				if( urlPrefix.equals( o.urlPrefix() ) ) {
					return o;
				}
			}

			return null;
		}

		/**
		 * @return The definition for the given URL prefix.
		 */
		public static InspectionRoute forEntityName( String entityName ) {
			for( InspectionRoute o : inspectionRoutes().values() ) {
				if( entityName.equals( o.entityClass().getSimpleName() ) ) {
					return o;
				}
			}

			return null;
			//			throw new IllegalArgumentException( "EntityName not found: " +  entityName );
		}
	}

	/**
	 * @return The given object opened in the default view page.
	 */
	public static WOActionResults inspectObjectInContext( Object object, WOContext context ) {
		final InspectionRoute ir = InspectionRoute.inspectionRoutes().get( object.getClass() );

		if( ir._viewFunction != null ) {
			return ir._viewFunction.apply( (DataObject)object, context );
		}

		final Class<? extends HasSelectedObjectPage> componentClass = ir.viewComponentClass();

		if( componentClass == null ) {
			throw new RuntimeException( "This object type has no associated view component" );
		}

		return inspectObjectInContextUsingComponent( object, context, componentClass );
	}

	/**
	 * @return The given object opened in the default edit page.
	 */
	public static WOActionResults editObjectInContext( Object object, WOContext context ) {
		final InspectionRoute ir = InspectionRoute.inspectionRoutes().get( object.getClass() );
		Class<? extends HasSelectedObjectPage> componentClass = ir.editComponentClass();

		// FIXME. I want to stop here, like in the viewing route
		if( componentClass == null ) {
			componentClass = USEditPageGeneric.class;
		}

		return editObjectInContextUsingComponent( object, context, componentClass );
	}

	public static WOActionResults openListPage( Class entityClass ) {
		USListPageEdit nextPage = ERXApplication.erxApplication().pageWithName( USListPageEdit.class );
		nextPage.setEntityClass( entityClass );
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

	public static WOActionResults editObjectInContextUsingGenericComponent( Object selectedObject, WOContext context ) {
		Class<? extends HasSelectedObjectPage> pageClass = null;

		if( selectedObject instanceof DataObject ) {
			pageClass = USEditPageGeneric.class;
		}

		return editObjectInContextUsingComponent( selectedObject, context, pageClass );
	}

	public static WOActionResults inspectObjectInContextUsingGenericComponent( Object selectedObject, WOContext context ) {
		Class<? extends HasSelectedObjectPage> pageClass = null;

		if( selectedObject instanceof DataObject ) {
			pageClass = USViewPageGeneric.class;
		}

		return inspectObjectInContextUsingComponent( selectedObject, context, pageClass );
	}
}
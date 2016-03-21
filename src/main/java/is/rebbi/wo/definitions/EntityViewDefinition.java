package is.rebbi.wo.definitions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.cayenne.DataObject;
import org.apache.cayenne.access.DataDomain;
import org.apache.cayenne.configuration.server.ServerRuntime;
import org.apache.cayenne.map.EntityResolver;
import org.apache.cayenne.map.ObjEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webobjects.eoaccess.EOAttribute;
import com.webobjects.eoaccess.EOEntity;
import com.webobjects.eoaccess.EOModel;
import com.webobjects.eoaccess.EOModelGroup;
import com.webobjects.eocontrol.EOEnterpriseObject;
import com.webobjects.eocontrol.EOSortOrdering;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSMutableArray;

import er.extensions.appserver.ERXApplication;
import er.extensions.components.ERXComponent;
import is.rebbi.wo.cayenne.USCayenne;
import is.rebbi.wo.interfaces.HasSelectedObjectPage;
import is.rebbi.wo.util.USCRUDUtilities;
import is.rebbi.wo.util.USEOUtilities;
import is.rebbi.wo.util.USGenericComparator;

/**
 * Defines the viewing of a certain entity.
 */

public class EntityViewDefinition<E, T extends HasSelectedObjectPage<E>, V extends HasSelectedObjectPage<E>> {

	private static final Logger logger = LoggerFactory.getLogger( EntityViewDefinition.class );

	/**
	 * All registered definitions.
	 */
	private static Map<String, EntityViewDefinition> _definitions;

	/**
	 * The attributes of this entity
	 */
	private Map<String, AttributeViewDefinition> _attributeViewDefinitions = new HashMap<>();

	/**
	 * Name of the entity this view definition defines.
	 */
	private String _name;

	/**
	 * Icelandic name.
	 */
	private String _icelandicName;

	private Class<? extends ERXComponent> _searchResultComponentClass;

	/**
	 * Icelandic plural name.
	 */
	private String _icelandicNamePlural;

	/**
	 * Icelandic description.
	 */
	private String _text;

	/**
	 * Class of component used to view objects if this type.
	 */
	private Class<T> _viewComponentClass;

	/**
	 * Class of component used to edit objects if this type.
	 */
	private Class<V> _editComponentClass;

	/**
	 * Prefix used in URLs to access objects of this type.
	 */
	private String _urlPrefix;

	/**
	 * Filename of icon used when showing objects of this type.
	 */
	private String _iconFileName;

	/**
	 * Filename of the icon used when this type of object is displayed.
	 */
	private boolean _showInList;

	/**
	 * Name of the category for this entity view definition.
	 */
	private String _categoryName;

	/**
	 * Cached list of attributes to show.
	 */
	private List<AttributeViewDefinition> _attributesToShow;

	/**
	 * The class that represents this entity. Can be null if the entity does not have a corresponding class.
	 */
	private Class<E> _entityClass;

	/**
	 * List of all objects that provide the system with EntityViewDefinitions.
	 */
	private static List<ProvidesEntityViewDefinitions> _entityViewDefinitionProviders;

	private EntityViewDefinition() {}

	private static List<ProvidesEntityViewDefinitions> entityViewDefinitionProviders() {
		if( _entityViewDefinitionProviders == null ) {
			_entityViewDefinitionProviders = new ArrayList<>();

			boolean appProvides = ERXApplication.application() instanceof ProvidesEntityViewDefinitions;

			if( appProvides ) {
				_entityViewDefinitionProviders.add( (ProvidesEntityViewDefinitions)ERXApplication.application() );
			}
		}

		Collections.sort( _entityViewDefinitionProviders, new USGenericComparator( "priority", true, false ) );

		return _entityViewDefinitionProviders;
	}

	public static void registerEntityViewDefinitionProvider( ProvidesEntityViewDefinitions provider ) {
		entityViewDefinitionProviders().add( provider );
	}

	/**
	 * @return True if the given entity is a Cayenne entity.
	 */
	public boolean isCayenneEntity() {
		return EOModelGroup.defaultGroup().entityNamed( name() ) == null;
	}

	public AttributeViewDefinition addAttributeViewDefinition( AttributeViewDefinition a ) {
		if( a.name() != null ) {
			_attributeViewDefinitions.put( a.name(), a );
		}

		return a;
	}

	private static Map<String, EntityViewDefinition> definitions() {
		if( _definitions == null ) {
			reloadAllDefinitions();
		}

		return _definitions;
	}

	public static void reloadAllDefinitions() {
		_definitions = new HashMap<>();

		for( ProvidesEntityViewDefinitions provider : entityViewDefinitionProviders() ) {
			logger.info( "Loading Entity View Definitions from {} - priority {}", provider.getClass(), provider.priority() );
			for( EntityViewDefinition e : provider.entityViewDefinitions() ) {
				e.register();
			}
		}
	}

	private static Class<?> classForEntity( String entityName ) {
		Class<?> entityClass = null;

		EOEntity eoEntity = EOModelGroup.defaultGroup().entityNamed( entityName );

		if( eoEntity != null ) {
			entityClass = USEOUtilities.classForEntityNamed( entityName );
		}
		else {
			ServerRuntime serverRuntime = USCayenne.serverRuntime();
			DataDomain dataDomain = serverRuntime.getDataDomain();
			EntityResolver entityResolver = dataDomain.getEntityResolver();
			ObjEntity entity = entityResolver.getObjEntity( entityName );

			if( entity != null ) {
				entityClass = entity.getJavaClass();
			}
		}

		return entityClass;
	}

	/**
	 * Define view definition for an entity.
	 */
	public static EntityViewDefinition create( String name, String icelandicName, String icelandicNamePlural, String categoryName, String text, String urlPrefix, String iconFileName, boolean showInLists, Class viewComponentClass, Class editComponentClass ) {
		EntityViewDefinition e = new EntityViewDefinition();
		e.setEntityClass( classForEntity( name ) );
		e.setName( name );
		e.setIcelandicName( icelandicName );
		e.setIcelandicNamePlural( icelandicNamePlural );
		e.setCategoryName( categoryName );
		e.setText( text );
		e.setUrlPrefix( urlPrefix );
		e.setIconFileName( iconFileName );
		e.setShowInList( showInLists );
		e.setViewComponentClass( viewComponentClass );
		e.setEditComponentClass( editComponentClass );

		return e;
	}

	/**
	 * Define view definition for an entity.
	 */
	public static EntityViewDefinition create( Class<?> entityClass, String icelandicName, String icelandicNamePlural, String categoryName, String text, String urlPrefix, String iconFileName, boolean showInLists, Class viewComponentClass, Class editComponentClass ) {
		EntityViewDefinition e = new EntityViewDefinition();
		e.setEntityClass( entityClass );

		String name = null;

		if( DataObject.class.isAssignableFrom( entityClass ) ) {
			name = USCayenne.serverRuntime().getDataDomain().getEntityResolver().getObjEntity( entityClass ).getName();
		}
		else if( EOEnterpriseObject.class.isAssignableFrom( entityClass ) ) {
			EOEntity entity = EOModelGroup.defaultGroup().entityNamed( entityClass.getSimpleName() );
			name = entity.name();
		}
		else {
			throw new IllegalArgumentException( "Sorry. I only support EO and Cayenne classes, not: " + entityClass );
		}

		e.setName( name );
		e.setIcelandicName( icelandicName );
		e.setIcelandicNamePlural( icelandicNamePlural );
		e.setCategoryName( categoryName );
		e.setText( text );
		e.setUrlPrefix( urlPrefix );
		e.setIconFileName( iconFileName );
		e.setShowInList( showInLists );
		e.setViewComponentClass( viewComponentClass );
		e.setEditComponentClass( editComponentClass );
		return e;
	}

	/**
	 * Define view definition for an entity.
	 */
	private void register() {
		logger.info( "Defining view for: {}", name() );

		EntityViewDefinition e = get( name() );

		if( entityClass() != null ) {
			e.setEntityClass( entityClass() );
		}

		if( icelandicName() != null ) {
			e.setIcelandicName( icelandicName() );
		}

		if( icelandicNamePlural() != null ) {
			e.setIcelandicNamePlural( icelandicNamePlural() );
		}

		if( categoryName() != null ) {
			e.setCategoryName( categoryName() );
		}

		if( text() != null ) {
			e.setText( text() );
		}

		if( urlPrefix() != null ) {
			e.setUrlPrefix( urlPrefix() );
		}

		if( iconFileName() != null ) {
			e.setIconFileName( iconFileName() );
		}

		if( e.showInList() != showInList() ) {
			e.setShowInList( showInList() );
		}

		if( viewComponentClass() != null ) {
			e.setViewComponentClass( viewComponentClass() );
		}

		if( editComponentClass() != null ) {
			e.setEditComponentClass( editComponentClass() );
		}

		if( searchResultComponentClass() != null ) {
			e.setSearchResultComponentClass( searchResultComponentClass() );
		}

		if( _attributeViewDefinitions != null ) {
			e._attributeViewDefinitions = _attributeViewDefinitions;
		}
	}

	public static EntityViewDefinition forObject( Object object ) {
		return EntityViewDefinition.get( USCRUDUtilities.entityNameFromObject( object ) );
	}

	/**
	 * @return The definition for the given entityName
	 */
	public static EntityViewDefinition get( String entityName ) {

		if( entityName == null ) {
			throw new IllegalArgumentException( "[entityName] cannot be null" );
		}

		EntityViewDefinition e = definitions().get( entityName );

		if( e == null ) {
			e = new EntityViewDefinition();
			e.setName( entityName );
			definitions().put( entityName, e );
		}

		return e;
	}

	/**
	 * @return The definition for the given class
	 */
	public static <T> EntityViewDefinition get( Class<T> entityClass ) {
		return get( entityClass.getSimpleName() );
	}

	public String name() {
		return _name;
	}

	public void setName( String value ) {
		_name = value;
	}

	public Class<E> entityClass() {
		if( _entityClass == null ) {
			_entityClass = (Class<E>)classForEntity( name() );
		}

		return _entityClass;
	}

	public void setEntityClass( Class<E> value ) {
		_entityClass = value;
	}

	public String icelandicName() {

		if( _icelandicName == null ) {
			_icelandicName = name();
		}

		return _icelandicName;
	}

	public void setIcelandicName( String value ) {
		_icelandicName = value;
	}

	public String icelandicNamePlural() {
		if( _icelandicNamePlural == null ) {
			_icelandicNamePlural = icelandicName();
		}

		return _icelandicNamePlural;
	}

	public void setIcelandicNamePlural( String value ) {
		_icelandicNamePlural = value;
	}

	public Class<T> viewComponentClass() {
		return _viewComponentClass;
	}

	public void setViewComponentClass( Class<T> value ) {
		_viewComponentClass = value;
	}

	public Class<V> editComponentClass() {
		return _editComponentClass;
	}

	public void setEditComponentClass( Class<V> value ) {
		_editComponentClass = value;
	}

	public Class<? extends ERXComponent> searchResultComponentClass() {
		return _searchResultComponentClass;
	}

	public void setSearchResultComponentClass( Class<? extends ERXComponent> clazz ) {
		_searchResultComponentClass = clazz;
	}

	public String urlPrefix() {
		if( _urlPrefix == null ) {
			_urlPrefix = name();
		}

		return _urlPrefix;
	}

	public void setUrlPrefix( String value ) {
		_urlPrefix = value;
	}

	public String iconFileName() {
		return _iconFileName;
	}

	public void setIconFileName( String value ) {
		_iconFileName = value;
	}

	public boolean showInList() {
		return _showInList;
	}

	public void setShowInList( boolean showInList ) {
		this._showInList = showInList;
	}

	public String categoryName() {
		return _categoryName;
	}

	public void setCategoryName( String value ) {
		_categoryName = value;
	}

	public String text() {
		return _text;
	}

	public void setText( String value ) {
		_text = value;
	}

	public EOEntity entity() {
		return EOModelGroup.defaultGroup().entityNamed( name() );
	}

	public AttributeViewDefinition attributeNamed( String attributeName ) {

		if( attributeName == null ) {
			return null;
		}

		AttributeViewDefinition result = _attributeViewDefinitions.get( attributeName );

		if( result == null ) {
			result = new AttributeViewDefinition();
			result.setName( attributeName );
			addAttributeViewDefinition( result );
		}

		return result;
	}

	public List<AttributeViewDefinition> attributes() {
		return new ArrayList<>( _attributeViewDefinitions.values() );
	}

	public List<AttributeViewDefinition> attributesToShow() {
		if( _attributesToShow == null ) {
			_attributesToShow = new ArrayList<>( _attributeViewDefinitions.values() );
			_attributesToShow = _attributesToShow.stream().filter( AttributeViewDefinition::show ).collect( Collectors.toList() );
			Collections.sort( _attributesToShow, new USGenericComparator( "name", true, false ) );
			Collections.sort( _attributesToShow, new USGenericComparator( "sortOrder", true, false ) );
		}

		return _attributesToShow;
	}

	/**
	 * @return Default sort orderings based on attributes shown.
	 */
	public NSArray<EOSortOrdering> defaultSortOrderings() {
		NSArray<EOSortOrdering> a = new NSMutableArray<>();

		for( AttributeViewDefinition attributeDefinition : attributesToShow() ) {
			EOAttribute attribute = entity().attributeNamed( attributeDefinition.name() );

			if( attribute != null ) {
				EOSortOrdering s;

				if( USEOUtilities.attributeIsString( attribute ) ) {
					s = new EOSortOrdering( attributeDefinition.name(), EOSortOrdering.CompareCaseInsensitiveAscending );
				}
				else {
					s = new EOSortOrdering( attributeDefinition.name(), EOSortOrdering.CompareAscending );
				}

				a.add( s );
			}
		}

		return a;
	}

	public static List<EntityViewDefinition> all() {
		List<EntityViewDefinition> all = new ArrayList<>();

		for( String entityName : allEOFEntityNames() ) {
			all.add( EntityViewDefinition.get( entityName ) );
		}

		for( String entityName : allCayenneEntityNames() ) {
			all.add( EntityViewDefinition.get( entityName ) );
		}

		Collections.sort( all, new USGenericComparator( "icelandicName", true, true ) );

		return all;
	}

	private static List<String> allCayenneEntityNames() {
		// FIXME: MAXIMUM UGLYNESS!
		ServerRuntime serverRuntime = USCayenne.serverRuntime();

		if( serverRuntime == null ) {
			return new ArrayList<>();
		}

		return serverRuntime.getDataDomain().getEntityResolver().getObjEntities().stream().map( ObjEntity::getName ).collect( Collectors.toList() );
	}

	private static List<String> allEOFEntityNames() {
		List<String> allEntityNames = new ArrayList<>();

		for( EOModel model : EOModelGroup.defaultGroup().models() ) {
			for( EOEntity entity : model.entities() ) {
				if( !entity.name().startsWith( "EO" ) ) {
					allEntityNames.add( entity.name() );
				}
			}
		}

		return allEntityNames;
	}

	/**
	 * @return Icelandic name of object associated with the named entity.
	 */
	public static String icelandicName( String entityName ) {
		EntityViewDefinition type = get( entityName );

		if( type != null ) {
			String name = type.icelandicName();

			if( name != null ) {
				return name;
			}
		}

		return null;
	}

	/**
	 * @return Icelandic name of object associated with the named entity.
	 */
	public static String icelandicNamePlural( String entityName ) {
		EntityViewDefinition type = get( entityName );

		if( type != null ) {
			String name = type.icelandicNamePlural();

			if( name != null ) {
				return name;
			}
		}

		return null;
	}

	/**
	 * @return The component class used to view the given type of object.
	 */
	public static Class viewComponentClass( Class entityClass ) {
		EntityViewDefinition type = get( entityClass );

		if( type != null ) {
			return type.viewComponentClass();
		}

		return null;
	}

	/**
	 * @return The component class used to edit the given type of object.
	 */
	public static Class editComponentClass( Class entityClass ) {
		EntityViewDefinition type = get( entityClass );

		if( type != null ) {
			return type.editComponentClass();
		}

		return null;
	}

	/**
	 * @return The definition for the given URL prefix.
	 */
	public static EntityViewDefinition definitionForURLPrefix( String urlPrefix ) {
		for( EntityViewDefinition o : definitions().values() ) {
			if( urlPrefix.equals( o.urlPrefix() ) ) {
				return o;
			}
		}

		return null;
	}

	public static void invalidateCache() {
		logger.info( "Invalidating the EntityViewDefinition cache" );
		_definitions = null;
	}

	public static List<EntityViewDefinition> typesToShowInList() {
		List<EntityViewDefinition> results = new ArrayList<>();

		for( EntityViewDefinition t : EntityViewDefinition.definitions().values() ) {
			if( t.showInList() ) {
				results.add( t );
			}
		}

		Collections.sort( results, new USGenericComparator( "icelandicName", true, true ) );

		return results;
	}

	@Override
	public String toString() {
		return "EntityViewDefinition [_attributeViewDefinitions=" + _attributeViewDefinitions + ", _name=" + _name + ", _icelandicName=" + _icelandicName + ", _searchResultComponentClass=" + _searchResultComponentClass + ", _icelandicNamePlural=" + _icelandicNamePlural + ", _text=" + _text + ", _viewComponentClass=" + _viewComponentClass + ", _editComponentClass=" + _editComponentClass + ", _urlPrefix=" + _urlPrefix + ", _iconFileName=" + _iconFileName + ", _showInList=" + _showInList + ", _categoryName=" + _categoryName + ", _attributesToShow=" + _attributesToShow + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((_attributeViewDefinitions == null) ? 0 : _attributeViewDefinitions.hashCode());
		result = prime * result + ((_attributesToShow == null) ? 0 : _attributesToShow.hashCode());
		result = prime * result + ((_categoryName == null) ? 0 : _categoryName.hashCode());
		result = prime * result + ((_icelandicName == null) ? 0 : _icelandicName.hashCode());
		result = prime * result + ((_icelandicNamePlural == null) ? 0 : _icelandicNamePlural.hashCode());
		result = prime * result + ((_iconFileName == null) ? 0 : _iconFileName.hashCode());
		result = prime * result + ((_name == null) ? 0 : _name.hashCode());
		result = prime * result + (_showInList ? 1231 : 1237);
		result = prime * result + ((_text == null) ? 0 : _text.hashCode());
		result = prime * result + ((_urlPrefix == null) ? 0 : _urlPrefix.hashCode());
		return result;
	}

	@Override
	public boolean equals( Object obj ) {
		if( this == obj ) {
			return true;
		}
		if( obj == null ) {
			return false;
		}
		if( getClass() != obj.getClass() ) {
			return false;
		}
		EntityViewDefinition other = (EntityViewDefinition)obj;
		if( _attributeViewDefinitions == null ) {
			if( other._attributeViewDefinitions != null ) {
				return false;
			}
		}
		else if( !_attributeViewDefinitions.equals( other._attributeViewDefinitions ) ) {
			return false;
		}
		if( _attributesToShow == null ) {
			if( other._attributesToShow != null ) {
				return false;
			}
		}
		else if( !_attributesToShow.equals( other._attributesToShow ) ) {
			return false;
		}
		if( _categoryName == null ) {
			if( other._categoryName != null ) {
				return false;
			}
		}
		else if( !_categoryName.equals( other._categoryName ) ) {
			return false;
		}
		if( _icelandicName == null ) {
			if( other._icelandicName != null ) {
				return false;
			}
		}
		else if( !_icelandicName.equals( other._icelandicName ) ) {
			return false;
		}
		if( _icelandicNamePlural == null ) {
			if( other._icelandicNamePlural != null ) {
				return false;
			}
		}
		else if( !_icelandicNamePlural.equals( other._icelandicNamePlural ) ) {
			return false;
		}
		if( _iconFileName == null ) {
			if( other._iconFileName != null ) {
				return false;
			}
		}
		else if( !_iconFileName.equals( other._iconFileName ) ) {
			return false;
		}
		if( _name == null ) {
			if( other._name != null ) {
				return false;
			}
		}
		else if( !_name.equals( other._name ) ) {
			return false;
		}
		if( _showInList != other._showInList ) {
			return false;
		}
		if( _text == null ) {
			if( other._text != null ) {
				return false;
			}
		}
		else if( !_text.equals( other._text ) ) {
			return false;
		}
		if( _urlPrefix == null ) {
			if( other._urlPrefix != null ) {
				return false;
			}
		}
		else if( !_urlPrefix.equals( other._urlPrefix ) ) {
			return false;
		}
		return true;
	}
}
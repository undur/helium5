package is.rebbi.wo.definitions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.cayenne.access.DataDomain;
import org.apache.cayenne.configuration.server.ServerRuntime;
import org.apache.cayenne.map.EntityResolver;
import org.apache.cayenne.map.ObjEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import er.extensions.appserver.ERXApplication;
import is.rebbi.wo.interfaces.HasSelectedObjectPage;
import is.rebbi.wo.util.USGenericComparator;
import jambalaya.Jambalaya;

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
	private Map<String, AttributeViewDefinition> _attributeViewDefinitions;

	/**
	 * Name of the entity this view definition defines.
	 */
	private String _name;

	/**
	 * Icelandic name.
	 */
	private String _icelandicName;

	/**
	 * Icelandic plural name.
	 */
	private String _icelandicNamePlural;

	/**
	 * Icelandic description.
	 */
	private String _text;

	/**
	 * Prefix used in URLs to access objects of this type.
	 */
	private String _urlPrefix;

	/**
	 * Filename of icon used when showing objects of this type.
	 */
	private String _iconFileName;

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
	 * Class of component used to view objects if this type.
	 */
	private Class<T> _viewComponentClass;

	/**
	 * Class of component used to edit objects if this type.
	 */
	private Class<V> _editComponentClass;

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
		invalidateCache();
	}

	public AttributeViewDefinition addAttributeViewDefinition( AttributeViewDefinition a ) {
		if( a.name() != null ) {
			attributeViewDefinitions().put( a.name(), a );
		}

		return a;
	}

	private static Map<String, EntityViewDefinition> definitions() {
		if( _definitions == null ) {
			_definitions = new HashMap<>();

			for( ProvidesEntityViewDefinitions provider : entityViewDefinitionProviders() ) {
				logger.info( "Loading Entity View Definitions from {} - priority {}", provider.getClass(), provider.priority() );
				for( EntityViewDefinition e : provider.entityViewDefinitions() ) {
					e.register();
				}
			}
		}

		return _definitions;
	}

	private static Class<?> classForEntity( String entityName ) {
		Class<?> entityClass = null;

		ServerRuntime serverRuntime = Jambalaya.serverRuntime();
		DataDomain dataDomain = serverRuntime.getDataDomain();
		EntityResolver entityResolver = dataDomain.getEntityResolver();
		ObjEntity entity = entityResolver.getObjEntity( entityName );

		if( entity != null ) {
			entityClass = entity.getJavaClass();
		}

		return entityClass;
	}

	/**
	 * Define view definition for an entity.
	 */
	public static EntityViewDefinition create( Class<?> entityClass, String icelandicName, String icelandicNamePlural, String categoryName, String text, String urlPrefix, String iconFileName, boolean showInLists, Class viewComponentClass, Class editComponentClass ) {
		EntityViewDefinition e = new EntityViewDefinition();
		e.setEntityClass( entityClass );

		String name = Jambalaya.serverRuntime().getDataDomain().getEntityResolver().getObjEntity( entityClass ).getName();

		e.setName( name );
		e.setIcelandicName( icelandicName );
		e.setIcelandicNamePlural( icelandicNamePlural );
		e.setCategoryName( categoryName );
		e.setText( text );
		e.setUrlPrefix( urlPrefix );
		e.setIconFileName( iconFileName );
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

		if( viewComponentClass() != null ) {
			e.setViewComponentClass( viewComponentClass() );
		}

		if( editComponentClass() != null ) {
			e.setEditComponentClass( editComponentClass() );
		}

		if( _attributeViewDefinitions != null ) {
			e._attributeViewDefinitions = _attributeViewDefinitions;
		}
	}

	/**
	 * @return The definition for the given class
	 */
	public static <T> EntityViewDefinition get( Class<T> entityClass ) {
		return get( entityClass.getSimpleName() );
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

	public String text() {
		return _text;
	}

	public void setText( String value ) {
		_text = value;
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

	public String categoryName() {
		return _categoryName;
	}

	public void setCategoryName( String value ) {
		_categoryName = value;
	}

	public ObjEntity entity() {
		return Jambalaya.newContext().getEntityResolver().getObjEntity( name() );
	}

	public AttributeViewDefinition attributeNamed( String attributeName ) {

		if( attributeName == null ) {
			return null;
		}

		AttributeViewDefinition result = attributeViewDefinitions().get( attributeName );

		if( result == null ) {
			result = new AttributeViewDefinition();
			result.setName( attributeName );
			addAttributeViewDefinition( result );
		}

		return result;
	}

	public List<AttributeViewDefinition> attributes() {
		return new ArrayList<>( attributeViewDefinitions().values() );
	}

	public Map<String, AttributeViewDefinition> attributeViewDefinitions() {
		if( _attributeViewDefinitions == null ) {
			_attributeViewDefinitions = new HashMap<>();
		}

		return _attributeViewDefinitions;
	}

	public List<AttributeViewDefinition> attributesToShow() {
		if( _attributesToShow == null ) {
			_attributesToShow = new ArrayList<>( attributeViewDefinitions().values() );
			_attributesToShow = _attributesToShow.stream().filter( AttributeViewDefinition::show ).collect( Collectors.toList() );
			Collections.sort( _attributesToShow, new USGenericComparator<>( "name", true, false ) );
			Collections.sort( _attributesToShow, new USGenericComparator<>( "sortOrder", true, false ) );
		}

		return _attributesToShow;
	}

	public static List<EntityViewDefinition> all() {
		List<EntityViewDefinition> all = new ArrayList<>();

		for( String entityName : allCayenneEntityNames() ) {
			all.add( EntityViewDefinition.get( entityName ) );
		}

		Collections.sort( all, new USGenericComparator<>( "icelandicName", true, true ) );

		return all;
	}

	private static List<String> allCayenneEntityNames() {
		// FIXME: MAXIMUM UGLYNESS!
		ServerRuntime serverRuntime = Jambalaya.serverRuntime();

		if( serverRuntime == null ) {
			return new ArrayList<>();
		}

		return serverRuntime.getDataDomain().getEntityResolver().getObjEntities().stream().map( ObjEntity::getName ).collect( Collectors.toList() );
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((_name == null) ? 0 : _name.hashCode());
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
		if( _name == null ) {
			if( other._name != null ) {
				return false;
			}
		}
		else if( !_name.equals( other._name ) ) {
			return false;
		}
		return true;
	}
}
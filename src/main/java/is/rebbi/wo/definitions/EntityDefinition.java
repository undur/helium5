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
import is.rebbi.wo.util.USGenericComparator;
import jambalaya.Jambalaya;

public class EntityDefinition<E> {

	private static final Logger logger = LoggerFactory.getLogger( EntityDefinition.class );

	/**
	 * All registered definitions.
	 */
	private static Map<String, EntityDefinition> _definitions;

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
	 * List of all objects that provide the system with EntityDefinitions.
	 */
	private static List<ProvidesEntityViewDefinitions> _entityDefinitionProviders;

	protected EntityDefinition() {}

	private static List<ProvidesEntityViewDefinitions> entityViewDefinitionProviders() {
		if( _entityDefinitionProviders == null ) {
			_entityDefinitionProviders = new ArrayList<>();

			boolean appProvides = ERXApplication.application() instanceof ProvidesEntityViewDefinitions;

			if( appProvides ) {
				_entityDefinitionProviders.add( (ProvidesEntityViewDefinitions)ERXApplication.application() );
			}
		}

		Collections.sort( _entityDefinitionProviders, new USGenericComparator( "priority", true, false ) );

		return _entityDefinitionProviders;
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

	// FIXME: This should be private
	public static Map<String, EntityDefinition> definitions() {
		if( _definitions == null ) {
			_definitions = new HashMap<>();

			for( ProvidesEntityViewDefinitions provider : entityViewDefinitionProviders() ) {
				logger.info( "Loading Entity View Definitions from {} - priority {}", provider.getClass(), provider.priority() );
				for( EntityViewDefinition e : provider.entityViewDefinitions() ) {
					((EntityDefinition)e).register(); // FIXME: Remove cast.
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

		if( iconFileName() != null ) {
			e.setIconFileName( iconFileName() );
		}

		if( _attributeViewDefinitions != null ) {
			((EntityDefinition)e)._attributeViewDefinitions = _attributeViewDefinitions; // FIXME: Remove the cast here once we're done abusing Java.
		}

		// FIXME: We're entering weird territory by casting ourselves.
		if( ((EntityViewDefinition)this).urlPrefix() != null ) {
			e.setUrlPrefix( ((EntityViewDefinition)this).urlPrefix() );
		}

		if( ((EntityViewDefinition)this).viewComponentClass() != null ) {
			e.setViewComponentClass( ((EntityViewDefinition)this).viewComponentClass() );
		}

		if( ((EntityViewDefinition)this).editComponentClass() != null ) {
			e.setEditComponentClass( ((EntityViewDefinition)this).editComponentClass() );
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

		// FIXME: Remove the cast once we're done
		EntityViewDefinition e = (EntityViewDefinition)definitions().get( entityName );

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
		EntityDefinition other = (EntityDefinition)obj;
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

	public static void invalidateCache() {
		logger.info( "Invalidating the EntityViewDefinition cache" );
		_definitions = null;
	}
}
package is.rebbi.wo.definitions;

import is.rebbi.wo.interfaces.HasSelectedObjectPage;
import jambalaya.Jambalaya;

/**
 * Defines the viewing of a certain entity.
 */

public class EntityViewDefinition<E, T extends HasSelectedObjectPage<E>, V extends HasSelectedObjectPage<E>> extends EntityDefinition<E> {

	/**
	 * Prefix used in URLs to access objects of this type.
	 */
	private String _urlPrefix;

	/**
	 * Class of component used to view objects if this type.
	 */
	private Class<T> _viewComponentClass;

	/**
	 * Class of component used to edit objects if this type.
	 */
	private Class<V> _editComponentClass;

	/**
	 * Define view definition for an entity.
	 */
	public static EntityViewDefinition create( Class<?> entityClass, String icelandicName, String icelandicNamePlural, String categoryName, String text, String urlPrefix, String iconFileName, boolean showInLists, Class viewComponentClass, Class editComponentClass ) {
		EntityViewDefinition e = new EntityViewDefinition();
		e.setEntityClass( entityClass );

		final String name = Jambalaya.serverRuntime().getDataDomain().getEntityResolver().getObjEntity( entityClass ).getName();

		e.setName( name );
		e.setIcelandicName( icelandicName );
		e.setIcelandicNamePlural( icelandicNamePlural );
		e.setCategoryName( categoryName );
		e.setText( text );
		e.setIconFileName( iconFileName );

		e.setUrlPrefix( urlPrefix );
		e.setViewComponentClass( viewComponentClass );
		e.setEditComponentClass( editComponentClass );
		return e;
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

	/**
	 * @return The definition for the given URL prefix.
	 *
	 * FIXME: Remove all this casting once done
	 */
	public static EntityViewDefinition definitionForURLPrefix( String urlPrefix ) {
		for( EntityDefinition o : definitions().values() ) {
			if( urlPrefix.equals( ((EntityViewDefinition)o).urlPrefix() ) ) {
				return (EntityViewDefinition)o;
			}
		}

		return null;
	}
}
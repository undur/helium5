package is.rebbi.wo.menu;

import com.webobjects.appserver.WOActionResults;

import is.rebbi.wo.objectroutes.Inspection;
import jambalaya.definitions.EntityDefinition;

public class USMenuItemEntity extends USMenuItem {

	private String _entityName;

	public static USMenuItemEntity create( String entityName, String iconClasses ) {
		USMenuItemEntity item = new USMenuItemEntity();
		item.setEntityName( entityName );
		item.setIconClasses( iconClasses );
		return item;
	}

	@Override
	public String name() {
		return EntityDefinition.get( _entityName ).icelandicNamePlural();
	}

	public String entityName() {
		return _entityName;
	}

	public void setEntityName( String value ) {
		_entityName = value;
	}

	@Override
	public WOActionResults action() {
		return Inspection.openListPage( EntityDefinition.get( _entityName ).entityClass() );
	}
}
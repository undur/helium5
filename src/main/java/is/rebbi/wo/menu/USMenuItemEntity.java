package is.rebbi.wo.menu;

import com.webobjects.appserver.WOActionResults;

import is.rebbi.wo.definitions.EntityViewDefinition;
import is.rebbi.wo.util.Inspection;

public class USMenuItemEntity extends USMenuItemPage {

	private String _entityName;

	public static USMenuItemEntity create( String entityName, String iconClasses ) {
		USMenuItemEntity item = new USMenuItemEntity();
		item.setEntityName( entityName );
		item.setIconClasses( iconClasses );
		return item;
	}

	@Override
	public String name() {
		return EntityViewDefinition.get( _entityName ).icelandicNamePlural();
	}

	public String entityName() {
		return _entityName;
	}

	public void setEntityName( String value ) {
		_entityName = value;
	}

	@Override
	public WOActionResults action() {
		return Inspection.openListPage( EntityViewDefinition.get( _entityName ).entityClass() );
	}
}
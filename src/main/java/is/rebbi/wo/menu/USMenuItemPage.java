package is.rebbi.wo.menu;

import com.webobjects.appserver.WOActionResults;

import er.extensions.appserver.ERXApplication;
import er.extensions.components.ERXComponent;

public class USMenuItemPage extends USMenuItem {

	private Class<? extends ERXComponent> _pageClass;

	public Class<? extends ERXComponent> pageClass() {
		return _pageClass;
	}

	public void setPageClass( Class<? extends ERXComponent> value ) {
		_pageClass = value;
	}

	public static USMenuItemPage create( String name, String iconClasses, Class<? extends ERXComponent> pageClass ) {

		if( name == null ) {
			name = pageClass.getSimpleName();
		}

		USMenuItemPage item = new USMenuItemPage();
		item.setName( name );
		item.setIconClasses( iconClasses );
		item.setPageClass( pageClass );
		return item;
	}

	@Override
	public WOActionResults action() {

		if( pageClass() == null ) {
			return null;
		}

		return ERXApplication.erxApplication().pageWithName( pageClass() );
	}
}
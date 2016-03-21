package is.rebbi.wo.menu;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.webobjects.appserver.WOActionResults;

import er.extensions.appserver.ERXApplication;
import er.extensions.components.ERXComponent;

public class USMenuItemPage implements USMenuItem {

	private String _identifier;
	private String _name;
	private Class<? extends ERXComponent> _pageClass;
	private String _iconClasses;
	private List<USMenuItemPage> _children = new ArrayList<>();
	private USMenuItem _parent;

	USMenuItemPage() {
		_identifier = UUID.randomUUID().toString();
	}

	public String name() {
		return _name;
	}

	public void setName( String value ) {
		_name = value;
	}

	public Class<? extends ERXComponent> pageClass() {
		return _pageClass;
	}

	public void setPageClass( Class<? extends ERXComponent> value ) {
		_pageClass = value;
	}

	public String iconClasses() {
		return _iconClasses;
	}

	public void setIconClasses( String value ) {
		_iconClasses = value;
	}

	@Override
	public List<USMenuItemPage> children() {
		return _children;
	}

	public void addChild( USMenuItemPage item ) {
		item._parent = this;
		_children.add( item );
	}

	public void setChildren( List<USMenuItemPage> value ) {
		_children = value;

		for( USMenuItemPage item : _children ) {
			item._parent = this;
		}
	}

	public WOActionResults action() {

		if( pageClass() == null ) {
			return null;
		}

		return ERXApplication.erxApplication().pageWithName( pageClass() );
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
	public USMenuItem parent() {
		return _parent;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((_identifier == null) ? 0 : _identifier.hashCode());
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
		USMenuItemPage other = (USMenuItemPage)obj;
		if( _identifier == null ) {
			if( other._identifier != null ) {
				return false;
			}
		}
		else if( !_identifier.equals( other._identifier ) ) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return name();
	}
}
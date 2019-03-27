package is.rebbi.wo.menu;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.webobjects.appserver.WOActionResults;

import is.rebbi.core.util.Hierarchy;

public abstract class USMenuItem implements Hierarchy {

	/**
	 * The name displayed to the user
	 */
	private String _name;

	/**
	 * A unique identifier for this particular item. Internal use only.
	 */
	private String _identifier;

	/**
	 * Name of icon for this item in the menu
	 */
	private String _iconClasses;

	/**
	 * Parent of the current menu item
	 */
	private USMenuItem _parent;

	/**
	 * Children of the menuitem
	 */
	private List<USMenuItem> _children = new ArrayList<>();

	public boolean forceOpen;

	USMenuItem() {
		_identifier = UUID.randomUUID().toString();
	}

	public abstract WOActionResults action();

	public String name() {
		return _name;
	}

	public void setName( String value ) {
		_name = value;
	}

	public String iconClasses() {
		return _iconClasses;
	}

	public void setIconClasses( String value ) {
		_iconClasses = value;
	}

	@Override
	public List<USMenuItem> children() {
		return _children;
	}

	public USMenuItem addChild( USMenuItem item ) {
		item._parent = this;
		_children.add( item );
		return item;
	}

	public void setChildren( List<USMenuItem> value ) {
		_children = value;

		for( USMenuItem item : _children ) {
			item._parent = this;
		}
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
		USMenuItem other = (USMenuItem)obj;
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
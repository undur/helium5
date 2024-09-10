package is.rebbi.wo.menu;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.webobjects.appserver.WOActionResults;

import is.rebbi.core.util.Hierarchy;
import is.rebbi.core.util.HierarchyUtilities;

public abstract class USMenuItem implements Hierarchy<USMenuItem> {

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

	/**
	 * FIXME: Actually implement
	 */
	@Deprecated
	public String badgeString;

	USMenuItem() {
		_identifier = UUID.randomUUID().toString();
	}

	public abstract WOActionResults action();

	public String id() {
		List<USMenuItem> everyParent = HierarchyUtilities.everyParentNode( this, true );
		StringBuilder b = new StringBuilder();

		for( USMenuItem parent : everyParent ) {
			b.append( parent.name() );
		}

		return b.toString();
	}

	public String url() {
		return null;
	}

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

	public boolean hasChildren() {
		return !children().isEmpty();
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

	@Override
	public String toString() {
		return name();
	}
}
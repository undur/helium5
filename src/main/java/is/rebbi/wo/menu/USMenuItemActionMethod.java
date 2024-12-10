package is.rebbi.wo.menu;

import java.util.function.Supplier;

import com.webobjects.appserver.WOActionResults;

@Deprecated
public class USMenuItemActionMethod extends USMenuItem {

	private Supplier<WOActionResults> _supplier;

	private void setFunction( Supplier<WOActionResults> function ) {
		_supplier = function;
	}

	public static USMenuItemActionMethod create( String name, String iconClasses, Supplier<WOActionResults> supplier ) {
		USMenuItemActionMethod item = new USMenuItemActionMethod();
		item.setName( name );
		item.setIconClasses( iconClasses );
		item.setFunction( supplier );
		return item;
	}

	@Override
	public WOActionResults action() {
		return _supplier.get();
	}
}
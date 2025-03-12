package is.rebbi.wo.menu;

import java.util.function.Supplier;

import com.webobjects.appserver.WOActionResults;

public class USMenuItemActionMethod extends USMenuItem {

	private Supplier<WOActionResults> _supplier;

	private void setSupplier( Supplier<WOActionResults> supplier ) {
		_supplier = supplier;
	}

	public static USMenuItemActionMethod create( String name, String iconClasses, Supplier<WOActionResults> supplier ) {
		USMenuItemActionMethod item = new USMenuItemActionMethod();
		item.setName( name );
		item.setIconClasses( iconClasses );
		item.setSupplier( supplier );
		return item;
	}

	@Override
	public WOActionResults action() {
		return _supplier.get();
	}
}
package is.rebbi.wo.menu;

import java.util.function.Function;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WOContext;

public class USMenuItemFunction extends USMenuItem {

	private Function<WOContext, WOActionResults> _function;
	private WOContext _context;

	private Function<WOContext, WOActionResults> function() {
		return _function;
	}

	private void setFunction( Function<WOContext, WOActionResults> function ) {
		_function = function;
	}

	public static USMenuItemFunction create( String name, String iconClasses, Function<WOContext, WOActionResults> function, WOContext context ) {
		USMenuItemFunction item = new USMenuItemFunction();
		item.setName( name );
		item.setIconClasses( iconClasses );
		item.setFunction( function );
		item._context = context;
		return item;
	}

	@Override
	public WOActionResults action() {
		return _function.apply( _context );
	}
}
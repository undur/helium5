package is.rebbi.wo.search.components;

import java.util.ArrayList;
import java.util.List;

import com.webobjects.appserver.WOContext;

import er.extensions.appserver.ERXApplication;
import er.extensions.components.ERXComponent;
import is.rebbi.wo.search.Indexer;
import is.rebbi.wo.search.USSearchAction;

public class USSearchField extends ERXComponent {

	/**
	 * The search string entered by the user.
	 */
	public String searchString;

	public USSearchField(WOContext context) {
		super( context );
	}

	public String searchString() {
		String s = context().request().stringFormValueForKey( "searchString_field" );

		if( searchString == null && s != null ) {
			searchString = s;
		}

		return searchString;
	}

	public String searchActionName() {
		StringBuilder b = new StringBuilder();
		b.append( USSearchAction.class.getSimpleName() );
		b.append( "/" );

		if( ERXApplication.isDevelopmentModeSafe() ) {
			b.append( "search" );
		}
		else {
			b.append( "searchRedirection" );
		}

		return b.toString();
	}

	public List<String> autoCompletes() {

		if( searchString != null && searchString.length() > 0 ) {
			return Indexer.autocomplete( searchString );
		}

		return new ArrayList<>();
	}
}
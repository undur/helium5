package is.rebbi.wo.components.admin;

import java.util.Collections;
import java.util.List;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WOContext;
import com.webobjects.eocontrol.EOSortOrdering;

import is.rebbi.wo.components.USListPageEdit;
import is.rebbi.wo.components.USViewPage;
import is.rebbi.wo.definitions.EntityViewDefinition;
import is.rebbi.wo.util.USGenericComparator;

public class USStartPage extends USViewPage {

	public EntityViewDefinition currentViewDefinition;

	public USStartPage(WOContext context) {
		super( context );
	}

	public List<EntityViewDefinition> all() {
		EOSortOrdering s = new EOSortOrdering( "icelandicName", EOSortOrdering.CompareCaseInsensitiveAscending );
		List<EntityViewDefinition> all = EntityViewDefinition.all();
		Collections.sort( all, new USGenericComparator( "icelandicName", true, true ) );
		return all;
	}

	public WOActionResults selectViewDefinition() {
		USListPageEdit nextPage = pageWithName( USListPageEdit.class );
		nextPage.setSelectedViewDefinition( currentViewDefinition );
		return nextPage;
	}
}
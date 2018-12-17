package is.rebbi.wo.components.admin;

import java.util.List;

import com.webobjects.appserver.WOContext;

import er.extensions.components.ERXComponent;
import is.rebbi.wo.definitions.EntityViewDefinition;

public class USViewDefinitionOverviewPage extends ERXComponent {

	public EntityViewDefinition current;

	public USViewDefinitionOverviewPage(WOContext context) {
		super( context );
	}

	public List<EntityViewDefinition> all() {
		return EntityViewDefinition.all();
	}
}
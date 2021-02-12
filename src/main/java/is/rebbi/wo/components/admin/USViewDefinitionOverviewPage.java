package is.rebbi.wo.components.admin;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.webobjects.appserver.WOContext;

import er.extensions.components.ERXComponent;
import jambalaya.definitions.EntityDefinition;

public class USViewDefinitionOverviewPage extends ERXComponent {

	public EntityDefinition current;

	public USViewDefinitionOverviewPage( WOContext context ) {
		super( context );
	}

	public List<EntityDefinition> all() {
		List<EntityDefinition> list = EntityDefinition.all();
		Collections.sort( list, Comparator.comparing( EntityDefinition::name ) );
		return list;
	}
}
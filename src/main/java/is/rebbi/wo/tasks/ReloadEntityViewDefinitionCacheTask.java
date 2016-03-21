package is.rebbi.wo.tasks;

import is.rebbi.wo.definitions.EntityViewDefinition;

public class ReloadEntityViewDefinitionCacheTask extends USTask {

	@Override
	public String name() {
		return "Reload entity view definition cache";
	}

	@Override
	public void run() {
		EntityViewDefinition.reloadAllDefinitions();
	}
}
package is.rebbi.wo.tasks;

import is.rebbi.wo.definitions.EntityViewDefinition;

public class FlushEntityViewDefinitionCacheTask extends USTask {

	@Override
	public String name() {
		return "Flush entity view definition cache";
	}

	@Override
	public void run() {
		EntityViewDefinition.invalidateCache();
	}
}
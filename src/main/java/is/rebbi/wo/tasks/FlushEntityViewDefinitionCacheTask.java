package is.rebbi.wo.tasks;

import is.rebbi.wo.definitions.EntityDefinition;

public class FlushEntityViewDefinitionCacheTask extends USTask {

	@Override
	public String name() {
		return "Flush entity view definition cache";
	}

	@Override
	public void run() {
		EntityDefinition.invalidateCache();
	}
}
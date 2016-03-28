package is.rebbi.wo.tasks;

import is.rebbi.wo.search.Indexer;

public class RegenerateIndexTask extends USTask {

	@Override
	public String name() {
		return "Regenerate search index";
	}

	@Override
	public void run() {
		Indexer.createIndex();
	}
}
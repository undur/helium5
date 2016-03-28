package is.rebbi.wo.tasks;

import is.rebbi.wo.search.IndexListener;

public class RegenerateIndexTask extends USTask {

	@Override
	public String name() {
		return "Regenerate search index";
	}

	@Override
	public void run() {
		IndexListener.createIndex();
	}
}
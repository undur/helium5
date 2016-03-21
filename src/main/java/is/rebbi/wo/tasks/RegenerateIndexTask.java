package is.rebbi.wo.tasks;

import is.rebbi.wo.search.IndexCreatorEOF;

public class RegenerateIndexTask extends USTask {

	@Override
	public String name() {
		return "Regenerate search index";
	}

	@Override
	public void run() {
		IndexCreatorEOF.createIndex();
	}
}
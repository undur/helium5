package is.rebbi.wo.tasks;

import is.rebbi.wo.search.IcelandicInflector;

public class RegenerateSynonymIndexTask extends USTask {

	@Override
	public String name() {
		return "Regenerate Icelandic Inflection index";
	}

	@Override
	public void run() {
		IcelandicInflector.sharedInstance().createIndex();
	}
}
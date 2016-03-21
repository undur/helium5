package is.rebbi.wo.tasks;

import com.webobjects.appserver.WOApplication;

public class FlushComponentDefinitionCacheTask extends USTask {

	@Override
	public String name() {
		return "Flush component definition cache";
	}

	@Override
	public void run() {
		WOApplication.application()._removeComponentDefinitionCacheContents();
	}
}
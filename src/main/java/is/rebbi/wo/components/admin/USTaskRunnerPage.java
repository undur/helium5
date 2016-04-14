package is.rebbi.wo.components.admin;

import java.util.List;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WOContext;

import er.extensions.components.ERXComponent;
import is.rebbi.wo.tasks.USTask;

public class USTaskRunnerPage extends ERXComponent {

	public Class currentTaskClass;

	public USTaskRunnerPage(WOContext context) {
		super( context );
	}

	public List<Class<? extends USTask>> taskClasses() {
		return USTask.taskClasses();
	}

	public WOActionResults run() {
		USTask.runTaskOfClass( currentTaskClass );
		return null;
	}

	public String currentTaskName() {
		try {
			return ((USTask)currentTaskClass.newInstance()).name();
		}
		catch( InstantiationException | IllegalAccessException e ) {
			throw new RuntimeException( "Failed to fetch task class name", e );
		}
	}
}
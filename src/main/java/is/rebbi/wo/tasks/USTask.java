package is.rebbi.wo.tasks;

import java.util.ArrayList;
import java.util.List;

/**
 * Everyone needs to run some tasks at some point.
 */

public abstract class USTask {

	private static List<Class<? extends USTask>> _taskClasses;

	public abstract String name();

	public abstract void run();

	public static List<Class<? extends USTask>> taskClasses() {
		if( _taskClasses == null ) {
			_taskClasses = new ArrayList<>();
			_taskClasses.add( FlushComponentDefinitionCacheTask.class );
			_taskClasses.add( FlushEntityViewDefinitionCacheTask.class );
			_taskClasses.add( ReloadEntityViewDefinitionCacheTask.class );
			_taskClasses.add( RegenerateIndexTask.class );
			_taskClasses.add( RegenerateSynonymIndexTask.class );
		}

		return _taskClasses;
	}

	public static void register( Class<? extends USTask> taskClass ) {
		taskClasses().add( taskClass );
	}

	public static void runTaskOfClass( Class<? extends USTask> taskClass ) {
		try {
			USTask instance = taskClass.newInstance();
			if( instance.runInBackground() ) {
				new Thread( () -> {
					instance.run();
				} ).run();
			}
			else {
				instance.run();
			}
		}
		catch( InstantiationException | IllegalAccessException e ) {
			throw new RuntimeException( e );
		}
	}

	public boolean runInBackground() {
		return true;
	}
}
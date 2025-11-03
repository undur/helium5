package is.rebbi.wo.components.admin;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WOApplication;
import com.webobjects.appserver.WOContext;
import com.webobjects.foundation.NSKeyValueCoding;
import com.webobjects.foundation.NSValidation;

import er.extensions.components.ERXComponent;
import jambalaya.definitions.EntityDefinition;

public class USTaskRunnerPage extends ERXComponent {

	public Class<? extends USTask> currentTaskClass;

	public USTaskRunnerPage( WOContext context ) {
		super( context );
	}

	public List<Class<? extends USTask>> taskClasses() {
		return List.of( FlushCachesTask.class, FlushEntityViewDefinitionCacheTask.class );
	}

	public WOActionResults run() {
		USTask.runTaskOfClass( currentTaskClass );
		return null;
	}

	public String currentTaskName() {
		try {
			return currentTaskClass.newInstance().name();
		}
		catch( InstantiationException | IllegalAccessException e ) {
			throw new RuntimeException( "Failed to fetch task class name", e );
		}
	}

	public static abstract class USTask {

		public abstract String name();

		public abstract void run();

		public static void runTaskOfClass( Class<? extends USTask> taskClass ) {
			try {
				final USTask taskInstance = taskClass.newInstance();

				System.out.println( "Running task '%s' of class '%s'. Background: %s".formatted( taskInstance.name(), taskClass, taskInstance.runInBackground() ) );

				if( taskInstance.runInBackground() ) {
					new Thread( () -> {
						taskInstance.run();
					} ).start();
				}
				else {
					taskInstance.run();
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

	public static class FlushEntityViewDefinitionCacheTask extends USTask {

		@Override
		public String name() {
			return "Flush entity view definition cache";
		}

		@Override
		public void run() {
			EntityDefinition.invalidateCache();
		}
	}

	public static class FlushCachesTask extends USTask {

		private static final Logger logger = LoggerFactory.getLogger( FlushCachesTask.class );

		@Override
		public String name() {
			return "Flush component, KVC, Action and Validation caches";
		}

		@Override
		public void run() {
			WOApplication.application()._removeComponentDefinitionCacheContents();
			NSKeyValueCoding.DefaultImplementation._flushCaches();
			NSKeyValueCoding._ReflectionKeyBindingCreation._flushCaches();
			NSKeyValueCoding.ValueAccessor._flushCaches();
			NSValidation.DefaultImplementation._flushCaches();

			try {
				Class woActionClass = Class.forName( "com.webobjects.appserver.WOAction", false, WOApplication.class.getClassLoader() );
				Field actionClassesField = woActionClass.getDeclaredField( "_actionClasses" );
				actionClassesField.setAccessible( true );
				Object actionClassesCacheDictionary = actionClassesField.get( null );
				Class nsThreadsafeMutableDictionaryClass = Class.forName( "com.webobjects.foundation._NSThreadsafeMutableDictionary", false, WOApplication.class.getClassLoader() );
				Method nsThreadsafeMutableDictionary_removeAllObjects = nsThreadsafeMutableDictionaryClass.getMethod( "removeAllObjects" );
				nsThreadsafeMutableDictionary_removeAllObjects.invoke( actionClassesCacheDictionary );
				logger.info( "Resetting Action class cache" );
			}
			catch( Exception e ) {
				e.printStackTrace();
			}
		}
	}
}
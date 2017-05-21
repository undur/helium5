package is.rebbi.wo.tasks;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webobjects.appserver.WOApplication;
import com.webobjects.foundation.NSKeyValueCoding;
import com.webobjects.foundation.NSValidation;

import javassist.ClassPool;

public class FlushComponentDefinitionCacheTask extends USTask {

	private static final Logger LOGGER = LoggerFactory.getLogger( FlushComponentDefinitionCacheTask.class );

	private Method woApplication_removeComponentDefinitionCacheContents;
	private Object woApplicationObject;
	private Method nsThreadsafeMutableDictionary_removeAllObjects;
	private Object actionClassesCacheDictionnary;

	@Override
	public String name() {
		return "Flush component, KVC, Action and Validation caches";
	}

	@Override
	public void run() {
		clearComponentCacheCommand.executeCommand();
		clearActionCacheCommand.executeCommand();

		NSKeyValueCoding.DefaultImplementation._flushCaches();
		NSKeyValueCoding._ReflectionKeyBindingCreation._flushCaches();
		NSKeyValueCoding.ValueAccessor._flushCaches();
		NSValidation.DefaultImplementation._flushCaches();
	}

	public void init() {
		ClassLoader appClassLoader = WOApplication.class.getClassLoader();

		try {
			Class woApplicationClass = Class.forName( "", false, appClassLoader );
			woApplication_removeComponentDefinitionCacheContents = woApplicationClass.getMethod( "_removeComponentDefinitionCacheContents" );
			woApplicationObject = woApplicationClass.getMethod( "application" ).invoke( null );

			ClassPool classPool = ClassPool.getDefault();

			Class woActionClass = Class.forName( "com.webobjects.appserver.WOAction", false, appClassLoader );
			Field actionClassesField = woActionClass.getDeclaredField( "_actionClasses" );
			actionClassesField.setAccessible( true );
			actionClassesCacheDictionnary = actionClassesField.get( null );

			Class nsThreadsafeMutableDictionaryClass = Class.forName( "com.webobjects.foundation._NSThreadsafeMutableDictionary", false, appClassLoader );
			woApplication_removeComponentDefinitionCacheContents = woApplicationClass.getMethod( "_removeComponentDefinitionCacheContents" );
			nsThreadsafeMutableDictionary_removeAllObjects = nsThreadsafeMutableDictionaryClass.getMethod( "removeAllObjects" );
		}
		catch( Exception e ) {
			e.printStackTrace();
		}
	}

	private interface Command {
		public void executeCommand();
	}

	private ClearComponentCache clearComponentCacheCommand = new ClearComponentCache();

	private class ClearComponentCache implements Command {
		@Override
		public void executeCommand() {
			try {
				woApplication_removeComponentDefinitionCacheContents.invoke( woApplicationObject );
				LOGGER.info( "Resetting Component Definition cache" );
			}
			catch( Exception e ) {
				e.printStackTrace();
			}
		}
	}

	private ClearActionCache clearActionCacheCommand = new ClearActionCache();

	private class ClearActionCache implements Command {
		@Override
		public void executeCommand() {
			try {
				nsThreadsafeMutableDictionary_removeAllObjects.invoke( actionClassesCacheDictionnary );
				LOGGER.info( "Resetting Action class cache" );
			}
			catch( Exception e ) {
				e.printStackTrace();
			}
		}
	}
}
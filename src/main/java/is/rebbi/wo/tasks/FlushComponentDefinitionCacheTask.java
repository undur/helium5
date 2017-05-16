package is.rebbi.wo.tasks;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webobjects.appserver.WOApplication;

import javassist.ClassPool;

public class FlushComponentDefinitionCacheTask extends USTask {

	@Override
	public String name() {
		return "Flush component, KVC, Action and Validation caches";
	}

	@Override
	public void run() {
		clearKVCCacheCommand.executeCommand();
		clearComponentCacheCommand.executeCommand();
		clearActionCacheCommand.executeCommand();
		clearValidationCacheCommand.executeCommand();
	}

	private static final Logger LOGGER = LoggerFactory.getLogger( HotswapWebObjectsPlugin.class );

	private Method kvcDefaultImplementation_flushCaches;
	private Method kvcReflectionKeyBindingCreation_flushCaches;
	private Method kvcValueAccessor_flushCaches;
	private Method nsValidationDefaultImplementation_flushCaches;
	private Method woApplication_removeComponentDefinitionCacheContents;
	private Object woApplicationObject;
	private Method nsThreadsafeMutableDictionary_removeAllObjects;
	private Object actionClassesCacheDictionnary;

	private interface Command {
		public void executeCommand();
	}

	public void init() {
		ClassLoader appClassLoader = WOApplication.class.getClassLoader();

		try {
			Class kvcDefaultImplementationClass = Class.forName( "com.webobjects.foundation.NSKeyValueCoding$DefaultImplementation", false, appClassLoader );
			kvcDefaultImplementation_flushCaches = kvcDefaultImplementationClass.getMethod( "_flushCaches" );

			Class kvcReflectionKeyBindingCreationClass = Class.forName( "com.webobjects.foundation.NSKeyValueCoding$_ReflectionKeyBindingCreation", false, appClassLoader );
			kvcReflectionKeyBindingCreation_flushCaches = kvcReflectionKeyBindingCreationClass.getMethod( "_flushCaches" );

			Class kvcValueAccessorClass = Class.forName( "com.webobjects.foundation.NSKeyValueCoding$ValueAccessor", false, appClassLoader );
			kvcValueAccessor_flushCaches = kvcValueAccessorClass.getMethod( "_flushCaches" );

			Class nsValidationDefaultImplementationClass = Class.forName( "com.webobjects.foundation.NSValidation$DefaultImplementation", false, appClassLoader );
			nsValidationDefaultImplementation_flushCaches = nsValidationDefaultImplementationClass.getMethod( "_flushCaches" );

			Class woApplicationClass = Class.forName( "com.webobjects.appserver.WOApplication", false, appClassLoader );
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

	private ClearKVCCache clearKVCCacheCommand = new ClearKVCCache();

	public class ClearKVCCache implements Command {
		@Override
		public void executeCommand() {
			try {
				kvcDefaultImplementation_flushCaches.invoke( null );
				kvcReflectionKeyBindingCreation_flushCaches.invoke( null );
				kvcValueAccessor_flushCaches.invoke( null );
				LOGGER.info( "Resetting KeyValueCoding caches" );
			}
			catch( Exception e ) {
				e.printStackTrace();
			}
		}
	}

	private ClearComponentCache clearComponentCacheCommand = new ClearComponentCache();

	public class ClearComponentCache implements Command {
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

	public class ClearActionCache implements Command {
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

	private ClearValidationCache clearValidationCacheCommand = new ClearValidationCache();

	public class ClearValidationCache implements Command {
		@Override
		public void executeCommand() {
			try {
				nsValidationDefaultImplementation_flushCaches.invoke( null );
				LOGGER.info( "Resetting NSValidation cache" );
			}
			catch( Exception e ) {
				e.printStackTrace();
			}
		}
	}
}
package is.rebbi.wo.tasks;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webobjects.appserver.WOApplication;
import com.webobjects.foundation.NSKeyValueCoding;
import com.webobjects.foundation.NSValidation;

public class FlushCachesTask extends USTask {

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
package is.rebbi.wo.components;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WOResponse;

import is.rebbi.wo.interfaces.HasSelectedObjectPage;
import is.rebbi.wo.util.USSettings;
import jambalaya.interfaces.UUIDStamped;
import jambalaya.interfaces.UniqueIDStamped;

public class USViewWrapper extends USViewPage {

	private static final Logger logger = LoggerFactory.getLogger( USViewWrapper.class );

	/**
	 * Name of the component currently being displayed.
	 */
	private HasSelectedObjectPage _displayComponentInstance;

	public USViewWrapper( WOContext context ) {
		super( context );
	}

	public HasSelectedObjectPage displayComponentInstance() {
		return _displayComponentInstance;
	}

	public void setDisplayComponentInstance( HasSelectedObjectPage displayComponentInstance ) {
		_displayComponentInstance = displayComponentInstance;
	}

	public String viewToolsComponentName() {

		if( USSettings.viewToolsComponentName() != null ) {
			return USSettings.viewToolsComponentName();
		}

		return USBaseViewTools.class.getSimpleName();
	}

	@Override
	public void appendToResponse( WOResponse response, WOContext context ) {
		String uniqueID = "[no uniqueID]";

		if( selectedObject() != null ) {
			if( selectedObject() instanceof UniqueIDStamped uuid ) {
				uniqueID = uuid.uniqueID();
			}
			if( selectedObject() instanceof UUIDStamped uuid ) {
				uniqueID = uuid.uniqueID().toString();
			}
		}

		logger.info( "displayComponentName : %s : %s".formatted( _displayComponentInstance.getClass().getName(), uniqueID ) );
		super.appendToResponse( response, context );
	}
}
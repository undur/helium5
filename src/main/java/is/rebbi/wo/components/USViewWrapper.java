package is.rebbi.wo.components;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WOResponse;

import is.rebbi.wo.util.SWSettings;

public class USViewWrapper extends USViewPage {

	private static final Logger logger = LoggerFactory.getLogger( USViewWrapper.class );

	/**
	 * Name of the component currently being displayed.
	 */
	private String _displayComponentName;

	public USViewWrapper( WOContext context ) {
		super( context );
	}

	public String displayComponentName() {
		return _displayComponentName;
	}

	public void setDisplayComponentName( String displayComponentName ) {
		_displayComponentName = displayComponentName;
	}

	public String viewToolsComponentName() {

		if( SWSettings.viewToolsComponentName() != null ) {
			return SWSettings.viewToolsComponentName();
		}

		return USBaseViewTools.class.getSimpleName();
	}

	@Override
	public void appendToResponse( WOResponse response, WOContext context ) {
		logger.info( "displayComponentName" + _displayComponentName );
		super.appendToResponse( response, context );
	}
}
package is.rebbi.wo.components;

import com.webobjects.appserver.WOContext;

import is.rebbi.wo.util.SWSettings;

public class USViewWrapper extends USViewPage {

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
}
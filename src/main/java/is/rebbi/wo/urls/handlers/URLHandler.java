package is.rebbi.wo.urls.handlers;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WOContext;

import is.rebbi.wo.urls.USURLPath;

public abstract class URLHandler {

	private USURLPath _path;
	private String _url;
	private WOContext _context;

	public URLHandler( String url, WOContext context ) {
		_url = url;
		_context = context;
		_path = USURLPath.create( _url );
	}

	public String url() {
		return _url;
	}

	public USURLPath path() {
		return _path;
	}

	public WOContext context() {
		return _context;
	}

	/**
	 * Defines a function that will be run when the button is clicked, passing the selectedObject if any.
	 */
	public abstract WOActionResults generateResponse();
}
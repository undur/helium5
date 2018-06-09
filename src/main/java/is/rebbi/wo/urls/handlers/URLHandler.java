package is.rebbi.wo.urls.handlers;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WOContext;

public abstract class URLHandler {

    private String _url;
    private WOContext _context;

    public URLHandler( String url, WOContext context ) {
        _url = url;
        _context = context;
    }
    
    public String url() {
        return _url;
    }

    public WOContext context() {
        return _context;
    }

	/**
	 * Defines a function that will be run when the button is clicked, passing the selectedObject if any.
	 */
	public abstract WOActionResults generateResponse();
}
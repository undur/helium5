package is.rebbi.wo.urls.handlers;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WOContext;

import is.rebbi.wo.urls.URLWrapper;

public abstract class URLHandler {

	private WOContext _context;
	private String _url;
	private URLWrapper _path;

	public URLHandler( final String url, final WOContext context ) {
		_context = context;
		_path = URLWrapper.create( url );
		_url = url;
	}

	public String url() {
		return _url;
	}

	public URLWrapper path() {
		return _path;
	}

	public WOContext context() {
		return _context;
	}

	public abstract WOActionResults generateResponse();
}
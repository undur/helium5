package is.rebbi.wo.util;

import java.util.Objects;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WOResponse;

public class StaticURLResponse implements WOActionResults {

	final String _urlString;

	private StaticURLResponse( final String urlString ) {
		Objects.requireNonNull( urlString );
		_urlString = urlString;
	}

	public static StaticURLResponse of( final String urlString ) {
		return new StaticURLResponse( urlString );
	}

	@Override
	public WOResponse generateResponse() {
		return USHTTPUtilities.redirectTemporary( _urlString );
	}
}
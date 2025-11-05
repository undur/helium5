package is.rebbi.wo.util;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import com.webobjects.appserver.WOCookie;
import com.webobjects.appserver.WORequest;
import com.webobjects.appserver.WOResponse;
import com.webobjects.foundation.NSData;
import com.webobjects.foundation.NSMutableArray;

import is.rebbi.core.util.StringUtilities;

public class USHTTPUtilities {

	private static final String HEADER_HOST_DEFAULT = "host";
	private static final String HEADER_HOST_IIS = "http_host";

	private static final String HEADER_REMOTE_HOST = "remote_host";
	private static final String HEADER_REMOTE_ADDR = "remote_addr";
	private static final String HEADER_REMOTE_USER = "remote_user";
	private static final String HEADER_WEBOBJECTS_REMOTE_ADDR = "x-webobjects-remote-addr";

	private static final String HEADER_CONTENT_TYPE = "content-type";
	private static final String HEADER_SET_COOKIE = "set-cookie";
	private static final String HEADER_REFERER = "referer";
	private static final String HEADER_REDIRECT_LOCATION = "location";
	private static final String HEADER_CONTENT_LENGTH = "content-length";
	private static final String HEADER_REDIRECT_URL = "redirect_url";
	private static final String HEADER_REDIRECT_QUERY_STRING = "REDIRECT_QUERY_STRING";
	private static final String HEADER_PRAGMA = "pragma";
	private static final String HEADER_CACHE_CONTROL = "cache-control";
	private static final String HEADER_CONTENT_DISPOSITION = "content-disposition";
	private static final String HEADER_USER_AGENT = "user-agent";
	private static final String HEADER_EXPIRES = "expires";
	private static final String HEADER_CONTENT_INLINE = "inline";
	private static final String HEADER_CONTENT_ATTACHMENT = "attachment";

	private static final String UNTITLED_FILENAME = "Untitled";
	private static final String MIME_TYPE_OCTET_STREAM = "octet/stream";

	/**
	 * @return The IP-address that initiated the WORequest (if present)
	 */
	public static String ipAddressFromRequest( final WORequest request ) {
		String host = request.headerForKey( HEADER_REMOTE_HOST );

		if( host != null ) {
			return host;
		}

		host = request.headerForKey( HEADER_REMOTE_ADDR );

		if( host != null ) {
			return host;
		}

		host = request.headerForKey( HEADER_REMOTE_USER );

		if( host != null ) {
			return host;
		}

		host = request.headerForKey( HEADER_WEBOBJECTS_REMOTE_ADDR );

		if( host != null ) {
			return host;
		}

		return null;
	}

	/**
	 * An adaptor-agnostic way of determining the requested host name.
	 */
	public static String host( final WORequest request ) {
		String host = request.headerForKey( HEADER_HOST_DEFAULT );

		if( !StringUtilities.hasValue( host ) ) {
			host = request.headerForKey( HEADER_HOST_IIS );
		}

		if( host == null ) {
			return null;
		}

		return host.toLowerCase();
	}

	/**
	 * @return The value of the referer header (if present)
	 */
	public static String referer( final WORequest request ) {
		return request.headerForKey( HEADER_REFERER );
	}

	/**
	 * This method creates a WOResponse with a temporary (302) redirect to the specified URL
	 *
	 * @param targetURL The URL to redirect to
	 */
	public static WOResponse redirectTemporary( final String targetURL ) {
		final WOResponse response = new WOResponse();

		response.setStatus( 302 );
		response.setHeader( targetURL, HEADER_REDIRECT_LOCATION );
		response.setHeader( "text/html", HEADER_CONTENT_TYPE );
		response.setHeader( "0", HEADER_CONTENT_LENGTH );

		return response;
	}

	/**
	 * This method creates a WOResponse with a permanent (301) redirect to the specified URL
	 *
	 * @param targetURL The URL to redirect to
	 */
	public static WOResponse redirectPermanent( final String targetURL ) {
		final WOResponse response = new WOResponse();

		response.setStatus( 301 );
		response.setHeader( targetURL, HEADER_REDIRECT_LOCATION );
		response.setHeader( "text/html", HEADER_CONTENT_TYPE );
		response.setHeader( "0", HEADER_CONTENT_LENGTH );

		return response;
	}

	/**
	 * Attempts to decode a referer string and get the host name from it.
	 */
	public static String hostFromURL( final String url ) {
		final int beginningIndex = url.indexOf( "//" );
		final int endIndex = url.indexOf( "/", beginningIndex + 2 );
		return url.substring( beginningIndex + 2, endIndex );
	}

	/**
	 * Attempts to decode a top level domain
	 */
	public static String domain( final WORequest request ) {
		return domainStringFromHostString( host( request ) );
	}

	/**
	 * Gets the top level domain from a host string.
	 */
	public static String domainStringFromHostString( String host ) {

		if( host == null ) {
			return null;
		}

		int colonIndex = host.lastIndexOf( ":" );

		if( colonIndex > -1 ) {
			host = host.substring( 0, colonIndex );
		}

		String numericString = host.replace( ".", "" );

		if( StringUtilities.isDigitsOnly( numericString ) ) {
			return host;
		}

		int i = host.lastIndexOf( "." );

		if( i > -1 ) {
			i = host.lastIndexOf( ".", i - 1 );

			if( i > -1 ) {
				return host.substring( i + 1, host.length() );
			}
			else {
				return host;
			}
		}

		return null;
	}

	/**
	 * Returns the user agent string of the guest.
	 */
	public static String userAgent( final WORequest request ) {
		return request.headerForKey( HEADER_USER_AGENT );
	}

	/**
	 * If the WO app is used as a 404 handler, this method returns the requested URL.
	 */
	public static String redirectURL( final WORequest request ) {
		return request.headerForKey( HEADER_REDIRECT_URL );
	}

	/**
	 * If the WO app is used as a 404 handler, this method returns the query string part of the requested URI.
	 */
	public static String redirectQueryString( final WORequest request ) {
		return request.headerForKey( HEADER_REDIRECT_QUERY_STRING );
	}

	public static WOResponse responseWithDataAndMimeType( final String filename, final NSData data, final String mimeType ) {
		return responseWithDataAndMimeType( filename, data, mimeType, false );
	}

	/**
	 * Creates a WOResponse containing the given data.
	 */
	public static WOResponse responseWithDataAndMimeType( final String filename, final byte[] bytes, final String mimeType ) {
		return responseWithDataAndMimeType( filename, bytes, mimeType, false );
	}

	/**
	 * Creates a WOResponse containing the given data.
	 */
	public static WOResponse responseWithDataAndMimeType( final String filename, byte[] bytes, final String mimeType, final boolean forceDownload ) {

		NSData data = NSData.EmptyData;

		if( bytes != null ) {
			data = new NSData( bytes );
		}

		return responseWithDataAndMimeType( filename, data, mimeType, forceDownload );
	}

	/**
	 * Creates a WOResponse containing the given string, encoded in UTF-8.
	 */
	public static WOResponse responseWithDataAndMimeType( final String filename, String contentString, final String mimeType ) {

		if( contentString == null ) {
			contentString = "";
		}

		final NSData data = new NSData( contentString.getBytes( StandardCharsets.UTF_8 ) );
		return responseWithDataAndMimeType( filename, data, mimeType + "; charset=UTF-8", false );
	}

	/**
	 * Creates a WOResponse containing the given data.
	 */
	public static WOResponse responseWithDataAndMimeType( String filename, NSData data, String mimeType, boolean forceDownload ) {

		if( !StringUtilities.hasValue( filename ) ) {
			filename = UNTITLED_FILENAME;
		}

		if( data == null ) {
			data = NSData.EmptyData;
		}

		if( mimeType == null ) {
			mimeType = MIME_TYPE_OCTET_STREAM;
		}

		String disposition = HEADER_CONTENT_INLINE;

		if( forceDownload ) {
			disposition = HEADER_CONTENT_ATTACHMENT;
		}

		WOResponse response = new WOResponse();
		response.setHeader( mimeType, HEADER_CONTENT_TYPE );
		response.setHeader( data.length() + "", HEADER_CONTENT_LENGTH );
		response.setHeader( disposition + ";filename=\"" + escapeFilenameForDispositionHeader( filename ) + "\"", HEADER_CONTENT_DISPOSITION );
		response.removeHeadersForKey( HEADER_CACHE_CONTROL );
		response.removeHeadersForKey( HEADER_PRAGMA );
		response.removeHeadersForKey( HEADER_EXPIRES );
		response.setContent( data );
		return response;
	}

	/**
	 * Creates a WOResponse containing the given data.
	 */
	public static WOResponse responseWithStreamAndMimeType( String filename, InputStream stream, long length, String mimeType, boolean forceDownload ) {

		if( !StringUtilities.hasValue( filename ) ) {
			filename = UNTITLED_FILENAME;
		}

		if( stream == null ) {
			stream = new ByteArrayInputStream( new byte[0] );
		}

		if( mimeType == null ) {
			mimeType = MIME_TYPE_OCTET_STREAM;
		}

		String disposition = HEADER_CONTENT_INLINE;

		if( forceDownload ) {
			disposition = HEADER_CONTENT_ATTACHMENT;
		}

		WOResponse response = new WOResponse();
		response.setHeader( mimeType, HEADER_CONTENT_TYPE );
		response.setHeader( length + "", HEADER_CONTENT_LENGTH );
		response.setHeader( disposition + ";filename=\"" + escapeFilenameForDispositionHeader( filename ) + "\"", HEADER_CONTENT_DISPOSITION );
		response.removeHeadersForKey( HEADER_CACHE_CONTROL );
		response.removeHeadersForKey( HEADER_PRAGMA );
		response.removeHeadersForKey( HEADER_EXPIRES );
		response.setContentStream( stream, 32000, length );
		return response;
	}

	/**
	 * @return The filename escaped for the content-disposition header
	 */
	private static String escapeFilenameForDispositionHeader( String string ) {
		string = string.replace( "–", "-" );
		string = string.replace( "&", "-" );

		return string;
	}

	/**
	 * @return A response with the given status and, if specified and HTML content string displayed to the user.
	 */
	public static WOResponse statusResponse( final int status, final String htmlContent ) {
		final WOResponse response = new WOResponse();

		if( htmlContent != null ) {
			response.setHeader( "text/html", HEADER_CONTENT_TYPE );
			response.setHeader( "inline;filename=\"" + status + ".html\"", USHTTPUtilities.HEADER_CONTENT_DISPOSITION );
			response.setContent( htmlContent );
		}

		response.setStatus( status );
		return response;
	}

	/**
	 * After dispatchRequest has been fired, the "cookie" header in WOResponse has already been set.
	 * If we make changes to cookies after that, we need to set the header manually.
	 *
	 * @param response The response to modify.
	 */
	public static void resetCookieHeaderInResponse( final WOResponse response ) {

		final NSMutableArray<String> cookieHeaderStrings = new NSMutableArray<String>();

		for( final WOCookie cookie : response.cookies() ) {
			cookieHeaderStrings.addObject( cookie.headerString() );
		}

		response.setHeaders( cookieHeaderStrings, HEADER_SET_COOKIE );
	}

	public static String cookieHost( final WORequest request ) {

		if( request == null ) {
			throw new IllegalArgumentException( "request must not be null" );
		}

		String host = USHTTPUtilities.host( request );

		if( host == null ) {
			return null;
		}

		int colonIndex = host.lastIndexOf( ":" );

		if( colonIndex > -1 ) {
			host = host.substring( 0, colonIndex );
		}

		if( host.equals( "localhost" ) ) {
			return null;
		}

		return host;
	}
}
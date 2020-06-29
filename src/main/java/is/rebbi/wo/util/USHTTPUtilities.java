package is.rebbi.wo.util;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webobjects.appserver.WOCookie;
import com.webobjects.appserver.WORequest;
import com.webobjects.appserver.WOResponse;
import com.webobjects.foundation.NSData;
import com.webobjects.foundation.NSMutableArray;

import is.rebbi.core.util.StringUtilities;

/**
 * Various WO utility methods.
 */

public class USHTTPUtilities {

	public static final String MIME_TYPE_EXCEL = "application/excel";
	public static final String MIME_TYPE_PDF = "application/pdf";
	public static final String MIME_TYPE_XML = "text/xml";
	public static final String MIME_TYPE_HTML = "text/html";
	public static final String MIME_TYPE_PNG = "image/png";
	public static final String MIME_TYPE_JPEG = "image/jpeg";
	public static final String MIME_TYPE_GIF = "image/gif";

	private static final Logger logger = LoggerFactory.getLogger( USHTTPUtilities.class );

	public static final String HEADER_CONTENT_TYPE = "content-type";
	public static final String HEADER_SET_COOKIE = "set-cookie";
	public static final String HEADER_COOKIE = "cookie";
	public static final String HEADER_REFERER = "referer";
	public static final String HEADER_HOST_DEFAULT = "host";
	private static final String HEADER_REMOTE_HOST = "remote_host";
	private static final String HEADER_REMOTE_ADDR = "remote_addr";
	private static final String HEADER_REMOTE_USER = "remote_user";
	private static final String HEADER_WEBOBJECTS_REMOTE_ADDR = "x-webobjects-remote-addr";
	private static final String HEADER_HOST_IIS = "http_host";
	private static final String HEADER_REDIRECT_LOCATION = "location";
	private static final String HEADER_CONTENT_LENGTH = "content-length";
	private static final String HEADER_CONTENT_ENCODING = "content-encoding";
	private static final String HEADER_REDIRECT_URL = "redirect_url";
	private static final String HEADER_REDIRECT_QUERY_STRING = "REDIRECT_QUERY_STRING";
	private static final String HEADER_ACCEPT_ENCODING = "accept-encoding";
	private static final String HEADER_PRAGMA = "pragma";
	private static final String HEADER_CACHE_CONTROL = "cache-control";
	public static final String HEADER_CONTENT_DISPOSITION = "content-disposition";
	private static final String HEADER_USER_AGENT = "user-agent";
	private static final String HEADER_EXPIRES = "expires";
	private static final String HEADER_CONTENT_INLINE = "inline";
	private static final String HEADER_CONTENT_ATTACHMENT = "attachment";

	private static final String MIME_TYPE_OCTET_STREAM = "octet/stream";
	private static final String CONTENT_ENCODING_GZIP = "gzip";
	private static final String UNTITLED_FILENAME = "Untitled";

	/**
	 * Fetches the IP-address from a WORequest.
	 */
	public static String ipAddressFromRequest( WORequest request ) {
		String host = null;

		if( host == null ) {
			host = request.headerForKey( HEADER_REMOTE_HOST );
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
		}
		return null;
	}

	/**
	 * Returns the referer header.
	 */
	public static String referer( WORequest request ) {
		return request.headerForKey( HEADER_REFERER );
	}

	/**
	 * This method creates a WOResponse with a temporary (302) redirect to the specified URL
	 *
	 * @param targetURL The URL to redirect to
	 */
	public static WOResponse redirectTemporary( String targetURL ) {
		WOResponse w = new WOResponse();

		w.setHeader( targetURL, HEADER_REDIRECT_LOCATION );
		w.setStatus( 302 );
		w.setHeader( MIME_TYPE_HTML, HEADER_CONTENT_TYPE );
		w.setHeader( "0", HEADER_CONTENT_LENGTH );

		return w;
	}

	/**
	 * This method creates a WOResponse with a permanent (301) redirect to the specified URL
	 *
	 * @param targetURL The URL to redirect to
	 */
	public static WOResponse redirectPermanent( String targetURL ) {
		WOResponse w = new WOResponse();

		w.setHeader( targetURL, HEADER_REDIRECT_LOCATION );
		w.setStatus( 301 );
		w.setHeader( MIME_TYPE_HTML, HEADER_CONTENT_TYPE );
		w.setHeader( "0", HEADER_CONTENT_LENGTH );

		return w;
	}

	/**
	 * Attempts to decode a referer string and get the host name from it.
	 */
	public static String hostFromURL( String url ) {
		int beginningIndex = url.indexOf( "//" );
		int endIndex = url.indexOf( "/", beginningIndex + 2 );
		return url.substring( beginningIndex + 2, endIndex );
	}

	/**
	 * Attempts to decode a top level domain
	 */
	public static String domain( WORequest request ) {
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

		String numericString = StringUtilities.replace( host, ".", "" );

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
	 * An adaptor-agnostic way of determining the requested host name.
	 */
	public static String host( WORequest request ) {
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
	 * Returns the user agent string of the guest.
	 */
	public static String userAgent( WORequest r ) {
		return r.headerForKey( HEADER_USER_AGENT );
	}

	/**
	 * Makes a filename cross-platform and cross browser friendly.
	 */
	public static String makeFilenameURLFriendly( String fileName, String extension ) {

		if( StringUtilities.hasValue( fileName ) ) {
			if( fileName.length() > 100 ) {
				fileName = fileName.substring( 0, 100 );
			}

			fileName = StringUtilities.replace( fileName, "/", "_" );
			fileName = StringUtilities.replace( fileName, "\\", "_" );
			fileName = StringUtilities.replace( fileName, "\"", "_" );
			fileName = StringUtilities.replace( fileName, ":", "_" );
		}
		else {
			fileName = "Untitled";
		}

		if( StringUtilities.hasValue( extension ) ) {
			if( !fileName.toLowerCase().endsWith( extension.toLowerCase() ) ) {
				fileName = fileName + "." + extension;
			}
		}

		return fileName;
	}

	/**
	 * If the WO app is used as a 404 handler, this method returns the requested URL.
	 */
	public static String redirectURL( WORequest r ) {
		return r.headerForKey( HEADER_REDIRECT_URL );
	}

	/**
	 * If the WO app is used as a 404 handler, this method returns the query string part of the requested URI.
	 */
	public static String redirectQueryString( WORequest r ) {
		return r.headerForKey( HEADER_REDIRECT_QUERY_STRING );
	}

	/**
	 * If the WO app is used as a 404 handler, this method returns the requested URL (that failed).
	 */
	public static String contentEncoding( WOResponse r ) {
		return r.headerForKey( HEADER_CONTENT_ENCODING );
	}

	public static WOResponse responseWithDataAndMimeType( String filename, NSData data, String mimeType ) {
		return responseWithDataAndMimeType( filename, data, mimeType, false );
	}

	/**
	 * Creates a WOResponse containing the given data.
	 */
	public static WOResponse responseWithDataAndMimeType( String filename, byte[] bytes, String mimeType ) {
		return responseWithDataAndMimeType( filename, bytes, mimeType, false );
	}

	/**
	 * Creates a WOResponse containing the given data.
	 */
	public static WOResponse responseWithDataAndMimeType( String filename, byte[] bytes, String mimeType, boolean forceDownload ) {

		NSData data = NSData.EmptyData;

		if( bytes != null ) {
			data = new NSData( bytes );
		}

		return responseWithDataAndMimeType( filename, data, mimeType, forceDownload );
	}

	/**
	 * Creates a WOResponse containing the given string, encoded in UTF-8.
	 */
	public static WOResponse responseWithDataAndMimeType( String filename, String string, String mimeType ) {

		if( !StringUtilities.hasValue( string ) ) {
			string = "";
		}

		NSData data = NSData.EmptyData;

		try {
			data = new NSData( string.getBytes( "UTF-8" ) );
		}
		catch( UnsupportedEncodingException e ) {
			logger.debug( "Attempted to convert string to unsupported encoding", e );
		}

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
		response.setHeader( disposition + ";filename=\"" + filename + "\"", HEADER_CONTENT_DISPOSITION );
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
		response.setHeader( disposition + ";filename=\"" + filename + "\"", HEADER_CONTENT_DISPOSITION );
		response.removeHeadersForKey( HEADER_CACHE_CONTROL );
		response.removeHeadersForKey( HEADER_PRAGMA );
		response.removeHeadersForKey( HEADER_EXPIRES );
		response.setContentStream( stream, 32000, length );
		return response;
	}

	/**
	 * @return Response with HTTP status 404
	 */
	public static WOResponse response404() {
		return statusResponse( 404, null );
	}

	/**
	 * @return Response with HTTP status 500.
	 */
	public static WOResponse response500() {
		return statusResponse( 500, null );
	}

	/**
	 * @return A response with the given status and, if specified and HTML content string displayed to the user.
	 */
	public static WOResponse statusResponse( int status, String htmlContent ) {
		WOResponse response = new WOResponse();

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
	public static void resetCookieHeaderInResponse( WOResponse response ) {

		NSMutableArray<String> cookieHeaderStrings = new NSMutableArray<String>();

		for( WOCookie cookie : response.cookies() ) {
			cookieHeaderStrings.addObject( cookie.headerString() );
		}

		response.setHeaders( cookieHeaderStrings, HEADER_SET_COOKIE );
	}

	public static String cookieHost( WORequest request ) {

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
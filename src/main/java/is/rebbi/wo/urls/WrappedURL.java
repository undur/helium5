package is.rebbi.wo.urls;

/**
 * Wraps URL paths for easy access to its components.
 *
 * - only stores the path (no hostname, no protocol) - does not differentiate between relative and absolute urls
 */

public class WrappedURL {

	/**
	 * The stored full URL, without preceding slashes
	 */
	private String _url;

	/**
	 * cached copy of the url's elements
	 */
	private String[] _pathElements;

	/**
	 * Instances are constructed using the create() method.
	 */
	private WrappedURL() {}

	public static WrappedURL create( String url ) {

		if( url == null ) {
			url = "";
		}

		if( url.startsWith( "/" ) ) {
			url = url.substring( 1 );
		}

		if( url.endsWith( "/" ) ) {
			url = url.substring( 0, url.length() - 1 );
		}

		WrappedURL object = new WrappedURL();
		object._url = url;
		return object;
	}

	@Override
	public String toString() {
		return _url;
	}

	private String[] pathElements() {
		if( _pathElements == null ) {
			_pathElements = _url.split( "/" );
		}

		return _pathElements;
	}

	public String url() {
		return _url;
	}

	/**
	 * @return The string value at [index] in the path. defaultValue if [index] does not exist.
	 */
	public String getString( int index, String defaultValue ) {

		if( index > pathElements().length - 1 ) {
			return defaultValue;
		}

		return pathElements()[index];
	}

	/**
	 * @return The string value at [index] in the path. null if [index] does not exist.
	 */
	public String getString( int index ) {
		return getString( index, null );
	}

	/**
	 * @return The integer value at [index] in the path. defaultValue if [index] does not exist.
	 */
	public Integer getInteger( int index, Integer defaultValue ) {
		String value = getString( index );

		if( value == null ) {
			return defaultValue;
		}

		return Integer.valueOf( value );
	}

	/**
	 * @return The integer value at [index] in the path. null if [index] does not exist.
	 */
	public Integer getInteger( int index ) {
		return getInteger( index, null );
	}

	public int length() {
		return pathElements().length;
	}
}
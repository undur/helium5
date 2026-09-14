package is.rebbi.wo.util;

import er.extensions.foundation.ERXProperties;

/**
 * Access to Helium's properties
 */

public class USSettings {

	/**
	 * Prefix for our properties
	 */
	private static final String PROPERTY_PREFIX = "concept";

	/**
	 * Available properties
	 */
	private static final String NAME = "name";
	private static final String DEFAULT_PASSWORD = "defaultPassword";
	private static final String DEFAULT_USERNAME = "defaultUsername";
	private static final String DEFAULT_LOOK_NAME = "defaultLookName";
	private static final String DEFAULT_DOMAIN_NAME = "defaultDomainName";
	private static final String GENERATE_FRIENDLY_URLS = "generateFriendlyURLs";
	private static final String VIEW_TOOLS_COMPONENT_NAME = "viewToolsComponentName";
	private static final String DEFAULT_EDIT_LOOK_NAME = "defaultEditLookName";
	private static final String START_PAGE_NAME = "startPageName";

	/**
	 * No instances.
	 */
	private USSettings() {}

	/**
	 * @return Name of setting on the format of a property.
	 */
	private static String p( String propertyName ) {
		return PROPERTY_PREFIX + "." + propertyName;
	}

	private static String stringForKey( String key ) {
		return stringForKey( key, null );
	}

	private static String stringForKey( String key, String defaultValue ) {
		String value = ERXProperties.stringForKey( p( key ) );

		if( value == null ) {
			value = defaultValue;
		}

		return value;
	}

	private static boolean booleanForKey( final String key ) {
		final String stringValue = stringForKey( key );

		if( stringValue == null ) {
			return false;
		}

		return stringValue.toLowerCase().equals( "true" );
	}

	/**
	 * Indicates if SEO should be active.
	 */
	public static boolean generateFriendlyURLs() {
		return booleanForKey( GENERATE_FRIENDLY_URLS );
	}

	/**
	 * @return The ID of the group that contains all users.
	 */
	public static String name() {
		return stringForKey( NAME );
	}

	/**
	 * @return The default admin username.
	 */
	public static String adminUsername() {
		return stringForKey( DEFAULT_USERNAME );
	}

	/**
	 * @return The default admin password.
	 */
	public static String adminPassword() {
		return stringForKey( DEFAULT_PASSWORD );
	}

	/**
	 * @return The domain to use when generating URLs.
	 */
	public static String defaultDomainName() {
		return stringForKey( DEFAULT_DOMAIN_NAME );
	}

	/**
	 * @return Name of the look component on the client side.
	 */
	public static String defaultLookName() {
		return stringForKey( DEFAULT_LOOK_NAME );
	}

	/**
	 * @return Name of the look component on the client side.
	 */
	public static String defaultEditLookName() {
		return stringForKey( DEFAULT_EDIT_LOOK_NAME );
	}

	/**
	 * @return Name of the viewTools component
	 */
	public static String viewToolsComponentName() {
		return stringForKey( VIEW_TOOLS_COMPONENT_NAME );
	}

	/**
	 * @return Name of the component the admin look's "Forsíða" opens; the framework's own start page unless the app names its own
	 */
	public static String startPageName() {
		return stringForKey( START_PAGE_NAME, "USStartPage" );
	}
}
package is.rebbi.wo.util;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webobjects.appserver.WOApplication;
import com.webobjects.appserver.WORequest;
import com.webobjects.foundation.NSArray;

import er.extensions.foundation.ERXProperties;
import is.rebbi.core.util.StringUtilities;

/**
 * SWSettings simplifies access to the system's settings and properties.
 */

public class SWSettings {

    private static final Logger logger = LoggerFactory.getLogger( SWSettings.class );

    /**
     * Settings loaded from local settings file.
     */
    private static SWDictionary<String, Object> _localDictionary;

    /**
     * Settings loaded from the application bundle.
     */
    private static SWDictionary<String, Object> _appDictionary;

    /**
     * Non-persistent settings (stored in memory).
     */
    private static SWDictionary<String, Object> _tempDictionary;

    private static String _homePath;
    private static String _settingsPath;
    private static String _documentPath;
    private static String _imageURL;
    private static String _imagePath;

    /**
     * All properties use this prefix.
     */
    private static final String PROPERTY_PREFIX = "concept";

    /**
     * Available settings.
     */
    private static final String NAME = "name";
    private static final String HOME = "home";
    private static final String SETTINGS_PATH = "settingsPath";
    private static final String INDEX_PATH = "indexPath";
    private static final String DOCUMENT_PATH = "documentPath";
    private static final String THUMBNAIL_PATH = "thumbnailPath";
    private static final String DATA_PATH = "dataPath";
    private static final String ENABLE_PRIVILEGES = "enablePrivileges";
    private static final String ALL_USER_GROUP_ID = "allUserGroupID";
    private static final String ONLY_SHOW_GROUP_PRIVILEGES = "onlyShowGroupPrivileges";
    private static final String SESSION_TIME_OUT_IN_MINUTES = "sessionTimeOut";
    private static final String NO_PAGE_FOUND_ERROR_PAGE_LINKING_NAME = "noPageFoundErrorPageLinkingName";
    private static final String WEBMASTER_EMAIL = "webmasterEmail";
    private static final String WEBMASTER_NAME = "webmasterName";
    private static final String DEFAULT_PASSWORD = "defaultPassword";
    private static final String DEFAULT_USERNAME = "defaultUsername";
    private static final String STORAGE_CLASS_NAME = "storageClassName";
    private static final String DEFAULT_LOOK_NAME = "defaultLookName";
    private static final String DEFAULT_DOMAIN_NAME = "defaultDomainName";
    private static final String GENERATE_FRIENDLY_URLS = "generateFriendlyURLs";
    private static final String FORCE_DEVELOPMENT_URLS = "forceDevelopmentURLs";
    private static final String ALLOW_USER_REGISTRATION = "allowUserRegistration";
    private static final String IMAGE_PATH = "imageLocationOnDisk";
    private static final String IMAGE_URL = "imageLocationOnServer";
    public static final String VIEW_TOOLS_COMPONENT_NAME = "viewToolsComponentName";
    public static final String DEFAULT_EDIT_LOOK_NAME = "defaultEditLookName";

    /**
     * No instances.
     */
    private SWSettings() {}

    public static void register( String home ) {
        _homePath = home;

        if( WOApplication.application() != null ) {
            logger.info( "Loading application settings" );
            String appSettingsString = USUtilities.stringFromResource( "sw.conf", null );
            _appDictionary = new SWDictionary<>( appSettingsString );
        }
        else {
            _appDictionary = new SWDictionary<>();
            logger.info( "No WOApplication object present. This probably means you're running a unit test, so I'm skipping load of application settings" );
        }

        _tempDictionary = new SWDictionary<>();

        logger.info( "Loading local settings: " + localSettingsPath() );
        File localSettingsFile = new File( localSettingsPath() );

        if( localSettingsFile.exists() ) {
            _localDictionary = new SWDictionary<>( localSettingsFile );
        }
        else {
            _localDictionary = new SWDictionary<>();
        }
    }

    public static void register() {
        register( null );
    }

    /**
     * Settings stored in memory.
     */
    private static SWDictionary<String, Object> tempDictionary() {
        return _tempDictionary;
    }

    /**
     * Settings stored in the application bundle.
     */
    private static SWDictionary<String, Object> appDictionary() {
        return _appDictionary;
    }

    /**
     * Load settings stored in $home.
     */
    public static SWDictionary<String, Object> localDictionary() {
        return _localDictionary;
    }

    public static NSArray<String> previewSizes() {
        String str = SWSettings.stringForKey( "pictureSizes" );
        return NSArray.componentsSeparatedByString( str, "," );
    }

    /**
     * @return Name of setting on the format of a property.
     */
    private static String p( String propertyName ) {
        return PROPERTY_PREFIX + "." + propertyName;
    }

    /**
     * Returns value of specified setting.
     */
    private static Object objectForKey( String key ) {
        return objectForKey( key, null );
    }

    /**
     * Returns value of specified setting, defaultValue if not set.
     */
    private static Object objectForKey( String key, Object defaultValue ) {

        Object value = null;

        if( tempDictionary() != null ) {
            value = tempDictionary().valueForKey( key );
        }

        if( value == null ) {
            if( localDictionary() != null ) {
                value = localDictionary().valueForKey( key );
            }
        }

        if( value == null ) {
            if( appDictionary() != null ) {
                value = appDictionary().valueForKey( key );
            }
        }

        if( value == null ) {
            value = ERXProperties.stringForKey( p( key ) );
        }

        if( value == null ) {
            value = defaultValue;
        }

        return value;
    }

    /**
     * Sets the specified setting value.
     */
    public static void setObjectForKey( Object value, String key ) {
        localDictionary().takeValueForKey( value, key );
    }

    /**
     * Sets the specified setting value for the duration of the application run.
     */
    public static void setTempObjectForKey( Object value, String key ) {
        tempDictionary().takeValueForKey( value, key );
    }

    public static String stringForKey( String key ) {
        return (String)objectForKey( key );
    }

    public static String stringForKey( String key, String defaultValue ) {
        return (String)objectForKey( key, defaultValue );
    }

    public static boolean booleanForKey( final String key ) {
    	final String stringValue = stringForKey( key );

    	if( stringValue == null ) {
    		return false;
    	}

    	return stringValue.toLowerCase().equals( "true" );
    }

    public static Integer integerForKey( String key ) {
    	final String stringValue = stringForKey( key );

    	if( stringValue == null ) {
    		return null;
    	}

    	return Integer.parseInt( stringValue );
    }

    /**
     * Returns the location of the settings file
     */
    private static String localSettingsPath() {
        return stringForKey( SETTINGS_PATH, home( "conf/sw.conf" ) );
    }

    /**
     * Indicates if SEO should be active.
     */
    public static boolean generateFriendlyURLs( WORequest r ) {
        return booleanForKey( GENERATE_FRIENDLY_URLS );
    }

    /**
     * Indicates if SEO should be active.
     */
    public static boolean forceDevelopmentURLs() {
        return booleanForKey( FORCE_DEVELOPMENT_URLS );
    }

    /**
     * Indicates if access privileges are enabled for this system.
     */
    public static boolean privilegesEnabled() {
        return booleanForKey( ENABLE_PRIVILEGES );
    }

    /**
     * @return A boolean indicating if privileges can be granted to groups only.
     */
    public static boolean onlyShowGroupPrivileges() {
        return booleanForKey( ONLY_SHOW_GROUP_PRIVILEGES );
    }

    /**
     * @return The ID of the group that contains all users.
     */
    public static Integer allUsersGroupID() {
        return integerForKey( ALL_USER_GROUP_ID );
    }

    /**
     * @return The ID of the group that contains all users.
     */
    public static String name() {
        return stringForKey( NAME );
    }

    public static void setAllUsersGroupID( Integer value ) {
        setObjectForKey( value, ALL_USER_GROUP_ID );
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
     * @return The web master e-mail address.
     */
    public static String webmasterName() {
        return stringForKey( WEBMASTER_NAME );
    }

    /**
     * @return The web master e-mail address.
     */
    public static String webmasterEmail() {
        return stringForKey( WEBMASTER_EMAIL );
    }

    /**
     * @return Name of class used to handle storage of binary documents.
     */
    public static String storageClassName() {
        return stringForKey( STORAGE_CLASS_NAME );
    }

    /**
     * @return The location of document data on disk. Specify a folder.
     */
    public static String home() {
        if( _homePath == null ) {
            _homePath = stringForKey( HOME );
        }

        return _homePath;
    }

    /**
     * @return A path within the home folder.
     */
    private static String home( String path ) {
        return home() + "/" + path;
    }

    public static String dataPath() {
        return stringForKey( DATA_PATH, home( "data" ) );
    }

    /**
     * @return The location of document data on disk. Specify a folder.
     */
    public static String documentPath() {
        if( _documentPath == null ) {
            _documentPath = SWSettings.stringForKey( "documentLocationOnDisk" );

            if( !StringUtilities.hasValue( _documentPath ) ) {
                _documentPath = stringForKey( DOCUMENT_PATH, home( "docs" ) );
            }

            if( !StringUtilities.hasValue( _documentPath ) ) {
                throw new RuntimeException( "You must define a document path to store documents using FileStorage." );
            }

            if( _documentPath != null ) {
                if( _documentPath.endsWith( "/" ) ) {
                    _documentPath = _documentPath.substring( 0, _documentPath.length() - 1 );
                }
            }
        }

        return _documentPath;
    }

    public static String imageURL() {
        if( _imageURL == null ) {
            _imageURL = stringForKey( IMAGE_URL );

            if( _imageURL != null ) {
                if( _imageURL.endsWith( "/" ) ) {
                    _imageURL = _imageURL.substring( 0, _imageURL.length() - 1 );
                }
            }
        }

        return _imageURL;
    }

    public static String imagePath() {
        if( _imagePath == null ) {
            _imagePath = stringForKey( IMAGE_PATH, home( "img" ) );

            if( _imagePath.endsWith( "/" ) ) {
                _imagePath = _imagePath.substring( 0, _imagePath.length() - 1 );
            }
        }

        return _imagePath;
    }

    public static String settingsPath() {
        if( _settingsPath == null ) {
            _settingsPath = home( "conf" );
        }

        return _settingsPath;
    }

    /**
     * @return Location of the index on disk.
     */
    public static final String indexPath() {
        return stringForKey( INDEX_PATH, home( "index" ) );
    }

    /**
     * @return Base Path for thumbnails.
     */
    public static final String thumbnailPath() {
        return stringForKey( THUMBNAIL_PATH, home( "thumbs" ) );
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
     * @return The name of the SWPage to return in case no page is found (404)
     */
    public static String noPageFoundErrorPageLinkingName() {
        return stringForKey( NO_PAGE_FOUND_ERROR_PAGE_LINKING_NAME );
    }

    /**
     * @return The session timeout
     */
    public static Integer sessionTimeOut() {
        return integerForKey( SESSION_TIME_OUT_IN_MINUTES );
    }

    public static boolean allowUserRegistration() {
        return booleanForKey( ALLOW_USER_REGISTRATION );
    }

    public static String viewToolsComponentName() {
        return stringForKey( VIEW_TOOLS_COMPONENT_NAME );
    }

    public static String smtpHost() {
        return ERXProperties.stringForKey( "er.javamail.smtpHost" );
    }

    public static String smtpPort() {
        return ERXProperties.stringForKey( "er.javamail.smtpPort" );
    }

    public static String smtpUsername() {
        return ERXProperties.stringForKey( "er.javamail.smtpUser" );
    }

    public static String smtpPassword() {
        return ERXProperties.stringForKey( "er.javamail.smtpPassword" );
    }
}
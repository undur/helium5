package is.rebbi.wo.urls;

import com.webobjects.appserver.WOContext;
import com.webobjects.eocontrol.EOEditingContext;

import er.extensions.eof.ERXGenericRecord;
import is.rebbi.wo.util.PKSerializerEOF;
import is.rebbi.wo.util.SWSettings;

public class URLProviderEO extends URLProviderPersistent {

	@Override
	public String urlForObject( Object object, WOContext context ) {
		ERXGenericRecord eo = (ERXGenericRecord)object;
		String serializedID = PKSerializerEOF.serialize( eo );
		return urlForObject( eo.entityName(), serializedID, context );
	}

	/**
	 * @return A URL for the given object.
	 */
	@Override
	public String urlForObject( String entityName, Object serializedID, WOContext context ) {
		String typeIdentifier = urlPrefix( entityName );
		String objectIdentifier = objectIdentifier( serializedID );

		if( context == null ) {
			return urlWithDomain( typeIdentifier, objectIdentifier );
		}
		else {
			String url = urlWithoutDomain( typeIdentifier, objectIdentifier );

			if( !SWSettings.generateFriendlyURLs( context.request() ) ) {
				url = URLUtilities.makeURLDeveloperFriendly( url, context );
			}

			return url;
		}
	}

	/**
	 * @return The object the user wanted from the URL.
	 */
	public static ERXGenericRecord objectFromURL( EOEditingContext ec, String url ) {
		String[] smu = url.split( "/" );
		String urlPrefix = smu[2];
		String objectName = smu[3];
		return objectFromIdentifiers( ec, urlPrefix, objectName );
	}

	/**
	 * @return The object specified by the parameters.
	 */
	private static ERXGenericRecord objectFromIdentifiers( EOEditingContext ec, String typeIdentifier, String objectIdentifier ) {

		if( objectIdentiferIsGeneric( objectIdentifier ) ) {
			String entityName = entityNameFromTypeIdentifier( typeIdentifier );
			String identifier = objectIdentifier.substring( PK_IDENTIFIER_PREFIX.length(), objectIdentifier.length() );
			return PKSerializerEOF.eo( ec, entityName, identifier );
		}
		else {
			throw new RuntimeException( "Unsupported URL format" );
		}
	}
}
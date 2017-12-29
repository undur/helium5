package is.rebbi.wo.util;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Locale;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.internal.bind.DateTypeAdapter;
import com.google.gson.internal.bind.util.ISO8601Utils;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import com.webobjects.appserver.WORequest;
import com.webobjects.appserver.WOResponse;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSDictionary;
import com.webobjects.foundation.NSKeyValueCodingAdditions;
import com.webobjects.foundation.NSMutableArray;
import com.webobjects.foundation.NSMutableDictionary;

import er.extensions.appserver.ERXResponse;
import is.rebbi.core.util.DateUtilities;

public class USJson {

	private static final Gson _gson = new GsonBuilder()
			.registerTypeHierarchyAdapter( Date.class, new DateTypeAdapter() )
			.registerTypeAdapter( LocalDate.class, new LocalDateThatLooksLikeDateAdapter() )
			.setPrettyPrinting()
			.create();

	public static String toJson( Object object ) {
		return _gson.toJson( object );
	}

	public static <T> T fromJson( String jsonString, Type type ) {
		return _gson.fromJson( jsonString, type );
	}

	public static <T> T fromJson( String jsonString, Class<T> clazz ) {
		return _gson.fromJson( jsonString, clazz );
	}

	/**
	 * @return A response containing the given object serialized.
	 */
	public static WOResponse response( Object object ) {
		String json = USJson.toJson( object );
		return responseWithString( json );
	}

	/**
	 * @return A response containing the given String.
	 */
	public static WOResponse responseWithString( String contentString ) {
		WOResponse r = new ERXResponse();
		r.setHeader( "text/plain; charset=utf-8", "content-type" );
		r.disableClientCaching();
		r.setContent( contentString );
		return r;
	}

	/**
	 * @return An object deserialized from the content of the given request.
	 */
	public static <E> E fromRequest( WORequest request, Class<E> clazz ) {
		E fromJson = USJson.fromJson( request.contentString(), clazz );

		if( fromJson == null ) {
			try {
				fromJson = clazz.newInstance();
			}
			catch( InstantiationException | IllegalAccessException e ) {
				e.printStackTrace();
			}
		}

		return fromJson;
	}

	/**
	 * @return An object deserialized from the content of the given request.
	 */
	public static <E> E fromRequest( WORequest request, Type type ) {
		E fromJson = USJson.fromJson( request.contentString(), type );

		//		Temporarily disabled 2014-10-13
		//
		//		if( fromJson == null ) {
		//			try {
		//				fromJson = clazz.newInstance();
		//			}
		//			catch( InstantiationException | IllegalAccessException e ) {
		//				e.printStackTrace();
		//			}
		//		}

		return fromJson;
	}

	public static NSArray<NSDictionary<String, Object>> valuesForKeyPaths( NSArray<?> objects, NSArray<String> keyPaths, NSDictionary<String, String> replacementKeys ) {
		NSMutableArray<NSDictionary<String, Object>> results = new NSMutableArray<>();

		for( Object object : objects ) {
			results.addObject( valuesForKeyPaths( object, keyPaths, replacementKeys ) );
		}

		return results;
	}

	public static NSDictionary<String, Object> valuesForKeyPaths( Object object, NSArray<String> keyPaths, NSDictionary<String, String> replacementKeys ) {
		NSMutableDictionary<String, Object> d = new NSMutableDictionary<>();

		for( String keyPath : keyPaths ) {
			Object value = NSKeyValueCodingAdditions.Utility.valueForKeyPath( object, keyPath );

			if( value != null ) {
				if( replacementKeys != null ) {
					String newKeyPath = replacementKeys.objectForKey( keyPath );

					if( newKeyPath != null ) {
						keyPath = newKeyPath;
					}
				}

				d.setObjectForKey( value, keyPath );
			}
		}

		return d;
	}

	public static NSDictionary<String, Object> remapKey( NSDictionary<String, Object> d, String oldKey, String newKey ) {
		d = d.mutableClone();

		Object value = d.objectForKey( oldKey );

		if( value != null ) {
			d.remove( oldKey );
			((NSMutableDictionary<String, Object>)d).setObjectForKey( value, newKey );
		}

		return d;
	}

	private static class LocalDateThatLooksLikeDateAdapter extends TypeAdapter<LocalDate> {

		private final DateFormat enUsFormat = DateFormat.getDateTimeInstance( DateFormat.DEFAULT, DateFormat.DEFAULT, Locale.US );
		private final DateFormat localFormat = DateFormat.getDateTimeInstance( DateFormat.DEFAULT, DateFormat.DEFAULT );

		@Override
		public LocalDate read( JsonReader in ) throws IOException {
			if( in.peek() == JsonToken.NULL ) {
				in.nextNull();
				return null;
			}
			return deserializeToDate( in.nextString() );
		}

		private synchronized LocalDate deserializeToDate( String json ) {
			try {
				return DateUtilities.toLocalDate( localFormat.parse( json ) );
			}
			catch( ParseException ignored ) {}
			try {
				return DateUtilities.toLocalDate( enUsFormat.parse( json ) );
			}
			catch( ParseException ignored ) {}
			try {
				return DateUtilities.toLocalDate( ISO8601Utils.parse( json, new ParsePosition( 0 ) ) );
			}
			catch( ParseException e ) {
				throw new JsonSyntaxException( json, e );
			}
		}

		@Override
		public synchronized void write( JsonWriter out, LocalDate value ) throws IOException {
			if( value == null ) {
				out.nullValue();
				return;
			}
			String dateFormatAsString = enUsFormat.format( Date.from( value.atStartOfDay().atZone( ZoneId.systemDefault() ).toInstant() ) );
			out.value( dateFormatAsString );
		}
	}
}
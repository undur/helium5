package is.rebbi.wo.components.fields;

import java.text.Format;
import java.text.SimpleDateFormat;

import com.webobjects.appserver.WOContext;

import er.extensions.components.ERXStatelessComponent;
import is.rebbi.core.formatters.FormatterWrapperNullSafe;

/**
 * A field for selecting date and time.
 *
 * @deprecated Should be replaced with the new components for java.time
 */

@Deprecated
public class USDateAndTimeField extends ERXStatelessComponent {

	public USDateAndTimeField( WOContext context ) {
		super( context );
	}

	public Format dateFormatterWithTime() {
		SimpleDateFormat format = new SimpleDateFormat( "d.MM.yyyy HH:mm" );
		return new FormatterWrapperNullSafe( format );
	}
}
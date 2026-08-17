package is.rebbi.wo.components.markdown;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.commonmark.Extension;
import org.commonmark.ext.gfm.tables.TablesExtension;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

import com.webobjects.appserver.WOContext;

import er.extensions.components.ERXComponent;

/**
 * A page displaying a markdown resource rendered as HTML — typically documentation bundled with
 * the application, linked from the pages it documents (see {@link MDLink}).
 *
 * <p>The resource is resolved from the classpath (e.g. "docs/alagning.md" for a file in
 * src/main/resources/docs/), so documentation ships inside the application bundle and is always
 * the version the running code was built with.
 */
public class MDPage extends ERXComponent {

	private String _resourceName;

	public MDPage( WOContext context ) {
		super( context );
	}

	public String resourceName() {
		return _resourceName;
	}

	public void setResourceName( String value ) {
		_resourceName = value;
	}

	/**
	 * @return The markdown resource rendered to HTML (with GFM table support), or an error
	 *         description if the resource can't be found
	 */
	public String html() {
		final String markdown = loadResource();

		if( markdown == null ) {
			return "<p>Fann ekki skjalið <code>%s</code></p>".formatted( _resourceName );
		}

		final List<Extension> extensions = List.of( TablesExtension.create() );
		final Parser parser = Parser.builder().extensions( extensions ).build();
		final HtmlRenderer renderer = HtmlRenderer.builder().extensions( extensions ).build();
		return renderer.render( parser.parse( markdown ) );
	}

	private String loadResource() {
		if( _resourceName == null ) {
			return null;
		}

		try( InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream( _resourceName ) ) {
			if( stream == null ) {
				return null;
			}

			return new String( stream.readAllBytes(), StandardCharsets.UTF_8 );
		}
		catch( IOException e ) {
			throw new RuntimeException( "Failed to read markdown resource: " + _resourceName, e );
		}
	}
}

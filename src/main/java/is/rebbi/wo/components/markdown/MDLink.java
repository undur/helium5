package is.rebbi.wo.components.markdown;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WOContext;

import er.extensions.components.ERXComponent;

/**
 * A link opening a bundled markdown resource rendered as HTML (in {@link MDPage}).
 *
 * <p>Lets any page link to the documentation that describes it:
 *
 * <pre>&lt;wo:MDLink resourceName="docs/alagning.md"&gt;Skjölun&lt;/wo:MDLink&gt;</pre>
 *
 * <p>Bindings:
 * <ul>
 *   <li><b>resourceName</b> — classpath path of the markdown resource (e.g. "docs/alagning.md")</li>
 *   <li><b>class</b> — optional CSS class for the generated link</li>
 * </ul>
 */
public class MDLink extends ERXComponent {

	public MDLink( WOContext context ) {
		super( context );
	}

	@Override
	public boolean synchronizesVariablesWithBindings() {
		return false;
	}

	public String linkClass() {
		return (String)valueForBinding( "class" );
	}

	public WOActionResults open() {
		final MDPage nextPage = pageWithName( MDPage.class );
		nextPage.setResourceName( (String)valueForBinding( "resourceName" ) );
		return nextPage;
	}
}

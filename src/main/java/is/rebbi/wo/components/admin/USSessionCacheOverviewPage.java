package is.rebbi.wo.components.admin;

import com.webobjects.appserver.WOContext;

import er.extensions.components.ERXComponent;

/**
 * ERExtensions' session cache overview (page caches per session, reuse statistics) wrapped in the
 * application's admin look. Point a menu item or route at this page; the actual reporting UI lives
 * in {@link er.extensions.appserver.cachemonitor.ERXSessionCacheOverviewPage}.
 */
public class USSessionCacheOverviewPage extends ERXComponent {

	public USSessionCacheOverviewPage( WOContext context ) {
		super( context );
	}
}

package is.rebbi.wo.urls.handlers;

import java.util.function.BiFunction;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WOContext;

import er.extensions.appserver.ERXApplication;
import is.rebbi.wo.components.admin.USLoginPage;

public class URLHandlerLogin implements URLHandler {

	@Override
	public String prefix() {
		return "/login";
	}

	@Override
	public BiFunction<String, WOContext, WOActionResults> execute() {
		return ( url, context ) -> {
			return ERXApplication.erxApplication().pageWithName( USLoginPage.class, context );
		};
	}
}
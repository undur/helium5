package is.rebbi.wo.components.admin.look;

import java.util.ArrayList;
import java.util.List;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WOContext;
import com.webobjects.foundation.NSDictionary;

import is.rebbi.wo.components.USViewPage;
import is.rebbi.wo.menu.USMenu;

public class USAdminLook extends USViewPage {

	public Object currentNotification;
	public Object selectedObject;

	public int numberOfNotificationsToShow = 5;

	public USAdminLook( WOContext context ) {
		super( context );
	}

	public USMenu menu() {
		return USMenu.defaultMenu();
	}

	public NSDictionary user() {
		return NSDictionary.emptyDictionary();
	}

	public boolean hasNotifications() {
		return notifications().size() > 0;
	}

	public List<?> notifications() {
		return new ArrayList<>();
	}

	public WOActionResults logout() {
		/*
		session().setUser( null );
		return pageWithName( OPLogin.class );
		*/
		return null;
	}

	public WOActionResults userSettings() {
		/*
		return Inspection.inspectObjectInContextUsingComponent( user(), context(), OPUserDetailPage.class );
		*/
		return null;
	}
}
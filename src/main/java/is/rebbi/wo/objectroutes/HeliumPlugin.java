package is.rebbi.wo.objectroutes;

import java.util.List;

import er.extensions.ERXExtensions;
import er.extensions.ERXPlugin;
import er.extensions.appserver.ERXApplication;
import er.extensions.routing.ERXRouter;

/**
 * Helium's setup in an application including it: its object routes ({@link ObjectRoutes}), declared in a table of their
 * own, so its elements link to objects with nothing to set up in the application
 */
public class HeliumPlugin implements ERXPlugin {

	@Override
	public List<Class<? extends ERXPlugin>> requires() {
		return List.of( ERXExtensions.class );
	}

	@Override
	public void finishInitialization( final ERXApplication application ) {
		ERXRouter.declare( "helium", ObjectRoutes::declare );
	}
}

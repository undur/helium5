package is.rebbi.wo.urls;

/**
 * The basis for route URLs
 */

public class RouteURLPattern {

	private RouteURLPattern( final String patternString ) {

	}

	public static RouteURLPattern of( final String patternString ) {
		return new RouteURLPattern( patternString );
	}

	/**
	 * @return true if the given URL compiles to this pattern
	 */
//	public boolean matches( final String URL ) {
//
//	}

	private static class RouteURLPathElement {
		private boolean _isParameter;

		public RouteURLPathElement( final String sourceString ) {

		}
	}
}
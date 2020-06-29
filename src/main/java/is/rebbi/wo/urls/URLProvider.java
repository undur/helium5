package is.rebbi.wo.urls;

/**
 * A URLProvider should be able to generate URLs for objects of a specific type.
 */

public abstract interface URLProvider<E> {

	public abstract String urlForObject( E object );
}
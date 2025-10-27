package is.rebbi.wo.objectroutes.urls;

/**
 * A URLProvider should be able to generate URLs for objects of a specific type.
 */

public interface URLProvider<E> {

	public String urlForObject( E object );
}
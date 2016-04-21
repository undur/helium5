package is.rebbi.wo.components.admin;

import java.util.List;

import org.apache.cayenne.query.Ordering;
import org.apache.cayenne.query.SortOrder;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WOContext;

import er.extensions.components.ERXStatelessComponent;
import is.rebbi.core.util.ListUtilities;

public class USSort extends ERXStatelessComponent {

	public USSort(WOContext context) {
		super( context );
	}

	public boolean disabled() {
		return booleanValueForBinding( "disabled" );
	}

	public WOActionResults sort() {
		List<Ordering> orderings = orderings();
		String keyPath = keyPath();

		Ordering newOrdering = new Ordering( keyPath, SortOrder.ASCENDING );

		if( ListUtilities.hasObjects( orderings ) ) {
			Ordering firstOrdering = orderings.get( 0 );

			if( firstOrdering.equals( newOrdering ) ) {
				if( newOrdering.isAscending() ) {
					newOrdering.setDescending();
				}
				else {
					newOrdering.setAscending();
				}
				orderings.remove( 0 );
			}
		}

		orderings.add( 0, newOrdering );
		orderings = ListUtilities.maxObjectsFromList( orderings, 3 );
		setValueForBinding( orderings, "orderings" );

		return null;
	}

	private List<Ordering> orderings() {
		return (List<Ordering>)valueForBinding( "orderings" );
	}

	private String keyPath() {
		return (String)valueForBinding( "keyPath" );
	}

	/**
	 * @return The index of the given keyPath in the ordering list.
	 */
	public Integer indexOfKeyPath() {
		for( Ordering ordering : orderings() ) {
			if( ordering.getSortSpecString().equals( keyPath() ) ) {
				return orderings().indexOf( ordering ) + 1;
			}
		}

		return null;
	}

	public boolean isUsedForOrdering() {
		for( Ordering ordering : orderings() ) {
			if( ordering.getSortSpecString().equals( keyPath() ) ) {
				return true;
			}
		}

		return false;
	}

	public String currentLabelClass() {
		return null;
	}

	public String currentIconClass() {
		for( Ordering ordering : orderings() ) {
			if( ordering.getSortSpecString().equals( keyPath() ) ) {
				if( ordering.isAscending() ) {
					return "glyphicon glyphicon glyphicon-sort-by-attributes";
				}
				else {
					return "glyphicon glyphicon glyphicon-sort-by-attributes-alt";
				}
			}
		}

		return null;
	}
}
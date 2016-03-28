package is.rebbi.wo.components;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WODisplayGroup;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSMutableArray;

import er.extensions.components.ERXComponent;

public class USBatchNavigation extends ERXComponent {

	public WODisplayGroup dg;
	public Integer currentBatchIndex;

	public USBatchNavigation( WOContext context ) {
		super( context );
	}

	public WOActionResults selectBatch() {
		dg.setCurrentBatchIndex( currentBatchIndex );
		return null;
	}

	public String classForCurrentBatchButton() {
		String cls = "btn btn-sm btn-default";

		if( currentBatchIndex == dg.currentBatchIndex() ) {
			cls = cls + " active";
		}

		return cls;
	}

	public String classForPreviousButton() {
		String cls = "btn btn-sm btn-default";

		if( noPreviousBatch() ) {
			cls = cls + " disabled";
		}

		return cls;
	}

	public String classForNextButton() {
		String cls = "btn btn-sm btn-default";

		if( noNextBatch() ) {
			cls = cls + " disabled";
		}

		return cls;
	}

	public boolean noPreviousBatch() {
		return dg.currentBatchIndex() == 1;
	}

	public boolean noNextBatch() {
		return dg.currentBatchIndex() == dg.batchCount();
	}

	public WOActionResults displayFirstBatch() {
		dg.setCurrentBatchIndex( 1 );
		return null;
	}

	public WOActionResults displayLastBatch() {
		dg.setCurrentBatchIndex( dg.batchCount() );
		return null;
	}

	public boolean showBatches() {
		return dg.batchCount() < 10;
	}

	int numberOfBatchesToShow = 10;

	public NSArray<Integer> batchNumbers() {
		NSMutableArray<Integer> a = new NSMutableArray<>();

		int batchCount = dg.batchCount();
		int selectedBatch = dg.currentBatchIndex();

		for( int i = 1; i <= batchCount; i++ ) {
			int difference = selectedBatch - i;

			if( difference < 0 ) {
				difference = difference * -1;
			}

			if( difference < 5 ) {
				a.addObject( i );
			}
		}

		return a;
	}
}
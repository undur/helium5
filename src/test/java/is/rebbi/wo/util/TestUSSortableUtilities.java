package is.rebbi.wo.util;

import org.junit.jupiter.api.Test;

import com.webobjects.foundation.NSMutableArray;

public class TestUSSortableUtilities {

	@Test
	public void changeSortOrder() {
		TestSortable ringo = new TestSortable( 2, "Ringo" );
		NSMutableArray<TestSortable> a = before();
		USSortableUtilities.changeSortOrder( ringo, a, -1 );
	}

	private NSMutableArray<TestSortable> before() {
		NSMutableArray<TestSortable> a = new NSMutableArray<>();
		a.addObject( new TestSortable( 0, "John" ) );
		a.addObject( new TestSortable( 1, "Paul" ) );
		a.addObject( new TestSortable( 2, "Ringo" ) );
		a.addObject( new TestSortable( 3, "George" ) );
		return a;
	}

	private NSMutableArray<TestSortable> after() {
		NSMutableArray<TestSortable> a = new NSMutableArray<>();
		a.addObject( new TestSortable( 0, "John" ) );
		a.addObject( new TestSortable( 1, "Ringo" ) );
		a.addObject( new TestSortable( 2, "Paul" ) );
		a.addObject( new TestSortable( 3, "George" ) );
		return a;
	}

	private static class TestSortable implements USSortable {

		private Integer _sortNumber;
		private String _identifier;

		public TestSortable( int sortNumber, String identifier ) {
			setSortNumber( sortNumber );
			setIdentifier( identifier );
		}

		private String identifier() {
			return _identifier;
		}

		private void setIdentifier( String value ) {
			_identifier = value;
		}

		@Override
		public Integer sortNumber() {
			return _sortNumber;
		}

		@Override
		public void setSortNumber( Integer value ) {
			_sortNumber = value;
		}

		@Override
		public int hashCode() {
			return sortNumber();
		}

		@Override
		public boolean equals( Object object ) {
			if( object != null ) {
				return sortNumber().equals( ((TestSortable)object).sortNumber() );
			}

			return false;
		}

		@Override
		public String toString() {
			return "TestSortable [_sortNumber=" + _sortNumber + ", _identifier=" + _identifier + "]";
		}
	}
}
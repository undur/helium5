package is.rebbi.wo.util;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

public class TestUSGenericComparator {

	public class Person {
		public String _icelandicName;

		public Person(String icelandicName) {
			_icelandicName = icelandicName;
		}

		@Override
		public String toString() {
			return "Person [_icelandicName=" + _icelandicName + "]";
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + ((_icelandicName == null) ? 0 : _icelandicName.hashCode());
			return result;
		}

		@Override
		public boolean equals( Object obj ) {
			if( this == obj ) {
				return true;
			}
			if( obj == null ) {
				return false;
			}
			if( getClass() != obj.getClass() ) {
				return false;
			}
			Person other = (Person)obj;
			if( !getOuterType().equals( other.getOuterType() ) ) {
				return false;
			}
			if( _icelandicName == null ) {
				if( other._icelandicName != null ) {
					return false;
				}
			}
			else if( !_icelandicName.equals( other._icelandicName ) ) {
				return false;
			}
			return true;
		}

		private TestUSGenericComparator getOuterType() {
			return TestUSGenericComparator.this;
		}
	}

	@Test
	public void sortSome() {
		List<Person> unsorted = Arrays.asList( new Person( "Þórarinn" ), new Person( "Aðalsteinn" ), new Person( "Ýsleyfur" ), new Person( "Jónas" ), new Person( "Gunnar" ) );
		List<Person> expectedSorted = Arrays.asList( new Person( "Aðalsteinn" ), new Person( "Gunnar" ), new Person( "Jónas" ), new Person( "Ýsleyfur" ), new Person( "Þórarinn" ) );
		Collections.sort( unsorted, new USGenericComparator( "icelandicName", true, true ) );
		assertEquals( expectedSorted, unsorted );
	}
}
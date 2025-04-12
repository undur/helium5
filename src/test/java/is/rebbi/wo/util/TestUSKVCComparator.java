package is.rebbi.wo.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

public class TestUSKVCComparator {

	public record Person( String name ) {}

	@Test
	public void sortSome() {
		final List<Person> unsorted = Arrays.asList(
				new Person( "Þórarinn" ),
				new Person( "ásgeir" ),
				new Person( null ),
				new Person( "Aðalsteinn" ),
				new Person( null ),
				new Person( "þjóðólfur" ),
				new Person( "Ýsleyfur" ),
				null,
				new Person( "þari" ),
				null,
				new Person( "Jónas" ),
				null,
				new Person( "Gunnar" ) );

		List<Person> expectedSorted = Arrays.asList(
				new Person( null ),
				new Person( null ),
				null,
				null,
				null,
				new Person( "Aðalsteinn" ),
				new Person( "ásgeir" ),
				new Person( "Gunnar" ),
				new Person( "Jónas" ),
				new Person( "Ýsleyfur" ),
				new Person( "þari" ),
				new Person( "þjóðólfur" ),
				new Person( "Þórarinn" ) );

		Collections.sort( unsorted, USKVCComparator.of( "name" ) );

		assertEquals( expectedSorted, unsorted );
	}
}
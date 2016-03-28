package is.rebbi.wo.formatters;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

public class TestDurationFormatter {

	@Test
	public void format() {
		DurationFormatter formatter = new DurationFormatter();

		String actual;
		String expected;

		actual = formatter.format( 3600 );
		expected = "1h";
		assertEquals( expected, actual );

		actual = formatter.format( 5400 );
		expected = "1h 30m";
		assertEquals( expected, actual );
	}

	@Test
	public void parseObject() {
		DurationFormatter formatter = new DurationFormatter();

		try {
			Long actual = (Long)formatter.parseObject( "6h" );
		}
		catch( Exception e ) {
			fail();
		}
	}
}

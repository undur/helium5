package is.rebbi.wo.formatters;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TestDurationFormatterDecimal {

	@Test
	public void format() {
		DurationFormatterDecimal formatter = new DurationFormatterDecimal();
		assertEquals( "1", formatter.format( 3600 ) );
		assertEquals( "1,5", formatter.format( 5400 ) );
		assertEquals( "2", formatter.format( 7200 ) );
	}
}

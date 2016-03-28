package is.rebbi.wo.formatters;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.text.ParseException;

import org.junit.Test;

public class TestPercentageFormatter {

	@Test
	public void testFormat() {
		PercentageFormatter p = new PercentageFormatter();
		assertEquals( "25", p.format( 0.25 ) );
		assertEquals( "25,5", p.format( 0.255 ) );
		assertEquals( "0", p.format( 0 ) );
		assertEquals( "100", p.format( 1 ) );
		assertEquals( "10.000", p.format( 100 ) );
		assertEquals( "100.000", p.format( 1000 ) );
	}

	@Test
	public void testParse() throws ParseException {
		PercentageFormatter p = new PercentageFormatter();
		assertEquals( new BigDecimal( 0 ), p.parseObject( "0" ) );
		assertEquals( new BigDecimal( "0.255" ), p.parseObject( "25,5" ) );
		assertEquals( new BigDecimal( "1" ), p.parseObject( "100" ) );
		assertEquals( new BigDecimal( "10" ), p.parseObject( "1000" ) );
	}
}
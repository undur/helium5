package is.rebbi.wo.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class TestUSUtilities {

	@Test
	public void integerFromObject() {
		assertEquals( new Integer( 1 ), USUtilities.integerFromObject( new Integer( 1 ) ) );
		assertEquals( new Integer( 2 ), USUtilities.integerFromObject( "2" ) );
		assertEquals( new Integer( 3 ), USUtilities.integerFromObject( new Float( 3.3 ) ) );
		assertEquals( new Integer( 4 ), USUtilities.integerFromObject( new Double( 4.444 ) ) );
		assertEquals( new Integer( 5555 ), USUtilities.integerFromObject( new Long( 5555 ) ) );
		assertEquals( new Integer( 6 ), USUtilities.integerFromObject( new Double( 6.999999 ) ) );
	}
}
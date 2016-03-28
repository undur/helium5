package is.rebbi.wo.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

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

	@Test
	public void booleanFromObject() {
		assertFalse( USUtilities.booleanFromObject( null ) );
		assertFalse( USUtilities.booleanFromObject( 0 ) );
		assertFalse( USUtilities.booleanFromObject( 0l ) );
		assertFalse( USUtilities.booleanFromObject( 0d ) );
		assertTrue( USUtilities.booleanFromObject( 1 ) );
		assertTrue( USUtilities.booleanFromObject( -1 ) );
		assertTrue( USUtilities.booleanFromObject( 1l ) );
		assertTrue( USUtilities.booleanFromObject( 1d ) );
		assertTrue( USUtilities.booleanFromObject( "true" ) );
		assertFalse( USUtilities.booleanFromObject( "false" ) );
		assertFalse( USUtilities.booleanFromObject( "fsdfsdf" ) );
	}

	@Test
	public void numberIsTrue() {
		assertFalse( USUtilities.numberIsTrue( null ) );
		assertFalse( USUtilities.numberIsTrue( 0 ) );
		assertFalse( USUtilities.numberIsTrue( 2 ) );
		assertTrue( USUtilities.numberIsTrue( 1 ) );
	}

	@Test
	public void stringFromObject() {
		assertEquals( "1", USUtilities.stringFromObject( new Integer( 1 ) ) );
	}
}
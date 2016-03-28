package is.rebbi.wo.urls;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class TestUSURLPath {

	@Test
	public void removesSlashAtStartAndEnd() {
		USURLPath u = USURLPath.create( "/url/" );
		assertEquals( u.toString(), "url" );
	}

	@Test
	public void length() {
		USURLPath u = USURLPath.create( "/url/gunnar/" );
		assertEquals( u.length(), 2 );
	}

	@Test
	public void integerValue() {
		USURLPath u = USURLPath.create( "/url/2/haha" );
		assertEquals( u.getInteger( 1 ), Integer.valueOf( 2 ) );
	}

	@Test
	public void stringValue() {
		USURLPath u = USURLPath.create( "/url/2/haha" );
		assertEquals( u.getString( 0 ), "url" );
		assertEquals( u.getString( 1 ), "2" );
		assertEquals( u.getString( 2 ), "haha" );
	}

	@Test
	public void stringValueExceedingLengthIsNull() {
		USURLPath u = USURLPath.create( "/url/2/haha" );
		assertNull( u.getString( 4 ) );
	}

	@Test
	public void integerValueExceedingLengthIsNull() {
		USURLPath u = USURLPath.create( "/url/2/haha" );
		assertNull( u.getInteger( 4 ) );
	}

	@Test
	public void nullURLIsEmpty() {
		USURLPath u = USURLPath.create( null );
		assertEquals( u.length(), 1 );
	}

	@Test
	public void howTheHellDoWeHandleDoubleSlashes() {}
}
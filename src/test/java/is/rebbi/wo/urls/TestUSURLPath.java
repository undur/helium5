package is.rebbi.wo.urls;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

public class TestUSURLPath {

	@Test
	public void removesSlashAtStartAndEnd() {
		URLWrapper u = URLWrapper.create( "/url/" );
		assertEquals( u.toString(), "url" );
	}

	@Test
	public void length() {
		URLWrapper u = URLWrapper.create( "/url/gunnar/" );
		assertEquals( u.length(), 2 );
	}

	@Test
	public void integerValue() {
		URLWrapper u = URLWrapper.create( "/url/2/haha" );
		assertEquals( u.getInteger( 1 ), Integer.valueOf( 2 ) );
	}

	@Test
	public void stringValue() {
		URLWrapper u = URLWrapper.create( "/url/2/haha" );
		assertEquals( u.getString( 0 ), "url" );
		assertEquals( u.getString( 1 ), "2" );
		assertEquals( u.getString( 2 ), "haha" );
	}

	@Test
	public void stringValueExceedingLengthIsNull() {
		URLWrapper u = URLWrapper.create( "/url/2/haha" );
		assertNull( u.getString( 4 ) );
	}

	@Test
	public void integerValueExceedingLengthIsNull() {
		URLWrapper u = URLWrapper.create( "/url/2/haha" );
		assertNull( u.getInteger( 4 ) );
	}

	@Test
	public void nullURLIsEmpty() {
		URLWrapper u = URLWrapper.create( null );
		assertEquals( u.length(), 1 );
	}

	@Test
	public void howTheHellDoWeHandleDoubleSlashes() {}
}
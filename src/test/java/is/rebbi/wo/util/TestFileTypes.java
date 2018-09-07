package is.rebbi.wo.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class TestFileTypes {

	@Test
	public void extensionForMimeType() {
		assertEquals( null, FileTypes.extensionForMimeType( null ) );
		assertEquals( null, FileTypes.extensionForMimeType( "nonExistantMimeType" ) );
		assertEquals( "png", FileTypes.extensionForMimeType( "image/png" ) );
		assertEquals( "png", FileTypes.extensionForMimeType( "IMAGE/PNG" ) );
	}

	@Test
	public void mimeTypeForExtension() {
		assertEquals( null, FileTypes.mimeTypeForExtension( null ) );
		assertEquals( null, FileTypes.mimeTypeForExtension( "nonExistantExtension" ) );
		assertEquals( "image/png", FileTypes.mimeTypeForExtension( "png" ) );
		assertEquals( "image/png", FileTypes.mimeTypeForExtension( "PNG" ) );
		assertEquals( "image/png", FileTypes.mimeTypeForExtension( "PnG" ) );
	}

	@Test
	public void extensionFromFileName() {
		assertEquals( null, FileTypes.extensionFromFilename( null ) );
		assertEquals( null, FileTypes.extensionFromFilename( "noextension" ) );
		assertEquals( null, FileTypes.extensionFromFilename( "endsWithDot." ) );
		assertEquals( null, FileTypes.extensionFromFilename( "endsWithTwoDots." ) );
		assertEquals( "jpeg", FileTypes.extensionFromFilename( "MAÐUR.JPEG" ) );
		assertEquals( "png", FileTypes.extensionFromFilename( "maður.jpg.PNG" ) );
		assertEquals( "png", FileTypes.extensionFromFilename( "maður.PnG" ) );
		assertEquals( null, FileTypes.extensionFromFilename( ".png" ) );
	}

	@Test
	public void filenameByRemovingExtension() {
		assertEquals( "normal", FileTypes.filenameByRemovingExtension( "normal.jpg" ) );
		assertEquals( "noExtension", FileTypes.filenameByRemovingExtension( "noExtension.jpg" ) );
		assertEquals( "endsWithTwoDots..", FileTypes.filenameByRemovingExtension( "endsWithTwoDots.." ) );
		assertEquals( "endsWithDot.", FileTypes.filenameByRemovingExtension( "endsWithDot." ) );
		assertEquals( ".startsWithDotAndHasNoExtension", FileTypes.filenameByRemovingExtension( ".startsWithDotAndHasNoExtension" ) );
		assertEquals( ".startsWithDotAndHasExtension", FileTypes.filenameByRemovingExtension( ".startsWithDotAndHasExtension.ext" ) );
		assertEquals( "hugi", FileTypes.filenameByRemovingExtension( "hugi" ) );
		assertEquals( null, FileTypes.filenameByRemovingExtension( null ) );
		assertEquals( "", FileTypes.filenameByRemovingExtension( "" ) );
	}
}
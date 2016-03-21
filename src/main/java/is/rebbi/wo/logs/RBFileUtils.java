package is.rebbi.wo.logs;

import java.io.File;

public class RBFileUtils {

	public static long sizeOf( File file ) {

		if( !file.exists() ) {
			return 0;
		}

		if( file.isDirectory() ) {
			return sizeOfDirectory( file );
		}
		else {
			return file.length();
		}

	}

	public static long sizeOfDirectory( File directory ) {
		if( !directory.exists() ) {
			return 0;
		}

		if( !directory.isDirectory() ) {
			String message = directory + " is not a directory";
			throw new IllegalArgumentException( message );
		}

		long size = 0;

		File[] files = directory.listFiles();
		if( files == null ) { // null if security restricted
			return 0L;
		}
		for( File file : files ) {
			size += sizeOf( file );
		}

		return size;
	}
}
package is.rebbi.wo.util;

import er.extensions.crypting.ERXCrypto;

/**
 * MD5. Introduced this class for indirection to reduce dependency on ERXCrypto.
 */

public class USMD5 {

	public static String md5( String input ) {
		return ERXCrypto.md5Encode( input );
	}
}
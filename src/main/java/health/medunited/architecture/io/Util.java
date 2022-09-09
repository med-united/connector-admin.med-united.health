package health.medunited.architecture.io;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;

public class Util {

	public static byte[] getBytesFromInputStream(InputStream is) throws IOException {
	    ByteArrayOutputStream os = new ByteArrayOutputStream(); 
	    byte[] buffer = new byte[0xFFFF];
	    for (int len = is.read(buffer); len != -1; len = is.read(buffer)) { 
	        os.write(buffer, 0, len);
	    }
	    return os.toByteArray();
	}

	public static String getETagDigest(byte[] paramArrayOfByte) {
		MessageDigest localMessageDigest;
		try {
			localMessageDigest = MessageDigest.getInstance("MD5");
		} catch (Exception localException) {
			throw new RuntimeException("MD5 cryptographic algorithm is not available.", localException);
		}
		byte[] arrayOfByte = localMessageDigest.digest(paramArrayOfByte);
		BigInteger localBigInteger = new BigInteger(1, arrayOfByte);
		StringBuilder localStringBuilder = new StringBuilder(48);
		localStringBuilder.append(localBigInteger.toString(16));
		return localStringBuilder.toString();
	}

}

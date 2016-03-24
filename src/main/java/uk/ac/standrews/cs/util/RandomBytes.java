package uk.ac.standrews.cs.util;

public class RandomBytes {

	public static byte[] generateRandomBytes(int number_of_bytes) {
		
		int BYTES_IN_HASH = 20; // number of bytes in SHA1 digest
		
		byte[] bytes = new byte[number_of_bytes];
		
		int i = 0;
		for (; i < number_of_bytes / BYTES_IN_HASH; i++) {
			
			setRandomBytes(bytes, i, BYTES_IN_HASH);
	
		}
		
		// Fill in remaining bytes if necessary.
		int bytes_left = number_of_bytes % BYTES_IN_HASH;
		if (bytes_left > 0) {
			setRandomBytes(bytes, i, bytes_left);
		}
	
		return bytes;
	}

	private static void setRandomBytes(byte[] bytes, int i, int bytes_to_copy) {
		String seed = String.valueOf(System.currentTimeMillis()) + String.valueOf(Runtime.getRuntime().freeMemory());
		byte[] hashed = SHA1KeyFactory.hash(seed.getBytes());
		System.arraycopy(hashed, 0, bytes, i * hashed.length, bytes_to_copy);
	}
}

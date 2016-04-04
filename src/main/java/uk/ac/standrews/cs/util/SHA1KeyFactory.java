/*
 * Created on 26-Oct-2004
 */
package uk.ac.standrews.cs.util;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import uk.ac.standrews.cs.interfaces.IKey;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.InetSocketAddress;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Provides various ways to generate keys.
 * 
 * @author stuart, graham
 */
public class SHA1KeyFactory {
	
	private static final int HEX_BASE = 16;
	
    /**
     * Prints out the digest in a form that can be easily compared to the test vectors. 
     */
    /*
    public static String toHex(byte[] bytes ) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < bytes.length ; i++) { 
            char c1, c2;
            c1 = (char)((bytes[i] >>> 4) & 0xf);
            c2 = (char)(bytes[i] & 0xf);
            c1 = (char)((c1 > 9) ? 'a' + (c1 - 10) : '0' + c1);
            c2 = (char)((c2 > 9) ? 'a' + (c2 - 10) : '0' + c2);
            sb.append(c1);
            sb.append(c2);
        }
        return sb.toString();
    }
    */
    
    public static byte[] hash( byte[] bytes ) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA");
			digest.update(bytes, 0, bytes.length);
			return digest.digest();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

		return null;
		/*
			sun.security.provider.SHA sha = new sun.security.provider.SHA();
			sha.engineUpdate
			return sha.engineDigest();
        */
    }

    public static byte[] hash(InputStream source) {
        try {
            return DigestUtils.sha1(source);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
		/*
			sun.security.provider.SHA sha = new sun.security.provider.SHA();
			sha.engineUpdate
			return sha.engineDigest();
        */
    }
    /**
	 * Creates a key with an arbitrary value. Subsequent calls return keys with the same value.
	 * 
	 * @return a key with an arbitrary value
	 */
	public static IKey generateKey() {
		return generateKey("null");
	}

	/**
	 * Creates a key with a value generated from the given string.
	 * 
	 * @param s the string from which to generate the key's value
	 * @return a key with a value generated from s
	 */
	public static IKey generateKey(String s) {	
        return generateKey(s.getBytes());
	}
	
	/**
	 * Creates a new key using the String representation of a BigInteger
	 * This method has been added for use in for de-serialisation - al
	 * 
	 * @param s - the String representation of a serialised Key
	 * @return a new Key using the parameter s as a long value
	 */
	public static IKey recreateKey( String s ) {
	    return new KeyImpl(s);
	}
	
	/**
	 * Creates a key with a value generated from the given byte array.
	 * 
	 * @param bytes the array from which to generate the key's value
	 * @return a key with a value generated from bytes
	 */
	public static IKey generateKey(byte[] bytes) {
        byte[] hashed = hash(bytes);
		String hex = Hex.encodeHexString(hashed);

        BigInteger bi = new BigInteger(hex, HEX_BASE);  // Convert to decimal.
        return new KeyImpl(bi);
	}

    private IKey generateKey(InputStream source) {
        byte[] hashed = hash(source);
        String hex = Hex.encodeHexString(hashed);

        BigInteger bi = new BigInteger(hex, HEX_BASE);  // Convert to decimal.
        return new KeyImpl(bi);
    }
	
	/**
	 * Creates a key with a pseudo-random value.
	 * 
	 * @return a key with a pseudo-random value
	 */
	public static IKey generateRandomKey() {
		String seed = String.valueOf(System.currentTimeMillis()) +
                String.valueOf(Runtime.getRuntime().freeMemory());
		return generateKey(seed);
	}
	
	/**
	 * Creates a key with a value generated from the given IP address.
	 * 
	 * @param ip_address the IP address from which to generate the key's value
	 * @return a key with a value generated from ip_address
	 */
	public static IKey generateKey(InetSocketAddress ip_address) {
		
		if (ip_address == null) {
		    Error.error("InetSocketAddress was null");
		    return generateKey();
		}
		
		String s = ip_address.getAddress().getHostAddress() + ip_address.getPort();
		return generateKey(s);
	}
}

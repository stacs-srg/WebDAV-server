/*
 * Created on 07-Dec-2004
 */
package uk.ac.standrews.cs.util;

import java.util.Random;

/**
 * @author stuart
 */
public class RandomString {

    private static final int BYTES=20; //160-bytes

    private static final String hexChars = "0123456789abcdef";
    private static final byte INDEX_TYPE = 6;
    private static final byte INDEX_VARIATION = 8;
    private static final byte TYPE_RANDOM_BASED = 4;

    private Random rnd;


    /**
     * Constructor. Instantiates the rnd object to generate random numbers.
     */
    public RandomString()
    {
        rnd = new Random(System.currentTimeMillis());
    }


    /**
     * Generates a random UUID and returns the String representation of it.
     * @returns a String representing a randomly generated UUID.
     */
    public String generateString()
    {
        // Generate 160-bit random number
        byte[] uuid = new byte[BYTES];
        nextRandomBytes(uuid);

        // Set various bits such as type
        uuid[INDEX_TYPE] &= (byte) 0x0F;
        uuid[INDEX_TYPE] |= (byte) (TYPE_RANDOM_BASED << 4);
        uuid[INDEX_VARIATION] &= (byte) 0x3F;
        uuid[INDEX_VARIATION] |= (byte) 0x80;

        // Convert byte array into formatted string
        StringBuffer b = new StringBuffer(BYTES*2);
        for (int i=0; i<BYTES; i++){
            int hex = uuid[i] & 0xFF;
            b.append(hexChars.charAt(hex >> 4));
            b.append(hexChars.charAt(hex & 0x0F));
        }

        // Return UID
        return b.toString();
    }


    /**
     * Generates random bytes and places them into a user-supplied byte array.
     * The number of random bytes produced is equal to the length of the byte array.
     * Nicked from java.util.Random because the stupid SNAP board doesn't have this method!
     * @param bytes the non-null byte array in which to put the random bytes.
     */
    private void nextRandomBytes(byte[] bytes)
    {
        int numRequested = bytes.length;
        int numGot = 0, rand = 0;
        while (true)
        {
            for (int i=0; i<4; i++)
            {
                if (numGot == numRequested)
                        return;
                rand = (i==0 ? rnd.nextInt() : rand>>8);
                bytes[numGot++] = (byte)rand;
             }
        }
    }



    /**
     * Test1. Only here for testing purposes.
     */
    public static void main(String[] args)
    {
        RandomString uuidgen = new RandomString();
        System.out.println(uuidgen.generateString());
    }

}

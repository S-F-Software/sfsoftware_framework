package com.sevensoupcans.sfsoftware.util;

import java.util.ArrayList;
import java.util.List;

public abstract class BitFlagUtils 
{
	/**
	 * Decodes a single byte into two 4-bit values (0–15).
	 * The high nibble (bits 7–4) becomes the first value,
	 * and the low nibble (bits 3–0) becomes the second value.
	 *
	 * @param b The byte to decode.
	 * @return An int array of length 2: [highNibble, lowNibble].
	 */
	public static int[] decodeByteToNibbles(byte b)
	{
	    int unsigned = b & 0xFF;      // prevent sign extension
	    int high = (unsigned >> 4) & 0x0F;
	    int low  = unsigned & 0x0F;
	    return new int[] { high, low };
	}
	
	/**
	 * Encodes two 4-bit integer values (0–15) into a single byte.
	 * The first parameter becomes the high nibble (bits 7–4),
	 * and the second becomes the low nibble (bits 3–0).
	 *
	 * @param highNibble the high 4-bit value (0–15)
	 * @param lowNibble  the low 4-bit value (0–15)
	 * @return the resulting packed byte
	 * @throws IllegalArgumentException if either value is out of range
	 */
	public static byte encodeNibbles(int highNibble, int lowNibble)
	{
	    if (highNibble < 0 || highNibble > 15 || lowNibble < 0 || lowNibble > 15)
	        throw new IllegalArgumentException("Nibble values must be in range 0–15");

	    int combined = ((highNibble & 0x0F) << 4) | (lowNibble & 0x0F);
	    return (byte) combined;
	}	
	
    public static int toInt(List<Boolean> flags) 
    {
        int result = 0;
        for (int i = 0; i < flags.size(); i++) 
        {
            if (flags.get(i)) 
            {
                result |= (1 << i);
            }
        }
        return result;
    }

    public static List<Boolean> fromInt(int bitFlag, int size) 
    {
        List<Boolean> result = new ArrayList<>();
        for (int i = 0; i < size; i++) 
        {
            result.add((bitFlag & (1 << i)) != 0);
        }
        return result;
    }

    public static boolean isFlagSet(int bitFlag, int index) 
    {
        return (bitFlag & (1 << index)) != 0;
    }

    public static int setFlag(int bitFlag, int index, boolean value) 
    {
        if (value) 
        {
            return bitFlag | (1 << index);
        } 
        else 
        {
            return bitFlag & ~(1 << index);
        }
    }
}
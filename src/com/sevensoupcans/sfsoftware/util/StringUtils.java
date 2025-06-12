package com.sevensoupcans.sfsoftware.util;

import java.util.Optional;

public abstract class StringUtils 
{
	public static final String COPYRIGHT_SYMBOL  = "\u00a9";
	public static final String REGISTERED_SYMBOL  = "\u00ae";
	public static final String TRADEMARK_SYMBOL  = "\u2122";

	private static final String DEFAULT_DELIMITER = ",";
	
	/**
	 * Converts a provided byte array to a comma delimited String
	 * 
	 * @param byteArray
	 * @return
	 */	
	public static String buildDelimitedStringFromByteArray(byte[] byteArray)
	{
		return buildDelimitedStringFromByteArray(byteArray, DEFAULT_DELIMITER);
	}
	
	/**
	 * Converts a provided byte array to a delimited String
	 * 
	 * @param byteArray
	 * @param delimiter
	 * @return
	 */
	public static String buildDelimitedStringFromByteArray(byte[] byteArray, String delimiter)
	{
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < byteArray.length; i++) 
		{
		    sb.append(byteArray[i]);
		    if (i < byteArray.length - 1) {
		        sb.append(delimiter);
		    }
		}
		return sb.toString();
	}
	
	/**
	 * Creates a String from the contents of a provided byte array.
	 * 
	 * @param byteArray
	 * @return
	 */
	public static String buildStringFromByteArray(byte[] byteArray) 
	{
	    return new String(byteArray, java.nio.charset.StandardCharsets.US_ASCII).trim();
	}	
	
	/**
	 * Changes the first character of each word to uppercase.
	 * 
	 * @param source
	 * @return
	 */
	public static String capitalizeString(final String source)
	{
		StringBuffer res = new StringBuffer();

	    String[] strArr = source.split(" ");
	    for (String str : strArr) 
	    {
	        char[] stringArray = str.trim().toCharArray();
	        stringArray[0] = Character.toUpperCase(stringArray[0]);
	        str = new String(stringArray);
	        res.append(str).append(" ");
	    }

	    return res.toString().trim();
	}
	
	/**
	 * Converts a provided comma delimited String to a byte array
	 * 
	 * @param delimitedString
	 * @return
	 */	
	public static byte[] getByteArrayFromDelimitedString(String delimitedString)
	{
		return getByteArrayFromDelimitedString(delimitedString, DEFAULT_DELIMITER);
	}
	
	/**
	 * Converts a provided delimited String to a byte array
	 * 
	 * @param delimitedString
	 * @param delimiter
	 * @return
	 */	
	public static byte[] getByteArrayFromDelimitedString(String delimitedString, String delimiter)
	{
		String[] stringArray = delimitedString.split(delimiter);
		byte[] byteArray = new byte[stringArray.length];
        
		for (int i = 0; i < stringArray.length; i++)
			byteArray[i] = Byte.parseByte(stringArray[i].trim());
		
        return byteArray;
	}
	
	/**
	 * Creates an array of bytes representing the provided String
	 * 
	 * @param name
	 * @return
	 */
	public static byte[] getByteArrayFromString(String string)
	{
		return getByteArrayFromString(string, string.length());
	}

	/**
	 * Creates an array of bytes representing the provided String and pads with spaces if needed
	 * 
	 * @param string
	 * @param maxLength
	 * @return
	 */
	public static byte[] getByteArrayFromString(String string, int maxLength) 
	{
		return getByteArrayFromString(string, maxLength, (byte) 32);
	}	
	
	/**
	 * Creates an array of bytes representing the provided String and padding with spaces if needed
	 * 
	 * @param string
	 * @param maxLength
	 * @return
	 */
	public static byte[] getByteArrayFromString(String string, int maxLength, byte paddingCharacter) 
	{
	    byte[] byteArray = new byte[maxLength];
	    byte[] encoded = string.getBytes(java.nio.charset.StandardCharsets.US_ASCII);
	    
	    int length = Math.min(encoded.length, maxLength);
	    System.arraycopy(encoded, 0, byteArray, 0, length);

	    // Remaining bytes are already 0 by default in Java, but we will pad with the provided character
	    for (int i = length; i < maxLength; i++)
	        byteArray[i] = paddingCharacter;

	    return byteArray;
	}
	
	
	public static int parseInt(final String str)
	{
		return Optional.ofNullable(str)
				.map(String::trim)
				.filter(val -> !val.isEmpty())
				.map(Integer::parseInt)
				.orElse(0);
	}

}

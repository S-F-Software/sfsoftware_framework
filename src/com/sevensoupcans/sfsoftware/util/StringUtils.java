package com.sevensoupcans.sfsoftware.util;

public abstract class StringUtils 
{
	public static final String COPYRIGHT_SYMBOL  = "\u00a9";
	public static final String REGISTERED_SYMBOL  = "\u00ae";
	public static final String TRADEMARK_SYMBOL  = "\u2122";
	
	/**
	 * Changes the first character of each word to uppercase.
	 * 
	 * @param source
	 * @return
	 */
	public static String capitalizeString(String source)
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

}

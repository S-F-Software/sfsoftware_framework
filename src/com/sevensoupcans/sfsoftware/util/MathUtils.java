package com.sevensoupcans.sfsoftware.util;

import java.util.Random;

public abstract class MathUtils
{
	private static Random random = new Random();
	
	public static int ensureRange(int value, int min, int max) 
	{
		return java.lang.Math.min(java.lang.Math.max(value, min), max);
	}
	
	/**
	* Get the closest greater power of 2 to the fold number
	* 
	* @param fold The target number
	* @return The power of 2
	*/
	public static int get2Fold(int fold) 
	{
		int ret = 2;
		while (ret < fold)
			ret *= 2;
		
		return ret;
	}
	
	/**
	 * Gets the angle between two points in radians. 
	 *  
	 * @param originX
	 * @param originY
	 * @param targetX
	 * @param targetY
	 * @return
	 */
	@Deprecated
	public static double getAngle(int originX, int originY, int targetX, int targetY)
	{		
		return getAngle((float) originX, (float) originY, (float) targetX, (float) targetY);
	}

	/**
	 * Gets the angle between two points in radians. 
	 *  
	 * @param originX
	 * @param originY
	 * @param targetX
	 * @param targetY
	 * @return
	 */
	public static double getAngle(float originX, float originY, float targetX, float targetY) 
	{
		double dx = targetX - originX;
		double dy = targetY - originY;
		
		double inRads = Math.atan2(dy, dx);
	
		if (inRads < 0) inRads += 2 * Math.PI;
		
		return inRads;
	}	
	
	/**
	 * Returns a boolean array of "bits" created from the provided byte.
	 * 
	 * @param b
	 * @return
	 */
	public static boolean[] getBooleanArrayFromByte(byte b)
	{
		boolean arr[] = new boolean[8];
	    for (int i = 0; i < 8; i++) 
	    	arr[i] = (b & (byte) (128 / Math.pow(2, i))) != 0;
	    
	    return arr;
	}	
	
	public static int getNextEven(int value)
	{
		return value % 2 == 0 ? value : value + 1;
	}
	
	public static int getNextOdd(int value)
	{
		return value % 2 == 1 ? value : value + 1;
	}
	
	public static boolean inRange(int value, int min, int max) 
	{
		return (value>= min) && (value<= max);
	}		
	
	public static float randomFloat()
	{
		return random.nextFloat();
	}
	
	public static float randomFloat(long seed)
	{
		Random r = new Random(seed);
		return r.nextFloat();
	}	
	
	public static int randomInt(int bound) 
	{	 		
		return random.nextInt(bound);
	}
	
	public static int randomInt(long seed, int bound)
	{
		Random r = new Random(seed);
		return r.nextInt(bound);
	}
	
	public static double random()
	{
		return Math.random();
	}
	
	public static void setRandomizerSeed(long seed)
	{
		random = new Random(seed);
	}
}

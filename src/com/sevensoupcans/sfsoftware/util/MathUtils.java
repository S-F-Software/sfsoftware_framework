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
	 * Gets the angle between two points in radians. 
	 *  
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * @return
	 */
	public static double getAngle(int x1, int y1, int x2, int y2)
	{
		double dx = x1 - x2;
		double dy = y1 - y2;
		
		double inRads = Math.atan2(dy,dx);
		
		if (inRads < 0)
		{
	        inRads = Math.abs(inRads);
		}
	    else
	    {
	        inRads = 2 * Math.PI - inRads;
	    }			
		
		return inRads;
	}		
	
	public static boolean inRange(int value, int min, int max) 
	{
		return (value>= min) && (value<= max);
	}		
	
	public static float randomFloat()
	{
		return random.nextFloat();
	}
	
	public static int randomInt(int bound) 
	{	 		
		return random.nextInt(bound);
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

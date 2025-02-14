package com.sevensoupcans.sfsoftware.util;

/**
 * A counter class similar to the implementation of a game state counter in
 * some classic console titles. 
 * 
 * The counter can be incremented or decremented and has a range of 0-255 
 * wrapping around when the value goes outside of the bounds.
 * 
 * @author S. Thompson <fuzmeister@sevensoupcans.com>
 *
 */
public final class Unsigned8BitCounter 
{

	private int counter = 0;
	
	public void decrement()
	{
		counter = (counter - 1) & 0xFF; // Wraps to 255 below 0
	}
	
	public int getValue()
	{
		return counter;
	}
	
	public void increment()
	{
		counter = (counter + 1) & 0xFF; // Wraps to 0 above 255
	}

}

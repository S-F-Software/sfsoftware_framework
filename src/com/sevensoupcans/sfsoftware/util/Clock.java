package com.sevensoupcans.sfsoftware.util;

import java.util.ArrayList;

import com.sevensoupcans.sfsoftware.game.Game;

public final class Clock 
{
	private final static char SEPARATOR = ':';
	private final static ArrayList<Clock> clocks = new ArrayList<Clock>();	
	private static long lastTick;
	
	private int clockInterval;
	private long clockLastTick;
	private boolean paused = false;
	
	static
	{
		lastTick = getTime();
	}
	
	public Clock(int interval) 
	{
		setInterval(interval);
		clockLastTick = getTime();
		clocks.add(this);
	}
	
	public static String getFormattedHoursMinutes(long time)
	{		
		String minutes = Integer.toString((int)((time % 3600) / 60));
		String hours = Integer.toString((int)(time / 3600));
		for (int i = 0; i < 2; i++) {
			if (minutes.length() < 2) {
				minutes = "0" + minutes;
			}
			if (hours.length() < 2) {
				hours = "0" + hours;
			}
		}		
		
		return hours + SEPARATOR + minutes;			
	}
	
	public static String getFormattedTime(long time)
	{				
		String seconds = Integer.toString((int)(time % 60));
		String minutes = Integer.toString((int)((time % 3600) / 60));
		String hours = Integer.toString((int)(time / 3600));
		for (int i = 0; i < 2; i++) {
			if (seconds.length() < 2) {
				seconds = "0" + seconds;
			}
			if (minutes.length() < 2) {
				minutes = "0" + minutes;
			}
			if (hours.length() < 2) {
				hours = "0" + hours;
			}
		}		
		
		return hours + SEPARATOR + minutes + SEPARATOR + seconds;				
	}
	
	/**
	 * Get the time in milliseconds
	 * 
	 * @return The system time in milliseconds
	 */
	public static long getTime() 
	{
	    return (org.lwjgl.Sys.getTime() * 1000) / org.lwjgl.Sys.getTimerResolution();
	}	
	
	public static double getSinOfTime()
	{
		return Math.abs(Math.sin(getTime()));
	}
	
	public static void togglePausedAll()
	{
		for(Clock c: clocks)
		{
			if(!(c.togglePaused()))
			{
				// If clock was just un-paused, update the last tick.
				c.clockLastTick = getTime();
			}
		}
	}

	public static void updateAll(Game g)
	{
		for(Clock c: clocks)
		{
			c.updateClock(g);
		}
	}	
	
	public boolean togglePaused()
	{
		paused = !(paused);
		return paused;
	}
	
	public boolean updateClock()
	{
		return updateClock(null);
	}
	
	public boolean updateClock(Game g) 
	{
		boolean triggerEvent = false;		
	    if (getTime() - clockLastTick > getInterval()) 
	    {	    		    		    		    	
	    	if(g == null)
	    	{
	    		triggerEvent = true;
	    	}
	    	else if (!(g.isPaused))
	    	{
	    		triggerEvent = true;
	    	}	
	    	clockLastTick += getInterval();
	    }
	    
	    return triggerEvent;
	}	
	
	public static void update(Game g) {
	    if (getTime() - lastTick > 1000) {
	    	if(!(g.isPaused))
	    	{
	    		g.incrementGameCounter();
	    	}
	    	lastTick += 1000; //add one millisecond
	    }
	}

	/**
	 * @return the clockInterval
	 */
	public int getInterval() 
	{
		return clockInterval;
	}

	/**
	 * @param clockInterval the clockInterval to set
	 */
	public void setInterval(int i) 
	{
		clockInterval = i;
	}	
	
	
}

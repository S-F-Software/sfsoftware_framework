package com.sevensoupcans.sfsoftware.util;

import com.sevensoupcans.sfsoftware.game.Game;

public final class Clock 
{
	private final static char SEPARATOR = ':';
	private static long lastTick;
	
	static
	{
		lastTick = getTime();
	}
	public static String getFormattedHoursMinutes(final long time)
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
	public static String getFormattedTime(final long time)
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
	
	public static double getSinOfTime()
	{
		return Math.abs(Math.sin(getTime()));
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
	
	public static void update(final Game g) {
	    if (getTime() - lastTick > 1000) {
	    	if(!(g.isPaused))
	    	{
	    		g.incrementGameCounter();
	    	}
	    	lastTick += 1000; //add one millisecond
	    }
	}
	
	private int clockInterval;
	
	private long clockLastTick;	
	
	private boolean paused = false;
	
	/**
	 * Creates a new Clock objects
	 * 
	 * @param intervalInMilliseconds
	 */
	public Clock(int intervalInMilliseconds) 
	{
		setInterval(intervalInMilliseconds);
		reset();
	}
	
	/**
	 * @return the clockInterval
	 */
	public int getInterval() 
	{
		return clockInterval;
	}
	
	public void reset()
	{
	    clockLastTick = getTime();
	}
	
	/**
	 * @param clockInterval the clockInterval to set
	 */
	public void setInterval(final int i) 
	{
		clockInterval = i;
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

	public boolean updateClock(final Game g) 
	{	
		long now = getTime();
		
		if(paused)
		{
			clockLastTick = now;
			return false;
		}
		
	    if (now - clockLastTick >= getInterval()) 
	    {	    		    		    		    	
	    	if(g == null || (!(g.isPaused)))
	    	{
	    		clockLastTick = now;
	    		return true;
	    	}
	    }
	    
	    return false;
	}	
	
	
}

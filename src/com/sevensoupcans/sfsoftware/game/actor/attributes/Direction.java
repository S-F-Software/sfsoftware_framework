package com.sevensoupcans.sfsoftware.game.actor.attributes;

import java.util.Arrays;
import java.util.List;

public enum Direction 
{
	UP("Up", 0, -1), DOWN("Down", 0, 1), LEFT("Left", -1, 0), RIGHT("Right", 1, 0);
	
	private final String directionDescription;
	private final int xDifference;
	private final int yDifference;
	
	public static List<Direction> getAll()
	{
		return Arrays.asList(Direction.values());
	}
	
	Direction(String directionDescription, int xDifference, int yDifference)
	{
		this.directionDescription = directionDescription;
		this.xDifference = xDifference;
		this.yDifference = yDifference;
	}
	
	public String getDescription()
	{
		return this.directionDescription;
	}
	
	public String getName()
	{
		return this.name();
	}
	
	public int getXDifference()
	{
		return this.xDifference;
	}
	
	public int getYDifference()
	{
		return this.yDifference;
	}
}

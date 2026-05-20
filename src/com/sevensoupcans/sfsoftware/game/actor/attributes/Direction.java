package com.sevensoupcans.sfsoftware.game.actor.attributes;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public enum Direction 
{
	UP("Up", 0, -1, 1), 
	DOWN("Down", 0, 1, 0), 
	LEFT("Left", -1, 0, 2), 
	RIGHT("Right", 1, 0, 3),
	LEFT_UP("LeftUp", -1, -1, 99),
	RIGHT_UP("RightUp", 1, -1, 99),
	LEFT_DOWN("LeftDown", 1, 1, 99),
	RIGHT_DOWN("RightDown", 1, 1, 99);
	
	private static final Direction[] VALUES = values();
	
	public static Direction fromVector(int x, int y)
	{
	    for (Direction direction : VALUES)
	    {
	        if (direction.getXDifference() == x
	                && direction.getYDifference() == y)
	        {
	            return direction;
	        }
	    }

	    return null;
	}
	
	public static List<Direction> getAll()
	{
		return Arrays.asList(Direction.values());
	}
	
    public static List<Direction> getCardinals() 
    {
        return Arrays.asList(UP, DOWN, LEFT, RIGHT);
    }	
	
    public static Direction getOpposite(Direction direction)
    {
        int oppositeX = -direction.xDifference;
        int oppositeY = -direction.yDifference;

        for (Direction d : values()) 
        {
            if (d.xDifference == oppositeX && d.yDifference == oppositeY) 
            {
                return d;
            }
        }

        throw new IllegalArgumentException("No opposite found for: " + direction);
    }	
	
	/**
	 * Returns a random four-way direction - this should be expanded to support eight eventually.
	 * 
	 * @return A random direction
	 */
	public static Direction random()
	{
		return getCardinals().toArray(new Direction[0])[ThreadLocalRandom.current().nextInt(4)];
	}
	
    public static Direction randomLeftRight() 
    {
        return ThreadLocalRandom.current().nextBoolean() ? LEFT : RIGHT;
    }

    public static Direction randomUpDown() 
    {
        return ThreadLocalRandom.current().nextBoolean() ? UP : DOWN;
    }	
	
	private final String directionDescription;
	private final double angleOfDirection;
	private final int legacyDirectionId;
	private final int xDifference;
	private final int yDifference;
	
	Direction(String directionDescription, int xDifference, int yDifference, int legacyDirectionId)
	{
		this.directionDescription = directionDescription;
		this.legacyDirectionId = legacyDirectionId;
		this.xDifference = xDifference;
		this.yDifference = yDifference;
		
		double angleOfDirection = Math.atan2(yDifference, xDifference);
		if (angleOfDirection < 0) angleOfDirection += 2 * Math.PI; // Normalize to [0, 2π) if needed
		
		this.angleOfDirection = angleOfDirection;
	}
	
	public double getAngle()
	{
		return this.angleOfDirection;
	}
	
	public String getDescription()
	{
		return this.directionDescription;
	}
	
	@Deprecated
	public int getLegacyDirectionId()
	{
		return this.legacyDirectionId;
	}
	
	public String getName()
	{
		return this.name();
	}
	
    public Direction getOpposite()
    {
    	return Direction.getOpposite(this);
    }    
	
	public int getXDifference()
	{
		return this.xDifference;
	}
	
	public int getYDifference()
	{
		return this.yDifference;
	}
	
    public Direction randomPerpendicular() 
    {
        switch(this) 
        {
            case UP:
            case DOWN:
                return randomLeftRight();
            case LEFT:
            case RIGHT:
                return randomUpDown();
            default:
                throw new IllegalStateException("Unknown direction: " + this);
        }
    }	
	
	public Direction rotateClockwise() 
	{
	    switch(this) 
	    {
	        case UP: 
	        	return RIGHT;
	        case RIGHT: 
	        	return DOWN;
	        case DOWN: 
	        	return LEFT;
	        case LEFT: 
	        	return UP;
	        default:
	    	    throw new IllegalStateException("Unknown direction: " + this);
	    }
	}

	public Direction rotateCounterClockwise() 
	{
	    switch(this) 
	    {
	        case UP:
	        	return LEFT;
	        case LEFT: 
	        	return DOWN;
	        case DOWN: 
	        	return RIGHT;
	        case RIGHT: 
	        	return UP;
	        default:
	        	throw new IllegalStateException("Unknown direction: " + this);
	    }
	}	
}

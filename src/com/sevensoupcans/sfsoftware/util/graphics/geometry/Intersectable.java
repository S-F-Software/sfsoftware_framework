package com.sevensoupcans.sfsoftware.util.graphics.geometry;

public interface Intersectable 
{
	default boolean intersectsWith(final float xPos, final float yPos)
	{
		return (xPos >= this.getX() && 
				xPos <= (this.getX() + this.getWidth()) && 
				yPos >= this.getY() && 
				yPos <= (this.getY() + this.getHeight()));
	}
	
	public float getHeight();
	public float getX();
	public float getY();
	public float getWidth();	
}

package com.sevensoupcans.sfsoftware.util.graphics.geometry;

public interface Collidable 
{	
	public boolean collisionResult(final Collidable object);
	
	default public boolean collidingWith(final Collidable collidable)
	{
		return (this.getX() < collidable.getX() + collidable.getWidth() &&
		        this.getX() + this.getWidth() > collidable.getX() &&
		        this.getY() < collidable.getY() + collidable.getHeight() &&
		        this.getY() + this.getHeight() > collidable.getY());
	}
	
	public float getHeight();
	public float getX();
	public float getY();
	public float getWidth();
}

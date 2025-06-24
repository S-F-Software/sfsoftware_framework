package com.sevensoupcans.sfsoftware.util.graphics.geometry;

public interface Collidable 
{	
	public boolean collisionResult(final Collidable object);
	
	default public boolean collidingWith(final Collidable collidable)
	{
		Quad collisionBox = this.getCollisionBox();
		
		return (collisionBox.getX() < collidable.getX() + collidable.getWidth() &&
				collisionBox.getX() + collisionBox.getWidth() > collidable.getX() &&
				collisionBox.getY() < collidable.getY() + collidable.getHeight() &&
				collisionBox.getY() + collisionBox.getHeight() > collidable.getY());
	}
	
	default public Quad getCollisionBox()
	{
		return new Quad(this.getX(), this.getY(), this.getWidth(), this.getHeight());
	}
	
	public float getHeight();
	public float getX();
	public float getY();
	public float getWidth();
}

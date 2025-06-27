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
	
	default public float getCollisionBoxBottomOffset()
	{
		return getCollisionBoxTopOffset() + getCollisionBox().getHeight();
	}
	
	default public float getCollisionBoxLeftOffset()
	{
		return getCollisionBox().getX() - getX();
	}

	default public float getCollisionBoxRightOffset()
	{
		return getCollisionBoxLeftOffset() + getCollisionBox().getWidth();
	}
	
	default public float getCollisionBoxTopOffset()
	{
		return getCollisionBox().getY() - getY();
	}	
	
	public float getHeight();
	public float getX();
	public float getY();
	public float getWidth();
}

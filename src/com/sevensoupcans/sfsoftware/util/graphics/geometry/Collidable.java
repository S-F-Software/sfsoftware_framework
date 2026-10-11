package com.sevensoupcans.sfsoftware.util.graphics.geometry;

public interface Collidable 
{	
	public boolean collisionResult(final Collidable object);

	default public boolean collidingWith(final Collidable anotherCollidable)
	{
		Quad ourCollisionBox = this.getCollisionBox();
		Quad theirCollisionBox = anotherCollidable.getCollisionBox();

		return ourCollisionBox.getLeft() < theirCollisionBox.getRight()
				&& ourCollisionBox.getRight() > theirCollisionBox.getLeft()
				&& ourCollisionBox.getTop() < theirCollisionBox.getBottom()
				&& ourCollisionBox.getBottom() > theirCollisionBox.getTop();
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
	public void setCollisionBox(Quad collisionBox);
	public void setCollisionBox(float x, float y, float width, float height);
}

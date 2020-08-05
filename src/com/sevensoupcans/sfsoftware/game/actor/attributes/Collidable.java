package com.sevensoupcans.sfsoftware.game.actor.attributes;

public interface Collidable {
	public boolean collisionResult(Collidable object);
	public boolean collidingWith(Collidable object);
}

package com.sevensoupcans.sfsoftware.game.actor.attributes;

public interface Collidable {
	public boolean collisionResult(final Collidable object);
	public boolean collidingWith(final Collidable object);
}

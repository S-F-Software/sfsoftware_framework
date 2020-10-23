package com.sevensoupcans.sfsoftware.util.tile.pathfinding;

import com.sevensoupcans.sfsoftware.game.actor.Actor;

public interface PathFinder {
	public Path getPath(Actor actor, int startX, int startY, int destX, int destY);
}

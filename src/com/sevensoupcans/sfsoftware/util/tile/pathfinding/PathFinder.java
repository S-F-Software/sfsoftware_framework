package com.sevensoupcans.sfsoftware.util.tile.pathfinding;

import com.sevensoupcans.sfsoftware.game.actor.Actor;
import com.sevensoupcans.sfsoftware.util.tile.TileMap;

public interface PathFinder {
	public TileMap getAssociatedTileMap();
	public Path getPath(Actor actor, int startX, int startY, int destX, int destY);
}

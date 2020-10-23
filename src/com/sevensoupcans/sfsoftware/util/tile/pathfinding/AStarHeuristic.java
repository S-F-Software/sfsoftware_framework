package com.sevensoupcans.sfsoftware.util.tile.pathfinding;

import com.sevensoupcans.sfsoftware.game.actor.Actor;
import com.sevensoupcans.sfsoftware.util.tile.TileMap;

public interface AStarHeuristic {
	public float getCost(TileMap tileMap, Actor actor, int x, int y, int tx, int ty);
}

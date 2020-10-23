package com.sevensoupcans.sfsoftware.util.tile.pathfinding.heuristics;

import com.sevensoupcans.sfsoftware.game.actor.Actor;
import com.sevensoupcans.sfsoftware.util.tile.TileMap;
import com.sevensoupcans.sfsoftware.util.tile.pathfinding.AStarHeuristic;

public class ClosestHeuristic implements AStarHeuristic {

	@Override
	public float getCost(TileMap tileMap, Actor actor, int x, int y, int tx, int ty) 
	{
		float dx = tx - x;
		float dy = ty - y;
		
		return (float) (Math.sqrt((dx * dx) +  (dy * dy)));
	}

}

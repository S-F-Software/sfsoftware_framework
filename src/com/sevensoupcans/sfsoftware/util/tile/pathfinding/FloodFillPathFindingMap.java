package com.sevensoupcans.sfsoftware.util.tile.pathfinding;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.function.BiPredicate;

import com.sevensoupcans.sfsoftware.game.actor.Actor;
import com.sevensoupcans.sfsoftware.game.actor.attributes.Direction;

public class FloodFillPathFindingMap 
{
	private static final BiPredicate<Integer, Integer> CLOSER_TO_ACTOR = (neighbor, current) -> neighbor > current;
	private static final BiPredicate<Integer, Integer> FARTHER_FROM_ACTOR = (neighbor, current) -> neighbor < current;	
	
	final private int[][] distanceValue;
	final private int actorTileValue;
	final private int actorTileX;
	final private int actorTileY;
	
	final private Actor actor;
	
	public FloodFillPathFindingMap(Actor actor) 
	{
		this.actor = actor;
		
		distanceValue = 
				new int[actor.getAssociatedGame().getPlayingFieldWidth()][actor.getAssociatedGame().getPlayingFieldHeight()];
		
		actorTileValue = 
				(actor.getAssociatedGame().getPlayingFieldWidth() * actor.getAssociatedGame().getPlayingFieldHeight()) / 4;
		
		actorTileX = actor.getCurrentTileX();
		actorTileY = actor.getCurrentTileY();
		
		populateMapValues(actorTileX, actorTileY, this.getActorLocationValue());
	}
	
	private void populateMapValues(int startX, int startY, int startValue) {
	    // Queue to manage tiles to process
	    Queue<int[]> queue = new LinkedList<>();
	    queue.add(new int[]{startX, startY, startValue});

	    boolean[][] walkabilityMap = this.actor.getAssociatedGame().getTileMap().getWalkabilityMap(true);
	    
	    while (!queue.isEmpty()) {
	        int[] current = queue.poll();
	        int x = current[0], y = current[1], value = current[2];

	        // Boundary check
	        if (x < 0 || x >= distanceValue.length || y < 0 || y >= distanceValue[0].length) continue;

	        // Skip tiles that are unwalkable or already have a higher value
	        if (!(walkabilityMap[x][y]) || distanceValue[x][y] >= value) continue;

	        // Assign the value to the current tile
	        distanceValue[x][y] = value;

	        // Stop propagation if value is <= 0
	        if (value <= 0) continue;

	        // Add neighbors to the queue
	        queue.add(new int[]{x - 1, y, value - 1}); // Left
	        queue.add(new int[]{x + 1, y, value - 1}); // Right
	        queue.add(new int[]{x, y - 1, value - 1}); // Up
	        queue.add(new int[]{x, y + 1, value - 1}); // Down
	    }
	}

	private List<Direction> getDirectionalTilesMatching(int currentX, int currentY, BiPredicate<Integer, Integer> condition) 
	{
	    int currentValue = distanceValue[currentX][currentY];
	    List<Direction> directions = new ArrayList<>();

	    if (currentValue <= 0 || currentValue >= this.getActorLocationValue()) return directions;

	    for (Direction dir : Direction.getCardinals()) {
	        int neighborX = currentX + dir.getXDifference();
	        int neighborY = currentY + dir.getYDifference();

	        if (isInBounds(neighborX, neighborY)) {
	            int neighborValue = distanceValue[neighborX][neighborY];
	            if (condition.test(neighborValue, currentValue)) {
	                directions.add(dir);
	            }
	        }
	    }

	    return directions;
	}
	
	public List<Direction> getDirectionsOfNextClosestTile(int x, int y) 
	{
	    return getDirectionalTilesMatching(x, y, CLOSER_TO_ACTOR);
	}

	public List<Direction> getDirectionsOfNextFarthestTile(int x, int y) 
	{
	    return getDirectionalTilesMatching(x, y, FARTHER_FROM_ACTOR);
	}
	
	private boolean isInBounds(int x, int y) {
	    return x >= 0 && y >= 0 && x < distanceValue.length && y < distanceValue[0].length;
	}
	
	public int getValueAtTile(final int x, final int y)
	{
		return distanceValue[x][y];
	}
	
	public int getActorLocationValue()
	{
		return actorTileValue;
	}
	
	public int getActorTileX()
	{
		return actorTileX;
	}
	
	public int getActorTileY()
	{
		return actorTileY;
	}
}

package com.sevensoupcans.sfsoftware.util.tile.pathfinding;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import com.sevensoupcans.sfsoftware.game.actor.Actor;
import com.sevensoupcans.sfsoftware.game.actor.attributes.Direction;

public class FloodFillPathFindingMap {

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
		
		/*for(int y = 0; y < distanceValue[0].length; y++)
		{	
			for(int x = 0; x < distanceValue.length; x++)
			{	
				if(distanceValue[x][y] < 10) {
					System.out.print("0" + distanceValue[x][y]);					
				}
				else 
				{
					System.out.print(distanceValue[x][y]);
				}
			}
			System.out.print("\n");
		}*/
	}
	
	private void populateMapValues(int startX, int startY, int startValue) {
	    // Queue to manage tiles to process
	    Queue<int[]> queue = new LinkedList<>();
	    queue.add(new int[]{startX, startY, startValue});

	    while (!queue.isEmpty()) {
	        int[] current = queue.poll();
	        int x = current[0], y = current[1], value = current[2];

	        // Boundary check
	        if (x < 0 || x >= distanceValue.length || y < 0 || y >= distanceValue[0].length) continue;

	        // Skip tiles that are unwalkable or already have a higher value
	        if (!(this.actor.getAssociatedGame().getTileMap().getMap()[x][y].isWalkable()) 
	        		|| distanceValue[x][y] >= value) continue;

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
	
	public List<Direction> getDirectionsOfNextTile(final int currentX, final int currentY)
	{	
		int currentValue = distanceValue[currentX][currentY];
		
		ArrayList<Direction> al = new ArrayList<Direction>();
		if(currentValue <= 0 || currentValue >= this.getActorLocationValue()) return al;
		
		for(int y = 0; y < distanceValue[0].length; y++)
		{	
			for(int x = 0; x < distanceValue.length; x++)
			{
				if(distanceValue[x][y] == (currentValue + 1))
				{
					if(y > currentY) al.add(Direction.DOWN);
					if(y < currentY) al.add(Direction.UP);
					if(x > currentX) al.add(Direction.RIGHT);
					if(x < currentX) al.add(Direction.LEFT);
				}
			}
		}
		
		return al;
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

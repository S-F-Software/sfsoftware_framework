package com.sevensoupcans.sfsoftware.util.tile.pathfinding;

import java.util.ArrayList;

import com.sevensoupcans.sfsoftware.game.actor.Actor;
import com.sevensoupcans.sfsoftware.util.SortedList;
import com.sevensoupcans.sfsoftware.util.tile.TileMap;
import com.sevensoupcans.sfsoftware.util.tile.pathfinding.heuristics.ClosestHeuristic;
import com.sevensoupcans.sfsoftware.util.tile.pathfinding.node.Node;
import com.sevensoupcans.sfsoftware.util.tile.pathfinding.node.NodeSortingComparator;

public class AStarPathFinder implements PathFinder {

	private ArrayList<Node> closed = new ArrayList<Node>();
	private SortedList<Node> open = new SortedList<Node>();
	
	private final TileMap tileMap;
	private final int maxSearchDistance;
	private final boolean allowDiagonalMovement;
	private final AStarHeuristic heuristic;
	
	private final Node[][] nodes;
	
	
	public AStarPathFinder(TileMap tileMap, int maxSearchDistance, boolean allowDiagonalMovement)
	{
		this(tileMap, maxSearchDistance, allowDiagonalMovement, new ClosestHeuristic());
	}
	
	public AStarPathFinder(TileMap tileMap, int maxSearchDistance, boolean allowDiagonalMovement, 
			AStarHeuristic heuristic)
	{
		this.tileMap = tileMap;
		this.maxSearchDistance = maxSearchDistance;
		this.allowDiagonalMovement = allowDiagonalMovement;
		this.heuristic = heuristic;
		
		this.nodes = new Node[tileMap.getWidth()][tileMap.getHeight()];
		for(int x = 0; x < tileMap.getWidth(); x++)
		{
			for(int y = 0; y < tileMap.getHeight(); y++)
			{
				nodes[x][y] = new Node(x, y);
			}
		}
	}
	
	protected void addtoClosed(Node node)
	{
		this.closed.add(node);
	}
	
	protected void addToOpen(Node node)
	{
		this.open.add(node, new NodeSortingComparator());
	}
	
	protected Node getFirstInOpen() 
	{
		return this.open.get(0);		
	}
	
	public float getMovementCost(Actor actor, int startX, int startY, int targetX, int targetY)
	{
		// TODO Variable movement cost based on tile type, etc.
		return 1;
	}
	
	protected boolean inClosedList(Node node)
	{
		return this.closed.contains(node);
	}
	
	protected boolean inOpenList(Node node)
	{
		return this.open.contains(node);
	}
	
	protected boolean isValidLocation(Actor actor, int sx, int sy, int x, int y) 
	{
		boolean invalid = (x < 0) || (y < 0) || (x >= tileMap.getWidth()) || (y >= tileMap.getHeight());		
		if ((!invalid) && ((sx != x) || (sy != y))) 
		{
			invalid = !(tileMap.getMap()[x][y].isWalkable());
		}
		return !invalid;
	}	
	
	public float getHeuristicCost(Actor actor, int x, int y, int tx, int ty)
	{
		return this.heuristic.getCost(this.tileMap, actor, x, y, tx, ty);
	}
	
	@Override
	public Path getPath(Actor actor, int startX, int startY, int destX, int destY) 
	{
		nodes[startX][startY].setCost(0);
		nodes[startX][startY].setDepth(0);
		
		this.closed.clear();
		this.open.clear();
		this.open.add(nodes[startX][startY], new NodeSortingComparator());
		
		//nodes[destX][destY].parent = null;
		
		int maxDepth = 0;
		while((maxDepth < maxSearchDistance) && (open.size() != 0))
		{
			Node current = this.getFirstInOpen();
			if(current == nodes[destX][destY]) break;
			
			this.removeFromOpen(current);
			this.addtoClosed(current);
			
			for(int x=-1; x<2; x++) 
			{
				for(int y=-1; y<2; y++) 
				{
					if((x == 0) && (y == 0)) continue;
					
					if(!this.allowDiagonalMovement)
					{
						if((x != 0) && (y != 0)) continue;							
					}
					
					int xp = x + current.getX();
					int yp = y + current.getY();
					
					if (isValidLocation(actor, startX, startY, xp, yp)) 
					{
						float nextStepCost = current.getCost() + getMovementCost(actor, current.getX(), current.getY(), xp, yp);
						Node neighbor = nodes[xp][yp];

						//TODO The following is used for debugging purposes per coke and code's example
						//tileMap.pathFinderVisited(xp, yp);
						
						if (nextStepCost < neighbor.getCost()) 
						{
							if (inOpenList(neighbor)) 
							{
								this.removeFromOpen(neighbor);
							}
							if (inClosedList(neighbor)) 
							{
								this.removeFromClosed(neighbor);
							}
						}
						
						if (!inOpenList(neighbor) && !(inClosedList(neighbor))) 
						{
							neighbor.setCost(nextStepCost);
							neighbor.setHeuristic(getHeuristicCost(actor, xp, yp, destX, destY));
							maxDepth = Math.max(maxDepth, neighbor.setParent(current));
							this.addToOpen(neighbor);
						}
					}
				}
			}
		}
		
		if(nodes[destX][destY].getParent() == null) return null;

		Path path = new Path(this.tileMap);
		Node target = nodes[destX][destY];
		while(target != nodes[startX][startY])
		{
			path.prependStep(target.getX(), target.getY());
			target = target.getParent();
		}
		path.prependStep(startX, startY);
		
		return path;
		
	}
	
	protected void removeFromClosed(Node node)
	{
		this.closed.remove(node);
	}
	
	protected void removeFromOpen(Node node)
	{
		this.open.remove(node);
	}

	@Override
	public TileMap getAssociatedTileMap() 
	{
		return this.tileMap;
	}

}

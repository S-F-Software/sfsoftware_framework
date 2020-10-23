package com.sevensoupcans.sfsoftware.util.tile.pathfinding.node;

public class Node implements Comparable<Node> {

	private int x;
	private int y;
	private float cost;
	private Node parent;
	private float heuristic;
	private int depth;
	
	public Node(int x, int y) 
	{
		this.x = x;
		this.y = y;
	}
	
	public float getCost()
	{
		return this.cost;
	}
	
	public Node getParent()
	{
		return this.parent;
	}
	
	public int getX()
	{
		return this.x;
	}
	
	public int getY()
	{
		return this.y;
	}
	
	public float setCost(float cost)
	{
		return (this.cost = cost);
	}
	
	public int setDepth(int depth)
	{
		return (this.depth = depth);
	}
	
	public void setHeuristic(float heuristic)
	{
		this.heuristic = heuristic;
	}
	
	public int setParent(Node parent)
	{
		this.parent = parent;		
		this.depth = parent.depth + 1;				
		return this.depth;
	}

	@Override
	public int compareTo(Node o) 
	{
		float f = this.heuristic + this.cost;
		float of = o.heuristic + o.cost;
		
		if(f < of)
		{
			return -1;
		}
		else if (f > of)
		{
			return 1;
		}
		else
		{
			return 0;
		}
	}
	

}

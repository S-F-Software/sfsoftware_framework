package com.sevensoupcans.sfsoftware.util.tile.pathfinding;

import java.util.ArrayList;

import com.sevensoupcans.sfsoftware.util.tile.Tile;
import com.sevensoupcans.sfsoftware.util.tile.TileMap;

public class Path {

	private ArrayList<Tile> steps = new ArrayList<Tile>();
	private final TileMap tileMap;
	
	public Path(TileMap tileMap) 
	{		
		this.tileMap = tileMap;
	}
	
	public void appendStep(Tile tile)
	{
		this.steps.add(tile);
	}
	
	public boolean contains(Tile tile)
	{
		return this.steps.contains(tile);
	}
	
	public int getLength()
	{
		return this.steps.size();
	}
	
	public Tile getStep(int index)
	{
		return this.steps.get(index);
	}
	
	public ArrayList<Tile> getSteps()
	{
		return this.steps;
	}

	public int getX(int index)
	{
		return (int) this.getStep(index).getX();
	}	
	
	public int getY(int index)
	{
		return (int) this.getStep(index).getY();
	}
	
	public void prependStep(int x, int y)
	{
		this.prependStep(this.tileMap.getMap()[x][y]);
	}
	
	public void prependStep(Tile tile)
	{
		this.steps.add(0, tile);
	}

}

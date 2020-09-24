package com.sevensoupcans.sfsoftware.util.tile;

import com.sevensoupcans.sfsoftware.util.graphics.Sprite;

public class Tile extends Sprite {

	private static final int DEFAULT_TILE_SIZE = 40;
	
	/**
	 * Returns the default tile size (40px)
	 * 
	 * @return
	 */
	public static int getDefaultTileSize() 
	{
		return DEFAULT_TILE_SIZE;
	}
	
	private boolean walkable = true;
	
	public Tile(int destX, int destY, String texture, boolean isWalkable)
	{
		 super(destX, destY, texture);
		 walkable = isWalkable;
	}
	
	public boolean isWalkable()
	{
		return walkable;
	}	
	
	@Override
	public void setTexture(String newTex)
	{
		super.setTexture(newTex);
		
		this.setSrcX(0);
		this.setSrcY(0);
	}	
	
	public void setWalkable(boolean w)
	{
		walkable = w;
	}
		
}

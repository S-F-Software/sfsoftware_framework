package com.sevensoupcans.sfsoftware.util.tile;

import com.sevensoupcans.sfsoftware.util.graphics.Sprite;
import com.sevensoupcans.sfsoftware.util.graphics.Texture;

public class Tile extends Sprite {

	private static final int DEFAULT_TILE_SIZE = 40;
	
	/**
	 * Returns the default tile size (40px)
	 * 
	 * @return
	 */
	public final static int getDefaultTileSize() 
	{
		return DEFAULT_TILE_SIZE;
	}
	
	private boolean walkable = true;
	
	public Tile(int destX, int destY, String texture, boolean isWalkable)
	{
		this(destX, destY, texture, 0, 0, isWalkable);
	}
	
	public Tile(int destX, int destY, String texture, int srcX, int srcY, boolean isWalkable)
	{
		 this(destX, destY, texture, getDefaultTileSize(), srcX, srcY, isWalkable);
	}
	
	public Tile(int destX, int destY, String texture, int tileSize, int srcX, int srcY, boolean isWalkable)
	{
		 super(destX, destY, texture, tileSize, tileSize, srcX, srcY);
		 walkable = isWalkable;
	}	
	
	@Override
	public boolean equals(Object obj)
	{
		if(obj instanceof Tile)
		{
			Tile tile = (Tile) obj;
			
			return (tile.getX() == this.getX()) 
					&& (tile.getY() == this.getY())
					&& (tile.isWalkable() == this.isWalkable())
					&& (tile.getTextureName().equalsIgnoreCase(this.getTextureName()));
		}
		
		return false;
	}
	
	public final boolean isWalkable()
	{
		return walkable;
	}	
	
	@Override
	public final Texture setTexture(final String newTex)
	{
		super.setTexture(newTex);
		
		this.setSrcX(0);
		this.setSrcY(0);
		
		return this.getTexture();
	}	
	
	public final void setWalkable(final boolean w)
	{
		walkable = w;
	}
		
}

package com.sevensoupcans.sfsoftware.util.tile;

import com.sevensoupcans.sfsoftware.util.MathUtils;
import com.sevensoupcans.sfsoftware.util.graphics.Sprite;

public final class TileMapSpriteIntersection {

	private final boolean bottomLeft;
	private final boolean bottomRight;	
	private final boolean topLeft;
	private final boolean topRight;
	
	public TileMapSpriteIntersection(Sprite sprite, TileMap tileMap, int destX, int destY) 
	{
		this(sprite, tileMap.getMap(), tileMap.getTileSize(), destX, destY);				
	}
	
	public TileMapSpriteIntersection(Sprite sprite, Tile[][] tiles, int tileSize, int destX, int destY)
	{
		int upY = MathUtils.ensureRange((int) Math.floor((destY - (sprite.getHeight() / 2)) / tileSize), 
				0, 	tiles[0].length - 1);

		int downY = MathUtils.ensureRange((int) Math.floor(((destY + (sprite.getHeight() / 2)) - 1) / tileSize), 
				0, tiles[0].length - 1);

		int leftX = MathUtils.ensureRange((int) Math.floor((destX - (sprite.getWidth() / 2)) / tileSize), 
				0, tiles.length - 1);

		int rightX = MathUtils.ensureRange((int) Math.floor(((destX + (sprite.getWidth() / 2)) - 1) / tileSize), 
				0, tiles.length - 1);				
		
		this.topLeft = tiles[leftX][upY].isWalkable();
		this.bottomLeft = tiles[leftX][downY].isWalkable();
		this.topRight = tiles[rightX][upY].isWalkable();						
		this.bottomRight = tiles[rightX][downY].isWalkable();
	}
	
	public boolean isBottomLeftCornerWalkable()
	{
		return bottomLeft;
	}
	
	public boolean isBottomRightCornerWalkable()
	{
		return bottomRight;
	}
	
	public boolean isTopLeftCornerWalkable()
	{
		return topLeft;
	}
	
	public boolean isTopRightCornerWalkable()
	{
		return topRight;
	}
	
}

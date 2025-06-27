package com.sevensoupcans.sfsoftware.util.tile;

import com.sevensoupcans.sfsoftware.util.MathUtils;
import com.sevensoupcans.sfsoftware.util.graphics.Sprite;
import com.sevensoupcans.sfsoftware.util.graphics.geometry.Quad;

public final class TileMapSpriteIntersection 
{
	private final boolean bottomLeft;
	private final boolean bottomRight;	
	private final boolean topLeft;
	private final boolean topRight;
	
	public TileMapSpriteIntersection(Sprite sprite, TileMap tileMap, 
			float collisionBoxDestCenterX, float collisionBoxDestCenterY) 
	{
		this(sprite, tileMap.getMap(), tileMap.getTileSize(), collisionBoxDestCenterX, collisionBoxDestCenterY);				
	}
	
	public TileMapSpriteIntersection(Sprite sprite, Tile[][] tiles, int tileSize, 
			float collisionBoxDestCenterX, float collisionBoxDestCenterY) 
	{
		Quad box = sprite.getCollisionBox();
		
		float halfWidth = box.getWidth() / 2f;
		float halfHeight = box.getHeight() / 2f;

		// These are the actual bounds of the moved collision box
		float left = collisionBoxDestCenterX - halfWidth;
		float right = collisionBoxDestCenterX + halfWidth - 1; // "-1" for inclusive bound
		float top = collisionBoxDestCenterY - halfHeight;
		float bottom = collisionBoxDestCenterY + halfHeight - 1;

		int leftX = MathUtils.ensureRange((int) Math.floor(left / tileSize), 0, tiles.length - 1);
		int rightX = MathUtils.ensureRange((int) Math.floor(right / tileSize), 0, tiles.length - 1);
		int upY = MathUtils.ensureRange((int) Math.floor(top / tileSize), 0, tiles[0].length - 1);
		int downY = MathUtils.ensureRange((int) Math.floor(bottom / tileSize), 0, tiles[0].length - 1);

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

package com.sevensoupcans.sfsoftware.util;

import com.sevensoupcans.sfsoftware.util.graphics.Sprite;

public class Tile extends Sprite {

	private static final int DEFAULT_TILE_SIZE = 40;
	
	private boolean walkable = true;
	private boolean visible = false;
	
	public Tile(int destX, int destY, String texture, boolean isWalkable)
	{
		 super(destX, destY, texture);
		 walkable = isWalkable;
		 visible = true;
	}

	public boolean isVisible()
	{
		return visible;
	}
	
	public boolean isWalkable()
	{
		return walkable;
	}
	
	public void setVisible(boolean v)
	{
		visible = v;
	}
	
	public void setWalkable(boolean w)
	{
		walkable = w;
	}
	
	public static int getBitMaskTileId(int x, int y, String currentTileTexture, Tile[][] tileMap)
	{
		int id = 15;
		String binStr = "";
		
		if(y == 0 || tileMap[x][y - 1].getTexture().equals(currentTileTexture))
		{
			binStr = "1";
		}
		else
		{
			binStr = "0";
		}
		
		if(x == (tileMap.length - 1) || tileMap[x + 1][y].getTexture().equals(currentTileTexture))
		{
			binStr = "1" + binStr;
		}
		else
		{
			binStr = "0" + binStr;
		}

		if(y == (tileMap[0].length - 1) || tileMap[x][y + 1].getTexture().equals(currentTileTexture))
		{
			binStr = "1" + binStr;
		}
		else
		{
			binStr = "0" + binStr;
		}
		
		if(x == 0 || tileMap[x - 1][y].getTexture().equals(currentTileTexture))
		{
			binStr = "1" + binStr;
		}
		else
		{
			binStr = "0" + binStr;
		}			
		
		id = Integer.parseInt(binStr, 2);			
		
		return id;
	}	
	
	public static int getDefaultTileSize() 
	{
		return DEFAULT_TILE_SIZE;
	}
	
	public static void floodFillTileVisibility(Tile[][] tileMap, int tileX, int tileY)
	{
		try
		{
			tileMap[tileX][tileY].setVisible(true);
		}
		catch(IndexOutOfBoundsException e)
		{
			e.printStackTrace();
		}
		
		if(tileX < tileMap.length - 1 && !(tileMap[tileX+1][tileY].isVisible()))
		{
			if((tileMap[tileX+1][tileY].isWalkable()))
			{
				floodFillTileVisibility(tileMap, tileX + 1, tileY);
			}
			else
			{
				tileMap[tileX + 1][tileY].setVisible(true);
			}
		}
		if(tileX > 0 && !(tileMap[tileX-1][tileY].isVisible()))
		{
			if((tileMap[tileX-1][tileY].isWalkable()))
			{
				floodFillTileVisibility(tileMap, tileX - 1, tileY);
			}
			else
			{
				tileMap[tileX - 1][tileY].setVisible(true);
			}		
		}
		if(tileY < tileMap[0].length - 1 && !(tileMap[tileX][tileY+1].isVisible()))
		{
			if((tileMap[tileX][tileY + 1].isWalkable()))
			{
				floodFillTileVisibility(tileMap, tileX, tileY + 1);
			}
			else
			{
				tileMap[tileX][tileY + 1].setVisible(true);
			}		
		}
		if(tileY > 0 && !(tileMap[tileX][tileY-1].isVisible())) 
		{
			if((tileMap[tileX][tileY - 1].isWalkable()))
			{
				floodFillTileVisibility(tileMap, tileX, tileY - 1);
			}
			else
			{
				tileMap[tileX][tileY - 1].setVisible(true);
			}		
		}
	}	
		
}

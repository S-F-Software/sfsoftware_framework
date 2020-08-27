package com.sevensoupcans.sfsoftware.util.tile;

/**
 * A class representing a 2D array of Tiles
 * 
 * @author S. Thompson
 *
 */
public class TileMap {
	
	private final int width;
	private final int height;
	private final int tileSize;
	
	private final Tile[][] map;
	
	public TileMap(Tile[][] map)
	{
		this.map = map.clone();
		
		this.width = map.length;
		this.height = map[0].length;
		this.tileSize = map[0][0].getWidth();
	}
	
	public TileMap(int width, int height)
	{
		this(width, height, Tile.getDefaultTileSize());
	}
	
	public TileMap(int width, int height, int tileSize)
	{
		this.width = width;
		this.height = height;
		
		this.tileSize = tileSize;
		
		map = new Tile[width][height];
		
		for(int i = 0; i < width; i++)
		{
			for(int j = 0; j < height; j++)
			{		
				map[i][j] = new Tile(i * tileSize, j * tileSize, "", true);
			}
		}		
	}
	
	/**
	 * Works outward on the tile map from the specified point and sets tile visibility 
	 * of any connected, walkable tiles. Unreachable tiles from the provided point are not set
	 * to be visible. 
	 * 
	 * @param tileX Starting x tile point
	 * @param tileY Starting y tile point
	 */
	public void floodFillTileVisibility(int tileX, int tileY)
	{
		try
		{
			map[tileX][tileY].setVisible(true);
		}
		catch(IndexOutOfBoundsException e)
		{
			e.printStackTrace();
		}
		
		if(tileX < map.length - 1 && !(map[tileX+1][tileY].isVisible()))
		{
			if((map[tileX+1][tileY].isWalkable()))
			{
				floodFillTileVisibility(tileX + 1, tileY);
			}
			else
			{
				map[tileX + 1][tileY].setVisible(true);
			}
		}
		if(tileX > 0 && !(map[tileX-1][tileY].isVisible()))
		{
			if((map[tileX-1][tileY].isWalkable()))
			{
				floodFillTileVisibility(tileX - 1, tileY);
			}
			else
			{
				map[tileX - 1][tileY].setVisible(true);
			}		
		}
		if(tileY < map[0].length - 1 && !(map[tileX][tileY+1].isVisible()))
		{
			if((map[tileX][tileY + 1].isWalkable()))
			{
				floodFillTileVisibility(tileX, tileY + 1);
			}
			else
			{
				map[tileX][tileY + 1].setVisible(true);
			}		
		}
		if(tileY > 0 && !(map[tileX][tileY-1].isVisible())) 
		{
			if((map[tileX][tileY - 1].isWalkable()))
			{
				floodFillTileVisibility(tileX, tileY - 1);
			}
			else
			{
				map[tileX][tileY - 1].setVisible(true);
			}		
		}
	}	
	
	/**
	 * Returns a bitmask id based on the texture of tiles surrounding the Tile at 
	 * the provided x, y coordinates
	 * 
	 * @param x The x position of the tile being checked against
	 * @param y The y position of the tile being checked against
	 * @return An id between 0 and 15 based on the surrounding tiles matching the tile's texture
	 */
	public int getBitMaskTileId(int x, int y)
	{
		int id = 15;
		String binStr = "";
		String currentTileTexture = map[x][y].getTexture();
		
		if(y == 0 || map[x][y - 1].getTexture().equals(currentTileTexture))
		{
			binStr = "1";
		}
		else
		{
			binStr = "0";
		}
		
		if(x == (map.length - 1) || map[x + 1][y].getTexture().equals(currentTileTexture))
		{
			binStr = "1" + binStr;
		}
		else
		{
			binStr = "0" + binStr;
		}

		if(y == (map[0].length - 1) || map[x][y + 1].getTexture().equals(currentTileTexture))
		{
			binStr = "1" + binStr;
		}
		else
		{
			binStr = "0" + binStr;
		}
		
		if(x == 0 || map[x - 1][y].getTexture().equals(currentTileTexture))
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
	
	public Tile[][] getMap()
	{
		return this.map;
	}
	
	public int getHeight()
	{
		return this.height;
	}
	
	/**
	 * Returns the number of occurrences of a specific texture in the tile map.
	 * 
	 * @param textureName
	 * @return
	 */
	public int getTextureCount(String textureName)
	{
		int tileCount = 0;
		
		for(int xTile = 0; xTile < map.length; xTile++)
		{
			for(int yTile = 0; yTile < map[0].length; yTile++)
			{
				if(map[xTile][yTile].getTexture().equalsIgnoreCase(textureName)) tileCount++;
			}
		}
		
		return tileCount;
	}	
	
	public int getTileSize()
	{
		return this.tileSize;
	}
	
	public int getWidth()
	{
		return this.width;
	}
	
	/**
	 * Replaces all instances of a texture in the tile map with a new texture
	 * 
	 * @param targetTextureName The name of the target texture to be replaced
	 * @param replacementTextureName The name of the texture replacing the target
	 * @return
	 */
	public boolean replaceAllTextures(String targetTextureName, String replacementTextureName)
	{
		boolean replacedTiles = false;
		
		for(int xTile = 0; xTile < map.length; xTile++)
		{
			for(int yTile = 0; yTile < map[0].length; yTile++)
			{
				if(map[xTile][yTile].getTexture().equalsIgnoreCase(targetTextureName)) 
				{
					map[xTile][yTile].setTexture(replacementTextureName);
					replacedTiles = true;
				}
			}
		}		
		
		return replacedTiles;
	}
	
	/**
	 * Sets the draw source coordinates for the tile at the provided coordinates in the map
	 * 
	 * @param x
	 * @param y 
	 * @param spriteSheetWidth The number of frames wide the tile's texture is
	 * @return
	 */
	public Tile setTileSrcFromBitMaskId(int x, int y, int spriteSheetWidth)
	{
		int bitMaskId = getBitMaskTileId(x, y);		
		int srcX = (bitMaskId % spriteSheetWidth) * getTileSize(); 
		int srcY = ((int) Math.floor((double) bitMaskId / spriteSheetWidth)) * getTileSize();
		
		map[x][y].setSrc(srcX, srcY);
		
		return map[x][y];
	}
}

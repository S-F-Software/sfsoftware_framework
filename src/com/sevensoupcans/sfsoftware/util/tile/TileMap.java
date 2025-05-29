package com.sevensoupcans.sfsoftware.util.tile;

import java.lang.reflect.InvocationTargetException;
import java.util.Set;

import com.sevensoupcans.sfsoftware.util.MathUtils;
import com.sevensoupcans.sfsoftware.util.graphics.Sprite;
import com.sevensoupcans.sfsoftware.util.graphics.Texture;

/**
 * A class representing a 2D array of Tiles
 * 
 * @author S. Thompson <fuzmeister@sevensoupcans.com>
 *
 */
public class TileMap {
	
	private static Set<String> bitMaskedTextureList;
	
	public static Set<String> setBitMaskedTextureList(Set<String> textures)
	{
		return (bitMaskedTextureList = textures);
	}
	
	private final int width;
	private final int height;
	private final int tileSize;
	
	private final Tile[][] map;
	
	public TileMap(Tile[][] map)
	{
		this.map = map.clone();
		
		this.width = map.length;
		this.height = map[0].length;
		this.tileSize = (int) (map[0][0].getWidth() <= 0 ? Tile.getDefaultTileSize() : map[0][0].getWidth());		
		this.setAllTilesSrcFromBitMaskId(4); 
	}
	
	public TileMap(int width, int height)
	{
		this(width, height, Tile.getDefaultTileSize());
	}
	
	public TileMap(int width, int height, String initialTexture)
	{
		this(width, height, Tile.getDefaultTileSize(), initialTexture);
	}
	
	public TileMap(int width, int height, Sprite initialTexture)
	{
		this(width, height, Tile.getDefaultTileSize(), initialTexture.getTextureName(), 
				initialTexture.getSrcX(), initialTexture.getSrcY());
	}	

	public TileMap(int width, int height, int tileSize)
	{
		this(width, height, tileSize, "");
	}

	public TileMap(int width, int height, int tileSize, String initialTexture)
	{
		this(width, height, tileSize, initialTexture, 0, 0, Tile.class);
	}

	public TileMap(int width, int height, int tileSize, String initialTexture, 
			int initialTextureSrcX,	int initialTextureSrcY)
	{
		this(width, height, tileSize, initialTexture, initialTextureSrcX, initialTextureSrcY, Tile.class);
	}

	public TileMap(int width, int height, int tileSize, String initialTexture, Class<? extends Tile> tileClass)
	{
		this(width, height, tileSize, initialTexture, 0, 0, tileClass);
	}
	
	public TileMap(int width, int height, int tileSize, String initialTexture, int initialTextureSrcX, 
			int initialTextureSrcY, Class<? extends Tile> tileClass)
	{
		this.width = width;
		this.height = height;
		
		this.tileSize = tileSize;
		
		map = new Tile[width][height];
		
		for(int i = 0; i < width; i++)
		{
			for(int j = 0; j < height; j++)
			{					
				try 
				{
					map[i][j] = tileClass.getConstructor(int.class, int.class, 
							String.class, int.class, int.class, int.class, boolean.class).newInstance(i * tileSize, 
									j * tileSize, initialTexture, tileSize, initialTextureSrcX, 
									initialTextureSrcY, true);
				} 
				catch (InstantiationException | IllegalAccessException | IllegalArgumentException
						| InvocationTargetException | NoSuchMethodException | SecurityException e) 
				{
					map[i][j] = new Tile(i * tileSize, j * tileSize, initialTexture, tileSize, 
							initialTextureSrcX, initialTextureSrcY, true);
				}
			}
		}
		
		this.setAllTilesSrcFromBitMaskId(4);
	}

	public final boolean containsTexture(final String textureName)
	{
		return (this.getTextureCount(textureName) > 0);		
	}
	
	public final void draw()
	{
		draw(1.0f, 1.0f, 1.0f, 1.0f);
	}
	
	public final void draw(final float alpha)
	{
		draw(1.0f, 1.0f, 1.0f, alpha);
	}
	
	public final void draw(final float red, final float green, final float blue, final float alpha)
	{
		for(int i = 0; i < this.width; i++)
		{
			for(int j = 0; j < this.height; j++)
			{
				Tile tile = this.map[i][j];
				if(tile != null)
				{
					tile.draw(getTileSize(), getTileSize(), tile.getSrcX(), tile.getSrcY(),							
						getTileSize(), getTileSize(), red, green, blue, alpha);
				}
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
	public final void floodFillTileVisibility(final int tileX, final int tileY)
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
	public final int getBitMaskTileId(final int x, final int y)
	{
		int id = 15;
		String binStr = "";
		String currentTileTexture = map[x][y].getTextureName();
						
		if(y == 0 || Sprite.getTextureName(map[x][y - 1]).equals(currentTileTexture))
		{
			binStr = "1";
		}
		else
		{
			binStr = "0";
		}
		
		if(x == (map.length - 1) || Sprite.getTextureName(map[x + 1][y]).equals(currentTileTexture))
		{
			binStr = "1" + binStr;
		}
		else
		{
			binStr = "0" + binStr;
		}

		if(y == (map[0].length - 1) || Sprite.getTextureName(map[x][y + 1]).equals(currentTileTexture))
		{
			binStr = "1" + binStr;
		}
		else
		{
			binStr = "0" + binStr;
		}
		
		if(x == 0 || Sprite.getTextureName(map[x - 1][y]).equals(currentTileTexture))
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
	
	public final Tile[][] getMap()
	{
		return this.map;
	}
	
	public final int getHeight()
	{
		return this.height;
	}
	
	public final Tile getRandomEmptyTile()
	{			
		Tile tile;
		// TODO Need to improve the way this tile count / increment stuff works - could check same tile twice/thrice/whatever
		int i = 0;
		do 
		{
			tile = this.getRandomTile();
			i++;
		} while(tile.getTexture() != null && i < this.getTileCount());
		
		return tile;
	}
	
	public final Tile getRandomTile()
	{
		int randomXPoint = MathUtils.randomInt(this.tileSize * this.width);
		int randomYPoint = MathUtils.randomInt(this.tileSize * this.height);		
		return this.getTileAtCoordinate(randomXPoint, randomYPoint);
	}
	
	public final int getTileCount()
	{
		return this.getWidth() * this.getHeight();
	}
	
	/**
	 * Returns the number of occurrences of a specific texture in the tile map.
	 * 
	 * @param textureName
	 * @return
	 */
	public final int getTextureCount(final String textureName)
	{
		int tileCount = 0;
		
		for(int xTile = 0; xTile < map.length; xTile++)
		{
			for(int yTile = 0; yTile < map[0].length; yTile++)
			{
				if(map[xTile][yTile].getTextureName().equalsIgnoreCase(textureName)) tileCount++;
			}
		}
		
		return tileCount;
	}	
	
	public final int getTileSize()
	{		
		return (this.tileSize <= 0 ? Tile.getDefaultTileSize() : this.tileSize);
	}
	
	public final Tile getTileAtCoordinate(final int x, final int y)
	{
		int tileX = this.getTileMapXIndexFromXCoordinate(x);
		int tileY = this.getTileMapYIndexFromYCoordinate(y);
		Tile tile = null;
		
		try 
		{
			tile = this.getMap()[tileX][tileY];	
		}
		catch(ArrayIndexOutOfBoundsException e)
		{
			
		}
		
		return tile;		
	}
	
	public final int getTileMapXIndexFromXCoordinate(final float x)
	{
		return (int) Math.floor(x / this.getTileSize());
	}		
	
	public final int getTileMapYIndexFromYCoordinate(final float y)
	{		
		return (int) Math.floor(y / this.getTileSize());
	}
	
	/**
	 * Returns the texture located at a given position on the tile map
	 * 
	 * @param x The x coordinate
	 * @param y The y coordinate
	 * @return Texture located in the tile map at the specified point
	 */
	public final Texture getTextureAtPoint(final float x, final float y)
	{		
		return this.getMap()[this.getTileMapXIndexFromXCoordinate(x)][this.getTileMapYIndexFromYCoordinate(y)].getTexture();
	}
	
	public final int getWidth()
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
	public final boolean replaceAllTextures(final String targetTextureName, final String replacementTextureName)
	{
		boolean replacedTiles = false;
		
		for(int xTile = 0; xTile < map.length; xTile++)
		{
			for(int yTile = 0; yTile < map[0].length; yTile++)
			{
				if(map[xTile][yTile].getTextureName().equalsIgnoreCase(targetTextureName)) 
				{
					map[xTile][yTile].setTexture(replacementTextureName);
					replacedTiles = true;
				}
			}
		}		
		
		return replacedTiles;
	}
	
	private final Tile[][] setAllTilesSrcFromBitMaskId(final int spriteSheetWidth)
	{
		if(bitMaskedTextureList == null) return null;
		
		for(int xTile = 0; xTile < map.length; xTile++)
		{
			for(int yTile = 0; yTile < map[0].length; yTile++)
			{			
				if(map[xTile][yTile] != null && bitMaskedTextureList.contains(map[xTile][yTile].getTextureName().trim()))
					setTileSrcFromBitMaskId(xTile, yTile, spriteSheetWidth);
			}
		}
		
		return map;
	}	
	
	/**
	 * Sets the draw source coordinates for all tiles on the map if a tile's texture 
	 * is in the provided set.
	 * 
	 * @param bitMaskedTextureList List of texture names to be affected by bit masking
	 * @param spriteSheetWidth The number of frames wide the tile's texture is
	 */
	public final Tile[][] setAllTilesSrcFromBitMaskId(final Set<String> bitMaskedTextureList, int spriteSheetWidth)
	{
		for(int xTile = 0; xTile < map.length; xTile++)
		{
			for(int yTile = 0; yTile < map[0].length; yTile++)
			{			
				if(bitMaskedTextureList.contains(map[xTile][yTile].getTextureName().trim()))
					setTileSrcFromBitMaskId(xTile, yTile, spriteSheetWidth);
			}
		}
		
		return map;
	}
	
	/**
	 * Sets the draw source coordinates for the tile at the provided coordinates in the map
	 * 
	 * @param x
	 * @param y 
	 * @param spriteSheetWidth The number of frames wide the tile's texture is
	 * @return
	 */
	public final Tile setTileSrcFromBitMaskId(final int x, final int y, final int spriteSheetWidth)
	{
		int bitMaskId = getBitMaskTileId(x, y);		
		int srcX = (bitMaskId % spriteSheetWidth) * getTileSize(); 
		int srcY = ((int) Math.floor((double) bitMaskId / spriteSheetWidth)) * getTileSize();
		
		map[x][y].setSrc(srcX, srcY);
		
		return map[x][y];
	}
}

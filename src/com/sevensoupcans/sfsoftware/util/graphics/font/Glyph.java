package com.sevensoupcans.sfsoftware.util.graphics.font;

public class Glyph 
{

	private final int width;
	private final int height;
	private final int storedX;
	private final int storedY;
	
	public Glyph(int width, int height, int storedX, int storedY) 
	{
		this.width = width;
		this.height = height;
		this.storedX = storedX;
		this.storedY = storedY;
	}
	
	public int getHeight()
	{
		return height;
	}
	
	public int getStoredX() 
	{
		return storedX;
	}
	
	public int getStoredY()
	{
		return storedY;
	}
	
	public int getWidth()
	{
		return width;
	}

}

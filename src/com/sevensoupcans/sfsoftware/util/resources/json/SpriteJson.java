package com.sevensoupcans.sfsoftware.util.resources.json;

import org.json.JSONObject;

import com.sevensoupcans.sfsoftware.util.graphics.Sprite;

public class SpriteJson 
{
	private final int height;
	private final int srcX;
	private final int srcY;	
	private final int width;
	private final Sprite sprite;
	private final String texture;
	
	public SpriteJson(Object json) 
	{
		if(json instanceof JSONObject)
		{
			JSONObject spriteJson = (JSONObject) json;
			this.height = Integer.valueOf(spriteJson.getString("height"));
			this.srcX = Integer.valueOf(spriteJson.getString("src_x"));
			this.srcY = Integer.valueOf(spriteJson.getString("src_y"));
			this.texture = spriteJson.getString("texture");
			this.width = Integer.valueOf(spriteJson.getString("width"));
		}
		else
		{
			this.height = 0;
			this.srcX = 0;
			this.srcY = 0;
			this.texture = String.valueOf(json);
			this.width = 0;
		}
		
		this.sprite = new Sprite(0, 0, this.texture, this.width, this.height, this.srcX, this.srcY);
	}
	
	public int getHeight()
	{
		return (int) this.getSprite().getHeight();
	}	
	
	public Sprite getSprite()
	{
		return this.sprite;
	}
	
	public int getSrcX()
	{
		return this.getSprite().getSrcX();
	}
	
	public int getSrcY()
	{
		return this.getSprite().getSrcY();
	}
	
	public String getTextureName()
	{
		return this.getSprite().getTextureName();
	}
	
	public int getWidth()
	{
		return (int) this.getSprite().getWidth();
	}

}

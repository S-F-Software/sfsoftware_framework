package com.sevensoupcans.sfsoftware.util.graphics;

import com.sevensoupcans.sfsoftware.game.Game;
import com.sevensoupcans.sfsoftware.game.actor.attributes.Collidable;

public class Sprite {
	
	public static boolean isSpriteOutOfGameBounds(Game game, Sprite sprite)
	{		
		int x = (int) sprite.getX();
		int y = (int) sprite.getY();
		int width = sprite.getWidth();
		int height = sprite.getHeight();
		
		return (x < (0 - width) || x > ((game.getPlayingFieldWidth() * game.getTileSize()) + width) || y < (0 - height) || y > ((game.getPlayingFieldHeight() * game.getTileSize()) + height));
	}
	
	private int x;	
	private int y;
	private int srcX;
	private int srcY;
	
	private Texture associatedTexture;
	private int height = 0;
	
	private int width = 0;

	public Sprite(int destX, int destY, String textureName)
	{		
		this(destX, destY, textureName, 0, 0);
	}
	
	public Sprite(int destX, int destY, Texture texture)
	{
		this(destX, destY, texture.getName());
	}
	
	public Sprite(int destX, int destY, String textureName, int destSrcX, int destSrcY)
	{
		x = destX;
		y = destY;

		if(!(textureName.equals("")) && !(Texture.isTextureLoaded(textureName))) System.out.println("The texture '" + textureName + "' was not found.");
		
		associatedTexture = Texture.getTexture(textureName);
		srcX = destSrcX;
		srcY = destSrcY;
	}
	
	public boolean collidingWith(Collidable object) 
	{		
		boolean isColliding = false;
		if(object instanceof Sprite)
		{
			Sprite a = (Sprite) object;
			Sprite b = this; //(Actor) objB;
			
			// Calculate distance from center points of sprites - hence the extra math
			int xDistance = (int) ((b.getX() + (b.getWidth() / 2)) - (a.getX() + (a.getWidth() / 2)));
			int yDistance = (int) ((b.getY() + (b.getHeight() / 2)) - (a.getY() + (a.getHeight() / 2)));

			if (Math.sqrt((xDistance * xDistance) + (yDistance * yDistance)) < (b.getWidth() / 2) + (a.getWidth() / 2)) {
				isColliding = true;
			}
			
		}
		return isColliding;		
	}
	
	public void draw(int width, int height)
	{
		draw(width, height, srcX, srcY, width, height);
	}
	
	// Only use this for non-scaled sprites. Assumes the source dimensions are the same as the destination ones.
	public void draw(int width, int height, int srcX, int srcY)
	{
		draw(width, height, srcX, srcY, width, height);
	}
	
	public void draw(int width, int height, int srcX, int srcY, int srcWidth, int srcHeight)
	{
		Graphics.drawSprite(this.getX(), this.getY(), associatedTexture.getName(), width, height, srcX, srcY, srcWidth, srcHeight);	
	}	
	
	public void draw(int width, int height, int srcX, int srcY, int srcWidth, int srcHeight, float red, float green, float blue, float alpha)
	{
		Graphics.drawSprite(this.getX(), this.getY(), associatedTexture.getName(), width, height, srcX, srcY, srcWidth, srcHeight, red, green, blue, alpha);	
	}
	
	public void draw(int x, int y, int width, int height, int srcX, int srcY, int srcWidth, int srcHeight, float red, float green, float blue, float alpha)
	{
		setX(x);
		setY(y);
		
		draw(width, height, srcX, srcY, srcWidth, srcHeight, red, green, blue, alpha);	
	}
	
	public int getBottom()
	{
		return (y + height);
	}
	
	public int getCenterX()
	{
		return (x + (width / 2));
	}
	
	public int getCenterY()
	{
		return (y + (height / 2));
	}
	
	public int getHeight()
	{
		return height;
	}
	
	public int getLeft()
	{
		return x;
	}
	
	public int getRight()
	{
		return (x + width);
	}
	
	public int getSrcX()
	{
		return srcX;
	}
	
	public int getSrcY()
	{
		return srcY;
	}
	
	public String getTexture()
	{
		return associatedTexture.getName();
	}
	
	public int getTop()
	{
		return y;
	}
	
	public int getWidth()
	{
		return width;
	}
	
	public float getX()
	{
		return x;
	}
	public float getY()
	{
		return y;
	}
	public boolean isOutOfGameBounds(Game game)
	{
		int x = (int) getX();
		int y = (int) getY();
		
		return (x < (0 - getWidth()) || x > ((game.getPlayingFieldWidth() * game.getTileSize()) + getWidth()) || y < (0 - getHeight()) || y > ((game.getPlayingFieldHeight() * game.getTileSize()) + getHeight()));		
	}
	public void move(float destX, float destY)
	{
		move(Math.round(destX), Math.round(destY));
	}	
	public void move(int destX, int destY)
	{
		x = destX;
		y = destY;
	}
	public void setHeight(int newHeight)
	{
		height = newHeight;
	}
	public void setSrc(int sourceX, int sourceY)
	{
		srcX = sourceX;
		srcY = sourceY;
	}	
	public void setSrcX(int sourceX)
	{
		srcX = sourceX;
	}
	public void setSrcY(int sourceY)
	{
		srcY = sourceY;
	}
	
	public void setTexture(String newTextureName)
	{
		associatedTexture = Texture.getTexture(newTextureName);
	}
	
	public void setWidth(int newWidth)
	{
		width = newWidth;
	}
	
	public void setX(int destX)
	{
		x = destX;
	}
	
	public void setY(int destY)
	{
		y = destY;
	}
}

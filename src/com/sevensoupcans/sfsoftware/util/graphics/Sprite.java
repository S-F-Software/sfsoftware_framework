package com.sevensoupcans.sfsoftware.util.graphics;

import org.lwjgl.opengl.GL11;

import com.sevensoupcans.sfsoftware.game.Game;
import com.sevensoupcans.sfsoftware.game.actor.attributes.Collidable;
import com.sevensoupcans.sfsoftware.util.graphics.geometry.Quad;
import com.sevensoupcans.sfsoftware.util.tile.TileMap;

public class Sprite extends Quad {
	
	public static void draw(final float x, final float y, final String textureName, final int width, 
			final int height, final int srcX, final int srcY, final int srcWidth, final int srcHeight)
	{
		draw(x,y,textureName,width,height,srcX,srcY,srcWidth,srcHeight, 1, 1, 1, 1);
	}
	
	public static void draw(final float x, final float y, final String textureName, final int width, 
			final int height, final int srcX, final int srcY, final int srcWidth, final int srcHeight, 
			final float red, final float green, final float blue, final float alpha) 
	{
		draw(x, y, textureName, width, height, srcX, srcY, srcWidth, srcHeight, 
				red, green, blue, alpha, 0);
	}
	
	/**
	 * draw a quad with the image on it - accept float for rotation!
	 */
	public static void draw(final float x, final float y, final String textureName, final int width, 
			final int height, final int srcX, final int srcY, final int srcWidth, final int srcHeight, 
			final float red, final float green, final float blue, final float alpha, final float angle) 
	{		
		// If the provided texture string is null or empty, don't try to draw it. :)
		if(textureName != null && !(textureName.equals("")))
		{			
			Texture temp = Texture.getTexture(textureName);
			if(temp == null)
			{				
		        // enable alpha blending
		        GL11.glEnable(GL11.GL_BLEND);
		        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
				
				GL11.glColor4f(red, green, blue, alpha);
				GL11.glBegin(GL11.GL_QUADS);
					// Top Left
					GL11.glVertex2f(x,y);
					// Top Right					
					GL11.glVertex2f(x + width, y);
					// Bottom Right
					GL11.glVertex2f(x + width,y + height);
					// Bottom Left
					GL11.glVertex2f(x,y + height);
				GL11.glEnd();
			}
			else
			{
				draw(x, y, temp, width, height, srcX, srcY, srcWidth, srcHeight, red, green, blue, alpha, angle);
			}			
		}
	}
	
	public static void draw(final float x, final float y, final Texture texture, final int width, final int height, 
			final int srcX, final int srcY, final int srcWidth, final int srcHeight, final float red, final float green, 
			final float blue, final float alpha, final float angle)
	{
		Texture.drawTexture(x, y, texture, width, height, srcX, srcY, srcWidth, srcHeight, 
				red, green, blue, alpha, angle);
	}	
	
	public static String getTextureName(final Sprite sprite)
	{
		return sprite != null ? sprite.getTextureName() : "";
	}
	
	public static boolean isSpriteOutOfGameBounds(final Game game, final Sprite sprite)
	{		
		int x = (int) sprite.getX();
		int y = (int) sprite.getY();
		int width = sprite.getWidth();
		int height = sprite.getHeight();
		
		return (x < (0 - width) || x > ((game.getPlayingFieldWidth() * game.getTileSize()) + width) 
				|| y < (0 - height) || y > ((game.getPlayingFieldHeight() * game.getTileSize()) + height));
	}
	
	private boolean visible = true;
	
	private float rotationAngle = 0;
	
	private int srcX;
	private int srcY;
	
	// Used in the event the associatedTexture is null
	private String targetTextureName;
	private Texture associatedTexture;	

	public Sprite(String textureName, int srcX, int srcY)
	{
		this(0, 0, textureName, srcX, srcY);
	}
	
	public Sprite(int destX, int destY, String textureName)
	{		
		this(destX, destY, textureName, 0, 0);
	}
	
	public Sprite(int destX, int destY, String textureName, int srcX, int srcY)
	{
		this(destX, destY, textureName, 0, 0, srcX, srcY);
	}
	
	public Sprite(int destX, int destY, String textureName, int width, int height, int srcX, int srcY)
	{
		super(destX, destY, width, height);		
		
		if(Texture.isTextureLoaded(textureName))
		{
			this.associatedTexture = Texture.getTexture(textureName);	
		}
		else if(!(textureName.equals("")))
		{
			this.targetTextureName = textureName;
			this.associatedTexture = null;			
		}		
		
		this.srcX = srcX;
		this.srcY = srcY;
	}
	
	public Sprite(int destX, int destY, Texture texture)
	{
		this(destX, destY, texture.getName());
	}
	
	public final boolean areCornersOnTexture(final TileMap tileMap, final String textureName)
	{
		if(tileMap.getTextureAtPoint(this.getLeft(), this.getTop()).getName().equals(textureName)) return true;
		if(tileMap.getTextureAtPoint(this.getRight(), this.getTop()).getName().equals(textureName)) return true;
		if(tileMap.getTextureAtPoint(this.getLeft(), this.getBottom()).getName().equals(textureName)) return true;
		if(tileMap.getTextureAtPoint(this.getRight(), this.getBottom()).getName().equals(textureName)) return true;	
		
		return false;
	}
	
	public final boolean areCornersOnTexture(final TileMap tileMap, final Texture texture)
	{
		return this.areCornersOnTexture(tileMap, texture.getName());
	}
	
	public boolean collidingWith(final Collidable object) 
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
	
	@Override
	public void draw()
	{
		draw(this.getWidth(), this.getHeight());
	}
	
	public void draw(final int width, final int height)
	{
		draw(width, height, this.getSrcX(), this.getSrcY(), width, height);
	}
	
	// Only use this for non-scaled sprites. Assumes the source dimensions are the same as the destination ones.
	public void draw(final int width, final int height, final int srcX, final int srcY)
	{
		draw(width, height, srcX, srcY, width, height);
	}
	
	public void draw(final int width, final int height, final int srcX, final int srcY, 
			final int srcWidth, final int srcHeight)
	{	
		draw(width, height, srcX, srcY, srcWidth, srcHeight, 
				this.getRed(), this.getGreen(), this.getBlue(), this.getAlpha());
	}
	
	public void draw(final int width, final int height, final int srcX, final int srcY, 
			final int srcWidth, final int srcHeight, final float red, final float green, 
			final float blue, final float alpha)
	{
		draw((int) this.getX(), (int) this.getY(), width, height, srcX, srcY, 
				srcWidth, srcHeight, red, green, blue, alpha);								
	}
	
	public void draw(final int x, final int y, final int width, final int height, final int srcX, 
			final int srcY, final int srcWidth, final int srcHeight, final float red, final float green, 
			final float blue, final float alpha)
	{	
		draw((int) this.getX(), (int) this.getY(), width, height, srcX, srcY, 
				srcWidth, srcHeight, red, green, blue, alpha, this.getRotationAngle());
	}
	
	public void draw(final int x, final int y, final int width, final int height, final int srcX, 
			final int srcY, final int srcWidth, final int srcHeight, final float red, final float green, 
			final float blue, final float alpha, final float rotationAngle)
	{	
		if(!(this.visible)) return;
		
		Sprite.draw(x, y, this.getTextureName(), width, height, srcX, srcY, srcWidth, srcHeight, 
				red, green, blue, alpha, rotationAngle);
	}	
	
	public final float getRotationAngle()
	{
		return this.rotationAngle;
	}
	
	public final int getSrcX()
	{
		return srcX;
	}
	
	public final int getSrcY()
	{
		return srcY;
	}
	
	public final Texture getTexture()
	{
		return (this.associatedTexture != null ? 
				this.associatedTexture : (Texture.isTextureLoaded(this.getTextureName()) ? this.setTexture(this.getTextureName()) : null));
	}
	
	public final String getTextureName()
	{
		return (this.associatedTexture != null ? 
				this.associatedTexture.getName() : (this.targetTextureName != null ? this.targetTextureName : ""));
	}
	
	public final boolean isCenterOnTexture(final TileMap tileMap, final String textureName)
	{
		return (tileMap.getTextureAtPoint(this.getCenterX(), this.getCenterY()).getName().equals(textureName));
	}
	public final boolean isCenterOnTexture(final TileMap tileMap, final Texture texture)
	{
		return this.isCenterOnTexture(tileMap, texture.getName());
	}
	public final boolean isOutOfGameBounds(final Game game)
	{
		int x = (int) getX();
		int y = (int) getY();
		
		return (x < (0 - getWidth()) || x > ((game.getPlayingFieldWidth() * game.getTileSize()) + getWidth()) 
				|| y < (0 - getHeight()) || y > ((game.getPlayingFieldHeight() * game.getTileSize()) + getHeight()));		
	}
	
	public final boolean isVisible()
	{
		return this.visible;
	}
	
	public void move(final float destX, final float destY)
	{
		move(Math.round(destX), Math.round(destY));
	}	
	
	public void move(final int destX, final int destY)
	{
		this.setX(destX);
		this.setY(destY);
	}		

	public final float setRotationAngle(final float rotationAngle)
	{
		return (this.rotationAngle = rotationAngle);
	}
	
	public void setSrc(final int sourceX, final int sourceY)
	{
		srcX = sourceX;
		srcY = sourceY;
	}
	
	public void setSrcX(final int sourceX)
	{
		srcX = sourceX;
	}
	
	public void setSrcY(final int sourceY)
	{
		srcY = sourceY;
	}
	
	public Texture setTexture(final String newTextureName)
	{
		return (this.associatedTexture = Texture.getTexture(newTextureName));
	}
	
	public final boolean setVisible(final boolean visible)
	{
		return (this.visible = visible);
	}
	
	public final boolean toggleVisibility()
	{
		return (this.visible = !(this.visible));
	}
	
}

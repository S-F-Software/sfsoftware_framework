package com.sevensoupcans.sfsoftware.util.graphics;

import org.lwjgl.opengl.GL11;

import com.sevensoupcans.sfsoftware.game.Game;
import com.sevensoupcans.sfsoftware.game.actor.attributes.Collidable;
import com.sevensoupcans.sfsoftware.util.graphics.geometry.Quad;
import com.sevensoupcans.sfsoftware.util.tile.TileMap;

public class Sprite extends Quad {
	
	public static void draw(float x, float y, String textureName, int width, int height, int srcX, int srcY, 
			int srcWidth, int srcHeight)
	{
		draw(x,y,textureName,width,height,srcX,srcY,srcWidth,srcHeight, 1, 1, 1, 1);
	}
	
	public static void draw(float x, float y, String textureName, int width, int height, int srcX, int srcY, 
			int srcWidth, int srcHeight, float red, float green, float blue, float alpha) 
	{
		draw(x, y, textureName, width, height, srcX, srcY, srcWidth, srcHeight, 
				red, green, blue, alpha, 0);
	}
	
	/**
	 * draw a quad with the image on it - accept float for rotation!
	 */
	public static void draw(float x, float y, String textureName, int width, int height, int srcX, int srcY, 
			int srcWidth, int srcHeight, float red, float green, float blue, float alpha, float angle) 
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
				Texture.drawTexture(x, y, temp, width, height, srcX, srcY, srcWidth, srcHeight, red, green, blue, alpha, angle);
			}			
		}
	}
	
	public static void draw(float x, float y, Texture texture, int width, int height, int srcX, int srcY, 
			int srcWidth, int srcHeight, float red, float green, float blue, float alpha, float angle)
	{
		// Bind the current texture to the current shader if one is in use.
		/*if(useShader)
		{
			setShaderUniform(currentShader, "texture", getTextureId(temp));
		}*/
		
        // enable alpha blending
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		
		texture.bind();								
		
		float fSrcX = ((float)srcX / texture.getWidth());
		float fSrcY = ((float)srcY / texture.getHeight());
		float fSrcWidth = (((float)srcX + (float)srcWidth) / texture.getWidth());
		float fSrcHeight = (((float)srcY + (float)srcHeight) / texture.getHeight());			
		
		GL11.glPushMatrix();				
						
		// Rotation works! 1/4/14
		if(angle != 0)
		{
			GL11.glTranslatef(x + (width / 2), y + (height / 2), 0); // move to the proper position
			GL11.glRotatef(angle, 0, 0, 1); // now rotate
			GL11.glTranslatef(-1 *(x+ (width / 2)), -1 * (y+(height  / 2)), 0);				
		}
		
		GL11.glColor4f(red, green, blue, alpha);
		GL11.glBegin(GL11.GL_QUADS);
			// Top Left
			GL11.glTexCoord2f(fSrcX, fSrcY);
			GL11.glVertex2f(x,y);
			// Top Right
			GL11.glTexCoord2f(fSrcWidth, fSrcY);
			GL11.glVertex2f(x + width, y);
			// Bottom Right
			GL11.glTexCoord2f(fSrcWidth,fSrcHeight);
			GL11.glVertex2f(x + width,y + height);
			// Bottom Left
			GL11.glTexCoord2f(fSrcX,fSrcHeight);
			GL11.glVertex2f(x,y + height);
		GL11.glEnd();
		
		GL11.glPopMatrix();
	}	
	
	public static boolean isSpriteOutOfGameBounds(Game game, Sprite sprite)
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
	
	private Texture associatedTexture;

	public Sprite(int destX, int destY, String textureName)
	{		
		this(destX, destY, textureName, 0, 0);
	}
	
	public Sprite(int destX, int destY, String textureName, int destSrcX, int destSrcY)
	{
		this(destX, destY, textureName, 0, 0, destSrcX, destSrcY);
	}
	
	public Sprite(int destX, int destY, String textureName, int width, int height, int destSrcX, int destSrcY)
	{
		super(destX, destY, width, height);		

		if(!(textureName.equals("")) && !(Texture.isTextureLoaded(textureName))) System.out.println("The texture '" + textureName + "' was not found.");
		
		associatedTexture = Texture.getTexture(textureName);
		srcX = destSrcX;
		srcY = destSrcY;
	}
	
	public Sprite(int destX, int destY, Texture texture)
	{
		this(destX, destY, texture.getName());
	}
	
	public final boolean areCornersOnTexture(TileMap tileMap, String textureName)
	{
		if(tileMap.getTextureAtPoint(this.getLeft(), this.getTop()).getName().equals(textureName)) return true;
		if(tileMap.getTextureAtPoint(this.getRight(), this.getTop()).getName().equals(textureName)) return true;
		if(tileMap.getTextureAtPoint(this.getLeft(), this.getBottom()).getName().equals(textureName)) return true;
		if(tileMap.getTextureAtPoint(this.getRight(), this.getBottom()).getName().equals(textureName)) return true;	
		
		return false;
	}
	
	public final boolean areCornersOnTexture(TileMap tileMap, Texture texture)
	{
		return this.areCornersOnTexture(tileMap, texture.getName());
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
	
	@Override
	public void draw()
	{
		draw(this.getWidth(), this.getHeight());
	}
	
	public void draw(int width, int height)
	{
		draw(width, height, this.getSrcX(), this.getSrcY(), width, height);
	}
	
	// Only use this for non-scaled sprites. Assumes the source dimensions are the same as the destination ones.
	public void draw(int width, int height, int srcX, int srcY)
	{
		draw(width, height, srcX, srcY, width, height);
	}
	
	public void draw(int width, int height, int srcX, int srcY, int srcWidth, int srcHeight)
	{	
		draw(width, height, srcX, srcY, srcWidth, srcHeight, 
				this.getRed(), this.getGreen(), this.getBlue(), this.getAlpha());
	}
	
	public void draw(int width, int height, int srcX, int srcY, int srcWidth, int srcHeight, 
			float red, float green, float blue, float alpha)
	{
		draw((int) this.getX(), (int) this.getY(), width, height, srcX, srcY, 
				srcWidth, srcHeight, red, green, blue, alpha);								
	}
	
	public void draw(int x, int y, int width, int height, int srcX, int srcY, int srcWidth, int srcHeight, 
			float red, float green, float blue, float alpha)
	{	
		draw((int) this.getX(), (int) this.getY(), width, height, srcX, srcY, 
				srcWidth, srcHeight, red, green, blue, alpha, this.getRotationAngle());
	}
	
	public void draw(int x, int y, int width, int height, int srcX, int srcY, int srcWidth, int srcHeight, 
			float red, float green, float blue, float alpha, float rotationAngle)
	{	
		if(!(this.visible)) return;
		
		String textureName = associatedTexture != null ? associatedTexture.getName() : null;
		
		Sprite.draw(x, y, textureName, width, height, srcX, srcY, srcWidth, srcHeight, 
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
		return associatedTexture;
	}
	
	public final String getTextureName()
	{
		return (associatedTexture != null ? associatedTexture.getName() : "");
	}
	
	public final boolean isCenterOnTexture(TileMap tileMap, String textureName)
	{
		return (tileMap.getTextureAtPoint(this.getCenterX(), this.getCenterY()).getName().equals(textureName));
	}
	public final boolean isCenterOnTexture(TileMap tileMap, Texture texture)
	{
		return this.isCenterOnTexture(tileMap, texture.getName());
	}
	public final boolean isOutOfGameBounds(Game game)
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
	
	public void move(float destX, float destY)
	{
		move(Math.round(destX), Math.round(destY));
	}	
	
	public void move(int destX, int destY)
	{
		this.setX(destX);
		this.setY(destY);
	}		

	public final float setRotationAngle(float rotationAngle)
	{
		return (this.rotationAngle = rotationAngle);
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
	
	public final boolean setVisible(boolean visible)
	{
		return (this.visible = visible);
	}
	
	public final boolean toggleVisibility()
	{
		return (this.visible = !(this.visible));
	}
	
}

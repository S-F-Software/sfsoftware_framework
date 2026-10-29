package com.sevensoupcans.sfsoftware.util.graphics;

import com.sevensoupcans.sfsoftware.game.Game;
import com.sevensoupcans.sfsoftware.util.graphics.geometry.Quad;
import com.sevensoupcans.sfsoftware.util.tile.TileMap;

public class Sprite extends Quad 
{
	
	public static void draw(final float x, final float y, final String textureName, final float width, 
			final float height, final int srcX, final int srcY, final float srcWidth, final float srcHeight)
	{
		draw(x,y,textureName,width,height,srcX,srcY,srcWidth,srcHeight, 1, 1, 1, 1);
	}
	
	public static void draw(final float x, final float y, final String textureName, final float width, 
			final float height, final int srcX, final int srcY, final float srcWidth, final float srcHeight, 
			final float red, final float green, final float blue, final float alpha) 
	{
		draw(x, y, textureName, width, height, srcX, srcY, srcWidth, srcHeight, 
				red, green, blue, alpha, 0, null);
	}
	
	/**
	 * draw a quad with the image on it - accept float for rotation!
	 */
	public static void draw(final float x, final float y, final String textureName, final float width, 
			final float height, final int srcX, final int srcY, final float srcWidth, final float srcHeight, 
			final float red, final float green, final float blue, final float alpha, final float angle, 
			final ColorPalette colorPalette) 
	{		
		// If the provided texture string is null or empty, don't try to draw it. :)
		if(textureName != null && !(textureName.equals("")))
		{			
			Texture texture = Texture.getTexture(textureName);
			if(texture == null)
			{
				// The requested Texture existed, but was null - draw a colored quad in its place.
				Quad.draw(x, y, width, height, red, green, blue, alpha);
				return;
			}

			draw(x, y, texture, width, height, srcX, srcY, srcWidth, srcHeight, red, green, blue, alpha, angle, colorPalette);
		}
	}
	
	public static void draw(final float x, final float y, final Texture texture, final float width, final float height, 
			final int srcX, final int srcY, final float srcWidth, final float srcHeight, final float red, final float green, 
			final float blue, final float alpha, final float angle, final ColorPalette colorPalette)
	{
		// Check if the provided palette matches the Texture's default color palette
		if(colorPalette == null || colorPalette.equals(texture.getColorPalette()))
		{
			// It does match - just draw the Texture as-is.
			Texture.drawTexture(x, y, texture, width, height, srcX, srcY, srcWidth, srcHeight, 
					red, green, blue, alpha, angle);
		}
		else
		{
			String textureName = texture.getName() + "_" + colorPalette.hashCode();
			Texture recolored = Texture.getTexture(textureName);
			if(recolored == null)
				recolored = new Texture(texture, textureName, texture.getColorPalette().getOverrides(colorPalette));

			Texture.drawTexture(x, y, recolored, width, height, srcX, srcY, srcWidth, srcHeight, 
					red, green, blue, alpha, angle);
		}
	}	
	
	public static String getTextureName(final Sprite sprite)
	{
		return sprite != null ? sprite.getTextureName() : "";
	}
	
	public static boolean isSpriteOutOfGameBounds(final Game game, final Sprite sprite)
	{		
		int x = (int) sprite.getX();
		int y = (int) sprite.getY();
		int width = (int) sprite.getWidth();
		int height = (int) sprite.getHeight();
		
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
	private Quad collisionBox;
	
	private ColorPalette colorPalette;

	public Sprite(String textureName, int srcX, int srcY)
	{
		this(0, 0, textureName, srcX, srcY);
	}
	
	public Sprite(float destX, float destY, String textureName)
	{		
		this(destX, destY, textureName, 0, 0);
	}
	
	public Sprite(float destX, float destY, String textureName, int srcX, int srcY)
	{
		this(destX, destY, textureName, 0, 0, srcX, srcY);
	}
	
	public Sprite(float destX, float destY, String textureName, float width, float height, int srcX, int srcY)
	{
		super(destX, destY, width, height);		
		
		if(Texture.isTextureLoaded(textureName))
		{
			this.associatedTexture = Texture.getTexture(textureName);
			this.colorPalette = new ColorPalette(this.associatedTexture.getColorPalette());
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
	
	@Override
	public void draw()
	{
		draw(this.getWidth(), this.getHeight());
	}
	
	public void draw(final float width, final float height)
	{
		draw(width, height, this.getSrcX(), this.getSrcY(), width, height);
	}
	
	// Only use this for non-scaled sprites. Assumes the source dimensions are the same as the destination ones.
	public void draw(final float width, final float height, final int srcX, final int srcY)
	{
		draw(width, height, srcX, srcY, width, height);
	}
	
	public void draw(final float width, final float height, final int srcX, final int srcY, 
			final float srcWidth, final float srcHeight)
	{	
		draw(width, height, srcX, srcY, srcWidth, srcHeight, 
				this.getRed(), this.getGreen(), this.getBlue(), this.getAlpha());
	}
	
	public void draw(final float width, final float height, final int srcX, final int srcY, 
			final float srcWidth, final float srcHeight, final float red, final float green, 
			final float blue, final float alpha)
	{
		draw((int) this.getX(), (int) this.getY(), width, height, srcX, srcY, 
				srcWidth, srcHeight, red, green, blue, alpha);								
	}
	
	public void draw(final float x, final float y, final float width, final float height, final int srcX, 
			final int srcY, final float srcWidth, final float srcHeight, final float red, final float green, 
			final float blue, final float alpha)
	{	
		draw(x, y, width, height, srcX, srcY, srcWidth, srcHeight, 
				red, green, blue, alpha, this.getRotationAngle());
	}
	
	public void draw(final float x, final float y, final float width, final float height, final int srcX, 
			final int srcY, final float srcWidth, final float srcHeight, final float red, final float green, 
			final float blue, final float alpha, final float rotationAngle)
	{	
		if(!(this.visible)) return;
		
		String textureName = this.getTextureName();
		if(textureName != null && !(textureName.equals("")))
		{			
			Texture temp = Texture.getTexture(textureName);
			if(temp == null)
			{
				// The requested Texture existed, but was null - draw a colored quad in its place.
				Quad.draw(x, y, width, height, red, green, blue, alpha);
			}
			else
			{
				Sprite.draw(x, y, temp, width, height, srcX, srcY, srcWidth, srcHeight, 
						red, green, blue, alpha, rotationAngle, colorPalette);
			}			
		}
	}
	
	public final float getRotationAngle()
	{
		return this.rotationAngle;
	}
	
	@Override
	public Quad getCollisionBox()
	{
		return collisionBox != null ?
				new Quad(collisionBox.getX() + this.getX(), 
						collisionBox.getY() + this.getY(), 
						collisionBox.getWidth(), 
						collisionBox.getHeight()) : 
				new Quad(this.getX(),
						this.getY(),
						this.getWidth(),
						this.getHeight());
	}
	
	public final ColorPalette getColorPalette()
	{
		return colorPalette;
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

	public final void renderAsFrameBufferOverlay(final FrameBuffer targetFrameBuffer, final float x, final float y, 
			final float width, final float height)
	{
		this.getTexture().renderAsFrameBufferOverlay(targetFrameBuffer, x, y, width, height, 
				this.getSrcX(), this.getSrcY(), this.getWidth(), this.getHeight(), 
				this.getRed(), this.getGreen(), this.getBlue(), this.getAlpha());
	}
	
	/**
	 * Resets the Sprite's color palette to the default texture color palette.
	 */
	public final void resetColorPaletteFromAssociatedTexture()
	{
		if(associatedTexture == null) return;
		
		// Manually destroy any palette-swap Textures created; a bit nasty, but necessary.
		String textureName = associatedTexture.getName() + "_" + colorPalette.hashCode();
		if(Texture.getTexture(textureName) != null)
			Texture.getTexture(textureName).destroy();		
		
		colorPalette = new ColorPalette(associatedTexture.getColorPalette());
	}
	
	/**
	 * Collision box x, y, width, and height values are relative to the over-arching
	 * Quad object rather than reflective of real world positioning.
	 * 
	 * @param collisionBox
	 */
	@Override
	public final void setCollisionBox(Quad collisionBox)
	{
		this.collisionBox = collisionBox;
	}
	
	/**
	 * Collision box x, y, width, and height values are relative to the over-arching
	 * Quad object rather than reflective of real world positioning.
	 */
	@Override
	public void setCollisionBox(float x, float y, float width, float height)
	{
		this.collisionBox = new Quad(x, y, width, height);	
	}
	
	public final void setPosition(final float destX, final float destY)
	{
		this.setX(destX);
		this.setY(destY);
	}	
	
	@Deprecated
	public void setPosition(final int destX, final int destY)
	{
		setPosition((float) destX, (float) destY);
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

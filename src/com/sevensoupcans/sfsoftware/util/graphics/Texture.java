package com.sevensoupcans.sfsoftware.util.graphics;

import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.GL_NEAREST;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameteri;

import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import javax.imageio.ImageIO;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;

import com.sevensoupcans.sfsoftware.util.BufferUtils;

public final class Texture 
{
	private static final RGBA DEFAULT_TRANSPARENT_COLOR = new RGBA(1.0f, 0.0f, 1.0f);
	private static HashMap <String, Texture> loadedTextures = new HashMap<String, Texture>();

	private final BufferedImage BUFFERED_IMAGE;
	private final int HEIGHT;
	private final int[] PIXEL_DATA;
	private final int[] INT_BUFFER_SRC;
	private final String ORIGIN_PATH;
	private final String TEXTURE_NAME;
	private final int TEXTURE_ID;
	private final int WIDTH;
	
	private final RGBA TRANSPARENT_COLOR;
	private final RGBA MASK_COLOR;
	
	private final HashSet<RGBA> colorPalette = new HashSet<RGBA>();
	
	/**
	 * Draw a quad with the image on it - accept float for rotation!
	 */
	public static void drawTexture(final float x, final float y, final Texture texture, final float width2, 
			final float height2, final int srcX, final int srcY, final float srcWidth, final float srcHeight, 
			final float red, final float green, final float blue, final float alpha, final float angle) 
	{		
		// If the provided texture string is null or empty, don't try to draw it. :)
		if(texture != null)
		{
	        // Enable alpha blending
	        GL11.glEnable(GL11.GL_BLEND);
	        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			
			texture.bind();
			
			float fSrcX = ((float)srcX / texture.getWidth());
			float fSrcY = ((float)srcY / texture.getHeight());
			float fSrcWidth = ((srcX + srcWidth) / texture.getWidth());
			float fSrcHeight = ((srcY + srcHeight) / texture.getHeight());			
			
			GL11.glPushMatrix();			
							
			// Rotation works! 1/4/14
			if(angle != 0)
			{
				GL11.glTranslatef(x + (width2 / 2), y + (height2 / 2), 0); // move to the proper position
				GL11.glRotatef(angle, 0, 0, 1); // now rotate
				GL11.glTranslatef(-1 *(x+ (width2 / 2)), -1 * (y+(height2  / 2)), 0);				
			}
			
			GL11.glColor4f(red, green, blue, alpha);
			GL11.glBegin(GL11.GL_QUADS);
				// Top Left
				GL11.glTexCoord2f(fSrcX, fSrcY);
				GL11.glVertex2f(x,y);
				// Top Right
				GL11.glTexCoord2f(fSrcWidth, fSrcY);
				GL11.glVertex2f(x + width2, y);
				// Bottom Right
				GL11.glTexCoord2f(fSrcWidth,fSrcHeight);
				GL11.glVertex2f(x + width2,y + height2);
				// Bottom Left
				GL11.glTexCoord2f(fSrcX,fSrcHeight);
				GL11.glVertex2f(x,y + height2);
			GL11.glEnd();
			
			GL11.glPopMatrix();
		}			
	}	
	
	public static RGBA getDefaultTransparentColor()
	{
		return DEFAULT_TRANSPARENT_COLOR;
	}
	
    public static boolean isTextureLoaded(final String textureName)
    {
    	return loadedTextures.containsKey(textureName);
    }
	
	public static Map<String, Texture> getLoadedTextures()
	{
		return loadedTextures;
	}
	
	public static Texture getTexture(final String textureName)
	{
		return loadedTextures.get(textureName);
	}
	
	public Texture(String path) throws FileNotFoundException, IOException
	{
		this(path, null, null);		
	}
	
	public Texture(String path, RGBA transparentColor) throws FileNotFoundException, IOException
	{
		this(path, transparentColor, null);
	}	
	
	public Texture(String path, RGBA transparentColor, RGBA maskColor) throws FileNotFoundException, IOException
	{	
		this(ImageIO.read(new FileInputStream(path)), transparentColor,	maskColor, path);
	}
	
	public Texture(BufferedImage image)
	{
		this(image, null, null, null);
	}
	
	public Texture(BufferedImage image, String path)
	{
		this(image, null, null, path);
	}	
	
	public Texture(BufferedImage image, RGBA transparentColor, RGBA maskColor, String path)
	{
		ORIGIN_PATH = path;
		TEXTURE_NAME = generateTextureName(path, (maskColor != null));		
		TRANSPARENT_COLOR = transparentColor;
		MASK_COLOR = maskColor;
		
		BUFFERED_IMAGE = image;
		
		WIDTH = image.getWidth();
		HEIGHT = image.getHeight();
		
		PIXEL_DATA = new int[WIDTH * HEIGHT];
		image.getRGB(0, 0, WIDTH, HEIGHT, PIXEL_DATA, 0, WIDTH);
		
		INT_BUFFER_SRC = new int[WIDTH * HEIGHT];
		for (int i = 0; i < (WIDTH * HEIGHT) ; i++ )
		{
			int a = (PIXEL_DATA[i] & 0xff000000) >> 24;
			int r = (PIXEL_DATA[i] & 0xff0000) >> 16;
			int g = (PIXEL_DATA[i] & 0xff00) >> 8;
			int b = (PIXEL_DATA[i] & 0xff);
			
			// If a "transparent color" is being used, we check the current pixel to determine if it matches and set alpha to 0.
			if(TRANSPARENT_COLOR != null && r == TRANSPARENT_COLOR.getRedInt() && g == TRANSPARENT_COLOR.getGreenInt() && b == TRANSPARENT_COLOR.getBlueInt())
			{
				a = 0;				
			}
			else if(MASK_COLOR != null)
			{
				r = MASK_COLOR.getRedInt();
				g = MASK_COLOR.getGreenInt();
				b = MASK_COLOR.getBlueInt();
			}			
			
			INT_BUFFER_SRC[i] = a << 24 | b << 16 | g << 8 | r;
			
			// Add the current R, G, B value to the texture's color palette HashSet
			colorPalette.add(new RGBA(r, g, b));			
		}
		
		TEXTURE_ID = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, TEXTURE_ID);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);//GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);		
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, WIDTH, HEIGHT, 0, GL_RGBA, GL_UNSIGNED_BYTE, BufferUtils.createIntBuffer(INT_BUFFER_SRC));
		glBindTexture(GL_TEXTURE_2D, 0);
		
		loadedTextures.put(TEXTURE_NAME, this);
	}

	public void bind()
	{
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, getID());
	}
	
	public boolean containsColor(final RGBA rgba)
	{
		// Alpha is not stored in the color palette - a color value where alpha is 1.0f should be provided.
		if(rgba.getAlpha() < 1.0f) return false;		
		return (colorPalette.contains(rgba));
	}
	
	public Texture destroy()
	{
		return loadedTextures.remove(this.getName());
	}
	
	public void draw(final float x, final float y, final int width, final int height, final int srcX, final int srcY, 
			final int srcWidth, final int srcHeight, final float red, final float green, final float blue, 
			final float alpha, final float angle)
	{
		drawTexture(x, y, this, width, height, srcX, srcY, srcWidth, srcHeight, 
				red, green, blue, alpha, angle);
	}
	
	private String generateTextureName(final String path, final boolean prependUnderscore)
	{
		if(path == null) return String.valueOf(this.hashCode());
		
		String fileName = path.substring(path.lastIndexOf('/') + 1);
		String textureName = fileName.substring(0, fileName.lastIndexOf(".")).trim();
		
		if(prependUnderscore)
			textureName = "_" + textureName;
				
		if(isTextureLoaded(textureName))
		{
			int i = 1;
			String s = textureName;
			
			/* If a texture with the specified name is already loaded, append a number until 
			   we find one that isn't. */	
			while(isTextureLoaded(s))
			{
				s = textureName + i;
				i++;
			}
			textureName = s;
		}			
		
		return textureName;			
	}
	
	public final BufferedImage getBufferedImage()
	{
		return BUFFERED_IMAGE;
	}
	
	public int getColorCount()
	{
		return colorPalette.size();
	}
	
	public RGBA[] getColorPalette()
	{
		return colorPalette.toArray(new RGBA[colorPalette.size()]);
	}
	
	public int getHeight() 
	{
		return HEIGHT;
	}
	
	public String getName()
	{
		return TEXTURE_NAME;
	}
	
	public String getOriginalFilename()
	{
		return ORIGIN_PATH.substring(ORIGIN_PATH.lastIndexOf('/') + 1);
	}
	
	public String getOriginalFilePath()
	{
		return ORIGIN_PATH.substring(0, ORIGIN_PATH.lastIndexOf('/') + 1);
	}
	
	public int getWidth() 
	{
		return WIDTH;
	}	
	
	public int getID() 
	{
		return TEXTURE_ID;
	}
	
	public final void renderAsFrameBufferOverlay(final FrameBuffer targetFrameBuffer, final float x, final float y, 
			final float width, final float height, final int srcX, final int srcY, final float srcWidth, 
			final float srcHeight, final float red, final float green, final float blue, final float alpha)
	{
		// Capture the current display buffer so we can revert back to it after rendering the lightmap.
		FrameBuffer fbo = Graphics.getCurrentDisplayBuffer();						
		// Switch to the target frame buffer
		Graphics.setBuffer(targetFrameBuffer);
		
		GL11.glPushMatrix();		
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		GL11.glOrtho(0, Display.getWidth(), Display.getHeight(), 0, 1, -1);
		GL11.glViewport(0, 0, Display.getWidth(), Display.getHeight());
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glLoadIdentity();				
		
		GL11.glPushAttrib(GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_ENABLE_BIT | GL11.GL_COLOR_BUFFER_BIT);
		GL14.glBlendFuncSeparate(GL11.GL_SRC_COLOR, GL11.GL_DST_COLOR, GL11.GL_ONE, GL11.GL_DST_COLOR);
		
		GL11.glEnable(GL11.GL_TEXTURE_2D);		
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.getID());
		
		float fSrcX = (srcX / srcWidth);
		float fSrcY = (srcY / srcHeight);
		float fSrcWidth = ((srcX + srcWidth) / srcWidth);
		float fSrcHeight = ((srcY + srcHeight) / srcHeight);			
		
		GL11.glPushMatrix();		
		
		// Need to reset the color or else things get weird...
		GL11.glColor4f(red, green, blue, alpha);
		GL11.glBegin(GL11.GL_QUADS);					
			// Top Left
			//GL11.glTexCoord2f(0.0f, 1.0f); 
			GL11.glTexCoord2f(fSrcX, 1 - fSrcY);
			GL11.glVertex2f(x, y);  		
			// Top Right
			//GL11.glTexCoord2f(1.0f, 1.0f); 
			GL11.glTexCoord2f(fSrcWidth, 1 - fSrcY);
			GL11.glVertex2f(x + width,  y); 		
			// Bottom Right
			//GL11.glTexCoord2f(1.0f, 0.0f);
			GL11.glTexCoord2f(fSrcWidth,1 - fSrcHeight);
			GL11.glVertex2f(x + width, y + height);		
			// Bottom Left		
			//GL11.glTexCoord2f(0.0f, 0.0f); 		
			GL11.glTexCoord2f(fSrcX, 1 - fSrcHeight);
			GL11.glVertex2f(x, y + height);	
		GL11.glEnd();
		      
		GL11.glPopMatrix();	
		GL11.glPopAttrib();		
		
		// Set some things back to the way they were.
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);		
		Graphics.setBuffer(fbo);		
	}
	
	public void swapColors(final RGBA[] colorsToReplace, final RGBA[] replacementColors)
	{
		if(colorsToReplace.length != replacementColors.length) return;
		
		// Clear the color palette completely - easiest solution is just to re-add as we scan through
		colorPalette.clear();
		
		for (int i = 0; i < (WIDTH * HEIGHT) ; i++ )
		{
			int a = (PIXEL_DATA[i] & 0xff000000) >> 24;
			int r = (PIXEL_DATA[i] & 0xff0000) >> 16;
			int g = (PIXEL_DATA[i] & 0xff00) >> 8;
			int b = (PIXEL_DATA[i] & 0xff);

			// If a "transparent color" is being used, we check the current pixel to determine if it matches and set alpha to 0.
			if(TRANSPARENT_COLOR != null && r == TRANSPARENT_COLOR.getRedInt() && g == TRANSPARENT_COLOR.getGreenInt() && b == TRANSPARENT_COLOR.getBlueInt())
			{
				a = 0;				
			}
			else if(MASK_COLOR != null)
			{
				r = MASK_COLOR.getRedInt();
				g = MASK_COLOR.getGreenInt();
				b = MASK_COLOR.getBlueInt();
			}
			
			for(int j = 0; j < colorsToReplace.length; j++)
			{				
				if(r == colorsToReplace[j].getRedInt() && 
						g == colorsToReplace[j].getGreenInt() && 
						b == colorsToReplace[j].getBlueInt())
				{
					r = replacementColors[j].getRedInt();
					g = replacementColors[j].getGreenInt();
					b = replacementColors[j].getBlueInt();
					a = replacementColors[j].getAlphaInt();
				}
			}
			
			INT_BUFFER_SRC[i] = a << 24 | b << 16 | g << 8 | r;
			
			// Add the current R, G, B value to the texture's color palette HashSet
			colorPalette.add(new RGBA(r, g, b));			
		}
				
		glBindTexture(GL_TEXTURE_2D, TEXTURE_ID);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);		
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, WIDTH, HEIGHT, 0, GL_RGBA, GL_UNSIGNED_BYTE, BufferUtils.createIntBuffer(INT_BUFFER_SRC));
		glBindTexture(GL_TEXTURE_2D, 0);		
	}
	
	public void swapColor(final RGBA colorToReplace, final RGBA replacementColor)
	{
		RGBA[] colorsToReplace = { colorToReplace };
		RGBA[] replacementColors = { replacementColor };
		
		swapColors(colorsToReplace, replacementColors);
	}
}

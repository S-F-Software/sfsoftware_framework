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

import javax.imageio.ImageIO;

import org.lwjgl.opengl.GL11;

import com.sevensoupcans.sfsoftware.util.BufferUtils;

public class Texture 
{
	private static final RGBA DEFAULT_TRANSPARENT_COLOR = new RGBA(1.0f, 0.0f, 1.0f);
	private static HashMap <String, Texture> loadedTextures = new HashMap<String, Texture>();
	
	private final int HEIGHT;
	private final String TEXTURE_NAME;
	private final int TEXTURE_ID;
	private final int WIDTH;
	
	private final RGBA TRANSPARENT_COLOR;
	private final RGBA MASK_COLOR;
	
	private final HashSet<RGBA> colorPalette = new HashSet<RGBA>();
	
	public static RGBA getDefaultTransparentColor()
	{
		return DEFAULT_TRANSPARENT_COLOR;
	}
	
    public static boolean isTextureLoaded(String textureName)
    {
    	return loadedTextures.containsKey(textureName);
    }
	
	public static HashMap<String, Texture> getLoadedTextures()
	{
		return loadedTextures;
	}
	
	public static Texture getTexture(String textureName)
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
		TEXTURE_NAME = generateTextureName(path, (maskColor != null));
		
		TRANSPARENT_COLOR = transparentColor;
		MASK_COLOR = maskColor;
		
		WIDTH = image.getWidth();
		HEIGHT = image.getHeight();
		
		int[] pixels = new int[WIDTH * HEIGHT];
		image.getRGB(0, 0, WIDTH, HEIGHT, pixels, 0, WIDTH);
		
		int[] data = new int[WIDTH * HEIGHT];
		for (int i = 0; i < (WIDTH * HEIGHT) ; i++ )
		{
			int a = (pixels[i] & 0xff000000) >> 24;
			int r = (pixels[i] & 0xff0000) >> 16;
			int g = (pixels[i] & 0xff00) >> 8;
			int b = (pixels[i] & 0xff);
			
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
				//a = 255;
			}			
			
			data[i] = a << 24 | b << 16 | g << 8 | r;
			
			// Add the current R, G, B value to the texture's color palette HashSet
			colorPalette.add(new RGBA(r, g, b));			
		}
		
		TEXTURE_ID = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, TEXTURE_ID);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);//GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);		
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, WIDTH, HEIGHT, 0, GL_RGBA, GL_UNSIGNED_BYTE, BufferUtils.createIntBuffer(data));
		glBindTexture(GL_TEXTURE_2D, 0);
		
		loadedTextures.put(TEXTURE_NAME, this);
	}

	public void bind()
	{
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, getID());
	}
	
	public boolean containsColor(RGBA rgba)
	{
		// Alpha is not stored in the color palette - a color value where alpha is 1.0f should be provided.
		if(rgba.getAlpha() < 1.0f) return false;		
		return (colorPalette.contains(rgba));
	}
	
	public Texture destroy()
	{
		return loadedTextures.remove(this.getName());
	}	
	
	private String generateTextureName(String path, boolean prependUnderscore)
	{
		if(path == null)
			return "";
		
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
	
	public int getWidth() 
	{
		return WIDTH;
	}	
	
	public int getID() 
	{
		return TEXTURE_ID;
	}	
}

package com.sevensoupcans.sfsoftware.util.graphics;

import static org.lwjgl.opengl.GL11.*;

import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.lwjgl.opengl.GL11;

import com.sevensoupcans.sfsoftware.util.BufferUtils;

public class Texture 
{
	private final int WIDTH;
	private final int HEIGHT;
	private final int TEXTURE_ID;
	
	private final RGBA TRANSPARENT_COLOR;
	private final RGBA MASK_COLOR;
	
	public static final RGBA DEFAULT_TRANSPARENT_COLOR = new RGBA(1.0f, 0.0f, 1.0f);
	
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
		this(ImageIO.read(new FileInputStream(path)), transparentColor, maskColor);
	}
	
	public Texture(BufferedImage image)
	{
		this(image, null, null);
	}
	
	public Texture(BufferedImage image, RGBA transparentColor, RGBA maskColor)
	{
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
		}
		
		TEXTURE_ID = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, TEXTURE_ID);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);//GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);		
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, WIDTH, HEIGHT, 0, GL_RGBA, GL_UNSIGNED_BYTE, BufferUtils.createIntBuffer(data));
		glBindTexture(GL_TEXTURE_2D, 0);
	}

	public void bind()
	{
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, getID());
	}
	
	public int getHeight() 
	{
		return HEIGHT;
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

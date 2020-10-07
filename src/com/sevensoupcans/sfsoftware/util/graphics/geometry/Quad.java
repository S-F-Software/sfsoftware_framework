package com.sevensoupcans.sfsoftware.util.graphics.geometry;

import org.lwjgl.opengl.GL11;

import com.sevensoupcans.sfsoftware.util.graphics.RGBA;

public class Quad {

	public static void draw(float x, float y, float width, float height, RGBA rgba)
	{	
		draw(x, y, width, height, rgba.getRed(), rgba.getGreen(), rgba.getBlue(), rgba.getAlpha());
	}
	
	public static void draw(float x, float y, float width, float height, float r, float g, float b, float a)
	{	
		RGBA rgba = new RGBA(r, g, b, a);
		draw(x, y, width, height, rgba, rgba, rgba, rgba);
		
		// set the color of the quad (R,G,B,A)
		/*GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glColor4f(r,g,b,a);*/		
	    
	    // draw quad
	    /*GL11.glBegin(GL11.GL_QUADS);	    
	    GL11.glVertex2f(x,y);
		GL11.glVertex2f(x+width,y);
		GL11.glVertex2f(x+width,y+height);
		GL11.glVertex2f(x,y+height);	    
		GL11.glEnd();
		
		GL11.glEnable(GL11.GL_TEXTURE_2D);*/
	}
	
	public static void draw(float x, float y, float width, float height, RGBA topLeft, RGBA topRight, RGBA bottomLeft, RGBA bottomRight)
	{	
		// set the color of the quad (R,G,B,A)
		GL11.glDisable(GL11.GL_TEXTURE_2D);		
	    
	    // draw quad
	    GL11.glBegin(GL11.GL_QUADS);
	    
		GL11.glColor4f(topLeft.getRed(), topLeft.getGreen(), topLeft.getBlue(), topLeft.getAlpha());
	    GL11.glVertex2f(x,y);
		GL11.glColor4f(topRight.getRed(), topRight.getGreen(), topRight.getBlue(), topRight.getAlpha());
		GL11.glVertex2f(x+width,y);
		GL11.glColor4f(bottomRight.getRed(), bottomRight.getGreen(), bottomRight.getBlue(), bottomRight.getAlpha());
		GL11.glVertex2f(x+width,y+height);
		GL11.glColor4f(bottomLeft.getRed(), bottomLeft.getGreen(), bottomLeft.getBlue(), bottomLeft.getAlpha());
		GL11.glVertex2f(x,y+height);
	    
		GL11.glEnd();
		
		GL11.glEnable(GL11.GL_TEXTURE_2D);
	}		
	
	private float red = 1.0f;
	private float green = 1.0f;
	private float blue = 1.0f;
	private float alpha = 1.0f;
	
	private int height;	
	private int width;
	private int x;	
	private int y;	
	
	public Quad(int x, int y, int width, int height) 
	{
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
	
	public void draw()
	{
		Quad.draw(this.getX(), this.getY(), this.getWidth(), this.getHeight(), 
				this.getRed(), this.getGreen(), this.getBlue(), this.getAlpha());
	}
	
	public final float getAlpha()
	{
		return this.alpha;
	}
	
	public final float getBlue()
	{
		return this.blue;
	}
	
	public final int getBottom()
	{
		return (y + height);
	}

	public final int getCenterX()
	{
		return (x + (width / 2));
	}
	
	public final int getCenterY()
	{
		return (y + (height / 2));
	}
	
	public final float getGreen()
	{
		return this.green;
	}
	
	public final int getHeight()
	{
		return height;
	}
	
	public final int getLeft()
	{
		return x;
	}
	
	public final float getRed()
	{
		return this.red;
	}
	
	public final int getRight()
	{
		return (x + width);
	}	
	
	public final int getTop()
	{
		return y;
	}
	
	public final int getWidth()
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
	
	public final float setAlpha(float alpha)
	{
		return (this.alpha = alpha);
	}
	
	public final float setBlue(float blue)
	{
		return (this.blue = blue);
	}
	
	public final float setGreen(float green)
	{
		return (this.green = green);
	}	
	
	public final void setHeight(int newHeight)
	{
		height = newHeight;
	}
	
	public final float setRed(float red)
	{
		return (this.red = red);
	}
	
	public final void setWidth(int newWidth)
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

package com.sevensoupcans.sfsoftware.util.graphics.geometry;

import org.lwjgl.opengl.GL11;

import com.sevensoupcans.sfsoftware.util.graphics.Graphics;
import com.sevensoupcans.sfsoftware.util.graphics.RGBA;

public class Quad implements Collidable, Intersectable {

	public static void draw(final float x, final float y, final float width, final float height, 
			final RGBA rgba)
	{	
		draw(x, y, width, height, rgba.getRed(), rgba.getGreen(), rgba.getBlue(), rgba.getAlpha());
	}
	
	public static void draw(final float x, final float y, final float width, final float height, 
			final float r, final float g, final float b, final float a)
	{	
		RGBA rgba = new RGBA(r, g, b, a);
		draw(x, y, width, height, rgba, rgba, rgba, rgba);
	}
	
	public static void draw(final float x, final float y, final float width, final float height, 
			final RGBA topLeft, final RGBA topRight, final RGBA bottomLeft, final RGBA bottomRight)
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
	
	private float height;	
	private float width;
	private float x;	
	private float y;
	
	public Quad(float x, float y, float width, float height) 
	{
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	@Override
	public boolean collisionResult(final Collidable object) 
	{
		// TODO Auto-generated method stub
		return false;
	}	
	
	public void draw()
	{
		Quad.draw(this.getX(), this.getY(), this.getWidth(), this.getHeight(), 
				this.getRed(), this.getGreen(), this.getBlue(), this.getAlpha());
	}
	
	public final void drawOutline()
	{
		this.drawOutline(2);
	}
	
	public final void drawOutline(int thickness)
	{
		Graphics.drawRect(this.getX(), this.getY(), this.getWidth(), this.getHeight(), 
				this.getRed(), this.getGreen(), this.getBlue(), this.getAlpha(), thickness);
	}
	
	public final float getAlpha()
	{
		return this.alpha;
	}
	
	public final float getBlue()
	{
		return this.blue;
	}
	
	public final float getBottom()
	{
		return (y + height);
	}

	public final float getCenterX()
	{
		return (x + (width / 2.0f));
	}
	
	public final float getCenterY()
	{
		return (y + (height / 2.0f));
	}
	
	public final float getGreen()
	{
		return this.green;
	}
	
	@Override
	public final float getHeight()
	{
		return height;
	}
	
	public final float getLeft()
	{
		return x;
	}
	
	public final float getRed()
	{
		return this.red;
	}
	
	public final float getRight()
	{
		return (x + width);
	}	
	
	public final float getTop()
	{
		return y;
	}
	
	@Override
	public final float getWidth()
	{
		return width;
	}
	
	@Override
	public float getX()
	{
		return x;
	}
	
	@Override
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
	
	@Override
	public void setCollisionBox(Quad collisionBox) 
	{
		// TODO Auto-generated method stub	
	}

	@Override
	public void setCollisionBox(float x, float y, float width, float height)
	{
		// TODO Auto-generated method stub	
	}
	
	public final float setGreen(float green)
	{
		return (this.green = green);
	}	
	
	public final void setHeight(float newHeight)
	{
		height = newHeight;
	}
	
	public final float setRed(float red)
	{
		return (this.red = red);
	}
	
	public final void setSize(float newWidth, float newHeight)
	{
		this.setWidth(newWidth);
		this.setHeight(newHeight);
	}	
	
	public final void setWidth(float newWidth)
	{
		width = newWidth;
	}
	
	public void setX(float destX)
	{
		x = destX;
	}
	
	public void setY(float destY)
	{
		y = destY;
	}
	
	@Override
	public String toString()
	{
		return "Quad{" + 
				"x=" + this.getX() +
				", y=" + this.getY() +
				", width=" + this.getWidth() +
				", height=" + this.getHeight() +
				'}';
	}
}
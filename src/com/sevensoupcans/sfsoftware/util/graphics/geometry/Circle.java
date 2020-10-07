package com.sevensoupcans.sfsoftware.util.graphics.geometry;

import org.lwjgl.opengl.GL11;

import com.sevensoupcans.sfsoftware.util.graphics.RGBA;

public class Circle {
	
	public static void draw(float x, float y, float radius, float r, float g, float b, float a)
	{
		RGBA rgba = new RGBA(r, g, b, a);
		draw(x, y, radius, rgba, rgba, 100);
	}	
	
	public static void draw(float x, float y, float radius, float r1, float g1, float b1, float a1, float r2, float g2, float b2, float a2)
	{
		RGBA inner = new RGBA(r1, g1, b1, a1);
		RGBA outer = new RGBA(r2, g2, b2, a2);
		draw(x, y, radius, inner, outer, 100);
	}
	
	public static void draw(float x, float y, float radius, RGBA innerRGBA, RGBA outerRGBA, int segments)
	{		
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glColor4f(innerRGBA.getRed(), innerRGBA.getGreen(), innerRGBA.getBlue(), innerRGBA.getAlpha());	

		GL11.glBegin(GL11.GL_TRIANGLE_FAN);
	    GL11.glVertex2f(x, y); // Center of the circle
	    
	    GL11.glColor4f(outerRGBA.getRed(), outerRGBA.getGreen(), outerRGBA.getBlue(), outerRGBA.getAlpha());
	    
	    for (int i = 0; i <= segments; i++) // Last vertex same as first vertex 
	    {
	         double angle = i * 2.0 * Math.PI / segments;  // 360 deg for all segments
	         GL11.glVertex2f(x + (float)Math.cos(angle) * radius, y + (float) Math.sin(angle) * radius);
	    }
		
		GL11.glEnd();		
		GL11.glEnable(GL11.GL_TEXTURE_2D);
	}

	public static void drawOutline(float x, float y, float radius, float r, float g, float b, float a)
	{
		drawOutline(x, y, radius, r, g, b, a, 100, true);
	}	
	
	public static void drawOutline(float x, float y, float radius, float r, float g, float b, float a, int segments, boolean smooth)
	{
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glColor4f(r,g,b,a);	

		GL11.glBegin(GL11.GL_LINE_LOOP);
		
		if(smooth)
		{
			//GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			//GL11.glEnable(GL11.GL_LINE_SMOOTH);
		}
	   		
		for (int i = 0; i <= segments; i++) // Last vertex same as first vertex 
    	{
			double angle = i * 2.0 * Math.PI / segments;  // 360 deg for all segments
			GL11.glVertex2f(x + (float)Math.cos(angle) * radius, y + (float) Math.sin(angle) * radius);
    	}
	
	    if(smooth)
	    {
	    	//GL11.glDisable(GL11.GL_LINE_SMOOTH);
	    }
	    		
		GL11.glEnd();						
		GL11.glEnable(GL11.GL_TEXTURE_2D);
	}	
	
	private int x;
	private int y;
	private int radius;
	
	private float red = 1.0f;
	private float green = 1.0f;
	private float blue = 1.0f;
	private float alpha = 1.0f;
	
	public Circle(int centerX, int centerY, int radius, float red, float green, float blue, float alpha) 
	{
		this.x = centerX;
		this.y = centerY;
		this.radius = radius;
		this.red = red;
		this.green = green;
		this.blue = blue;
		this.alpha = alpha;
	}
	
	public void draw()
	{
		Circle.draw(this.x, this.y, this.radius, this.red, this.green, this.blue, this.alpha);
	}
	
	public void drawAsOutline()
	{
		Circle.drawOutline(this.x, this.y, this.radius, this.red, this.green, this.blue, this.alpha);
	}
	
	public int[] getXCoordinatesOnPath(int segments)
	{
		int[] xCoordinates = new int[segments];
		
	    for (int i = 0; i <= segments; i++) 
	    {
	    	double angle = i * (2.0 * Math.PI) / segments;
	    	xCoordinates[i] = this.getXOnPath(angle);
	    }
	    
	    return xCoordinates;
	}
	
	public int getXOnPath(double angle)
	{
		return (int) ((Math.cos(angle) * -1) * this.radius);
	}

	public int[] getYCoordinatesOnPath(int segments)
	{
		int[] yCoordinates = new int[segments];
		
	    for (int i = 0; i <= segments; i++) 
	    {
	    	double angle = i * (2.0 * Math.PI) / segments;
	    	yCoordinates[i] = this.getYOnPath(angle);
	    }
	    
	    return yCoordinates;
	}	
	
	public int getYOnPath(double angle)
	{
		return (int) ((Math.sin(angle)) * this.radius);		
	}

}

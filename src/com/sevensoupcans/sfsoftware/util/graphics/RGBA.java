package com.sevensoupcans.sfsoftware.util.graphics;

import org.lwjgl.opengl.GL11;

public class RGBA {
	private float red = 1;
	private float green = 1;
	private float blue = 1;
	private float alpha = 1;
	
	public RGBA(int r, int g, int b)
	{
		red = ((float) r / 255);
		green = ((float) g / 255);
		blue = ((float) b / 255);
	}

	public RGBA(int r, int g, int b, float a)
	{
		red = ((float) r / 255);
		green = ((float) g / 255);
		blue = ((float) b / 255);
		alpha = a;		
	}	
	
	public RGBA(float r, float g, float b) 
	{
		red = r;
		green = g;
		blue = b;		
	}
	
	public RGBA(float r, float g, float b, float a) 
	{
		red = r;
		green = g;
		blue = b;
		alpha = a;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if(obj instanceof RGBA)
		{
			RGBA rgba = (RGBA) obj;
			if(this.getRed() == rgba.getRed() && this.getGreen() == rgba.getGreen() && this.getBlue() == rgba.getBlue() && this.getAlpha() == rgba.getAlpha())
			{
				return true;
			}
			else
			{
				return false;
			}
		}
		/*else if(obj instanceof Color)
		{
			Color c = (Color) obj;
			if(this.getRed() == c.r && this.getGreen() == c.g && this.getBlue() == c.b && this.getAlpha() == c.a)
			{
				return true;
			}
			else
			{
				return false;
			}			
		}*/
		
		return false;
	}
	
	public int getRedInt()
	{		
		return (int)(red * 255);
	}
	
	public int getGreenInt()
	{		
		return (int)(green * 255);
	}
	
	public int getBlueInt()
	{		
		return (int)(blue * 255);
	}
	
	public int getAlphaInt()
	{
		return (int)(alpha * 255);
	}
	
	public float getRed()
	{
		return red;
	}
	
	public float getGreen()
	{
		return green;
	}
	
	public float getBlue()
	{
		return blue;
	}
	
	public float getAlpha()
	{
		return alpha;
	}	

	public void bind()
	{
		GL11.glColor4f(red, green, blue, alpha);		
	}
	
}

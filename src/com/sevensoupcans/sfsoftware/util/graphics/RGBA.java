package com.sevensoupcans.sfsoftware.util.graphics;

import java.awt.Color;

import org.lwjgl.opengl.GL11;

public class RGBA {
	
	public static final RGBA RED = new RGBA(1.0f, 0.0f, 0.0f, 1.0f);
	public static final RGBA GREEN = new RGBA(0.0f, 1.0f, 0.0f, 1.0f);
	public static final RGBA BLUE = new RGBA(0.0f, 0.0f, 1.0f, 1.0f);
	public static final RGBA CYAN = new RGBA(0.0f, 1.0f, 1.0f, 1.0f);
	public static final RGBA YELLOW = new RGBA(1.0f, 1.0f, 0.0f, 1.0f);
	public static final RGBA WHITE = new RGBA(1.0f, 1.0f, 1.0f, 1.0f);
	public static final RGBA BLACK = new RGBA(0.0f, 0.0f, 0.0f, 1.0f);	
	
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
	
	public void bind()
	{
		GL11.glColor4f(red, green, blue, alpha);
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if(obj instanceof RGBA)
		{
			if(this.getHexValue().equalsIgnoreCase(((RGBA) obj).getHexValue())) return true;			
		}
		else if(obj instanceof Color)
		{
			Color c = (Color) obj;
			return (this.getRedInt() == c.getRed() && this.getGreenInt() == c.getGreen() &&
					this.getBlueInt() == c.getBlue() &&	this.getAlphaInt() == c.getAlpha());
		}
		else if(obj instanceof String)
		{
			if(this.getHexValue().equalsIgnoreCase((String) obj)) return true;
		}
		
		return false;
	}
	
	public String getHexValue()
	{
		Color color = new Color(this.getRedInt(),this.getGreenInt(), this.getBlueInt());
		StringBuffer hex = new StringBuffer(Integer.toHexString(color.getRGB() & 0xffffff));
		while (hex.length() < 6)
			hex.insert(0, "0");
		
		hex.insert(0, "#");	    
	    return hex.toString();
	}
	
	public byte getRedByte()
	{
		return (byte)(red * 127);
	}
	
	public byte getGreenByte()
	{
		return (byte)(green * 127);
	}
	
	public byte getBlueByte()
	{
		return (byte)(blue * 127);
	}
	
	public byte getAlphaByte()
	{
		return (byte)(alpha * 127);
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
	
	@Override
	public int hashCode() 
	{
		StringBuffer sb = new StringBuffer();
		sb.append(this.getRedInt());
		sb.append(this.getGreenInt());
		sb.append(this.getBlueInt());
		// Omit alpha channel as this would overflow the primitive int type
		
		return Integer.valueOf(sb.toString());
	}	
	
	@Override
	public String toString()
	{
		return this.getHexValue();
	}
}

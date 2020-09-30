package com.sevensoupcans.sfsoftware.util.ui;

import com.sevensoupcans.sfsoftware.util.graphics.Graphics;
import com.sevensoupcans.sfsoftware.util.graphics.RGBA;
import com.sevensoupcans.sfsoftware.util.graphics.TextureFont;

public class FillBar {
	
	private static final RGBA borderColor = new RGBA(1.0f, 1.0f, 1.0f, 1.0f);
		
	private int displayValue;
	private int maxValue;
	private int value;
	
	private boolean hasBorder = true;
	
	private final RGBA fillColorTop;
	private final RGBA fillColorBottom;
	private final RGBA emptyColorTop;
	private final RGBA emptyColorBottom;	
	private final TextureFont font;
	
	public FillBar(int maxValue, int initialValue, RGBA fillColorTop, RGBA fillColorBottom, 
			RGBA emptyColorTop, RGBA emptyColorBottom, TextureFont font)
	{
		this.maxValue = maxValue;
		this.displayValue = initialValue;
		this.value = initialValue;		
		this.fillColorTop = fillColorTop;
		this.fillColorBottom = fillColorBottom;
		this.emptyColorTop = emptyColorTop;
		this.emptyColorBottom = emptyColorBottom;
		this.font = font;
	}
	
	public void draw(int x, int y, int width, int height, String statusText)
	{
		if(hasBorder)
			Graphics.drawRect(x, y, width, height, borderColor, 2);
		
		Graphics.drawQuad(x + 2, y + 2, (width - 4), (height - 4), emptyColorTop, emptyColorTop, 
				emptyColorBottom, emptyColorBottom);							
		
		if(displayValue < value)
		{
			displayValue++;
		}
		else if(displayValue > value)
		{
			displayValue--;
		}
		
		float displayWidth = (((float) displayValue) / maxValue) * (width - 4);
		
		Graphics.drawQuad(x + 2, y + 2, displayWidth, (height - 4), fillColorTop, fillColorTop, 
				fillColorBottom, fillColorBottom);
		
		if(statusText != null && !(statusText.equals("")))
			font.drawString(x + (width / 2) - (font.getWidth(statusText) / 2), y + 2, statusText);
	}
	
	public final int getMaxValue()
	{
		return this.maxValue;
	}
	
	public final int getValue()
	{
		return this.value;
	}
	
	public final void setBordered(boolean hasBorder)
	{
		this.hasBorder = hasBorder;
	}
	
	public final void setMaxValue(int maxValue)
	{
		this.maxValue = maxValue;
	}
	
	public final void setValue(int value)
	{
		this.value = value;
	}
	
}

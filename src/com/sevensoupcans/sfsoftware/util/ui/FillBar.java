package com.sevensoupcans.sfsoftware.util.ui;

import com.sevensoupcans.sfsoftware.util.graphics.Graphics;
import com.sevensoupcans.sfsoftware.util.graphics.RGBA;
import com.sevensoupcans.sfsoftware.util.graphics.TextureFont;
import com.sevensoupcans.sfsoftware.util.graphics.geometry.Quad;

public class FillBar implements GUIElement {
	
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
	
	private int x;
	private int y;
	private int height;
	private int width;
	private String statusText;
	
	public FillBar(int maxValue, int initialValue, RGBA fillColorTop, RGBA fillColorBottom, 
			RGBA emptyColorTop, RGBA emptyColorBottom, TextureFont font)
	{
		this(0, 0, 64, 16, maxValue, initialValue, fillColorTop, fillColorBottom, emptyColorTop, emptyColorBottom, font);
	}
	
	public FillBar(int x, int y, int width, int height, int maxValue, int initialValue, RGBA fillColorTop, RGBA fillColorBottom, 
			RGBA emptyColorTop, RGBA emptyColorBottom, TextureFont font)
	{
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.maxValue = maxValue;
		this.displayValue = initialValue;
		this.value = initialValue;		
		this.fillColorTop = fillColorTop;
		this.fillColorBottom = fillColorBottom;
		this.emptyColorTop = emptyColorTop;
		this.emptyColorBottom = emptyColorBottom;
		this.font = font;
	}	
	
	@Override
	public void draw() 
	{
		this.draw(this.getX(), this.getY(), this.getWidth(), this.getHeight(), this.getStatusText());
	}
	
	public void draw(final int x, final int y, final int width, final int height, final String statusText)
	{
		if(hasBorder)
			Graphics.drawRect(x, y, width, height, borderColor, 2);
		
		Quad.draw(x + 2, y + 2, (width - 4), (height - 4), emptyColorTop, emptyColorTop, 
				emptyColorBottom, emptyColorBottom);							
		
		//this.update();
		
		float displayWidth = (((float) displayValue) / maxValue) * (width - 4);
		
		Quad.draw(x + 2, y + 2, displayWidth, (height - 4), fillColorTop, fillColorTop, 
				fillColorBottom, fillColorBottom);
		
		if(statusText != null && !(statusText.equals("")))
			font.drawString(x + (width / 2) - (font.getWidth(statusText) / 2), y + 2, statusText);
	}
	
	@Override
	public int getHeight() {
		return height;
	}
	
	public final int getMaxValue()
	{
		return this.maxValue;
	}
	
	public String getStatusText() {
		return statusText;
	}
	
	public final int getValue()
	{
		return this.value;
	}

	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public int getX() {
		return x;
	}

	@Override
	public int getY() {
		return y;
	}

	public final void setBordered(final boolean hasBorder)
	{
		this.hasBorder = hasBorder;
	}

	@Override
	public void setHeight(int height) {
		this.height = height;
	}

	public final void setMaxValue(final int maxValue)
	{
		this.maxValue = maxValue;
	}

	public void setStatusText(String statusText) {
		this.statusText = statusText;
	}

	public final void setValue(final int value)
	{
		this.value = value;
	}

	@Override
	public void setWidth(int width) {
		this.width = width;
	}

	@Override
	public void setX(int x) {
		this.x = x;
	}

	@Override
	public void setY(int y) {
		this.y = y;
	}

	@Override
	public void update() {
		if(displayValue < value)
		{
			displayValue++;
		}
		else if(displayValue > value)
		{
			displayValue--;
		}
	}

	@Override
	public boolean shouldRemove() {
		// TODO Auto-generated method stub
		return false;
	}
	
}

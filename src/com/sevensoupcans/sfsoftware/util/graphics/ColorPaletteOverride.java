package com.sevensoupcans.sfsoftware.util.graphics;

public class ColorPaletteOverride 
{
	private final RGBA originalColor;
	private final RGBA replacementColor;
	
	public ColorPaletteOverride(final RGBA originalColor, final RGBA replacementColor)	
	{
		this.originalColor = originalColor;
		this.replacementColor = replacementColor;
	}
	
	public RGBA getOriginalColor()
	{
		return originalColor;
	}
	
	public RGBA getReplacementColor()
	{
		return replacementColor;
	}
}

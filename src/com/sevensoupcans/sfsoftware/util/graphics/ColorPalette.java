package com.sevensoupcans.sfsoftware.util.graphics;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ColorPalette
{
	
	private static RGBA fromRGB(int rgb)
    {
		return new RGBA((rgb >> 16) & 0xFF, (rgb >> 8) & 0xFF, rgb & 0xFF);
    }

	private static int toRGB(RGBA color)
    {
		return (color.getRedInt() << 16) | (color.getGreenInt() << 8) | color.getBlueInt();
    }

	private final Map<Integer, Integer> colors = new HashMap<>();

	public ColorPalette() 
	{
	}
	
	public ColorPalette(ColorPalette palette)
	{
		this.colors.putAll(palette.colors);
	}
	
	public void add(RGBA color)
	{
		int rgb = toRGB(color);
		colors.putIfAbsent(rgb, rgb);
	}
	
    public void clear()
	{
		this.colors.clear();
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
	        return true;
	
		if (!(obj instanceof ColorPalette))
	    	return false;
	
		ColorPalette other = (ColorPalette) obj;
		return this.colors.equals(other.colors);
	}
    
	public RGBA getColor(int sourceRGB)
	{
		int source = sourceRGB & 0xFFFFFF;
		int target = this.colors.getOrDefault(source, source);

		return fromRGB(target);
	}

    public RGBA getColor(RGBA source)
    {
    	return this.getColor(toRGB(source));
    }

    /**
     * Provided with a replacement color palette, get a List of the differences (original
     * and replacement colors) for a palette.
     * 
     * @param other
     * @return
     */
	public List<ColorPaletteOverride> getOverrides(ColorPalette other)
	{
		return this.colors.entrySet().stream()
				.filter(entry -> entry.getValue() != other.colors.getOrDefault(entry.getKey(), entry.getKey()))
				.map(entry -> new ColorPaletteOverride(fromRGB(entry.getKey()), 
						fromRGB(other.colors.getOrDefault(entry.getKey(), entry.getKey()))))
				.toList();
	}
    
    public boolean hasColor(int sourceRGB)
    {
    	return this.colors.containsKey(sourceRGB & 0xFFFFFF);
    }

    public boolean hasColor(RGBA source)
    {
    	return this.hasColor(toRGB(source));
    }

    public void removeColor(int sourceRGB)
    {
    	this.colors.remove(sourceRGB & 0xFFFFFF);
    }

    public void removeColor(RGBA source)
    {
    	this.removeColor(toRGB(source));
    }

	public void replaceAll(RGBA color)
	{
		int rgb = toRGB(color);
		this.colors.replaceAll((source, target) -> rgb);
	}
    
    public void setColor(int sourceRGB, int targetRGB)
	{
    	this.colors.put(sourceRGB & 0xFFFFFF, targetRGB & 0xFFFFFF);
	}

    public void setColor(RGBA source, RGBA target)
    {
    	this.setColor(toRGB(source), toRGB(target));
    }
    
    public void setColors(RGBA[] source, RGBA[] target)
    {
    	for(int i = 0; i < source.length; i++)
    	{
    		this.setColor(toRGB(source[i]), toRGB(target[i]));
    	}
    }
    
    public int size()
    {
    	return colors.size();
    }
    
    public RGBA[] toArray()
    {
    	RGBA[] colorsArray = new RGBA[this.colors.size()];
    	int index = 0;
        
    	for (Integer color : this.colors.values())
    	{
    		colorsArray[index++] = fromRGB(color);
    	}
    	
    	return colorsArray;
    }
}
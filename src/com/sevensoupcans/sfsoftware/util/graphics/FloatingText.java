package com.sevensoupcans.sfsoftware.util.graphics;

import java.awt.Font;
import java.util.ArrayList;
import java.util.List;

import com.sevensoupcans.sfsoftware.util.Clock;
import com.sevensoupcans.sfsoftware.util.Updatable;

public final class FloatingText implements Updatable {
	private static List<FloatingText> floatingTexts = new ArrayList<FloatingText>();
	private static final TextureFont FLOATING_TEXT_FONT;
	private Clock fadeClock = new Clock(10);
	private double fadeOut;
	
	private RGBA textColor;	
	private float x;
	private float y;
	private String text;
	
	private float red;
	private float green;
	private float blue;
	private float alpha;		
	
	static
	{
		Font awtFont = new Font("Verdana", Font.BOLD, 11);
		FLOATING_TEXT_FONT = new TextureFont(awtFont, true);		
	}
	
	public FloatingText(float destX, float destY, String destText, float r, float g, float b, float a)
	{
		x = destX;
		y = destY;
		text = destText;

		red = r;
		green = g;
		blue = b;
		alpha = a;
		
		fadeOut = (Math.PI / 2);
		textColor = new RGBA(red, green, blue, alpha);

		floatingTexts.add(this);		
	}
	
	public FloatingText(float destX, float destY, String destText) {
		x = destX;
		y = destY;
		text = destText;

		red = 1;
		green = 1;
		blue = 1;
		alpha = 1;
		
		fadeOut = (Math.PI / 2);
		textColor = new RGBA(red, green, blue, alpha);

		floatingTexts.add(this);
	}

	/**
	 * Static method to update all of the particles currently in existence.
	 */
	public static void updateAll()
	{
		floatingTexts.forEach(floatingText -> floatingText.update());
	}	
	
	@Override
	public void update() {		
		int textWidth = FLOATING_TEXT_FONT.getWidth(text);
		FLOATING_TEXT_FONT.drawString(x - (textWidth / 2), y, text, textColor);		
		
		if(fadeClock.updateClock())
		{
			fadeOut = fadeOut + 0.025;
		}
		
		alpha = (float) Math.sin(fadeOut); //alpha - 0.015f;
		textColor = new RGBA(red, green, blue, alpha);
		
		y = y - 1;
		
		if(alpha <= 0)
		{
			floatingTexts.remove(this);
		}
		
	}

}

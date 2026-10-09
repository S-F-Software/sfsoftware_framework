package com.sevensoupcans.sfsoftware.util.graphics.font;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

import com.sevensoupcans.sfsoftware.util.graphics.RGBA;
import com.sevensoupcans.sfsoftware.util.graphics.Texture;

/**
 * Original TrueType font implementation for Slick by
 * 
 * @author James Chambers (Jimmy)
 * @author Jeremy Adams (elias4444)
 * @author Kevin Glass (kevglass)
 * @author Peter Korzuszek (genail)
 */
public class BitmapFont 
{
	private final static BitmapFont DEFAULT_FONT;
	
	static
	{
		DEFAULT_FONT = new BitmapFont("Arial", Font.PLAIN, 12);
	}
	
	public static BitmapFont getDefaultFont()
	{
		return DEFAULT_FONT;
	}
	
	/** Array that holds necessary information about the font characters */
	private Glyph[] charArray = new Glyph[256];
	
	/** Map of user defined font characters (Character <-> IntObject) */
	private Map<Character, Glyph> customChars = new HashMap<Character, Glyph>();

	/** Boolean flag on whether AntiAliasing is enabled or not */
	private boolean antiAlias;

	/** Font's size */
	private int fontSize = 0;

	/** Font's height */
	private int fontHeight = 0;

	/** Texture used to cache the font 0-255 characters */
	private Texture fontTexture;

	/** A reference to Java's AWT Font that we create our font texture from */
	private java.awt.Font font;

	/** The font metrics for our Java AWT font */
	private FontMetrics fontMetrics;
	
	private int textureWidth = 512;
	private int textureHeight = 512;
	
	/**
	 * Constructor for the TrueTypeFont class Pass in the preloaded standard
	 * Java TrueType font, and whether you want it to be cached with
	 * AntiAliasing applied.
	 * 
	 * @param font
	 *            Standard Java AWT font
	 * @param antiAlias
	 *            Whether or not to apply AntiAliasing to the cached font
	 * @param additionalChars
	 *            Characters of font that will be used in addition of first 256 (by unicode).
	 */
	public BitmapFont(java.awt.Font font, boolean antiAlias, char[] additionalChars) 
	{
		
		this.font = font;
		this.fontSize = font.getSize();
		this.antiAlias = antiAlias;

		createSet( additionalChars );
	}
	
	public BitmapFont(String name, int style, int size)
	{
		this(new Font(name, style, size), true);
	}	
	
	public BitmapFont(String name, int style, int size, boolean antiAlias)
	{
		this(new Font(name, style, size), antiAlias);
	}		
	
	public BitmapFont(Font font, boolean antiAlias) {
		this(font, antiAlias, null);
	}
	
	/**
	 * Create and store the font
	 * 
	 * @param customCharsArray Characters that should be also added to the cache.
	 */
	private void createSet(final char[] customCharsArray ) {
		// If there are custom chars then I expand the font texture twice		
		if	(customCharsArray != null && customCharsArray.length > 0) {
			textureWidth *= 2;
		}
		
		// In any case this should be done in other way. Texture with size 512x512
		// can maintain only 256 characters with resolution of 32x32. The texture
		// size should be calculated dynamicaly by looking at character sizes. 
		
		//try {
			
			BufferedImage imgTemp = new BufferedImage(textureWidth, textureHeight, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = (Graphics2D) imgTemp.getGraphics();

			g.setColor(new Color(255,255,255,1));
			g.fillRect(0,0,textureWidth,textureHeight);
			
			int rowHeight = 0;
			int positionX = 0;
			int positionY = 0;
			
			int customCharsLength = ( customCharsArray != null ) ? customCharsArray.length : 0; 

			for (int i = 0; i < 256 + customCharsLength; i++) {
				
				// get 0-255 characters and then custom characters
				char ch = ( i < 256 ) ? (char) i : customCharsArray[i-256];
				
				BufferedImage fontImage = getFontImage(ch);

				int width = fontImage.getWidth();
				int height = fontImage.getHeight();

				if (positionX + width >= textureWidth) {
					positionX = 0;
					positionY += rowHeight;
					rowHeight = 0;
				}
				
				Glyph newIntObject = new Glyph(width, height, positionX, positionY);

				if (newIntObject.getHeight() > fontHeight) {
					fontHeight = newIntObject.getHeight();
				}

				if (newIntObject.getHeight() > rowHeight) {
					rowHeight = newIntObject.getHeight();
				}

				// Draw it here
				g.drawImage(fontImage, positionX, positionY, null);

				positionX += newIntObject.getWidth();

				if( i < 256 ) { // standard characters
					charArray[i] = newIntObject;
				} else { // custom characters
					customChars.put( Character.valueOf(ch), newIntObject );
				}

				fontImage = null;
			}

			fontTexture = new Texture(imgTemp);			

		/*} catch (IOException e) {
			System.err.println("Failed to create font.");
			e.printStackTrace();
		}*/
	}
	
	/**
	 * Create a standard Java2D BufferedImage of the given character
	 * 
	 * @param ch
	 *            The character to create a BufferedImage for
	 * 
	 * @return A BufferedImage containing the character
	 */
	private BufferedImage getFontImage(final char ch) {
		// Create a temporary image to extract the character's size
		BufferedImage tempfontImage = new BufferedImage(1, 1,
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = (Graphics2D) tempfontImage.getGraphics();
		if (antiAlias == true) {
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
		}
		g.setFont(font);
		fontMetrics = g.getFontMetrics();
		int charwidth = fontMetrics.charWidth(ch);

		if (charwidth <= 0) {
			charwidth = 1;
		}
		int charheight = fontMetrics.getHeight();
		if (charheight <= 0) {
			charheight = fontSize;
		}

		// Create another image holding the character we are creating
		BufferedImage fontImage;
		fontImage = new BufferedImage(charwidth, charheight,
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D gt = (Graphics2D) fontImage.getGraphics();
		if (antiAlias == true) {
			gt.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
		}
		gt.setFont(font);

		gt.setColor(Color.WHITE);
		int charx = 0;
		int chary = 0;
		gt.drawString(String.valueOf(ch), (charx), (chary)
				+ fontMetrics.getAscent());

		return fontImage;

	}


	
	/**
	 * Get the width of a given String
	 * 
	 * @param whatchars
	 *            The characters to get the width of
	 * 
	 * @return The width of the characters
	 */
	public int getWidth(final String whatchars) 
	{
		int totalwidth = 0;
		Glyph intObject = null;
		int currentChar = 0;
		for (int i = 0; i < whatchars.length(); i++) {
			currentChar = whatchars.charAt(i);
			if (currentChar < 256) {
				intObject = charArray[currentChar];
			} else {
				intObject = customChars.get( Character.valueOf( (char) currentChar ) );
			}
			
			if( intObject != null )
				totalwidth += intObject.getWidth();
		}
		return totalwidth;
	}

	/**
	 * Get the font's height
	 * 
	 * @return The height of the font
	 */
	public int getHeight() 
	{
		return fontHeight;
	}

	/**
	 * Get the height of a String
	 * 
	 * @return The height of a given string
	 */
	public int getHeight(final String HeightString) 
	{
		return fontHeight;
	}

	/**
	 * Get the font's line height
	 * 
	 * @return The line height of the font
	 */
	public int getLineHeight() 
	{
		return fontHeight;
	}
	
	/**
	 * Draw a string
	 * 
	 * @param x
	 *            The x position to draw the string
	 * @param y
	 *            The y position to draw the string
	 * @param whatchars
	 *            The string to draw
	 * @param color
	 *            The color to draw the text
	 */
	public void drawString(final float x, final float y, final String whatchars, final RGBA color) 
	{
		drawString(x,y,whatchars,color,0,whatchars.length()-1);
	}
	
	/**
	 * @see Font#drawString(float, float, String, RGBA, int, int)
	 */
	public void drawString(final float x, final float y, final String whatchars, final RGBA color, 
			final int startIndex, final int endIndex) {

		Glyph intObject = null;
		int charCurrent;

		int totalwidth = 0;
		for (int i = 0; i < whatchars.length(); i++) 
		{
			charCurrent = whatchars.charAt(i);
			if (charCurrent < 256) 
			{
				intObject = charArray[charCurrent];
			} 
			else 
			{
				intObject = customChars.get( Character.valueOf( (char) charCurrent ) );
			} 
			
			if( intObject != null ) {
				if ((i >= startIndex) || (i <= endIndex)) 
				{
					Texture.drawTexture((x + totalwidth), y, fontTexture, intObject.getWidth(), intObject.getHeight(), 
							intObject.getStoredX(), intObject.getStoredY(), intObject.getWidth(), intObject.getHeight(), 
							color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha(), 0);
				}
				totalwidth += intObject.getWidth();
			}
		}

	}
	
	/**
	 * Draw a string
	 * 
	 * @param x
	 *            The x position to draw the string
	 * @param y
	 *            The y position to draw the string
	 * @param whatchars
	 *            The string to draw
	 */
	public void drawString(final float x, final float y, final String whatchars) 
	{
		drawString(x, y, whatchars, new RGBA(1.0f, 1.0f, 1.0f));
	}
	
	public Texture getTexture()
	{
		return fontTexture;
	}
}

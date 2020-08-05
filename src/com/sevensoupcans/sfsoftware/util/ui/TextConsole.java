package com.sevensoupcans.sfsoftware.util.ui;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;

import org.lwjgl.input.Keyboard;

import com.sevensoupcans.sfsoftware.game.Game;
import com.sevensoupcans.sfsoftware.util.graphics.Graphics;
import com.sevensoupcans.sfsoftware.util.graphics.RGBA;
import com.sevensoupcans.sfsoftware.util.graphics.TextureFont;
import com.sevensoupcans.sfsoftware.util.input.InputDevice;
import com.sevensoupcans.sfsoftware.util.input.Kboard;
import com.sevensoupcans.sfsoftware.util.input.UserInput;

public class TextConsole implements UserInput
{
	private boolean consoleOpen = false;
	private boolean firstOpen = true;
	private ByteArrayOutputStream baos = new ByteArrayOutputStream();
	private PrintStream ps = new PrintStream(baos, true);
	private int yPos = -240;
	private float r;
	private float g;
	private float b;
	private int cursorY;
	
	private Game game;
	
	public TextConsole(Game game, float red, float green, float blue)
	{
		r = red;
		g = green;
		b = blue;
		
		this.game = game;
		
		System.setOut(ps);
	}
	
	public TextConsole(Game game)
	{
		this(game, 0, 0, 0);
	}
	
	public void toggleConsole()
	{
		consoleOpen = !(consoleOpen);
		if(firstOpen)
		{
			cursorY = getLineCount() - 15;
			firstOpen = false;
		}
	}
	
	public boolean isOpen()
	{
		return consoleOpen;
	}
	
	public int getLineCount()
	{
		return getLines().length;
	}
	
	private String[] getLines(int startPos, int lineCount) throws ArrayIndexOutOfBoundsException
	{		
		String[] fullLines = getLines();					
		return Arrays.copyOfRange(fullLines, startPos, Math.min(startPos + lineCount, fullLines.length));
	}
	
	private String[] getLines()
	{
		String contents = baos.toString();
		return contents.split(System.lineSeparator());	
	}
	
	public boolean pollInput()
	{
		InputDevice inputDevice = game.getInputDevice();
		boolean pressed = false;
		
		if(inputDevice.wasBackPressed())
		{
			toggleConsole();
			pressed = true;
		}
		else if(inputDevice.wasUpPressed() && cursorY > 0)
		{
			cursorY--;
			pressed = true;
		}
		else if(inputDevice.wasDownPressed() && cursorY < (getLineCount() - 15))
		{
			cursorY++;
			pressed = true;
		}
		else if(Kboard.keyPressed(Keyboard.KEY_END)) 
		{
			cursorY = getLineCount() - 15;
		}
		
		inputDevice.storeState();
		
		return pressed;
	}
	
	public void update()
	{
		if(!(consoleOpen))
		{
			yPos = yPos > -240 ? yPos - 20 : -240;
		}
		else
		{
			yPos = yPos < 0 ? yPos + 20 : 0;			
		}
		
		float alpha = Math.min((((float) yPos + 240) / 240), 0.75f);
		
		Graphics.drawQuad(0, yPos, game.getScreenWidth(), 240, new RGBA(r, g, b, alpha));
				 
		Graphics.drawQuad((game.getScreenWidth() - 8), yPos + ((float) cursorY / (getLineCount() - 15)) * 226, 4, 12, new RGBA(1.0f, 1.0f, 1.0f, alpha));		
		
		
		String[] lines = getLines(cursorY, getLines().length >= 15 ? 15 : getLines().length - 1);
		int lineHeight = TextureFont.getDefaultFont().getHeight();
		for(int i = 0; i < lines.length; i ++)
		{
			TextureFont.getDefaultFont().drawString(5, yPos + (lineHeight * i), lines[i]);
		}			
	}
}

package com.sevensoupcans.sfsoftware.util.ui;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;

import org.lwjgl.input.Keyboard;

import com.sevensoupcans.sfsoftware.game.Game;
import com.sevensoupcans.sfsoftware.util.Clock;
import com.sevensoupcans.sfsoftware.util.graphics.RGBA;
import com.sevensoupcans.sfsoftware.util.graphics.TextureFont;
import com.sevensoupcans.sfsoftware.util.graphics.geometry.Quad;
import com.sevensoupcans.sfsoftware.util.input.InputDevice;
import com.sevensoupcans.sfsoftware.util.input.Kboard;
import com.sevensoupcans.sfsoftware.util.input.UserInput;

public class TextConsole implements GUIElement, UserInput
{
	private boolean consoleOpen = false;
	private boolean firstOpen = true;
	private ByteArrayOutputStream baos = new ByteArrayOutputStream();
	private PrintStream ps = new PrintStream(baos, true);
	private StringBuffer userInputField = new StringBuffer();	
	private float r;
	private float g;
	private float b;
	private int cursorY;
	private int height = 240;
	private int yPos = (0 - height);
	private boolean blinkCursor = false;
	private Clock blinkCursorClock = new Clock(250);
	
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
			cursorY = Math.max(0, getLineCount() - 15);
			firstOpen = false;
		}
	}
	
	public final boolean isOpen()
	{
		return consoleOpen;
	}
	
	public final int getLineCount()
	{
		return getLines().length;
	}
	
	private String[] getLines(final int startPos, final int lineCount) throws ArrayIndexOutOfBoundsException
	{		
		String[] fullLines = getLines();					
		return Arrays.copyOfRange(fullLines, startPos, Math.min(startPos + lineCount, fullLines.length));
	}
	
	private String[] getLines()
	{
		String contents = baos.toString();
		return contents.split(System.lineSeparator());	
	}
	
	@Override
	public boolean pollInput(final InputDevice inputDevice)
	{
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
			cursorY = Math.max(0, getLineCount() - 15);
		}
		else if(Kboard.keyPressed(Keyboard.KEY_RETURN))
		{
			// Execute a command based on the provided user input
			System.out.println("> " + userInputField.toString());
			game.executeTextConsoleCommand(userInputField.toString());
			cursorY = Math.max(0, getLineCount() - 15);
			// Discard the existing user input
			userInputField = new StringBuffer();
		}
		else if(Kboard.keyPressed(Keyboard.KEY_BACK) && userInputField.length() > 0)
		{
			userInputField.deleteCharAt(userInputField.length() - 1);
		}
		else
		{
			for(int i : Kboard.alphabetKeys)
			{
				if(Kboard.keyPressed(i))
				{
					String text = Kboard.getKeyName(i);
					if(!(Kboard.isKeyDown(Kboard.shiftKeys))) text = text.toLowerCase();
					
					userInputField.append(text);					
				}
			}
			for(int i : Kboard.numberKeys)
			{
				if(Kboard.keyPressed(i))
				{
					String text = Kboard.getKeyName(i).substring(Kboard.getKeyName(i).length() - 1);					
					userInputField.append(text);					
				}
			}
			if(Kboard.keyPressed(Keyboard.KEY_SPACE))
			{
				userInputField.append(' ');					
			}
			else if(Kboard.keyPressed(Keyboard.KEY_SEMICOLON))
			{
				userInputField.append(';');
			}
			else if(Kboard.keyPressed(Keyboard.KEY_APOSTROPHE))
			{
				userInputField.append('\'');
			}
		}
		
		inputDevice.storeState();
		
		return pressed;
	}
	
	@Override
	public void update()
	{
		if(!(consoleOpen))
		{
			yPos = yPos > (0 - height) ? yPos - 20 : (0 - height);
		}
		else
		{
			yPos = yPos < 0 ? yPos + 20 : 0;			
		}
		
		if(yPos > (0 - height))
		{
			this.draw();
		}
	}

	@Override
	public void draw() 
	{
		float alpha = Math.min((((float) yPos + height) / height), 0.75f);
		
		Quad.draw(0, yPos, game.getScreenWidth(), height, new RGBA(r, g, b, alpha));				 
		Quad.draw((game.getScreenWidth() - 8), yPos + ((float) cursorY / (getLineCount() - 15)) * 226, 4, 12, new RGBA(1.0f, 1.0f, 1.0f, alpha));		
		
		
		String[] lines = getLines(cursorY, getLines().length >= 15 ? 15 : getLines().length - 1);
		int lineHeight = TextureFont.getDefaultFont().getHeight();
		for(int i = 0; i < lines.length; i ++)
		{
			TextureFont.getDefaultFont().drawString(5, yPos + (lineHeight * i), lines[i]);
		}
		
		if(blinkCursorClock.updateClock()) blinkCursor = !(blinkCursor);
		
		String userInputDisplayString = blinkCursor ? "> " + userInputField.toString() + "_" : "> " + userInputField.toString();
		TextureFont.getDefaultFont().drawString(5, (yPos + 235) - TextureFont.getDefaultFont().getHeight(), userInputDisplayString);			
	}

	@Override
	public int getHeight() {
		return height;
	}

	@Override
	public int getWidth() {
		return game.getScreenWidth();
	}

	@Override
	public void setHeight(int height) {
		this.height = height;
	}

	@Override
	public void setWidth(int width) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getX() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getY() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setX(int X) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setY(int Y) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean shouldRemove() {
		// TODO Auto-generated method stub
		return false;
	}
}

package com.sevensoupcans.sfsoftware.util.input;

import java.util.Vector;

import org.lwjgl.input.Keyboard;

public class Kboard implements InputDevice {		
	public static int[] leftKeys = 	{Keyboard.KEY_A, Keyboard.KEY_LEFT};
	public static int[] rightKeys = {Keyboard.KEY_D, Keyboard.KEY_RIGHT};
	public static int[] upKeys = 	{Keyboard.KEY_W, Keyboard.KEY_UP};
	public static int[] downKeys = 	{Keyboard.KEY_S, Keyboard.KEY_DOWN};	
	public static int[] pauseKeys = {Keyboard.KEY_TAB, Keyboard.KEY_RSHIFT};
	
	public static final int KEY_A = Keyboard.KEY_A;
	public static final int KEY_D = Keyboard.KEY_D;
	public static final int KEY_F = Keyboard.KEY_F;
	public static final int KEY_S = Keyboard.KEY_S;
	public static final int KEY_W = Keyboard.KEY_W;		
	public static final int KEY_GRAVE = Keyboard.KEY_GRAVE;
	
	private static Vector<Integer> lastKeyState = new Vector<Integer>();
	
	/**
	 * Accepts a key and, if pressed, returns true
	 * 
	 * @param key
	 * @return
	 */
	public static boolean isKeyDown(int key)
	{		
		return Keyboard.isKeyDown(key);
	}
	
	/**
	 * Accepts an array of keys and, if any are currently pressed, returns true
	 * 
	 * @param keyGroup
	 * @return
	 */
	public static boolean isKeyDown(int[] keyGroup)
	{
		for(int i = 0; i < keyGroup.length; i++)
		{
			if(isKeyDown(keyGroup[i]))
			{
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Returns true if the given key is pressed and wasn't just pressed in the last pass.
	 * Ideal for a single keystroke.
	 * 
	 * @param key
	 * @return
	 */
	public static boolean keyPressed(int key)
	{		
		return (isKeyDown(key) && !(lastKeyState.contains(key)));
	}
	
	public static boolean keyPressed(int[] keyGroup)
	{
		for(int i = 0; i < keyGroup.length; i++)
		{
			if(keyPressed(keyGroup[i]))
			{
				return true;
			}
		}
		return false;
	}
	
	public static String getKeyName(int key)
	{
		String result =	Keyboard.getKeyName(key);
		if(key == Keyboard.KEY_LCONTROL)
		{
			result = "L Ctrl";
		}
		else if(key == Keyboard.KEY_RCONTROL)
		{
			result = "R Ctrl";
		}		
		result = result.toUpperCase();
		return result;
	}
	
	/**
	 * Stores the currently pressed keys
	 */
	public static void storeKBState()
	{
		lastKeyState.clear();
		for(int i = 0; i < Keyboard.KEYBOARD_SIZE; i++)
		{
			if(Keyboard.isKeyDown(i))
			{		
				lastKeyState.add(i);
			}
		}
	}

	@Override
	public boolean isUpDown() {
		return Kboard.isKeyDown(upKeys);
	}

	@Override
	public boolean isDownDown() {
		return Kboard.isKeyDown(downKeys);
	}

	@Override
	public boolean isLeftDown() {
		return Kboard.isKeyDown(leftKeys);
	}

	@Override
	public boolean isRightDown() { 
		return Kboard.isKeyDown(rightKeys);
	}// TODO Auto-generated method stub
	

	@Override
	public boolean wasUpPressed() {
		return Kboard.keyPressed(upKeys);		
	}

	@Override
	public boolean wasDownPressed() {
		return Kboard.keyPressed(downKeys);
	}

	@Override
	public boolean wasLeftPressed() {
		return Kboard.keyPressed(leftKeys);
	}

	@Override
	public boolean wasRightPressed() {
		return keyPressed(rightKeys);
	}

	@Override
	public boolean isButtonADown() {
		return Kboard.isKeyDown(Keyboard.KEY_SPACE);
	}

	@Override
	public boolean isButtonBDown() {
		return Kboard.isKeyDown(Keyboard.KEY_LCONTROL);
	}

	@Override
	public boolean isButtonXDown() {
		return Kboard.isKeyDown(Keyboard.KEY_LSHIFT);
	}

	@Override
	public boolean isButtonYDown() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean wasButtonAPressed() {
		return Kboard.keyPressed(Keyboard.KEY_SPACE);
	}

	@Override
	public boolean wasButtonBPressed() {
		return Kboard.keyPressed(Keyboard.KEY_LCONTROL);
	}

	@Override
	public boolean wasButtonXPressed() {
		return Kboard.keyPressed(Keyboard.KEY_LSHIFT);
	}

	@Override
	public boolean wasButtonYPressed() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void poll() {		
	}

	@Override
	public boolean wasBackPressed() {
		return Kboard.keyPressed(Keyboard.KEY_ESCAPE);
	}

	@Override
	public boolean wasStartPressed() {
		return Kboard.keyPressed(Keyboard.KEY_RETURN);
	}

	@Override
	public void storeState() {
		Kboard.storeKBState();		
	}

	@Override
	public boolean wasPausedPressed() {
		return Kboard.keyPressed(Kboard.pauseKeys);
	}
	
}

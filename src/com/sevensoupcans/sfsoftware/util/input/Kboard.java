package com.sevensoupcans.sfsoftware.util.input;

import java.util.Vector;

import org.lwjgl.input.Keyboard;

public final class Kboard implements InputDevice {		
	public static final int[] leftKeys = 	{Keyboard.KEY_A, Keyboard.KEY_LEFT};
	public static final int[] rightKeys = {Keyboard.KEY_D, Keyboard.KEY_RIGHT};
	public static final int[] upKeys = 	{Keyboard.KEY_W, Keyboard.KEY_UP};
	public static final int[] downKeys = 	{Keyboard.KEY_S, Keyboard.KEY_DOWN};
	
	// TODO As tab and right shift aren't universal "pause keys," it may be worth moving this elsewhere
	public static final int[] pauseKeys = {Keyboard.KEY_TAB, Keyboard.KEY_RSHIFT};
	
	public static final int[] alphabetKeys = { Keyboard.KEY_Q, Keyboard.KEY_W, Keyboard.KEY_E, Keyboard.KEY_R,
			Keyboard.KEY_T,Keyboard.KEY_Y,Keyboard.KEY_U,Keyboard.KEY_I,Keyboard.KEY_O,Keyboard.KEY_P,
			Keyboard.KEY_A,Keyboard.KEY_S,Keyboard.KEY_D,Keyboard.KEY_F,Keyboard.KEY_G,Keyboard.KEY_H,
			Keyboard.KEY_J,Keyboard.KEY_K,Keyboard.KEY_L,Keyboard.KEY_Z,Keyboard.KEY_X,Keyboard.KEY_C,
			Keyboard.KEY_V,Keyboard.KEY_B,Keyboard.KEY_N,Keyboard.KEY_M };
	
	public static final int[] numberKeys = { Keyboard.KEY_0, Keyboard.KEY_1, Keyboard.KEY_2, Keyboard.KEY_3, 
			Keyboard.KEY_4, Keyboard.KEY_5, Keyboard.KEY_6, Keyboard.KEY_7, Keyboard.KEY_8, Keyboard.KEY_9, 
			Keyboard.KEY_NUMPAD0, Keyboard.KEY_NUMPAD1, Keyboard.KEY_NUMPAD2, Keyboard.KEY_NUMPAD3,
			Keyboard.KEY_NUMPAD4, Keyboard.KEY_NUMPAD5, Keyboard.KEY_NUMPAD6, Keyboard.KEY_NUMPAD7,
			Keyboard.KEY_NUMPAD8, Keyboard.KEY_NUMPAD9 };
	
	public static final int[] numPadKeys = { Keyboard.KEY_MULTIPLY, Keyboard.KEY_DIVIDE,
			Keyboard.KEY_DECIMAL, Keyboard.KEY_ADD, Keyboard.KEY_SUBTRACT, Keyboard.KEY_NUMPAD0, 
			Keyboard.KEY_NUMPAD1, Keyboard.KEY_NUMPAD2, Keyboard.KEY_NUMPAD3, Keyboard.KEY_NUMPAD4, 
			Keyboard.KEY_NUMPAD5, Keyboard.KEY_NUMPAD6, Keyboard.KEY_NUMPAD7, Keyboard.KEY_NUMPAD8, 
			Keyboard.KEY_NUMPAD9 };
	
	public static final int[] shiftKeys = { Keyboard.KEY_LSHIFT, Keyboard.KEY_RSHIFT };
	
	public static final int[] symbolKeys = { Keyboard.KEY_COMMA, Keyboard.KEY_PERIOD, Keyboard.KEY_SLASH, 
			Keyboard.KEY_BACKSLASH, Keyboard.KEY_APOSTROPHE, Keyboard.KEY_SEMICOLON, Keyboard.KEY_LBRACKET,
			Keyboard.KEY_RBRACKET, Keyboard.KEY_GRAVE, Keyboard.KEY_EQUALS, Keyboard.KEY_UNDERLINE, };	
	
	public static final int KEY_A = Keyboard.KEY_A;
	public static final int KEY_D = Keyboard.KEY_D;
	public static final int KEY_F = Keyboard.KEY_F;
	public static final int KEY_S = Keyboard.KEY_S;
	public static final int KEY_W = Keyboard.KEY_W;		
	public static final int KEY_GRAVE = Keyboard.KEY_GRAVE;
	
	private static final Vector<Integer> lastKeyState = new Vector<Integer>();
	
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
	public boolean isButtonADown() {
		return Kboard.isKeyDown(Keyboard.KEY_SPACE);
	}

	@Override
	public boolean isButtonBDown() {
		return Kboard.isKeyDown(Keyboard.KEY_LCONTROL);
	}

	@Override
	public boolean isButtonShoulderLeftDown() 
	{
		return Kboard.isKeyDown(Keyboard.KEY_LBRACKET);
	}

	@Override
	public boolean isButtonShoulderRightDown() 
	{
		return Kboard.isKeyDown(Keyboard.KEY_RBRACKET);
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
	public boolean isUpDown() {
		return Kboard.isKeyDown(upKeys);
	}

	@Override
	public void poll() {		
	}

	@Override
	public void storeState() {
		Kboard.storeKBState();		
	}

	@Override
	public boolean wasBackPressed() {
		return Kboard.keyPressed(Keyboard.KEY_ESCAPE);
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
	public boolean wasButtonShoulderLeftPressed() 
	{
		return Kboard.keyPressed(Keyboard.KEY_LBRACKET);
	}

	@Override
	public boolean wasButtonShoulderRightPressed() 
	{
		return Kboard.keyPressed(Keyboard.KEY_RBRACKET);
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
	public boolean wasDownPressed() {
		return Kboard.keyPressed(downKeys);
	}

	@Override
	public boolean wasLeftPressed() {
		return Kboard.keyPressed(leftKeys);
	}

	@Override
	public boolean wasPausedPressed() {
		return Kboard.keyPressed(Kboard.pauseKeys);
	}

	@Override
	public boolean wasRightPressed() {
		return keyPressed(rightKeys);
	}

	@Override
	public boolean wasStartPressed() {
		return Kboard.keyPressed(Keyboard.KEY_RETURN);
	}

	@Override
	public boolean wasUpPressed() {
		return Kboard.keyPressed(upKeys);		
	}
	
}

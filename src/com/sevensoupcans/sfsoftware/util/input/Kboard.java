package com.sevensoupcans.sfsoftware.util.input;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Keyboard;

public final class Kboard implements InputDevice {		
	public static final int[] leftKeys = 	{Keyboard.KEY_A, Keyboard.KEY_LEFT};
	public static final int[] rightKeys = {Keyboard.KEY_D, Keyboard.KEY_RIGHT};
	public static final int[] upKeys = 	{Keyboard.KEY_W, Keyboard.KEY_UP};
	public static final int[] downKeys = 	{Keyboard.KEY_S, Keyboard.KEY_DOWN};
	
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
	public static final int KEY_E = Keyboard.KEY_E;
	public static final int KEY_ESCAPE = Keyboard.KEY_ESCAPE;	
	public static final int KEY_F = Keyboard.KEY_F;
	public static final int KEY_GRAVE = Keyboard.KEY_GRAVE;		
	public static final int KEY_LBRACKET = Keyboard.KEY_LBRACKET;
	public static final int KEY_LCONTROL = Keyboard.KEY_LCONTROL;
	public static final int KEY_LSHIFT = Keyboard.KEY_LSHIFT;
	public static final int KEY_PAUSE = Keyboard.KEY_PAUSE;
	public static final int KEY_RBRACKET = Keyboard.KEY_RBRACKET;
	public static final int KEY_RCONTROL = Keyboard.KEY_RCONTROL;
	public static final int KEY_RETURN = Keyboard.KEY_RETURN;	
	public static final int KEY_RSHIFT = Keyboard.KEY_RSHIFT;	
	public static final int KEY_S = Keyboard.KEY_S;	
	public static final int KEY_SPACE = Keyboard.KEY_SPACE;
	public static final int KEY_TAB = Keyboard.KEY_TAB;	
	public static final int KEY_W = Keyboard.KEY_W;			
	
	private static final List<Integer> lastKeyState = new ArrayList<Integer>();
	
	private int AButtonKey = Keyboard.KEY_SPACE;
	private int BButtonKey = Keyboard.KEY_LCONTROL;
	private int XButtonKey = Keyboard.KEY_LSHIFT;
	private int YButtonKey = Keyboard.KEY_RCONTROL;
	private int BackButtonKey = Keyboard.KEY_ESCAPE;
	private int ShoulderLeftKey = Keyboard.KEY_LBRACKET;
	private int ShoulderRightKey = Keyboard.KEY_RBRACKET;
	private int StartButtonKey = Keyboard.KEY_RETURN;
	private int[] pauseKeys = {Keyboard.KEY_PAUSE};
	
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
		return Kboard.isKeyDown(this.AButtonKey);
	}

	@Override
	public boolean isButtonBDown() {
		return Kboard.isKeyDown(this.BButtonKey);
	}

	@Override
	public boolean isButtonShoulderLeftDown() 
	{
		return Kboard.isKeyDown(this.ShoulderLeftKey);
	}

	@Override
	public boolean isButtonShoulderRightDown() 
	{
		return Kboard.isKeyDown(this.ShoulderRightKey);
	}
	

	@Override
	public boolean isButtonXDown() {
		return Kboard.isKeyDown(this.XButtonKey);
	}

	@Override
	public boolean isButtonYDown() {
		return Kboard.isKeyDown(this.YButtonKey);
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
	}

	@Override
	public boolean isUpDown() {
		return Kboard.isKeyDown(upKeys);
	}

	@Override
	public void poll() {		
	}

	public String getAButtonKeyName()
	{
		return getKeyName(this.AButtonKey);
	}
	
	public String getBButtonKeyName()
	{
		return getKeyName(this.BButtonKey);
	}
	
	public String getXButtonKeyName()
	{
		return getKeyName(this.XButtonKey);
	}
	
	public String getYButtonKeyName()
	{
		return getKeyName(this.YButtonKey);
	}		
	
	public String getBackButtonKeyName()
	{
		return getKeyName(this.BackButtonKey);
	}
	
	public String getStartButtonKeyName()
	{		
		return getKeyName(this.StartButtonKey);
	}
	
	public String getLeftShoulerButtonKeyName()
	{
		return getKeyName(this.ShoulderLeftKey);
	}
	
	public String getRightShoulerButtonKeyName()
	{
		return getKeyName(this.ShoulderRightKey);
	}	
	
	public void mapKeyAsAButton(final int key)
	{		
		this.AButtonKey = key;
	}
	
	public void mapKeyAsBButton(final int key)
	{
		this.BButtonKey = key;
	}
	
	public void mapKeyAsBackButton(final int key)
	{
		this.BackButtonKey = key;
	}	
	
	public void mapKeyAsStartButton(final int key)
	{
		this.StartButtonKey = key;
	}
	
	public void mapKeyAsXButton(final int key)
	{
		this.XButtonKey = key;
	}	

	public void mapKeyAsYButton(final int key)
	{
		this.YButtonKey = key;
	}
	
	public void mapKeyAsLeftShoulderButton(final int key)
	{
		this.ShoulderLeftKey = key;
	}
	
	public void mapKeyAsRightShoulderButton(final int key)
	{
		this.ShoulderRightKey = key;
	}		
	
	public void setPauseKeys(int[] keys)
	{
		pauseKeys = keys;
	}	
	
	@Override
	public void storeState() {
		Kboard.storeKBState();		
	}

	@Override
	public boolean wasBackPressed() {
		return Kboard.keyPressed(this.BackButtonKey);
	}

	@Override
	public boolean wasButtonAPressed() {
		return Kboard.keyPressed(this.AButtonKey);
	}

	@Override
	public boolean wasButtonBPressed() {
		return Kboard.keyPressed(this.BButtonKey);
	}

	@Override
	public boolean wasButtonShoulderLeftPressed() 
	{
		return Kboard.keyPressed(this.ShoulderLeftKey);
	}

	@Override
	public boolean wasButtonShoulderRightPressed() 
	{
		return Kboard.keyPressed(this.ShoulderRightKey);
	}

	@Override
	public boolean wasButtonXPressed() {
		return Kboard.keyPressed(this.XButtonKey);
	}

	@Override
	public boolean wasButtonYPressed() {
		return Kboard.keyPressed(this.YButtonKey);
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
		return Kboard.keyPressed(pauseKeys);
	}

	@Override
	public boolean wasRightPressed() {
		return keyPressed(rightKeys);
	}

	@Override
	public boolean wasStartPressed() {
		return Kboard.keyPressed(this.StartButtonKey);
	}

	@Override
	public boolean wasUpPressed() {
		return Kboard.keyPressed(upKeys);		
	}
	
}

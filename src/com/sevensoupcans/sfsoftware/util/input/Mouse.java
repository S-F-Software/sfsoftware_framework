package com.sevensoupcans.sfsoftware.util.input;

public class Mouse {
	
	public static void setGrabbed(boolean b) 
	{
		org.lwjgl.input.Mouse.setGrabbed(b);
	}
}

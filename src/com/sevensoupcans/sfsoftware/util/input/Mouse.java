package com.sevensoupcans.sfsoftware.util.input;

public class Mouse {
	
	public static void setGrabbed(final boolean b) 
	{
		org.lwjgl.input.Mouse.setGrabbed(b);
	}
}

package com.sevensoupcans.sfsoftware.util.input;

public interface InputDevice {	

	public boolean isButtonADown();
	public boolean isButtonBDown();
	public boolean isButtonXDown();
	public boolean isButtonYDown();
	public boolean isUpDown();
	public boolean isDownDown();
	public boolean isLeftDown();
	public boolean isRightDown();
	
	public void storeState();
	
	public boolean wasBackPressed();
	public boolean wasButtonAPressed();
	public boolean wasButtonBPressed();
	public boolean wasButtonXPressed();
	public boolean wasButtonYPressed();	
	public boolean wasUpPressed();
	public boolean wasDownPressed();
	public boolean wasLeftPressed();
	public boolean wasRightPressed();
	public boolean wasStartPressed();
	public boolean wasPausedPressed();
	
	public void poll();
}

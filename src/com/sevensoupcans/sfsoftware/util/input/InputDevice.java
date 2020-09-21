package com.sevensoupcans.sfsoftware.util.input;

public interface InputDevice {	

	public boolean isButtonADown();
	public boolean isButtonBDown();
	public boolean isButtonShoulderLeftDown();
	public boolean isButtonShoulderRightDown();
	public boolean isButtonXDown();
	public boolean isButtonYDown();
	public boolean isDownDown();
	public boolean isLeftDown();
	public boolean isRightDown();
	public boolean isUpDown();
	public void poll();	
	public void storeState();	
	public boolean wasBackPressed();
	public boolean wasButtonAPressed();
	public boolean wasButtonBPressed();
	public boolean wasButtonShoulderLeftPressed();
	public boolean wasButtonShoulderRightPressed();
	public boolean wasButtonXPressed();
	public boolean wasButtonYPressed();
	public boolean wasDownPressed();
	public boolean wasLeftPressed();
	public boolean wasPausedPressed();
	public boolean wasRightPressed();
	public boolean wasStartPressed();
	public boolean wasUpPressed();	
}

package com.sevensoupcans.sfsoftware.util.input;

import java.util.ArrayList;
import java.util.Vector;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Controller;
import org.lwjgl.input.Controllers;

public class Gamepad implements InputDevice
{
	private static Controller[] availableDevices;
	private static int selectedController = 0;
	private static boolean isInitialized = false;
	private static Controller controller;		
	private static float lastPOVX = 0;
	private static float lastPOVY = 0;
	private static float lastXAxis = 0;
	private static float lastYAxis = 0;
	private static float deadZone = 0.25f;
	private static Vector<Integer> lastGamepadState = new Vector<Integer>();
	
	// These button mappings are specifically for the 360 type controller
	public static final int BUTTON_A = 0;
	public static final int BUTTON_B = 1;
	public static final int BUTTON_X = 2;
	public static final int BUTTON_Y = 3;
	public static final int BUTTON_BACK = 6;
	public static final int BUTTON_START = 7;
	
	static
	{
		try 
		{
			Controllers.create();
			if(Controllers.getControllerCount() > 0)
			{
				// Add all connected controllers to an ArrayList
				ArrayList<Controller> al = new ArrayList<Controller>();
				for(int i = 0; i < Controllers.getControllerCount(); i++)
				{							
					if(!(Controllers.getController(i).getButtonCount() <= 0))
					{
						al.add(Controllers.getController(i));
						System.out.println("Input device #" + i + ": " + Controllers.getController(i).getName() + " found");
					}
				}
				availableDevices = al.toArray(new Controller[al.size()]);
				setController(0);
				setInitialized(true);				
			}
		}
		catch(LWJGLException e)
		{
			e.printStackTrace();
		}
	}

	public static boolean buttonPressed(int button)
	{
		boolean b = false;
		if(isInitialized() && !(lastGamepadState.contains(button)))
		{
			b = isButtonDown(button);
		}				
		
		return b;
	}
	
	public static String getControllerName()
	{
		return controller.getName();
	}
	
	public static int getDeviceCount()
	{		
		return availableDevices.length;
	}
	
	public static int getSelectedController()
	{
		return selectedController;
	}
	
	public static void setController(int id) 
	{
		try
		{			
			controller = availableDevices[id];
			selectedController = id;
		}
		catch(ArrayIndexOutOfBoundsException e)
		{				
			e.printStackTrace();
		}
		catch(NullPointerException e)
		{
			e.printStackTrace();
		}
	}
	
	public static boolean isButtonDown(int button)
	{
		boolean b = false;
		if(isInitialized() && (button < controller.getButtonCount()))
		{
			try
			{
				b = controller.isButtonPressed(button);
			}
			catch(ArrayIndexOutOfBoundsException e)
			{				
				e.printStackTrace();
			}
		}
		
		return b;
	}

	@Override
	public void poll()
	{
		if(controller != null)
		{
			controller.poll();
		}
	}		
	
	public static boolean povXLeftPressed()
	{
		boolean b = false;
		if(isInitialized() && lastPOVX == 0)
		{
			if(controller.getPovX() < (-1 * deadZone))
			{				
				b = true;
			}		
		}
		return b;
	}	
	
	public static boolean povXRightPressed()
	{
		boolean b = false;
		if(isInitialized() && lastPOVX == 0)
		{
			if(controller.getPovX() > deadZone)
			{
				b = true;
			}		
		}
		return b;
	}

	public static boolean povYDownPressed()
	{
		boolean b = false;
		if(isInitialized() && lastPOVY == 0)
		{
			if(controller.getPovY() > deadZone)
			{
				b = true;
			}		
		}
		return b;
	}	

	public static boolean povYUpPressed()
	{
		boolean b = false;
		if(isInitialized() && lastPOVY == 0)
		{
			if(controller.getPovY() < (-1 * deadZone))
			{
				b = true;
			}		
		}
		return b;
	}	
		
	
	/**
	 * Stores the currently pressed gamepad buttons and axis states
	 */
	public static void storeGamepadState()
	{		
		if(isInitialized()) 
		{
			lastGamepadState.clear();
			lastPOVX = 0;
			lastPOVY = 0;
			lastXAxis = 0;
			lastYAxis = 0;
			
			// Store button states
			for(int i = 0; i < controller.getButtonCount(); i++)
			{
				if(controller.isButtonPressed(i))
				{		
					lastGamepadState.add(i);
				}
			}
			
			// Store Axis X & Y
			if(controller.getXAxisValue() != 0)
			{
				lastXAxis = controller.getXAxisValue();
			}		
			if(controller.getYAxisValue() != 0)
			{
				lastYAxis = controller.getYAxisValue();
			}
			
			// Store POV X & Y
			if (controller.getPovX() != 0)
			{
				lastPOVX = controller.getPovX();
			}
			if (controller.getPovY() != 0)
			{
				lastPOVY = controller.getPovY();
			}
			
		}
	}	
	
	//TODO Separate POV X Left from Axis X Left
	public static boolean xAxisLeft()
	{
		boolean b = false;
		if(isInitialized())
		{
			if(controller.getXAxisValue() < (-1 * deadZone) || controller.getPovX() < (-1 * deadZone))
			{
				b = true;
			}
		}
		return b;
	}
	
	public static boolean xAxisLeftPressed()
	{
		boolean b = false;
		if(isInitialized() && lastXAxis > (-1 * deadZone))
		{			
			if(controller.getXAxisValue() < (-1 * deadZone))
			{
				b = true;
			}		
		}
		return b;
	}	
	
	//TODO Separate POV X Right from Axis X Right	
	public static boolean xAxisRight()
	{
		boolean b = false;
		if(isInitialized())
		{
			if(controller.getXAxisValue() > deadZone || controller.getPovX() > deadZone)
			{
				b = true;
			}
		}
		return b;
	}

	public static boolean xAxisRightPressed()
	{
		boolean b = false;
		if(isInitialized() && lastXAxis < deadZone)
		{
			if(controller.getXAxisValue() > deadZone)
			{
				b = true;
			}		
		}
		return b;
	}

	//TODO Separate POV Y Down from Axis Y Down
	public static boolean yAxisDown()
	{
		boolean b = false;
		if(isInitialized())
		{
			if(controller.getYAxisValue() > deadZone || controller.getPovY() > deadZone)
			{
				b = true;
			}
		}
		return b;
	}	
	
	public static boolean yAxisDownPressed()
	{
		boolean b = false;
		if(isInitialized() && lastYAxis < deadZone)
		{
			if(controller.getYAxisValue() > deadZone)
			{
				b = true;
			}		
		}
		return b;
	}	
	
	//TODO Separate POV Y Left from Axis Y Up
	public static boolean yAxisUp()
	{
		boolean b = false;
		if(isInitialized())
		{
			if(controller.getYAxisValue() < (-1 * deadZone) || controller.getPovY() < (-1 * deadZone))
			{
				b = true;
			}
		}
		return b;
	}

	public static boolean yAxisUpPressed()
	{
		boolean b = false;
		if(isInitialized() && lastYAxis > (-1 * deadZone))
		{
			if(controller.getYAxisValue() < (-1 * deadZone))
			{
				b = true;
			}		
		}
		return b;
	}

	public static boolean isInitialized() 
	{
		return isInitialized;
	}

	private static void setInitialized(boolean isInitialized) {
		Gamepad.isInitialized = isInitialized;
	}

	@Override
	public boolean isUpDown() {
		return Gamepad.yAxisUp();
	}

	@Override
	public boolean isDownDown() {
		return Gamepad.yAxisDown();
	}

	@Override
	public boolean isLeftDown() {
		return Gamepad.xAxisLeft();
	}

	@Override
	public boolean isRightDown() {
		return Gamepad.xAxisRight();
	}

	@Override
	public boolean wasUpPressed() {
		return (Gamepad.povYUpPressed() || Gamepad.yAxisUpPressed());
	}

	@Override
	public boolean wasDownPressed() {
		return (Gamepad.povYDownPressed() || Gamepad.yAxisDownPressed());
	}

	@Override
	public boolean wasLeftPressed() {
		return (Gamepad.povXLeftPressed() || Gamepad.xAxisLeftPressed());
	}

	@Override
	public boolean wasRightPressed() {
		return (Gamepad.povXRightPressed() || Gamepad.xAxisRightPressed());
	}

	@Override
	public boolean isButtonADown() {
		return isButtonDown(Gamepad.BUTTON_A);
	}

	@Override
	public boolean isButtonBDown() {
		return isButtonDown(Gamepad.BUTTON_B);
	}

	@Override
	public boolean isButtonXDown() {
		return isButtonDown(Gamepad.BUTTON_X);
	}

	@Override
	public boolean isButtonYDown() {
		return isButtonDown(Gamepad.BUTTON_Y);
	}

	@Override
	public boolean wasButtonAPressed() {
		return buttonPressed(Gamepad.BUTTON_A);
	}

	@Override
	public boolean wasButtonBPressed() {
		return buttonPressed(Gamepad.BUTTON_B);
	}

	@Override
	public boolean wasButtonXPressed() {
		return buttonPressed(Gamepad.BUTTON_X);
	}

	@Override
	public boolean wasButtonYPressed() {
		return buttonPressed(Gamepad.BUTTON_Y);
	}

	@Override
	public boolean wasBackPressed() {
		return buttonPressed(Gamepad.BUTTON_BACK);
	}

	@Override
	public boolean wasStartPressed() {
		return buttonPressed(Gamepad.BUTTON_START);
	}

	@Override
	public void storeState() {
		Gamepad.storeGamepadState();
	}

	@Override
	public boolean wasPausedPressed() {
		return wasStartPressed();
	}
	
}

package com.sevensoupcans.sfsoftware.util.input;

import java.util.ArrayList;
import java.util.Vector;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Controller;
import org.lwjgl.input.Controllers;

public class Gamepad implements InputDevice
{
	private static final Controller[] AVAILABLE_DEVICES;
	
	static
	{
		Controller[] devices = new Controller[0];
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
						System.out.println("Input device #" + i + ": " + 
											Controllers.getController(i).getName() + " found");
					}
				}
				devices = al.toArray(new Controller[al.size()]);		
			}
		}
		catch(LWJGLException e)
		{
			e.printStackTrace();
		}
		finally
		{
			AVAILABLE_DEVICES = devices;
		}
	}
	
	public static String[] getAvailableDeviceNames()
	{
		if(AVAILABLE_DEVICES == null) return new String[0];
		
		ArrayList<String> al = new ArrayList<String>();
		for(Controller c : AVAILABLE_DEVICES)
		{
			al.add(c.getName());
		}
		return al.toArray(new String[al.size()]);
	}
	
	public static int getDeviceCount()
	{		
		return AVAILABLE_DEVICES.length;
	}
	
	private int deviceId = 0;
	private boolean isInitialized = false;
	private float lastPOVX = 0;
	private float lastPOVY = 0;
	private float lastXAxis = 0;	
	private float lastYAxis = 0;
	private float xDeadZone;
	private float yDeadZone;
	private Vector<Integer> lastGamepadState = new Vector<Integer>();
	// These button mappings are specifically for the 360 type controller
	public final int BUTTON_A = 0;
	public final int BUTTON_B = 1;
	public final int BUTTON_X = 2;	
	public final int BUTTON_Y = 3;
	public final int BUTTON_BACK = 6;	
	public final int BUTTON_START = 7;
	
	private int xAxisIndex;
	private int yAxisIndex;
	private String[] buttons = new String[0];
	private String[] rumblers = new String[0];
	
	private Controller controller;
	
	public Gamepad(int deviceId)
	{
		this.setDevice(deviceId);
		
		for(int i = 0; i < controller.getAxisCount(); i++)
		{
			if(controller.getAxisName(i).equalsIgnoreCase("x"))
			{
				xAxisIndex = i;
			}
			else if(controller.getAxisName(i).equalsIgnoreCase("y"))
			{
				yAxisIndex = i;
			}
		}
		
		xDeadZone = controller.getDeadZone(xAxisIndex);
		yDeadZone = controller.getDeadZone(yAxisIndex);	
		
		ArrayList<String> al = new ArrayList<String>();
		for(int i = 0; i < controller.getRumblerCount(); i++)
		{
			al.add(controller.getRumblerName(i));
		}
		rumblers = al.toArray(new String[al.size()]);
		
		al = new ArrayList<String>();
		for(int i = 0; i < controller.getButtonCount(); i++)
		{
			al.add(controller.getButtonName(i));
		}
		buttons = al.toArray(new String[al.size()]);				
	}
	
	public boolean buttonPressed(int button)
	{
		boolean b = false;
		if(isInitialized() && !(lastGamepadState.contains(button)))
		{
			b = isButtonDown(button);
		}				
		
		return b;
	}
	
	public String[] getButtonNames()
	{
		return buttons;
	}
	
	public String getName()
	{
		return controller.getName();
	}
	
	public String[] getRumblerNames()
	{
		return rumblers;
	}
	
	public int getSelectedController()
	{
		return deviceId;
	}
	
	@Override
	public boolean isButtonADown() {
		return isButtonDown(this.BUTTON_A);
	}

	@Override
	public boolean isButtonBDown() {
		return isButtonDown(this.BUTTON_B);
	}
	
	public boolean isButtonDown(int button)
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
	public boolean isButtonXDown() {
		return isButtonDown(this.BUTTON_X);
	}	
	
	@Override
	public boolean isButtonYDown() {
		return isButtonDown(this.BUTTON_Y);
	}

	@Override
	public boolean isDownDown() {
		return this.yAxisDown();
	}	

	public boolean isInitialized() 
	{
		return isInitialized;
	}	
		
	
	@Override
	public boolean isLeftDown() {
		return this.xAxisLeft();
	}	
	
	@Override
	public boolean isRightDown() {
		return this.xAxisRight();
	}
	
	@Override
	public boolean isUpDown() {
		return this.yAxisUp();
	}	
	
	@Override
	public void poll()
	{
		if(controller != null)
		{
			controller.poll();
		}
	}

	public boolean povXLeftPressed()
	{
		boolean b = false;
		if(isInitialized() && lastPOVX == 0)
		{
			if(controller.getPovX() < (-1 * xDeadZone))
			{				
				b = true;
			}		
		}
		return b;
	}

	public boolean povXRightPressed()
	{
		boolean b = false;
		if(isInitialized() && lastPOVX == 0)
		{
			if(controller.getPovX() > xDeadZone)
			{
				b = true;
			}		
		}
		return b;
	}	
	
	public boolean povYDownPressed()
	{
		boolean b = false;
		if(isInitialized() && lastPOVY == 0)
		{
			if(controller.getPovY() > yDeadZone)
			{
				b = true;
			}		
		}
		return b;
	}	
	
	public boolean povYUpPressed()
	{
		boolean b = false;
		if(isInitialized() && lastPOVY == 0)
		{
			if(controller.getPovY() < (-1 * yDeadZone))
			{
				b = true;
			}		
		}
		return b;
	}

	private void setDevice(int deviceId) 
	{
		try
		{			
			controller = AVAILABLE_DEVICES[deviceId];
			this.deviceId = deviceId;
		}
		catch(ArrayIndexOutOfBoundsException e)
		{				
			e.printStackTrace();
			this.setInitialized(false);
			return;
		}
		catch(NullPointerException e)
		{
			e.printStackTrace();
			this.setInitialized(false);
			return;
		}
		
		this.setInitialized(true);
	}

	private void setInitialized(boolean isInitialized) {
		this.isInitialized = isInitialized;
	}

	/**
	 * Stores the currently pressed gamepad buttons and axis states
	 */
	public void storeGamepadState()
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

	@Override
	public void storeState() {
		this.storeGamepadState();
	}

	@Override
	public boolean wasBackPressed() {
		return buttonPressed(this.BUTTON_BACK);
	}

	@Override
	public boolean wasButtonAPressed() {
		return buttonPressed(this.BUTTON_A);
	}

	@Override
	public boolean wasButtonBPressed() {
		return buttonPressed(this.BUTTON_B);
	}

	@Override
	public boolean wasButtonXPressed() {
		return buttonPressed(this.BUTTON_X);
	}

	@Override
	public boolean wasButtonYPressed() {
		return buttonPressed(this.BUTTON_Y);
	}

	@Override
	public boolean wasDownPressed() {
		return (this.povYDownPressed()); // || this.yAxisDownPressed());
	}

	@Override
	public boolean wasLeftPressed() {
		return (this.povXLeftPressed()); // || this.xAxisLeftPressed());
	}

	@Override
	public boolean wasPausedPressed() {
		return wasStartPressed();
	}

	@Override
	public boolean wasRightPressed() {
		return (this.povXRightPressed()); // || this.xAxisRightPressed());
	}

	@Override
	public boolean wasStartPressed() {
		return buttonPressed(this.BUTTON_START);
	}

	@Override
	public boolean wasUpPressed() {
		return (this.povYUpPressed());// || this.yAxisUpPressed());
	}

	//TODO Separate POV X Left from Axis X Left
	public boolean xAxisLeft()
	{
		boolean b = false;
		if(isInitialized())
		{
			if(controller.getPovX() < (-1 * xDeadZone)) // || controller.getXAxisValue() < (-1 * xDeadZone)
			{
				b = true;
			}
		}
		return b;
	}

	public boolean xAxisLeftPressed()
	{
		boolean b = false;
		if(isInitialized() && lastXAxis > (-1 * xDeadZone))
		{			
			if(controller.getXAxisValue() < (-1 * xDeadZone))
			{
				b = true;
			}		
		}
		return b;
	}

	//TODO Separate POV X Right from Axis X Right	
	public boolean xAxisRight()
	{
		boolean b = false;
		if(isInitialized())
		{
			if(controller.getPovX() > xDeadZone) // || controller.getXAxisValue() > xDeadZone
			{
				b = true;
			}
		}
		return b;
	}

	public boolean xAxisRightPressed()
	{
		boolean b = false;
		if(isInitialized() && lastXAxis < xDeadZone)
		{
			if(controller.getXAxisValue() > xDeadZone)
			{
				b = true;
			}		
		}
		return b;
	}

	//TODO Separate POV Y Down from Axis Y Down
	public boolean yAxisDown()
	{
		boolean b = false;
		if(isInitialized())
		{
			if(controller.getPovY() > yDeadZone) // || controller.getYAxisValue() > yDeadZone
			{
				b = true;
			}
		}
		return b;
	}

	public boolean yAxisDownPressed()
	{
		boolean b = false;
		if(isInitialized() && lastYAxis < yDeadZone)
		{
			if(controller.getYAxisValue() > yDeadZone)
			{
				b = true;
			}		
		}
		return b;
	}

	//TODO Separate POV Y Left from Axis Y Up
	public boolean yAxisUp()
	{
		boolean b = false;
		if(isInitialized())
		{
			if(controller.getPovY() < (-1 * yDeadZone)) // || controller.getYAxisValue() < (-1 * yDeadZone)
			{
				b = true;
			}
		}
		return b;
	}

	public boolean yAxisUpPressed()
	{
		boolean b = false;
		if(isInitialized() && lastYAxis > (-1 * yDeadZone))
		{
			if(controller.getYAxisValue() < (-1 * yDeadZone))
			{
				b = true;
			}		
		}
		return b;
	}
	
}

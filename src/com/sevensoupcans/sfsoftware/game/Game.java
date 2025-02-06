package com.sevensoupcans.sfsoftware.game;

import java.awt.Font;
import java.util.ArrayList;
import java.util.prefs.Preferences;
import java.util.stream.Collectors;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Controllers;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.newdawn.slick.openal.SoundStore;

import com.sevensoupcans.sfsoftware.util.Clock;
import com.sevensoupcans.sfsoftware.util.audio.Sound;
import com.sevensoupcans.sfsoftware.util.graphics.Graphics;
import com.sevensoupcans.sfsoftware.util.graphics.RGBA;
import com.sevensoupcans.sfsoftware.util.graphics.Sprite;
import com.sevensoupcans.sfsoftware.util.graphics.TextureFont;
import com.sevensoupcans.sfsoftware.util.graphics.geometry.Quad;
import com.sevensoupcans.sfsoftware.util.input.InputDevice;
import com.sevensoupcans.sfsoftware.util.input.Kboard;
import com.sevensoupcans.sfsoftware.util.input.Mouse;
import com.sevensoupcans.sfsoftware.util.resources.FileUtils;
import com.sevensoupcans.sfsoftware.util.tile.Tile;
import com.sevensoupcans.sfsoftware.util.tile.TileMap;
import com.sevensoupcans.sfsoftware.util.ui.GUIElement;
import com.sevensoupcans.sfsoftware.util.ui.LoggedList;
import com.sevensoupcans.sfsoftware.util.ui.TextConsole;

public abstract class Game 
{
	protected final TextConsole console = new TextConsole(this);		
	
	private GameState gameState = GameState.TITLE_SCREEN;
	private int screenHeight = 864; //600;
	private int screenWidth = 1152; //800;	
	private long gameCounter = 0;
	private boolean running = true;
	private String gameTitle;
	private ArrayList<GUIElement> guiElements = new ArrayList<GUIElement>();
	
	protected boolean debugMode;	
	
	// Default input device will always be keyboard.
	protected InputDevice inputDevice = new Kboard();
	
	public boolean isPaused = false;			
	public boolean vsync = false;
	
	private final Preferences PREFERENCES = Preferences.userNodeForPackage(this.getClass());

	public abstract boolean executeTextConsoleCommand(final String command);
	public abstract int getPlayingFieldHeight();
	public abstract int getPlayingFieldWidth();
	public abstract String getDefaultFontName();
	public abstract TextureFont getGameFont();
	public abstract TileMap getTileMap();
	protected abstract void start();	
	
	public final void addGUIElement(GUIElement e)
	{
		this.guiElements.add(e);
	}
	
	/**
	 * Handles drawing of the loading screen
	 * 
	 * @param loadingDetails An ArrayList containing loading detail text
	 * @param font The TextureFont used to handle drawing the detail text
	 */
	public final void drawLoadingScreen(final ArrayList<String> loadingDetails, final TextureFont font)
	{
		Graphics.clear();
		
		int size = loadingDetails.size();
		for(String s : loadingDetails)
		{
			font.drawString(35, (getScreenHeight() - 60) - (font.getHeight() * (size - 1)), s);
			size--;
		}
		Quad.draw(0, 0, getScreenWidth(), (getScreenHeight() / 2), 0, 0, 0, 1);
		Quad.draw(0, (getScreenHeight() / 2), getScreenWidth(), (getScreenHeight() / 2), new RGBA(0,0,0,1), new RGBA(0,0,0,1), new RGBA(0,0,0,0), new RGBA(0,0,0,0));
		Graphics.update();
	}	
	
	protected final void end()
	{
		Graphics.destroy();
		Sound.destroy();
		Controllers.destroy();
		
		System.out.println("Bye, bye!");
		System.exit(0);
	}
	
	protected final String getClassOriginPath()
	{
		String path = this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
		if(path.indexOf("bin/") > -1)
    	{
    		path = path.substring(0, path.indexOf("bin/"));    		
    	}
		
		// Windows issue.
		if(FileUtils.getDirectorySeparator() == '\\' && path.substring(0, 1).equalsIgnoreCase("/"))
		{
			path = path.substring(1, path.length());
		}
		
		return path;
	}
	
	public final String getGameCounter()
	{
		return Clock.getFormattedHoursMinutes(gameCounter);
	}
	
	public final GameState getGameState()
	{
		return gameState;
	}
	
	protected final ArrayList<GUIElement> getGUIElements()
	{
		return this.guiElements;
	}
	
	public final InputDevice getInputDevice()
	{
		return inputDevice;
	}
	
	public final Preferences getPreferences()
	{	
		return PREFERENCES;
	}	
	
	protected String getResourcePath()
	{
		return getClassOriginPath() + "res/"; 
	}	
	
	public final int getScreenHeight()
	{
		return screenHeight;
	}
	
	public final int getScreenWidth()
	{
		return screenWidth;
	}		
	
	public int getTileSize()
	{
		return Tile.getDefaultTileSize();
	}
	
	public final int getGameHashCode()
	{
		return gameTitle.hashCode();
	}
	
	public final String getGameTitle()
	{
		return gameTitle;
	}
	
	public final String incrementGameCounter()
	{
		gameCounter++;
		return getGameCounter();
	}

	public final boolean inDebugMode()
	{
		return debugMode;
	}	
	
	protected void init()
	{
		init(false);
	}
	
	protected void init(final int screenWidth, final int screenHeight, final boolean loadResources)
	{
		this.screenHeight = screenHeight;
		this.screenWidth = screenWidth;
		
		init(loadResources);
		
	}
	
	protected void init(final boolean loadResources)
	{
		System.out.println(getGameTitle() + ", (c) S&F Software, 2020");
		Graphics.setAppIcon(getResourcePath() + "graphics/icon16.png", getResourcePath() + "graphics/icon32.png");
		Graphics.initGL(getScreenWidth(), getScreenHeight(), getGameTitle());					
		Graphics.setDisplayMode(getScreenWidth(), getScreenHeight(), getPreferences().getBoolean("fullscreen", false));
		vsync = getPreferences().getBoolean("vsync", false);
		
		Mouse.setGrabbed(Graphics.isFullScreen());
		
		setGameState(GameState.TITLE_SCREEN);
		
		if(loadResources)
		{
			TextureFont loadingFont = new TextureFont("Verdana", Font.BOLD, 20);	
			LoggedList<String> loadingText = new LoggedList<String>();
			
			loadingText.add("Loading " + getGameTitle() + "...");
			drawLoadingScreen(loadingText, loadingFont);
			
			loadingText.add("Loading textures...");
			drawLoadingScreen(loadingText, loadingFont);		
			Graphics.loadTextures(getClassOriginPath() + Graphics.DEFAULT_GRAPHICS_FILE_PATH, "png", this.getClass());
			
			loadingText.add("Loading audio...");
			drawLoadingScreen(loadingText, loadingFont);
			Sound.loadAudio(getClassOriginPath() + Sound.AUDIO_PATH, this.getClass());
		}
	}
	
	public final boolean isRunning()
	{
		return running;
	}
	
	public void newGame()
	{
		Sound.stopMusic();
		
		setGameState(GameState.INGAME);
	}
	
	public final void removeGUIElement(GUIElement e)
	{
		this.guiElements.remove(e);
	}	
	
	/**
	 * Set the display mode to be used 
	 * 
	 * @param width The width of the display required
	 * @param height The height of the display required
	 * @param fullscreen True if we want fullscreen mode
	 */
	public final static void setDisplayMode(final int width, final int height, final boolean fullscreen) 
	{		
	    // return if requested DisplayMode is already set
	    if ((Display.getDisplayMode().getWidth() == width) && 
	        (Display.getDisplayMode().getHeight() == height) && 
		(Display.isFullscreen() == fullscreen)) {
		    return;
	    }

	    try {
	        DisplayMode targetDisplayMode = null;
			
		if (fullscreen) {
		    DisplayMode[] modes = Display.getAvailableDisplayModes();
		    int freq = 0;
					
		    for (int i=0;i<modes.length;i++) {
		        DisplayMode current = modes[i];
						
			if ((current.getWidth() == width) && (current.getHeight() == height)) {
			    if ((targetDisplayMode == null) || (current.getFrequency() >= freq)) {
			        if ((targetDisplayMode == null) || (current.getBitsPerPixel() > targetDisplayMode.getBitsPerPixel())) {
				    targetDisplayMode = current;
				    freq = targetDisplayMode.getFrequency();
	                        }
	                    }

			    // if we've found a match for bpp and frequence against the 
			    // original display mode then it's probably best to go for this one
			    // since it's most likely compatible with the monitor
			    if ((current.getBitsPerPixel() == Display.getDesktopDisplayMode().getBitsPerPixel()) &&
	                        (current.getFrequency() == Display.getDesktopDisplayMode().getFrequency())) {
	                            targetDisplayMode = current;
	                            break;
	                    }
	                }
	            }
	        } else {
	            targetDisplayMode = new DisplayMode(width,height);
	        }

	        if (targetDisplayMode == null) {
	            System.out.println("Failed to find value mode: "+width+"x"+height+" fs="+fullscreen);
	            return;
	        }

	        Display.setDisplayMode(targetDisplayMode);
	        Display.setFullscreen(fullscreen);
				
	    } catch (LWJGLException e) {
	        System.out.println("Unable to setup mode "+width+"x"+height+" fullscreen="+fullscreen + e);
	    }
	}		
	
	/**
	 * Sets the state of the game based on the provided GameState
	 * @param s
	 */
	public final void setGameState(final GameState s)
	{
		gameState = s;
	}
	
	/**
	 * Sets the game title
	 * @param s
	 */
	protected final void setGameTitle(final String s)
	{
		gameTitle = s;
	}
	
	/**
	 * Sets the game's InputDevice
	 * @param device
	 */
	public final void setInputDevice(final InputDevice device)
	{
		this.inputDevice = device;
	}
	
	/**
	 * Sets the boolean indicating the game is running
	 * @param arg0
	 */
	public final void setRunning(final boolean arg0)
	{
		running = arg0;
	}		
	
	protected void updateAndDrawGUIElements()
	{
		this.getGUIElements().forEach(e -> {
			e.update();
			e.draw();
		});
		
		this.getGUIElements().removeAll(this.getGUIElements()
												.stream()
												.filter(e -> e.shouldRemove())
												.collect(Collectors.toList()));
	}
	
	/**
	 * Displays the S&F Software logo
	 */
	protected void updateLogo()
	{
		int logoState = 0;
		int logoWait = 0;
		float logoAlpha = 0;
		
		Sound.playMusic("logo", false);
		
		while (logoState != 2)
		{			
			Graphics.clear();
			
			if(logoState == 0)
			{
				if(logoAlpha < 1)
				{
					logoAlpha = logoAlpha + 0.02f;
				}
				else
				{			
					if(logoWait < 50)
					{
						logoWait++;
					}else
					{
						logoState++;
					}
				}
			}
			else if(logoState == 1)
			{
				if(logoAlpha > 0)
				{
					logoAlpha = logoAlpha - 0.005f;
				}
				else
				{
					logoState++;
				}				
			}

			// TODO Possibly want to allow more flexibility here - this is displaying the "classic" logo
			Sprite.draw((this.getScreenWidth() / 2) - 44, (this.getScreenHeight() / 2) - 44, "sflogo", 
					89, 89, 0, 0, 89, 89, 1, 1, 1, logoAlpha);
			
			SoundStore.get().poll(0);
			Display.update();
			Display.sync(60);
		}
		
		Sound.stopMusic();
	}	
	
}

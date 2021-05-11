package com.sevensoupcans.sfsoftware.util.graphics;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashMap;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GLContext;
import org.newdawn.slick.opengl.PNGDecoder;
import org.newdawn.slick.util.ResourceLoader;

import com.sevensoupcans.sfsoftware.util.graphics.geometry.Quad;
import com.sevensoupcans.sfsoftware.util.resources.ClasspathHelper;
import com.sevensoupcans.sfsoftware.util.resources.FileUtils;

public abstract class Graphics 
{
	public static final String DEFAULT_GRAPHICS_FILE_PATH = "res/graphics";
	
	private static boolean verbose = false;
	private static int displayHeight = 0;
	private static int displayWidth = 0;
	public static int shaderProgram;
	private static String[] maskTextures;	
	
	private static long window;	
	private static FrameBuffer primaryDisplayBuffer;
	private static FrameBuffer currentDisplayBuffer;
	
	public static FrameBuffer getCurrentDisplayBuffer()
	{
		return currentDisplayBuffer;
	}
	
	public static long getWindow()
	{
		return window;
	}

	public static int getScreenHeight()
	{
		return Display.getDisplayMode().getHeight();
	}
	
	public static int getScreenWidth()
	{
		return Display.getDisplayMode().getWidth();
	}
	
	/**
	 * 	Clears the screen and depth buffers
	 */
	public static void clear()
	{
		GL11.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
	}
    
    public static void destroy()
    {
    	Display.destroy();
    }

	public static void drawDisplayBuffer(int x, int y, int width, int height)
	{
		drawDisplayBuffer(primaryDisplayBuffer, x, y, width, height);
	}
	
	public static void drawDisplayBuffer(FrameBuffer fb, int x, int y, int width, int height)
	{				
		drawDisplayBuffer(fb, x, y, width, height, 0, 0, fb.getWidth(), fb.getHeight());
	}
	
	public static void drawDisplayBuffer(FrameBuffer fb, int x, int y, int width, int height, int srcX, int srcY, 
			int srcWidth, int srcHeight)
	{
		drawDisplayBuffer(fb, x, y, width, height, srcX, srcY, srcWidth, srcHeight, 1.0f, 1.0f, 1.0f, 1.0f);
	}
	
	public static void drawDisplayBuffer(FrameBuffer fb, int x, int y, int width, int height, int srcX, int srcY, 
			int srcWidth, int srcHeight, float red, float green, float blue, float alpha)
	{
		setBuffer(null);
		fb.draw(x, y, width, height, srcX, srcY, srcWidth, srcHeight, red, green, blue, alpha);
	}
	
	public static void drawEllipse(float x, float y, float radiusX, float radiusY, float r, float g, float b, float a)
	{
		drawEllipse(x, y, radiusX, radiusY, r, g, b, a, 100);
	}
	
	public static void drawEllipse(float x, float y, float radiusX, float radiusY, float r, float g, float b, float a, int segments)
	{
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glColor4f(r,g,b,a);	

		GL11.glBegin(GL11.GL_TRIANGLE_FAN);
	    GL11.glVertex2f(x, y); // Center of the circle
	    
	    for (int i = 0; i <= segments; i++) // Last vertex same as first vertex 
	    {
	         double angle = i * 2.0 * Math.PI / segments;  // 360 deg for all segments
	         GL11.glVertex2f(x + (float)Math.cos(angle) * radiusX, y + (float) Math.sin(angle) * radiusY);
	    }
	    
		GL11.glEnd();		
		GL11.glEnable(GL11.GL_TEXTURE_2D);
	}	
	
	public static void drawRect(float x, float y, float width, float height, RGBA rgba, int thickness)
	{
		drawRect(x, y, width, height, rgba.getRed(), rgba.getGreen(), rgba.getBlue(), rgba.getAlpha(), thickness);
	}
	
	public static void drawRect(float x, float y, float width, float height, float r, float g, float b, float a, int thickness)
	{
		Quad.draw(x, y, thickness, height, r, g, b, a);
		Quad.draw(x + (width - thickness), y, thickness, height, r, g, b, a);		
		Quad.draw(x + thickness, y, width - thickness, thickness, r, g, b, a);
		Quad.draw(x + thickness, y + (height - thickness), width - thickness, thickness, r, g, b, a);				
	}

	public static boolean isCloseRequested()
	{
		return Display.isCloseRequested();
	}
	
	/**
	 * Initialize the GL display
	 * 
	 * @param width The width of the display
	 * @param height The height of the display
	 */
	public static void initGL(int width, int height, String windowTitle) 
	{				
		try 
		{
			Display.setTitle(windowTitle);
			//Display.setIcon(arg0)
			Display.setDisplayMode(new DisplayMode(width, height));
			Display.create();
		} 
		catch (LWJGLException e) 
		{
			e.printStackTrace();
			System.exit(0);
		}
 
		GL11.glEnable(GL11.GL_TEXTURE_2D);               
 
		GL11.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);          
 
        // enable alpha blending
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
 
        GL11.glViewport(0,0,width,height);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
 
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		GL11.glOrtho(0, width, height, 0, 1, -1);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		
		setDisplayWidth(width);
		setDisplayHeight(height);
		
		// check if GL_EXT_framebuffer_object can be use on this system
		if (!GLContext.getCapabilities().GL_EXT_framebuffer_object) 
		{
			System.out.println("FBO not supported!!!");
			System.exit(0);
		}
		else 
		{			
			primaryDisplayBuffer = new FrameBuffer(width, height);
		}
		
	}   	

	/**
	 * Set the display mode to be used 
	 * 
	 * @param width The width of the display required
	 * @param height The height of the display required
	 * @param fullscreen True if we want fullscreen mode
	 */
	public static void setDisplayMode(int width, int height, boolean fullscreen) 
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

	public static HashMap<String, Texture> loadTextures(Class<?> invokingClass)
	{
		return loadTextures(DEFAULT_GRAPHICS_FILE_PATH, invokingClass);
	}

	public static HashMap<String, Texture> loadTextures(String texturePath, Class<?> invokingClass)
	{	
		return loadTextures(texturePath, "png", null, invokingClass);
	}
	
	public static HashMap<String, Texture> loadTextures(String texturePath, String filetype, Class<?> invokingClass)
	{
		return loadTextures(texturePath, filetype, null, invokingClass);
	}
	
	public static HashMap<String, Texture> loadTextures(String texturePath, String filetype, RGBA transparentColor, Class<?> invokingClass)
	{
		try 
		{			 	 	 
			   String files;			   			  
			   String[] listOfFiles = FileUtils.getResourceListing(texturePath, filetype, ClasspathHelper.getClasspath(invokingClass));
			   
			   for (int i = 0; i < listOfFiles.length; i++) 
			   {				   	
					files = listOfFiles[i];
					if (files.endsWith("." + filetype.toLowerCase()) || files.endsWith("." + filetype.toUpperCase()))
					{							
						String textureName = files.substring(0, files.lastIndexOf(".")).trim();
						
						if(transparentColor != null)
						{
							new Texture(texturePath + "/" + files, transparentColor);
						}
						else
						{
							new Texture(texturePath + "/" + files);
						}
												
						if(maskTextures.length > 0 && (Arrays.asList(maskTextures)).contains(textureName))
						{							
							String maskTextureName = "_" + textureName;							
							new Texture(texturePath + "/" + files, transparentColor, RGBA.WHITE);							
							
							if(verbose)
							{
								System.out.println("Loaded texture '" + maskTextureName + "'");
							}							
						}
										           
						if(verbose)
						{
							System.out.println("Loaded texture '" + textureName + "'");
						}
					}			        
			   }				          
		} 
		catch (IOException | URISyntaxException e) 
		{
			e.printStackTrace();
		}	
		
		return Texture.getLoadedTextures();
	}
	
	/*	Thanks for this one goes out to
	/	http://www.thehelper.net/threads/java-lwjgl-opengl-display-seticon-question.156958/
	*/
	private static ByteBuffer loadIcon(final String path) throws IOException 
	{
        InputStream inputStream = ResourceLoader.getResourceAsStream(path); //new FileInputStream(path);
        try 
        {
            PNGDecoder decoder = new PNGDecoder(inputStream);
            ByteBuffer bytebuf = ByteBuffer.allocateDirect(decoder.getWidth()*decoder.getHeight()*4);
            decoder.decode(bytebuf, decoder.getWidth()*4, PNGDecoder.Format.RGBA);
            bytebuf.flip();
            return bytebuf;
        } 
        finally 
        {
            inputStream.close();
        }
    }	
	
	/**
	 * Sets the display icons from two file paths provided.
	 * 
	 * @param icon16	16x16 version of the app icon
	 * @param icon32	32x32 verison of the app icon
	 */
	public static void setAppIcon(final String icon16, final String icon32)
	{ 
		try 
		{
			Display.setIcon(new ByteBuffer[] {loadIcon(icon16), loadIcon(icon32)});
		} 
		catch (IOException e) 
		{		
			e.printStackTrace();
		}		
		
	}	
	
	/**
	 * Sets the buffer to draw to - 0 is the main buffer.
	 * 
	 * @param bufferId
	 * @param bufferWidth
	 * @param bufferHeight
	 * @param clearBuffer
	 */
	private static void setBuffer(final int bufferId, final int bufferWidth, final int bufferHeight, final boolean clearBuffer)
	{
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, bufferId);
        GL11.glViewport(0, 0, bufferWidth, bufferHeight);
		
		if(clearBuffer)
			Graphics.clear();	
	}	
	
	/**
	 * 
	 * @param fbo
	 * @param clearBuffer
	 */
	public static void setBuffer(final FrameBuffer fbo, final boolean clearBuffer)
	{		
		if(fbo != null)
		{
			setBuffer(fbo.getId(), fbo.getWidth(), fbo.getHeight(), clearBuffer);
		}
		else
		{
			setBuffer(0, Display.getDisplayMode().getWidth(), Display.getDisplayMode().getHeight(), clearBuffer);
		}
		
		currentDisplayBuffer = fbo;
	}
	
	/**
	 * 
	 * @param fbo
	 */
	public static void setBuffer(final FrameBuffer fbo)
	{
		setBuffer(fbo, false);
	}
	
	public static void setMaskTextureList(final String[] textureList)
	{
		maskTextures = textureList;
	}
	
	/**
	 * Changes how GL space is scaled - useful if gamefield is 640x480, but monitor doesn't support that low
	 * 
	 * @param xScaler
	 * @param yScaler
	 */
	public static void setScaledDisplay(final double xScaler, final double yScaler)
	{
		GL11.glOrtho(0, getDisplayWidth() / xScaler, getDisplayHeight() / yScaler, 0, 1, -1);
	}
    
    public static FrameBuffer getPrimaryDisplayBuffer()
    {
    	return primaryDisplayBuffer;
    }
    
    public static boolean isFullScreen()
    {
    	return Display.isFullscreen();
    }
    
    public static void sync(final int fps)
    {
    	Display.sync(fps);
    }
    
    public static void update()
    {
    	Display.update();
    }
    
    public static void update(final boolean sync)
    {
    	update();
    	if(sync) sync(60);
    }
    
	private static int getDisplayHeight() {
		return displayHeight;
	}

	private static void setDisplayHeight(final int displayHeight) {
		Graphics.displayHeight = displayHeight;
	}

	private static int getDisplayWidth() {
		return displayWidth;
	}

	private static void setDisplayWidth(final int displayWidth) {
		Graphics.displayWidth = displayWidth;
	}	
	
	public static void setVSync(final boolean vsync)
	{
		Display.setVSyncEnabled(vsync);
	}

}

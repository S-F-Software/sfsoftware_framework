package com.sevensoupcans.sfsoftware.util.graphics;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashMap;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;
import org.newdawn.slick.opengl.PNGDecoder;
import org.newdawn.slick.util.ResourceLoader;

import com.sevensoupcans.sfsoftware.util.resources.ClasspathHelper;
import com.sevensoupcans.sfsoftware.util.resources.FileUtils;

import static org.lwjgl.opengl.EXTFramebufferObject.*;

public abstract class Graphics 
{
	public static final RGBA RED = new RGBA(1.0f, 0.0f, 0.0f, 1.0f);
	public static final RGBA GREEN = new RGBA(0.0f, 1.0f, 0.0f, 1.0f);
	public static final RGBA BLUE = new RGBA(0.0f, 0.0f, 1.0f, 1.0f);
	public static final RGBA CYAN = new RGBA(0.0f, 1.0f, 1.0f, 1.0f);
	public static final RGBA YELLOW = new RGBA(1.0f, 1.0f, 0.0f, 1.0f);
	public static final RGBA WHITE = new RGBA(1.0f, 1.0f, 1.0f, 1.0f);
	public static final RGBA BLACK = new RGBA(0.0f, 0.0f, 0.0f, 1.0f);	
	public static final RGBA DEFAULT_TRANSPARENT_COLOR = new RGBA(1.0f, 0.0f, 1.0f, 1.0f);
	public static final String GRAPHICS_FILE_PATH = "res/graphics";
	
	private static boolean verbose = false;
	//private static boolean useShader = false;
	private static int displayHeight = 0;
	private static int displayWidth = 0;
	//private static int currentShader;
	public static int shaderProgram;
	private static HashMap <String, Texture> texture = new HashMap<String, Texture>();
	private static String[] maskTextures;	
	@SuppressWarnings("unused")
	private static HashMap <String, Integer> shaders = new HashMap<String, Integer>();
	
	private static long window;
	
	// FrameBuffer Object Related	
	private static FrameBuffer displayBuffer;
	//private static int currentBufferID = 0;
	
	/**
	* Get the closest greater power of 2 to the fold number
	* 
	* @param fold The target number
	* @return The power of 2
	*/
	@SuppressWarnings("unused")
	private static int get2Fold(int fold) 
	{
		int ret = 2;
		while (ret < fold) 
		{
			ret *= 2;
		}
		return ret;
	}
	
	public static long getWindow()
	{
		return window;
	}
	
	/**
	 * 	Clears the screen and depth buffers
	 */
	public static void clear()
	{
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
	}
	
    /*
     * 	http://lwjgl.org/wiki/index.php?title=GLSL_Shaders_with_LWJGL example!
     * 
     * 	With the exception of syntax, setting up vertex and fragment shaders
     * 	is the same.
     * 	@param the name and path to the vertex shader
    */
    @SuppressWarnings("unused")
	private static int createShader(String filename, int shaderType) throws Exception 
    {
    	int shader = 0;
    	try 
    	{
	        shader = ARBShaderObjects.glCreateShaderObjectARB(shaderType);
	        
	        if(shader == 0)
	        	return 0;
	        
	        ARBShaderObjects.glShaderSourceARB(shader, readFileAsString(filename));
	        ARBShaderObjects.glCompileShaderARB(shader);
	        
	        if (ARBShaderObjects.glGetObjectParameteriARB(shader, ARBShaderObjects.GL_OBJECT_COMPILE_STATUS_ARB) == GL11.GL_FALSE)
	            throw new RuntimeException("Error creating shader: " + getLogInfo(shader));
	        
	        return shader;
    	}
    	catch(Exception exc) {
    		ARBShaderObjects.glDeleteObjectARB(shader);
    		throw exc;
    	}
    }	
	
    public static boolean isTextureLoaded(String textureName)
    {
    	return texture.containsKey(textureName);
    }
    
    public static void destroy()
    {
    	Display.destroy();
    }
    
	public static void drawCircle(float x, float y, float radius, float r1, float g1, float b1, float a1, float r2, float g2, float b2, float a2)
	{
		RGBA inner = new RGBA(r1, g1, b1, a1);
		RGBA outer = new RGBA(r2, g2, b2, a2);
		drawCircle(x, y, radius, inner, outer, 100);
	}	
	
	public static void drawCircle(float x, float y, float radius, float r, float g, float b, float a)
	{
		RGBA rgba = new RGBA(r, g, b, a);
		drawCircle(x, y, radius, rgba, rgba, 100);
	}
	
	public static void drawCircle(float x, float y, float radius, RGBA innerRGBA, RGBA outerRGBA, int segments)
	{		
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glColor4f(innerRGBA.getRed(), innerRGBA.getGreen(), innerRGBA.getBlue(), innerRGBA.getAlpha());	

		GL11.glBegin(GL11.GL_TRIANGLE_FAN);
	    GL11.glVertex2f(x, y); // Center of the circle
	    
	    GL11.glColor4f(outerRGBA.getRed(), outerRGBA.getGreen(), outerRGBA.getBlue(), outerRGBA.getAlpha());
	    
	    for (int i = 0; i <= segments; i++) // Last vertex same as first vertex 
	    {
	         double angle = i * 2.0 * Math.PI / segments;  // 360 deg for all segments
	         GL11.glVertex2f(x + (float)Math.cos(angle) * radius, y + (float) Math.sin(angle) * radius);
	    }
		
		GL11.glEnd();		
		GL11.glEnable(GL11.GL_TEXTURE_2D);
	}

	public static void drawCircleOutline(float x, float y, float radius, float r, float g, float b, float a)
	{
		drawCircleOutline(x, y, radius, r, g, b, a, 100, true);
	}	
	
	public static void drawCircleOutline(float x, float y, float radius, float r, float g, float b, float a, int segments, boolean smooth)
	{
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glColor4f(r,g,b,a);	

		GL11.glBegin(GL11.GL_LINE_LOOP);
		
		if(smooth)
		{
			//GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			//GL11.glEnable(GL11.GL_LINE_SMOOTH);
		}
	   		
		for (int i = 0; i <= segments; i++) // Last vertex same as first vertex 
    	{
			double angle = i * 2.0 * Math.PI / segments;  // 360 deg for all segments
			GL11.glVertex2f(x + (float)Math.cos(angle) * radius, y + (float) Math.sin(angle) * radius);
    	}
	
	    if(smooth)
	    {
	    	//GL11.glDisable(GL11.GL_LINE_SMOOTH);
	    }
	    		
		GL11.glEnd();						
		GL11.glEnable(GL11.GL_TEXTURE_2D);
	}

	public static void drawDisplayBuffer(int x, int y, int width, int height)
	{				
		drawDisplayBuffer(x, y, width, height, 0, 0, displayBuffer.getWidth(), displayBuffer.getHeight());
	}
	
	public static void drawDisplayBuffer(int x, int y, int width, int height, int srcX, int srcY, int srcWidth, int srcHeight)
	{
		setBuffer(null);		
		displayBuffer.draw(x, y, width, height, srcX, srcY, srcWidth, srcHeight);
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
	
	public static void drawQuad(float x, float y, float width, float height, RGBA rgba)
	{	
		drawQuad(x, y, width, height, rgba.getRed(), rgba.getGreen(), rgba.getBlue(), rgba.getAlpha());
	}
	
	public static void drawQuad(float x, float y, float width, float height, float r, float g, float b, float a)
	{	
		// set the color of the quad (R,G,B,A)
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glColor4f(r,g,b,a);		
	    
	    // draw quad
	    GL11.glBegin(GL11.GL_QUADS);
	    
	    GL11.glVertex2f(x,y);
		GL11.glVertex2f(x+width,y);
		GL11.glVertex2f(x+width,y+height);
		GL11.glVertex2f(x,y+height);
	    
		GL11.glEnd();
		
		GL11.glEnable(GL11.GL_TEXTURE_2D);
	}
	
	public static void drawQuad(float x, float y, float width, float height, RGBA topLeft, RGBA topRight, RGBA bottomLeft, RGBA bottomRight)
	{	
		// set the color of the quad (R,G,B,A)
		GL11.glDisable(GL11.GL_TEXTURE_2D);		
	    
	    // draw quad
	    GL11.glBegin(GL11.GL_QUADS);
	    
		GL11.glColor4f(topLeft.getRed(), topLeft.getGreen(), topLeft.getBlue(), topLeft.getAlpha());
	    GL11.glVertex2f(x,y);
		GL11.glColor4f(topRight.getRed(), topRight.getGreen(), topRight.getBlue(), topRight.getAlpha());
		GL11.glVertex2f(x+width,y);
		GL11.glColor4f(bottomRight.getRed(), bottomRight.getGreen(), bottomRight.getBlue(), bottomRight.getAlpha());
		GL11.glVertex2f(x+width,y+height);
		GL11.glColor4f(bottomLeft.getRed(), bottomLeft.getGreen(), bottomLeft.getBlue(), bottomLeft.getAlpha());
		GL11.glVertex2f(x,y+height);
	    
		GL11.glEnd();
		
		GL11.glEnable(GL11.GL_TEXTURE_2D);
	}	
	
	public static void drawRect(float x, float y, float width, float height, RGBA rgba, int thickness)
	{
		drawRect(x, y, width, height, rgba.getRed(), rgba.getGreen(), rgba.getBlue(), rgba.getAlpha(), thickness);
	}
	
	public static void drawRect(float x, float y, float width, float height, float r, float g, float b, float a, int thickness)
	{
		drawQuad(x, y, thickness, height, r, g, b, a);
		drawQuad(x + (width - thickness), y, thickness, height, r, g, b, a);		
		drawQuad(x + thickness, y, width - thickness, thickness, r, g, b, a);
		drawQuad(x + thickness, y + (height - thickness), width - thickness, thickness, r, g, b, a);				
	}
	
	public static void drawSprite(float x, float y, String textureName, int width, int height, int srcX, int srcY, int srcWidth, int srcHeight)
	{
		drawSprite(x,y,textureName,width,height,srcX,srcY,srcWidth,srcHeight, 1, 1, 1, 1);
	}
	
	/**
	 * draw a quad with the image on it
	 */
	public static void drawSprite(float x, float y, String textureName, int width, int height, int srcX, int srcY, int srcWidth, int srcHeight, float red, float green, float blue, float alpha) 
	{
		drawSprite(x, y, textureName, width, height, srcX, srcY, srcWidth, srcHeight, red, green, blue, alpha, 0);
	}
	
	public static void drawSprite(float x, float y, Texture texture, int width, int height, int srcX, int srcY, int srcWidth, int srcHeight, float red, float green, float blue, float alpha, float angle)
	{
		// Bind the current texture to the current shader if one is in use.
		/*if(useShader)
		{
			setShaderUniform(currentShader, "texture", getTextureId(temp));
		}*/
		
		texture.bind();								
		
		float fSrcX = ((float)srcX / texture.getWidth());
		float fSrcY = ((float)srcY / texture.getHeight());
		float fSrcWidth = (((float)srcX + (float)srcWidth) / texture.getWidth());
		float fSrcHeight = (((float)srcY + (float)srcHeight) / texture.getHeight());			
		
		GL11.glPushMatrix();				
						
		// Rotation works! 1/4/14
		if(angle != 0)
		{
			GL11.glTranslatef(x + (width / 2), y + (height / 2), 0); // move to the proper position
			GL11.glRotatef(angle, 0, 0, 1); // now rotate
			GL11.glTranslatef(-1 *(x+ (width / 2)), -1 * (y+(height  / 2)), 0);				
		}
		
		GL11.glColor4f(red, green, blue, alpha);
		GL11.glBegin(GL11.GL_QUADS);
			// Top Left
			GL11.glTexCoord2f(fSrcX, fSrcY);
			GL11.glVertex2f(x,y);
			// Top Right
			GL11.glTexCoord2f(fSrcWidth, fSrcY);
			GL11.glVertex2f(x + width, y);
			// Bottom Right
			GL11.glTexCoord2f(fSrcWidth,fSrcHeight);
			GL11.glVertex2f(x + width,y + height);
			// Bottom Left
			GL11.glTexCoord2f(fSrcX,fSrcHeight);
			GL11.glVertex2f(x,y + height);
		GL11.glEnd();
		
		GL11.glPopMatrix();
	}
	
	/**
	 * draw a quad with the image on it - accept float for rotation!
	 */
	public static void drawSprite(float x, float y, String textureName, int width, int height, int srcX, int srcY, int srcWidth, int srcHeight, float red, float green, float blue, float alpha, float angle) 
	{		
		// If the provided texture string is null or empty, don't try to draw it. :)
		if(textureName != null && !(textureName.equals("")))
		{			
			Texture temp = getTexture(textureName);
			if(temp == null)
			{
				if(verbose)
				{
					//System.out.println("Request for the texture '" + textureName + "' returned null. Please verify file exists and is not corrupt.");
				}								
				
				GL11.glColor4f(red, green, blue, alpha);
				GL11.glBegin(GL11.GL_QUADS);
					// Top Left
					GL11.glVertex2f(x,y);
					// Top Right					
					GL11.glVertex2f(x + width, y);
					// Bottom Right
					GL11.glVertex2f(x + width,y + height);
					// Bottom Left
					GL11.glVertex2f(x,y + height);
				GL11.glEnd();
			}
			else
			{
				drawTexture(x, y, temp, width, height, srcX, srcY, srcWidth, srcHeight, red, green, blue, alpha, angle);
			}			
		}
	}

	/**
	 * draw a quad with the image on it - accept float for rotation!
	 */
	public static void drawTexture(float x, float y, Texture texture, int width, int height, int srcX, int srcY, int srcWidth, int srcHeight, float red, float green, float blue, float alpha, float angle) 
	{		
		// If the provided texture string is null or empty, don't try to draw it. :)
		if(texture != null)
		{										
			// Bind the current texture to the current shader if one is in use.
			/*if(useShader)
			{
				setShaderUniform(currentShader, "texture", getTextureId(texture));
			}*/
			
			texture.bind();
			
			float fSrcX = ((float)srcX / texture.getWidth());
			float fSrcY = ((float)srcY / texture.getHeight());
			float fSrcWidth = (((float)srcX + (float)srcWidth) / texture.getWidth());
			float fSrcHeight = (((float)srcY + (float)srcHeight) / texture.getHeight());			
			
			GL11.glPushMatrix();				
							
			// Rotation works! 1/4/14
			if(angle != 0)
			{
				GL11.glTranslatef(x + (width / 2), y + (height / 2), 0); // move to the proper position
				GL11.glRotatef(angle, 0, 0, 1); // now rotate
				GL11.glTranslatef(-1 *(x+ (width / 2)), -1 * (y+(height  / 2)), 0);				
			}
			
			GL11.glColor4f(red, green, blue, alpha);
			GL11.glBegin(GL11.GL_QUADS);
				// Top Left
				GL11.glTexCoord2f(fSrcX, fSrcY);
				GL11.glVertex2f(x,y);
				// Top Right
				GL11.glTexCoord2f(fSrcWidth, fSrcY);
				GL11.glVertex2f(x + width, y);
				// Bottom Right
				GL11.glTexCoord2f(fSrcWidth,fSrcHeight);
				GL11.glVertex2f(x + width,y + height);
				// Bottom Left
				GL11.glTexCoord2f(fSrcX,fSrcHeight);
				GL11.glVertex2f(x,y + height);
			GL11.glEnd();
			
			GL11.glPopMatrix();
		}			
	}	
	
	public static boolean isCloseRequested()
	{
		return Display.isCloseRequested();
	}
	
	public static int getWindowHeight()
	{
		return 0;
	}
	
	public static int getWindowWidth()
	{
		return 0;
	}	
	
	@SuppressWarnings("unused")
	private static int getTextureId(Texture texture)
	{
		int id = texture.getID();
		return id;
	}
	
	public static Texture getTexture(String textureName)
	{
		return texture.get(textureName);
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
			displayBuffer = new FrameBuffer(width, height);
					
		}
		
		try 
		{
			Graphics.loadShaders();
		} 
		catch (Exception e) 
		{		
			e.printStackTrace();
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
	
	/*	Thanks for this one goes out to
	/	http://www.thehelper.net/threads/java-lwjgl-opengl-display-seticon-question.156958/
	*/
	//TODO Rewrite
	/*private static ByteBuffer loadIcon(String path) throws IOException 
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
    }*/	
	
	public static void loadShaders() throws Exception
	{
		// Handle shaders
		/*int vertShader = createShader("res/shaders/screen.vert", ARBVertexShader.GL_VERTEX_SHADER_ARB);
		int fragShader = createShader("res/shaders/screen.frag", ARBFragmentShader.GL_FRAGMENT_SHADER_ARB);
		shaderProgram = ARBShaderObjects.glCreateProgramObjectARB();
		
		if(shaderProgram != 0)
		{
			ARBShaderObjects.glAttachObjectARB(shaderProgram, vertShader);
			ARBShaderObjects.glAttachObjectARB(shaderProgram, fragShader);
			
			ARBShaderObjects.glLinkProgramARB(shaderProgram);
			ARBShaderObjects.glValidateProgramARB(shaderProgram);
		
			System.out.println("Shader program " + shaderProgram + " initialized.");			
		}*/
	}
	
	public static HashMap<String, Texture> loadTextures(Class<?> invokingClass)
	{
		return loadTextures(GRAPHICS_FILE_PATH, invokingClass);
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
						Texture temp = null;
						String textureName = files.substring(0, files.lastIndexOf(".")).trim();
						
						if(transparentColor != null)
						{
							temp = new Texture(texturePath + "/" + files, transparentColor);
						}
						else
						{
							temp = new Texture(texturePath + "/" + files);
						}
												
						if(maskTextures.length > 0 && (Arrays.asList(maskTextures)).contains(textureName))
						{
							Texture maskTemp = null;
							String maskTextureName = "_" + textureName;
							maskTemp = new Texture(texturePath + "/" + files, transparentColor, Graphics.WHITE);
							texture.put(maskTextureName, maskTemp);
							
							if(verbose)
							{
								System.out.println("Loaded texture '" + maskTextureName + "'");
							}							
						}
						
						texture.put(textureName, temp);				           
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
		
		return texture;
	}
	
	/*	Thanks for this one goes out to
	/	http://www.thehelper.net/threads/java-lwjgl-opengl-display-seticon-question.156958/
	*/
	private static ByteBuffer loadIcon(String path) throws IOException 
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
	public static void setAppIcon(String icon16, String icon32)
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
	
	/*public static void setCurrentShader(int programId)
	{
		if(programId == 0)
		{
			useShader = false;
		}
		{
			useShader = true;
		}
		
		currentShader = programId;
		ARBShaderObjects.glUseProgramObjectARB(programId);		
	}*/
	
	/**
	 * Set the display mode to be used 
	 * 
	 * @param width The width of the display required
	 * @param height The height of the display required
	 * @param fullscreen True if we want fullscreen mode
	 */
	/*public static void setDisplayMode(int width, int height, boolean fullscreen) 
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
	}*/	
	
	/**
	 * Sets the display icons from two file paths provided.
	 * 
	 * @param icon16	16x16 version of the app icon
	 * @param icon32	32x32 verison of the app icon
	 */
	// TODO Rewrite
	/*public static void setAppIcon(String icon16, String icon32)
	{ 
		try 
		{
			Display.setIcon(new ByteBuffer[] {loadIcon(icon16), loadIcon(icon32)});
		} 
		catch (IOException e) 
		{		
			e.printStackTrace();
		}		
		
	}*

	/**
	 * Sets the buffer to draw to - 0 is the main buffer.
	 * 
	 * @param bufferId
	 */
	private static void setBuffer(int bufferId, boolean clearBuffer)
	{
		//currentBufferID = bufferId;
		glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, bufferId);
		GL11.glPopAttrib();
		if(clearBuffer)
		{
			Graphics.clear();
		}
	}	
	
	/**
	 * Sets the buffer to draw to - 0 is the main buffer.
	 * 
	 * @param bufferId
	 */
	private static void setBuffer(int bufferId)
	{
		setBuffer(bufferId, false);
	}
	
	/**
	 * 
	 * @param fbo
	 * @param clearBuffer
	 */
	public static void setBuffer(FrameBuffer fbo, boolean clearBuffer)
	{
		if(fbo != null)
		{
			setBuffer(fbo.getId(), clearBuffer);
		}
		else
		{
			setBuffer(0, clearBuffer);
		}
	}
	
	/**
	 * 
	 * @param fbo
	 */
	public static void setBuffer(FrameBuffer fbo)
	{
		if(fbo != null)
		{
			setBuffer(fbo.getId(), false);
		}
		else
		{
			setBuffer(0);
		}
	}
	
	public static void setMaskTextureList(String[] textureList)
	{
		maskTextures = textureList;
	}
	
	/**
	 * Changes how GL space is scaled - useful if gamefield is 640x480, but monitor doesn't support that low
	 * 
	 * @param xScaler
	 * @param yScaler
	 */
	public static void setScaledDisplay(double xScaler, double yScaler)
	{
		GL11.glOrtho(0, getDisplayWidth() / xScaler, getDisplayHeight() / yScaler, 0, 1, -1);
	}
	
	/*private static void setShaderUniform(int programId, String attribute, int value)
	{		
		int attributeLoc = ARBShaderObjects.glGetUniformLocationARB(programId, attribute);
		ARBShaderObjects.glUniform1iARB(attributeLoc, value);			
	}*/
	
	/**
	 * Returns a String containing information about the supplied DisplayMode object
	 * 
	 * @param d
	 * @return
	 */
	/*public static String getDisplayModeInfo(DisplayMode d)
	{
		String s = d.getWidth() + "x" + d.getHeight() + " [" + d.getFrequency() + "] " + d.getBitsPerPixel();
		return s;
	}*/
	
    private static String getLogInfo(int obj) 
    {
        return ARBShaderObjects.glGetInfoLogARB(obj, ARBShaderObjects.glGetObjectParameteriARB(obj, ARBShaderObjects.GL_OBJECT_INFO_LOG_LENGTH_ARB));
    }
    
    private static String readFileAsString(String filename) throws Exception 
    {
        StringBuilder source = new StringBuilder();        
        InputStream in = new FileInputStream(filename); 
        		//ResourceLoader.getResourceAsStream(filename);
        Exception exception = null;
        
        BufferedReader reader;
        try{
            reader = new BufferedReader(new InputStreamReader(in,"UTF-8"));
            
            Exception innerExc= null;
            try {
            	String line;
                while((line = reader.readLine()) != null)
                    source.append(line).append('\n');
            }
            catch(Exception exc) {
            	exception = exc;
            }
            finally {
            	try {
            		reader.close();
            	}
            	catch(Exception exc) {
            		if(innerExc == null)
            			innerExc = exc;
            		else
            			exc.printStackTrace();
            	}
            }
            
            if(innerExc != null)
            	throw innerExc;
        }
        catch(Exception exc) {
        	exception = exc;
        }
        finally {
        	try {
        		in.close();
        	}
        	catch(Exception exc) {
        		if(exception == null)
        			exception = exc;
        		else
					exc.printStackTrace();
        	}
        	
        	if(exception != null)
        		throw exception;
        }
        //System.out.println("Shader loaded: \n\n" + source.toString());
        return source.toString();
    }

    public static FrameBuffer getDisplayBuffer()
    {
    	return displayBuffer; //.getFrameBufferId()
    }
    
    public static boolean isFullScreen()
    {
    	return Display.isFullscreen();
    }
    
    public static void sync(int fps)
    {
    	Display.sync(fps);
    }
    
    public static void update()
    {
    	Display.update();
    }
    
    public static void update(boolean sync)
    {
    	update();
    	if(sync) sync(60);
    }
    
	private static int getDisplayHeight() {
		return displayHeight;
	}

	private static void setDisplayHeight(int displayHeight) {
		Graphics.displayHeight = displayHeight;
	}

	private static int getDisplayWidth() {
		return displayWidth;
	}

	private static void setDisplayWidth(int displayWidth) {
		Graphics.displayWidth = displayWidth;
	}	
	
	public static void setVSync(boolean vsync)
	{
		Display.setVSyncEnabled(vsync);
	}

}

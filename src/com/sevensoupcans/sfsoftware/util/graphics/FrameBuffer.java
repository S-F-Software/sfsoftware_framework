package com.sevensoupcans.sfsoftware.util.graphics;

import static org.lwjgl.opengl.EXTFramebufferObject.GL_COLOR_ATTACHMENT0_EXT;
import static org.lwjgl.opengl.EXTFramebufferObject.GL_DEPTH_ATTACHMENT_EXT;
import static org.lwjgl.opengl.EXTFramebufferObject.GL_FRAMEBUFFER_EXT;
import static org.lwjgl.opengl.EXTFramebufferObject.GL_RENDERBUFFER_EXT;
import static org.lwjgl.opengl.EXTFramebufferObject.glBindFramebufferEXT;
import static org.lwjgl.opengl.EXTFramebufferObject.glBindRenderbufferEXT;
import static org.lwjgl.opengl.EXTFramebufferObject.glFramebufferRenderbufferEXT;
import static org.lwjgl.opengl.EXTFramebufferObject.glFramebufferTexture2DEXT;
import static org.lwjgl.opengl.EXTFramebufferObject.glGenFramebuffersEXT;
import static org.lwjgl.opengl.EXTFramebufferObject.glGenRenderbuffersEXT;
import static org.lwjgl.opengl.EXTFramebufferObject.glRenderbufferStorageEXT;

import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.EXTFramebufferObject;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL30;

public class FrameBuffer 
{
	private int colorTextureID;
	private int framebufferID;
	private int depthRenderBufferID;	

	private int width;
	private int height;
	
	private int currentShader;
	
	public FrameBuffer(int width, int height) 
	{
		// init our fbo	
		framebufferID = glGenFramebuffersEXT();								// create a new framebuffer
		colorTextureID = GL11.glGenTextures();								// and a new texture used as a color buffer
		depthRenderBufferID = glGenRenderbuffersEXT();						// And finally a new depthbuffer

		glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, framebufferID); 			// switch to the new framebuffer

		// initialize color texture
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, colorTextureID);				// Bind the colorbuffer texture
		
		GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);		// make it linear filtered
		GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST); //GL11.GL_NEAREST_MIPMAP_LINEAR);		// make it linear filtered
		
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, width, height, 0, GL11.GL_RGBA, GL11.GL_INT, (java.nio.ByteBuffer) null);	// Create the texture data
		glFramebufferTexture2DEXT(GL_FRAMEBUFFER_EXT,GL_COLOR_ATTACHMENT0_EXT, GL11.GL_TEXTURE_2D, colorTextureID, 0); // attach it to the framebuffer

		// initialize depth renderbuffer
		glBindRenderbufferEXT(GL_RENDERBUFFER_EXT, depthRenderBufferID);	// bind the depth renderbuffer
		glRenderbufferStorageEXT(GL_RENDERBUFFER_EXT, GL14.GL_DEPTH_COMPONENT24, width, height);	// get the data space for it
		glFramebufferRenderbufferEXT(GL_FRAMEBUFFER_EXT,GL_DEPTH_ATTACHMENT_EXT,GL_RENDERBUFFER_EXT, depthRenderBufferID); // bind it to the renderbuffer

		glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, 0);									// Switch back to normal framebuffer rendering	
		
		setWidth(width);
		setHeight(height);

		verify();
		
		if(getStatus() != GL30.GL_FRAMEBUFFER_COMPLETE)
		{
			//GL30.glDeleteFramebuffers(framebufferID);			
			System.out.println("There was an issue with FBO Id " + framebufferID + ". Status: " + getStatus());
		}
		
	}
	
	public void clear()
	{
		glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, this.getId());
		GL11.glPopAttrib();

		GL11.glPushAttrib(GL11.GL_VIEWPORT_BIT);
		GL11.glViewport(0, 0, this.getWidth(), this.getHeight());
		
		GL11.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
	}
	
	public void draw(final int x, final int y)
	{
		draw(x, y, getWidth(), getHeight());
	}
	
	public void draw(final int x, final int y, final int width, final int height)
	{
		draw(x, y, width, height, 0, 0, width, height);
	}

	public void draw(final int x, final int y, final int width, final int height, final int srcX, final int srcY, final int srcWidth, final int srcHeight)
	{
		draw(x, y, width, height, srcX, srcY, srcWidth, srcHeight, 1.0f, 1.0f, 1.0f, 1.0f);
	}
	
	public void draw(final int x, final int y, final int width, final int height, final int srcX, final int srcY, final int srcWidth, 
			int srcHeight, float red, float green, float blue, float alpha)
	{
		GL11.glPushMatrix();		
		
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		GL11.glOrtho(0, Display.getWidth(), Display.getHeight(), 0, 1, -1);
		GL11.glViewport(0, 0, Display.getWidth(), Display.getHeight());
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glLoadIdentity();					
		
		GL11.glEnable(GL11.GL_TEXTURE_2D);
				
		int attributeLoc = ARBShaderObjects.glGetUniformLocationARB(currentShader, "texture");
		ARBShaderObjects.glUseProgramObjectARB(currentShader);								
		ARBShaderObjects.glUniform1iARB(attributeLoc, getTextureId());						
		
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, getTextureId());
		
		float fSrcX = ((float)srcX / getWidth());
		float fSrcY = ((float)srcY / getHeight());
		float fSrcWidth = (((float)srcX + (float)srcWidth) / getWidth());
		float fSrcHeight = (((float)srcY + (float)srcHeight) / getHeight());			
		
		GL11.glPushMatrix();		
		
		// Need to reset the color or else things get weird...
		GL11.glColor4f(red, green, blue, alpha);
		GL11.glBegin(GL11.GL_QUADS);					
			// Top Left
			//GL11.glTexCoord2f(0.0f, 1.0f); 
			GL11.glTexCoord2f(fSrcX, 1 - fSrcY);
			GL11.glVertex2i(x, y);  		
			// Top Right
			//GL11.glTexCoord2f(1.0f, 1.0f); 
			GL11.glTexCoord2f(fSrcWidth, 1 - fSrcY);
			GL11.glVertex2i(x + width,  y); 		
			// Bottom Right
			//GL11.glTexCoord2f(1.0f, 0.0f);
			GL11.glTexCoord2f(fSrcWidth,1 - fSrcHeight);
			GL11.glVertex2i(x + width, y + height);		
			// Bottom Left		
			//GL11.glTexCoord2f(0.0f, 0.0f); 		
			GL11.glTexCoord2f(fSrcX, 1 - fSrcHeight);
			GL11.glVertex2i(x, y + height);	
		GL11.glEnd();
		      
		GL11.glPopMatrix();	
		GL11.glPopAttrib();
		
		ARBShaderObjects.glUseProgramObjectARB(0);		
	}
	
	public int getDepthBufferId()
	{
		return depthRenderBufferID;
	}
	
	public int getId()
	{
		return framebufferID;
	}
	
	public int getStatus()
	{
		return EXTFramebufferObject.glCheckFramebufferStatusEXT(this.getId());				
	}
	
	public int getTextureId()
	{
		return colorTextureID;
	}
	
	public void setAsCurrentBuffer()
	{
		setAsCurrentBuffer(false);
	}
	
	public void setAsCurrentBuffer(final boolean clearBuffer)
	{
		Graphics.setBuffer(this, clearBuffer);
	}
	
	public void setCurrentShader(final int programId)
	{
		/*if(programId == 0)
		{
			useShader = false;
		}
		{
			useShader = true;
		}*/
		
		currentShader = programId;
		ARBShaderObjects.glUseProgramObjectARB(programId);		
	}

	public int getWidth() 
	{
		return width;
	}

	private void setWidth(final int width) 
	{
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	private void setHeight(final int height) {
		this.height = height;
	}	
	
	private void verify()
	{
		int framebuffer = EXTFramebufferObject.glCheckFramebufferStatusEXT( this.getId() ); 
		switch ( framebuffer ) {
		    case EXTFramebufferObject.GL_FRAMEBUFFER_COMPLETE_EXT:
		        break;
		    case EXTFramebufferObject.GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT_EXT:
		        throw new RuntimeException( "FrameBuffer: " + this.getId()
		                + ", has caused a GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT_EXT exception" );
		    case EXTFramebufferObject.GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT_EXT:
		        throw new RuntimeException( "FrameBuffer: " + this.getId()
		                + ", has caused a GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT_EXT exception" );
		    case EXTFramebufferObject.GL_FRAMEBUFFER_INCOMPLETE_DIMENSIONS_EXT:
		        throw new RuntimeException( "FrameBuffer: " + this.getId()
		                + ", has caused a GL_FRAMEBUFFER_INCOMPLETE_DIMENSIONS_EXT exception" );
		    case EXTFramebufferObject.GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER_EXT:
		        throw new RuntimeException( "FrameBuffer: " + this.getId()
		                + ", has caused a GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER_EXT exception" );
		    case EXTFramebufferObject.GL_FRAMEBUFFER_INCOMPLETE_FORMATS_EXT:
		        throw new RuntimeException( "FrameBuffer: " + this.getId()
		                + ", has caused a GL_FRAMEBUFFER_INCOMPLETE_FORMATS_EXT exception" );
		    case EXTFramebufferObject.GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER_EXT:
		        throw new RuntimeException( "FrameBuffer: " + this.getId()
		                + ", has caused a GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER_EXT exception" );		    
		}
	}
	
}

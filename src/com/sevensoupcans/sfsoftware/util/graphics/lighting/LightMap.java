package com.sevensoupcans.sfsoftware.util.graphics.lighting;

import java.util.ArrayList;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;

import com.sevensoupcans.sfsoftware.game.actor.Actor;
import com.sevensoupcans.sfsoftware.util.graphics.FrameBuffer;
import com.sevensoupcans.sfsoftware.util.graphics.Graphics;
import com.sevensoupcans.sfsoftware.util.graphics.RGBA;

public class LightMap {

	private static FrameBuffer lightMapFrameBuffer;
	
	private final ArrayList<LightSource> lights;
	private final RGBA baselineColor;		
	
	public LightMap(int width, int height)
	{
		this(width, height, new ArrayList<LightSource>());
	}
	
	public LightMap(int width, int height, RGBA baselineColor)
	{
		this(width, height, new ArrayList<LightSource>(), baselineColor);
	}
	
	public LightMap(int width, int height, ArrayList<LightSource> lights)
	{
		this(width, height, lights, RGBA.BLACK);
	}
	
	public LightMap(int width, int height, ArrayList<LightSource> lights, RGBA baselineColor) 
	{
		// If the frame buffer has never been instantiated
		if(lightMapFrameBuffer == null)
		{
			lightMapFrameBuffer = new FrameBuffer(width, height);
		}
		// If the frame buffer was instantiated, but the new map's width or height doesn't match
		else if(lightMapFrameBuffer.getWidth() != width || lightMapFrameBuffer.getHeight() != height)
		{
			lightMapFrameBuffer = new FrameBuffer(width, height);
		}
		
		this.baselineColor = baselineColor;
		this.lights = lights;
	}
	
	public boolean addLightSource(LightSource ls)
	{
		return this.lights.add(ls);
	}
	
	public void draw(int x, int y)
	{
		draw(x, y, lightMapFrameBuffer.getWidth(), lightMapFrameBuffer.getHeight());
	}
	
	public void draw(int x, int y, int width, int height)
	{
		draw(x, y, width, height, 0, 0, width, height);
	}

	public void draw(int x, int y, int width, int height, int srcX, int srcY, int srcWidth, int srcHeight)
	{
		draw(x, y, width, height, srcX, srcY, srcWidth, srcHeight, 1.0f);
	}
	
	public void draw(int x, int y, int width, int height, int srcX, int srcY, int srcWidth, 
				int srcHeight, float alpha)
	{
		// Capture the current display buffer so we can revert back to it after rendering the lightmap.
		FrameBuffer fbo = Graphics.getCurrentDisplayBuffer();
		
		RGBA quadColor = new RGBA(baselineColor.getRed(), baselineColor.getGreen(), baselineColor.getBlue(), alpha);
		
		Graphics.setBuffer(lightMapFrameBuffer);		
		Graphics.drawQuad(0, 0, lightMapFrameBuffer.getWidth(), lightMapFrameBuffer.getHeight(), quadColor);
		
		// Draw the light sources associated with this map
		LightSource.updateIntensity();
		for(LightSource ls : lights)		
			ls.draw(alpha);
		
		// Switch to the primary buffer / display
		Graphics.setBuffer(null);
		
		GL11.glPushMatrix();		
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		GL11.glOrtho(0, Display.getWidth(), Display.getHeight(), 0, 1, -1);
		GL11.glViewport(0, 0, Display.getWidth(), Display.getHeight());
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glLoadIdentity();				
		
		GL11.glPushAttrib(GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_ENABLE_BIT | GL11.GL_COLOR_BUFFER_BIT);
		GL14.glBlendFuncSeparate(GL11.GL_SRC_COLOR, GL11.GL_DST_COLOR, GL11.GL_ONE, GL11.GL_DST_COLOR);
		
		GL11.glEnable(GL11.GL_TEXTURE_2D);		
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, lightMapFrameBuffer.getTextureId());
		
		float fSrcX = ((float)srcX / lightMapFrameBuffer.getWidth());
		float fSrcY = ((float)srcY / lightMapFrameBuffer.getHeight());
		float fSrcWidth = (((float)srcX + (float)srcWidth) / lightMapFrameBuffer.getWidth());
		float fSrcHeight = (((float)srcY + (float)srcHeight) / lightMapFrameBuffer.getHeight());			
		
		GL11.glPushMatrix();		
		
		// Need to reset the color or else things get weird...
		GL11.glColor4f(1.0f, 1.0f, 1.0f, alpha);
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
		
		// Set some things back to the way they were.
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);		
		Graphics.setBuffer(fbo);
		
	}
	
	public ArrayList<LightSource> getLightSourcesForActor(Actor actor)
	{
		ArrayList<LightSource> associatedLightSources = new ArrayList<LightSource>();
		
		for(LightSource ls : lights)		
			if(ls.getAssociatedActor() != null && ls.getAssociatedActor().equals(actor))
				associatedLightSources.add(ls);		
		
		return associatedLightSources;
	}
	
	public boolean removeLightSource(LightSource ls)
	{
		return lights.remove(ls);
	}

}

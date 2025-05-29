package com.sevensoupcans.sfsoftware.util.graphics.lighting;

import com.sevensoupcans.sfsoftware.game.actor.Actor;
import com.sevensoupcans.sfsoftware.util.graphics.RGBA;
import com.sevensoupcans.sfsoftware.util.graphics.geometry.Circle;
import com.sevensoupcans.sfsoftware.util.tile.Tile;

public class LightSource {
	
	private static final RGBA DEFAULT_CORE_COLOR = new RGBA(220, 220, 220, 0.55f);
	private static final RGBA DEFAULT_OUTER_COLOR = new RGBA(220, 220, 220, 0);
	
	private static int segmentsToDraw = 64;
	private static float intensity = 0.0f;	
	
	private final Actor associatedActor;
	private final float maximumIntensity;	
	private final float radius;
	private final RGBA coreColor;
	private final RGBA outerColor;		
	
	private float x;
	private float y;
	
	public static float updateIntensity()
	{
		return (intensity = intensity < Math.PI ? intensity + 0.025f : 0.0f);
	}
	
	public LightSource(Actor associatedActor)
	{
		this(associatedActor.getCenterX(), associatedActor.getCenterY(),
				((associatedActor.getWidth() + associatedActor.getHeight()) / 2) * 2, 
				DEFAULT_CORE_COLOR, DEFAULT_OUTER_COLOR, (associatedActor.getWidth() + associatedActor.getHeight()), 
				associatedActor);
	}
	
	public LightSource(float x, float y)
	{
		this(x, y, Tile.getDefaultTileSize() * 2, DEFAULT_CORE_COLOR);
	}
	
	public LightSource(float x, float y, int radius, RGBA coreColor)
	{
		this(x, y, radius, coreColor, DEFAULT_OUTER_COLOR);
	}
	
	public LightSource(float x, float y, int radius, RGBA coreColor, RGBA outerColor)
	{
		this(x, y, radius, coreColor, outerColor, Tile.getDefaultTileSize(), null);
	}	
	
	public LightSource(float x, float y, float radius, RGBA coreColor, RGBA outerColor, float maximumIntensity, Actor associatedActor)
	{
		this.x = x;
		this.y = y;
		this.coreColor = coreColor;
		this.radius = radius;
		this.outerColor = outerColor;
		this.maximumIntensity = maximumIntensity;
		this.associatedActor = associatedActor;
	}
	
	public void draw(final float alpha)
	{
		// If the light source is associated with an Actor, the position is updated based on the Actor's
		if(associatedActor != null)
		{
			x = associatedActor.getCenterX();
			y = associatedActor.getCenterY();
		}
				
		Circle.draw(x, y, (float) (radius + (Math.sin(intensity) * maximumIntensity)), 
				new RGBA(coreColor.getRed(), coreColor.getGreen(), coreColor.getBlue(), coreColor.getAlpha() > alpha ? alpha : coreColor.getAlpha()),
				outerColor, segmentsToDraw);		
	}
	
	public Actor getAssociatedActor()
	{
		return associatedActor;
	}
	
}

package com.sevensoupcans.sfsoftware.util.graphics.particles;

import java.util.Vector;

import com.sevensoupcans.sfsoftware.util.Clock;
import com.sevensoupcans.sfsoftware.util.Updatable;
import com.sevensoupcans.sfsoftware.util.graphics.RGBA;
import com.sevensoupcans.sfsoftware.util.graphics.geometry.Circle;
import com.sevensoupcans.sfsoftware.util.graphics.geometry.Quad;

public class Particle implements Updatable 
{
	private static Vector<Particle> particles = new Vector<Particle>();
	
	public static void generateParticles(final int originX, final int originY, final int originWidth, 
			final int originHeight, final int particleCount, final float red, final float green, 
			final float blue, final int size)
	{
		for(int i = 0; i < particleCount; i++)
		{
			new Particle(originX + (int) (Math.random() * originWidth), 
					originY + (int) (Math.random() * originHeight), red, green, blue, (byte) size);
		}		
	}
	/**
	 * Removes all particle elements
	 */
	public static void removeAll()
	{
		particles.removeAllElements();
	}
	
	/**
	 * Static method to update all of the particles currently in existence.
	 */
	public static void updateAll()
	{
		Particle[] c = new Particle[particles.size()];
		particles.toArray(c);	
		
		for(int i = 0; i < c.length; i++)		
		{
			Particle p = (c[i]);
			p.update();		
		}
	}

	private boolean readyToDie = false;
	private boolean useGravity = true;	
	
	private byte particleSize = 2;
	
	private Clock deathClock = new Clock(0);
	
	private int x;	
	private int y;
	
	private double directionAngle;
	
	private float accel = 0;
	private float decayRate = 0.025f;
	private float gravity = 0.25f;	
	private float red = 1;	
	private float green = 1;
	private float blue = 1;	
	private float alpha = 1;
	
	public Particle(int destX, int destY)
	{
		this(destX, destY, 1.0f, 1.0f, 1.0f, (byte) 2);
	}	

	public Particle(int destX, int destY, float destRed, float destGreen, float destBlue, byte size) 
	{
		this(destX, destY, destRed, destGreen, destBlue, size, false);		
	}	
	
	public Particle(int destX, int destY, float destRed, float destGreen, float destBlue, byte size, 
			boolean gravity) 
	{
		this(destX, destY, destRed, destGreen, destBlue, size, (Math.random() * (2 * Math.PI)), gravity);		
	}
	
	public Particle(int destX, int destY, float destRed, float destGreen, float destBlue, byte size, 
			double angle, boolean gravity)
	{
		x = destX;
		y = destY;
		red = destRed;
		green = destGreen;
		blue = destBlue;
		directionAngle = angle;
		particleSize = size;
		useGravity = gravity;
		
		if(Math.sin(directionAngle) < 0) accel = (float) (Math.random() * -1);	
		
		particles.add(this);
	}
	
	public final float setDecayRate(final float decayRate)
	{
		return (this.decayRate = decayRate);
	}
	
	public final void setLifeSpan(final int lifeSpanInMilliseconds)
	{
		this.deathClock = new Clock(lifeSpanInMilliseconds);
	}	
	
	@Override
	/**
	 * Handles rendering and location updates of a particle instance.
	 */
	public final void update() {

		double xDirection = Math.cos(directionAngle) * -1;
		double yDirection = Math.sin(directionAngle);

		x = (int) (x + Math.round(xDirection * 4));
		y = (int) ((int) (y + Math.round(yDirection * 4)) + accel);

		if(this.useGravity) this.accel = this.accel + this.gravity;		
		
		if(deathClock.updateClock() && !(this.readyToDie)) this.readyToDie = true;		
		if(this.readyToDie) this.alpha = this.alpha - this.decayRate;	
		
		if(this.alpha <= 0)
		{
			particles.remove(this);
		}
		else
		{
			if(particleSize < 2)
			{
				Quad.draw(x, y, particleSize, particleSize, red, green, blue, alpha);			
			}
			else
			{
				RGBA inner = new RGBA(red, green, blue, alpha);
				RGBA outer = new RGBA(red, green, blue, 0.75f);
				Circle.draw(x, y, (particleSize / 2), inner, outer, 16);
			}
		}
		
	}

}
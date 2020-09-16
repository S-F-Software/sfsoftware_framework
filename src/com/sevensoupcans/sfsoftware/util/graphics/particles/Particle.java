package com.sevensoupcans.sfsoftware.util.graphics.particles;

import java.util.Vector;

import com.sevensoupcans.sfsoftware.util.Updatable;
import com.sevensoupcans.sfsoftware.util.graphics.Graphics;
import com.sevensoupcans.sfsoftware.util.graphics.RGBA;

public class Particle implements Updatable 
{
	private float gravity = 0.25f;
	private float accel = 0;
	private int particleSize = 2;
	
	private double directionAngle;
	private static Vector<Particle> particles = new Vector<Particle>();
	
	private float red = 1;
	private float green = 1;
	private float blue = 1;
	private float alpha = 1;
	
	private int x;
	private int y;
	
	private boolean useGravity = true;
	
	public Particle(int destX, int destY)
	{
		x = destX;
		y = destY;
		directionAngle = (Math.random() * (2 * Math.PI));
		
		if(Math.sin(directionAngle) < 0)
		{
			accel = (float) (Math.random() * -10);
		}
		
		particles.add(this);
	}
	
	public Particle(int destX, int destY, float destRed, float destGreen, float destBlue, int size) {
		x = destX;
		y = destY;
		red = destRed;
		green = destGreen;
		blue = destBlue;
		directionAngle = (Math.random() * (2 * Math.PI));
		particleSize = size;
		
		if(Math.sin(directionAngle) < 0)
		{
			accel = (float) (Math.random() * -1);
		}
		
		particles.add(this);
	}
	
	public Particle(int destX, int destY, float destRed, float destGreen, float destBlue, int size, double angle, boolean gravity)
	{
		x = destX;
		y = destY;
		red = destRed;
		green = destGreen;
		blue = destBlue;
		directionAngle = angle;
		particleSize = size;
		useGravity = gravity;
		
		particles.add(this);
	}
	

	public static void generateParticles(int originX, int originY, int originWidth, int originHeight, int particleCount, float red, float green, float blue, int size)
	{
		for(int i = 0; i < particleCount; i++)
		{
			new Particle(originX + (int) (Math.random() * originWidth), originY + (int) (Math.random() * originHeight), red, green, blue, size);
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
			Particle p = ((Particle) c[i]);
			p.update();		
		}
	}
	
	@Override
	/**
	 * Handles rendering and location updates of a particle instance.
	 */
	public void update() {

		double xDirection = Math.cos(directionAngle) * -1;
		double yDirection = Math.sin(directionAngle);

		x = (int) (x + Math.round(xDirection * 4));
		y = (int) ((int) (y + Math.round(yDirection * 4)) + accel);

		if(useGravity) accel = accel + gravity;		
		
		alpha = alpha - 0.025f;
		if(alpha <= 0)
		{
			particles.remove(this);
		}
		else
		{
			if(particleSize < 2)
			{
				Graphics.drawQuad(x, y, particleSize, particleSize, red, green, blue, alpha);			
			}
			else
			{
				RGBA inner = new RGBA(red, green, blue, alpha);
				RGBA outer = new RGBA(red, green, blue, 0.75f);
				
				//Graphics.drawCircle(x, y, (particleSize / 2), red, green, blue, alpha, 16);
				Graphics.drawCircle(x, y, (particleSize / 2), inner, outer, 16);
			}
		}
		
	}

}
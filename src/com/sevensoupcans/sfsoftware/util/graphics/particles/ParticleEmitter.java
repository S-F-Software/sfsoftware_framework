package com.sevensoupcans.sfsoftware.util.graphics.particles;

import com.sevensoupcans.sfsoftware.util.MathUtils;
import com.sevensoupcans.sfsoftware.util.Updatable;
import com.sevensoupcans.sfsoftware.util.graphics.geometry.Quad;

public class ParticleEmitter extends Quad implements Updatable 
{
	private byte particleSize = 2;
	private int particleLifeSpanInFrames;
	private int particlesPerFrame = 200;
	private double particleDirection;
	
	public ParticleEmitter(int x, int y, int width, int height)
	{
		this(x, y, width, height, 60, 0);
	}
	
	public ParticleEmitter(int x, int y, int width, int height, int particleLifeSpanInFrames, 
			double particleDirection) {
		super(x, y, width, height);
		this.particleLifeSpanInFrames = particleLifeSpanInFrames;
		this.particleDirection = particleDirection;
	}

	@Override
	public void update() 
	{
		for(int i = 0; i < particlesPerFrame; i++)
		{
			Particle p = new Particle((int) (this.getX() + (Math.random() * this.getWidth())), 
					(int) (this.getY() + (Math.random() * this.getHeight())), this.getRed(), 
					this.getGreen(), this.getBlue(), particleSize, particleDirection, false);
			
			p.setLifeSpan(this.particleLifeSpanInFrames - MathUtils.randomInt(this.particleLifeSpanInFrames));			
		}

	}

}

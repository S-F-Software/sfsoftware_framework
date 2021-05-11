package com.sevensoupcans.sfsoftware.game.actor;

import java.util.Vector;

import com.sevensoupcans.sfsoftware.game.Game;
import com.sevensoupcans.sfsoftware.game.actor.attributes.Collidable;
import com.sevensoupcans.sfsoftware.game.actor.attributes.Permanent;
import com.sevensoupcans.sfsoftware.util.Updatable;
import com.sevensoupcans.sfsoftware.util.graphics.Sprite;
import com.sevensoupcans.sfsoftware.util.graphics.geometry.Quad;
import com.sevensoupcans.sfsoftware.util.graphics.particles.Particle;
import com.sevensoupcans.sfsoftware.util.tile.Tile;
import com.sevensoupcans.sfsoftware.util.tile.TileMap;
import com.sevensoupcans.sfsoftware.util.tile.pathfinding.AStarPathFinder;
import com.sevensoupcans.sfsoftware.util.tile.pathfinding.Path;
import com.sevensoupcans.sfsoftware.util.tile.pathfinding.PathFinder;

public class Actor extends Sprite implements Collidable 
{
	protected static int playingFieldX;
	protected static int playingFieldY;
	
	protected final static Vector<Actor> cast = new Vector<Actor>();
	
	public final static double getAngle(final Actor a, final Actor b)
	{
		double dx = a.getCenterX() - b.getCenterX(); // (a.getX() + (a.getWidth() / 2)) - (b.getX() + (b.getWidth() / 2));
		double dy = a.getCenterY() - b.getCenterY(); // (a.getY() + (a.getHeight() / 2)) - (b.getY() + (b.getHeight() / 2));
		
		double inRads = Math.atan2(dy,dx);
		
		if (inRads < 0)
		{
	        inRads = Math.abs(inRads);
		}
	    else
	    {
	        inRads = 2 * Math.PI - inRads;
	    }			
		
		return inRads;
	}
	public final static Vector<Actor> getCast()
	{
		return cast;
	}
			
	public final static void remove(final Actor a)
	{
		cast.remove(a);
	}
	public static void setPlayingField(final int x, final int y)
	{
		playingFieldX = x;
		playingFieldY = y;
	}	
	public static void updateCast()
	{
		updateCast(0);
	}	
	
	/**
	 * Updates all Actors - this includes drawing
	 */
	public static void updateCast(final int z) 
	{		
		// Copy the Actor vector to an array to avoid concurrent modification issues
		Actor[] c = new Actor[cast.size()];
		cast.toArray(c);	
		
		for(int i = 0; i < c.length; i++)		
		{					
			Actor a = c[i];
			
			// Only update Actors with the specified z level. There may be a better way to address this.
			if(a.getZOrder() != z) continue;
			
			if(a instanceof Updatable)
			{
				((Updatable) a).update();
			}
			
			a.draw();
		}				
	}	
	private final Game ASSOCIATED_GAME;	
	
	private final int TILE_SIZE;
	
	protected double xDirection = 0;	
	
	protected double yDirection = 0;

	protected int speed = 4;
	
	private boolean isWalkable = true;	
	
	private int zOrder = 0;
	
	public Actor(int destX, int destY, String texture, int destWidth, int destHeight, Game associatedGame) 
	{
		this(destX, destY, texture, 0, 0, destWidth, destHeight, associatedGame);
	}	
	
	public Actor(int destX, int destY, String texture, int srcX, int srcY, int destWidth, int destHeight, Game associatedGame) 
	{
		super(destX, destY, texture);		
		
		this.ASSOCIATED_GAME = associatedGame;
		this.TILE_SIZE = associatedGame.getTileMap().getTileSize();
		
		this.setHeight(destHeight);
		this.setWidth(destWidth);
		
		this.setSrcX(srcX);
		this.setSrcY(srcY);
		
		// Do not add the Player to our cast vector as this vector is cleared with each new room
		if(!(this instanceof Permanent))			
			cast.add(this);
	}
	
	@Override
	public boolean collidingWith(final Collidable object) {		
		return super.collidingWith(object);	
	}
	
	protected boolean collidingWithCast(final double dirX, final double dirY)
	{				
		boolean b = false;
		// Copy the Actor vector to an array to avoid concurrent modification issues
		Actor[] c = new Actor[cast.size()];
		cast.toArray(c);	
		
		for(int i = 0; i < c.length; i++)
		{
			// The cast vector can only hold Actor objects so no need to use instanceof
			Actor a  = c[i];			
			// Only check for collision if the current Actor isn't walkable and isn't us!
			if(!(a.equals(this)) && !(a.isWalkable()))
			{
				// Destination X and Y IF the Actor moves
				int newX = (int) (getX() + Math.round(getSpeed() * dirX));
				int newY = (int) (getY() + Math.round(getSpeed() * dirY));				
				// Create a temporary Actor object representing where OUR actor WILL move
				Actor temp = new Actor(newX, newY, "", 0, 0, getWidth(), getHeight(), getAssociatedGame());								
				// Check using our collidingWith method if the two objects are colliding!
				if(a.collidingWith(temp))
				{
					// Return true - this will prevent movement from happening.
					b = true;
					// Call the collisionResult method to trigger any expected behavior
					a.collisionResult(this);
				}
				// VERY important! Remove the temp Actor or huge memory leak problems will occur!
				temp.remove();
			}
		}
		
		return b;
	}	
	
	@Override
	public boolean collisionResult(final Collidable object) {
		// TODO Auto-generated method stub
		return false;
	}

	protected int distanceToActor(final Actor a)
	{
		int x1 = a.getCenterX(); // (a.getX() + (a.getWidth() / 2));
		int x2 = getCenterX(); // (getX() + (getWidth() / 2));
		int y1 = a.getCenterY(); // (a.getY() + (a.getHeight() / 2));
		int y2 = getCenterY(); //(getY() + (getHeight() / 2));
		
		return (int) Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
	}
	
	protected final int distanceToPoint(final int x, final int y)
	{
		return (int) Math.sqrt((x - getCenterX()) * (x - getCenterX()) + (y - getCenterY()) * (y - getCenterY()));		
	}	
	
	@Override
	public void draw()
	{
		super.draw(getWidth(), getHeight());
	}
	
	protected void generateParticles(final int particleCount, final float red, final float green, final float blue)
	{
		for(int i = 0; i < particleCount; i++)
		{
			new Particle((int)(getX() + (int) (Math.random() * getWidth())), (int)(getY() + (int) (Math.random() * getHeight())), red, green, blue, (byte) 2);
		}
	}	
	
	protected void generateParticles(final int particleCount, final float red, final float green, final float blue, final int size)
	{
		for(int i = 0; i < particleCount; i++)
		{
			new Particle((int)(getX() + (int) (Math.random() * getWidth())), (int)(getY() + (int) (Math.random() * getHeight())), red, green, blue, (byte) size);
		}
	}	
	
	protected final double getAngleFromActor(final Actor a)
	{
		return getAngle(a, this);
	}	
	
	protected final double getAngleFromPoint(final double x, final double y)
	{
		double dx = x - getCenterX(); // (getX() + (getWidth() / 2)) - x;
		double dy = y - getCenterY(); // (getY() + (getHeight() / 2)) - y;
		
		double inRads = Math.atan2(dy,dx);
		
		if (inRads < 0)
		{
	        inRads = Math.abs(inRads);
		}
	    else
	    {
	        inRads = 2 * Math.PI - inRads;
	    }			
		
		return inRads;
	}
	
	protected final double getAngleToActor(final Actor b)
	{
		return getAngle(this, b);
	}
	
	protected final double getAngleToPoint(final double x, final double y)
	{
		double dx = getCenterX() - x;
		double dy = getCenterY() - y;
		
		double inRads = Math.atan2(dy,dx);
		
		if (inRads < 0)
		{
	        inRads = Math.abs(inRads);
		}
	    else
	    {
	        inRads = 2 * Math.PI - inRads;
	    }			
		
		return inRads;
	}		
	
	public Game getAssociatedGame()
	{
		return ASSOCIATED_GAME;
	}
	
	public final Tile getCurrentTile()
	{		
		Tile[][] tilemap = getAssociatedGame().getTileMap().getMap();
		
		int xTile = Math.max(0, Math.min(tilemap.length - 1, getCurrentTileX()));
		int yTile = Math.max(0, Math.min(tilemap[0].length - 1, getCurrentTileY()));		
		
		return tilemap[xTile][yTile];
	}		
	
	public final int getCurrentTileX()
	{
		int xTile = (int) Math.floor((getCenterX() - playingFieldX) / TILE_SIZE);
		return xTile;
	}
	
	public final int getCurrentTileY()
	{		
		int yTile = (int) Math.floor((getCenterY() - playingFieldY) / TILE_SIZE);
		return yTile;
	}

	protected final Path getPathToActor(final Actor a)
	{
		return this.getPathToActor(new AStarPathFinder(this.getAssociatedGame().getTileMap(), 15, false), a);
	}	
	
	protected final Path getPathToActor(final PathFinder pathFinder, final Actor a)
	{
		return this.getPathToActor(pathFinder, a, pathFinder.getAssociatedTileMap());
	}
	
	protected final Path getPathToActor(final PathFinder pathFinder, final Actor a, final TileMap tileMap)
	{	
		// Get our x and y tile indexes on the tileMap.
		int xTile = tileMap.getTileMapXIndexFromXCoordinate((int) this.getX());
		int yTile = tileMap.getTileMapYIndexFromYCoordinate((int) this.getY());
		
		// Get the x and y tile indexes for the provided actor.
		int actorXTile = tileMap.getTileMapXIndexFromXCoordinate((int) a.getX());
		int actorYTile = tileMap.getTileMapYIndexFromYCoordinate((int) a.getY());
		
		// Find the path between our position and theirs
		return pathFinder.getPath(this, xTile, yTile, actorXTile, actorYTile);
	}	
	
	public int getSpeed() 
	{
		return speed;
	}	
	
	public double getXDirection()
	{
		return this.xDirection;
	}
	
	public double getYDirection()
	{
		return this.yDirection;
	}
	
	public int getZOrder()
	{
		return zOrder;
	}
	
	protected final boolean hasLineOfSightToActor(final Actor a)
	{
		boolean ableToSeeActor = true;
		double directionAngle = getAngleToActor(a);
	
		for(int i=1; i < distanceToActor(a); i++)
		{
			double xDirection = Math.cos(directionAngle) * -1;
			double yDirection = Math.sin(directionAngle);	
			
			int xPoint = (int) (getCenterX() + (xDirection * i));
			int yPoint = (int) (getCenterY() + (yDirection * i));					
			int xTile = (int) Math.floor((xPoint - playingFieldX) / TILE_SIZE);
			int yTile = (int) Math.floor((yPoint - playingFieldY) / TILE_SIZE);	
			
			if(getAssociatedGame().inDebugMode())
			{
				Quad.draw(xPoint, yPoint, 1, 1, 1, 0, 1, 1);
			}
			
			// TODO Check if the point intersects with any non-walkable Actors.
			/*if(Actor.collidingWithCast(xPoint, yPoint))
			{
				ableToSeeActor = false;
				break;
			}*/
			
			try
			{
				Tile[][] tileMap = getAssociatedGame().getTileMap().getMap();
				if(!(tileMap[xTile][yTile].isWalkable()))
				{
					ableToSeeActor = false;
					break;
				}
			}
			catch(ArrayIndexOutOfBoundsException e)
			{
				ableToSeeActor = false;
				break;
			}
		}	
		return ableToSeeActor;		
	}
	
	public final boolean isWalkable()
	{
		return isWalkable;
	}	
	
	/**
	 * Moves an Actor without checking any collision
	 * 
	 * @param dirX
	 * @param dirY
	 */
	protected boolean moveWithoutCheckingCollision(final double dirX, final double dirY)
	{
		super.move((float) (this.getX() + (dirX * speed)), (float) (this.getY() + (dirY * speed)));
		return true;
	}	
	
	public void remove()
	{
		cast.remove(this);
	}
	
	public void setSpeed(final int newSpeed)
	{
		speed = newSpeed;
	}	
	
	public final void setWalkable(final boolean b)
	{
		isWalkable = b;
	}
	
	public void setZOrder(final int i)
	{
		zOrder = i;
	}
	
	/**
	 * Snaps an Actor to the tile on the playing field that there center coordinates sit in.
	 */
	public final void snap()
	{
		snapX();
		snapY();
	}	
	
	protected final void snapX()
	{
		setX(getCurrentTile().getCenterX() - (this.getWidth() / 2));
	}
	
	protected final void snapY()
	{				
		setY(getCurrentTile().getCenterY() - (this.getHeight() / 2));		
	}
	
	@Override
	public String toString()
	{
		return "Actor \"" + this.getTextureName() + "\" @ " + (int) getX() + ", " + (int) getY();		
	}	

}

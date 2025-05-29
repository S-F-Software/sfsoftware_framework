package com.sevensoupcans.sfsoftware.game.actor;

import java.util.List;
import java.util.stream.Collectors;

import com.sevensoupcans.sfsoftware.game.Game;
import com.sevensoupcans.sfsoftware.util.MathUtils;
import com.sevensoupcans.sfsoftware.util.Updatable;
import com.sevensoupcans.sfsoftware.util.graphics.Sprite;
import com.sevensoupcans.sfsoftware.util.graphics.geometry.Quad;
import com.sevensoupcans.sfsoftware.util.graphics.particles.Particle;
import com.sevensoupcans.sfsoftware.util.tile.Tile;
import com.sevensoupcans.sfsoftware.util.tile.TileMap;
import com.sevensoupcans.sfsoftware.util.tile.pathfinding.AStarPathFinder;
import com.sevensoupcans.sfsoftware.util.tile.pathfinding.Path;
import com.sevensoupcans.sfsoftware.util.tile.pathfinding.PathFinder;

public class Actor extends Sprite 
{
	protected static int playingFieldX;
	protected static int playingFieldY;	
	
	public final static double getAngle(final Actor a, final Actor b)
	{
		return MathUtils.getAngle(a.getCenterX(), a.getCenterY(), b.getCenterX(), b.getCenterY());
	}
	
	@Deprecated
	public final static Cast getCast()
	{
		return Cast.getInstance();		
	}
			
	public final static void remove(final Actor a)
	{
		Cast.getInstance().remove(a);
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
		// Only update Actors with the specified z level.
		List<Actor> actors = 
				getCast().stream().filter(actor -> actor.getZOrder() == z).collect(Collectors.toList());
		
		for(Actor actor : actors)		
		{
			if(actor instanceof Updatable)
			{
				((Updatable) actor).update();
			}
			
			actor.draw();
		}				
	}
	
	private final Game ASSOCIATED_GAME;	
	
	private final int TILE_SIZE;
	
	protected float xDirection = 0;	
	
	protected float yDirection = 0;

	protected int speed = 4;
	
	private boolean isWalkable = true;	
	
	private int zOrder = 0;
	
	public Actor(float destX, float destY, String texture, float destWidth, float destHeight, Game associatedGame) 
	{
		this(destX, destY, texture, 0, 0, destWidth, destHeight, associatedGame);
	}	
	
	public Actor(float destX, float destY, String texture, int srcX, int srcY, float destWidth, float destHeight, Game associatedGame) 
	{
		super(destX, destY, texture);		
		
		this.ASSOCIATED_GAME = associatedGame;
		this.TILE_SIZE = associatedGame.getTileMap().getTileSize();
		
		this.setHeight(destHeight);
		this.setWidth(destWidth);
		
		this.setSrcX(srcX);
		this.setSrcY(srcY);			
		
		Cast.getInstance().add(this);
	}
	
	protected boolean collidingWithCast(final float dirX, final float dirY)
	{				
		boolean b = false;
		
		List<Actor> actors = getCast().stream()
								.filter(a -> !(a.equals(this)) && !(a.isWalkable()))
								.collect(Collectors.toList());		
		
		// Create a new Quad representing where we INTEND to move
		Quad temp = new Quad(getX() + (getSpeed() * dirX), getY() + (getSpeed() * dirY), getWidth(), getHeight());
		
		for(Actor a : actors)
		{
			if(a.collidingWith(temp))
			{
				// Return true - this will prevent movement from happening.
				b = true;
				// Call the collisionResult method to trigger any expected behavior
				a.collisionResult(this);
			}
		}
		
		return b;
	}

	protected int distanceToActor(final Actor a)
	{
		float x1 = a.getCenterX(); // (a.getX() + (a.getWidth() / 2));
		float x2 = getCenterX(); // (getX() + (getWidth() / 2));
		float y1 = a.getCenterY(); // (a.getY() + (a.getHeight() / 2));
		float y2 = getCenterY(); //(getY() + (getHeight() / 2));
		
		return (int) Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
	}
	
	protected final int distanceToPoint(final float x, final float y)
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
	
	protected final double getAngleFromPoint(final float x, final float y)
	{
		return MathUtils.getAngle(x, y, this.getCenterX(), this.getCenterY());
	}
	
	protected final double getAngleToActor(final Actor b)
	{
		return getAngle(this, b);
	}
	
	protected final double getAngleToPoint(final float x, final float y)
	{
		return MathUtils.getAngle(this.getCenterX(), this.getCenterY(), x, y);
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
	
	public float getXDirection()
	{
		return this.xDirection;
	}
	
	public float getYDirection()
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
			float xDirection = (float) Math.cos(directionAngle);
			float yDirection = (float) Math.sin(directionAngle);	
			
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
	protected boolean moveWithoutCheckingCollision(final float dirX, final float dirY)
	{
		super.move(this.getX() + (dirX * speed), this.getY() + (dirY * speed));
		return true;
	}	
	
	public void remove()
	{
		Cast.getInstance().remove(this);
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

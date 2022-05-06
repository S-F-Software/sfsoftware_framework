package com.sevensoupcans.sfsoftware.game.actor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import com.sevensoupcans.sfsoftware.game.actor.attributes.Permanent;

public class Cast implements List<Actor> {

	public final static Cast cast = new Cast(); 
	
	public static Cast getInstance()
	{
		return cast;
	}
	
	private final List<Actor> actors;
	
	private Cast() 
	{
		actors = new ArrayList<Actor>();
	}

	@Override
	public boolean add(Actor actor) 
	{
		// Do not add the Player to our cast vector as this vector is cleared with each new room
		if(!(this instanceof Permanent))
		{
			return actors.add(actor);
		}
		
		return false;
	}

	@Override
	public void add(int index, Actor actor) 
	{
		actors.add(index, actor);		
	}

	@Override
	public boolean addAll(Collection<? extends Actor> arg0) 
	{
		return actors.addAll(arg0);
	}

	@Override
	public boolean addAll(int index, Collection<? extends Actor> a) 
	{
		return actors.addAll(index, a);
	}

	@Override
	public void clear() 
	{
		for(int i = actors.size() - 1; i >= 0; i--)
			actors.get(i).remove();
	}

	@Override
	public boolean contains(Object obj) 
	{
		return actors.contains(obj);
	}

	@Override
	public boolean containsAll(Collection<?> arg0) 
	{
		return actors.containsAll(arg0);
	}

	@Override
	public Actor get(int index) 
	{
		return actors.get(index);
	}

	@Override
	public int indexOf(Object actor) {
		return actors.indexOf(actor);
	}

	@Override
	public boolean isEmpty() 
	{
		return actors.isEmpty();
	}

	@Override
	public Iterator<Actor> iterator() 
	{
		return actors.iterator();
	}

	@Override
	public int lastIndexOf(Object actor) 
	{
		return actors.lastIndexOf(actor);
	}

	@Override
	public ListIterator<Actor> listIterator() 
	{
		return actors.listIterator();
	}

	@Override
	public ListIterator<Actor> listIterator(int index) 
	{
		return actors.listIterator(index);
	}

	@Override
	public boolean remove(Object o) 
	{
		return actors.remove(o);
	}

	@Override
	public Actor remove(int index) {
		Actor a = actors.get(index);
		a.remove();
		
		return a;
	}

	@Override
	public boolean removeAll(Collection<?> c) 
	{
		// TODO As in other places, this should call each individual remove method on an Actor to trigger any associated behavior.
		return actors.removeAll(c);
	}

	@Override
	public boolean retainAll(Collection<?> c) 
	{
		return actors.retainAll(c);
	}

	@Override
	public Actor set(int index, Actor element) 
	{
		return actors.set(index, element);
	}

	@Override
	public int size() 
	{
		return actors.size();
	}

	@Override
	public List<Actor> subList(int fromIndex, int toIndex) 
	{
		return actors.subList(fromIndex, toIndex);
	}

	@Override
	public Object[] toArray() 
	{
		return actors.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) 
	{
		return actors.toArray(a);
	}

}

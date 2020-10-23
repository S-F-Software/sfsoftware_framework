package com.sevensoupcans.sfsoftware.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class SortedList<T> {

	private ArrayList<T> list = new ArrayList<T>();
	
	public T first()
	{
		return list.get(0);
	}
	
	public void clear()
	{
		this.list.clear();
	}
	
	public void add(T o, Comparator<T> c)
	{
		this.list.add(o);
		Collections.sort(this.list, c);
	}
	
	public void remove(T o)
	{
		this.list.remove(o);
	}
	
	public int size()
	{
		return this.list.size();
	}
	
	public boolean contains(T o)
	{
		return this.list.contains(o);
	}

	public T get(int index) 
	{
		return this.list.get(index);
	}
	
	

}

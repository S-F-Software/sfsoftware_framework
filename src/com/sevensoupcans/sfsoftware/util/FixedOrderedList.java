package com.sevensoupcans.sfsoftware.util;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Provides a fixed capacity pile of objects. Once the pile is full, the oldest
 * in the pile is removed to make room for the most-recently added.
 * 
 * @param <T>
 */
public class FixedOrderedList<T>
{
	private final int LIST_CAPACITY;
	
	private final Deque<T> CONTENTS = new ArrayDeque<>();
	
	public FixedOrderedList(int capacity)
	{
		if (capacity < 1)
			throw new IllegalArgumentException("List capacity must be positive");
		
		LIST_CAPACITY = capacity;
	}
	
	public void add(T object)
	{
		if (CONTENTS.size() >= LIST_CAPACITY)
		{
			CONTENTS.removeFirst();
		}

		CONTENTS.addLast(object);
	}

	public int getCapacity()
	{
		return LIST_CAPACITY;
	}
	
	public T getOldest()
	{
		return CONTENTS.peekFirst();
	}

	public T getLatest()
	{
		return CONTENTS.peekLast();
	}

	public int size()
	{
		return CONTENTS.size();
	}

}
package com.sevensoupcans.sfsoftware.util;

import java.util.Objects;

/**
 * An extension of the FixedOrderedList that enforces uniqueness for the most-recent
 * addition to the List. 
 * 
 * E.G. we try to add object X when object X is already the latest addition.
 * 
 * @param <T>
 */
public class FixedOrderedDistinctList<T> extends FixedOrderedList<T> 
{

	public FixedOrderedDistinctList(int capacity) 
	{
		super(capacity);
	}

	@Override
	public void add(T object)
	{
		if (size() > 0 && Objects.equals(getLatest(), object))
		{
			return;
		}
		
		super.add(object);
	}
}
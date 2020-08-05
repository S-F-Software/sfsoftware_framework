package com.sevensoupcans.sfsoftware.util.ui;

import java.util.ArrayList;

/*
 * Extension of the ArrayList class that logs each item added.
 */
public class LoggedList<E> extends ArrayList<E> 
{

	private static final long serialVersionUID = 1L;

	@Override
	public boolean add(E e)
	{
		System.out.println(e.toString());		
		return super.add(e);
	}
	
}

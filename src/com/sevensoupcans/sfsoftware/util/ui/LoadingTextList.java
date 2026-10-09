package com.sevensoupcans.sfsoftware.util.ui;

import java.awt.Font;
import java.util.ArrayList;

import com.sevensoupcans.sfsoftware.game.Game;
import com.sevensoupcans.sfsoftware.util.graphics.font.BitmapFont;

public final class LoadingTextList<E> extends LoggedList<E> 
{	
	private static final long serialVersionUID = 1L;

	private Game game;
	private BitmapFont loadingFont;
	
	public LoadingTextList(Game game)
	{
		this.game = game;
		this.loadingFont = new BitmapFont(game.getDefaultFontName(), Font.BOLD, 20);
	}
	
	@Override
	public boolean add(E e)
	{
		ArrayList<String> s = new ArrayList<String>();
		for(Object obj : this)
		{
			if(obj instanceof String) s.add((String)obj);
		}
		
		game.drawLoadingScreen(s, loadingFont);
		
		return super.add(e);
	}
}

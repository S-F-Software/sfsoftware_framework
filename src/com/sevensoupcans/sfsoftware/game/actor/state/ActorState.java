package com.sevensoupcans.sfsoftware.game.actor.state;

import com.sevensoupcans.sfsoftware.game.actor.Actor;
import com.sevensoupcans.sfsoftware.util.input.InputDevice;

public interface ActorState<T extends Actor>
{
	public void enterState(T actor);
	public void exitState();
	public boolean pollInput(InputDevice inputDevice); 
	public void update();
}

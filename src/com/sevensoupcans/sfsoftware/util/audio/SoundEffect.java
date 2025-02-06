package com.sevensoupcans.sfsoftware.util.audio;

import org.newdawn.slick.openal.Audio;

public class SoundEffect {

	private final Audio audio;
	private final String name;
	
	private float volume;
	
	public SoundEffect(final String soundName) {
		this.name = soundName;
		this.audio = Sound.getSoundEffect(soundName);
		this.volume = Sound.getSoundEffectVolume();
	}
	
	public String getName()
	{
		return this.name;
	}
	
	public void loop()
	{
		loop(1.0f);
	}
	
	public void loop(final float pitch)
	{
		play(pitch, volume, true);
	}
	
	public void play()
	{
		play(1.0f, volume, false);
	}
	
	public void play(final float pitch, final float volume, final boolean loop)
	{
		this.audio.playAsSoundEffect(pitch, volume, loop);
	}
	
	public void setVolume(final float volume)
	{
		this.volume = (volume * Sound.getSoundEffectVolume());
	}
	
	public void stop()
	{
		this.audio.stop();
	}

}

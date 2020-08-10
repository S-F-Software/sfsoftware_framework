package com.sevensoupcans.sfsoftware.util;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.lwjgl.openal.AL;
import org.newdawn.slick.openal.Audio;
import org.newdawn.slick.openal.AudioLoader;
import org.newdawn.slick.openal.SoundStore;
import org.newdawn.slick.util.ResourceLoader;

import com.sevensoupcans.sfsoftware.util.resources.ClasspathHelper;
import com.sevensoupcans.sfsoftware.util.resources.FileUtils;

public abstract class Sound 
{
	public static final String AUDIO_PATH = "res/audio";
	private static boolean verbose = false;
	private static float musicVolume = 1.0f;
	private static float soundEffectVolume = 1.0f;
	private static Audio oggStream;	
	private static HashMap<String, Audio> soundLib = new HashMap<String, Audio>();
	private static HashMap<String, Audio> musicLib = new HashMap<String, Audio>();		
	
	public static void destroy()
	{
		AL.destroy();
	}
	
	public static float getMusicVolume()
	{
		return musicVolume;
	}
	
	public static float getSoundEffectVolume()
	{
		return soundEffectVolume;
	}
	
	public static void pollSoundStore(int i)
	{
		SoundStore.get().poll(i);
	}
	
	public static boolean isMusicPlaying()
	{
		if(oggStream != null)
		{
			return oggStream.isPlaying();
		}
		else
		{
			return false;
		}
	}
	
	public static void loadAudio(Class<?> invokingClass)
	{
		loadAudio(AUDIO_PATH, invokingClass);
	}
	
	public static void loadAudio(String audioResourcePath, Class<?> invokingClass)
	{		
		loadOggs(audioResourcePath, invokingClass);
		loadWaves(audioResourcePath, invokingClass);
	}
	
	public static HashMap<String, Audio> loadOggs(String audioResourcePath, Class<?> invokingClass)
	{
		try 
		{			 	
			   String files;
			   
			   /*File folder = new File(path);
			   File[] listOfFiles = folder.listFiles();*/ 
			  
			   //String[] listOfFiles = FileUtils.getResourceListing(Graphics.class, AUDIO_PATH);
			   String[] listOfFiles = FileUtils.getResourceListing(audioResourcePath, "ogg", ClasspathHelper.getClasspath(invokingClass));
			   for (int i = 0; i < listOfFiles.length; i++) 
			   {			  
				    /*if (listOfFiles[i].isFile()) 
				    {
				    	files = listOfFiles[i].getName();*/
				   		files = listOfFiles[i];
				        if (files.endsWith(".ogg") || files.endsWith(".OGG"))
				        {			           
				        	//System.out.println("Loading sound res/audio/" + files);							        	
				        	Audio temp = AudioLoader.getStreamingAudio("OGG", ResourceLoader.getResource("res/audio/" + files));
				        	musicLib.put(files.substring(0, files.indexOf(".")), temp);
				        	if(verbose)
				        	{
				        		System.out.println("Loaded music file '" + files.substring(0, files.indexOf(".")) + "'");
				        	}
				        }			        
				     //}
			   }				          
		} 
		catch (IOException | URISyntaxException e) 
		{
			e.printStackTrace();
		}	
		
		return soundLib;
	}	
	
	public static HashMap<String, Audio> loadWaves(String audioResourcePath, Class<?> invokingClass)
	{
		try 
		{			  	  
			   String files;
			   
			   /*File folder = new File(path);
			   File[] listOfFiles = folder.listFiles();*/ 
			   //String[] listOfFiles = FileUtils.getResourceListing(Graphics.class, AUDIO_PATH);
			   String[] listOfFiles = FileUtils.getResourceListing(audioResourcePath, "wav", ClasspathHelper.getClasspath(invokingClass));
			   for (int i = 0; i < listOfFiles.length; i++) 
			   {			  
				    /*if (listOfFiles[i].isFile()) 
				    {
				    	files = listOfFiles[i].getName();*/
				   		files = listOfFiles[i];
				        if (files.endsWith(".wav") || files.endsWith(".WAV"))
				        {			           
				        	//System.out.println("Loading sound res/audio/" + files);			
				        	Audio temp = AudioLoader.getAudio("WAV", ResourceLoader.getResourceAsStream("res/audio/" + files));
				        	soundLib.put(files.substring(0, files.indexOf(".")), temp);
				        	if(verbose)
				        	{
				        		System.out.println("Loaded sound '" + files.substring(0, files.indexOf(".")) + "'");
				        	}
				        }			        
				     //}
			   }				          
		} 
		catch (IOException | URISyntaxException e) 
		{
			e.printStackTrace();
		}	
		
		return soundLib;
	}
	
	public static void playSound(String soundName)
	{		
		Audio temp = soundLib.get(soundName);
		if(temp != null)
		{
			temp.playAsSoundEffect(1.0f, soundEffectVolume, false);
		}
	}

	public static String playMusic()
	{
		String[] s = {};
		return playMusic(s);
	}
	
	public static String playMusic(String[] doNotPlayList)
	{		
		// Play a random song not contained on the "do not play" list!
		List<String> keysAsArray = new ArrayList<String>(musicLib.keySet());
		if(doNotPlayList != null)
		{				
			keysAsArray.removeAll(Arrays.asList(doNotPlayList));
		}
		Random r = new Random();
		String songName = keysAsArray.get(r.nextInt(keysAsArray.size()));		
		
		playMusic(songName, true);
		
		return songName;
	}
	
	public static void playMusic(String songName)
	{
		playMusic(songName, false);
	}
	
	public static void playMusic(String songName, boolean loopSong)
	{		
		oggStream = musicLib.get(songName);
		if(oggStream != null)
		{
			// Gain parameter doesn't seem to work...
			oggStream.playAsMusic(1.0f, musicVolume, loopSong);
		}
	}
	
	public static void setMusicPitch(float pitch)
	{
		SoundStore.get().setMusicPitch(pitch);
	}
	
	public static void setMusicVolume(float volume)
	{
		SoundStore.get().setMusicVolume(volume); 
		musicVolume = volume;
	}
	
	public static void setSoundEffectVolume(float volume)
	{
		soundEffectVolume = volume;
	}
	
	public static void stopMusic()
	{
		if(oggStream != null) {
			oggStream.stop();
		}
	}
}

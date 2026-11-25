package com.sevensoupcans.sfsoftware.util.audio;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

import org.lwjgl.openal.AL;
import org.newdawn.slick.openal.Audio;
import org.newdawn.slick.openal.AudioLoader;
import org.newdawn.slick.openal.SoundStore;

import com.sevensoupcans.sfsoftware.util.resources.ClasspathHelper;
import com.sevensoupcans.sfsoftware.util.resources.FileUtils;
import com.sevensoupcans.sfsoftware.util.resources.ResourceUtils;

public final class Sound
{
    public static final String AUDIO_PATH = "res/audio";

    private static boolean verbose = false;
    private static float musicVolume = 1.0f;
    private static float soundEffectVolume = 1.0f;
    private static Optional<Audio> oggStream = Optional.empty();
    private static Map<String, Audio> soundLib = new HashMap<String, Audio>();
    private static Map<String, Audio> musicLib = new HashMap<String, Audio>();

    public static void destroy()
    {
        AL.destroy();
    }

    public static float getMusicVolume()
    {
        return musicVolume;
    }

    public static Audio getSoundEffect(String soundName)
    {
        return soundLib.get(soundName);
    }

    public static float getSoundEffectVolume()
    {
        return soundEffectVolume;
    }

    public static boolean isMusicPlaying()
    {
        return oggStream.isPresent() && oggStream.get().isPlaying();
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

    public static Map<String, Audio> loadOggs(String audioResourcePath,
            Class<?> invokingClass)
    {
        try
        {
            Arrays.stream(FileUtils.getResourceListing(audioResourcePath, "ogg",
                            ClasspathHelper.getClasspath(invokingClass)))
                .filter(file -> file.toLowerCase().endsWith(".ogg"))
                .forEach(file ->
                {
                    String name = file.substring(0, file.indexOf('.'));

                    try
                    {
                        String resourcePath = audioResourcePath + "/" + file;
                        Audio audio = AudioLoader.getStreamingAudio("OGG",
                                ResourceUtils.getResource(resourcePath, invokingClass));
                        musicLib.put(name, audio);

                        if (verbose)
                        {
                            System.out.println("Loaded music file '" + name + "'");
                        }
                    }
                    catch (IOException e)
                    {
                        System.out.println("Failed to load music file '" + name
                                + "': " + e.getMessage());
                    }
                });
        }
        catch (IOException | URISyntaxException e)
        {
            e.printStackTrace();
        }

        return soundLib;
    }

    public static Map<String, Audio> loadWaves(String audioResourcePath,
            Class<?> invokingClass)
    {
        try
        {
            Arrays.stream(FileUtils.getResourceListing(audioResourcePath, "wav",
                            ClasspathHelper.getClasspath(invokingClass)))
                .filter(file -> file.toLowerCase().endsWith(".wav"))
                .forEach(file ->
                {
                    String name = file.substring(0, file.indexOf('.'));

                    try
                    {
                        String resourcePath = audioResourcePath + "/" + file;
                        Audio audio;

                        try (InputStream stream = ResourceUtils.getResourceAsStream(
                                resourcePath, invokingClass))
                        {
                            audio = AudioLoader.getAudio("WAV", stream);
                        }

                        soundLib.put(name, audio);

                        if (verbose)
                        {
                            System.out.println("Loaded sound '" + name + "'");
                        }
                    }
                    catch (IOException e)
                    {
                        System.out.println("Failed to load sound file '" + name
                                + "': " + e.getMessage());
                    }
                });
        }
        catch (IOException | URISyntaxException e)
        {
            e.printStackTrace();
        }

        return soundLib;
    }

    public static String playMusic()
    {
        return playMusic(new String[0]);
    }

    public static void playMusic(String songName)
    {
        playMusic(songName, false);
    }

    public static void playMusic(String songName, boolean loopSong)
    {
        oggStream = Optional.ofNullable(musicLib.get(songName));
        if (oggStream.isPresent())
        {
            oggStream.get().playAsMusic(1.0f, musicVolume, loopSong);
        }
    }

    public static String playMusic(String[] doNotPlayList)
    {
        List<String> keys = new ArrayList<String>(musicLib.keySet());
        if (doNotPlayList != null)
        {
            keys.removeAll(Arrays.asList(doNotPlayList));
        }

        if (keys.isEmpty())
        {
            return null;
        }

        String songName = keys.get(new Random().nextInt(keys.size()));
        playMusic(songName, true);
        return songName;
    }

    public static Sound playSound(String soundName)
    {
        return playSound(soundName, 1.0f);
    }

    public static Sound playSound(String soundName, float pitch)
    {
        Audio audio = soundLib.get(soundName);
        if (audio == null)
        {
            return null;
        }

        audio.playAsSoundEffect(pitch, soundEffectVolume, false);
        return new Sound(audio, soundName);
    }

    public static void pollSoundStore(int i)
    {
        SoundStore.get().poll(i);
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
        if (oggStream.isPresent())
        {
            oggStream.get().stop();
        }
    }

    private Audio associatedAudio;
    private String name;

    public Sound(Audio audio, String name)
    {
        associatedAudio = audio;
        this.name = name;
    }

    public String getName()
    {
        return name;
    }

    public boolean isPlaying()
    {
        return associatedAudio.isPlaying();
    }

    public void play()
    {
        play(1.0f, false);
    }

    public void play(float pitch)
    {
        play(pitch, false);
    }

    public void play(float pitch, boolean loop)
    {
        if (associatedAudio != null)
        {
            associatedAudio.playAsSoundEffect(pitch, soundEffectVolume, loop);
        }
    }

    public void stop()
    {
        if (associatedAudio != null)
        {
            associatedAudio.stop();
        }
    }
}

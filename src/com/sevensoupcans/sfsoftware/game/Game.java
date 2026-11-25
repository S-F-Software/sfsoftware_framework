package com.sevensoupcans.sfsoftware.game;

import java.awt.Font;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.prefs.Preferences;
import java.util.stream.Collectors;

import org.newdawn.slick.openal.SoundStore;

import com.sevensoupcans.sfsoftware.util.Clock;
import com.sevensoupcans.sfsoftware.util.audio.Sound;
import com.sevensoupcans.sfsoftware.util.graphics.Graphics;
import com.sevensoupcans.sfsoftware.util.graphics.RGBA;
import com.sevensoupcans.sfsoftware.util.graphics.Sprite;
import com.sevensoupcans.sfsoftware.util.graphics.font.BitmapFont;
import com.sevensoupcans.sfsoftware.util.graphics.geometry.Quad;
import com.sevensoupcans.sfsoftware.util.input.InputDevice;
import com.sevensoupcans.sfsoftware.util.input.Kboard;
import com.sevensoupcans.sfsoftware.util.input.Mouse;
import com.sevensoupcans.sfsoftware.util.resources.FileUtils;
import com.sevensoupcans.sfsoftware.util.tile.Tile;
import com.sevensoupcans.sfsoftware.util.tile.TileMap;
import com.sevensoupcans.sfsoftware.util.ui.GUIElement;
import com.sevensoupcans.sfsoftware.util.ui.LoggedList;
import com.sevensoupcans.sfsoftware.util.ui.TextConsole;

public abstract class Game {
    private final TextConsole CONSOLE = new TextConsole(this);
    private final PrintStream DEFAULT_SYSTEM_OUT = System.out;
    private GameState gameState = GameState.TITLE_SCREEN;
    private int screenHeight = 864;
    private int screenWidth = 1152;
    private long gameCounter;
    private boolean running = true;
    private String gameTitle;
    private ArrayList<GUIElement> guiElements = new ArrayList<GUIElement>();
    private boolean debugMode;
    protected InputDevice inputDevice = new Kboard();
    public boolean isPaused;
    public boolean vsync;
    private final Preferences PREFERENCES = Preferences.userNodeForPackage(this.getClass());

    public abstract boolean executeTextConsoleCommand(String command);
    public abstract int getPlayingFieldHeight();
    public abstract int getPlayingFieldWidth();
    public abstract String getDefaultFontName();
    public abstract BitmapFont getGameFont();
    public abstract TileMap getTileMap();
    protected abstract void start();

    public final void addGUIElement(GUIElement e) { guiElements.add(e); }
    public final void drawLoadingScreen(ArrayList<String> loadingDetails, BitmapFont font) {
        Graphics.clear();
        int size = loadingDetails.size();
        for (String text : loadingDetails) {
            font.drawString(35, (getScreenHeight() - 60) - (font.getHeight() * (size - 1)), text);
            size--;
        }
        Quad.draw(0, 0, getScreenWidth(), getScreenHeight() / 2, 0, 0, 0, 1);
        Quad.draw(0, getScreenHeight() / 2, getScreenWidth(), getScreenHeight() / 2,
                new RGBA(0, 0, 0, 1), new RGBA(0, 0, 0, 1), new RGBA(0, 0, 0, 0), new RGBA(0, 0, 0, 0));
        Graphics.update();
    }
    protected final void end() {
        Graphics.destroy();
        Sound.destroy();
        System.out.println("Bye, bye!");
        System.exit(0);
    }
    protected final String getClassOriginPath() {
        String path = getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
        if (path.indexOf("bin/") > -1) path = path.substring(0, path.indexOf("bin/"));
        if (FileUtils.getDirectorySeparator() == '\\' && path.startsWith("/")) path = path.substring(1);
        return path;
    }
    public final String getGameCounter() { return Clock.getFormattedHoursMinutes(gameCounter); }
    public final GameState getGameState() { return gameState; }
    protected final ArrayList<GUIElement> getGUIElements() { return guiElements; }
    public final InputDevice getInputDevice() { return inputDevice; }
    public final Preferences getPreferences() { return PREFERENCES; }
    protected String getResourcePath() { return getClassOriginPath() + "res/"; }
    public final int getScreenHeight() { return screenHeight; }
    public final int getScreenWidth() { return screenWidth; }
    public TextConsole getTextConsole() { return CONSOLE; }
    public int getTileSize() { return Tile.getDefaultTileSize(); }
    public final int getGameHashCode() { return gameTitle.hashCode(); }
    public final String getGameTitle() { return gameTitle; }
    public final String incrementGameCounter() { gameCounter++; return getGameCounter(); }
    public final boolean inDebugMode() { return debugMode; }
    protected void init() { init(false); }
    protected void init(int screenWidth, int screenHeight, boolean loadResources) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        init(loadResources);
    }
    protected void init(boolean loadResources) {
        System.out.println(getGameTitle() + ", (c) S&F Software, 2020");
        Graphics.setAppIcon(getResourcePath() + "graphics/icon16.png", getResourcePath() + "graphics/icon32.png");
        Graphics.initGL(getScreenWidth(), getScreenHeight(), getGameTitle());
        Graphics.setDisplayMode(getScreenWidth(), getScreenHeight(), getPreferences().getBoolean("fullscreen", false));
        vsync = getPreferences().getBoolean("vsync", false);
        Graphics.setVSync(vsync);
        Mouse.setGrabbed(Graphics.isFullScreen());
        setGameState(GameState.TITLE_SCREEN);
        if (loadResources) {
            BitmapFont loadingFont = new BitmapFont("Verdana", Font.BOLD, 20);
            LoggedList<String> loadingText = new LoggedList<String>();
            loadingText.add("Loading " + getGameTitle() + "...");
            drawLoadingScreen(loadingText, loadingFont);
            loadingText.add("Loading textures...");
            drawLoadingScreen(loadingText, loadingFont);
            Graphics.loadTextures(getClassOriginPath() + Graphics.DEFAULT_GRAPHICS_FILE_PATH, "png", getClass());
            loadingText.add("Loading audio...");
            drawLoadingScreen(loadingText, loadingFont);
            Sound.loadAudio(getClassOriginPath() + Sound.AUDIO_PATH, getClass());
        }
    }
    public final boolean isRunning() { return running; }
    public void newGame() { Sound.stopMusic(); setGameState(GameState.INGAME); }
    public final void removeGUIElement(GUIElement e) { guiElements.remove(e); }
    public final void setDebugMode(boolean debugMode) {
        this.debugMode = debugMode;
        System.setOut(debugMode ? DEFAULT_SYSTEM_OUT : CONSOLE.getPrintStream());
    }
    /** Sets the window dimensions and fullscreen state. */
    public static void setDisplayMode(int width, int height, boolean fullscreen) {
        Graphics.setDisplayMode(width, height, fullscreen);
    }
    public final void setGameState(GameState state) { gameState = state; }
    protected final void setGameTitle(String title) { gameTitle = title; }
    public final void setInputDevice(InputDevice device) { inputDevice = device; }
    public final void setRunning(boolean running) { this.running = running; }
    protected void updateAndDrawGUIElements() {
        guiElements.forEach(e -> { e.update(); e.draw(); });
        guiElements.removeAll(guiElements.stream().filter(GUIElement::shouldRemove).collect(Collectors.toList()));
    }
    protected void updateLogo() {
        int logoState = 0;
        int logoWait = 0;
        float logoAlpha = 0;
        Sound.playMusic("logo", false);
        while (logoState != 2 && isRunning() && !Graphics.isCloseRequested()) {
            Graphics.clear();
            if (logoState == 0) {
                if (logoAlpha < 1) logoAlpha += 0.02f;
                else if (logoWait < 50) logoWait++;
                else logoState++;
            } else if (logoState == 1) {
                if (logoAlpha > 0) logoAlpha -= 0.005f;
                else logoState++;
            }
            Sprite.draw(getScreenWidth() / 2 - 44, getScreenHeight() / 2 - 44, "sflogo",
                    89, 89, 0, 0, 89, 89, 1, 1, 1, logoAlpha);
            SoundStore.get().poll(0);
            Graphics.update(true);
        }
        Sound.stopMusic();
    }
}

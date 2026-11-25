package com.sevensoupcans.sfsoftware.util.input;

import static org.lwjgl.glfw.GLFW.*;

import java.util.ArrayList;
import java.util.List;

import com.sevensoupcans.sfsoftware.util.graphics.Graphics;

// Keyboard input backed by GLFW (LWJGL 3). Key values are GLFW key codes.
public final class Kboard implements InputDevice
{
    public static final int[] leftKeys = { GLFW_KEY_A, GLFW_KEY_LEFT };
    public static final int[] rightKeys = { GLFW_KEY_D, GLFW_KEY_RIGHT };
    public static final int[] upKeys = { GLFW_KEY_W, GLFW_KEY_UP };
    public static final int[] downKeys = { GLFW_KEY_S, GLFW_KEY_DOWN };

    public static final int[] alphabetKeys = {
        GLFW_KEY_Q, GLFW_KEY_W, GLFW_KEY_E, GLFW_KEY_R, GLFW_KEY_T, GLFW_KEY_Y, GLFW_KEY_U,
        GLFW_KEY_I, GLFW_KEY_O, GLFW_KEY_P, GLFW_KEY_A, GLFW_KEY_S, GLFW_KEY_D, GLFW_KEY_F,
        GLFW_KEY_G, GLFW_KEY_H, GLFW_KEY_J, GLFW_KEY_K, GLFW_KEY_L, GLFW_KEY_Z, GLFW_KEY_X,
        GLFW_KEY_C, GLFW_KEY_V, GLFW_KEY_B, GLFW_KEY_N, GLFW_KEY_M
    };

    public static final int[] numberKeys = {
        GLFW_KEY_0, GLFW_KEY_1, GLFW_KEY_2, GLFW_KEY_3, GLFW_KEY_4, GLFW_KEY_5,
        GLFW_KEY_6, GLFW_KEY_7, GLFW_KEY_8, GLFW_KEY_9, GLFW_KEY_KP_0, GLFW_KEY_KP_1,
        GLFW_KEY_KP_2, GLFW_KEY_KP_3, GLFW_KEY_KP_4, GLFW_KEY_KP_5, GLFW_KEY_KP_6,
        GLFW_KEY_KP_7, GLFW_KEY_KP_8, GLFW_KEY_KP_9
    };

    public static final int[] numPadKeys = {
        GLFW_KEY_KP_MULTIPLY, GLFW_KEY_KP_DIVIDE, GLFW_KEY_KP_DECIMAL, GLFW_KEY_KP_ADD,
        GLFW_KEY_KP_SUBTRACT, GLFW_KEY_KP_0, GLFW_KEY_KP_1, GLFW_KEY_KP_2, GLFW_KEY_KP_3,
        GLFW_KEY_KP_4, GLFW_KEY_KP_5, GLFW_KEY_KP_6, GLFW_KEY_KP_7, GLFW_KEY_KP_8, GLFW_KEY_KP_9
    };

    public static final int[] shiftKeys = { GLFW_KEY_LEFT_SHIFT, GLFW_KEY_RIGHT_SHIFT };

    public static final int[] symbolKeys = {
        GLFW_KEY_COMMA, GLFW_KEY_PERIOD, GLFW_KEY_SLASH, GLFW_KEY_BACKSLASH, GLFW_KEY_APOSTROPHE,
        GLFW_KEY_SEMICOLON, GLFW_KEY_LEFT_BRACKET, GLFW_KEY_RIGHT_BRACKET, GLFW_KEY_GRAVE_ACCENT,
        GLFW_KEY_EQUAL, GLFW_KEY_MINUS
    };

    public static final int KEY_A = GLFW_KEY_A;
    public static final int KEY_D = GLFW_KEY_D;
    public static final int KEY_E = GLFW_KEY_E;
    public static final int KEY_ESCAPE = GLFW_KEY_ESCAPE;
    public static final int KEY_F = GLFW_KEY_F;
    public static final int KEY_GRAVE = GLFW_KEY_GRAVE_ACCENT;
    public static final int KEY_LBRACKET = GLFW_KEY_LEFT_BRACKET;
    public static final int KEY_LCONTROL = GLFW_KEY_LEFT_CONTROL;
    public static final int KEY_LSHIFT = GLFW_KEY_LEFT_SHIFT;
    public static final int KEY_PAUSE = GLFW_KEY_PAUSE;
    public static final int KEY_RBRACKET = GLFW_KEY_RIGHT_BRACKET;
    public static final int KEY_RCONTROL = GLFW_KEY_RIGHT_CONTROL;
    public static final int KEY_RETURN = GLFW_KEY_ENTER;
    public static final int KEY_RSHIFT = GLFW_KEY_RIGHT_SHIFT;
    public static final int KEY_S = GLFW_KEY_S;
    public static final int KEY_SPACE = GLFW_KEY_SPACE;
    public static final int KEY_TAB = GLFW_KEY_TAB;
    public static final int KEY_W = GLFW_KEY_W;

    private static final List<Integer> lastKeyState = new ArrayList<Integer>();

    private int AButtonKey = GLFW_KEY_SPACE;
    private int BButtonKey = GLFW_KEY_LEFT_CONTROL;
    private int XButtonKey = GLFW_KEY_LEFT_SHIFT;
    private int YButtonKey = GLFW_KEY_RIGHT_CONTROL;
    private int BackButtonKey = GLFW_KEY_ESCAPE;
    private int ShoulderLeftKey = GLFW_KEY_LEFT_BRACKET;
    private int ShoulderRightKey = GLFW_KEY_RIGHT_BRACKET;
    private int StartButtonKey = GLFW_KEY_ENTER;
    private int[] pauseKeys = { GLFW_KEY_PAUSE };

    private static long window()
    {
        long window = Graphics.getWindow();
        if (window == 0L)
        {
            throw new IllegalStateException("The GLFW window has not been initialized");
        }
        return window;
    }

    public static String getKeyName(int key)
    {
        String result = glfwGetKeyName(key, 0);
        if (key == GLFW_KEY_LEFT_CONTROL)
        {
            result = "L Ctrl";
        }
        else if (key == GLFW_KEY_RIGHT_CONTROL)
        {
            result = "R Ctrl";
        }
        else if (result == null)
        {
            result = keyNameFallback(key);
        }
        return result.toUpperCase();
    }

    private static String keyNameFallback(int key)
    {
        switch (key)
        {
            case GLFW_KEY_ESCAPE:
                return "Escape";
            case GLFW_KEY_ENTER:
                return "Enter";
            case GLFW_KEY_TAB:
                return "Tab";
            case GLFW_KEY_SPACE:
                return "Space";
            case GLFW_KEY_LEFT_SHIFT:
                return "Left Shift";
            case GLFW_KEY_RIGHT_SHIFT:
                return "Right Shift";
            case GLFW_KEY_LEFT_ALT:
                return "Left Alt";
            case GLFW_KEY_RIGHT_ALT:
                return "Right Alt";
            case GLFW_KEY_LEFT:
                return "Left";
            case GLFW_KEY_RIGHT:
                return "Right";
            case GLFW_KEY_UP:
                return "Up";
            case GLFW_KEY_DOWN:
                return "Down";
            default:
                return "Key " + key;
        }
    }

    public static boolean isKeyDown(int key)
    {
        return glfwGetKey(window(), key) == GLFW_PRESS;
    }

    public static boolean isKeyDown(int[] keyGroup)
    {
        for (int key : keyGroup)
        {
            if (isKeyDown(key))
            {
                return true;
            }
        }
        return false;
    }

    public static boolean keyPressed(int key)
    {
        return isKeyDown(key) && !lastKeyState.contains(key);
    }

    public static boolean keyPressed(int[] keyGroup)
    {
        for (int key : keyGroup)
        {
            if (keyPressed(key))
            {
                return true;
            }
        }
        return false;
    }

    // Saves the current keyboard state; call once after processing each frame.
    public static void storeKBState()
    {
        lastKeyState.clear();
        long window = window();
        for (int key = GLFW_KEY_SPACE; key <= GLFW_KEY_LAST; key++)
        {
            if (glfwGetKey(window, key) == GLFW_PRESS)
            {
                lastKeyState.add(key);
            }
        }
    }

    @Override
    public boolean isButtonADown()
    {
        return isKeyDown(AButtonKey);
    }

    @Override
    public boolean isButtonBDown()
    {
        return isKeyDown(BButtonKey);
    }

    @Override
    public boolean isButtonShoulderLeftDown()
    {
        return isKeyDown(ShoulderLeftKey);
    }

    @Override
    public boolean isButtonShoulderRightDown()
    {
        return isKeyDown(ShoulderRightKey);
    }

    @Override
    public boolean isButtonXDown()
    {
        return isKeyDown(XButtonKey);
    }

    @Override
    public boolean isButtonYDown()
    {
        return isKeyDown(YButtonKey);
    }

    @Override
    public boolean isDownDown()
    {
        return isKeyDown(downKeys);
    }

    @Override
    public boolean isLeftDown()
    {
        return isKeyDown(leftKeys);
    }

    @Override
    public boolean isRightDown()
    {
        return isKeyDown(rightKeys);
    }

    @Override
    public boolean isUpDown()
    {
        return isKeyDown(upKeys);
    }

    @Override
    public void poll()
    {
    }

    public String getAButtonKeyName()
    {
        return getKeyName(AButtonKey);
    }

    public String getBButtonKeyName()
    {
        return getKeyName(BButtonKey);
    }

    public String getXButtonKeyName()
    {
        return getKeyName(XButtonKey);
    }

    public String getYButtonKeyName()
    {
        return getKeyName(YButtonKey);
    }

    public String getBackButtonKeyName()
    {
        return getKeyName(BackButtonKey);
    }

    public String getStartButtonKeyName()
    {
        return getKeyName(StartButtonKey);
    }

    public String getLeftShoulerButtonKeyName()
    {
        return getKeyName(ShoulderLeftKey);
    }

    public String getRightShoulerButtonKeyName()
    {
        return getKeyName(ShoulderRightKey);
    }

    public void mapKeyAsAButton(int key)
    {
        AButtonKey = key;
    }

    public void mapKeyAsBButton(int key)
    {
        BButtonKey = key;
    }

    public void mapKeyAsBackButton(int key)
    {
        BackButtonKey = key;
    }

    public void mapKeyAsStartButton(int key)
    {
        StartButtonKey = key;
    }

    public void mapKeyAsXButton(int key)
    {
        XButtonKey = key;
    }

    public void mapKeyAsYButton(int key)
    {
        YButtonKey = key;
    }

    public void mapKeyAsLeftShoulderButton(int key)
    {
        ShoulderLeftKey = key;
    }

    public void mapKeyAsRightShoulderButton(int key)
    {
        ShoulderRightKey = key;
    }

    public void setPauseKeys(int[] keys)
    {
        pauseKeys = keys;
    }

    @Override
    public void storeState()
    {
        storeKBState();
    }

    @Override
    public boolean wasBackPressed()
    {
        return keyPressed(BackButtonKey);
    }

    @Override
    public boolean wasButtonAPressed()
    {
        return keyPressed(AButtonKey);
    }

    @Override
    public boolean wasButtonBPressed()
    {
        return keyPressed(BButtonKey);
    }

    @Override
    public boolean wasButtonShoulderLeftPressed()
    {
        return keyPressed(ShoulderLeftKey);
    }

    @Override
    public boolean wasButtonShoulderRightPressed()
    {
        return keyPressed(ShoulderRightKey);
    }

    @Override
    public boolean wasButtonXPressed()
    {
        return keyPressed(XButtonKey);
    }

    @Override
    public boolean wasButtonYPressed()
    {
        return keyPressed(YButtonKey);
    }

    @Override
    public boolean wasDownPressed()
    {
        return keyPressed(downKeys);
    }

    @Override
    public boolean wasLeftPressed()
    {
        return keyPressed(leftKeys);
    }

    @Override
    public boolean wasPausedPressed()
    {
        return keyPressed(pauseKeys);
    }

    @Override
    public boolean wasRightPressed()
    {
        return keyPressed(rightKeys);
    }

    @Override
    public boolean wasStartPressed()
    {
        return keyPressed(StartButtonKey);
    }

    @Override
    public boolean wasUpPressed()
    {
        return keyPressed(upKeys);
    }
}

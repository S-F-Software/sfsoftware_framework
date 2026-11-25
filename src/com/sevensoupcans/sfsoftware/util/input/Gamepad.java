package com.sevensoupcans.sfsoftware.util.input;

import static org.lwjgl.glfw.GLFW.*;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

/** Joystick input backed by GLFW (LWJGL 3). GLFW does not expose rumblers. */
public final class Gamepad implements InputDevice {
    private static final float DEFAULT_DEAD_ZONE = 0.2f;
    private static final int DEFAULT_BUTTON_A = 0;
    private static final int DEFAULT_BUTTON_B = 1;
    private static final int DEFAULT_BUTTON_X = 2;
    private static final int DEFAULT_BUTTON_Y = 3;
    private static final int DEFAULT_BUTTON_BACK = 6;
    private static final int DEFAULT_BUTTON_START = 7;
    private static final int DEFAULT_BUTTON_SHOULDER_LEFT = 4;
    private static final int DEFAULT_BUTTON_SHOULDER_RIGHT = 5;

    private int joystickId = -1;
    private boolean initialized;
    private final List<Integer> lastButtons = new ArrayList<Integer>();
    private int lastHat;
    private float lastXAxis;
    private float lastYAxis;
    private final String[] buttons;
    public final int BUTTON_A = DEFAULT_BUTTON_A;
    public final int BUTTON_B = DEFAULT_BUTTON_B;
    public final int BUTTON_X = DEFAULT_BUTTON_X;
    public final int BUTTON_Y = DEFAULT_BUTTON_Y;
    public final int BUTTON_BACK = DEFAULT_BUTTON_BACK;
    public final int BUTTON_START = DEFAULT_BUTTON_START;
    public final int BUTTON_SHOULDER_LEFT = DEFAULT_BUTTON_SHOULDER_LEFT;
    public final int BUTTON_SHOULDER_RIGHT = DEFAULT_BUTTON_SHOULDER_RIGHT;

    public Gamepad(int deviceId) {
        int[] devices = findDevices();
        if (deviceId >= 0 && deviceId < devices.length) {
            joystickId = devices[deviceId];
            initialized = true;
        }
        ByteBuffer currentButtons = initialized ? glfwGetJoystickButtons(joystickId) : null;
        buttons = new String[currentButtons == null ? 0 : currentButtons.remaining()];
        for (int i = 0; i < buttons.length; i++) buttons[i] = "Button " + (i + 1);
    }

    private static int[] findDevices() {
        if (!glfwInitWasCalled()) return new int[0];
        List<Integer> devices = new ArrayList<Integer>();
        for (int id = GLFW_JOYSTICK_1; id <= GLFW_JOYSTICK_LAST; id++) {
            if (glfwJoystickPresent(id)) devices.add(id);
        }
        int[] result = new int[devices.size()];
        for (int i = 0; i < result.length; i++) result[i] = devices.get(i);
        return result;
    }

    private static boolean glfwInitWasCalled() {
        // glfwJoystickPresent is safe after GLFW initialization; GLFW reports
        // no present devices before initialization. Window creation owns init.
        try { return glfwGetCurrentContext() != 0L || glfwGetPrimaryMonitor() != 0L; }
        catch (IllegalStateException e) { return false; }
    }

    public static String[] getAvailableDeviceNames() {
        int[] devices = findDevices();
        String[] names = new String[devices.length];
        for (int i = 0; i < devices.length; i++) {
            String name = glfwGetJoystickName(devices[i]);
            names[i] = name == null ? "Joystick " + (i + 1) : name;
        }
        return names;
    }

    public static int getDeviceCount() { return findDevices().length; }

    private ByteBuffer buttonState() {
        return initialized && glfwJoystickPresent(joystickId) ? glfwGetJoystickButtons(joystickId) : null;
    }
    private FloatBuffer axisState() {
        return initialized && glfwJoystickPresent(joystickId) ? glfwGetJoystickAxes(joystickId) : null;
    }
    private int hatState() {
        if (!initialized || !glfwJoystickPresent(joystickId)) return GLFW_HAT_CENTERED;
        ByteBuffer hats = glfwGetJoystickHats(joystickId);
        return hats == null || !hats.hasRemaining() ? GLFW_HAT_CENTERED : hats.get(0) & 0xff;
    }
    private float axis(int index) {
        FloatBuffer axes = axisState();
        return axes != null && axes.limit() > index ? axes.get(index) : 0.0f;
    }

    public boolean buttonPressed(int button) {
        return isInitialized() && isButtonDown(button) && !lastButtons.contains(button);
    }
    public String[] getButtonNames() { return buttons.clone(); }
    public String getName() {
        return initialized ? glfwGetJoystickName(joystickId) : null;
    }
    /** GLFW has no standard joystick-rumble API; this remains an empty array. */
    public String[] getRumblerNames() { return new String[0]; }
    public int getSelectedController() { return joystickId; }
    public boolean isInitialized() { return initialized && glfwJoystickPresent(joystickId); }
    public boolean isButtonDown(int button) {
        ByteBuffer state = buttonState();
        return state != null && button >= 0 && state.limit() > button && state.get(button) == GLFW_PRESS;
    }
    @Override public boolean isButtonADown() { return isButtonDown(BUTTON_A); }
    @Override public boolean isButtonBDown() { return isButtonDown(BUTTON_B); }
    @Override public boolean isButtonShoulderLeftDown() { return isButtonDown(BUTTON_SHOULDER_LEFT); }
    @Override public boolean isButtonShoulderRightDown() { return isButtonDown(BUTTON_SHOULDER_RIGHT); }
    @Override public boolean isButtonXDown() { return isButtonDown(BUTTON_X); }
    @Override public boolean isButtonYDown() { return isButtonDown(BUTTON_Y); }
    @Override public boolean isDownDown() { return (hatState() & GLFW_HAT_DOWN) != 0 || axis(1) > DEFAULT_DEAD_ZONE; }
    @Override public boolean isLeftDown() { return (hatState() & GLFW_HAT_LEFT) != 0 || axis(0) < -DEFAULT_DEAD_ZONE; }
    @Override public boolean isRightDown() { return (hatState() & GLFW_HAT_RIGHT) != 0 || axis(0) > DEFAULT_DEAD_ZONE; }
    @Override public boolean isUpDown() { return (hatState() & GLFW_HAT_UP) != 0 || axis(1) < -DEFAULT_DEAD_ZONE; }
    @Override public void poll() { /* GLFW joystick state is refreshed by glfwPollEvents. */ }

    public boolean povXLeftPressed() { return (hatState() & GLFW_HAT_LEFT) != 0 && (lastHat & GLFW_HAT_LEFT) == 0; }
    public boolean povXRightPressed() { return (hatState() & GLFW_HAT_RIGHT) != 0 && (lastHat & GLFW_HAT_RIGHT) == 0; }
    public boolean povYDownPressed() { return (hatState() & GLFW_HAT_DOWN) != 0 && (lastHat & GLFW_HAT_DOWN) == 0; }
    public boolean povYUpPressed() { return (hatState() & GLFW_HAT_UP) != 0 && (lastHat & GLFW_HAT_UP) == 0; }

    public void storeGamepadState() {
        lastButtons.clear();
        ByteBuffer state = buttonState();
        if (state != null) for (int i = 0; i < state.limit(); i++) if (state.get(i) == GLFW_PRESS) lastButtons.add(i);
        lastHat = hatState();
        lastXAxis = axis(0);
        lastYAxis = axis(1);
    }
    @Override public void storeState() { storeGamepadState(); }
    @Override public boolean wasBackPressed() { return buttonPressed(BUTTON_BACK); }
    @Override public boolean wasButtonAPressed() { return buttonPressed(BUTTON_A); }
    @Override public boolean wasButtonBPressed() { return buttonPressed(BUTTON_B); }
    @Override public boolean wasButtonShoulderLeftPressed() { return buttonPressed(BUTTON_SHOULDER_LEFT); }
    @Override public boolean wasButtonShoulderRightPressed() { return buttonPressed(BUTTON_SHOULDER_RIGHT); }
    @Override public boolean wasButtonXPressed() { return buttonPressed(BUTTON_X); }
    @Override public boolean wasButtonYPressed() { return buttonPressed(BUTTON_Y); }
    @Override public boolean wasDownPressed() { return povYDownPressed() || (lastYAxis <= DEFAULT_DEAD_ZONE && axis(1) > DEFAULT_DEAD_ZONE); }
    @Override public boolean wasLeftPressed() { return povXLeftPressed() || (lastXAxis >= -DEFAULT_DEAD_ZONE && axis(0) < -DEFAULT_DEAD_ZONE); }
    @Override public boolean wasPausedPressed() { return wasStartPressed(); }
    @Override public boolean wasRightPressed() { return povXRightPressed() || (lastXAxis <= DEFAULT_DEAD_ZONE && axis(0) > DEFAULT_DEAD_ZONE); }
    @Override public boolean wasStartPressed() { return buttonPressed(BUTTON_START); }
    @Override public boolean wasUpPressed() { return povYUpPressed() || (lastYAxis >= -DEFAULT_DEAD_ZONE && axis(1) < -DEFAULT_DEAD_ZONE); }

    public boolean xAxisLeft() { return axis(0) < -DEFAULT_DEAD_ZONE; }
    public boolean xAxisLeftPressed() { return lastXAxis >= -DEFAULT_DEAD_ZONE && xAxisLeft(); }
    public boolean xAxisRight() { return axis(0) > DEFAULT_DEAD_ZONE; }
    public boolean xAxisRightPressed() { return lastXAxis <= DEFAULT_DEAD_ZONE && xAxisRight(); }
    public boolean yAxisDown() { return axis(1) > DEFAULT_DEAD_ZONE; }
    public boolean yAxisDownPressed() { return lastYAxis <= DEFAULT_DEAD_ZONE && yAxisDown(); }
    public boolean yAxisUp() { return axis(1) < -DEFAULT_DEAD_ZONE; }
    public boolean yAxisUpPressed() { return lastYAxis >= -DEFAULT_DEAD_ZONE && yAxisUp(); }
}

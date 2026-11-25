package com.sevensoupcans.sfsoftware.util.input;

import static org.lwjgl.glfw.GLFW.*;

import com.sevensoupcans.sfsoftware.util.graphics.Graphics;

/** Mouse cursor controls backed by GLFW (LWJGL 3). */
public final class Mouse {
    private Mouse() { }

    public static void setGrabbed(boolean grabbed) {
        long window = Graphics.getWindow();
        if (window == 0L) throw new IllegalStateException("The GLFW window has not been initialized");
        glfwSetInputMode(window, GLFW_CURSOR, grabbed ? GLFW_CURSOR_DISABLED : GLFW_CURSOR_NORMAL);
    }
}

package com.sevensoupcans.sfsoftware.util.graphics;

/**
 * Abstraction over the rendering backend so the legacy fixed-function OpenGL path
 * can be swapped out for a modern renderer without changing game logic.
 *
 * <p>New rendering code should target this interface. Existing game code and
 * legacy framework helpers remain intact for compatibility and are preserved as
 * deprecated wrappers.</p>
 */
public interface Renderer {
    void clear();
    void destroy();
    void initGL(int width, int height, String windowTitle);
    void setDisplayMode(int width, int height, boolean fullscreen);
    void setBuffer(FrameBuffer fbo, boolean clearBuffer);
    void update();
    void sync(int fps);
    int getScreenWidth();
    int getScreenHeight();
    void drawEllipse(float x, float y, float radiusX, float radiusY, float r, float g, float b, float a);
    void drawRect(float x, float y, float width, float height, float r, float g, float b, float a, int thickness);
    void drawDisplayBuffer(FrameBuffer fb, int x, int y, int width, int height, int srcX, int srcY, int srcWidth, int srcHeight, float red, float green, float blue, float alpha);
    void setVSync(boolean vsync);
}

package com.sevensoupcans.sfsoftware.util.graphics;

/**
 * Legacy OpenGL renderer compatibility layer.
 *
 * <p>The framework keeps the existing behavior and public APIs intact, but the new
 * renderer abstraction is introduced here so that the rendering backend can be
 * swapped without changing gameplay code. The implementation intentionally stays
 * behaviorally equivalent to the old fixed-function pipeline for now.</p>
 *
 * <p>Prefer using {@link Renderer} and the newer renderer registration APIs when
 * creating new drawing code; this class exists as the compatibility path for the
 * legacy game framework.</p>
 */
public final class LegacyOpenGLRenderer implements Renderer {
    private static final LegacyOpenGLRenderer INSTANCE = new LegacyOpenGLRenderer();

    private LegacyOpenGLRenderer() {
    }

    public static LegacyOpenGLRenderer getInstance() {
        return INSTANCE;
    }

    @Override
    public void clear() {
        org.lwjgl.opengl.GL11.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        org.lwjgl.opengl.GL11.glClear(org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT | org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT);
    }

    @Override
    public void destroy() {
        org.lwjgl.opengl.Display.destroy();
    }

    @Override
    public void initGL(int width, int height, String windowTitle) {
        try {
            org.lwjgl.opengl.Display.setTitle(windowTitle);
            org.lwjgl.opengl.Display.setDisplayMode(new org.lwjgl.opengl.DisplayMode(width, height));
            org.lwjgl.opengl.Display.create();
        } catch (org.lwjgl.LWJGLException e) {
            e.printStackTrace();
            System.exit(0);
        }

        org.lwjgl.opengl.GL11.glEnable(org.lwjgl.opengl.GL11.GL_TEXTURE_2D);
        org.lwjgl.opengl.GL11.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        org.lwjgl.opengl.GL11.glEnable(org.lwjgl.opengl.GL11.GL_BLEND);
        org.lwjgl.opengl.GL11.glBlendFunc(org.lwjgl.opengl.GL11.GL_SRC_ALPHA, org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA);
        org.lwjgl.opengl.GL11.glViewport(0, 0, width, height);
        org.lwjgl.opengl.GL11.glMatrixMode(org.lwjgl.opengl.GL11.GL_MODELVIEW);
        org.lwjgl.opengl.GL11.glMatrixMode(org.lwjgl.opengl.GL11.GL_PROJECTION);
        org.lwjgl.opengl.GL11.glLoadIdentity();
        org.lwjgl.opengl.GL11.glOrtho(0, width, height, 0, 1, -1);
        org.lwjgl.opengl.GL11.glMatrixMode(org.lwjgl.opengl.GL11.GL_MODELVIEW);

        if (!org.lwjgl.opengl.GLContext.getCapabilities().GL_EXT_framebuffer_object) {
            System.out.println("FBO not supported!!!");
            System.exit(0);
        }
    }

    @Override
    public void setDisplayMode(int width, int height, boolean fullscreen) {
        if ((org.lwjgl.opengl.Display.getDisplayMode().getWidth() == width)
                && (org.lwjgl.opengl.Display.getDisplayMode().getHeight() == height)
                && (org.lwjgl.opengl.Display.isFullscreen() == fullscreen)) {
            return;
        }

        try {
            org.lwjgl.opengl.DisplayMode targetDisplayMode = null;
            if (fullscreen) {
                org.lwjgl.opengl.DisplayMode[] modes = org.lwjgl.opengl.Display.getAvailableDisplayModes();
                int freq = 0;
                for (int i = 0; i < modes.length; i++) {
                    org.lwjgl.opengl.DisplayMode current = modes[i];
                    if ((current.getWidth() == width) && (current.getHeight() == height)) {
                        if ((targetDisplayMode == null) || (current.getFrequency() >= freq)) {
                            if ((targetDisplayMode == null) || (current.getBitsPerPixel() > targetDisplayMode.getBitsPerPixel())) {
                                targetDisplayMode = current;
                                freq = targetDisplayMode.getFrequency();
                            }
                        }
                        if ((current.getBitsPerPixel() == org.lwjgl.opengl.Display.getDesktopDisplayMode().getBitsPerPixel())
                                && (current.getFrequency() == org.lwjgl.opengl.Display.getDesktopDisplayMode().getFrequency())) {
                            targetDisplayMode = current;
                            break;
                        }
                    }
                }
            } else {
                targetDisplayMode = new org.lwjgl.opengl.DisplayMode(width, height);
            }

            if (targetDisplayMode == null) {
                System.out.println("Failed to find value mode: " + width + "x" + height + " fs=" + fullscreen);
                return;
            }

            org.lwjgl.opengl.Display.setDisplayMode(targetDisplayMode);
            org.lwjgl.opengl.Display.setFullscreen(fullscreen);
        } catch (org.lwjgl.LWJGLException e) {
            System.out.println("Unable to setup mode " + width + "x" + height + " fullscreen=" + fullscreen + e);
        }
    }

    @Override
    public void setBuffer(FrameBuffer fbo, boolean clearBuffer) {
        if (fbo != null) {
            org.lwjgl.opengl.GL11.glBindTexture(org.lwjgl.opengl.GL11.GL_TEXTURE_2D, 0);
            org.lwjgl.opengl.GL30.glBindFramebuffer(org.lwjgl.opengl.GL30.GL_FRAMEBUFFER, fbo.getId());
            org.lwjgl.opengl.GL11.glViewport(0, 0, fbo.getWidth(), fbo.getHeight());
        } else {
            org.lwjgl.opengl.GL11.glBindTexture(org.lwjgl.opengl.GL11.GL_TEXTURE_2D, 0);
            org.lwjgl.opengl.GL30.glBindFramebuffer(org.lwjgl.opengl.GL30.GL_FRAMEBUFFER, 0);
            org.lwjgl.opengl.GL11.glViewport(0, 0, org.lwjgl.opengl.Display.getDisplayMode().getWidth(), org.lwjgl.opengl.Display.getDisplayMode().getHeight());
        }

        if (clearBuffer) {
            this.clear();
        }
    }

    @Override
    public void update() {
        org.lwjgl.opengl.Display.update();
    }

    @Override
    public void sync(int fps) {
        org.lwjgl.opengl.Display.sync(fps);
    }

    @Override
    public int getScreenWidth() {
        return org.lwjgl.opengl.Display.getDisplayMode().getWidth();
    }

    @Override
    public int getScreenHeight() {
        return org.lwjgl.opengl.Display.getDisplayMode().getHeight();
    }

    @Override
    public void drawEllipse(float x, float y, float radiusX, float radiusY, float r, float g, float b, float a) {
        org.lwjgl.opengl.GL11.glDisable(org.lwjgl.opengl.GL11.GL_TEXTURE_2D);
        org.lwjgl.opengl.GL11.glColor4f(r, g, b, a);
        org.lwjgl.opengl.GL11.glBegin(org.lwjgl.opengl.GL11.GL_TRIANGLE_FAN);
        org.lwjgl.opengl.GL11.glVertex2f(x, y);
        for (int i = 0; i <= 100; i++) {
            double angle = i * 2.0 * Math.PI / 100;
            org.lwjgl.opengl.GL11.glVertex2f(x + (float) Math.cos(angle) * radiusX, y + (float) Math.sin(angle) * radiusY);
        }
        org.lwjgl.opengl.GL11.glEnd();
        org.lwjgl.opengl.GL11.glEnable(org.lwjgl.opengl.GL11.GL_TEXTURE_2D);
    }

    @Override
    public void drawRect(float x, float y, float width, float height, float r, float g, float b, float a, int thickness) {
        Quad.draw(x, y, thickness, height, r, g, b, a);
        Quad.draw(x + (width - thickness), y, thickness, height, r, g, b, a);
        Quad.draw(x + thickness, y, width - thickness, thickness, r, g, b, a);
        Quad.draw(x + thickness, y + (height - thickness), width - thickness, thickness, r, g, b, a);
    }

    @Override
    public void drawDisplayBuffer(FrameBuffer fb, int x, int y, int width, int height, int srcX, int srcY, int srcWidth, int srcHeight, float red, float green, float blue, float alpha) {
        if (fb == null) {
            return;
        }
        this.setBuffer(null, false);
        fb.draw(x, y, width, height, srcX, srcY, srcWidth, srcHeight, red, green, blue, alpha);
    }

    @Override
    public void setVSync(boolean vsync) {
        org.lwjgl.opengl.Display.setVSyncEnabled(vsync);
    }
}

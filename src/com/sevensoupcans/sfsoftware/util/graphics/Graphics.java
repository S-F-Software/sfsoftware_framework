package com.sevensoupcans.sfsoftware.util.graphics;

import static org.lwjgl.glfw.GLFW.*;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Arrays;
import java.util.Map;

import javax.imageio.ImageIO;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryStack;

import com.sevensoupcans.sfsoftware.util.graphics.geometry.Quad;
import com.sevensoupcans.sfsoftware.util.resources.ClasspathHelper;
import com.sevensoupcans.sfsoftware.util.resources.FileUtils;
import com.sevensoupcans.sfsoftware.util.resources.ResourceUtils;

// Window, display and legacy immediate-mode rendering helpers.
public abstract class Graphics
{
    public static final String DEFAULT_GRAPHICS_FILE_PATH = "res/graphics";

    private static boolean verbose = false;
    private static int displayHeight;
    private static int displayWidth;
    public static int shaderProgram;
    private static String[] maskTextures = new String[0];

    private static long window;
    private static FrameBuffer primaryDisplayBuffer;
    private static FrameBuffer currentDisplayBuffer;
    private static ByteBuffer[] pendingIcons;

    public static FrameBuffer getCurrentDisplayBuffer()
    {
        return currentDisplayBuffer;
    }

    public static long getWindow()
    {
        return window;
    }

    public static int getScreenHeight()
    {
        if (window == 0L)
        {
            return displayHeight;
        }

        try (MemoryStack stack = MemoryStack.stackPush())
        {
            IntBuffer height = stack.mallocInt(1);
            glfwGetWindowSize(window, stack.mallocInt(1), height);
            return height.get(0);
        }
    }

    public static int getScreenWidth()
    {
        if (window == 0L)
        {
            return displayWidth;
        }

        try (MemoryStack stack = MemoryStack.stackPush())
        {
            IntBuffer width = stack.mallocInt(1);
            glfwGetWindowSize(window, width, stack.mallocInt(1));
            return width.get(0);
        }
    }

    public static void clear()
    {
        GL11.glClearColor(0f, 0f, 0f, 1f);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
    }

    public static void destroy()
    {
        if (window != 0L)
        {
            glfwDestroyWindow(window);
            window = 0L;
        }
        glfwTerminate();
    }

    public static void drawDisplayBuffer(int x, int y, int width, int height)
    {
        drawDisplayBuffer(primaryDisplayBuffer, x, y, width, height);
    }

    public static void drawDisplayBuffer(FrameBuffer fb, int x, int y, int width, int height)
    {
        drawDisplayBuffer(fb, x, y, width, height, 0, 0, fb.getWidth(), fb.getHeight());
    }

    public static void drawDisplayBuffer(FrameBuffer fb, int x, int y, int width, int height,
            int srcX, int srcY, int srcWidth, int srcHeight)
    {
        drawDisplayBuffer(fb, x, y, width, height, srcX, srcY, srcWidth, srcHeight,
                1f, 1f, 1f, 1f);
    }

    public static void drawDisplayBuffer(FrameBuffer fb, int x, int y, int width, int height,
            int srcX, int srcY, int srcWidth, int srcHeight,
            float red, float green, float blue, float alpha)
    {
        setBuffer(null);
        fb.draw(x, y, width, height, srcX, srcY, srcWidth, srcHeight,
                red, green, blue, alpha);
    }

    public static void drawEllipse(float x, float y, float radiusX, float radiusY,
            float r, float g, float b, float a)
    {
        drawEllipse(x, y, radiusX, radiusY, r, g, b, a, 100);
    }

    public static void drawEllipse(float x, float y, float radiusX, float radiusY,
            float r, float g, float b, float a, int segments)
    {
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glColor4f(r, g, b, a);
        GL11.glBegin(GL11.GL_TRIANGLE_FAN);
        GL11.glVertex2f(x, y);

        for (int i = 0; i <= segments; i++)
        {
            double angle = i * 2.0 * Math.PI / segments;
            GL11.glVertex2f(x + (float) Math.cos(angle) * radiusX,
                    y + (float) Math.sin(angle) * radiusY);
        }

        GL11.glEnd();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }

    public static void drawRect(float x, float y, float width, float height,
            RGBA rgba, int thickness)
    {
        drawRect(x, y, width, height, rgba.getRed(), rgba.getGreen(), rgba.getBlue(),
                rgba.getAlpha(), thickness);
    }

    public static void drawRect(float x, float y, float width, float height,
            float r, float g, float b, float a, int thickness)
    {
        Quad.draw(x, y, thickness, height, r, g, b, a);
        Quad.draw(x + width - thickness, y, thickness, height, r, g, b, a);
        Quad.draw(x + thickness, y, width - thickness, thickness, r, g, b, a);
        Quad.draw(x + thickness, y + height - thickness,
                width - thickness, thickness, r, g, b, a);
    }

    public static boolean isCloseRequested()
    {
        return window == 0L || glfwWindowShouldClose(window);
    }

    // Initializes GLFW, creates the OpenGL window and prepares the context.
    public static void initGL(int width, int height, String windowTitle)
    {
        if (!glfwInit())
        {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 2);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 1);

        window = glfwCreateWindow(width, height, windowTitle, 0L, 0L);
        if (window == 0L)
        {
            glfwTerminate();
            throw new IllegalStateException("Unable to create GLFW window");
        }

        glfwMakeContextCurrent(window);
        GL.createCapabilities();
        glfwSwapInterval(0);
        glfwShowWindow(window);

        if (pendingIcons != null)
        {
            glfwSetWindowIcon(window, pendingIcons);
        }

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glClearColor(0f, 0f, 0f, 0f);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glViewport(0, 0, width, height);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glLoadIdentity();
        GL11.glOrtho(0, width, height, 0, 1, -1);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);

        displayWidth = width;
        displayHeight = height;

        if (!GL.getCapabilities().GL_EXT_framebuffer_object
                && !GL.getCapabilities().OpenGL30)
        {
            throw new IllegalStateException("Framebuffer objects are not supported");
        }

        primaryDisplayBuffer = new FrameBuffer(width, height);
    }

    // Switches between windowed mode and the selected monitor's fullscreen mode.
    public static void setDisplayMode(int width, int height, boolean fullscreen)
    {
        if (window == 0L)
        {
            throw new IllegalStateException("The GLFW window has not been initialized");
        }

        long currentMonitor = glfwGetWindowMonitor(window);
        if (!fullscreen && currentMonitor == 0L
                && getScreenWidth() == width && getScreenHeight() == height)
        {
            return;
        }

        if (fullscreen)
        {
            long monitor = glfwGetPrimaryMonitor();
            if (monitor == 0L)
            {
                throw new IllegalStateException("No primary monitor is available");
            }

            org.lwjgl.glfw.GLFWVidMode mode = glfwGetVideoMode(monitor);
            if (mode == null)
            {
                throw new IllegalStateException("Unable to query primary monitor mode");
            }

            glfwSetWindowMonitor(window, monitor, 0, 0, width, height,
                    mode.refreshRate());
        }
        else
        {
            glfwSetWindowMonitor(window, 0L, 100, 100, width, height,
                    GLFW_DONT_CARE);
        }

        displayWidth = width;
        displayHeight = height;
        GL11.glViewport(0, 0, width, height);
    }

    public static Map<String, Texture> loadTextures(Class<?> invokingClass)
    {
        return loadTextures(DEFAULT_GRAPHICS_FILE_PATH, invokingClass);
    }

    public static Map<String, Texture> loadTextures(String texturePath,
            Class<?> invokingClass)
    {
        return loadTextures(texturePath, "png", null, invokingClass);
    }

    public static Map<String, Texture> loadTextures(String texturePath,
            String filetype, Class<?> invokingClass)
    {
        return loadTextures(texturePath, filetype, null, invokingClass);
    }

    public static Map<String, Texture> loadTextures(String texturePath,
            String filetype, RGBA transparentColor, Class<?> invokingClass)
    {
        try
        {
            Arrays.stream(FileUtils.getResourceListing(texturePath, filetype,
                            ClasspathHelper.getClasspath(invokingClass)))
                .filter(file -> file.toLowerCase().endsWith("." + filetype.toLowerCase()))
                .forEach(file ->
                {
                    String textureName = file.substring(0, file.lastIndexOf('.')).trim();

                    try
                    {
                        if (transparentColor != null)
                        {
                            new Texture(texturePath + "/" + file, transparentColor);
                        }
                        else
                        {
                            new Texture(texturePath + "/" + file);
                        }

                        if (maskTextures != null
                                && maskTextures.length > 0
                                && Arrays.asList(maskTextures).contains(textureName))
                        {
                            new Texture(texturePath + "/" + file,
                                    transparentColor, RGBA.WHITE);

                            if (verbose)
                            {
                                System.out.println("Loaded texture '_" + textureName + "'");
                            }
                        }

                        if (verbose)
                        {
                            System.out.println("Loaded texture '" + textureName + "'");
                        }
                    }
                    catch (IOException e)
                    {
                        System.out.println("Failed to load texture '" + textureName
                                + "': " + e.getMessage());
                    }
                });
        }
        catch (IOException | URISyntaxException e)
        {
            e.printStackTrace();
        }

        return Texture.getLoadedTextures();
    }

    // Decode an icon as tightly packed RGBA pixels for GLFW.
    private static ByteBuffer loadIcon(String path) throws IOException
    {
        try (InputStream inputStream = ResourceUtils.getResourceAsStream(path,
                Graphics.class))
        {
            BufferedImage image = ImageIO.read(inputStream);
            if (image == null)
            {
                throw new IOException("Unsupported or invalid image: " + path);
            }

            int width = image.getWidth();
            int height = image.getHeight();
            ByteBuffer pixels = BufferUtils.createByteBuffer(width * height * 4);

            for (int y = 0; y < height; y++)
            {
                for (int x = 0; x < width; x++)
                {
                    int argb = image.getRGB(x, y);
                    pixels.put((byte) ((argb >>> 16) & 0xFF));
                    pixels.put((byte) ((argb >>> 8) & 0xFF));
                    pixels.put((byte) (argb & 0xFF));
                    pixels.put((byte) ((argb >>> 24) & 0xFF));
                }
            }

            pixels.flip();
            return pixels;
        }
    }

    public static void setAppIcon(String icon16, String icon32)
    {
        try
        {
            pendingIcons = new ByteBuffer[] { loadIcon(icon16), loadIcon(icon32) };
            if (window != 0L)
            {
                glfwSetWindowIcon(window, pendingIcons);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private static void setBuffer(int bufferId, int bufferWidth, int bufferHeight,
            boolean clearBuffer)
    {
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, bufferId);
        GL11.glViewport(0, 0, bufferWidth, bufferHeight);

        if (clearBuffer)
        {
            clear();
        }
    }

    public static void setBuffer(FrameBuffer fbo, boolean clearBuffer)
    {
        if (fbo != null)
        {
            setBuffer(fbo.getId(), fbo.getWidth(), fbo.getHeight(), clearBuffer);
        }
        else
        {
            setBuffer(0, getScreenWidth(), getScreenHeight(), clearBuffer);
        }

        currentDisplayBuffer = fbo;
    }

    public static void setBuffer(FrameBuffer fbo)
    {
        setBuffer(fbo, false);
    }

    public static void setMaskTextureList(String[] textureList)
    {
        maskTextures = textureList;
    }

    public static void setScaledDisplay(double xScaler, double yScaler)
    {
        GL11.glOrtho(0, getDisplayWidth() / xScaler,
                getDisplayHeight() / yScaler, 0, 1, -1);
    }

    public static FrameBuffer getPrimaryDisplayBuffer()
    {
        return primaryDisplayBuffer;
    }

    public static boolean isFullScreen()
    {
        return window != 0L && glfwGetWindowMonitor(window) != 0L;
    }

    public static void sync(int fps)
    {
        if (fps <= 0)
        {
            return;
        }

        long frameNanos = 1_000_000_000L / fps;
        long deadline = System.nanoTime() + frameNanos;

        while (System.nanoTime() < deadline)
        {
            long remaining = deadline - System.nanoTime();
            if (remaining > 2_000_000L)
            {
                try
                {
                    Thread.sleep(Math.max(1L, remaining / 1_000_000L - 1L));
                }
                catch (InterruptedException e)
                {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
            else
            {
                Thread.yield();
            }
        }
    }

    public static void update()
    {
        if (window == 0L)
        {
            return;
        }

        glfwSwapBuffers(window);
        glfwPollEvents();
    }

    public static void update(boolean sync)
    {
        update();
        if (sync)
        {
            sync(60);
        }
    }

    private static int getDisplayHeight()
    {
        return displayHeight;
    }

    private static void setDisplayHeight(int value)
    {
        displayHeight = value;
    }

    private static int getDisplayWidth()
    {
        return displayWidth;
    }

    private static void setDisplayWidth(int value)
    {
        displayWidth = value;
    }

    public static void setVSync(boolean vsync)
    {
        if (window != 0L)
        {
            glfwSwapInterval(vsync ? 1 : 0);
        }
    }

    private Graphics()
    {
    }
}


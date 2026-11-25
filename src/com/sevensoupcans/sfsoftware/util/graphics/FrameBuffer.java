package com.sevensoupcans.sfsoftware.util.graphics;

import static org.lwjgl.opengl.EXTFramebufferObject.GL_COLOR_ATTACHMENT0_EXT;
import static org.lwjgl.opengl.EXTFramebufferObject.GL_DEPTH_ATTACHMENT_EXT;
import static org.lwjgl.opengl.EXTFramebufferObject.GL_FRAMEBUFFER_EXT;
import static org.lwjgl.opengl.EXTFramebufferObject.GL_RENDERBUFFER_EXT;
import static org.lwjgl.opengl.EXTFramebufferObject.glBindFramebufferEXT;
import static org.lwjgl.opengl.EXTFramebufferObject.glBindRenderbufferEXT;
import static org.lwjgl.opengl.EXTFramebufferObject.glFramebufferRenderbufferEXT;
import static org.lwjgl.opengl.EXTFramebufferObject.glFramebufferTexture2DEXT;
import static org.lwjgl.opengl.EXTFramebufferObject.glGenFramebuffersEXT;
import static org.lwjgl.opengl.EXTFramebufferObject.glGenRenderbuffersEXT;
import static org.lwjgl.opengl.EXTFramebufferObject.glRenderbufferStorageEXT;

import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.EXTFramebufferObject;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL30;

public class FrameBuffer {
    private int colorTextureID;
    private int framebufferID;
    private int depthRenderBufferID;
    private int width;
    private int height;
    private int currentShader;

    public FrameBuffer(int width, int height) {
        framebufferID = glGenFramebuffersEXT();
        colorTextureID = GL11.glGenTextures();
        depthRenderBufferID = glGenRenderbuffersEXT();
        glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, framebufferID);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, colorTextureID);
        GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, width, height, 0,
                GL11.GL_RGBA, GL11.GL_INT, (java.nio.ByteBuffer) null);
        glFramebufferTexture2DEXT(GL_FRAMEBUFFER_EXT, GL_COLOR_ATTACHMENT0_EXT, GL11.GL_TEXTURE_2D, colorTextureID, 0);
        glBindRenderbufferEXT(GL_RENDERBUFFER_EXT, depthRenderBufferID);
        glRenderbufferStorageEXT(GL_RENDERBUFFER_EXT, GL14.GL_DEPTH_COMPONENT24, width, height);
        glFramebufferRenderbufferEXT(GL_FRAMEBUFFER_EXT, GL_DEPTH_ATTACHMENT_EXT, GL_RENDERBUFFER_EXT, depthRenderBufferID);
        glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, 0);
        this.width = width;
        this.height = height;
        verify();
        if (getStatus() != GL30.GL_FRAMEBUFFER_COMPLETE)
            System.out.println("There was an issue with FBO Id " + framebufferID + ". Status: " + getStatus());
    }
    public void clear() {
        glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, getId());
        GL11.glPopAttrib();
        GL11.glPushAttrib(GL11.GL_VIEWPORT_BIT);
        GL11.glViewport(0, 0, getWidth(), getHeight());
        GL11.glClearColor(0, 0, 0, 1);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
    }
    public void draw(int x, int y) { draw(x, y, getWidth(), getHeight()); }
    public void draw(int x, int y, int width, int height) { draw(x, y, width, height, 0, 0, width, height); }
    public void draw(int x, int y, int width, int height, int srcX, int srcY, int srcWidth, int srcHeight) {
        draw(x, y, width, height, srcX, srcY, srcWidth, srcHeight, 1, 1, 1, 1);
    }
    public void draw(int x, int y, int width, int height, int srcX, int srcY,
            int srcWidth, int srcHeight, float red, float green, float blue, float alpha) {
        int screenWidth = Graphics.getScreenWidth();
        int screenHeight = Graphics.getScreenHeight();
        GL11.glPushMatrix();
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glLoadIdentity();
        GL11.glOrtho(0, screenWidth, screenHeight, 0, 1, -1);
        GL11.glViewport(0, 0, screenWidth, screenHeight);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glLoadIdentity();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        int attributeLoc = ARBShaderObjects.glGetUniformLocationARB(currentShader, "texture");
        ARBShaderObjects.glUseProgramObjectARB(currentShader);
        ARBShaderObjects.glUniform1iARB(attributeLoc, getTextureId());
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, getTextureId());
        float fSrcX = (float) srcX / getWidth();
        float fSrcY = (float) srcY / getHeight();
        float fSrcWidth = (float) (srcX + srcWidth) / getWidth();
        float fSrcHeight = (float) (srcY + srcHeight) / getHeight();
        GL11.glPushMatrix();
        GL11.glColor4f(red, green, blue, alpha);
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glTexCoord2f(fSrcX, 1 - fSrcY); GL11.glVertex2i(x, y);
        GL11.glTexCoord2f(fSrcWidth, 1 - fSrcY); GL11.glVertex2i(x + width, y);
        GL11.glTexCoord2f(fSrcWidth, 1 - fSrcHeight); GL11.glVertex2i(x + width, y + height);
        GL11.glTexCoord2f(fSrcX, 1 - fSrcHeight); GL11.glVertex2i(x, y + height);
        GL11.glEnd();
        GL11.glPopMatrix();
        GL11.glPopAttrib();
        ARBShaderObjects.glUseProgramObjectARB(0);
    }
    public int getDepthBufferId() { return depthRenderBufferID; }
    public int getId() { return framebufferID; }
    public int getStatus() { return EXTFramebufferObject.glCheckFramebufferStatusEXT(this.getId()); }
    public int getTextureId() { return colorTextureID; }
    public void setAsCurrentBuffer() { setAsCurrentBuffer(false); }
    public void setAsCurrentBuffer(boolean clearBuffer) { Graphics.setBuffer(this, clearBuffer); }
    public void setCurrentShader(int programId) { currentShader = programId; ARBShaderObjects.glUseProgramObjectARB(programId); }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    private void verify() {
        int framebuffer = EXTFramebufferObject.glCheckFramebufferStatusEXT(getId());
        switch (framebuffer) {
            case EXTFramebufferObject.GL_FRAMEBUFFER_COMPLETE_EXT: return;
            case EXTFramebufferObject.GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT_EXT:
                throw new RuntimeException("FrameBuffer: " + getId() + ", has caused a GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT_EXT exception");
            case EXTFramebufferObject.GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT_EXT:
                throw new RuntimeException("FrameBuffer: " + getId() + ", has caused a GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT_EXT exception");
            case EXTFramebufferObject.GL_FRAMEBUFFER_INCOMPLETE_DIMENSIONS_EXT:
                throw new RuntimeException("FrameBuffer: " + getId() + ", has caused a GL_FRAMEBUFFER_INCOMPLETE_DIMENSIONS_EXT exception");
            case EXTFramebufferObject.GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER_EXT:
                throw new RuntimeException("FrameBuffer: " + getId() + ", has caused a GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER_EXT exception");
            case EXTFramebufferObject.GL_FRAMEBUFFER_INCOMPLETE_FORMATS_EXT:
                throw new RuntimeException("FrameBuffer: " + getId() + ", has caused a GL_FRAMEBUFFER_INCOMPLETE_FORMATS_EXT exception");
            case EXTFramebufferObject.GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER_EXT:
                throw new RuntimeException("FrameBuffer: " + getId() + ", has caused a GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER_EXT exception");
            default: throw new RuntimeException("FrameBuffer: " + getId() + ", incomplete status: " + framebuffer);
        }
    }
}

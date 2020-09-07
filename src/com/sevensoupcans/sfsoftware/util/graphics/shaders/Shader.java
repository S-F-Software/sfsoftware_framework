package com.sevensoupcans.sfsoftware.util.graphics.shaders;

import org.lwjgl.opengl.ARBFragmentShader;
import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.ARBVertexShader;
import org.lwjgl.opengl.GL11;

public class Shader {
	
	public static final int TYPE_VERTEX = ARBVertexShader.GL_VERTEX_SHADER_ARB;
	public static final int TYPE_FRAGMENT = ARBFragmentShader.GL_FRAGMENT_SHADER_ARB;
	
	private final int shaderId;
	
    private static String getLogInfo(int obj) 
    {
        return ARBShaderObjects.glGetInfoLogARB(obj, ARBShaderObjects.glGetObjectParameteriARB(obj, 
        		ARBShaderObjects.GL_OBJECT_INFO_LOG_LENGTH_ARB));
    }
	
	public Shader(String fileContents, int type)
	{
    	int shader = 0;
    	try 
    	{
	        shader = ARBShaderObjects.glCreateShaderObjectARB(type);	        
	        
	        ARBShaderObjects.glShaderSourceARB(shader, fileContents);
	        ARBShaderObjects.glCompileShaderARB(shader);
	        
	        if (ARBShaderObjects.glGetObjectParameteriARB(shader, ARBShaderObjects.GL_OBJECT_COMPILE_STATUS_ARB) == GL11.GL_FALSE)
	            throw new RuntimeException("Error creating shader: " + getLogInfo(shader));
	        
    	}
    	catch(Exception exc) {
    		ARBShaderObjects.glDeleteObjectARB(shader);
    		throw exc;
    	}
    	finally
    	{
    		shaderId = shader;
    	}
	}
	
	public int getId()
	{
		return shaderId;
	}
	
	public void setShaderUniform(String attribute, int value)
	{		
		int attributeLoc = ARBShaderObjects.glGetUniformLocationARB(getId(), attribute);
		ARBShaderObjects.glUniform1iARB(attributeLoc, value);			
	}
	
}

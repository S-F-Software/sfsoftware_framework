package com.sevensoupcans.sfsoftware.util.resources;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class FileUtils 
{
	public static char getDirectorySeparator()
	{
		return File.separatorChar;
	}		
	
	public static String readFile(final String path, final Charset encoding) throws IOException 
	{
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return new String(encoded, encoding);
	}
	
	public static String readFile(final String path) throws IOException 
	{
		return readFile(path, StandardCharsets.UTF_8);
	}	
	
	public static Reader getResourceAsStream(final String filename) throws IOException
	{
		return new InputStreamReader(new FileInputStream(filename));
	}
	
    public static String[] fileToStringArray(final String filename) throws IOException
    {
        //FileReader fileReader = new FileReader(filename);
        InputStreamReader fileReader = new InputStreamReader(new FileInputStream(filename)); 
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        List<String> lines = new ArrayList<String>();
        String line = null;
        while ((line = bufferedReader.readLine()) != null) 
        {
            lines.add(line);
        }
        bufferedReader.close();
        fileReader.close();
        return lines.toArray(new String[lines.size()]);
    }
    
    public static String[] getResourceListing(final String path, final String type) throws IOException, URISyntaxException
    {
    	return getResourceListing(path, type, ClasspathHelper.getClasspath());
    }
    
    public static String[] getResourceListing(String path, final String type, final List<URL> classpathList) throws IOException, URISyntaxException
    {
    	if(!(path.endsWith("/")))
    	{
    		path = path + "/";
    	}
    	Collection<URL> elements = ClasspathHelper.elementsOfFolder(path, type, classpathList);
    	//System.out.println("$$$ " + elements.size() + " objects ending with " + type + " @ " + path);
    	if(elements.size() <= 0)
    	{
    		System.out.println("** Warning: Zero objects ending with " + type + " @ " + path);
        }
    	List<String> resources = new ArrayList<String>();
    	
    	for(URL url: elements)
    	{
    		String fileName = url.getFile().toString();
    		fileName = fileName.substring(fileName.lastIndexOf("/") + 1, fileName.length());    		
    		//System.out.println("### " + fileName);
    		resources.add(fileName);
    	}    	
    	
    	return resources.toArray(new String[resources.size()]);
    } 
    
    public static void writeToFile(final String file, final String text)
    {
    	try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(file, true)))) 
    	{
    	    out.println(text);
    	}
    	catch (IOException e) 
    	{
    	    //exception handling left as an exercise for the reader
    	}    	
    }
}
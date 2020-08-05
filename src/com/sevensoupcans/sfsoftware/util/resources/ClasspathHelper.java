/*
 * Copyright (C) 2014 Daniel Heinrich <dannynullzwo@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.sevensoupcans.sfsoftware.util.resources;

import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.nio.file.attribute.*;
import java.util.*;
import java.util.jar.*;

/**
 *
 * @author Daniel Heinrich <dannynullzwo@gmail.com>
 */
public abstract class ClasspathHelper 
{
    public static final String JAR_FILE = "jar";
    public static final String FOLDER = "/";
    
    public static List<URL> getClasspath(Class<?> c) 
    {    	
    	try
    	{
    		// Get the path of the current class
        	URL classPath = c.getResource(c.getSimpleName() + ".class");
        	// Only use the path string until /bin
        	String basePath = classPath.toString().substring(0, classPath.toString().indexOf("/bin"));

			URL revisedPath = new URL(basePath + "/");
			URL pathes[] = { revisedPath };			
			
			return Arrays.asList(pathes);
		} 
    	catch (MalformedURLException e) 
    	{
			return null;
		}
    }    
    
    public static List<URL> getClasspath() 
    {    
    	return getClasspath(ClasspathHelper.class);
    }

    private static Collection<URL> filter(List<URL> list, String pred)
    {
    	ArrayList<URL> al = new ArrayList<URL>();
    	for(URL url : list)
        {
    		//System.out.println(url.getPath() + ", " + pred.postfix);
        	if(url.getPath().endsWith(pred))
        	{
        		//System.out.println("Adding " + url.getPath());
        		al.add(url);
        	}
        }
		return al;  	
    }
    
    public static List<URL> elementsOfFolder(String folder, List<URL> classpathList) throws IOException, URISyntaxException
    {
    	List<URL> elements = new ArrayList<>();

        for (URL url : filter(classpathList, JAR_FILE)) 
        {
            JarFile jar = new JarFile(url.getPath().replaceAll("%20", " "));            
            for (JarEntry entry : Collections.list(jar.entries())) 
            {
                String name = entry.getName();                               
                if (name.startsWith(folder)) 
                {
                    elements.add(new URL("jar:" + url + "!/" + name));
                }
            }
            jar.close();
        }
                  
        if(folder.startsWith("/"))
        {
        	// Absolute path was used.
        	Path folderPath = Paths.get(folder);
            FileCollector collector = new FileCollector(folderPath);
            Files.walkFileTree(folderPath, collector);
            elements.addAll(collector.collected);        	
        }
        else
        {        	
            for (URL url : filter(classpathList, FOLDER)) 
            {
            	// The classpath will likely be the bin directory. Res is a level above, so let's make that the URL.
            	String urlString = url.toString();        	
            	if(urlString.indexOf("bin/") > -1)
            	{
            		urlString = urlString.substring(0, urlString.indexOf("bin/"));
            		url = new URL(urlString);
            	}
            	System.out.println("**" + url.toString());
                Path root = Paths.get(url.toURI());            
                FileCollector collector = new FileCollector(root.resolve(folder));
                Files.walkFileTree(root, collector);
                elements.addAll(collector.collected);
            }        	
        }       

        return elements;    	
    }
    
    public static List<URL> elementsOfFolder(String folder) throws IOException, URISyntaxException 
    {
        return elementsOfFolder(folder, getClasspath());
    }

    public static Collection<URL> elementsOfFolder(String folder, String pred) throws IOException, URISyntaxException {
    	return elementsOfFolder(folder, pred, getClasspath());
    }
    
    public static Collection<URL> elementsOfFolder(String folder, String pred, List<URL> classpathList) throws IOException, URISyntaxException {
        List<URL> list = elementsOfFolder(folder, classpathList);
        ArrayList<URL> al = new ArrayList<URL>();
    	for(URL url : list)
        {
    		//System.out.println(url.getPath());
        	if(url.getPath().endsWith(pred))
        	{
        		//System.out.println("Adding " + url.getPath());
        		al.add(url);
        	}
        }
		return al;          
    }

    private static class FileCollector extends SimpleFileVisitor<Path> {

        private final List<URL> collected = new ArrayList<>();
        private final Path root;

        private FileCollector(Path resolve) {
            root = resolve;
        }

        @Override
        public FileVisitResult visitFile(Path path, BasicFileAttributes mainAtts) throws IOException {
            if (path.startsWith(root))
                collected.add(path.toUri().toURL());
            return FileVisitResult.CONTINUE;
        }
    }
}

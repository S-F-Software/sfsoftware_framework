package com.sevensoupcans.sfsoftware.util.resources;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

// Classpath and filesystem resource access without a third-party loader.
public final class ResourceUtils
{
    private ResourceUtils()
    {
    }

    // Resolves a resource from the filesystem first, then from the supplied class's class loader.
    // A leading slash is accepted for classpath names.
    public static URL getResource(String resourcePath, Class<?> context) throws IOException
    {
        if (resourcePath == null || resourcePath.isEmpty())
        {
            throw new FileNotFoundException("Resource path must not be empty");
        }

        try
        {
            Path path = Paths.get(resourcePath);
            if (Files.isRegularFile(path))
            {
                return path.toUri().toURL();
            }
        }
        catch (RuntimeException ignored)
        {
            // Not a valid filesystem path; continue with URL/classpath lookup.
        }

        try
        {
            return new URL(resourcePath);
        }
        catch (MalformedURLException ignored)
        {
            // Not an absolute URL; try the classpath.
        }

        String classpathPath = resourcePath.startsWith("/")
                ? resourcePath.substring(1)
                : resourcePath;

        ClassLoader loader = context == null ? null : context.getClassLoader();
        URL resource = loader == null
                ? ClassLoader.getSystemResource(classpathPath)
                : loader.getResource(classpathPath);

        if (resource == null && context != null)
        {
            resource = context.getResource(resourcePath.startsWith("/")
                    ? resourcePath
                    : "/" + resourcePath);
        }

        if (resource == null)
        {
            throw new FileNotFoundException("Resource not found: " + resourcePath);
        }

        return resource;
    }

    public static InputStream getResourceAsStream(String resourcePath, Class<?> context) throws IOException
    {
        return getResource(resourcePath, context).openStream();
    }
}

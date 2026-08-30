package com.learn.auto.utilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Singleton responsible for reading and providing access to the
 * default.properties configuration file.
 *
 * <p>This class uses a thread-safe, lazy-initialized singleton pattern.
 * The properties file is first looked up on the classpath; if not found
 * there, it falls back to the file system (project root directory).</p>
 */
public final class ConfigManager
{
    private static final String PROPERTIES_FILE = "default.properties";

    private static volatile ConfigManager instance;
    private final Properties properties;

    private ConfigManager()
    {
        properties = new Properties();
        loadProperties();
    }

    /**
     * Returns the singleton instance of ConfigManager.
     *
     * @return the singleton ConfigManager instance
     */
    public static ConfigManager getInstance()
    {
        if (instance == null)
        {
            synchronized (ConfigManager.class)
            {
                ConfigManager.instance = instance = new ConfigManager();
            }
        }
        return instance;
    }

    /**
     * Loads the properties from the default.properties file.
     * It first attempts to load from the classpath, and if that fails,
     * it falls back to loading from the file system (project root directory).
     */
    private void loadProperties()
    {
        // First, try to load from the classpath (e.g. src/main/resources).
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(PROPERTIES_FILE))
        {
            if (input != null)
            {
                properties.load(input);
            }
        }
        catch (IOException e)
        {
            // Fallback: load from the file system (project root directory).
            try (InputStream input = new FileInputStream(new File(PROPERTIES_FILE)))
            {
                properties.load(input);
            }
            catch (Exception ex)
            {
                throw new RuntimeException("Failed to load properties file: " + PROPERTIES_FILE, ex);
            }
        }
    }

    /**
     * Returns the value for the given property key.
     *
     * @param key the property key
     * @return the property value, or {@code null} if the key is not present
     */
    public String getProperty(String key)
    {
        return properties.getProperty(key);
    }

    /**
     * Returns the value for the given property key, or a default value
     * if the key is not present.
     *
     * @param key the property key
     * @param defaultValue the default value to return if the key is missing
     * @return the property value, or {@code defaultValue} if the key is not present
     */
    public String getProperty(String key, String defaultValue)
    {
        return properties.getProperty(key, defaultValue);
    }
}
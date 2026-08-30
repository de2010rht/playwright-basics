package com.learn.auto.utilities;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;

/**
 * Utility for reading JSON resources from the classpath.
 */
public final class JsonReader
{
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private JsonReader()
    {
    }

    /**
     * Reads and parses a JSON resource available on the test or main classpath.
     *
     * @param resourceName the classpath resource name
     * @return the parsed JSON tree
     */
    public static JsonNode readJsonFile(String resourceName)
    {
        try (InputStream input = JsonReader.class.getClassLoader().getResourceAsStream(resourceName))
        {
            if (input == null)
            {
                throw new IllegalArgumentException("JSON resource not found: " + resourceName);
            }
            return OBJECT_MAPPER.readTree(input);
        }
        catch (IOException e)
        {
            throw new IllegalStateException("Failed to read JSON resource: " + resourceName, e);
        }
    }
}
package com.sevensoupcans.sfsoftware.util.resources.json;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

public class FormattedJsonObject
{
    private final Map<String, Object> values = new HashMap<>();

    public FormattedJsonObject(JSONObject json,
                               Map<String, JsonFieldType> format)
    {
        for(Map.Entry<String, JsonFieldType> entry : format.entrySet())
        {
            String key = entry.getKey();
            JsonFieldType type = entry.getValue();

            if(!json.has(key))
            {
                continue;
            }

            try
            {
                switch(type)
                {
                    case STRING:
                        values.put(key, json.getString(key));
                        break;

                    case INTEGER:
                        values.put(key, json.getInt(key));
                        break;
                }
            }
            catch(Exception e)
            {
                System.err.println("Failed parsing JSON field: " + key);
            }
        }
    }

    public Object get(String key)
    {
        return values.get(key);
    }

    public int getInteger(String key)
    {
        return getInteger(key, 0);
    }
    
    public int getInteger(String key, int defaultValue)
    {
        return values.get(key) != null ? (Integer) values.get(key) : defaultValue;
    }

    public String getString(String key)
    {
        return getString(key, "");
    }
    
    public String getString(String key, String defaultValue)
    {
        return values.get(key) != null ? (String) values.get(key) : defaultValue;
    }

    public boolean has(String key)
    {
        return values.containsKey(key);
    }
}
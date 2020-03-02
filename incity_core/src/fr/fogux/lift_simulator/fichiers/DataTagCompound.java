package fr.fogux.lift_simulator.fichiers;

import java.util.HashMap;
import java.util.Map.Entry;

public class DataTagCompound
{
    protected HashMap<String, String> map = new HashMap<String, String>();

    public DataTagCompound(String data)
    {
        for (String entry : data.split(","))
        {
            String[] keyAndVal = entry.split(":");
            map.put(keyAndVal[0], keyAndVal[1]);
        }
        // System.out.println("le datatag" + map.toString());
    }

    public DataTagCompound()
    {
    }

    public long getLong(String key)
    {
        return Long.parseLong(map.get(key));
    }

    public int getInt(String key)
    {
        return Integer.parseInt(map.get(key));
    }

    public String getString(String key)
    {
        String str = map.get(key);
        str = str.replace("¤v", ",");
        str = str.replace("¤p", ":");
        return str;
    }

    public boolean getBoolean(String key)
    {
        return Boolean.parseBoolean(map.get(key));
    }

    public float getFloat(String key)
    {
        return Float.valueOf(map.get(key));
    }

    public void setFloat(String key, float val)
    {
        map.put(key, String.valueOf(val));
    }

    public void setBoolean(String key, boolean val)
    {
        map.put(key, String.valueOf(val));
    }

    public void setInt(String key, int val)
    {
        map.put(key, String.valueOf(val));
    }

    public void setString(String key, String val)
    {

        val = val.replace("¤", "");
        val = val.replace(",", "¤v");
        val = val.replace(":", "¤p");
        map.put(key, val);
    }

    public void setLong(String key, long val)
    {
        map.put(key, String.valueOf(val));
    }

    public String getValueAsString()
    {
        String value = "";
        for (Entry<String, String> entr : map.entrySet())
        {
            value = value + entr.getKey() + ":" + entr.getValue() + ",";
        }
        if (!value.isEmpty())
        {
            value = value.substring(0, value.length() - 1);
        }
        return value;
    }
}

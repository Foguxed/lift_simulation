package fr.fogux.lift_simulator.fichiers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import fr.fogux.lift_simulator.exceptions.SimulateurException;

public class DataTagCompound
{
    protected HashMap<String, String> map = new HashMap<>();

    public DataTagCompound(final String text)
    {
        final String data = innerValue(text);
        int debutSegment = 0;
        String lastKey = null;
        int innerCompoundCount = 0;
        for(int i = 0; i < data.length(); i ++)
        {
            if(innerCompoundCount == 0)
            {

                if(data.charAt(i) == ':')
                {
                    lastKey = data.substring(debutSegment, i);
                    debutSegment = i+1;
                }
                else if(data.charAt(i) == ',')
                {
                    map.put(lastKey,data.substring(debutSegment, i));
                    lastKey = null;
                    debutSegment = i+1;
                }
                else if(data.charAt(i) == '{')
                {
                    innerCompoundCount ++;
                }
                else if(data.charAt(i) == '}')
                {
                    throw new SimulateurException("probleme caractere " + i + " dans " + data);
                }
            }
            else
            {
                if(data.charAt(i) == '{')
                {
                    innerCompoundCount ++;
                }
                else if(data.charAt(i) == '}')
                {
                    innerCompoundCount --;
                }
            }
        }


        if(lastKey != null)
        {
            map.put(lastKey,data.substring(debutSegment, data.length()));
        }
    }

    public DataTagCompound copy()
    {
        final DataTagCompound d = new DataTagCompound();
        d.overrideWith(this);
        return d;
    }

    /**
     *
     * @param mergedC
     * ajoute le contenu du compound paramètre à ce compound
     * throws SimulateurException si les deux compound ont une key commune
     */
    public void mergeWith(final DataTagCompound mergedC)
    {
        for(final String key : mergedC.map.keySet())
        {
            if(map.containsKey(key))
            {
                throw new SimulateurException("same key " + key + " in " + this + " and " + mergedC);
            }
        }
        map.putAll(mergedC.map);
    }

    public void removeKeysCommunes(final DataTagCompound deleter)
    {
        for(final String key : deleter.map.keySet())
        {
            map.remove(key);
        }
    }

    public void overrideWith(final DataTagCompound overrider)
    {
        map.putAll(overrider.map);
    }

    private String innerValue(final String compound)
    {
        return compound.substring(compound.indexOf("{") + 1, compound.lastIndexOf("}"));
    }

    public DataTagCompound()
    {
    }

    public DataTagCompound getCompound(final String key)
    {
        return new DataTagCompound(map.get(key));
    }

    /*
     * format {{valeur_compound1},{valeur_compound2}}
     */
    public List<DataTagCompound> getCompoundList(final String key)
    {
        final String data = innerValue(map.get(key));
        int debutSegment = 0;
        int innerCompoundCount = 0;
        final List<DataTagCompound> list = new ArrayList<>();
        for(int i = 0; i < data.length(); i ++)
        {
            if(innerCompoundCount == 0)
            {
                if(data.charAt(i) == ',')
                {
                    list.add(new DataTagCompound(data.substring(debutSegment, i)));
                    debutSegment = i+1;
                }
                else if(data.charAt(i) == '{')
                {
                    innerCompoundCount ++;
                }
                else if(data.charAt(i) == '}')
                {
                    throw new SimulateurException("probleme caractere " + i + " dans " + data);
                }
            }
            else
            {
                if(data.charAt(i) == '{')
                {
                    innerCompoundCount ++;
                }
                else if(data.charAt(i) == '}')
                {
                    innerCompoundCount --;
                }
            }
        }
        if(debutSegment < data.length())
        {
            list.add(new DataTagCompound(data.substring(debutSegment, data.length())));
        }
        return list;
    }

    public long getLong(final String key)
    {
        return Long.parseLong(map.get(key));
    }

    public int getInt(final String key)
    {
        return Integer.parseInt(map.get(key));
    }

    public String getString(final String key)
    {
        String str = map.get(key);
        str = str.replace("¤v", ",");
        str = str.replace("¤p", ":");
        return str;
    }

    public boolean hasKey(final String key)
    {
        return map.containsKey(key);
    }

    public boolean getBoolean(final String key)
    {
        return Boolean.parseBoolean(map.get(key));
    }

    public float getFloat(final String key)
    {
        return Float.valueOf(map.get(key));
    }

    public void setCompoundList(final String key, final List<DataTagCompound> list)
    {
        String val = "";
        for(final DataTagCompound c : list)
        {
            val = val + c.getValueAsString() + "," ;
        }
        val = val.substring(0, val.length()-1);
        map.put(key, "{" + val + "}");
    }

    public void setFloat(final String key, final float val)
    {
        map.put(key, String.valueOf(val));
    }

    public void setBoolean(final String key, final boolean val)
    {
        map.put(key, String.valueOf(val));
    }

    public void setInt(final String key, final int val)
    {
        map.put(key, String.valueOf(val));
    }

    public void setString(final String key, String val)
    {

        val = val.replace("¤", "");
        val = val.replace(",", "¤v");
        val = val.replace(":", "¤p");
        map.put(key, val);
    }

    public void setLong(final String key, final long val)
    {
        map.put(key, String.valueOf(val));
    }

    public String getValueAsString()
    {
        String value = "";
        for (final Entry<String, String> entr : map.entrySet())
        {
            value = value + entr.getKey() + ":" + entr.getValue() + ",";
        }
        if (!value.isEmpty())
        {
            value = value.substring(0, value.length() - 1);
        }
        return "{" + value + "}";
    }

    @Override
    public String toString()
    {
        return getValueAsString();
    }
}

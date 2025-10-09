package com.sevensoupcans.sfsoftware.util;

import java.util.ArrayList;
import java.util.List;

public abstract class BitFlagUtils 
{
    public static int toInt(List<Boolean> flags) 
    {
        int result = 0;
        for (int i = 0; i < flags.size(); i++) 
        {
            if (flags.get(i)) 
            {
                result |= (1 << i);
            }
        }
        return result;
    }

    public static List<Boolean> fromInt(int bitFlag, int size) 
    {
        List<Boolean> result = new ArrayList<>();
        for (int i = 0; i < size; i++) 
        {
            result.add((bitFlag & (1 << i)) != 0);
        }
        return result;
    }

    public static boolean isFlagSet(int bitFlag, int index) 
    {
        return (bitFlag & (1 << index)) != 0;
    }

    public static int setFlag(int bitFlag, int index, boolean value) 
    {
        if (value) 
        {
            return bitFlag | (1 << index);
        } 
        else 
        {
            return bitFlag & ~(1 << index);
        }
    }
}
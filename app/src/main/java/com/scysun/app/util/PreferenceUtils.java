package com.scysun.app.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.scysun.app.core.Constants;

/**
 * @author Phoenix
 */
public class PreferenceUtils
{
    public static String readSharedPreference(Context context, String preferenceName, String key, String defaultValue)
    {
        String result = context.getSharedPreferences(preferenceName, Context.MODE_PRIVATE).getString(key, defaultValue);
        return result;
    }

    public static int readSharedPreferenceIntVariable(Context context, String preferenceName, String key, int defaultValue)
    {
        int result = context.getSharedPreferences(preferenceName, Context.MODE_PRIVATE).getInt(key, defaultValue);
        return result;
    }

    public static boolean readSharedPreferenceBooleanVariable(Context context, String preferenceName, String key, boolean defaultValue)
    {
        boolean result = context.getSharedPreferences(preferenceName, Context.MODE_PRIVATE).getBoolean(key, defaultValue);
        return result;
    }

    public static SharedPreferences.Editor writeSharedPreference(Context context, String preferenceName, String key, String value, boolean isToCommit)
    {
        SharedPreferences.Editor editor = context.getSharedPreferences(preferenceName, Context.MODE_PRIVATE).edit();
        editor.putString(key, value);
        if(isToCommit)
            editor.commit();
        return editor;
    }

    public static SharedPreferences.Editor writeSharedPreferenceIntValue(Context context, String preferenceName, String key, int value, boolean isToCommit)
    {
        SharedPreferences.Editor editor = context.getSharedPreferences(preferenceName, Context.MODE_PRIVATE).edit();
        editor.putInt(key, value);
        if(isToCommit)
            editor.commit();

        return editor;
    }

    public static SharedPreferences.Editor writeSharedPreferenceBooleanValue(Context context, String preferenceName, String key, boolean value, boolean isToCommit)
    {
        SharedPreferences.Editor editor = context.getSharedPreferences(preferenceName, Context.MODE_PRIVATE).edit();
        editor.putBoolean(key, value);
        if(isToCommit)
            editor.commit();
        return editor;
    }
}

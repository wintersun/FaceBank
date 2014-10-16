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
    public static String readSharedPreference(Activity activity, String preferenceName, String key, String defaultValue)
    {
        String result = activity.getSharedPreferences(preferenceName, Context.MODE_PRIVATE).getString(key, defaultValue);
        return result;
    }

    public static int readSharedPreferenceIntVariable(Activity activity, String preferenceName, String key, int defaultValue)
    {
        int result = activity.getSharedPreferences(preferenceName, Context.MODE_PRIVATE).getInt(key, defaultValue);
        return result;
    }

    public static SharedPreferences.Editor writeSharedPreference(Activity activity, String preferenceName, String key, String value, boolean isToCommit)
    {
        SharedPreferences.Editor editor = activity.getSharedPreferences(preferenceName, Context.MODE_PRIVATE).edit();
        editor.putString(key, value);
        if(isToCommit)
            editor.commit();
        return editor;
    }

    public static SharedPreferences.Editor writeSharedPreferenceIntValue(Activity activity, String preferenceName, String key, int value, boolean isToCommit)
    {
        SharedPreferences.Editor editor = activity.getSharedPreferences(preferenceName, Context.MODE_PRIVATE).edit();
        editor.putInt(key, value);
        if(isToCommit)
            editor.commit();

        return editor;
    }
}

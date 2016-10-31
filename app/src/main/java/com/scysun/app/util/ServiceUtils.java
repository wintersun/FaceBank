package com.scysun.app.util;

import android.app.ActivityManager;


/**
 * @author Phoenix
 */
public class ServiceUtils {

    /***
     * Check if a service is running
     * @param manager
     * @param clazz
     * @return
     */
    public static boolean isServiceRunning(ActivityManager manager, Class clazz) {
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (clazz.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}

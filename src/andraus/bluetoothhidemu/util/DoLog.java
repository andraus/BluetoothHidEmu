package andraus.bluetoothhidemu.util;

import java.util.HashMap;
import java.util.Map;

import android.util.Log;

/**
 * Wrapper for android.util.Log. This class will check for the proper log level before writing messages to the log
 * This class uses a cache in order to reduce overhead for Log.isLoggable() calls. The cache may be disabled by 
 * setting USE_CACHE to false.
 * 
 * Note: to enable the logs for your tag, use:
 * 
 * - adb shell setprop log.tag.<TAG> <LEVEL>
 * 
 * e.g.
 * 
 * - adb shell setprop log.tag.BluetoothKeyb DEBUG
 * 
 * 
 * @author andraus
 *
 */
public class DoLog {
    
    private static boolean USE_CACHE = true;
    private static Map<String, Boolean> cacheMap = new HashMap<String, Boolean>();

    private static boolean isLoggable(String tag, int level) {
        
        if (!USE_CACHE) {
            return Log.isLoggable(tag, level);
        } else {
        
            String key = tag + "-" + level;
            
            Boolean isLoggable = cacheMap.get(key);
            
            if (isLoggable == null) {
                isLoggable = Boolean.valueOf(Log.isLoggable(tag, level));
                cacheMap.put(key, isLoggable);
            }       
            
            return isLoggable.booleanValue();
        }
        
    }
    
    public static void v(String tag, String msg) {
        if (isLoggable(tag, Log.VERBOSE)) {
            Log.v(tag, msg);
        }
    }
    public static void v(String tag, String msg, Exception e) {
        if (isLoggable(tag, Log.VERBOSE)) {
            Log.v(tag, msg, e);
        }
    }
    
    public static void d(String tag, String msg) {
        if (isLoggable(tag, Log.DEBUG)) {
            Log.d(tag, msg);
        }
    }
    public static void d(String tag, String msg, Exception e) {
        if (isLoggable(tag, Log.DEBUG)) {
            Log.d(tag, msg, e);
        }
    }
    
    public static void i(String tag, String msg) {
        if (isLoggable(tag, Log.INFO)) {
            Log.i(tag, msg);
        }
    }
    public static void i(String tag, String msg, Exception e) {
        if (isLoggable(tag, Log.INFO)) {
            Log.i(tag, msg, e);
        }
    }
    
    public static void w(String tag, String msg) {
        if (isLoggable(tag, Log.WARN)) {
            Log.w(tag, msg);
        }
    }
    public static void w(String tag, String msg, Exception e) {
        if (isLoggable(tag, Log.WARN)) {
            Log.w(tag, msg, e);
        }
    }
    
    public static void e(String tag, String msg) {
        if (isLoggable(tag, Log.ERROR)) {
            Log.e(tag, msg);
        }
    }
    public static void e(String tag, String msg, Exception e) {
        if (isLoggable(tag, Log.ERROR)) {
            Log.e(tag, msg, e);
        }
    }

}

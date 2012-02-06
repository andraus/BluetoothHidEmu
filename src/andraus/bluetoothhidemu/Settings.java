package andraus.bluetoothhidemu;

import java.security.acl.LastOwnerException;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

public class Settings extends PreferenceActivity {
    
    private final static String PREF_LAST_DEVICE = "last_device";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        addPreferencesFromResource(R.xml.main_preferences);

    }

    /**
     * 
     * @param context
     * @return
     */
    public static int getEmulationMode(Context context) {
        String value = PreferenceManager.getDefaultSharedPreferences(context).getString("emulation_mode", "-1");
        
        return Integer.valueOf(value);
    }
    
    /**
     * 
     * @param context
     * @return
     */
    public static String getLastConnectedDevice(Context context) {
        String value = PreferenceManager.getDefaultSharedPreferences(context).getString(PREF_LAST_DEVICE, null);
        
        return value;
    }
    
    /**
     * 
     * @param context
     * @param value
     */
    public static void setLastDevice(Context context, String value) {
        
        savePref(context, PREF_LAST_DEVICE, value);
    }
    
    /**
     * 
     * @param context
     * @param key
     * @param value
     */
    private static void savePref(Context context, String key, String value) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPref.edit();
        
        editor.putString(key, value);
        editor.apply();
        
    }
    
    

}

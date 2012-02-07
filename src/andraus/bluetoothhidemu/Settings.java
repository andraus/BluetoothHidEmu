package andraus.bluetoothhidemu;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

/**
 * Main Settings screen.
 */
public class Settings extends PreferenceActivity implements OnSharedPreferenceChangeListener {
    
    /* package */ final static String PREF_EMULATION_MODE = "emulation_mode";
    /* package */ final static String PREF_LAST_DEVICE = "last_device";
    /* package */ final static String PREF_SPOOF = "spoof_enabled";

    /**
     * 
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        addPreferencesFromResource(R.xml.main_preferences);

    }

    /**
     * 
     */
    @Override
    protected void onPause() {
        
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        
        super.onPause();
    }

    /**
     * 
     */
    @Override
    protected void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        updateEmulationModeSummary();
    }

    /**
     * 
     * @param context
     * @return
     */
    public static int getEmulationMode(Context context) {
        String value = PreferenceManager.getDefaultSharedPreferences(context).getString(PREF_EMULATION_MODE, "-1");
        
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

    /**
     * 
     */
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(Settings.PREF_EMULATION_MODE)) {
            updateEmulationModeSummary();
        }
        
    }
    
    /**
     * 
     */
    public void updateEmulationModeSummary() {
        ListPreference emulationModePreference = (ListPreference) getPreferenceScreen().findPreference(PREF_EMULATION_MODE);
        String summary = getResources().getString(R.string.msg_pref_summary_emulation_mode);
        emulationModePreference.setSummary(String.format(summary, emulationModePreference.getEntry()));
        
    }

}

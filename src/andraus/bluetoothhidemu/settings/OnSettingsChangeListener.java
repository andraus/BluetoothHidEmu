package andraus.bluetoothhidemu.settings;

import andraus.bluetoothhidemu.BluetoothHidEmuActivity;
import andraus.bluetoothhidemu.util.DoLog;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;

/**
 * Listener to monitor settings being changed.
 */
public class OnSettingsChangeListener implements OnSharedPreferenceChangeListener {

    private static final String TAG = BluetoothHidEmuActivity.TAG;

    /**
     * 
     */
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        
        if (key.equals(Settings.PREF_EMULATION_MODE)) {
            DoLog.d(TAG, "new emulation mode: " + sharedPreferences.getString(key, null));
        } else if (key.equals(Settings.PREF_SPOOF)) {
            DoLog.d(TAG, "spoof = " + sharedPreferences.getBoolean(key, false));
        }

    }

}

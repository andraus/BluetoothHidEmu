package andraus.bluetoothhidemu.settings;

import java.util.Set;

import andraus.bluetoothhidemu.BluetoothHidEmuActivity;
import andraus.bluetoothhidemu.R;
import andraus.bluetoothhidemu.spoof.BluetoothAdapterSpoofer;
import andraus.bluetoothhidemu.spoof.BluetoothAdapterSpooferFactory;
import andraus.bluetoothhidemu.util.DoLog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.os.Handler;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceManager;

/**
 * Main Settings screen.
 */
public class Settings extends PreferenceActivity implements OnSharedPreferenceChangeListener {

    private static final String TAG = BluetoothHidEmuActivity.TAG;
    
    public static final int BLUETOOTH_REQUEST_OK = 1;
    public static final int BLUETOOTH_DISCOVERABLE_DURATION = 15;
    
    /* package */ final static String PREF_LAST_DEVICE = "last_device";
    /* package */ final static String PREF_EMULATION_MODE = "emulation_mode";
    /* package */ final static String PREF_SPOOF = "spoof_enabled";
    private final static String PREF_BT_DISCOVERABLE = "bt_discoverable";
    private final static String PREF_DEVICE_LIST = "bt_device_list";
    
    private BluetoothAdapterSpoofer mSpoofer = null;
    
    private CheckBoxPreference mBtDiscoverablePreference = null;
    private PreferenceCategory mDeviceListCategory = null;

    // Handler to update screen elements
    private Handler mUiUpdateHandler = null;
    
    // counter for bluetooth discoverability timeout
    private int mCountDown = 0;
    
    // workaround for onResume() being called twice after bluetooth dialog
    private boolean mIsResumingFromDialog = false;
    
    // Runnable used with mUiUpdateHandler to display discoverability countdown
    private final Runnable mUpdateCountdownSummaryRunnable = new Runnable() {
        public void run() {
            
            if (BluetoothAdapter.getDefaultAdapter().getScanMode() == BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            
                
                if (mCountDown != Integer.MAX_VALUE && mCountDown > 0) {
                    mBtDiscoverablePreference.setSummary(
                            getResources().getQuantityString(
                                    R.plurals.msg_pref_summary_bluetooth_discoverable_timeout, 
                                    mCountDown, 
                                    mCountDown));
                    mCountDown--;
                    
                } else if (mCountDown == Integer.MAX_VALUE) {
                    mBtDiscoverablePreference.setSummary(R.string.Msg_pref_summary_bluetooth_discoverable_no_timeout);
                }
                
                mUiUpdateHandler.postDelayed(this, 1000 /* ms */);
                
            } else {
                setBluetoothDiscoverableCheck(false);
            }
            
        }
    };


    /**
     * onCreate
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        addPreferencesFromResource(R.xml.main_preferences);
        
        mBtDiscoverablePreference = (CheckBoxPreference) findPreference(PREF_BT_DISCOVERABLE);
        mDeviceListCategory = (PreferenceCategory) findPreference(PREF_DEVICE_LIST);
        
        mSpoofer = BluetoothAdapterSpooferFactory.getInstance(getApplicationContext(), BluetoothAdapter.getDefaultAdapter());
        
        mUiUpdateHandler = new Handler();
        
        mBtDiscoverablePreference.setOnPreferenceClickListener(new OnPreferenceClickListener() {
            
            @Override
            public boolean onPreferenceClick(Preference preference) {
                
                // Do not enable the preference right away, need to fire the bluetooth discoverability intent first
                setBluetoothDiscoverableCheck(!mBtDiscoverablePreference.isChecked());
                
                if (!mBtDiscoverablePreference.isChecked()) {
                
                    Intent bluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                    bluetoothIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, Settings.BLUETOOTH_DISCOVERABLE_DURATION);
                    startActivityForResult(bluetoothIntent, Settings.BLUETOOTH_REQUEST_OK);
                }
                
                return false;
            }
            
        });

    }

    /**
     * onPause
     */
    @Override
    protected void onPause() {
        
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        mUiUpdateHandler.removeCallbacksAndMessages(null);
        
        super.onPause();
    }

    /**
     * onResume
     */
    @Override
    protected void onResume() {
        super.onResume();
        
        populateDeviceList();

        if (!mIsResumingFromDialog) {
            getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
            updateEmulationModeSummary();
            
            
            setBluetoothDiscoverableCheck(BluetoothAdapter.getDefaultAdapter().getScanMode() == BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE);
            if (mBtDiscoverablePreference.isChecked()) {
                mCountDown = Integer.MAX_VALUE;
                mUiUpdateHandler.post(mUpdateCountdownSummaryRunnable);
            }
        } else {
            mIsResumingFromDialog = false;
        }
        
        
    }

    /**
     * onActivityResult - used to monitor bluetooth discoverable states    
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        DoLog.d(TAG, "onActivityResult: " + requestCode + " " + resultCode);
        mIsResumingFromDialog = true;
        
        if (requestCode == BLUETOOTH_REQUEST_OK && resultCode == BLUETOOTH_DISCOVERABLE_DURATION) {
            setBluetoothDiscoverableCheck(true);
            
            mUiUpdateHandler.removeCallbacksAndMessages(null);
            mCountDown = BLUETOOTH_DISCOVERABLE_DURATION;
            mUiUpdateHandler.post(mUpdateCountdownSummaryRunnable);
            
            //savePref(this, PREF_BT_DISCOVERABLE, true);
        } else if (requestCode == BLUETOOTH_REQUEST_OK && resultCode == RESULT_CANCELED) {
            setBluetoothDiscoverableCheck(false);
            //savePref(this, PREF_BT_DISCOVERABLE, false);
        }
        
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * getEmulationMode
     * 
     * @param context
     * @return
     */
    public static int getEmulationMode(Context context) {
        String value = PreferenceManager.getDefaultSharedPreferences(context).getString(PREF_EMULATION_MODE, "-1");
        
        return Integer.valueOf(value);
    }
    
    /**
     * getLastConnectedDevice
     * 
     * @param context
     * @return
     */
    public static String getLastConnectedDevice(Context context) {
        String value = PreferenceManager.getDefaultSharedPreferences(context).getString(PREF_LAST_DEVICE, null);
        
        return value;
    }
    
    /**
     * setLastDevice
     * 
     * @param context
     * @param value
     */
    public static void setLastDevice(Context context, String value) {
        
        savePref(context, PREF_LAST_DEVICE, value);
    }
    
    /**
     * savePref
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
     * onSharedPreferenceChanged
     * 
     */
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(Settings.PREF_EMULATION_MODE)) {
            updateEmulationModeSummary();
        }
        
    }
    
    /**
     * updateEmulationModeSummary
     */
    private void updateEmulationModeSummary() {
        ListPreference emulationModePreference = (ListPreference) getPreferenceScreen().findPreference(PREF_EMULATION_MODE);
        String summary = getResources().getString(R.string.msg_pref_summary_emulation_mode);
        emulationModePreference.setSummary(String.format(summary, emulationModePreference.getEntry()));
        
    }
    
    /**
     * Toggle state for Bluetooth discoverable item
     * @param state
     */
    private void setBluetoothDiscoverableCheck(boolean state) {
        mBtDiscoverablePreference.setChecked(state);
        mBtDiscoverablePreference.setEnabled(!state);
        if (!state) {
            if (mSpoofer.isSpoofed()) mSpoofer.tearDownSpoofing();
            mBtDiscoverablePreference.setSummary(getResources().getString(R.string.msg_pref_summary_bluetooth_discoverable_click));
        } else {
            // TODO: hard-coded - Select mode properly
            if (!mSpoofer.isSpoofed()) mSpoofer.tearUpSpoofing(BluetoothAdapterSpoofer.SpoofMode.HID_GENERIC);
        }
    }
    
    /**
     * 
     */
    private void populateDeviceList() {
        mDeviceListCategory.removeAll();
        Set<BluetoothDevice> deviceSet = BluetoothAdapter.getDefaultAdapter().getBondedDevices();
        
        for (BluetoothDevice device: deviceSet) {
            Preference devicePref = new Preference(this);
            devicePref.setTitle(device.getName());
            devicePref.setSummary(device.getAddress());
            
            mDeviceListCategory.addPreference(devicePref);
        }
        
    }
    
    
}

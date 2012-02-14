package andraus.bluetoothhidemu.settings;

import java.util.Set;

import andraus.bluetoothhidemu.BluetoothHidEmuActivity;
import andraus.bluetoothhidemu.R;
import andraus.bluetoothhidemu.spoof.BluetoothAdapterSpoofer.SpoofMode;
import andraus.bluetoothhidemu.util.DoLog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.os.Handler;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
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
    public static final int BLUETOOTH_DISCOVERABLE_DURATION = 30;
    
    /* package */ final static String PREF_LAST_DEVICE = "last_device";
    /* package */ final static String PREF_EMULATION_MODE = "emulation_mode";
    private final static String PREF_BT_DISCOVERABLE = "bt_discoverable";
    private final static String PREF_DEVICE_LIST = "bt_device_list";

    private ListPreference mEmulationModeListPreference = null;
    private CheckBoxPreference mBtDiscoverablePreference = null;
    private PreferenceCategory mDeviceListCategory = null;

    // Handler to update screen elements
    private Handler mUiUpdateHandler = null;
    
    // counter for bluetooth discoverability timeout
    private int mCountdown = 0;
    
    // workaround for onResume() being called twice after bluetooth dialog
    private boolean mIsResumingFromDialog = false;
    
    private BluetoothDeviceStateReceiver mBluetoothDeviceReceiver = null;
    private BluetoothAdapterStateReceiver mBluetoothAdapterStateReceiver = null;
    
    
    // Runnable used with mUiUpdateHandler to display discoverability countdown
    private final Runnable mUpdateCountdownSummaryRunnable = new Runnable() {
        public void run() {
            
            if (BluetoothAdapter.getDefaultAdapter().getScanMode() == BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            
                
                if (mCountdown != Integer.MAX_VALUE && mCountdown > 0) {
                    mBtDiscoverablePreference.setSummary(
                            getResources().getQuantityString(
                                    R.plurals.msg_pref_summary_bluetooth_discoverable_timeout, 
                                    mCountdown, 
                                    mCountdown));
                    mCountdown--;
                    
                } else if (mCountdown == Integer.MAX_VALUE) {
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
        DoLog.d(TAG, "onCreate()");
        addPreferencesFromResource(R.xml.main_preferences);
        
        mEmulationModeListPreference = (ListPreference) findPreference(PREF_EMULATION_MODE);
        mBtDiscoverablePreference = (CheckBoxPreference) findPreference(PREF_BT_DISCOVERABLE);
        mDeviceListCategory = (PreferenceCategory) findPreference(PREF_DEVICE_LIST);
        populateDeviceList(mDeviceListCategory);

        mBluetoothDeviceReceiver = new BluetoothDeviceStateReceiver(mDeviceListCategory);
        registerReceiver(mBluetoothDeviceReceiver, new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED));
        
        mBluetoothAdapterStateReceiver = new BluetoothAdapterStateReceiver(this);
        registerReceiver(mBluetoothAdapterStateReceiver, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));
        
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
        
        mEmulationModeListPreference.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
            
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                
                updateEmulationModeSummary();
                
                return true;
            }
        });
        
    }
    
    /**
     * 
     */
    @Override
    protected void onDestroy() {

        if (mBluetoothDeviceReceiver != null) {
            unregisterReceiver(mBluetoothDeviceReceiver);
        }
        if (mBluetoothAdapterStateReceiver != null) {
            unregisterReceiver(mBluetoothAdapterStateReceiver);
        }
        
        super.onDestroy();
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
        
        if (!mIsResumingFromDialog) {
            getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
            updateEmulationModeSummary();
            
            setBluetoothDiscoverableCheck(BluetoothAdapter.getDefaultAdapter().getScanMode() == BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE);
            if (mBtDiscoverablePreference.isChecked()) {
                mCountdown = Integer.MAX_VALUE;
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
            mCountdown = BLUETOOTH_DISCOVERABLE_DURATION;
            mUiUpdateHandler.post(mUpdateCountdownSummaryRunnable);
            
        } else if (requestCode == BLUETOOTH_REQUEST_OK && resultCode == RESULT_CANCELED) {
            setBluetoothDiscoverableCheck(false);
        }
        
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * getEmulationMode
     * 
     * @param context
     * @return
     */
    public static SpoofMode getEmulationMode(Context context) {
        int value = Integer.valueOf(PreferenceManager.getDefaultSharedPreferences(context).getString(PREF_EMULATION_MODE, "-1"));
        
        /*
         * Corolary: BluetoothAdapterSpoofer.SpoofEnum match emulation modes string arrays in arrays.xml
         */
        
        switch (value) {
        case 0:
            return SpoofMode.HID_GENERIC;
        case 1:
            return SpoofMode.HID_BDREMOTE;
        default:
            throw new IllegalStateException("Invalid Emulation mode");
            
        }
        
        
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
        String summary = getResources().getString(R.string.msg_pref_summary_emulation_mode);
        mEmulationModeListPreference.setSummary(String.format(summary, mEmulationModeListPreference.getEntry()));
        
    }
    
    /**
     * Toggle state for Bluetooth discoverable item
     * @param state
     */
    private void setBluetoothDiscoverableCheck(boolean state) {
        mBtDiscoverablePreference.setChecked(state);
        mBtDiscoverablePreference.setEnabled(!state);
        mEmulationModeListPreference.setEnabled(!state);
        if (!state) {
            mBtDiscoverablePreference.setSummary(getResources().getString(R.string.msg_pref_summary_bluetooth_discoverable_click));
        }
    }
    
    /**
     * 
     */
    private void populateDeviceList(PreferenceCategory deviceListCategory) {
        deviceListCategory.removeAll();
        Set<BluetoothDevice> deviceSet = BluetoothAdapter.getDefaultAdapter().getBondedDevices();
        
        for (BluetoothDevice device: deviceSet) {
            Preference devicePref = new Preference(this);
            devicePref.setTitle(device.getName().equals("") ? device.getAddress() : device.getName());
            devicePref.setSummary(device.getAddress());
            
            deviceListCategory.addPreference(devicePref);
        }
        
    }
    
}

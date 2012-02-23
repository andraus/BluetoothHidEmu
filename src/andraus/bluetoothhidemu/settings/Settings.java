package andraus.bluetoothhidemu.settings;

import java.util.Set;

import andraus.bluetoothhidemu.BluetoothHidEmuActivity;
import andraus.bluetoothhidemu.R;
import andraus.bluetoothhidemu.spoof.Spoof;
import andraus.bluetoothhidemu.spoof.Spoof.SpoofMode;
import andraus.bluetoothhidemu.util.DoLog;
import andraus.bluetoothhidemu.view.BluetoothDeviceView;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
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
 * 
 * TODO: Implement mechanism to "rename" bluetooth devices.
 * 
 */
public class Settings extends PreferenceActivity {

    private static final String TAG = BluetoothHidEmuActivity.TAG;
    
    public static final int BLUETOOTH_REQUEST_OK = 1;
    public static final int BLUETOOTH_REQUEST_DISCOVERABLE_FOR_PS3_OK = 2;
    public static final int BLUETOOTH_DISCOVERABLE_DURATION_100 = 100;
    public static final int BLUETOOTH_DISCOVERABLE_DURATION_5 = 5;
    
    public static final String FILE_PREF_DEVICES = "bt_devices";
    /* package */ final static String PREF_LAST_DEVICE = "last_device";
    /* package */ final static String PREF_EMULATION_MODE = "emulation_mode";
    private final static String PREF_BT_DISCOVERABLE = "bt_discoverable";
    private final static String PREF_DEVICE_LIST = "bt_device_list";

    private CheckBoxPreference mBtDiscoverablePreference = null;
    private ListPreference mEmulationModeListPreference = null;
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
                    mBtDiscoverablePreference.setSummary(R.string.msg_pref_summary_bluetooth_discoverable_no_timeout);
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
        
        mBtDiscoverablePreference = (CheckBoxPreference) findPreference(PREF_BT_DISCOVERABLE);
        mEmulationModeListPreference = (ListPreference) findPreference(PREF_EMULATION_MODE);
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
                
                    startActivityForResult(createBluetoothDiscoverableIntent(Settings.BLUETOOTH_DISCOVERABLE_DURATION_100), Settings.BLUETOOTH_REQUEST_OK);
                }
                
                return false;
            }
            
        });
        
        mEmulationModeListPreference.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
        	            
        	            @Override
        	            public boolean onPreferenceChange(Preference preference, Object newValue) {
        	
        	                
        	                mEmulationModeListPreference.setSummary(getEmulationModeSummary(getApplicationContext(), Integer.valueOf((String) newValue)));
        	                
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
     * onResume
     */
    @Override
    protected void onResume() {
        super.onResume();
        
        if (!mIsResumingFromDialog) {
            
            setBluetoothDiscoverableCheck(BluetoothAdapter.getDefaultAdapter().getScanMode() == BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE);
            mEmulationModeListPreference.setSummary(
            		                    getEmulationModeSummary(this, Integer.valueOf(
            		                            PreferenceManager.getDefaultSharedPreferences(this).getString(PREF_EMULATION_MODE, "-1"))));

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
        
        if (requestCode == BLUETOOTH_REQUEST_OK && resultCode == BLUETOOTH_DISCOVERABLE_DURATION_100) {
            setBluetoothDiscoverableCheck(true);
            
            mUiUpdateHandler.removeCallbacksAndMessages(null);
            mCountdown = BLUETOOTH_DISCOVERABLE_DURATION_100;
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
    public static SpoofMode getPrefEmulationMode(Context context) {
    	
    	int value = Integer.valueOf(PreferenceManager.getDefaultSharedPreferences(context).getString(PREF_EMULATION_MODE, "-1"));

        return Spoof.fromInt(value);
        
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
     * Returns emulation mode for the specified device
     * 
     * @param context
     * @param device
     * @return
     */
    public static SpoofMode getEmulationMode(Context context, BluetoothDevice device) {
        SharedPreferences devicesPref = context.getSharedPreferences(Settings.FILE_PREF_DEVICES, Context.MODE_PRIVATE);
        SpoofMode mode = Spoof.fromInt(devicesPref.getInt(device.getAddress(), Spoof.intValue(SpoofMode.INVALID)));

       return mode;
    }
    
    /**
     * Store emulation mode for the paired device as a shared preference
     * 
     * @param context
     * @param device
     */
    public static void storeDeviceEmulationMode(Context context, BluetoothDevice device, SpoofMode spoofMode) {
        SharedPreferences devicesPref = context.getSharedPreferences(Settings.FILE_PREF_DEVICES, Context.MODE_PRIVATE);
        
        SharedPreferences.Editor editor = devicesPref.edit();
        editor.putInt(device.getAddress(), Spoof.intValue(spoofMode));
        editor.apply();
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
     * getEmulationModeSummary
     * 
     * @param modeIndex
     */
    public static String getEmulationModeSummary(Context context, int modeIndex) {
        String[] modeNames = context.getResources().getStringArray(R.array.emulation_mode_names);
        
        return modeNames[modeIndex];
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
            
            BluetoothDeviceView.isBluetoothDevicePs3(device);
            
            SpoofMode spoofMode = getEmulationMode(this, device);
            String emulationSummary = (spoofMode == SpoofMode.INVALID) ? 
                    getResources().getString(R.string.msg_pref_summary_device_emulation_mode_invalid) :
                    getEmulationModeSummary(this, Spoof.intValue(spoofMode)); 
            
            devicePref.setSummary(device.getAddress() + "\n" 
                        + String.format(getResources().getString(R.string.msg_pref_summary_device_emulation_mode), 
                                emulationSummary));

            deviceListCategory.addPreference(devicePref);
        }
        
    }
    
    /**
     * 
     * @param context
     */
    public static void showPs3InvalidModeDialog(Context context) {
    	
	    AlertDialog dialog =  new AlertDialog.Builder(context).create();
	    dialog.setTitle(context.getResources().getString(R.string.msg_dialog_invalid_ps3_bonding_title));
	    dialog.setMessage(String.format(
	    						context.getResources().getString(R.string.msg_dialog_invalid_ps3_bonding_text), 
	    						getEmulationModeSummary(context, Spoof.intValue(SpoofMode.HID_GENERIC)),
	    						getEmulationModeSummary(context, Spoof.intValue(SpoofMode.HID_PS3KEYPAD))
	    						));
	    dialog.setButton(DialogInterface.BUTTON_NEUTRAL, context.getResources().getString(android.R.string.ok), new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				
			}
		});
	    
	    dialog.show();

    }
    
    /**
     * 
     * @param duration
     * @return
     */
    public static Intent createBluetoothDiscoverableIntent(final int duration) {
        Intent bluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        bluetoothIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, duration);
        
        return bluetoothIntent;
    }
        
}

package andraus.bluetoothhidemu.settings;

import andraus.bluetoothhidemu.BluetoothHidEmuActivity;
import andraus.bluetoothhidemu.R;
import andraus.bluetoothhidemu.spoof.Spoof;
import andraus.bluetoothhidemu.spoof.Spoof.SpoofMode;
import andraus.bluetoothhidemu.util.DoLog;
import andraus.bluetoothhidemu.view.BluetoothDeviceArrayAdapter;
import andraus.bluetoothhidemu.view.BluetoothDeviceView;
import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.widget.Spinner;

/**
 * 
 */
public class BluetoothDeviceStateReceiver extends BroadcastReceiver {
    
    private static final String TAG = BluetoothHidEmuActivity.TAG;
    
    private PreferenceCategory mBluetoothDevicePrefCategory = null;
    private Spinner mDeviceSpinner = null;
    

    /**
     * 
     * @param bluetoothDevicePrefCategory
     */
    public BluetoothDeviceStateReceiver(PreferenceCategory bluetoothDevicePrefCategory) {
        super();
        
        mBluetoothDevicePrefCategory = bluetoothDevicePrefCategory;
    }
    
    /**
     * 
     * @param bluetoothDeviceArrayAdapter
     */
    public BluetoothDeviceStateReceiver(Spinner deviceSpinner) {
        super();
        
        mDeviceSpinner = deviceSpinner;
    }

    /**
     * 
     */
    @Override
    public void onReceive(Context context, Intent intent) {
    	
    	DoLog.d(TAG, "context: " + context);
        
        if (intent.getAction().equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)) {
            final BluetoothDevice device = (BluetoothDevice) intent.getExtras().get(BluetoothDevice.EXTRA_DEVICE);
            final int state = intent.getExtras().getInt(BluetoothDevice.EXTRA_BOND_STATE);
            final int prevState = intent.getExtras().getInt(BluetoothDevice.EXTRA_PREVIOUS_BOND_STATE);
            
            DoLog.d("BluetoothHidEmu", String.format("device receiver: device = %s | state = %s | prevState = %s", device, state, prevState));
            
            switch (state) {
            case BluetoothDevice.BOND_BONDED:
            	
            	SpoofMode emulationMode = Settings.getPrefEmulationMode(context);
            	
            	if (BluetoothDeviceView.isBluetoothDevicePs3(device) && emulationMode != SpoofMode.HID_PS3KEYPAD) {
            		emulationMode = SpoofMode.INVALID;

            		/*
            		 *  There are two instances of BluetoothDeviceStateReceiver: one is created
            		 *  for the Settings activity, the other for the main activity. We need to
            		 *  verify which one is on foreground before showing this dialog.
            		 *  
            		 */
            		
               		if ( ((Activity) context).hasWindowFocus() ) {
               			Settings.showPs3InvalidModeDialog(context);
               		}
               	 
            	}
            	
        		addDeviceToPreference(context, device, emulationMode);
        		addDeviceToArrayAdapter(device, emulationMode);
        		Settings.storeDeviceEmulationMode(context, device, emulationMode);
                break;
            case BluetoothDevice.BOND_NONE:
                removeDeviceFromPreference(device);
                removeDeviceFromArrayAdapter(device);
                break;
            }
        }
    }

    /**
     * 
     * @param device
     */
    private void removeDeviceFromPreference(BluetoothDevice device) {
        
        if (mBluetoothDevicePrefCategory == null) {
            return;
        }
        
        for (int i = 0; i < mBluetoothDevicePrefCategory.getPreferenceCount(); i++) {
            Preference pref = (Preference) mBluetoothDevicePrefCategory.getPreference(i);
            
            /*
             * Summary has format of <address>/n<Emulation:lorem...>
             * Need to compare only with address.
             */
            String summary = pref.getSummary().toString();
            String[] token = summary.split("\n"); 
            
            if (token[0].equals(device.getAddress())) {
                mBluetoothDevicePrefCategory.removePreference(pref);
                break;
            }
        }
    }

    /**
     * 
     * @param device
     */
    private void removeDeviceFromArrayAdapter(BluetoothDevice device) {
        
        if (mDeviceSpinner == null) {
            return;
        }
        
        BluetoothDeviceArrayAdapter bluetoothDeviceArrayAdapter = (BluetoothDeviceArrayAdapter) mDeviceSpinner.getAdapter();

        for (int i = 0; i < bluetoothDeviceArrayAdapter.getCount(); i++) {
            BluetoothDeviceView deviceView = bluetoothDeviceArrayAdapter.getItem(i);
            
            if (deviceView.getAddress().equals(device.getAddress())) {
                bluetoothDeviceArrayAdapter.remove(deviceView);
                DoLog.d(TAG, "removed: " + deviceView);
                break;
            }
            
        }
        mDeviceSpinner.setSelection(bluetoothDeviceArrayAdapter.getNullPosition());
        
    }
    
    /**
     * 
     * @param context
     * @param device
     */
    private void addDeviceToPreference(Context context, BluetoothDevice device, SpoofMode spoofMode) {
        
        if (mBluetoothDevicePrefCategory == null) {
            return;
        }
        
        String emulationSummary = (spoofMode == SpoofMode.INVALID) ? 
                context.getResources().getString(R.string.msg_pref_summary_device_emulation_mode_invalid) :
                Settings.getEmulationModeSummary(context, Spoof.intValue(spoofMode)); 
        
        boolean exists = false;
        for (int i = 0; i < mBluetoothDevicePrefCategory.getPreferenceCount(); i++) {
            Preference pref = (Preference) mBluetoothDevicePrefCategory.getPreference(i);
            
            /*
             * Summary has format of <address>/n<Emulation:lorem...>
             * Need to compare only with address.
             */
            String summary = pref.getSummary().toString();
            String[] token = summary.split("\n"); 
            
            if (token[0].equals(device.getAddress())) {
                pref.setTitle(device.getName());
                pref.setSummary(device.getAddress() + "\n" 
                        + String.format(context.getResources().getString(R.string.msg_pref_summary_device_emulation_mode), 
                                emulationSummary));
                exists = true;
                break;
            }
            
        }
        if (!exists) {
            Preference devicePref = new Preference(context);
            devicePref.setTitle(device.getName().equals("") ? device.getAddress() : device.getName());
            devicePref.setSummary(device.getAddress() + "\n" 
                    + String.format(context.getResources().getString(R.string.msg_pref_summary_device_emulation_mode), 
                            emulationSummary));
            
            mBluetoothDevicePrefCategory.addPreference(devicePref);
        }
    }
    
    /**
     * 
     * @param device
     */
    private void addDeviceToArrayAdapter(BluetoothDevice device, SpoofMode spoofMode) {
       
        if (mDeviceSpinner == null) {
            return;
        }

        BluetoothDeviceArrayAdapter bluetoothDeviceArrayAdapter = (BluetoothDeviceArrayAdapter) mDeviceSpinner.getAdapter();
        
        boolean exists = false;
        for (int i = 0; i < bluetoothDeviceArrayAdapter.getCount(); i++) {
            BluetoothDeviceView deviceView = bluetoothDeviceArrayAdapter.getItem(i);
            
            if (device.getAddress().equals(deviceView.getAddress())) {
                deviceView.setBluetoothDevice(device);
                exists = true;
                break;
            }
        }
        
        if (!exists) {
            BluetoothDeviceView deviceView = new BluetoothDeviceView(device, spoofMode);
            bluetoothDeviceArrayAdapter.add(deviceView);
            mDeviceSpinner.setSelection(bluetoothDeviceArrayAdapter.getNullPosition());
        }
    }
    
}

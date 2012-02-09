package andraus.bluetoothhidemu.settings;

import andraus.bluetoothhidemu.util.DoLog;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.preference.Preference;
import android.preference.PreferenceCategory;

/**
 * 
 */
public class BluetoothDeviceStateReceiver extends BroadcastReceiver {
    
    private PreferenceCategory mBluetoothDevicePrefCategory = null;
    

    
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
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        
        if (intent.getAction().equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)) {
            BluetoothDevice device = (BluetoothDevice) intent.getExtras().get(BluetoothDevice.EXTRA_DEVICE);
            int state = intent.getExtras().getInt(BluetoothDevice.EXTRA_BOND_STATE);
            int prevState = intent.getExtras().getInt(BluetoothDevice.EXTRA_PREVIOUS_BOND_STATE);
            
            DoLog.d("BluetoothHidEmu", String.format("device receiver: device = %s | state = %s | prevState = %s", device, state, prevState));
            
            switch (state) {
            case BluetoothDevice.BOND_BONDED:
                addDeviceToPreference(context, device);
                break;
            case BluetoothDevice.BOND_NONE:
                removeDeviceFromPreference(device);
                break;
            }
            
            
        }

    }
    
    private void removeDeviceFromPreference(BluetoothDevice device) {
        
        if (mBluetoothDevicePrefCategory == null) {
            return;
        }
        
        for (int i = 0; i < mBluetoothDevicePrefCategory.getPreferenceCount(); i++) {
            Preference pref = (Preference) mBluetoothDevicePrefCategory.getPreference(i);
            
            if (pref.getSummary().equals(device.getAddress())) {
                mBluetoothDevicePrefCategory.removePreference(pref);
                break;
            }
        }
    }
    
    private void addDeviceToPreference(Context context, BluetoothDevice device) {
        
        if (mBluetoothDevicePrefCategory == null) {
            return;
        }
        
        boolean exists = false;
        for (int i = 0; i < mBluetoothDevicePrefCategory.getPreferenceCount(); i++) {
            Preference pref = (Preference) mBluetoothDevicePrefCategory.getPreference(i);
            
            if (pref.getSummary().equals(device.getAddress())) {
                pref.setTitle(device.getName());
                exists = true;
                break;
            }
            
        }
        if (!exists) {
            Preference devicePref = new Preference(context);
            devicePref.setTitle(device.getName());
            devicePref.setSummary(device.getAddress());
            
            mBluetoothDevicePrefCategory.addPreference(devicePref);
        }
    }

}

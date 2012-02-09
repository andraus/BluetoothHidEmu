package andraus.bluetoothhidemu.settings;

import andraus.bluetoothhidemu.util.DoLog;
import andraus.bluetoothhidemu.view.BluetoothDeviceArrayAdapter;
import andraus.bluetoothhidemu.view.BluetoothDeviceView;
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
    private BluetoothDeviceArrayAdapter mBluetoothDeviceArrayAdapter = null;
    

    
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
    public BluetoothDeviceStateReceiver(BluetoothDeviceArrayAdapter bluetoothDeviceArrayAdapter) {
        super();
        
        mBluetoothDeviceArrayAdapter = bluetoothDeviceArrayAdapter;
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
                addDeviceToArrayAdapter(device);
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
            
            if (pref.getSummary().equals(device.getAddress())) {
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
        
        if (mBluetoothDeviceArrayAdapter == null) {
            return;
        }
        
        for (int i = 0; i < mBluetoothDeviceArrayAdapter.getCount(); i++) {
            BluetoothDeviceView deviceView = mBluetoothDeviceArrayAdapter.getItem(i);
            
            if (deviceView.getAddress().equals(device.getAddress())) {
                mBluetoothDeviceArrayAdapter.remove(deviceView);
                mBluetoothDeviceArrayAdapter.notifyDataSetChanged();
                break;
            }
            
        }
    }
    
    /**
     * 
     * @param context
     * @param device
     */
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
    
    /**
     * 
     * @param device
     */
    private void addDeviceToArrayAdapter(BluetoothDevice device) {
        
        if (mBluetoothDeviceArrayAdapter == null) {
            return;
        }
        
        boolean exists = false;
        for (int i = 0; i < mBluetoothDeviceArrayAdapter.getCount(); i++) {
            BluetoothDeviceView deviceView = mBluetoothDeviceArrayAdapter.getItem(i);
            
            if (deviceView.getBluetoothDevice().getAddress().equals(device.getAddress())) {
                deviceView.setBluetoothDevice(device);
                exists = true;
                break;
            }
        }
        
        if (!exists) {
            mBluetoothDeviceArrayAdapter.add(new BluetoothDeviceView(device));
        }
        mBluetoothDeviceArrayAdapter.notifyDataSetChanged();
    }
    
    /**
     * Setter
     * 
     * @param bluetoothDeviceArrayAdapter
     */
    public void setBluetoothDeviceArrayAdapter(BluetoothDeviceArrayAdapter bluetoothDeviceArrayAdapter) {
        mBluetoothDeviceArrayAdapter = bluetoothDeviceArrayAdapter;
    }

}

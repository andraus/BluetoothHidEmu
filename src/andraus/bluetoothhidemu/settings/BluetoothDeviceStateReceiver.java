package andraus.bluetoothhidemu.settings;

import andraus.bluetoothhidemu.BluetoothHidEmuActivity;
import andraus.bluetoothhidemu.util.DoLog;
import andraus.bluetoothhidemu.view.BluetoothDeviceArrayAdapter;
import andraus.bluetoothhidemu.view.BluetoothDeviceView;
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
        
        BluetoothDeviceArrayAdapter bluetoothDeviceArrayAdapter = (BluetoothDeviceArrayAdapter) mDeviceSpinner.getAdapter();
        if (bluetoothDeviceArrayAdapter == null) {
            return;
        }
        
        for (int i = 0; i < bluetoothDeviceArrayAdapter.getCount(); i++) {
            BluetoothDeviceView deviceView = bluetoothDeviceArrayAdapter.getItem(i);
            
            if (deviceView.getAddress().equals(device.getAddress())) {
                bluetoothDeviceArrayAdapter.remove(deviceView);
                DoLog.d(TAG, "removed: " + deviceView);
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
       
        if (mDeviceSpinner == null) {
            return;
        }

        BluetoothDeviceArrayAdapter bluetoothDeviceArrayAdapter = (BluetoothDeviceArrayAdapter) mDeviceSpinner.getAdapter();
        
        boolean exists = false;
        for (int i = 0; i < bluetoothDeviceArrayAdapter.getCount(); i++) {
            BluetoothDeviceView deviceView = bluetoothDeviceArrayAdapter.getItem(i);
            
            if (deviceView.getBluetoothDevice().getAddress().equals(device.getAddress())) {
                deviceView.setBluetoothDevice(device);
                exists = true;
                break;
            }
        }
        
        if (!exists) {
            BluetoothDeviceView deviceView = new BluetoothDeviceView(device);
            bluetoothDeviceArrayAdapter.add(deviceView);
            mDeviceSpinner.setSelection(bluetoothDeviceArrayAdapter.getPosition(deviceView));
        }
    }
    
}

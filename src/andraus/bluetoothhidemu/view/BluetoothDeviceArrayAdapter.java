package andraus.bluetoothhidemu.view;

import java.util.Set;

import andraus.bluetoothhidemu.R;
import andraus.bluetoothhidemu.settings.Settings;
import andraus.bluetoothhidemu.spoof.Spoof.SpoofMode;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.widget.ArrayAdapter;

/**
 * Custom ArrayAdapter
 *
 */
public class BluetoothDeviceArrayAdapter extends ArrayAdapter<BluetoothDeviceView> {
    
    /**
     * Constructor
     * 
     * Note: ArrayAdapter will be empty. needs to be populate afterwards.
     * 
     * @param context
     */
    public BluetoothDeviceArrayAdapter(Context context) {
        super(context, R.layout.spinner_layout);
        setDropDownViewResource(R.layout.spinner_dropdown_layout);
        setNotifyOnChange(true);
    }
    
    /**
     * Re-populates the adapter. Will clean any previous data.
     * 
     * @param bondedDeviceSet
     */
    public void rePopulate(Set<BluetoothDevice> bondedDeviceSet) {

        if (!isEmpty()) {
            clear();
        }
        
        for (BluetoothDevice device: bondedDeviceSet) {
            BluetoothDeviceView deviceView = new BluetoothDeviceView(device, Settings.getEmulationMode(getContext(), device));
            add(deviceView);
        }
        
        sort(BluetoothDeviceView.getComparator());
        
    }

    /**
     * 
     * @param bluetoothAddress
     * @return
     */
    public int getPositionByAddress(String bluetoothAddress) {
        for (int i = 0; i < getCount(); i++) {
            BluetoothDeviceView deviceView = getItem(i);
            if (deviceView.getAddress().equals(bluetoothAddress)) {
                return i;
            }
        }
        
        return -1;
    }
    
    /**
     * 
     */
    @Override
    public void add(BluetoothDeviceView deviceView) {
        if (deviceView.getSpoofMode() != SpoofMode.INVALID) {
            super.add(deviceView);
        }
    }

}
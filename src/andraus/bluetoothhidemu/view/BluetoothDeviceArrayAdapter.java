package andraus.bluetoothhidemu.view;

import java.util.Set;

import andraus.bluetoothhidemu.R;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.SharedPreferences;
import android.widget.ArrayAdapter;

/**
 * Custom ArrayAdapter
 *
 */
public class BluetoothDeviceArrayAdapter extends ArrayAdapter<BluetoothDeviceView> {
    
    private static final String PREF_DEVICES = "bt_devices";
    
    private SharedPreferences mSharedPref = null;

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
            BluetoothDeviceView deviceView = new BluetoothDeviceView(device);
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
    
    /*
    mSharedPref = context.getSharedPreferences(PREF_DEVICES, Context.MODE_PRIVATE);
    SharedPreferences.Editor editor = mSharedPref.edit();
    editor.putString("uga", "buga");
    editor.apply();
    */
    
    
}
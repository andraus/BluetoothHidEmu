package andraus.bluetoothhidemu.view;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import andraus.bluetoothhidemu.R;
import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;

/**
 * Custom ArrayAdapter
 *
 */
public class BluetoothDeviceArrayAdapter extends ArrayAdapter<BluetoothDeviceView> implements SpinnerAdapter {
    
    // array to store the "raw" string format
    Map<Integer, BluetoothDeviceView> deviceMap = null;
    
    /**
     * Constructor
     * @param context
     * @param strings
     */
    public BluetoothDeviceArrayAdapter(Context context, Set<BluetoothDeviceView> bluetoothDeviceSet) {
        super(context, R.layout.spinner_layout);
        setDropDownViewResource(R.layout.spinner_dropdown_layout);
        
        deviceMap = new HashMap<Integer, BluetoothDeviceView>();
        int i = 0;
        for (BluetoothDeviceView deviceView:bluetoothDeviceSet ) {
            deviceMap.put(Integer.valueOf(i++), deviceView);
        }
        
    }

    @Override
    public int getCount() {
        
        return deviceMap.size();
    }

    /**
     * Return screen-formatted value
     */
    @Override
    public BluetoothDeviceView getItem(int i) {
        return deviceMap.get(Integer.valueOf(i));
    }

    @Override
    public long getItemId(int i) {
        return i;
    }
    
    /**
     * Returns the array position. <b>item</b> must be raw-formatted.
     */
    @Override
    public int getPosition(BluetoothDeviceView item) {
            
        for (int i = 0; i < deviceMap.size(); i++) {
            BluetoothDeviceView deviceView = deviceMap.get(Integer.valueOf(i));
            if (deviceView.equals(item)) {
                return i;
            }
        }
        
        return -1;
    }
    
    public int getPositionByAddress(String bluetoothAddress) {
        for (int i = 0; i < deviceMap.size(); i++) {
            BluetoothDeviceView deviceView = deviceMap.get(Integer.valueOf(i));
            if (deviceView.getAddress().equals(bluetoothAddress)) {
                return i;
            }
        }
        
        return -1;
    }
    
}
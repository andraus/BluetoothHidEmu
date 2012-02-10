package andraus.bluetoothhidemu.view;

import java.util.List;

import andraus.bluetoothhidemu.R;
import android.content.Context;
import android.widget.ArrayAdapter;

/**
 * Custom ArrayAdapter
 *
 */
public class BluetoothDeviceArrayAdapter extends ArrayAdapter<BluetoothDeviceView> {
    
    
    public BluetoothDeviceArrayAdapter(Context context, List<BluetoothDeviceView> objects) {
        super(context, R.layout.spinner_layout, objects);
        setDropDownViewResource(R.layout.spinner_dropdown_layout);
    }

    public int getPositionByAddress(String bluetoothAddress) {
        for (int i = 0; i < getCount(); i++) {
            BluetoothDeviceView deviceView = getItem(i);
            if (deviceView.getAddress().equals(bluetoothAddress)) {
                return i;
            }
        }
        
        return -1;
    }
    
}
package andraus.bluetoothhidemu.view;

import java.util.List;

import andraus.bluetoothhidemu.R;
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
    
    
    public BluetoothDeviceArrayAdapter(Context context, List<BluetoothDeviceView> objects) {
        super(context, R.layout.spinner_layout, objects);
        setDropDownViewResource(R.layout.spinner_dropdown_layout);
        
        mSharedPref = context.getSharedPreferences(PREF_DEVICES, Context.MODE_PRIVATE);
        
        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.putString("uga", "buga");
        editor.apply();
        
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
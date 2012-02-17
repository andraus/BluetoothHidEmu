package andraus.bluetoothhidemu.view;

import java.util.Set;

import andraus.bluetoothhidemu.R;
import andraus.bluetoothhidemu.settings.Settings;
import andraus.bluetoothhidemu.spoof.Spoof.SpoofMode;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

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
        
        //Add "null" element
        add(BluetoothDeviceView.getNullBluetoothDeviceView(getContext().getResources().getString(R.string.msg_device_list_null)));
        
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
     * Returns the position for null element (tipically 0)
     * @return
     */
    public int getNullPosition() {
        for (int i = 0; i < getCount(); i++) {
            BluetoothDeviceView deviceView = getItem(i);
            if (deviceView.isNull()) {
                return i;
            }
        }
        
        throw new IllegalStateException("No null value found!");
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

    /**
     * 
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Set spinner text to "empty" if null element is selected (disconnect option)
        View view = super.getView(position, convertView, parent);
        if (getItem(position).isNull()) {
            ((TextView) view).setText("");
        }
        
        return view;
    }
    
    

}
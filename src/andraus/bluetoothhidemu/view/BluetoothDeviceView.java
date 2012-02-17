package andraus.bluetoothhidemu.view;

import java.util.Comparator;

import andraus.bluetoothhidemu.spoof.Spoof.SpoofMode;
import android.bluetooth.BluetoothDevice;

public class BluetoothDeviceView {
    
    private BluetoothDevice mBluetoothDevice = null;
    private SpoofMode mSpoofMode;
    
    private String mOverridenName = null; 

    /**
     * Returns a "null" BluetoothDevice. Used to add a dummy item to the arraylist
     * 
     * @param name
     * @return
     */
    public static BluetoothDeviceView getNullBluetoothDeviceView(String name) {
        
        BluetoothDeviceView device = new BluetoothDeviceView(name);
        
        return device;
    }

    /**
     * Private constructor to get a dummy null BluetoothDeviceView
     * @param name
     */
    private BluetoothDeviceView (String name) {
        this(null, SpoofMode.HID_GENERIC);
        mOverridenName = name;
    }
    
    /**
     * Note: bluetoothDevice may be a null value. This bean will handle properly such scenario
     * 
     * @param bluetoothDevice
     * @param spoofMode
     */
    public BluetoothDeviceView(BluetoothDevice bluetoothDevice, SpoofMode spoofMode) {
        super();
        mBluetoothDevice = bluetoothDevice;
        mSpoofMode = spoofMode;
    }

    public BluetoothDevice getBluetoothDevice() {
        return mBluetoothDevice;
    }
    
    public SpoofMode getSpoofMode() {
        return mSpoofMode;
    }

    public void setBluetoothDevice(BluetoothDevice bluetoothDevice) {
        mBluetoothDevice = bluetoothDevice;
    }
    
    public String getAddress() {
        return (mBluetoothDevice != null) ? mBluetoothDevice.getAddress() : "";
    }
    
    public String getName() {
        return (mBluetoothDevice != null) ? mBluetoothDevice.getName() : mOverridenName;
    }

    @Override
    public boolean equals(Object o) {
        return mBluetoothDevice != null ? mBluetoothDevice.getAddress().equals(((BluetoothDeviceView)o).getAddress()) : false;
    }

    @Override
    public String toString() {
        if (mBluetoothDevice != null) {
            String name = mBluetoothDevice.getName();
            return name != null && !"".equals(name) ? name : mBluetoothDevice.getAddress();
        } else {
            return mOverridenName;
        }
    }
    
    public static Comparator<BluetoothDeviceView> getComparator() {
        Comparator<BluetoothDeviceView> comparator = new Comparator<BluetoothDeviceView>() {
            public int compare(BluetoothDeviceView device1, BluetoothDeviceView device2) {
                return device1.getName().compareTo(device2.getName());
            }
        };
        return comparator;
    }
    
    /**
     * 
     * @return
     */
    public boolean isNull() {
        return mBluetoothDevice == null;
    }
    
    

}

package andraus.bluetoothhidemu.view;

import java.util.Comparator;

import andraus.bluetoothhidemu.spoof.Spoof.SpoofMode;
import android.bluetooth.BluetoothDevice;

public class BluetoothDeviceView {
    
    private BluetoothDevice mBluetoothDevice = null;
    private SpoofMode mSpoofMode;
    
    /**
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
        return mBluetoothDevice.getAddress();
    }
    
    public String getName() {
        return mBluetoothDevice.getName();
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
            return null;
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
    
    

}

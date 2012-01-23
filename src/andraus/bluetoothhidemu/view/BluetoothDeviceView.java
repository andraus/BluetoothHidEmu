package andraus.bluetoothhidemu.view;

import java.util.Comparator;

import android.bluetooth.BluetoothDevice;

public class BluetoothDeviceView {
    
    private BluetoothDevice mBluetoothDevice = null;
    
    public BluetoothDeviceView(BluetoothDevice mBluetoothDevice) {
        super();
        this.mBluetoothDevice = mBluetoothDevice;
    }

    public BluetoothDevice getBluetoothDevice() {
        return mBluetoothDevice;
    }

    public void setBluetoothDevice(BluetoothDevice mBluetoothDevice) {
        this.mBluetoothDevice = mBluetoothDevice;
    }
    
    public String getAddress() {
        return this.mBluetoothDevice.getAddress();
    }
    
    

    @Override
    public boolean equals(Object o) {
        return mBluetoothDevice != null ? mBluetoothDevice.equals(o) : false;
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
                return device1.getAddress().compareTo(device2.getAddress());
            }
        };
        return comparator;
    }
    
    

}

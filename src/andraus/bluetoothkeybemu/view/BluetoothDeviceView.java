package andraus.bluetoothkeybemu.view;

import android.bluetooth.BluetoothDevice;

public class BluetoothDeviceView {
    
    private BluetoothDevice mBluetoothDevice = null;
    
    
    

    public BluetoothDeviceView(BluetoothDevice mBluetoothDevice) {
        super();
        this.mBluetoothDevice = mBluetoothDevice;
    }

    public BluetoothDevice getmBluetoothDevice() {
        return mBluetoothDevice;
    }

    public void setmBluetoothDevice(BluetoothDevice mBluetoothDevice) {
        this.mBluetoothDevice = mBluetoothDevice;
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
    
    

}

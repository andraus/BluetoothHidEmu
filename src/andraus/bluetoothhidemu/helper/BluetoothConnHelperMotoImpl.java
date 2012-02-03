package andraus.bluetoothhidemu.helper;

import java.io.IOException;

import andraus.bluetoothhidemu.helper.BluetoothConnHelper;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;

public class BluetoothConnHelperMotoImpl extends BluetoothConnHelper {

    BluetoothConnHelperMotoImpl(BluetoothAdapter adapter) {
        super(null, adapter);
    }
    
    /**
     * Returns current bluetooth device class number.
     * 
     * format: 0xaabbcc, where:
     * 0xaa -> service class number
     * 0xbb -> major device number
     * 0xcc -> minor device number
     * 
     * @return
     */

    @Override
    public int getBluetoothDeviceClass() {
       
        return adapter.getAdapterClass();
    }

    /**
     * Spoof the bluetooth device class number. Format of <i>deviceClass</i> follows the same pattern from method
     * <i>getBluetoothDeviceClass</i>.
     * 
     * @param deviceClass
     * @return
     */
    @Override
    public int spoofBluetoothDeviceClass(int deviceClass) {
        mOriginalDeviceClass = getBluetoothDeviceClass();
        
        return adapter.spoofAdapterClass(deviceClass);
    }

    /**
     * Adds a custom SDP record to enable HID emulation over bluetooth.
     * 
     * @return
     */
    @Override
    public int addHidDeviceSdpRecord() {

        return adapter.addHidKeybSdpRecord();
    }

    /**
     * Removes a SDP record identified by <i>handle</i>. 
     * implementation.
     * 
     */
    @Override
    protected void delHidDeviceSdpRecord() {
        adapter.removeServiceRecord(mHidSdpHandle);

    }

    @Override
    public BluetoothSocket connectL2capSocket(BluetoothDevice device, int port, 
            boolean auth, boolean encrypt) throws IOException {

        return device.createl2capSocket(port, auth, encrypt);
    }
    
    /**
     * No set-up necessary in this implementation.
     */
    public boolean setup() {
        return true;
    }
    
}

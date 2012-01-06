package andraus.bluetoothkeybemu.helper;

import java.io.IOException;

import andraus.bluetoothkeybemu.helper.BluetoothConnHelper;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;

public class BluetoothConnHelperMotoImpl extends BluetoothConnHelper {

    BluetoothConnHelperMotoImpl() {
        super(null);
    }
    
    /**
     * Returns current bluetooth device class number.
     * 
     * format: 0xaabbcc, where:
     * 0xaa -> service class number
     * 0xbb -> major device number
     * 0xcc -> minor device number
     * 
     * @param adapter
     * @return
     */

    @Override
    public int getBluetoothDeviceClass(BluetoothAdapter adapter) {
       
        return adapter.getAdapterClass();
    }

    /**
     * Spoof the bluetooth device class number. Format of <i>deviceClass</i> follows the same pattern from method
     * <i>getBluetoothDeviceClass</i>.
     * 
     * @param adapter
     * @param deviceClass
     * @return
     */
    @Override
    public int spoofBluetoothDeviceClass(BluetoothAdapter adapter, int deviceClass) {
        
        return adapter.spoofAdapterClass(deviceClass);
    }

    /**
     * Adds a custom SDP record to enable HID emulation over bluetooth.
     * 
     * @param adapter
     * @return
     */
    @Override
    public int addHidDeviceSdpRecord(BluetoothAdapter adapter) {

        return adapter.addHidKeybSdpRecord();
    }

    /**
     * Removes a SDP record identified by <i>handle</i>. 
     * implementation.
     * 
     * @param adapter
     */
    @Override
    public void delHidDeviceSdpRecord(BluetoothAdapter adapter) {
        // Not implemented - SDP Record will be automatically deleted once the application dies.

    }

    @Override
    public BluetoothSocket connectL2capSocket(BluetoothDevice device, int port, 
            boolean auth, boolean encrypt) throws IOException {

        return device.createl2capSocket(port, auth, encrypt);
    }
    
    /**
     * Clean-up is automatic in this implementation.
     */
    public void cleanup() {
        
    }
    
    /**
     * No set-up necessary in this implementation.
     */
    public boolean setup() {
        return true;
    }
    
}

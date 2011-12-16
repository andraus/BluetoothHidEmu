package andraus.bluetoothkeybemu.helper;

import java.io.IOException;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

public interface BluetoothConnHelperInterface {
    
    /**
     * Returns current bluetooth device class number. Depending on the implementation, a <i>adapter</i> may 
     * or may not be required.
     * 
     * format: 0xaabbcc, where:
     * 0xaa -> service class number
     * 0xbb -> major device number
     * 0xcc -> minor device number
     * 
     * @param adapter
     * @return
     */
    public int getBluetoothDeviceClass(BluetoothAdapter adapter);
 
    /**
     * Spoof the bluetooth device class number. Format of <i>deviceClass</i> follows the same pattern from method
     * <i>getBluetoothDeviceClass</i>. <i>adapter</i> may be required depending on the implementation.
     * 
     * @param adapter
     * @param deviceClass
     * @return
     */
    public int spoofBluetoothDeviceClass(BluetoothAdapter adapter, int deviceClass);
    
    /**
     * Adds a custom SDP record to enable HID emulation over bluetooth. <i>adapter</i> may be required 
     * depending on the implementation. 
     * 
     * @param adapter
     * @return
     */
    public int addHidDeviceSdpRecord(BluetoothAdapter adapter);
    
    /**
     * Removes a SDP record identified by <i>handle</i>. <i>adapter</i> may be required depending on 
     * implementation.
     * 
     * @param adapter
     * @param handle
     */
    public void dellHidDeviceSdpRecord(BluetoothAdapter adapter, int handle);
    
    /**
     * Returns a BluetoothSocket connected using L2CAP protocol.
     * 
     * @param device
     * @param port
     * @param auth
     * @param encrypt
     * @return
     * @throws IOException
     */
    public BluetoothSocket connectL2capSocket(BluetoothDevice device, int port, 
            boolean auth, boolean encrypt) throws IOException;
    
    /**
     * Performs clean-up actions, if needed.
     */
    public void cleanup();
    
    /**
     * Performs set-up actions, if needed.
     * 
     * @return
     */
    public boolean setup();
    
    /**
     * Retrieves get failure message if setup() call failed.
     * @return
     */
    public String getSetupErrorMsg();

}

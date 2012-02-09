package andraus.bluetoothhidemu.spoof;

import java.io.IOException;

import andraus.bluetoothhidemu.util.DoLog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;

public class BluetoothAdapterSpooferMoto extends BluetoothAdapterSpoofer {

    private Object mHidSdpHandle;

    BluetoothAdapterSpooferMoto(BluetoothAdapter adapter) {
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
    protected int getBluetoothDeviceClass() {
       
        return mAdapter.getAdapterClass();
    }

    /**
     * Spoof the bluetooth device class number. Format of <i>deviceClass</i> follows the same pattern from method
     * <i>getBluetoothDeviceClass</i>.
     * 
     * @param deviceClass
     * @return
     */
    @Override
    protected int spoofBluetoothDeviceClass(int deviceClass) {
        mOriginalDeviceClass = getBluetoothDeviceClass();
        
        return mAdapter.spoofAdapterClass(deviceClass);
    }

    /**
     * Adds a custom SDP record to enable HID emulation over bluetooth.
     * 
     * @return
     */
    @Override
    protected int addHidDeviceSdpRecord(SpoofMode mode) {
        
        if (mHidSdpHandle != 0) {
            DoLog.w(TAG, String.format("HID SDP record already present. Handle: 0x%06X",mHidSdpHandle));
            return mHidSdpHandle;
        }
        
        mHidSdpHandle = (mode == SpoofMode.HID_GENERIC) ? mAdapter.addHidKeybSdpRecord() : mAdapter.addHidBdRemoteRecord();

        return mHidSdpHandle;
    }

    /**
     * Removes a SDP record identified by <i>handle</i>. 
     * implementation.
     * 
     */
    @Override
    protected void delHidDeviceSdpRecord() {
        if (mHidSdpHandle == 0) {
            DoLog.w(TAG, "No HID SDP record handle present.");
            return;
        }
        
        mAdapter.removeServiceRecord(mHidSdpHandle);
        
        mHidSdpHandle = 0;

    }

    @Override
    public BluetoothSocket connectL2capSocket(BluetoothDevice device, int port, 
            boolean auth, boolean encrypt) throws IOException {

        return device.createl2capSocket(port, auth, encrypt);
    }
    
    @Override
    public boolean requirementsCheck() {
        // TODO implement check for frameworks support
        return true;
    }
    
}

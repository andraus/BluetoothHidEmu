package andraus.bluetoothhidemu.spoof;

import java.io.IOException;

import andraus.bluetoothhidemu.BluetoothHidEmuActivity;
import andraus.bluetoothhidemu.util.DoLog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;

/**
 * Abstraction of a class that will implement the necessary spoofing for the bluetooth adapter.
 */
public abstract class BluetoothAdapterSpoofer {
    
    protected static final String TAG = BluetoothHidEmuActivity.TAG;

    public static enum SpoofMode { HID_GENERIC, HID_BDREMOTE };
    
    protected static final int DEV_CLASS_HID_GENERIC = 0x002540;
    protected static final int DEV_CLASS_HID_BDREMOTE = 0x000140;
    
    protected boolean mSpoofed = false;
    protected String mSetupErrorMsg;
    protected Context mContext;
    protected BluetoothAdapter mAdapter;
    protected int mOriginalDeviceClass;

    protected BluetoothAdapterSpoofer(Context appContext, BluetoothAdapter adapter) {
        mContext  = appContext;
        mAdapter = adapter;
    }

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
    public abstract BluetoothSocket connectL2capSocket(BluetoothDevice device, int port, 
            boolean auth, boolean encrypt) throws IOException;
    
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
     protected abstract int getBluetoothDeviceClass();
     
     /**
      * Spoof the bluetooth adapter device class
      * 
      * @param deviceClass
      * @return 0 if success
      */
     protected abstract int spoofBluetoothDeviceClass(int deviceClass);
     
     /**
      * Add the SDP record for the selected spoofing mode.
      * 
      * @param mode
      * @return handle for the SDP record entry.
      */
     protected abstract int addHidDeviceSdpRecord(SpoofMode mode);
    
     /**
      * Removes the SDP record of mHidSdpHandle
      */
     protected abstract void delHidDeviceSdpRecord();
    
    /**
     * Performs tear-up actions.
     * 
     * @param mode
     */
    public void tearUpSpoofing(SpoofMode mode) throws IllegalStateException {
        
        if (mSpoofed) {
            throw new IllegalStateException("Bluetooth device already spoofed");
        }
        
        DoLog.d(TAG, "original class = 0x" + Integer.toHexString(getBluetoothDeviceClass()));

        int newClass = (mode == SpoofMode.HID_GENERIC) ? DEV_CLASS_HID_GENERIC : DEV_CLASS_HID_BDREMOTE;
        int err = spoofBluetoothDeviceClass(newClass);
        DoLog.d(TAG, "set class ret = " + err);

        int sdpRecHandle = addHidDeviceSdpRecord(mode);
        
        DoLog.d(TAG, "SDP record handle = " + Integer.toHexString(sdpRecHandle));
        mSpoofed = true;
    }
    
    /**
     * Performs tear-down actions and necessary clean-up actions.
     */
    public void tearDownSpoofing() throws IllegalStateException {
        if (!mSpoofed) {
            throw new IllegalStateException("Bluetooth device not spoofed");
        }
        
        if (mOriginalDeviceClass != 0) {
            spoofBluetoothDeviceClass(mOriginalDeviceClass);
        }
        
        delHidDeviceSdpRecord();
    }
    
    /**
     * 
     * @return
     */
    public boolean isSpoofed() {
        return mSpoofed;
    }
    
    /**
     * Perform a requirements check for necessary for the underlying implementation to work, like
     * being root, specific vendor, etc.
     * 
     * @return
     */
    public abstract boolean requirementsCheck();
    
    /**
     * Retrieves get failure message if setup() call failed.
     * @return
     */
    public String getSetupErrorMsg() {
        
        return mSetupErrorMsg;
        
    }
    
}

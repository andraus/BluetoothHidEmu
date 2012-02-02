package andraus.bluetoothhidemu.helper;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import andraus.bluetoothhidemu.BluetoothHidEmuActivity;
import andraus.bluetoothhidemu.util.DoLog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

/**
 * Same as BluetoothConnHelperMotoImpl, except that it calls framework functions 
 * through Reflection
 */
public class BluetoothConnHelperMotoReflectImpl extends BluetoothConnHelperGenericImpl {
    
    private static final String TAG = BluetoothHidEmuActivity.TAG;

    BluetoothConnHelperMotoReflectImpl() {
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
        Integer devClass = 0;
        try {
            Method getAdapterClassMethod = BluetoothAdapter.class.getMethod("getAdapterClass", (Class<?>[]) null);
            getAdapterClassMethod.setAccessible(true);
            
            devClass = (Integer) getAdapterClassMethod.invoke(adapter, (Object[]) null);
            
        } catch (SecurityException e) {
            DoLog.e(TAG, "reflection error: ", e);
            throw new IllegalStateException(e);
        } catch (NoSuchMethodException e) {
            DoLog.e(TAG, "reflection error: ", e);
            throw new IllegalStateException(e);
        } catch (IllegalArgumentException e) {
            DoLog.e(TAG, "reflection error: ", e);
            throw new IllegalStateException(e);
        } catch (IllegalAccessException e) {
            DoLog.e(TAG, "reflection error: ", e);
            throw new IllegalStateException(e);
        } catch (InvocationTargetException e) {
            DoLog.e(TAG, "reflection error: ", e);
            throw new IllegalStateException(e);
        }
        
        return Integer.valueOf(devClass);
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
        Integer newClass = 0;
        try {

            Method spoofAdapterClassMethod = BluetoothAdapter.class.getMethod("spoofAdapterClass", new Class<?>[] { int.class });
            spoofAdapterClassMethod.setAccessible(true);
            
            newClass = (Integer) spoofAdapterClassMethod.invoke(adapter, new Object[] { deviceClass });
            
        } catch (SecurityException e) {
            DoLog.e(TAG, "reflection error: ", e);
            throw new IllegalStateException(e);
        } catch (NoSuchMethodException e) {
            DoLog.e(TAG, "reflection error: ", e);
            throw new IllegalStateException(e);
        } catch (IllegalArgumentException e) {
            DoLog.e(TAG, "reflection error: ", e);
            throw new IllegalStateException(e);
        } catch (IllegalAccessException e) {
            DoLog.e(TAG, "reflection error: ", e);
            throw new IllegalStateException(e);
        } catch (InvocationTargetException e) {
            DoLog.e(TAG, "reflection error: ", e);
            throw new IllegalStateException(e);
        }
        
        return Integer.valueOf(newClass);
    }

    /**
     * Adds a custom SDP record to enable HID emulation over bluetooth.
     * 
     * @param adapter
     * @return
     */
    @Override
    public int addHidDeviceSdpRecord(BluetoothAdapter adapter) {
        Integer handle = 0;
        try {
            Method addHidSdpRecordMethod = BluetoothAdapter.class.getMethod("addHidKeybSdpRecord", (Class<?>[]) null);
            addHidSdpRecordMethod.setAccessible(true);
            
            handle = (Integer) addHidSdpRecordMethod.invoke(adapter, (Object[]) null);
            
        } catch (SecurityException e) {
            DoLog.e(TAG, "reflection error: ", e);
            throw new IllegalStateException(e);
        } catch (NoSuchMethodException e) {
            DoLog.e(TAG, "reflection error: ", e);
            throw new IllegalStateException(e);
        } catch (IllegalArgumentException e) {
            DoLog.e(TAG, "reflection error: ", e);
            throw new IllegalStateException(e);
        } catch (IllegalAccessException e) {
            DoLog.e(TAG, "reflection error: ", e);
            throw new IllegalStateException(e);
        } catch (InvocationTargetException e) {
            DoLog.e(TAG, "reflection error: ", e);
            throw new IllegalStateException(e);
        }

        return Integer.valueOf(handle);
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

        return super.connectL2capSocket(device, port, auth, encrypt);
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

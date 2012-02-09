package andraus.bluetoothhidemu.spoof;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import andraus.bluetoothhidemu.BluetoothHidEmuActivity;
import andraus.bluetoothhidemu.util.DoLog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.ParcelUuid;

/**
 * Same as BluetoothAdapterSpooferMotoImpl, except that it calls framework functions 
 * through Reflection
 */
public class BluetoothAdapterSpooferMotoReflect extends BluetoothAdapterSpoofer {
    
    private static final String TAG = BluetoothHidEmuActivity.TAG;
    private Integer mHidSdpHandle;

    /**
     * 
     * @param adapter
     */
    BluetoothAdapterSpooferMotoReflect(BluetoothAdapter adapter) {
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
    protected int getBluetoothDeviceClass() {
        Integer devClass = 0;
        try {
            Method getAdapterClassMethod = BluetoothAdapter.class.getMethod("getAdapterClass", (Class<?>[]) null);
            getAdapterClassMethod.setAccessible(true);
            
            devClass = (Integer) getAdapterClassMethod.invoke(mAdapter, (Object[]) null);
            
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
     * @param deviceClass
     * @return
     */
    protected int spoofBluetoothDeviceClass(int deviceClass) {
        mOriginalDeviceClass = getBluetoothDeviceClass();
        
        Integer newClass = 0;
        try {

            Method spoofAdapterClassMethod = BluetoothAdapter.class.getMethod("spoofAdapterClass", new Class<?>[] { int.class });
            spoofAdapterClassMethod.setAccessible(true);
            
            newClass = (Integer) spoofAdapterClassMethod.invoke(mAdapter, new Object[] { deviceClass });
            
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
     * @return
     */
    public int addHidDeviceSdpRecord(SpoofMode mode) {

        if (mHidSdpHandle != 0) {
            DoLog.w(TAG, String.format("HID SDP record already present. Handle: 0x%06X",mHidSdpHandle));
            return mHidSdpHandle;
        }
        
        Integer handle = 0;
        try {
            // TODO: must finish the frameworks implementation to validate this call
            
            Method addHidSdpRecordMethod = BluetoothAdapter.class.getMethod("addHidKeybSdpRecord", (Class<?>[]) null);
            addHidSdpRecordMethod.setAccessible(true);
            
            handle = (Integer) addHidSdpRecordMethod.invoke(mAdapter, (Object[]) null);
            
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
        
        mHidSdpHandle = Integer.valueOf(handle);

        return mHidSdpHandle;
    }

    /**
     * Removes a SDP record identified by <i>handle</i>. 
     * implementation.
     * 
     * @param adapter
     */
    protected void delHidDeviceSdpRecord() {
        if (mHidSdpHandle == 0) {
            DoLog.w(TAG, "No HID SDP record handle present.");
            return;
        }
        
        try {

            Method removeServiceRecordMethod = BluetoothAdapter.class.getMethod("removeServiceRecord", new Class<?>[] { int.class });
            removeServiceRecordMethod.setAccessible(true);
            
            removeServiceRecordMethod.invoke(mAdapter, new Object[] { mHidSdpHandle });
            
            mHidSdpHandle = 0;
            
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
        
    }

    @Override
    public BluetoothSocket connectL2capSocket(BluetoothDevice device, int port,
            boolean auth, boolean encrypt) throws IOException {
        
        final int TYPE_L2CAP = 3;
        
        BluetoothSocket socket = null;
        Class<?>[] argsClasses = new Class[] { int.class /* type */,
                                            int.class /* fd */,
                                        boolean.class /* auth */,
                                        boolean.class /* encrypt */,
                                BluetoothDevice.class /* device */,
                                            int.class /* port */,
                                     ParcelUuid.class /* uuid */ };
        
        Object[] args = new Object[] {  Integer.valueOf(TYPE_L2CAP),
                                        Integer.valueOf(-1),
                                        Boolean.valueOf(auth),
                                        Boolean.valueOf(encrypt),
                                        device,
                                        Integer.valueOf(port),
                                        null };
        
        try {
            Class<BluetoothSocket> bluetoothSocketClass = BluetoothSocket.class;
            Constructor<BluetoothSocket> bluetoothSocketConstructor;
            bluetoothSocketConstructor = bluetoothSocketClass.getDeclaredConstructor(argsClasses);
            bluetoothSocketConstructor.setAccessible(true);
            
            socket = bluetoothSocketConstructor.newInstance(args);
            
        } catch (SecurityException e) {
            DoLog.e(TAG, "Reflection error:", e);
            throw new IOException("Reflection error", e);
        } catch (NoSuchMethodException e) {
            DoLog.e(TAG, "Reflection error:", e);
            throw new IOException("Reflection error", e);
        } catch (IllegalArgumentException e) {
            DoLog.e(TAG, "Reflection error:", e);
            throw new IOException("Reflection error", e);
        } catch (InstantiationException e) {
            DoLog.e(TAG, "Reflection error:", e);
            throw new IOException("Reflection error", e);
        } catch (IllegalAccessException e) {
            DoLog.e(TAG, "Reflection error:", e);
            throw new IOException("Reflection error", e);
        } catch (InvocationTargetException e) {
            DoLog.e(TAG, "Reflection error:", e);
            throw new IOException("Reflection error", e);
        }
        
        
        return socket;
        
    }

    @Override
    public boolean requirementsCheck() {
        // TODO implement check for frameworks support
        return true;
    }
    
}

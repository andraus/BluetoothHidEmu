package andraus.bluetoothhidemu.spoof.jni;

/**
 * JNI Calls for bluetooth unorthodox calls
 */
public class BluetoothSocketJni {

    /**
     * Creates a l2CAP Bluetooth Socket file descriptor properly configured
     * @param auth
     * @param encrypt
     * @return
     */
    public static native int createL2capFileDescriptor(boolean auth, boolean encrypt);

    /**
     * Read Bluetooth Device class
     * @return
     */
    public static native int[] readBluetoothDeviceClass();

    /**
     * Spoofs the bluetooth device class
     *
     * @param newDeviceClazz
     * @return
     */
    public static native int spoofBluetoothDeviceClass(String newDeviceClazz);

    static {
        System.loadLibrary("bluetoothsocket");
    }
}

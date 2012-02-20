package andraus.bluetoothhidemu.view;

import java.util.Comparator;

import andraus.bluetoothhidemu.spoof.Spoof.SpoofMode;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;

public class BluetoothDeviceView {
    
    private BluetoothDevice mBluetoothDevice = null;
    private SpoofMode mSpoofMode;
    
    private String mOverridenName = null; 

    /**
     * Returns a "null" BluetoothDevice. Used to add a dummy item to the arraylist
     * 
     * @param name
     * @return
     */
    public static BluetoothDeviceView getNullBluetoothDeviceView(String name) {
        
        BluetoothDeviceView device = new BluetoothDeviceView(name);
        
        return device;
    }

    /**
     * Private constructor to get a dummy null BluetoothDeviceView
     * @param name
     */
    private BluetoothDeviceView (String name) {
        this(null, SpoofMode.HID_GENERIC);
        mOverridenName = name;
    }
    
    /**
     * Note: bluetoothDevice may be a null value. This bean will handle properly such scenario
     * 
     * @param bluetoothDevice
     * @param spoofMode
     */
    public BluetoothDeviceView(BluetoothDevice bluetoothDevice, SpoofMode spoofMode) {
        super();
        mBluetoothDevice = bluetoothDevice;
        mSpoofMode = spoofMode;
    }

    public BluetoothDevice getBluetoothDevice() {
        return mBluetoothDevice;
    }
    
    public SpoofMode getSpoofMode() {
        return mSpoofMode;
    }

    public void setBluetoothDevice(BluetoothDevice bluetoothDevice) {
        mBluetoothDevice = bluetoothDevice;
    }
    
    public String getAddress() {
        return (mBluetoothDevice != null) ? mBluetoothDevice.getAddress() : "";
    }
    
    public String getName() {
        return (mBluetoothDevice != null) ? mBluetoothDevice.getName() : mOverridenName;
    }

    @Override
    public boolean equals(Object o) {
        return mBluetoothDevice != null ? mBluetoothDevice.getAddress().equals(((BluetoothDeviceView)o).getAddress()) : false;
    }

    @Override
    public String toString() {
        if (mBluetoothDevice != null) {
            String name = mBluetoothDevice.getName();
            return name != null && !"".equals(name) ? name : mBluetoothDevice.getAddress();
        } else {
            return mOverridenName;
        }
    }
    
    
    /**
     * comparator
     * 
     * @return
     */
    public static Comparator<BluetoothDeviceView> getComparator() {
        Comparator<BluetoothDeviceView> comparator = new Comparator<BluetoothDeviceView>() {
            public int compare(BluetoothDeviceView device1, BluetoothDeviceView device2) {
            	
            	String s1 = device1.isNull() ? "" : device1.getName();
            	String s2 = device2.isNull() ? "" : device2.getName();
            	
                return s1.compareTo(s2);
            }
        };
        return comparator;
    }
    
    /**
     * 
     * @return
     */
    public boolean isNull() {
        return mBluetoothDevice == null;
    }
 
    private static final int PS3_MAJOR_MINOR = 0x0108;
    private static final int[] PS3_SVC_LIST = { BluetoothClass.Service.RENDER,
    											BluetoothClass.Service.CAPTURE,
    											BluetoothClass.Service.AUDIO };
    /**
     * Checks wether the specified device is a PS3 host. Weirdly enough, the PS3 reports 
     * a device class of 0x2c0108:
     *
     * Which means:
     *    - service (0x2c): Rendering | Capturing | Audio
     *    - major # (0x01): Computer
     *    - minor # (0x08): Server-class computer 
     * 
     * This setup seems rather generic for the ps3, however it will have to do for now. 
     * Another option, would be to check the bluetooth MAC prefix, which identifies the
     * adapter vendor. CECHGxx units seems to be built with Alps Co. adapters (00:1B:FB)
     * But to check it now could refrain this to work on other unchecked units.
     * 
     * So, currently we will only check for service/major/minor.
     * 
     * @param device
     * @return
     */
    public static boolean isBluetoothDevicePs3(BluetoothDevice device) {
    	
    	if (device == null) {
    		return false;
    	}
    	
    	BluetoothClass bluetoothClass = device.getBluetoothClass();
    	
    	boolean isPs3 = PS3_MAJOR_MINOR == bluetoothClass.getDeviceClass();
    	
    	int i = 0;
    	while (isPs3 && i < PS3_SVC_LIST.length) {
    		isPs3 = isPs3 && bluetoothClass.hasService(PS3_SVC_LIST[i]);
    		i++;
    	}
    	
    	return isPs3;
    }

    /**
     * Checks wether if the current device is a PS3
     * @return
     */
    public boolean isPs3() {
    	return isBluetoothDevicePs3(mBluetoothDevice);
    }

}

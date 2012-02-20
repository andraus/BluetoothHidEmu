package andraus.bluetoothhidemu.spoof;

public final class Spoof {
    public static enum SpoofMode { INVALID, HID_GENERIC, HID_PS3KEYPAD, HID_BDREMOTE }
    
    private static final int DEV_CLASS_HID_GENERIC = 0x002540;
    private static final int DEV_CLASS_HID_BDREMOTE = 0x000140;

    /**
     * Must match "emulation_mode_names" / "emulation_mode_values" in arrays.xml
     * @param mode
     * @return
     */
    public static int intValue(SpoofMode mode) {
        
        switch (mode) {
        case INVALID: return -1;
        case HID_GENERIC: return 0;
        case HID_PS3KEYPAD: return 1;
        case HID_BDREMOTE: return 2;
        default: return errorInvalidMode(mode);
        }
        
    }
    
    /**
     * 
     * @param i
     * @return
     */
    public static SpoofMode fromInt(int i) {
        
        for (SpoofMode mode : SpoofMode.values()) {
            if (intValue(mode) == i) {
                return mode;
            }
        }
        
        return null;
    }
    
    /**
     * Returns device class in format 0xAABBCC for the corresponding spoof mode.
     * 
     * @param mode
     * @return
     */
    public static int getBluetoothDeviceClass(SpoofMode mode) {
        
        switch (mode) {
        case HID_GENERIC: return DEV_CLASS_HID_GENERIC;
        case HID_BDREMOTE: return DEV_CLASS_HID_BDREMOTE;
        case HID_PS3KEYPAD: return DEV_CLASS_HID_GENERIC; // same as generic
        default: return errorInvalidMode(mode);
        }
        
    }
    
    private static int errorInvalidMode(SpoofMode mode) {
        throw new IllegalStateException("Invalid spoof mode: " + mode);
    }
}
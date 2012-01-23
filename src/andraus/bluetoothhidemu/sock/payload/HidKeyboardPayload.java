package andraus.bluetoothhidemu.sock.payload;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import andraus.bluetoothhidemu.BluetoothHidEmuActivity;
import andraus.bluetoothhidemu.util.DoLog;

/**
 * Regular keyboard payload. 
 */
public class HidKeyboardPayload extends HidPayload {
	
    private static final String TAG = HidPayload.TAG;

    
    private static final int SIZE = 10;
    
    private static final int MODF_NULL = 0x00;
    private static final int MODF_SHIFT = 0x02;
    private static final int REPORT_ID_KEYBOARD = 0x01;
    
    // Static map for keys
    private static final Map<Character, HidKeyPair> KEY_HID_MAP;
    static {
        Map<Character, HidKeyPair>keyHidMap = new HashMap<Character, HidKeyPair>();
        
        keyHidMap.put(Character.valueOf(Character.MIN_VALUE), new HidKeyPair(0x00, MODF_NULL));
        keyHidMap.put('a', new HidKeyPair(0x04, MODF_NULL));
        keyHidMap.put('b', new HidKeyPair(0x05, MODF_NULL));
        keyHidMap.put('c', new HidKeyPair(0x06, MODF_NULL));
        keyHidMap.put('d', new HidKeyPair(0x07, MODF_NULL));
        keyHidMap.put('e', new HidKeyPair(0x08, MODF_NULL));
        keyHidMap.put('f', new HidKeyPair(0x09, MODF_NULL));
        keyHidMap.put('g', new HidKeyPair(0x0a, MODF_NULL));
        keyHidMap.put('h', new HidKeyPair(0x0b, MODF_NULL));
        keyHidMap.put('i', new HidKeyPair(0x0c, MODF_NULL));
        keyHidMap.put('j', new HidKeyPair(0x0d, MODF_NULL));
        keyHidMap.put('k', new HidKeyPair(0x0e, MODF_NULL));
        keyHidMap.put('l', new HidKeyPair(0x0f, MODF_NULL));
        keyHidMap.put('m', new HidKeyPair(0x10, MODF_NULL));
        keyHidMap.put('n', new HidKeyPair(0x11, MODF_NULL));
        keyHidMap.put('o', new HidKeyPair(0x12, MODF_NULL));
        keyHidMap.put('p', new HidKeyPair(0x13, MODF_NULL));
        keyHidMap.put('q', new HidKeyPair(0x14, MODF_NULL));
        keyHidMap.put('r', new HidKeyPair(0x15, MODF_NULL));
        keyHidMap.put('s', new HidKeyPair(0x16, MODF_NULL));
        keyHidMap.put('t', new HidKeyPair(0x17, MODF_NULL));
        keyHidMap.put('u', new HidKeyPair(0x18, MODF_NULL));
        keyHidMap.put('v', new HidKeyPair(0x19, MODF_NULL));
        keyHidMap.put('w', new HidKeyPair(0x1a, MODF_NULL));
        keyHidMap.put('x', new HidKeyPair(0x1b, MODF_NULL));
        keyHidMap.put('y', new HidKeyPair(0x1c, MODF_NULL));
        keyHidMap.put('z', new HidKeyPair(0x1d, MODF_NULL));
        
        keyHidMap.put('1', new HidKeyPair(0x1e, MODF_NULL));
        keyHidMap.put('!', new HidKeyPair(0x1e, MODF_SHIFT));
        keyHidMap.put('2', new HidKeyPair(0x1f, MODF_NULL));
        keyHidMap.put('@', new HidKeyPair(0x1f, MODF_SHIFT));
        keyHidMap.put('3', new HidKeyPair(0x20, MODF_NULL));
        keyHidMap.put('#', new HidKeyPair(0x20, MODF_SHIFT));
        keyHidMap.put('4', new HidKeyPair(0x21, MODF_NULL));
        keyHidMap.put('$', new HidKeyPair(0x21, MODF_SHIFT));
        keyHidMap.put('5', new HidKeyPair(0x22, MODF_NULL));
        keyHidMap.put('%', new HidKeyPair(0x22, MODF_SHIFT));
        keyHidMap.put('6', new HidKeyPair(0x23, MODF_NULL));
        keyHidMap.put('^', new HidKeyPair(0x23, MODF_SHIFT));
        keyHidMap.put('7', new HidKeyPair(0x24, MODF_NULL));
        keyHidMap.put('&', new HidKeyPair(0x24, MODF_SHIFT));
        keyHidMap.put('8', new HidKeyPair(0x25, MODF_NULL));
        keyHidMap.put('*', new HidKeyPair(0x25, MODF_SHIFT));
        keyHidMap.put('9', new HidKeyPair(0x26, MODF_NULL));
        keyHidMap.put('(', new HidKeyPair(0x26, MODF_SHIFT));
        keyHidMap.put('0', new HidKeyPair(0x27, MODF_NULL));
        keyHidMap.put(')', new HidKeyPair(0x27, MODF_SHIFT));
        
        keyHidMap.put(HidKeyPair.ENTER, new HidKeyPair(0x28, MODF_NULL));
        keyHidMap.put(HidKeyPair.DEL, new HidKeyPair(0x2a, MODF_NULL));
        keyHidMap.put(' ', new HidKeyPair(0x2c, MODF_NULL));
        
        keyHidMap.put('-', new HidKeyPair(0x2d, MODF_NULL));
        keyHidMap.put('_', new HidKeyPair(0x2d, MODF_SHIFT));
        keyHidMap.put('=', new HidKeyPair(0x2e, MODF_NULL));
        keyHidMap.put('+', new HidKeyPair(0x2e, MODF_SHIFT));
        keyHidMap.put('[', new HidKeyPair(0x2f, MODF_NULL));
        keyHidMap.put('{', new HidKeyPair(0x2f, MODF_SHIFT));
        keyHidMap.put(']', new HidKeyPair(0x30, MODF_NULL));
        keyHidMap.put('}', new HidKeyPair(0x30, MODF_SHIFT));
        
        keyHidMap.put(HidKeyPair.INV_BACKSLASH, new HidKeyPair(0x31, MODF_NULL));
        keyHidMap.put('|', new HidKeyPair(0x31, MODF_SHIFT));
        
        keyHidMap.put(';', new HidKeyPair(0x33, MODF_NULL));
        keyHidMap.put(':', new HidKeyPair(0x33, MODF_SHIFT));
        keyHidMap.put(HidKeyPair.QUOTE, new HidKeyPair(0x34, MODF_NULL));
        keyHidMap.put(HidKeyPair.DB_QUOTE, new HidKeyPair(0x34, MODF_SHIFT));
        keyHidMap.put('`', new HidKeyPair(0x35, MODF_NULL));
        keyHidMap.put('~', new HidKeyPair(0x35, MODF_SHIFT));
        keyHidMap.put(',', new HidKeyPair(0x36, MODF_NULL));
        keyHidMap.put('<', new HidKeyPair(0x36, MODF_SHIFT));
        keyHidMap.put('.', new HidKeyPair(0x37, MODF_NULL));
        keyHidMap.put('>', new HidKeyPair(0x37, MODF_SHIFT));
        keyHidMap.put('/', new HidKeyPair(0x38, MODF_NULL));
        keyHidMap.put('?', new HidKeyPair(0x38, MODF_SHIFT));


        /*
        
        keyHidMap.put(KeyEvent.KEYCODE_DPAD_RIGHT, 0x4f);
        keyHidMap.put(KeyEvent.KEYCODE_DPAD_LEFT, 0x50);
        keyHidMap.put(KeyEvent.KEYCODE_DPAD_UP, 0x52);
        keyHidMap.put(KeyEvent.KEYCODE_DPAD_DOWN, 0x51);
        */
        
        KEY_HID_MAP = Collections.unmodifiableMap(keyHidMap);
    }

    /**
     * 
     * @param size
     */
    public HidKeyboardPayload() {
        super(SIZE);
        
        mPayload[0] = (byte)0xa1;
        mPayload[1] = (byte)REPORT_ID_KEYBOARD;    // report_id (keyboard)
        mPayload[2] = (byte)0x00;                  // modifier
        mPayload[3] = (byte)0x00;                  // reserved
        mPayload[4] = (byte)0x00;                  // keycode
        mPayload[5] = (byte)0x00;                  // keycode
        mPayload[6] = (byte)0x00;                  // keycode
        mPayload[7] = (byte)0x00;                  // keycode
        mPayload[8] = (byte)0x00;                  // keycode
        mPayload[9] = (byte)0x00;                  // keycode
        
    }
    
    /**
     * 
     * @param value
     */
    public void setModifier(int value) {
        setByte(0x02, value);
    }
    
    /**
     * 
     * @param value
     */
    public void setKeycode(int value) {
        setByte(0x04, value);
    }
    
    /**
     * 
     */
    public void resetBytes() {
        setModifier(0x00);
        setKeycode(0x00);
    }
    
    /**
     * Assemble a HID payload byte array for specified character
     * @param keyCode - Android framework keycode
     * 
     */
    public byte[] assemblePayload(char character) {
        
        
        // logic to handle uppercase letters. this avoids duplicating entries in KEY_HID_MAP for uppercase letters.
        int overrideModifier = -1;
        if (Character.isUpperCase(character)) {
            character = Character.toLowerCase(character);
            overrideModifier = MODF_SHIFT;
        }
        HidKeyPair hidByteSet = KEY_HID_MAP.get(Character.valueOf(character));
        if (overrideModifier > 0) {
            hidByteSet = new HidKeyPair(hidByteSet.getCode(), overrideModifier);
        }
        
        if (hidByteSet == null) {
            DoLog.w(TAG, "No hid code found for character = " + character);
            return null;
        }
        
        setModifier(hidByteSet.getMod());
        setKeycode(hidByteSet.getCode());
        
        return mPayload;
    }


}

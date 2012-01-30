package andraus.bluetoothhidemu.sock.payload;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import andraus.bluetoothhidemu.util.DoLog;
import android.view.KeyEvent;

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
    private static final Map<Character, HidKeyPair> CHAR_HID_MAP;
    static {
        Map<Character, HidKeyPair>charHidMap = new HashMap<Character, HidKeyPair>();
        
        charHidMap.put(Character.valueOf(Character.MIN_VALUE), new HidKeyPair(0x00, MODF_NULL));
        charHidMap.put('a', new HidKeyPair(0x04, MODF_NULL));
        charHidMap.put('b', new HidKeyPair(0x05, MODF_NULL));
        charHidMap.put('c', new HidKeyPair(0x06, MODF_NULL));
        charHidMap.put('d', new HidKeyPair(0x07, MODF_NULL));
        charHidMap.put('e', new HidKeyPair(0x08, MODF_NULL));
        charHidMap.put('f', new HidKeyPair(0x09, MODF_NULL));
        charHidMap.put('g', new HidKeyPair(0x0a, MODF_NULL));
        charHidMap.put('h', new HidKeyPair(0x0b, MODF_NULL));
        charHidMap.put('i', new HidKeyPair(0x0c, MODF_NULL));
        charHidMap.put('j', new HidKeyPair(0x0d, MODF_NULL));
        charHidMap.put('k', new HidKeyPair(0x0e, MODF_NULL));
        charHidMap.put('l', new HidKeyPair(0x0f, MODF_NULL));
        charHidMap.put('m', new HidKeyPair(0x10, MODF_NULL));
        charHidMap.put('n', new HidKeyPair(0x11, MODF_NULL));
        charHidMap.put('o', new HidKeyPair(0x12, MODF_NULL));
        charHidMap.put('p', new HidKeyPair(0x13, MODF_NULL));
        charHidMap.put('q', new HidKeyPair(0x14, MODF_NULL));
        charHidMap.put('r', new HidKeyPair(0x15, MODF_NULL));
        charHidMap.put('s', new HidKeyPair(0x16, MODF_NULL));
        charHidMap.put('t', new HidKeyPair(0x17, MODF_NULL));
        charHidMap.put('u', new HidKeyPair(0x18, MODF_NULL));
        charHidMap.put('v', new HidKeyPair(0x19, MODF_NULL));
        charHidMap.put('w', new HidKeyPair(0x1a, MODF_NULL));
        charHidMap.put('x', new HidKeyPair(0x1b, MODF_NULL));
        charHidMap.put('y', new HidKeyPair(0x1c, MODF_NULL));
        charHidMap.put('z', new HidKeyPair(0x1d, MODF_NULL));
        
        charHidMap.put('1', new HidKeyPair(0x1e, MODF_NULL));
        charHidMap.put('!', new HidKeyPair(0x1e, MODF_SHIFT));
        charHidMap.put('2', new HidKeyPair(0x1f, MODF_NULL));
        charHidMap.put('@', new HidKeyPair(0x1f, MODF_SHIFT));
        charHidMap.put('3', new HidKeyPair(0x20, MODF_NULL));
        charHidMap.put('#', new HidKeyPair(0x20, MODF_SHIFT));
        charHidMap.put('4', new HidKeyPair(0x21, MODF_NULL));
        charHidMap.put('$', new HidKeyPair(0x21, MODF_SHIFT));
        charHidMap.put('5', new HidKeyPair(0x22, MODF_NULL));
        charHidMap.put('%', new HidKeyPair(0x22, MODF_SHIFT));
        charHidMap.put('6', new HidKeyPair(0x23, MODF_NULL));
        charHidMap.put('^', new HidKeyPair(0x23, MODF_SHIFT));
        charHidMap.put('7', new HidKeyPair(0x24, MODF_NULL));
        charHidMap.put('&', new HidKeyPair(0x24, MODF_SHIFT));
        charHidMap.put('8', new HidKeyPair(0x25, MODF_NULL));
        charHidMap.put('*', new HidKeyPair(0x25, MODF_SHIFT));
        charHidMap.put('9', new HidKeyPair(0x26, MODF_NULL));
        charHidMap.put('(', new HidKeyPair(0x26, MODF_SHIFT));
        charHidMap.put('0', new HidKeyPair(0x27, MODF_NULL));
        charHidMap.put(')', new HidKeyPair(0x27, MODF_SHIFT));
        
        charHidMap.put(HidKeyPair.ENTER, new HidKeyPair(0x28, MODF_NULL));
        charHidMap.put(HidKeyPair.DEL, new HidKeyPair(0x2a, MODF_NULL));
        charHidMap.put(' ', new HidKeyPair(0x2c, MODF_NULL));
        
        charHidMap.put('-', new HidKeyPair(0x2d, MODF_NULL));
        charHidMap.put('_', new HidKeyPair(0x2d, MODF_SHIFT));
        charHidMap.put('=', new HidKeyPair(0x2e, MODF_NULL));
        charHidMap.put('+', new HidKeyPair(0x2e, MODF_SHIFT));
        charHidMap.put('[', new HidKeyPair(0x2f, MODF_NULL));
        charHidMap.put('{', new HidKeyPair(0x2f, MODF_SHIFT));
        charHidMap.put(']', new HidKeyPair(0x30, MODF_NULL));
        charHidMap.put('}', new HidKeyPair(0x30, MODF_SHIFT));
        
        charHidMap.put(HidKeyPair.INV_BACKSLASH, new HidKeyPair(0x31, MODF_NULL));
        charHidMap.put('|', new HidKeyPair(0x31, MODF_SHIFT));
        
        charHidMap.put(';', new HidKeyPair(0x33, MODF_NULL));
        charHidMap.put(':', new HidKeyPair(0x33, MODF_SHIFT));
        charHidMap.put(HidKeyPair.QUOTE, new HidKeyPair(0x34, MODF_NULL));
        charHidMap.put(HidKeyPair.DB_QUOTE, new HidKeyPair(0x34, MODF_SHIFT));
        charHidMap.put('`', new HidKeyPair(0x35, MODF_NULL));
        charHidMap.put('~', new HidKeyPair(0x35, MODF_SHIFT));
        charHidMap.put(',', new HidKeyPair(0x36, MODF_NULL));
        charHidMap.put('<', new HidKeyPair(0x36, MODF_SHIFT));
        charHidMap.put('.', new HidKeyPair(0x37, MODF_NULL));
        charHidMap.put('>', new HidKeyPair(0x37, MODF_SHIFT));
        charHidMap.put('/', new HidKeyPair(0x38, MODF_NULL));
        charHidMap.put('?', new HidKeyPair(0x38, MODF_SHIFT));

        CHAR_HID_MAP = Collections.unmodifiableMap(charHidMap);
    }
    
    private static final Map<Integer, HidKeyPair> KEYCODE_HID_MAP;
    static {
        Map<Integer, HidKeyPair>keyCodeHidMap = new HashMap<Integer, HidKeyPair>();
        
        keyCodeHidMap.put(Integer.valueOf(0), new HidKeyPair(0x00, MODF_NULL));

        keyCodeHidMap.put(Integer.valueOf(KeyEvent.KEYCODE_DPAD_RIGHT), new HidKeyPair(0x4f, MODF_NULL));
        keyCodeHidMap.put(Integer.valueOf(KeyEvent.KEYCODE_DPAD_LEFT), new HidKeyPair(0x50, MODF_NULL));
        keyCodeHidMap.put(Integer.valueOf(KeyEvent.KEYCODE_DPAD_UP), new HidKeyPair(0x52, MODF_NULL));
        keyCodeHidMap.put(Integer.valueOf(KeyEvent.KEYCODE_DPAD_DOWN), new HidKeyPair(0x51, MODF_NULL));
        
        keyCodeHidMap.put(Integer.valueOf(KeyEvent.KEYCODE_ENTER), new HidKeyPair(0x28, MODF_NULL));
        keyCodeHidMap.put(Integer.valueOf(KeyEvent.KEYCODE_BACK), new HidKeyPair(0x29, MODF_NULL));
        
        KEYCODE_HID_MAP = Collections.unmodifiableMap(keyCodeHidMap);
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
        setByte(2, value);
    }
    
    /**
     * 
     * @param value
     */
    public void setKeycode(int value) {
        setByte(4, value);
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
    public void assemblePayload(char character) {
        
        resetBytes();
        // logic to handle uppercase letters. this avoids duplicating entries in CHAR_HID_MAP for uppercase letters.
        int overrideModifier = -1;
        if (Character.isUpperCase(character)) {
            character = Character.toLowerCase(character);
            overrideModifier = MODF_SHIFT;
        }
        HidKeyPair hidByteSet = CHAR_HID_MAP.get(Character.valueOf(character));
        if (overrideModifier > 0) {
            hidByteSet = new HidKeyPair(hidByteSet.getCode(), overrideModifier);
        }
        
        if (hidByteSet == null) {
            DoLog.w(TAG, "No hid code found for character = " + character);
        } else {
        	setModifier(hidByteSet.getMod());
        	setKeycode(hidByteSet.getCode());
        }

    }
    
    /**
     * Assemble a HID payload byte array for specified keyCode (see KeyEvent)
     * @param keyCode
     */
    public void assemblePayload(int keyCode) {
        resetBytes();
        
        HidKeyPair hidByteSet = KEYCODE_HID_MAP.get(Integer.valueOf(keyCode));
        if (hidByteSet == null) {
            DoLog.w(TAG, "Nod hid code found for keyCode = " + keyCode);
        } else {
            setModifier(hidByteSet.getMod());
            setKeycode(hidByteSet.getCode());
        }
        
    }


}

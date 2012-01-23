package andraus.bluetoothhidemu.sock;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import andraus.bluetoothhidemu.BluetoothHidEmuActivity;
import andraus.bluetoothhidemu.sock.payload.HidKeyPair;
import andraus.bluetoothhidemu.util.DoLog;

public class HidProtocolManager {
    
    // Singleton instance
    private static final HidProtocolManager mInstance = new HidProtocolManager();
    
    private static final String TAG = BluetoothHidEmuActivity.TAG;
    
    private static final int PROTO_KEYBOARD = 0x01;
    private static final int PROTO_MOUSE = 0x02;
    
    private static final int MODF_NULL = 0x00;
    private static final int MODF_SHIFT = 0x02;
    
    public static final int MAX_POINTER_MOVE = 0x7f;
    
    public static final int MOUSE_BUTTON_NONE = 0x00;
    public static final int MOUSE_BUTTON_1 = 0x01;
    public static final int MOUSE_BUTTON_2 = 0x02;
    
    private int mMouseButtonState = 0x00;

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
     * @return
     */
    public static HidProtocolManager getInstance() {
        return mInstance;
    }

    /**
     * private constructor - singleton
     */
    private HidProtocolManager() {
        super();
    }

    /**
     * Assemble a HID payload byte array for specified character
     * @param keyCode - Android framework keycode
     * @return
     */
    public byte[] payloadKeyb(char character) {
        
        
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
        
        byte[] bytes = new byte[10];
        
        bytes[0] = (byte)0xa1;
        bytes[1] = (byte)PROTO_KEYBOARD;        // report_id (keyboard)
        bytes[2] = (byte)hidByteSet.getMod();   // modifier
        bytes[3] = (byte)0x00;                  // reserved
        bytes[4] = (byte)hidByteSet.getCode();  // keycode
        bytes[5] = (byte)0x00;                  // keycode
        bytes[6] = (byte)0x00;                  // keycode
        bytes[7] = (byte)0x00;                  // keycode
        bytes[8] = (byte)0x00;                  // keycode
        bytes[9] = (byte)0x00;                  // keycode
        
        return bytes;
    }
    
    public byte[] payloadMouseMove(int x, int y) {
        
        byte[] bytes = new byte[6];

        bytes[0] = (byte)0xa1;
        bytes[1] = (byte)PROTO_MOUSE;       // report_id (mouse)
        bytes[2] = (byte)mMouseButtonState; // button
        bytes[3] = (byte)x;
        bytes[4] = (byte)y;
        bytes[5] = (byte)0x00;              // wheel
        
        return bytes;
    }
    
    public byte[] payloadMouseButton(int button) {
        
        mMouseButtonState = button;
        
        byte[] bytes = new byte[6];

        bytes[0] = (byte)0xa1;
        bytes[1] = (byte)PROTO_MOUSE;       // report_id (mouse)
        bytes[2] = (byte)mMouseButtonState; // button
        bytes[3] = (byte)0;                 // x
        bytes[4] = (byte)0;                 // y
        bytes[5] = (byte)0x00;              // wheel
        
        return bytes;
    }
    
    /**
     * TODO: support for PS3 controler
     * 
     * reference: http://wiki.ps2dev.org/ps3:hardware:sixaxis
     * 
     * @param button
     * @return
     */
    public byte[] ps3Button(int button) {
    	return null;
    }

    /**
     * Assemble a HID payload byte array for disconnect request.
     * @return
     */
    public byte[] disconnectReq() {
        byte[] bytes = new byte[10];
        
        bytes[0] = (byte)0xa1;
        bytes[1] = (byte)0x06; // Disconnection Request
        bytes[2] = (byte)0x00;
        bytes[3] = (byte)0x00;
        bytes[4] = (byte)0x00;
        bytes[5] = (byte)0x00;
        bytes[6] = (byte)0x00;
        bytes[7] = (byte)0x00;
        bytes[8] = (byte)0x00;
        bytes[9] = (byte)0x00;
        
        return bytes;
        
    }
    
}

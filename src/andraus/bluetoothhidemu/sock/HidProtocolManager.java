package andraus.bluetoothhidemu.sock;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import andraus.bluetoothhidemu.BluetoothHidEmuActivity;
import andraus.bluetoothhidemu.sock.payload.HidByteSet;
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
    private static final Map<Character, HidByteSet> KEY_HID_MAP;
    static {
        Map<Character, HidByteSet>keyHidMap = new HashMap<Character, HidByteSet>();
        
        keyHidMap.put(Character.valueOf(Character.MIN_VALUE), new HidByteSet(0x00, MODF_NULL));
        keyHidMap.put('a', new HidByteSet(0x04, MODF_NULL));
        keyHidMap.put('b', new HidByteSet(0x05, MODF_NULL));
        keyHidMap.put('c', new HidByteSet(0x06, MODF_NULL));
        keyHidMap.put('d', new HidByteSet(0x07, MODF_NULL));
        keyHidMap.put('e', new HidByteSet(0x08, MODF_NULL));
        keyHidMap.put('f', new HidByteSet(0x09, MODF_NULL));
        keyHidMap.put('g', new HidByteSet(0x0a, MODF_NULL));
        keyHidMap.put('h', new HidByteSet(0x0b, MODF_NULL));
        keyHidMap.put('i', new HidByteSet(0x0c, MODF_NULL));
        keyHidMap.put('j', new HidByteSet(0x0d, MODF_NULL));
        keyHidMap.put('k', new HidByteSet(0x0e, MODF_NULL));
        keyHidMap.put('l', new HidByteSet(0x0f, MODF_NULL));
        keyHidMap.put('m', new HidByteSet(0x10, MODF_NULL));
        keyHidMap.put('n', new HidByteSet(0x11, MODF_NULL));
        keyHidMap.put('o', new HidByteSet(0x12, MODF_NULL));
        keyHidMap.put('p', new HidByteSet(0x13, MODF_NULL));
        keyHidMap.put('q', new HidByteSet(0x14, MODF_NULL));
        keyHidMap.put('r', new HidByteSet(0x15, MODF_NULL));
        keyHidMap.put('s', new HidByteSet(0x16, MODF_NULL));
        keyHidMap.put('t', new HidByteSet(0x17, MODF_NULL));
        keyHidMap.put('u', new HidByteSet(0x18, MODF_NULL));
        keyHidMap.put('v', new HidByteSet(0x19, MODF_NULL));
        keyHidMap.put('w', new HidByteSet(0x1a, MODF_NULL));
        keyHidMap.put('x', new HidByteSet(0x1b, MODF_NULL));
        keyHidMap.put('y', new HidByteSet(0x1c, MODF_NULL));
        keyHidMap.put('z', new HidByteSet(0x1d, MODF_NULL));
        
        keyHidMap.put('1', new HidByteSet(0x1e, MODF_NULL));
        keyHidMap.put('!', new HidByteSet(0x1e, MODF_SHIFT));
        keyHidMap.put('2', new HidByteSet(0x1f, MODF_NULL));
        keyHidMap.put('@', new HidByteSet(0x1f, MODF_SHIFT));
        keyHidMap.put('3', new HidByteSet(0x20, MODF_NULL));
        keyHidMap.put('#', new HidByteSet(0x20, MODF_SHIFT));
        keyHidMap.put('4', new HidByteSet(0x21, MODF_NULL));
        keyHidMap.put('$', new HidByteSet(0x21, MODF_SHIFT));
        keyHidMap.put('5', new HidByteSet(0x22, MODF_NULL));
        keyHidMap.put('%', new HidByteSet(0x22, MODF_SHIFT));
        keyHidMap.put('6', new HidByteSet(0x23, MODF_NULL));
        keyHidMap.put('^', new HidByteSet(0x23, MODF_SHIFT));
        keyHidMap.put('7', new HidByteSet(0x24, MODF_NULL));
        keyHidMap.put('&', new HidByteSet(0x24, MODF_SHIFT));
        keyHidMap.put('8', new HidByteSet(0x25, MODF_NULL));
        keyHidMap.put('*', new HidByteSet(0x25, MODF_SHIFT));
        keyHidMap.put('9', new HidByteSet(0x26, MODF_NULL));
        keyHidMap.put('(', new HidByteSet(0x26, MODF_SHIFT));
        keyHidMap.put('0', new HidByteSet(0x27, MODF_NULL));
        keyHidMap.put(')', new HidByteSet(0x27, MODF_SHIFT));
        
        keyHidMap.put(HidByteSet.ENTER, new HidByteSet(0x28, MODF_NULL));
        keyHidMap.put(HidByteSet.DEL, new HidByteSet(0x2a, MODF_NULL));
        keyHidMap.put(' ', new HidByteSet(0x2c, MODF_NULL));
        
        keyHidMap.put('-', new HidByteSet(0x2d, MODF_NULL));
        keyHidMap.put('_', new HidByteSet(0x2d, MODF_SHIFT));
        keyHidMap.put('=', new HidByteSet(0x2e, MODF_NULL));
        keyHidMap.put('+', new HidByteSet(0x2e, MODF_SHIFT));
        keyHidMap.put('[', new HidByteSet(0x2f, MODF_NULL));
        keyHidMap.put('{', new HidByteSet(0x2f, MODF_SHIFT));
        keyHidMap.put(']', new HidByteSet(0x30, MODF_NULL));
        keyHidMap.put('}', new HidByteSet(0x30, MODF_SHIFT));
        
        keyHidMap.put(HidByteSet.INV_BACKSLASH, new HidByteSet(0x31, MODF_NULL));
        keyHidMap.put('|', new HidByteSet(0x31, MODF_SHIFT));
        
        keyHidMap.put(';', new HidByteSet(0x33, MODF_NULL));
        keyHidMap.put(':', new HidByteSet(0x33, MODF_SHIFT));
        keyHidMap.put(HidByteSet.QUOTE, new HidByteSet(0x34, MODF_NULL));
        keyHidMap.put(HidByteSet.DB_QUOTE, new HidByteSet(0x34, MODF_SHIFT));
        keyHidMap.put('`', new HidByteSet(0x35, MODF_NULL));
        keyHidMap.put('~', new HidByteSet(0x35, MODF_SHIFT));
        keyHidMap.put(',', new HidByteSet(0x36, MODF_NULL));
        keyHidMap.put('<', new HidByteSet(0x36, MODF_SHIFT));
        keyHidMap.put('.', new HidByteSet(0x37, MODF_NULL));
        keyHidMap.put('>', new HidByteSet(0x37, MODF_SHIFT));
        keyHidMap.put('/', new HidByteSet(0x38, MODF_NULL));
        keyHidMap.put('?', new HidByteSet(0x38, MODF_SHIFT));


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
        HidByteSet hidByteSet = KEY_HID_MAP.get(Character.valueOf(character));
        if (overrideModifier > 0) {
            hidByteSet = new HidByteSet(hidByteSet.getCode(), overrideModifier);
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

package andraus.bluetoothkeybemu.sock;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


import andraus.bluetoothkeybemu.BluetoothKeybEmuActivity;
import andraus.bluetoothkeybemu.util.DoLog;
import android.view.KeyEvent;

public class HidProtocolManager {
    
    public enum KeybShiftModifier { NONE, SHIFT_ONCE, SHIFT_ALWAYS };
    
    // Singleton instance
    private static final HidProtocolManager mInstance = new HidProtocolManager();
    
    private static final String TAG = BluetoothKeybEmuActivity.TAG;
    
    private KeybShiftModifier mShiftModifier = KeybShiftModifier.NONE;
    
    public static final int NULL = 0x00;
    
    // Static map for HID modifiers
    private static final Map<KeybShiftModifier, Integer> KEY_HID_MODF_MAP;
    static {
        Map<KeybShiftModifier, Integer>keyHidModifierMap = new HashMap<KeybShiftModifier, Integer>();
        
        keyHidModifierMap.put(KeybShiftModifier.NONE, NULL);
        keyHidModifierMap.put(KeybShiftModifier.SHIFT_ONCE, 0x02);
        keyHidModifierMap.put(KeybShiftModifier.SHIFT_ALWAYS, 0x02);
        
        KEY_HID_MODF_MAP = Collections.unmodifiableMap(keyHidModifierMap);
    }
    
    // Static map for keys
    private static final Map<Integer, Integer> KEY_HID_MAP;
    static {
        Map<Integer, Integer>keyHidMap = new HashMap<Integer, Integer>();
        
        keyHidMap.put(0, 0); // empty code
        keyHidMap.put(KeyEvent.KEYCODE_A, 0x04);
        keyHidMap.put(KeyEvent.KEYCODE_B, 0x05);
        keyHidMap.put(KeyEvent.KEYCODE_C, 0x06);
        keyHidMap.put(KeyEvent.KEYCODE_D, 0x07);
        keyHidMap.put(KeyEvent.KEYCODE_E, 0x08);
        keyHidMap.put(KeyEvent.KEYCODE_F, 0x09);
        keyHidMap.put(KeyEvent.KEYCODE_G, 0x0a);
        keyHidMap.put(KeyEvent.KEYCODE_H, 0x0b);
        keyHidMap.put(KeyEvent.KEYCODE_I, 0x0c);
        keyHidMap.put(KeyEvent.KEYCODE_J, 0x0d);
        keyHidMap.put(KeyEvent.KEYCODE_K, 0x0e);
        keyHidMap.put(KeyEvent.KEYCODE_L, 0x0f);
        keyHidMap.put(KeyEvent.KEYCODE_M, 0x10);
        keyHidMap.put(KeyEvent.KEYCODE_N, 0x11);
        keyHidMap.put(KeyEvent.KEYCODE_O, 0x12);
        keyHidMap.put(KeyEvent.KEYCODE_P, 0x13);
        keyHidMap.put(KeyEvent.KEYCODE_Q, 0x14);
        keyHidMap.put(KeyEvent.KEYCODE_R, 0x15);
        keyHidMap.put(KeyEvent.KEYCODE_S, 0x16);
        keyHidMap.put(KeyEvent.KEYCODE_T, 0x17);
        keyHidMap.put(KeyEvent.KEYCODE_U, 0x18);
        keyHidMap.put(KeyEvent.KEYCODE_V, 0x19);
        keyHidMap.put(KeyEvent.KEYCODE_W, 0x1a);
        keyHidMap.put(KeyEvent.KEYCODE_X, 0x1b);
        keyHidMap.put(KeyEvent.KEYCODE_Y, 0x1c);
        keyHidMap.put(KeyEvent.KEYCODE_Z, 0x1d);

        keyHidMap.put(KeyEvent.KEYCODE_1, 0x1e);
        keyHidMap.put(KeyEvent.KEYCODE_2, 0x1f);
        keyHidMap.put(KeyEvent.KEYCODE_3, 0x20);
        keyHidMap.put(KeyEvent.KEYCODE_4, 0x21);
        keyHidMap.put(KeyEvent.KEYCODE_5, 0x22);
        keyHidMap.put(KeyEvent.KEYCODE_6, 0x23);
        keyHidMap.put(KeyEvent.KEYCODE_7, 0x24);
        keyHidMap.put(KeyEvent.KEYCODE_8, 0x25);
        keyHidMap.put(KeyEvent.KEYCODE_9, 0x26);
        keyHidMap.put(KeyEvent.KEYCODE_0, 0x27);
        
        keyHidMap.put(KeyEvent.KEYCODE_ENTER, 0x28);
        keyHidMap.put(KeyEvent.KEYCODE_DEL, 0x2a);
        keyHidMap.put(KeyEvent.KEYCODE_SPACE, 0x2c);
        keyHidMap.put(KeyEvent.KEYCODE_AT, 0x14);
        keyHidMap.put(KeyEvent.KEYCODE_PERIOD, 0x37);
        keyHidMap.put(KeyEvent.KEYCODE_COMMA, 0x36);
        
        keyHidMap.put(KeyEvent.KEYCODE_DPAD_RIGHT, 0x4f);
        keyHidMap.put(KeyEvent.KEYCODE_DPAD_LEFT, 0x50);
        keyHidMap.put(KeyEvent.KEYCODE_DPAD_UP, 0x52);
        keyHidMap.put(KeyEvent.KEYCODE_DPAD_DOWN, 0x51);
        
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
     * Toggle shift
     */
    public void toggleKeyboardShift() {
        // retrieve the next enum, wrapping to begginning if it's the last one.
        mShiftModifier = KeybShiftModifier.values()[(mShiftModifier.ordinal() + 1) % KeybShiftModifier.values().length];
        
        DoLog.d(TAG, "shift = " + mShiftModifier);
    }
    
    /**
     * Assemble a HID payload byte array for specified character
     * @param keyCode - Android framework keycode
     * @return
     */
    public byte[] payloadKeyb(int keyCode) {
        
        Integer modCode = KEY_HID_MODF_MAP.get(mShiftModifier);
        
        if (mShiftModifier == KeybShiftModifier.SHIFT_ONCE) {
            mShiftModifier = KeybShiftModifier.NONE;
        }
        
        Integer hidCode = KEY_HID_MAP.get(Integer.valueOf(keyCode));
        
        if (hidCode == null) {
            DoLog.w(TAG, "No hid code found for keyCode = " + hidCode);
            return null;
        }
        
        byte[] bytes = new byte[10];
        
        bytes[0] = (byte)0xa1;
        bytes[1] = (byte)0x01;          // report_id (keyboard)
        bytes[2] = modCode.byteValue(); // modifier
        bytes[3] = (byte)0x00;          // reserved
        bytes[4] = hidCode.byteValue(); // keycode
        bytes[5] = (byte)0x00;          // keycode
        bytes[6] = (byte)0x00;          // keycode
        bytes[7] = (byte)0x00;          // keycode
        bytes[8] = (byte)0x00;          // keycode
        bytes[9] = (byte)0x00;          // keycode
        
        return bytes;
    }
    
    public byte[] payloadMouse(int x, int y) {
        byte[] bytes = new byte[6];
        
        bytes[0] = (byte)0xa1;
        bytes[1] = (byte)0x02; // report_id (mouse)
        bytes[2] = (byte)0x00; // button
        bytes[3] = (byte)x;
        bytes[4] = (byte)y;
        bytes[5] = (byte)0x00; // wheel
        
        return bytes;
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

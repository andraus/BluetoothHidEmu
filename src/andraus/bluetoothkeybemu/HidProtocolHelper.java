package andraus.bluetoothkeybemu;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


import andraus.bluetoothkeybemu.util.DoLog;
import android.view.KeyEvent;

public class HidProtocolHelper {
    
    private static final String TAG = BluetoothKeybEmuActivity.TAG;
    
    private static final Map<Integer, Integer> KEY_HID_MAP;
    static {
        Map<Integer, Integer>keyHidMap = new HashMap<Integer, Integer>();
        
        keyHidMap.put(0, 0); // empty code
        keyHidMap.put(KeyEvent.KEYCODE_A,  4);
        keyHidMap.put(KeyEvent.KEYCODE_B,  5);
        keyHidMap.put(KeyEvent.KEYCODE_C,  6);
        keyHidMap.put(KeyEvent.KEYCODE_D,  7);
        keyHidMap.put(KeyEvent.KEYCODE_E,  8);
        keyHidMap.put(KeyEvent.KEYCODE_F,  9);
        keyHidMap.put(KeyEvent.KEYCODE_G, 10);
        keyHidMap.put(KeyEvent.KEYCODE_H, 11);
        keyHidMap.put(KeyEvent.KEYCODE_I, 12);
        keyHidMap.put(KeyEvent.KEYCODE_J, 13);
        keyHidMap.put(KeyEvent.KEYCODE_K, 14);
        keyHidMap.put(KeyEvent.KEYCODE_L, 15);
        keyHidMap.put(KeyEvent.KEYCODE_M, 16);
        keyHidMap.put(KeyEvent.KEYCODE_N, 17);
        keyHidMap.put(KeyEvent.KEYCODE_O, 18);
        keyHidMap.put(KeyEvent.KEYCODE_P, 19);
        keyHidMap.put(KeyEvent.KEYCODE_Q, 20);
        keyHidMap.put(KeyEvent.KEYCODE_R, 21);
        keyHidMap.put(KeyEvent.KEYCODE_S, 22);
        keyHidMap.put(KeyEvent.KEYCODE_T, 23);
        keyHidMap.put(KeyEvent.KEYCODE_U, 24);
        keyHidMap.put(KeyEvent.KEYCODE_V, 25);
        keyHidMap.put(KeyEvent.KEYCODE_W, 26);
        keyHidMap.put(KeyEvent.KEYCODE_X, 27);
        keyHidMap.put(KeyEvent.KEYCODE_Y, 28);
        keyHidMap.put(KeyEvent.KEYCODE_Z, 29);

        keyHidMap.put(KeyEvent.KEYCODE_1, 30);
        keyHidMap.put(KeyEvent.KEYCODE_2, 31);
        keyHidMap.put(KeyEvent.KEYCODE_3, 32);
        keyHidMap.put(KeyEvent.KEYCODE_4, 33);
        keyHidMap.put(KeyEvent.KEYCODE_5, 34);
        keyHidMap.put(KeyEvent.KEYCODE_6, 35);
        keyHidMap.put(KeyEvent.KEYCODE_7, 36);
        keyHidMap.put(KeyEvent.KEYCODE_8, 37);
        keyHidMap.put(KeyEvent.KEYCODE_9, 38);
        keyHidMap.put(KeyEvent.KEYCODE_0, 39);
        
        keyHidMap.put(KeyEvent.KEYCODE_ENTER, 40);
        keyHidMap.put(KeyEvent.KEYCODE_DEL, 42);
        keyHidMap.put(KeyEvent.KEYCODE_SPACE, 44);
        keyHidMap.put(KeyEvent.KEYCODE_AT, 20);
        keyHidMap.put(KeyEvent.KEYCODE_PERIOD, 55);
        keyHidMap.put(KeyEvent.KEYCODE_COMMA, 54);
        
        keyHidMap.put(KeyEvent.KEYCODE_DPAD_RIGHT, 79);
        keyHidMap.put(KeyEvent.KEYCODE_DPAD_LEFT, 80);
        keyHidMap.put(KeyEvent.KEYCODE_DPAD_UP, 82);
        keyHidMap.put(KeyEvent.KEYCODE_DPAD_DOWN, 81);
        
        KEY_HID_MAP = Collections.unmodifiableMap(keyHidMap);
    }
    
    /**
     * Assemble a HID payload byte array for specified character
     * @param keyCode - Android framework keycode
     * @return
     */
    public byte[] payload(int keyCode) {
        
        Integer hidCode = KEY_HID_MAP.get(Integer.valueOf(keyCode));
        
        if (hidCode == null) {
            DoLog.w(TAG, "No hid code found for keyCode = " + hidCode);
            return null;
        }
        
        byte[] bytes = new byte[10];
        
        bytes[0] = (byte)0xa1;
        bytes[1] = (byte)0x01; // input
        bytes[2] = (byte)0x00; //1 -left control ,2 - left shift, 4 left alt,5- ctrl+ alt (01 + 04) 8 - left gui, 16 - right control, 32 - right sift, 64 - right alt, 128 - right gui
        bytes[3] = (byte)0x00;
        bytes[4] = hidCode.byteValue();
        bytes[5] = (byte)0x00;
        bytes[6] = (byte)0x00;
        bytes[7] = (byte)0x00;
        bytes[8] = (byte)0x00;
        bytes[9] = (byte)0x00;
        
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

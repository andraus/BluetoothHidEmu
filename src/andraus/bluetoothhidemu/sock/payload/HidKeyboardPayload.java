package andraus.bluetoothhidemu.sock.payload;

/**
 * Regular keyboard payload. 
 */
public class HidKeyboardPayload extends HidPayload {
    
    private static final int SIZE = 10;
    
    private static final int REPORT_ID_KEYBOARD = 0x01;
    
    private static final int INDEX_MODIFIER = 2;
    private static final int INDEX_KEYCODE = 4;

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
        setByte(INDEX_MODIFIER, value);
    }
    
    /**
     * 
     * @param value
     */
    public void setKeycode(int value) {
        setByte(INDEX_KEYCODE, value);
    }
    
    /**
     * 
     */
    public void resetBytes() {
        setModifier(0x00);
        setKeycode(0x00);
    }

}

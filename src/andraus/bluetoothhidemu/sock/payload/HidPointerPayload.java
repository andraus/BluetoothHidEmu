package andraus.bluetoothhidemu.sock.payload;

/**
 * 
 * @author andraus
 *
 */
public class HidPointerPayload extends HidPayload {
	
	private static final int SIZE = 6;
	private static final int REPORT_ID_POINTER = 0x02;
	
    public static final int MAX_POINTER_MOVE = 0x7f;
    
    public static final int MOUSE_BUTTON_NONE = 0x00;
    public static final int MOUSE_BUTTON_1 = 0x01; // bit 0
    public static final int MOUSE_BUTTON_2 = 0x02; // bit 1
    
	/**
	 * 
	 */
	public HidPointerPayload() {
		super(SIZE);
		
        mPayload[0] = (byte)0xa1;
        mPayload[1] = (byte)REPORT_ID_POINTER; // report_id (mouse)
        mPayload[2] = (byte)0x00; 			   // button
        mPayload[3] = (byte)0x00;			   // x
        mPayload[4] = (byte)0x00;			   // y
        mPayload[5] = (byte)0x00;              // wheel
	}
	
	/**
	 * 
	 * @param x
	 * @param y
	 */
	public void movePointer(int x, int y) {
		setByte(3, x);
		setByte(4, y);
	}
	
	/**
	 * 
	 */
	public void resetBytes() {
		setByte(2, MOUSE_BUTTON_NONE);
		movePointer(0x00, 0x00);
	}
	
	/**
	 * 
	 */
	public void clickButton(int button) {
	    // enable bit
	    setByte(2, mPayload[2] | button);
	}
	
	/**
	 * 
	 */
	public void releaseButton(int button) {
	    // disable bit
	    setByte(2, mPayload[2] ^ button);
	}

}

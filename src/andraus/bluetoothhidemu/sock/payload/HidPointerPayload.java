package andraus.bluetoothhidemu.sock.payload;

/**
 * 
 * @author andraus
 *
 */
public class HidPointerPayload extends HidPayload {
	
	private static final int SIZE = 6;
	private static final int REPORT_ID_POINTER = 0x02;

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
	 * @param button
	 */
	public void setButton(int button) {
		setByte(0x02, button);
	}
	
	/**
	 * 
	 * @param x
	 * @param y
	 */
	public void setCoords(int x, int y) {
		setByte(0x03, x);
		setByte(0x04, y);
	}
	
	/**
	 * 
	 */
	public void resetBytes() {
		setButton(0x00);
		setCoords(0x00, 0x00);
	}

}

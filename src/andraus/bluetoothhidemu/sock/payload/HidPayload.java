package andraus.bluetoothhidemu.sock.payload;

import andraus.bluetoothhidemu.BluetoothHidEmuActivity;

/**
 * Generic payload implementation.
 * 
 * TODO: ref. for PS3 blu-ray remote: http://fedoraproject.org/wiki/Features/PlayStationBDRemote
 * 
 */
public abstract class HidPayload {
	
    protected static final String TAG = BluetoothHidEmuActivity.TAG;

    public static final int REQ_SET_PROTOCOL = 0x70;
    
    protected byte[] mPayload = null;
    
    /**
     * 
     * @param size
     */
    protected HidPayload(int size) {
        mPayload = new byte[size];
    }
    
    /**
     * 
     * @return
     */
    public byte[] getPayload() {
        return mPayload;
    }
    
    /**
     * 
     * @param index
     * @param value
     * @return
     */
    protected boolean setByte(int index, int value) {
        if (index < mPayload.length) {
            mPayload[index] = (byte) value;
        } else {
            return false; 
        }
        
        return true;
    }
    
    /**
     * 
     */
    public abstract void resetBytes();
    
    /**
     * Assemble a HID payload byte array for disconnect request.
     * @return
     */
    public static byte[] disconnectReq() {
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

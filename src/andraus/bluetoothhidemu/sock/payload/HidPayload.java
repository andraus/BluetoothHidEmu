package andraus.bluetoothhidemu.sock.payload;

/**
 * Generic payload implementation.
 */
public abstract class HidPayload {
    
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
    public boolean setByte(int index, int value) {
        if (index < mPayload.length) {
            mPayload[index] = (byte) value;
        } else {
            return false; 
        }
        
        return true;
    }
    

}

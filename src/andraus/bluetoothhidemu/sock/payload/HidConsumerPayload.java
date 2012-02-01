package andraus.bluetoothhidemu.sock.payload;


public class HidConsumerPayload extends HidPayload {
    
    private static final int SIZE = 4;
    private static final int REPORT_ID_CONSUMER = 0x03;
    
    private static final int[] USAGE_NULL = { 0x00, 0x00 };
    public static final int[] USAGE_MEDIA_PLAY = { 0x40, 0x00 };
    
    /**
     * 
     */
    public HidConsumerPayload() {
        super(SIZE);
        
        mPayload[0] = (byte)0xa1;
        mPayload[1] = (byte)REPORT_ID_CONSUMER;         // report_id
        mPayload[2] = (byte)0x00;                       // usage_id (bits 7 - 0)
        mPayload[3] = (byte)0x00;                       // numeric keypad (bits 7 - 4 ) | usage_id (bits 11 - 8)
        
    }
    
    /**
     * 
     * @param usage_low
     * @param usage_high
     */
    public void set(int[] usage) {
        
        //if (usage.length == 2) {
        {
            mPayload[2] = (byte)usage[0];
            mPayload[3] = (byte)usage[1];
        }
    }
    
    /**
     * 
     */
    @Override
    public void resetBytes() {
        set(USAGE_NULL);        
    }

}

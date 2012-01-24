package andraus.bluetoothhidemu.sock.payload;

/**
 * Payload for Sixaxis PS3 controller. 
 * Reference: http://wiki.ps2dev.org/ps3:hardware:sixaxis
 */
public class HidSixaxisPayload extends HidPayload {
    
    private static final int SIZE = 10;
    private static final int REPORT_ID_KEYBOARD = 0x01;
    
    /**
     * 
     */
    public HidSixaxisPayload() {
        super(SIZE);
        
        mPayload[0] = (byte)0xa1;
        mPayload[1] = (byte)REPORT_ID_KEYBOARD;    // report_id (keyboard)
        
        mPayload[2] = (byte)0x00;                  // unknown
        
        mPayload[3] = (byte)0x00;                  // b1 (simple button state)
        mPayload[4] = (byte)0x00;                  // b2 (simple button state)
        mPayload[5] = (byte)0x00;                  // b3 (simple button state)
        
        mPayload[6] = (byte)0x00;                  // unknown
        
        mPayload[7] = (byte)0x00;                  // left analog stick (horizontal axis - left = 0x00, right = 0xff)
        mPayload[8] = (byte)0x00;                  // left analog stick (vertical axis - top = 0x00, bottom = 0xff)
        
        mPayload[9] = (byte)0x00;                  // right analog stick (horizontal axis - left = 0x00, right = 0xff)
        mPayload[10] = (byte)0x00;                 // right analog stick (vertical axis - top = 0x00, bottom = 0xff)
        
        mPayload[11] = (byte)0x00;                 // unknown 
        mPayload[12] = (byte)0x00;                 // unknown
        mPayload[13] = (byte)0x00;                 // unknown
        mPayload[14] = (byte)0x00;                 // unknown
        
        mPayload[15] = (byte)0x00;                 // press. sensitive up nav. pad
        mPayload[16] = (byte)0x00;                 // press. sensitive right nav. pad
        mPayload[17] = (byte)0x00;                 // press. sensitive down nav. pad
        mPayload[18] = (byte)0x00;                 // press. sensitive left nav. pad
        
        mPayload[19] = (byte)0x00;                 // press. sensitive L2
        mPayload[20] = (byte)0x00;                 // press. sensitive R2
        mPayload[21] = (byte)0x00;                 // press. sensitive L1
        mPayload[22] = (byte)0x00;                 // press. sensitive R1
        
        mPayload[23] = (byte)0x00;                 // press. sensitive triangle
        mPayload[24] = (byte)0x00;                 // press. sensitive circle
        mPayload[25] = (byte)0x00;                 // press. sensitive cross
        mPayload[26] = (byte)0x00;                 // press. sensitive square
        
        mPayload[27] = (byte)0x00;                 // unknown
        mPayload[28] = (byte)0x00;                 // unknown
        mPayload[29] = (byte)0x00;                 // unknown
        
        mPayload[30] = (byte)0x03;                 // cable status (normal = 0x03, cable = 0x02)
        
        mPayload[31] = (byte)0x05;                 // battery level
        
        mPayload[32] = (byte)0x14;                 // op. status (bluetooth = 0x14, cable = 0x10; bluetooth + rumble = 0x16)
        
        mPayload[33] = (byte)0xff;                 // unknown (values fluctuate)
        mPayload[34] = (byte)0xb9;                 // unknown (values fluctuate)
        mPayload[35] = (byte)0x00;                 // unknown (values fluctuate)
        mPayload[36] = (byte)0x00;                 // unknown (values fluctuate)
        mPayload[37] = (byte)0x23;                 // unknown (values fluctuate)
        mPayload[38] = (byte)0x16;                 // unknown (values fluctuate)
        mPayload[39] = (byte)0x77;                 // unknown (values fluctuate)
        mPayload[40] = (byte)0x01;                 // unknown (values fluctuate)
        mPayload[41] = (byte)0x81;                 // unknown (values fluctuate)
        
        mPayload[42] = (byte)0x02;                 // accel
        mPayload[43] = (byte)0x08;                 // accel
        mPayload[44] = (byte)0x01;                 // accel
        mPayload[45] = (byte)0xf2;                 // accel
        mPayload[46] = (byte)0x01;                 // accel
        mPayload[47] = (byte)0x93;                 // accel
        
        mPayload[48] = (byte)0x00;                 // Z gyro
        mPayload[49] = (byte)0x02;                 // Z gyro
        
    }

    /**
     * 
     */
    @Override
    public void resetBytes() {
        // TODO Auto-generated method stub
        releaseCircle();
    }
    
    /**
     * 
     */
    public void pressCircle() {
        mPayload[4] = (byte)0x20;
    }
    
    /**
     * 
     */
    public void releaseCircle() {
        mPayload[4] = (byte)0x00;
    }

}

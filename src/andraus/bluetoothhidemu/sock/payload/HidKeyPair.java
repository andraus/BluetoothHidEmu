package andraus.bluetoothhidemu.sock.payload;

/**
 * Class to represent a "hid key" in terms of a (modifier, hid_code) pair.
 */
public class HidKeyPair {
    
    public static final char ENTER = '\n';
    public static final char DEL = '\b';
    public static final char QUOTE ='\'';
    public static final char DB_QUOTE = '\"';
    public static final char INV_BACKSLASH = '\\';
    
    
    private int code;
    private int mod;
    
    public HidKeyPair(int code, int mod) {
        this.code = code;
        this.mod = mod;
    }
    
    public int getCode() {
        return code;
    }
    
    public int getMod() {
        return mod;
    }
}

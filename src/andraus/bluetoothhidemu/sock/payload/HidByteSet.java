package andraus.bluetoothhidemu.sock.payload;

/**
 * 
 */
public class HidByteSet {
    
    public static final char ENTER = '\n';
    public static final char DEL = '\b';
    public static final char QUOTE ='\'';
    public static final char DB_QUOTE = '\"';
    public static final char INV_BACKSLASH = '\\';
    
    
    private int code;
    private int mod;
    
    public HidByteSet(int code, int mod) {
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

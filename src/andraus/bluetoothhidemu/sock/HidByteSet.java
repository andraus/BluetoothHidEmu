package andraus.bluetoothhidemu.sock;

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
    
    HidByteSet(int code, int mod) {
        this.code = code;
        this.mod = mod;
    }
    
    int getCode() {
        return code;
    }
    
    int getMod() {
        return mod;
    }
}

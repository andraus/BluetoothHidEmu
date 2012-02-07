package andraus.bluetoothhidemu.spoof;

public class CleanupExceptionHandler implements Thread.UncaughtExceptionHandler {
    
    private BluetoothAdapterSpoofer mSpoofer = null;
    private Thread.UncaughtExceptionHandler mDefaultExceptionHandler = null ;
    
    public CleanupExceptionHandler(BluetoothAdapterSpoofer connHelper) {
        mDefaultExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
        mSpoofer = connHelper;
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {

        if (mSpoofer != null && mSpoofer.isSpoofed()) {
            mSpoofer.tearDownSpoofing();
        }
        
        mDefaultExceptionHandler.uncaughtException(thread, ex);
    }
    
    

}

package andraus.bluetoothhidemu.helper;

import android.content.Context;

public final class BluetoothConnHelperFactory {
    
    private static BluetoothConnHelper mInstance = null;
    
    public static BluetoothConnHelper getInstance(Context appContext) {
        if (mInstance == null) {
            mInstance = new BluetoothConnHelperMotoReflectImpl();
            //mInstance = new BluetoothConnHelperGenericImpl(appContext);
        }
        
        return mInstance;
    }

}

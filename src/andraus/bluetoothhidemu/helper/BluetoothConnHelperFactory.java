package andraus.bluetoothhidemu.helper;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;

public final class BluetoothConnHelperFactory {
    
    private static BluetoothConnHelper mInstance = null;
    
    /**
     * 
     * @param appContext
     * @param adapter
     * @return
     */
    public static BluetoothConnHelper getInstance(Context appContext, BluetoothAdapter adapter) {
        if (mInstance == null) {
            //mInstance = new BluetoothConnHelperMotoReflectImpl(adapter);
            mInstance = new BluetoothConnHelperGenericImpl(appContext, adapter);
        }
        
        return mInstance;
    }

}

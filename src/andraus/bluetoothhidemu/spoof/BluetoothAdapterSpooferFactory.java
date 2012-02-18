package andraus.bluetoothhidemu.spoof;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;

public final class BluetoothAdapterSpooferFactory {
    
    private static BluetoothAdapterSpoofer mInstance = null;
    
    /**
     * 
     * @param appContext
     * @param adapter
     * @return
     */
    public static BluetoothAdapterSpoofer getInstance(Context appContext, BluetoothAdapter adapter) {
        if (mInstance == null) {
            mInstance = new BluetoothAdapterSpooferMotoReflect(adapter);
            //mInstance = new BluetoothAdapterSpooferGeneric(appContext, adapter);
        }
        
        return mInstance;
    }

}

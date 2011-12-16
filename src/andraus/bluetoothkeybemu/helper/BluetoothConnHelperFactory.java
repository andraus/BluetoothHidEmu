package andraus.bluetoothkeybemu.helper;

import android.content.Context;

public final class BluetoothConnHelperFactory {
    
    public static BluetoothConnHelperInterface getInstance(Context appContext) {
        //BluetoothConnHelperInterface connHelper = new BluetoothConnHelperMoto();
        BluetoothConnHelperInterface connHelper = new BluetoothConnHelperGenericImpl(appContext);
        
        return connHelper;
    }

}

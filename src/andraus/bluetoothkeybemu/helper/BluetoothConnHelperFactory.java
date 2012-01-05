package andraus.bluetoothkeybemu.helper;

import android.content.Context;

public final class BluetoothConnHelperFactory {
    
    public static BluetoothConnHelper getInstance(Context appContext) {
        //BluetoothConnHelperInterface connHelper = new BluetoothConnHelperMoto();
        BluetoothConnHelper connHelper = new BluetoothConnHelperGenericImpl(appContext);
        
        return connHelper;
    }

}

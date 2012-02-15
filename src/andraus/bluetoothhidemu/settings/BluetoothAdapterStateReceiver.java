package andraus.bluetoothhidemu.settings;

import andraus.bluetoothhidemu.BluetoothHidEmuActivity;
import andraus.bluetoothhidemu.spoof.BluetoothAdapterSpoofer;
import andraus.bluetoothhidemu.spoof.Spoof.SpoofMode;
import andraus.bluetoothhidemu.util.DoLog;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Receive notification of bluetooth adapter being turned off
 */
public class BluetoothAdapterStateReceiver extends BroadcastReceiver {
    
    private static final String TAG = BluetoothHidEmuActivity.TAG;
    
    private Activity mActivity = null;
    private static BluetoothAdapterSpoofer mSpoofer = null;

    /**
     * Constructor
     * 
     * @param activity
     */
    public BluetoothAdapterStateReceiver(Activity activity) {
        super();
        
        mActivity = activity;
    }
    
    /**
     * Constructor
     * 
     * @param activity
     * @param adapterSpoofer
     */
    public BluetoothAdapterStateReceiver(Activity activity, BluetoothAdapterSpoofer adapterSpoofer) {
        this(activity);
        
        mSpoofer = adapterSpoofer;
    }

    /**
     * 
     */
    @Override
    public void onReceive(Context context, Intent intent) {

        if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(intent.getAction())) {
            DoLog.d(TAG, "BluetoothAdapter turned off. Bailing out...");
            mActivity.finish();
        } else if (BluetoothAdapter.ACTION_SCAN_MODE_CHANGED.equals(intent.getAction())) {
            int scanMode = intent.getExtras().getInt(BluetoothAdapter.EXTRA_SCAN_MODE);
            DoLog.d(TAG, "Scan mode changed: " + scanMode);

            if (scanMode == BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
                
                SpoofMode spoofMode = Settings.getEmulationMode(context);
                
                if (!mSpoofer.isSpoofed()) mSpoofer.tearUpSpoofing(spoofMode);
            } else {
                if (mSpoofer.isSpoofed()) mSpoofer.tearDownSpoofing();
            }
            
        }

    }

}

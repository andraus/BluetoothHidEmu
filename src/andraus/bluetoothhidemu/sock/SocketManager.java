package andraus.bluetoothhidemu.sock;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import java.io.IOException;

import andraus.bluetoothhidemu.BluetoothHidEmuActivity;
import andraus.bluetoothhidemu.sock.payload.HidPayload;
import andraus.bluetoothhidemu.spoof.BluetoothAdapterSpoofer;
import andraus.bluetoothhidemu.util.DoLog;
import andraus.bluetoothhidemu.view.BluetoothDeviceView;

/**
 * Singleton
 * 
 */
public class SocketManager {
    
    private String TAG = BluetoothHidEmuActivity.TAG;
    
    private static SocketManager mInstance = null;
    
    public static final int STATE_NONE = BluetoothSocketThread.STATE_NONE;
    public static final int STATE_WAITING = BluetoothSocketThread.STATE_WAITING;
    public static final int STATE_ACCEPTED = BluetoothSocketThread.STATE_ACCEPTED;
    public static final int STATE_DROPPING = BluetoothSocketThread.STATE_DROPPING;
    public static final int STATE_DROPPED = BluetoothSocketThread.STATE_DROPPED;
    
    private BluetoothAdapterSpoofer mSpoofer = null;
    
    private BluetoothSocketThread mCtrlThread = null;
    private BluetoothSocketThread mIntrThread = null;
    
    /**
     * 
     * @param connHelper
     */
    private SocketManager(BluetoothAdapterSpoofer connHelper) {
        mSpoofer = connHelper;
    }

    /**
     * 
     * @param connHelper
     * @return
     */
    public static SocketManager getInstance(BluetoothAdapterSpoofer connHelper) {
        if (mInstance == null) {
            mInstance = new SocketManager(connHelper);
        }
        
        return mInstance;
    }


    /**
     * 
     */
    public void destroyThreads() {
        mCtrlThread = null;
        mIntrThread = null;

    }
    
    /**
     * 
     * @param hidPayload
     */
    public void sendPayload(HidPayload hidPayload) {

    	if (mIntrThread != null && mIntrThread.isAlive()) {
            mIntrThread.sendBytes(hidPayload.getPayload());
        }
    }

    /**
     * 
     * @param thread
     * @param name
     * @param hostDevice
     * @param socketPort
     * @return
     */
    private BluetoothSocketThread initThread(BluetoothSocketThread thread, 
                    String name, BluetoothDevice hostDevice, int socketPort) {
        
        BluetoothSocket socket;

        try {
            socket = mSpoofer.connectL2capSocket(hostDevice, socketPort, true, true);
        } catch (IOException e) {
            DoLog.e(TAG, String.format("Cannot acquire %sSocket", name), e);
            throw new RuntimeException(e);
        }
        
        if (socket != null) {
            DoLog.d(TAG, String.format("%s socket successfully created: %s", name, socket));
        }
        return new BluetoothSocketThread(socket, name);
    }
    
    
    /**
     * Init sockets and threads
     * 
     * @param adapter
     * @param deviceView
     */
    public void startSockets(BluetoothAdapter adapter, BluetoothDeviceView deviceView) {
        
        BluetoothDevice device = deviceView.getBluetoothDevice();
        
        if (device == null) {
            DoLog.w(TAG, "no hosts not found");
            return;
        } else {
            DoLog.d(TAG, "host selected: " + device);
        }
    
        // discovery is a heavy process. Apps must always cancel it when connecting.
        adapter.cancelDiscovery();
        
        mCtrlThread = initThread(mCtrlThread, "ctrl", device, 0x11);
        mIntrThread = initThread(mIntrThread, "intr", device, 0x13);
        
        new Thread(mCtrlThread).start();
        new Thread(mIntrThread).start();
    }
    
    /**
     * Stop L2CAP "control" and "interrupt" channel threads
     */
    public void stopSockets() {

        DoLog.d(TAG, "stop bluetooth connections");
        
        if (mIntrThread != null) {
            mIntrThread.sendBytes(HidPayload.disconnectReq());
            mIntrThread.stopGracefully();
            mIntrThread = null;
        }

        if (mCtrlThread != null) {
            mCtrlThread.stopGracefully();
            mCtrlThread = null;
        }
        
    }
    
    public boolean checkState(int state) {
        return mCtrlThread != null && (mCtrlThread.getConnectionState() == state || mIntrThread.getConnectionState() == state);
    }

}

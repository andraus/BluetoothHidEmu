package andraus.bluetoothhidemu.sock;

import java.io.IOException;
import java.util.Set;

import andraus.bluetoothhidemu.BluetoothKeybEmuActivity;
import andraus.bluetoothhidemu.helper.BluetoothConnHelper;
import andraus.bluetoothhidemu.util.DoLog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

/**
 * Singleton
 * 
 */
public class SocketManager {
    
    private String TAG = BluetoothKeybEmuActivity.TAG;
    
    private static SocketManager mInstance = null;
    
    public static final int STATE_NONE = BluetoothSocketThread.STATE_NONE;
    public static final int STATE_WAITING = BluetoothSocketThread.STATE_WAITING;
    public static final int STATE_ACCEPTED = BluetoothSocketThread.STATE_ACCEPTED;
    public static final int STATE_DROPPING = BluetoothSocketThread.STATE_DROPPING;
    public static final int STATE_DROPPED = BluetoothSocketThread.STATE_DROPPED;
    
    private BluetoothConnHelper mConnHelper = null;
    
    private BluetoothSocketThread mCtrlThread = null;
    private BluetoothSocketThread mIntrThread = null;
    
    private final HidProtocolManager mHidManager = HidProtocolManager.getInstance();

    /**
     * 
     * @param bluetoothAdapter
     * @param connHelper
     */
    private SocketManager(BluetoothConnHelper connHelper) {
        mConnHelper = connHelper;
    }

    /**
     * 
     * @param connHelper
     * @return
     */
    public static SocketManager getInstance(BluetoothConnHelper connHelper) {
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
     * @param keyCode
     */
    public void sendChar(char character) {

        if (mIntrThread != null && mIntrThread.isAlive()) {
            byte[] payload = mHidManager.payloadKeyb(character);
            
            if (payload != null) {
                mIntrThread.sendBytes(payload);
            }
        }
    }

    /**
     * 
     * @param x
     * @param y
     */
    public void sendPointerMove(int x, int y) {
        
        if (mIntrThread != null && mIntrThread.isAlive()) {
            byte[] payload = mHidManager.payloadMouseMove(x, y);
            
            if (payload != null) {
                mIntrThread.sendBytes(payload);
            }
        }
    }

    /**
     * 
     * @param button
     */
    public void sendPointerButton(int button) {
        
        if (mIntrThread != null && mIntrThread.isAlive()) {
            byte[] payload = mHidManager.payloadMouseButton(button);
            
            if (payload != null) {
                mIntrThread.sendBytes(payload);
            }
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
            socket = mConnHelper.connectL2capSocket(hostDevice, socketPort, true, true);
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
     * @param hostDevice
     */
    public void startSockets(BluetoothAdapter adapter, BluetoothDevice hostDevice) {
        
        Set<BluetoothDevice> pairedDevices = adapter.getBondedDevices();
        
        if (pairedDevices.isEmpty()) {
            DoLog.w(TAG, "no paired devices found");
            return;
        }
        
        if (hostDevice == null) {
            DoLog.w(TAG, "no hosts not found");
            return;
        } else {
            DoLog.d(TAG, "host selected: " + hostDevice);
        }
    
        // discovery is a heavy process. Apps must always cancel it when connecting.
        adapter.cancelDiscovery();
        
        mCtrlThread = initThread(mCtrlThread, "ctrl", hostDevice, 0x11);
        mIntrThread = initThread(mIntrThread, "intr", hostDevice, 0x13);

        mCtrlThread.start();
        mIntrThread.start();
    }
    
    /**
     * Stop L2CAP "control" and "interrupt" channel threads
     */
    public void stopSockets() {

        DoLog.d(TAG, "stop bluetooth connections");
        
        if (mIntrThread != null) {
            mIntrThread.sendBytes(mHidManager.disconnectReq());
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

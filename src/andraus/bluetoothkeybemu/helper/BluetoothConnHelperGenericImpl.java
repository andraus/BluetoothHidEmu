package andraus.bluetoothkeybemu.helper;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import andraus.bluetoothkeybemu.BluetoothKeybEmuActivity;
import andraus.bluetoothkeybemu.util.DoLog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;

import andraus.bluetoothkeybemu.R;

public class BluetoothConnHelperGenericImpl implements BluetoothConnHelperInterface {
    
    private static final String TAG = BluetoothKeybEmuActivity.TAG;
    
    private static final String CMD_SU = "su";
    private static final String CMD_ID = "id\n";
    
    private static final String CMD_ID_RESP = "uid=0(root)";
    
    private String mSetupErrorMsg;
    private Context mContext = null;
    private String mHidEmuPath = null;
    
    private class ShellResponse {
        static final int ERROR = -0x1;
        static final int SUCCESS = 0x0;
        static final int NON_ROOT = 0xff;
        
        int code = -1;
        String msg;
    }
    
    private String getHidEmuPath() {
        return "/data/data/" + mContext.getPackageName() + "/hid_emu";
    }
    
    private boolean installHidEmu() {
        boolean result = true;
        mHidEmuPath = getHidEmuPath();
        
        DoLog.d(TAG, "Checking existence of " + mHidEmuPath);
        
        File hidEmu = new File(mHidEmuPath);
        
        if (!hidEmu.exists()) {
            DoLog.d(TAG, "hid_emu does not exist. Installing...");
            InputStream inStream = null;
            OutputStream outStream = null;
            try {
                inStream = mContext.getAssets().open("hid_emu");
                outStream = new FileOutputStream(mHidEmuPath);
                
                byte[] buffer = new byte[1024];
                int size;
                while ((size = inStream.read(buffer)) > 0) {
                    outStream.write(buffer, 0, size);
                }
                outStream.flush();
                
            } catch (IOException e) {
                DoLog.e(TAG,  "Failed to install hid_emu:", e);
                result = false;
            } finally {
                if (outStream != null)
                    try {
                        outStream.close();
                    } catch (IOException e) {
                        DoLog.e(TAG, "Failed to close output stream: ", e);
                    }
                if (inStream != null)
                    try {
                        inStream.close();
                    } catch (IOException e) {
                        DoLog.e(TAG, "Failed to close input stream: ", e);
                    }
            }
            DoLog.d(TAG, "hid_emu installed. Setting permissions...");
            ShellResponse shellResp = executeShellCmd("chmod 744 " + mHidEmuPath + "\n");
            
            result  = (shellResp.code == 0);
        }
        
        if (result) {
            ShellResponse shellResp = executeShellCmd(mHidEmuPath + "\n");
            if (result = (shellResp.code == 0)) {
                DoLog.d(TAG, "hid_emu version: " + shellResp.msg);
            }
        }
        
        return result;
    }
    
    private ShellResponse executeShellCmd(String cmd) {
        ShellResponse shellResponse = new ShellResponse();
        
        Process process = null;
        try {
            process = Runtime.getRuntime().exec(CMD_SU);
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            writer.write(cmd);
            writer.flush();
            writer.write("exit\n");
            writer.flush();
            
            String line = null;
            while ((line = reader.readLine()) != null) {
                shellResponse.msg = line;
                DoLog.d(TAG, "shell: " + shellResponse.msg);
            }
            
            shellResponse.code = process.waitFor();
            DoLog.d(TAG, "process exit value: " + shellResponse.code);
            
        } catch (IOException e) {
            DoLog.d(TAG, "ioexception: ", e);
            // Probably su doesn't exist.
            shellResponse.code = ShellResponse.NON_ROOT;
        } catch (InterruptedException e) {
            DoLog.e(TAG, "interrupted exception: ", e);
            shellResponse.code = ShellResponse.ERROR;
        } finally {
            if (process != null) {
                process.destroy();
            }
        }
        
        return shellResponse;
    }

    @Override
    public int getBluetoothDeviceClass(BluetoothAdapter adapter) {
        // TODO Auto-generated method stub
       throw new IllegalStateException("not implemented");
    }

    @Override
    public int spoofBluetoothDeviceClass(BluetoothAdapter adapter,
            int deviceClass) {
        // TODO Auto-generated method stub
        throw new IllegalStateException("not implemented");
    }

    @Override
    public int addHidDeviceSdpRecord(BluetoothAdapter adapter) {
        // TODO Auto-generated method stub
        throw new IllegalStateException("not implemented");
    }

    @Override
    public void dellHidDeviceSdpRecord(BluetoothAdapter adapter, int handle) {
        // TODO Auto-generated method stub
        throw new IllegalStateException("not implemented");

    }

    @Override
    public BluetoothSocket connectL2capSocket(BluetoothDevice device, int port,
            boolean auth, boolean encrypt) throws IOException {
        // TODO Auto-generated method stub
        throw new IllegalStateException("not implemented");
    }

    @Override
    public void cleanup() {
        // TODO Auto-generated method stub
        throw new IllegalStateException("not implemented");

    }

    @Override
    public boolean setup() {
        ShellResponse shellResp = executeShellCmd(CMD_ID);
        
        switch (shellResp.code) {
            case ShellResponse.NON_ROOT:
                mSetupErrorMsg = mContext.getResources().getString(R.string.msg_no_root); 
                return false;
            case ShellResponse.SUCCESS:
                if (shellResp.msg != null && shellResp.msg.contains(CMD_ID_RESP)) {
                    mSetupErrorMsg = null;
                    //return true;
                    return installHidEmu();
                } else {
                    mSetupErrorMsg = mContext.getResources().getString(R.string.msg_no_root);
                    return false;
                }
            case ShellResponse.ERROR:
            default:
                mSetupErrorMsg = shellResp.msg;
                return false;
        }
    }
    
    @Override
    public String getSetupErrorMsg() {
        
        return mSetupErrorMsg;
        
    }
    
    BluetoothConnHelperGenericImpl(Context appContext) {
        mContext  = appContext;
    }

}

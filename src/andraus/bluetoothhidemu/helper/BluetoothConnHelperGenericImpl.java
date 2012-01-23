package andraus.bluetoothhidemu.helper;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import andraus.bluetoothhidemu.BluetoothHidEmuActivity;
import andraus.bluetoothhidemu.R;
import andraus.bluetoothhidemu.util.DoLog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.ParcelUuid;

public class BluetoothConnHelperGenericImpl extends BluetoothConnHelper {
    

    private static final String TAG = BluetoothHidEmuActivity.TAG;
    
    private static final String CMD_SU = "su";

    private static final String CMD_ID = "id\n";
    private static final String CMD_ID_RESP = "uid=0(root)";
    
    private static final String CMD_READ_CLASS = " read_class\n";
    private static final String CMD_READ_CLASS_RESP = "class";
    
    private static final String CMD_SPOOF_CLASS = " spoof_class 0x%06X\n";
    private static final String CMD_SPOOF_CLASS_RESP = "class spoofed.";
    
    private static final String CMD_ADD_HID_SDP = " add_hid\n";
    private static final String CMD_ADD_HID_SDP_RESP = "handle";
    
    private static final String CMD_DEL_HID_SDP = " del_hid 0x%06X\n";
    
    private String mHidEmuPath = null;
    private int mOriginalDeviceClass = 0;
    private int mHidSdpHandle = 0;
    
    BluetoothConnHelperGenericImpl(Context appContext) {
        super(appContext);
    }

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
    
    private ShellResponse installHidEmu() {
        boolean result = true;
        ShellResponse shellResp = null;
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
            shellResp = executeShellCmd("chmod 744 " + mHidEmuPath + "\n");
            
            result  = (shellResp.code == 0);
        }
        
        if (result) {
            shellResp = executeShellCmd(mHidEmuPath + "\n");
            if (shellResp.code == 0) {
                DoLog.d(TAG, "hid_emu version: " + shellResp.msg);
            }
        }
        
        return shellResp;
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
        ShellResponse shellResp = executeShellCmd(mHidEmuPath + CMD_READ_CLASS);
        
        if (shellResp.code != 0) {
            throw new IllegalStateException("Unexpected failure");
        }
        
        /*
         * response from hid_emu read_class is like:
         * class: 0xaabbcc
         * 
         * using ": 0x" as reg. exp for split, will result in
         * token[0] = class
         * token[1] = aabbcc
         */
        String[] token = shellResp.msg.split(": 0x");
        int deviceClass = 0;
        if (CMD_READ_CLASS_RESP.equals(token[0])) {
            deviceClass = Integer.parseInt(token[1], 16);
        }
        
        return deviceClass;
    }

    @Override
    public int spoofBluetoothDeviceClass(BluetoothAdapter adapter,
            int deviceClass) {
        
        if (adapter != null) {
            mOriginalDeviceClass = getBluetoothDeviceClass(adapter);
            DoLog.d(TAG, String.format("original class stored: 0x%06X", mOriginalDeviceClass));
        }
        
        ShellResponse shellResp = executeShellCmd(mHidEmuPath + String.format(CMD_SPOOF_CLASS, deviceClass));
        
        if (shellResp.code != 0) {
            throw new IllegalStateException("Unexpected failure");
        }
        
        if (CMD_SPOOF_CLASS_RESP.equals(shellResp.msg)) {
            return 0;
        } else {
            return -1;
        }
    }

    @Override
    public int addHidDeviceSdpRecord(BluetoothAdapter adapter) {
        
        if (mHidSdpHandle != 0) {
            DoLog.w(TAG, String.format("HID SDP record already present. Handle: 0x%06X",mHidSdpHandle));
            return mHidSdpHandle;
        }
        
        ShellResponse shellResp = executeShellCmd(mHidEmuPath + CMD_ADD_HID_SDP);
        
        if (shellResp.code != 0) {
            throw new IllegalStateException("Unexpected failure");
        }
        
        /*
         * response from hid_emu add_hid is like:
         * handle: 0xaabbcc
         * 
         * using ": 0x" as reg. exp for split, will result in
         * token[0] = handle
         * token[1] = aabbcc
         */
        String[] token = shellResp.msg.split(": 0x");
        if (CMD_ADD_HID_SDP_RESP.equals(token[0])) {
            mHidSdpHandle = Integer.parseInt(token[1], 16);
        }
        
        return mHidSdpHandle;
    }

    @Override
    public void delHidDeviceSdpRecord(BluetoothAdapter adapter) {
        if (mHidSdpHandle == 0) {
            DoLog.w(TAG, "No HID SDP record handle present.");
            return;
        }
        
        ShellResponse shellResp = executeShellCmd(mHidEmuPath + String.format(CMD_DEL_HID_SDP, mHidSdpHandle));
        
        if (shellResp.code != 0) {
            throw new IllegalStateException("Unexpected failure");
        }
        
        /*
         * response from hid_emu del_hid <handle> is like:
         * Removed handle: 0xaabbcc
         */

    }

    @Override
    public BluetoothSocket connectL2capSocket(BluetoothDevice device, int port,
            boolean auth, boolean encrypt) throws IOException {
        
        final int TYPE_L2CAP = 3;
        
        BluetoothSocket socket = null;
        Class<?>[] argsClasses = new Class[] { int.class /* type */,
                                            int.class /* fd */,
                                        boolean.class /* auth */,
                                        boolean.class /* encrypt */,
                                BluetoothDevice.class /* device */,
                                            int.class /* port */,
                                     ParcelUuid.class /* uuid */ };
        
        Object[] args = new Object[] {  Integer.valueOf(TYPE_L2CAP),
                                        Integer.valueOf(-1),
                                        Boolean.valueOf(auth),
                                        Boolean.valueOf(encrypt),
                                        device,
                                        Integer.valueOf(port),
                                        null };
        
        try {
            Class<BluetoothSocket> bluetoothSocketClass = BluetoothSocket.class;
            Constructor<BluetoothSocket> bluetoothSocketConstructor;
            bluetoothSocketConstructor = bluetoothSocketClass.getDeclaredConstructor(argsClasses);
            bluetoothSocketConstructor.setAccessible(true);
            
            socket = bluetoothSocketConstructor.newInstance(args);
            
        } catch (SecurityException e) {
            DoLog.e(TAG, "Reflection error:", e);
            throw new IOException("Reflection error", e);
        } catch (NoSuchMethodException e) {
            DoLog.e(TAG, "Reflection error:", e);
            throw new IOException("Reflection error", e);
        } catch (IllegalArgumentException e) {
            DoLog.e(TAG, "Reflection error:", e);
            throw new IOException("Reflection error", e);
        } catch (InstantiationException e) {
            DoLog.e(TAG, "Reflection error:", e);
            throw new IOException("Reflection error", e);
        } catch (IllegalAccessException e) {
            DoLog.e(TAG, "Reflection error:", e);
            throw new IOException("Reflection error", e);
        } catch (InvocationTargetException e) {
            DoLog.e(TAG, "Reflection error:", e);
            throw new IOException("Reflection error", e);
        }
        
        
        return socket;
        
    }

    @Override
    public void cleanup() {
        if (mOriginalDeviceClass != 0) {
            spoofBluetoothDeviceClass(null, mOriginalDeviceClass);
        }
        if (mHidSdpHandle != 0) {
            delHidDeviceSdpRecord(null);
        }
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
                    shellResp = installHidEmu();
                    if (shellResp.code != 0) {
                        String errorMsg = shellResp.msg == null ? Integer.toString(shellResp.code) : shellResp.msg;
                        mSetupErrorMsg = mContext.getResources().getString(R.string.msg_generic_failure, errorMsg);
                    }
                    return (shellResp.code == 0);
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
}

package andraus.bluetoothkeybemu;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


import andraus.bluetoothkeybemu.util.DoLog;
import android.bluetooth.BluetoothSocket;

public class BluetoothSocketThread extends Thread {
	
	private static final String TAG = BluetoothKeybEmuActivity.TAG;
	
	private static final int BUF_SIZE = 16;
	
	BluetoothSocket mSocket = null;
	static final int STATE_NONE = 0;
	static final int STATE_WAITING = 1;
	static final int STATE_ACCEPTED = 2;
	static final int STATE_DROPPED = 3;
	
	static final int TIME_5_SEC = 5000;
	static final int TIME_1_SEC = 1000;
	
	private int mState = 0;
	private int mSuggestedRetryTimeMs = 0;
	private boolean mReuseSocket = false;
	
	private InputStream mInputStream = null;
	private OutputStream mOutputStream = null;
	
	
	public BluetoothSocketThread(String name) {
	    super();
	    
	    if (name == null) {
	        throw new IllegalStateException("name is null");
	    }
	    setName(name);
	    
	}
	public BluetoothSocketThread(BluetoothSocket socket, String name) {
        super(name);
		
		if (socket == null) {
		    throw new IllegalStateException("socket is null");
		}
		
		mSocket = socket;
	}
	
	@Override
	public void run() {
		
		try {
			mState = STATE_WAITING;
			mSocket.connect();
		} catch (IOException e) {
			DoLog.e(TAG, getName(), e);
			dropConnection(false, TIME_5_SEC);
		}
		
		if (mSocket != null && mState == STATE_WAITING) {
			try {
				mState = STATE_ACCEPTED;
				mInputStream = mSocket.getInputStream();
				mOutputStream = mSocket.getOutputStream();
				
				int numBytes = 0;
				byte[] bytes = new byte[BUF_SIZE];
				
				while (mState == STATE_ACCEPTED) {
					
					if (mInputStream.available() > 0 && (numBytes = mInputStream.read(bytes)) >= 0) {
						
						String s = getByteString(bytes, numBytes);

						DoLog.d(TAG, getName() + " - received: " + s);
						DoLog.d(TAG, getName() + " - size: " + numBytes);
					}
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						DoLog.e(TAG, getName(), e);
						dropConnection(false, TIME_5_SEC);
					}
					
				}
	
			} catch (IOException e) {
				DoLog.e(TAG, getName(), e);
				dropConnection(false, TIME_5_SEC);
			}
		}
		
	}
	
	public void sendBytes(byte[] bytes) {
		if (mState == STATE_ACCEPTED) {
			try {
				DoLog.d(TAG, "Sending bytes to " + getName() + ": " + getByteString(bytes, bytes.length));
				mOutputStream.write(bytes);
				mOutputStream.flush();
				
			} catch (IOException e) {
			    // connection dropped for some reason.
				DoLog.e(TAG, getName(),e);
				dropConnection(false, TIME_5_SEC);
			}
		}
	}
	
	private void dropConnection(boolean reuseSocket, int suggestedRetryTimeMs) {
		DoLog.d(TAG, "dropping socket - " + mSocket);
		mState = STATE_DROPPED;
		mSuggestedRetryTimeMs = suggestedRetryTimeMs;
		mReuseSocket = reuseSocket;
		if (mSocket != null) {
			try {
				if (mInputStream != null) mInputStream.close();
				if (mOutputStream != null) mOutputStream.close();
				mSocket.close();
				
			} catch (IOException e) {
				DoLog.w(TAG, getName(), e);
			}
		}
		interrupt();
	}
	
	public void stopGracefully() {
        dropConnection(false, TIME_5_SEC);
	    mState = STATE_NONE;
	}
	
	public int getConnectionState() {
		return mState;
	}
	
	public int getSuggestedRetryTimeMs() {
	    return mSuggestedRetryTimeMs;
	}
	
	public boolean reuseSocket() {
	    return mReuseSocket;
	}
	
	private String getByteString(byte[] bytes, int size) {
		StringBuilder s = new StringBuilder();
		
		for (int i = 0; i < size; i++) {
			s.append(String.format("0x%02X ", bytes[i]));
		}
		
		return s.toString();
	}
	
}

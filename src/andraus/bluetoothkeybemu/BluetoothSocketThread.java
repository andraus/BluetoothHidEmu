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
	static int STATE_NONE = 0;
	static int STATE_WAITING = 1;
	static int STATE_ACCEPTED = 2;
	
	private int mState = 0;
	private InputStream mInputStream = null;
	private OutputStream mOutputStream = null;
	
	

	public BluetoothSocketThread(BluetoothSocket socket, String name) {
		super();
		
		mSocket = socket;
		setName(name);
	}
	
	@Override
	public void run() {
		
		try {
			mState = STATE_WAITING;
			mSocket.connect();
		} catch (IOException e) {
			DoLog.e(TAG, "interrupted: ", e);
		}
		
		if (mSocket != null) {
			try {
				mState = STATE_ACCEPTED;
				mInputStream = mSocket.getInputStream();
				mOutputStream = mSocket.getOutputStream();
				
				int numBytes = 0;
				byte[] bytes = new byte[BUF_SIZE];
				
				while (mState == STATE_ACCEPTED) {
					
					if ((numBytes = mInputStream.read(bytes)) >= 0) {
						
						String s = getByteString(bytes, numBytes);

						DoLog.d(TAG, getName() + " - received: " + s);
					}
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						DoLog.e(TAG, "interrupted: " + e);
					}
					
				}
	
			} catch (IOException e) {
				DoLog.e(TAG, "ioexception: ", e);
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
				DoLog.e(TAG, "ioException: ",e);
			}
		}
	}
	
	public void stopGracefully() {
		DoLog.d(TAG, "stopping thread - " + mSocket);
		if (mSocket != null) {
			try {
				mInputStream.close();
				mOutputStream.close();
				mSocket.close();
				mState = STATE_NONE;
				
			} catch (IOException e) {
				DoLog.e(TAG, "close failed: ", e);
			}
		}
	}
	
	public int getConnectionState() {
		return mState;
	}
	
	private String getByteString(byte[] bytes, int size) {
		StringBuilder s = new StringBuilder();
		
		for (int i = 0; i < size; i++) {
			s.append(String.format("0x%02X ", bytes[i]));
		}
		
		return s.toString();
	}
	
}

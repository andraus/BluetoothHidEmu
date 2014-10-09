package andraus.bluetoothhidemu.sock;

import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import andraus.bluetoothhidemu.BluetoothHidEmuActivity;
import andraus.bluetoothhidemu.sock.payload.HidPayload;
import andraus.bluetoothhidemu.util.DoLog;

public class BluetoothSocketThread implements Runnable {
	
	private String TAG = BluetoothHidEmuActivity.TAG + "Comm";
	
	private static final int BUF_SIZE = 16;
	
	BluetoothSocket mSocket = null;
	static final int STATE_NONE = 0;
	static final int STATE_WAITING = 1;
	static final int STATE_ACCEPTED = 2;
	static final int STATE_DROPPING = 3;
	static final int STATE_DROPPED = 4;
	
	static final int TIME_5_SEC = 5000;
	static final int TIME_1_SEC = 1000;
	
	private int mState = 0;
	private int mSuggestedRetryTimeMs = 0;
	
	private InputStream mInputStream = null;
	private OutputStream mOutputStream = null;

    private String name;
	
	
	public BluetoothSocketThread(String name) {
	    super();
	    
	    if (name == null) {
	        throw new IllegalStateException("name is null");
	    }
	    setName(name);
	    
	}
	public BluetoothSocketThread(BluetoothSocket socket, String name) {
        super();

		if (socket == null) {
		    throw new IllegalStateException("socket is null");
		}

        setName(name);
		mSocket = socket;
	}
	
	@Override
	public void run() {
		
		try {
			mState = STATE_WAITING;
			mSocket.connect();
		} catch (IOException e) {
			DoLog.e(TAG, getName(), e);
			dropConnection(TIME_5_SEC);
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
						
						handleRequest(bytes,numBytes);
					}
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						DoLog.e(TAG, getName(), e);
						dropConnection(TIME_5_SEC);
					}
					
				}
	
			} catch (IOException e) {
				DoLog.e(TAG, getName(), e);
				dropConnection(TIME_5_SEC);
			}
		}
		mState = STATE_DROPPED;
	}
	
	public synchronized void sendBytes(byte[] bytes) {
		if (mState == STATE_ACCEPTED) {
			try {
				DoLog.d(TAG, "Sending bytes to " + getName() + ": " + getByteString(bytes, bytes.length));
				mOutputStream.write(bytes);
				mOutputStream.flush();
				
			} catch (IOException e) {
			    // connection dropped for some reason.
				DoLog.e(TAG, getName(),e);
				dropConnection(TIME_1_SEC);
			}
		}
	}
	
	private synchronized void dropConnection(int suggestedRetryTimeMs) {
		DoLog.d(TAG, "dropping socket - " + mSocket);
		mState = STATE_DROPPING;
		mSuggestedRetryTimeMs = suggestedRetryTimeMs;
		
		if (mSocket != null) {
			try {
				if (mInputStream != null) mInputStream.close();
				if (mOutputStream != null) mOutputStream.close();
				mSocket.close();
				
			} catch (IOException e) {
				DoLog.w(TAG, getName(), e);
			}
		}
		mState = STATE_DROPPED;
	}
	
	public synchronized void stopGracefully() {
        if (mState == STATE_ACCEPTED || mState == STATE_WAITING) {
            dropConnection(TIME_5_SEC);
        }
	    mState = STATE_NONE;
	}
	
	public int getConnectionState() {
	    //DoLog.d(TAG,getName() + " state - " + mState);
		return mState;
	}

    public boolean isAlive() {
        return mState == STATE_ACCEPTED;
    }
	
	public int getSuggestedRetryTimeMs() {
	    return mSuggestedRetryTimeMs;
	}
	
	public void setSocket(BluetoothSocket socket) {
	    mSocket = socket;
	}
	
	private String getByteString(byte[] bytes, int size) {
		StringBuilder s = new StringBuilder();
		
		for (int i = 0; i < size; i++) {
			s.append(String.format("0x%02x ", bytes[i]));
		}
		
		return s.toString();
	}

    /**
     * Method to handle incoming requests from the host
     * @param bytes
     * @param length
     */
    private void handleRequest(byte[] bytes, int length) {

        if (length == 1) {
            switch (bytes[0]) {
                case (HidPayload.REQ_SET_PROTOCOL): // Request SET_PROTOCOL Boot Protocol Mode
                case (HidPayload.REQ_SET_PROTOCOL | 1): // Request SET_PROTOCOL Report Protocol Mode

                    // ACK the request immediately:
                    final byte[] ack = { 0x00 };
                    sendBytes(ack);
                    break;
            }
        }

        String s = getByteString(bytes, length);

        DoLog.i(TAG, getName() + " - received: " + s);
        DoLog.d(TAG, getName() + " - size: " + length);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

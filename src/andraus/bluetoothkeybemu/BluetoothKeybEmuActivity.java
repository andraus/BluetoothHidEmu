package andraus.bluetoothkeybemu;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import andraus.bluetoothkeybemu.helper.BluetoothConnHelperFactory;
import andraus.bluetoothkeybemu.helper.BluetoothConnHelper;
import andraus.bluetoothkeybemu.helper.CleanupExceptionHandler;
import andraus.bluetoothkeybemu.util.DoLog;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class BluetoothKeybEmuActivity extends Activity {
	
	public static String TAG = "BluetoothKeyb";
	
	private ToggleButton mToggleSocketButton = null;
	private Spinner mDeviceSpinner = null;
	private TextView mCtrlTextView = null;
	private TextView mIntrTextView = null;
	private BluetoothDeviceArrayAdapter mBluetoothDeviceArrayAdapter = null;
	
	private BluetoothAdapter mBluetoothAdapter = null;
	
	private BluetoothSocketThread mCtrlThread = null;
	private BluetoothSocketThread mIntrThread = null;
	
	private final HidProtocolHelper mHidHelper = new HidProtocolHelper();
	private BluetoothConnHelper mConnHelper = null;
	
	
	/**
	 * Initialize UI elements
	 */
	private void setupApp() {
		setContentView(R.layout.main);
		mToggleSocketButton = (ToggleButton) findViewById(R.id.ToggleSocketButton);
		mToggleSocketButton.setOnCheckedChangeListener(mToggleSocketButtonListener);
		
		mCtrlTextView = (TextView) findViewById(R.id.CtrlTextView);
		mIntrTextView = (TextView) findViewById(R.id.IntrTextView);
		
		mDeviceSpinner = (Spinner) findViewById(R.id.DeviceSpinner);
		
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        
        mConnHelper = BluetoothConnHelperFactory.getInstance(getApplicationContext());
        
        mBluetoothDeviceArrayAdapter = new BluetoothDeviceArrayAdapter(this, mBluetoothAdapter.getBondedDevices());
        mDeviceSpinner.setAdapter(mBluetoothDeviceArrayAdapter);
	}
	
	/**
	 * Listener for Bluetooth HID Keyboard toggle button
	 */
	CompoundButton.OnCheckedChangeListener mToggleSocketButtonListener = 
			new CompoundButton.OnCheckedChangeListener() {
		
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			if (buttonView.isChecked()) {
				//BluetoothKeybNative.setupSdp();
				startHidL2capSockets();
			} else {
				stopHidL2capSockets();
			}
			
		}
	};
	
	AdapterView.OnItemSelectedListener mSelectDeviceListener = 
			new AdapterView.OnItemSelectedListener() {

				@Override
				public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void onNothingSelected(AdapterView<?> adapterView) {
					// TODO Auto-generated method stub
					
				}
		
	};
	
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setupApp();
        Thread.setDefaultUncaughtExceptionHandler(new CleanupExceptionHandler(mConnHelper));

        if (!mConnHelper.validateBluetoothAdapter(mBluetoothAdapter) || !mConnHelper.setup()) {
            Toast.makeText(getApplicationContext(), mConnHelper.getSetupErrorMsg(), Toast.LENGTH_SHORT).show();
            finish();
        } else {
        
            int originalClass = mConnHelper.getBluetoothDeviceClass(mBluetoothAdapter);
            DoLog.d(TAG, "original class = 0x" + Integer.toHexString(originalClass));
    
            int err = mConnHelper.spoofBluetoothDeviceClass(mBluetoothAdapter, 0x002540);
            DoLog.d(TAG, "set class ret = " + err);

            int sdpRecHandle = mConnHelper.addHidDeviceSdpRecord(mBluetoothAdapter);
            
            DoLog.d(TAG, "SDP record handle = " + Integer.toHexString(sdpRecHandle));
        }
        
        
    }
    
    @Override
    protected void onDestroy() {
        DoLog.d(TAG, "...being destroyed");
        if (mConnHelper != null) {
            mConnHelper.cleanup();
        }
        super.onDestroy();
    }

    @Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		
    	if (mIntrThread != null && mIntrThread.isAlive()) {
    		byte[] payload = mHidHelper.hidPayload(keyCode);
    		
    		if (payload != null) {
    		    mIntrThread.sendBytes(payload);
    		}
    	}
    	
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {

    	if (mIntrThread != null && mIntrThread.isAlive()) {
    		byte[] payload = mHidHelper.hidPayload(0);
    		
    		if (payload != null) {
    		    mIntrThread.sendBytes(payload);
    		}
    	}
		return super.onKeyUp(keyCode, event);
	}


    private void startHidL2capSockets() {
    	Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
    	
    	if (pairedDevices.isEmpty()) {
    		DoLog.w(TAG, "no paired devices found");
    		return;
    	}
    	
    	BluetoothDevice hostDevice = (BluetoothDevice) mDeviceSpinner.getSelectedItem();
    	
    	if (hostDevice == null) {
    		DoLog.w(TAG, "no hosts not found");
    		return;
    	} else {
    		DoLog.d(TAG, "host selected: " + hostDevice);
    	}

    	BluetoothSocket ctrlSocket;

    	try {
    		ctrlSocket = mConnHelper.connectL2capSocket(hostDevice, 0x11, true, true);
    	} catch (IOException e) {
    		DoLog.e(TAG, "ioexception: ", e);
    		throw new RuntimeException(e);
    	}
    	
    	if (ctrlSocket != null) {
    		DoLog.d(TAG, "Ctrl socket successfully created: " + ctrlSocket);
    	}
    	
    	BluetoothSocket intrSocket;
    	try {
    		intrSocket = mConnHelper.connectL2capSocket(hostDevice, 0x13, true, true);
    	} catch (IOException e) {
    		DoLog.e(TAG, "ioexception ", e);
    		throw new RuntimeException(e);
    	}
    	
    	if (intrSocket != null) {
    		DoLog.d(TAG, "Intr socket successfully created: " + intrSocket);
    	}
    	
    	mCtrlThread = new BluetoothSocketThread(ctrlSocket, "ctrl");
    	mCtrlThread.start();
    	
    	mIntrThread = new BluetoothSocketThread(intrSocket, "intr");
    	mIntrThread.start();
    	
    	mThreadMonitorHandler.sendEmptyMessageDelayed(0, 200);
    	
    	
    }
    
    /**
     * Stop L2CAP "control" and "interrupt" channel threads
     */
    private void stopHidL2capSockets() {

		DoLog.d(TAG, "stop bt server");
		
		if (mCtrlThread != null) {
			mCtrlThread.stopGracefully();
		}
		
		if (mIntrThread != null) {
			mIntrThread.stopGracefully();
		}
		
		mThreadMonitorHandler.sendEmptyMessage(1);
    }
    
    private void monitorThread(BluetoothSocketThread thread, TextView textView) {
    	if (thread != null) {
	    	if (!thread.isAlive()) {
	    		textView.setText(thread.getName() + " stopped");
	    	} else if (thread.getConnectionState() == BluetoothSocketThread.STATE_WAITING) {
	    		textView.setText(thread.getName() + " waiting");
	    	} else if (thread.getConnectionState() == BluetoothSocketThread.STATE_ACCEPTED) {
	    		textView.setText(thread.getName() + "accepted");
	    	}
    	}
    }
    
    private Handler mThreadMonitorHandler = new  Handler() {
    	@Override
    	public void handleMessage(Message msg) {
    		
    		if (msg.what == 0) {
    			monitorThread(mCtrlThread, mCtrlTextView);
    			monitorThread(mIntrThread, mIntrTextView);
    		
    			sendEmptyMessageDelayed(0, 200);
    		}
    	}
    };
    
    /**
     * Custom ArrayAdapter
     *
     */
    private class BluetoothDeviceArrayAdapter extends ArrayAdapter<BluetoothDevice> implements SpinnerAdapter {
    	
    	// array to store the "raw" string format
    	Map<Integer, BluetoothDevice> deviceMap = null;
    	
    	/**
    	 * Constructor
    	 * @param context
    	 * @param strings
    	 */
		public BluetoothDeviceArrayAdapter(Context context, Set<BluetoothDevice> bluetoothDeviceSet) {
			super(context, R.layout.spinner_layout);
			setDropDownViewResource(R.layout.spinner_dropdown_layout);
			
			deviceMap = new HashMap<Integer, BluetoothDevice>();
			int i = 0;
			for (BluetoothDevice device:bluetoothDeviceSet ) {
				DoLog.d(TAG, "Adding " + i + " as " + device);
				deviceMap.put(Integer.valueOf(i++), device);
			}
			
		}

		@Override
		public int getCount() {
			
			return deviceMap.size();
		}

		/**
		 * Return screen-formatted value
		 */
		@Override
		public BluetoothDevice getItem(int i) {
			return deviceMap.get(Integer.valueOf(i));
		}

		@Override
		public long getItemId(int i) {
			return i;
		}
		
		/**
		 * Returns the array position. <b>item</b> must be raw-formatted.
		 */
		@Override
		public int getPosition(BluetoothDevice item) {
				
			for (int i = 0; i < deviceMap.size(); i++) {
				BluetoothDevice device = deviceMap.get(Integer.valueOf(i));
				if (device.equals(item)) {
					return i;
				}
			}
			
			return -1;
		}
		
		
		
    }; 
    	
}
package andraus.bluetoothkeybemu;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import andraus.bluetoothkeybemu.helper.BluetoothConnHelper;
import andraus.bluetoothkeybemu.helper.BluetoothConnHelperFactory;
import andraus.bluetoothkeybemu.helper.CleanupExceptionHandler;
import andraus.bluetoothkeybemu.util.DoLog;
import andraus.bluetoothkeybemu.view.BluetoothDeviceView;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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

	private static final int HANDLER_MONITOR_STOP = -1;
    private static final int HANDLER_MONITOR_SOCKET = 0;
    private static final int HANDLER_MONITOR_PAIRING = 1;
    private static final int HANDLER_CONNECT = 2;

	private static String PREF_FILE = "pref";
	private static String PREF_KEY_DEVICE = "selected_device";
	
	private static int BLUETOOTH_REQUEST_OK = 1;
	private static int BLUETOOTH_DISCOVERABLE_DURATION = 300;
	
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
	 * Register intent filters for this activity
	 */
	private void registerIntentFilters() {
        IntentFilter intentFilter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(mBluetoothReceiver, intentFilter);
	}
	
	private void populateBluetoothDeviceCombo() {
	    SharedPreferences pref = getSharedPreferences(PREF_FILE, MODE_PRIVATE);
        String storedDeviceAddr = pref.getString(PREF_KEY_DEVICE, null);
        DoLog.d(TAG, "restored from pref :" + storedDeviceAddr);
        
        Set<BluetoothDevice> deviceSet = mBluetoothAdapter.getBondedDevices();
        Set<BluetoothDeviceView> deviceViewSet = new HashSet<BluetoothDeviceView>();
        for (BluetoothDevice device: deviceSet) {
            BluetoothDeviceView deviceView = new BluetoothDeviceView(device);
            deviceViewSet.add(deviceView);
        }
        
        mBluetoothDeviceArrayAdapter = new BluetoothDeviceArrayAdapter(this, deviceViewSet);
        mBluetoothDeviceArrayAdapter.sort(BluetoothDeviceView.getComparator());
        
        int posStoredDevice = mBluetoothDeviceArrayAdapter.getPositionByAddress(storedDeviceAddr);
        
        mDeviceSpinner.setAdapter(mBluetoothDeviceArrayAdapter);
        if (posStoredDevice >= 0) {
            mDeviceSpinner.setSelection(posStoredDevice);
        }
        mDeviceSpinner.setOnItemSelectedListener(mSelectDeviceListener);
	}

	/**
	 * Customize bluetooth adapter
	 */
	private void setupBluetoothAdapter() {
        int originalClass = mConnHelper.getBluetoothDeviceClass(mBluetoothAdapter);
        DoLog.d(TAG, "original class = 0x" + Integer.toHexString(originalClass));

        int err = mConnHelper.spoofBluetoothDeviceClass(mBluetoothAdapter, 0x002540);
        DoLog.d(TAG, "set class ret = " + err);

        int sdpRecHandle = mConnHelper.addHidDeviceSdpRecord(mBluetoothAdapter);
        
        DoLog.d(TAG, "SDP record handle = " + Integer.toHexString(sdpRecHandle));
	}
	
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
        
        registerIntentFilters();
        
        if (mBluetoothAdapter.getBondedDevices().isEmpty()) {
            showNoBondedDevicesDialog();
        }
        
        populateBluetoothDeviceCombo();
        
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
				stopHidL2capSockets(false);
			}
			
		}
	};
	
	AdapterView.OnItemSelectedListener mSelectDeviceListener = 
			new AdapterView.OnItemSelectedListener() {

				@Override
				public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
					SharedPreferences pref = getSharedPreferences(PREF_FILE, MODE_PRIVATE);
					SharedPreferences.Editor editor = pref.edit();
					
					BluetoothDeviceView device = (BluetoothDeviceView) mDeviceSpinner.getSelectedItem();
					
					editor.putString(PREF_KEY_DEVICE, device.getAddress());
					editor.apply();
					
					//mThreadMonitorHandler.sendEmptyMessageDelayed(HANDLER_CONNECT, 1000 /*ms*/);
					stopHidL2capSockets(true);
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
            Toast.makeText(getApplicationContext(), mConnHelper.getSetupErrorMsg(), Toast.LENGTH_LONG).show();
            finish();
        } else {
            setupBluetoothAdapter();
        }
    }
    
    
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
    
    

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.menu_quit:
            finish();
            break;
        case R.id.menu_refresh_devices:
            populateBluetoothDeviceCombo();
            break;
            
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        DoLog.d(TAG, "...being destroyed");
        unregisterReceiver(mBluetoothReceiver);
        stopHidL2capSockets(false);
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
	
	
	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if (requestCode == BLUETOOTH_REQUEST_OK && resultCode == BLUETOOTH_DISCOVERABLE_DURATION) {
	        
	        mThreadMonitorHandler.sendEmptyMessage(HANDLER_MONITOR_PAIRING);

	    } else if (requestCode == BLUETOOTH_REQUEST_OK && resultCode == RESULT_CANCELED) {
	        finish();
	    }
	    
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void showNoBondedDevicesDialog() {
	    DialogInterface.OnClickListener bondedDialogClickListener = new DialogInterface.OnClickListener() {
            
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                
                case DialogInterface.BUTTON_NEUTRAL:
                    Intent bluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                    bluetoothIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, BLUETOOTH_DISCOVERABLE_DURATION);
                    startActivityForResult(bluetoothIntent, BLUETOOTH_REQUEST_OK);
                    break;
                }
                
            }
        };
        
	    AlertDialog dialog =  new AlertDialog.Builder(this).create();
	    dialog.setTitle(R.string.msg_dialog_no_bonded_devices_title);
	    dialog.setMessage(getResources().getString(R.string.msg_dialog_no_bonded_devices_text));
	    dialog.setButton(DialogInterface.BUTTON_NEUTRAL, getResources().getString(android.R.string.ok), bondedDialogClickListener);
	    
	    dialog.show();
	}


    private void startHidL2capSockets() {
    	Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
    	
    	if (pairedDevices.isEmpty()) {
    		DoLog.w(TAG, "no paired devices found");
    		return;
    	}
    	
    	BluetoothDevice hostDevice = ((BluetoothDeviceView) mDeviceSpinner.getSelectedItem()).getBluetoothDevice();
    	
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
    	
    	mThreadMonitorHandler.sendEmptyMessageDelayed(HANDLER_MONITOR_SOCKET, 200);
    	
    	
    }
    
    /**
     * Stop L2CAP "control" and "interrupt" channel threads
     */
    private void stopHidL2capSockets(boolean reconnect) {

		DoLog.d(TAG, "stop bt server");
		
		if (mCtrlThread != null) {
			mCtrlThread.stopGracefully();
		}
		
		if (mIntrThread != null) {
			mIntrThread.stopGracefully();
		}
		
		if (reconnect) {
		    mThreadMonitorHandler.sendEmptyMessageDelayed(HANDLER_CONNECT, 200 /*ms */);
		} else {
		    mThreadMonitorHandler.sendEmptyMessage(HANDLER_MONITOR_STOP);
		}
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
    		
    	    switch (msg.what) {
    	    case HANDLER_MONITOR_SOCKET:
    			monitorThread(mCtrlThread, mCtrlTextView);
    			monitorThread(mIntrThread, mIntrTextView);
    		
    			sendEmptyMessageDelayed(msg.what, 200 /*ms */);
    			break;
    			
    	    case HANDLER_MONITOR_PAIRING:
    	        DoLog.d(TAG, "waiting for a device to show up...");
    	        if (mBluetoothAdapter.getBondedDevices().isEmpty()) {
    	            sendEmptyMessageDelayed(msg.what, 500 /* ms */);
    	        } else {
    	            populateBluetoothDeviceCombo();
    	        }
    	        
    	        break;
    	        
    	    case HANDLER_CONNECT:
    	        startHidL2capSockets();
    	        break;
    		}
    	}
    };
    
    private final BroadcastReceiver mBluetoothReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(intent.getAction())) {
                DoLog.d(TAG, "BluetoothAdapter turned off. Bailing out...");
                finish();
            }
            
        }
        
    };
    
    /**
     * Custom ArrayAdapter
     *
     */
    private final class BluetoothDeviceArrayAdapter extends ArrayAdapter<BluetoothDeviceView> implements SpinnerAdapter {
    	
    	// array to store the "raw" string format
    	Map<Integer, BluetoothDeviceView> deviceMap = null;
    	
    	/**
    	 * Constructor
    	 * @param context
    	 * @param strings
    	 */
		public BluetoothDeviceArrayAdapter(Context context, Set<BluetoothDeviceView> bluetoothDeviceSet) {
			super(context, R.layout.spinner_layout);
			setDropDownViewResource(R.layout.spinner_dropdown_layout);
			
			deviceMap = new HashMap<Integer, BluetoothDeviceView>();
			int i = 0;
			for (BluetoothDeviceView deviceView:bluetoothDeviceSet ) {
				deviceMap.put(Integer.valueOf(i++), deviceView);
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
		public BluetoothDeviceView getItem(int i) {
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
		public int getPosition(BluetoothDeviceView item) {
				
			for (int i = 0; i < deviceMap.size(); i++) {
				BluetoothDeviceView deviceView = deviceMap.get(Integer.valueOf(i));
				if (deviceView.equals(item)) {
					return i;
				}
			}
			
			return -1;
		}
		
		public int getPositionByAddress(String bluetoothAddress) {
		    for (int i = 0; i < deviceMap.size(); i++) {
		        BluetoothDeviceView deviceView = deviceMap.get(Integer.valueOf(i));
		        if (deviceView.getAddress().equals(bluetoothAddress)) {
		            return i;
		        }
		    }
		    
		    return -1;
		}
		
    }; 
    	
}
package andraus.bluetoothhidemu;

import andraus.bluetoothhidemu.settings.BluetoothAdapterStateReceiver;
import andraus.bluetoothhidemu.settings.BluetoothDeviceStateReceiver;
import andraus.bluetoothhidemu.settings.Settings;
import andraus.bluetoothhidemu.sock.SocketManager;
import andraus.bluetoothhidemu.spoof.BluetoothAdapterSpoofer;
import andraus.bluetoothhidemu.spoof.BluetoothAdapterSpooferFactory;
import andraus.bluetoothhidemu.spoof.CleanupExceptionHandler;
import andraus.bluetoothhidemu.ui.UiControls;
import andraus.bluetoothhidemu.util.DoLog;
import andraus.bluetoothhidemu.view.BluetoothDeviceArrayAdapter;
import andraus.bluetoothhidemu.view.BluetoothDeviceView;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class BluetoothHidEmuActivity extends Activity {
	
	public static String TAG = "BluetoothHidEmu";
	
    private static final int HANDLER_MONITOR_SOCKET = 0;
    private static final int HANDLER_CONNECT = 1;
    private static final int HANDLER_BLUETOOTH_ENABLED = 2;

	private boolean mDisableBluetoothUponExit = false;
	
	private enum StatusIconStates { OFF, ON, INTERMEDIATE };
	private StatusIconStates mStatusState = StatusIconStates.OFF;
	
	private TextView mStatusTextView = null;
	private Spinner mDeviceSpinner = null;
	
    private LinearLayout mMainLayout = null;
    private UiControls mUiControls = null;

	
	private BluetoothDeviceArrayAdapter mBluetoothDeviceArrayAdapter = null;
	
	private BluetoothAdapter mBluetoothAdapter = null;
	
	private static SocketManager mSocketManager = null;
	private static BluetoothAdapterSpoofer mSpoofer = null;

	private BluetoothDeviceStateReceiver mBluetoothDeviceStateReceiver = null;
    private BluetoothAdapterStateReceiver mBluetoothAdapterStateReceiver = null;


	/**
	 * Register intent filters for this activity
	 */
	private void registerIntentFilters() {
        
        if (mBluetoothDeviceStateReceiver == null) {
            mBluetoothDeviceStateReceiver = new BluetoothDeviceStateReceiver(mDeviceSpinner);
        }
        registerReceiver(mBluetoothDeviceStateReceiver, new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED));
        if (mBluetoothAdapterStateReceiver == null) {
            mBluetoothAdapterStateReceiver = new BluetoothAdapterStateReceiver(getParent(), mSpoofer);
        }
        registerReceiver(mBluetoothAdapterStateReceiver, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));
        registerReceiver(mBluetoothAdapterStateReceiver, new IntentFilter(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED));
	}
	
	/**
	 * 
	 */
	private void setupDeviceSpinner() {
        String storedDeviceAddr = Settings.getLastConnectedDevice(getApplicationContext());
        DoLog.d(TAG, "restored from pref :" + storedDeviceAddr);
        
        if (mBluetoothDeviceArrayAdapter == null) {
            mBluetoothDeviceArrayAdapter = new BluetoothDeviceArrayAdapter(getApplicationContext());
        }
        mBluetoothDeviceArrayAdapter.rePopulate(mBluetoothAdapter.getBondedDevices());
        
        int posStoredDevice = mBluetoothDeviceArrayAdapter.getPositionByAddress(storedDeviceAddr);
        
        mDeviceSpinner.setAdapter(mBluetoothDeviceArrayAdapter);
        mDeviceSpinner.setOnItemSelectedListener(mSelectDeviceListener);

        if (posStoredDevice >= 0) {
            mDeviceSpinner.setSelection(posStoredDevice);
        } else {
            showNoBondedDevicesDialog();
        }
	}

	/**
	 * Initialize UI elements
	 * 
	 */
	private void setupApp() {
	       setContentView(R.layout.main);
	        
	        if (!mSpoofer.requirementsCheck()) {
	            Toast.makeText(getApplicationContext(), mSpoofer.getSetupErrorMsg(), Toast.LENGTH_LONG).show();
	            finish();
	        }

	        mSocketManager = SocketManager.getInstance(mSpoofer);

	        mMainLayout = (LinearLayout) findViewById(R.id.MainLayout);
	        mDeviceSpinner = (Spinner) findViewById(R.id.DeviceSpinner);
	        mStatusTextView = (TextView) findViewById(R.id.StatusTextView);
	        mStatusTextView.setShadowLayer(6, 0f, 0f, Color.BLACK);
	        
	}
	
	
	/**
	 * Updates UI
	 * @param state
	 */
	private void setStatusIconState(StatusIconStates state) {

	    if (state == mStatusState) {
	        return;
	    }
	    
        Animation animation = null;
	    switch (state) {
	    case ON:
	        if ((animation = mStatusTextView.getAnimation()) != null) {
	            animation.cancel();
	            mStatusTextView.setAnimation(null);
	        }
	        mStatusTextView.setTextColor(Color.GREEN);
	        mStatusTextView.setShadowLayer(6, 0f, 0f, Color.BLACK);
	        mStatusTextView.setText(getResources().getString(R.string.msg_status_connected));
	        
	        if (mUiControls != null) mUiControls.animate(View.VISIBLE);
	        
	        break;
	    case OFF:
            if ((animation = mStatusTextView.getAnimation()) != null) {
                animation.cancel();
                mStatusTextView.setAnimation(null);
            }
            mStatusTextView.setTextColor(Color.RED);
            mStatusTextView.setShadowLayer(6, 0f, 0f, Color.BLACK);
            mStatusTextView.setText(getResources().getString(R.string.msg_status_disconnected));
            
            if (mUiControls != null) mUiControls.animate(View.INVISIBLE);

	        break;
	    case INTERMEDIATE:
	        
	        mStatusTextView.setTextColor(0xffffff00);
	        mStatusTextView.setShadowLayer(6, 0f, 0f, Color.BLACK);
            mStatusTextView.setText(getResources().getString(R.string.msg_status_connecting));
	        
            AlphaAnimation alphaAnim = new AlphaAnimation(1, 0.2f);
            alphaAnim.setDuration(250);
            alphaAnim.setInterpolator(new DecelerateInterpolator(10f));
            alphaAnim.setRepeatCount(Animation.INFINITE);
            alphaAnim.setRepeatMode(Animation.REVERSE);
            
            mStatusTextView.startAnimation(alphaAnim);
            
            if (mUiControls != null) mUiControls.animate(View.INVISIBLE);
	        break;
	    }
	    mStatusState = state;

	}
	

    /**
     * 
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mSpoofer = BluetoothAdapterSpooferFactory.getInstance(getApplicationContext(), mBluetoothAdapter);
        
        if (mBluetoothAdapter.getScanMode() == BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            mSpoofer.tearUpSpoofing(Settings.getPrefEmulationMode(getApplicationContext()));
        }
        
        Thread.setDefaultUncaughtExceptionHandler(new CleanupExceptionHandler(mSpoofer));

        if (!mBluetoothAdapter.isEnabled()) {
            requestBluetoothAdapterOn();
        } else { 
            setupApp();
            setupDeviceSpinner();
            registerIntentFilters();
        }

    }
    
    /**
     * 
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
    
    /**
     * 
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.menu_quit:
            finish();
            break;
        case R.id.menu_settings:
            startActivity(new Intent(getApplicationContext(), Settings.class));
        }
        
        return super.onOptionsItemSelected(item);
    }

    /**
     * 
     */
    @Override
    protected void onDestroy() {
        DoLog.d(TAG, "...being destroyed");
        try {
            unregisterReceiver(mBluetoothAdapterStateReceiver);
        } catch (IllegalArgumentException e) {
            DoLog.w(TAG, "Receiver not registered - nothing done.");
        }
        try {
            unregisterReceiver(mBluetoothDeviceStateReceiver);
        } catch (IllegalArgumentException e) {
            DoLog.w(TAG, "Receiver not registered - nothing done.");
        }
        
        mMainHandler.removeCallbacksAndMessages(null);
        if (mSpoofer != null && mSpoofer.isSpoofed()) {
            mSpoofer.tearDownSpoofing();
        }

        if (mSocketManager != null) {
            stopSockets(false);
            mSocketManager.destroyThreads();
            mSocketManager = null;
        }
        
        if (mDisableBluetoothUponExit) {
            mBluetoothAdapter.disable();
        }
        
        super.onDestroy();
    }
    
    
    /**
     * 
     */
	@Override
    public void onConfigurationChanged(Configuration newConfig) {
        
	    LinearLayout connectionLayout = (LinearLayout) findViewById(R.id.ConnectionLayout);
	    
	    switch (newConfig.orientation) {
	    
	    case Configuration.ORIENTATION_LANDSCAPE:
	        connectionLayout.setVisibility(View.GONE);
	        break;
        case Configuration.ORIENTATION_PORTRAIT:
        default:
            connectionLayout.setVisibility(View.VISIBLE);
            break;
	        
	    }
	    
        super.onConfigurationChanged(newConfig);
    }

	/**
	 * 
	 */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        
        if (mUiControls != null) {
            return mUiControls.processKeyDown(keyCode, event);
        }
        
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 
     */
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        
        if (mUiControls != null) {
            return mUiControls.processKeyUp(keyCode, event);
        }
        
        return super.onKeyUp(keyCode, event);
    }

    /**
	 *
	 */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        
        if (requestCode == Settings.BLUETOOTH_REQUEST_OK && resultCode == RESULT_OK) { // bt enabled
	        
	        ProgressDialog btEnableDialog = ProgressDialog.show(getApplicationContext(), null, getResources().getString(R.string.msg_dialog_enabling_bluetooth));
	        Message msg = Message.obtain(mMainHandler, HANDLER_BLUETOOTH_ENABLED, btEnableDialog);
	        mMainHandler.sendMessageDelayed(msg, 5000 /* ms */);

	    } else if (requestCode == Settings.BLUETOOTH_REQUEST_OK && resultCode == RESULT_CANCELED) { // bt enable cancelled
	        
	        finish();
	        
	    } else if (requestCode == Settings.BLUETOOTH_REQUEST_DISCOVERABLE_FOR_PS3_OK && resultCode == Settings.BLUETOOTH_DISCOVERABLE_DURATION_5) { // discoverable for ps3

	        mSocketManager.startSockets(mBluetoothAdapter, (BluetoothDeviceView) mDeviceSpinner.getSelectedItem());
            mMainHandler.sendEmptyMessageDelayed(HANDLER_MONITOR_SOCKET, 200);
	        
	    } else if (requestCode == Settings.BLUETOOTH_REQUEST_DISCOVERABLE_FOR_PS3_OK && resultCode == RESULT_CANCELED) { // discoverable for ps3 cancelled

	        mDeviceSpinner.setSelection(((BluetoothDeviceArrayAdapter) mDeviceSpinner.getAdapter()).getNullPosition());
	        
	    }
	    
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 
     */
    private void showNoBondedDevicesDialog() {
	    DialogInterface.OnClickListener bondedDialogClickListener = new DialogInterface.OnClickListener() {
            
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                
                case DialogInterface.BUTTON_NEUTRAL:
                    
                    startActivity(new Intent(getApplicationContext(), Settings.class));
                    
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
    
    /**
     * 
     */
    private void requestBluetoothAdapterOn() {
        mDisableBluetoothUponExit = true;
        Intent bluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(bluetoothIntent, Settings.BLUETOOTH_REQUEST_OK);
    }


    /**
     * Stop L2CAP HID connections
     */
    private void stopSockets(boolean reconnect) {

		mSocketManager.stopSockets();
		
		if (reconnect) {
		    mMainHandler.sendEmptyMessageDelayed(HANDLER_CONNECT, 500 /*ms */);
		} 
    }
    
    /**
     * Check socket and connection states and update UI accordingly
     */
    private void monitorSocketStates() {
        
        if (mSocketManager == null) {
            return;
        }
        SocketManager sm = mSocketManager;

        if (sm.checkState(SocketManager.STATE_NONE) || sm.checkState(SocketManager.STATE_DROPPING)) {
            
            if (mUiControls != null) mUiControls.setControlsListeners(false);

    	} else if (sm.checkState(SocketManager.STATE_WAITING)) {

    	    mMainHandler.sendEmptyMessageDelayed(HANDLER_MONITOR_SOCKET, 1000 /*ms */);

    	} else if (sm.checkState(SocketManager.STATE_DROPPED)) {
            
            if (mUiControls != null) mUiControls.setControlsListeners(false);

            if (mStatusState != StatusIconStates.INTERMEDIATE) {
                setStatusIconState(StatusIconStates.INTERMEDIATE);
            }
            
            mMainHandler.sendEmptyMessageDelayed(HANDLER_CONNECT, 5000 /*ms */);
    	
    	} else if (sm.checkState(SocketManager.STATE_ACCEPTED)) {

    	    if (mStatusState != StatusIconStates.ON) { 
    	        setStatusIconState(StatusIconStates.ON);
    	        
    	        mUiControls.setControlsListeners(true);
    		
    	    }
    		
    		mMainHandler.sendEmptyMessageDelayed(HANDLER_MONITOR_SOCKET, 200 /*ms */);
    	}
    }
    
    /**
     * Adapter view for paired devices
     */
    AdapterView.OnItemSelectedListener mSelectDeviceListener = 
            new AdapterView.OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
                    BluetoothDeviceView device = (BluetoothDeviceView) mDeviceSpinner.getSelectedItem();
                    Settings.setLastDevice(getApplicationContext(), device.getAddress());
                    
                    mMainHandler.removeMessages(HANDLER_MONITOR_SOCKET);
                    mMainHandler.removeMessages(HANDLER_CONNECT);
                    
                    if (!device.isNull()) {
                        stopSockets(true);
                    } else {
                        stopSockets(false);
                        setStatusIconState(StatusIconStates.OFF);
                    }
                    
                    mUiControls = UiControls.setupInstance(getApplicationContext(), mSocketManager, device.getSpoofMode(), (ViewGroup) mMainLayout);
                    
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                    mMainHandler.removeMessages(HANDLER_MONITOR_SOCKET);
                    mMainHandler.removeMessages(HANDLER_CONNECT);
                    
                    stopSockets(false);
                    setStatusIconState(StatusIconStates.OFF);
                    
                    showNoBondedDevicesDialog();
                }
        
    };

    
    /**
     * Main handler to deal with UI events
     */
    private Handler mMainHandler = new  Handler() {

    	@Override
    	public void handleMessage(Message msg) {
    	    
    	    //DoLog.d(TAG, String.format("handleMessage(%d)", msg.what));
    	    
    	    switch (msg.what) {
    	    
    	    case HANDLER_BLUETOOTH_ENABLED:
    	        setupApp();
                setupDeviceSpinner();
                registerIntentFilters();
    	        ((ProgressDialog)msg.obj).dismiss();
    	        break;
    	    
    	    case HANDLER_MONITOR_SOCKET:
    	        monitorSocketStates();
    			break;
    			
    	    case HANDLER_CONNECT:
    	        setStatusIconState(StatusIconStates.INTERMEDIATE);

    	        BluetoothDeviceView deviceView = (BluetoothDeviceView) mDeviceSpinner.getSelectedItem();
    	        if (deviceView != null) {

    	            /*
    	             * PS3 hosts requires the HID device to be discoverable upon connection.
    	             * Android doesn't allow us to do this silently, so we need to request the user.
    	             */
    	            
    	            if (deviceView.isPs3() && mBluetoothAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {

    	                startActivityForResult(Settings.createBluetoothDiscoverableIntent(
    	                                                        Settings.BLUETOOTH_DISCOVERABLE_DURATION_5),
    	                                                        Settings.BLUETOOTH_REQUEST_DISCOVERABLE_FOR_PS3_OK);
    	            } else {
    	            
    	                mSocketManager.startSockets(mBluetoothAdapter, deviceView);
    	                mMainHandler.sendEmptyMessageDelayed(HANDLER_MONITOR_SOCKET, 200);
    	            }
    	        }
    	        break;
    		}
    	}
    };
       	
}
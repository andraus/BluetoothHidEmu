package andraus.bluetoothhidemu;

import andraus.bluetoothhidemu.settings.BluetoothAdapterStateReceiver;
import andraus.bluetoothhidemu.settings.BluetoothDeviceStateReceiver;
import andraus.bluetoothhidemu.settings.Settings;
import andraus.bluetoothhidemu.sock.SocketManager;
import andraus.bluetoothhidemu.sock.payload.HidPointerPayload;
import andraus.bluetoothhidemu.spoof.BluetoothAdapterSpoofer;
import andraus.bluetoothhidemu.spoof.BluetoothAdapterSpooferFactory;
import andraus.bluetoothhidemu.spoof.CleanupExceptionHandler;
import andraus.bluetoothhidemu.util.DoLog;
import andraus.bluetoothhidemu.view.BluetoothDeviceArrayAdapter;
import andraus.bluetoothhidemu.view.BluetoothDeviceView;
import andraus.bluetoothhidemu.view.EchoEditText;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.method.KeyListener;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

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
	
	private RadioGroup mTabsRadioGroup = null;
	private ViewFlipper mGenericHidViewFlipper = null;
	
	private LinearLayout mMainLayout = null;
	private LinearLayout mControlsLayout = null;
	
	private EchoEditText mEchoEditText = null;
	private ImageView mTouchpadImageView = null;
	private ImageView mLeftClickImageView = null;
	private ImageView mRightClickImageView = null;
	
	View mUpButton = null;
	View mDownButton = null;
	View mLeftButton = null;
	View mRightButton = null;
	View mEnterButton = null;
	View mEscButton = null;
	View mMediaPrevButton = null;
	View mMediaForwButton = null;
	View mMediaPlayButton = null;

	private KeyboardKeyListener mKeyboardKeyListener = null;
    private KeyboardTextWatcher mKeyboardTextWatcher = null;
    private TouchpadListener mTouchpadListener = null;
    private ButtonClickListener mLeftClickListener = null;
    private ButtonClickListener mRightClickListener = null;
    private HidPointerPayload mHidPayload;
	
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
            mBluetoothAdapterStateReceiver = new BluetoothAdapterStateReceiver(this, mSpoofer);
        }
        registerReceiver(mBluetoothAdapterStateReceiver, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));
        registerReceiver(mBluetoothAdapterStateReceiver, new IntentFilter(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED));
	}
	
	/**
	 * 
	 */
	private void setupDeviceSpinner() {
        String storedDeviceAddr = Settings.getLastConnectedDevice(this);
        DoLog.d(TAG, "restored from pref :" + storedDeviceAddr);
        
        if (mBluetoothDeviceArrayAdapter == null) {
            mBluetoothDeviceArrayAdapter = new BluetoothDeviceArrayAdapter(this);
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
	 * 
	 */
	private void setupScreenControls(BluetoothDeviceView device) {
	    
        if (mControlsLayout != null) {
            if (mControlsLayout.getVisibility() == View.VISIBLE) {
                animateControlsScreen(View.INVISIBLE);
            }
            mMainLayout.removeView(mControlsLayout);
        }

	    if (device == null) {
	        DoLog.w(TAG, "No emulated devices found");
	        return;
	    }
	    
        final LayoutInflater inflaterService = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        
        final int resId;
        switch (device.getSpoofMode()) {
        case HID_GENERIC: 
            resId = R.layout.generic_controls_layout;
            break;
        default:
            throw new IllegalStateException("Unsupported emulation mode");
        }

        mControlsLayout = (LinearLayout) inflaterService.inflate(resId, null);
        mControlsLayout.setVisibility(View.INVISIBLE);

        mMainLayout.addView(mControlsLayout);

        switch (device.getSpoofMode()) {
		case HID_GENERIC:
		    
	        mTouchpadImageView = (ImageView) findViewById(R.id.TouchpadImageView);
	        mLeftClickImageView = (ImageView) findViewById(R.id.LeftButtonImageView);
	        mRightClickImageView = (ImageView) findViewById(R.id.RightButtonImageView);
	        
	        mEchoEditText = (EchoEditText) findViewById(R.id.EchoEditText);
	        mEchoEditText.setGravity(Gravity.CENTER);
	        mEchoEditText.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);

            setupGenericHidControlTabs(true);
	        setupGenericHidButtons(true);
	        break;
		case HID_BDREMOTE:
		    
		    mTouchpadImageView = null;
		    mLeftClickImageView = null;
		    mRightClickImageView = null;
		    
		    mEchoEditText = null;
		    
		    setupGenericHidControlTabs(false);
		    setupGenericHidButtons(false);
		    break;
		}
		
	}

	/**
	 * 
	 * @param enable
	 */
    private void setupGenericHidControlTabs(boolean enable) {
        
        if (enable) { // enable nav buttons
            mTabsRadioGroup = (RadioGroup) findViewById(R.id.NavRadioGroup);
            mGenericHidViewFlipper = (ViewFlipper) findViewById(R.id.MainViewFlipper);
            
            mTabsRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    
                    switch (checkedId) {
                    case R.id.TouchpadRadioButton:
                        mGenericHidViewFlipper.setDisplayedChild(0);
                        break;
                    case R.id.NavKeysRadioButton:
                        mGenericHidViewFlipper.setDisplayedChild(1);
                        break;
                    case R.id.MediaKeysRadioButton:
                        mGenericHidViewFlipper.setDisplayedChild(2);
                        break;
                    }
                    
                }
            });
            
        } else { // disable nav buttons
            if (mTabsRadioGroup != null) {
                mTabsRadioGroup.setOnCheckedChangeListener(null);
                mTabsRadioGroup = null;
            }
            mGenericHidViewFlipper = null;
        }
        
    }

    /**
     * 
     * @param enable
     */
	private void setupGenericHidButtons(boolean enable) {
	    
	    if (enable) { // enable media keys
    	    SpecialKeyListener specialKeyListener = new SpecialKeyListener(getApplicationContext(), mSocketManager);
    	    
    	    mUpButton = (View) findViewById(R.id.UpButton);
    	    mUpButton.setOnTouchListener(specialKeyListener);
    	    mDownButton = (View) findViewById(R.id.DownButton);
            mDownButton.setOnTouchListener(specialKeyListener);
            mLeftButton = (View) findViewById(R.id.LeftButton);
            mLeftButton.setOnTouchListener(specialKeyListener);
            mRightButton = (View) findViewById(R.id.RightButton);
            mRightButton.setOnTouchListener(specialKeyListener);
            
            mEnterButton = (View) findViewById(R.id.EnterButton);
            mEnterButton.setOnTouchListener(specialKeyListener);
            mEscButton = (View) findViewById(R.id.EscButton);
            mEscButton.setOnTouchListener(specialKeyListener);
            
            mMediaPrevButton = (View) findViewById(R.id.PrevMediaButton);
            mMediaPrevButton.setOnTouchListener(specialKeyListener);
            mMediaPlayButton = (View) findViewById(R.id.PlayMediaButton);
            mMediaPlayButton.setOnTouchListener(specialKeyListener);
            mMediaForwButton = (View) findViewById(R.id.ForwardMediaButton);
            mMediaForwButton.setOnTouchListener(specialKeyListener);
            
	    } else { // disable media keys
	        if (mUpButton != null) {
	            mUpButton.setOnTouchListener(null);
	            mUpButton = null;
	        }
            if (mDownButton != null) {
                mDownButton.setOnTouchListener(null);
                mDownButton = null;
            }
            if (mLeftButton != null) {
                mLeftButton.setOnTouchListener(null);
                mLeftButton = null;
            }
            if (mRightButton != null) {
                mRightButton.setOnTouchListener(null);
                mRightButton = null;
            }
            
            if (mEnterButton != null) {
                mEnterButton.setOnTouchListener(null);
                mEnterButton = null;
            }
            if (mEscButton != null) {
                mEscButton.setOnTouchListener(null);
                mEscButton = null;
            }
            
            if (mMediaPrevButton != null) {
                mMediaPrevButton.setOnTouchListener(null);
                mMediaPrevButton = null;
            }
            if (mMediaForwButton != null) {
                mMediaForwButton.setOnTouchListener(null);
                mMediaForwButton = null;
            }
            if (mMediaPlayButton != null) {
                mMediaPlayButton.setOnTouchListener(null);
                mMediaPlayButton = null;
            }
            
	    }
	    
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
	        
	        animateControlsScreen(View.VISIBLE);
	        if (mEchoEditText != null) mEchoEditText.requestFocus();
	        
	        break;
	    case OFF:
            if ((animation = mStatusTextView.getAnimation()) != null) {
                animation.cancel();
                mStatusTextView.setAnimation(null);
            }
            mStatusTextView.setTextColor(Color.RED);
            mStatusTextView.setShadowLayer(6, 0f, 0f, Color.BLACK);
            mStatusTextView.setText(getResources().getString(R.string.msg_status_disconnected));
            
            animateControlsScreen(View.INVISIBLE);

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
            
            animateControlsScreen(View.INVISIBLE);
	        break;
	    }
	    mStatusState = state;

	}
	
	/**
	 * 
	 * @param visibility
	 */
	private void animateControlsScreen(int visibility) {

	    final int duration = 250;
	    
	    if (mControlsLayout.getVisibility() == visibility) {
	        return;
	    }
	    
	    mControlsLayout.clearAnimation();
	    if (visibility == View.VISIBLE) {
	        mControlsLayout.setVisibility(visibility);
	        //Animation animation = new AlphaAnimation(0f, 1f);
	        Animation animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF,
	                                                    0f, 
	                                                    Animation.RELATIVE_TO_SELF,
	                                                    0f,
	                                                    Animation.RELATIVE_TO_PARENT,
	                                                    1f,
	                                                    Animation.RELATIVE_TO_PARENT,
	                                                    0f);
	        animation.setDuration(duration);
	        animation.setInterpolator(new DecelerateInterpolator(1f));
	        
	        mControlsLayout.startAnimation(animation);
	        
	    } else {
	        //Animation animation = new AlphaAnimation(1f, 0f);
            Animation animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF,
									0f,
									Animation.RELATIVE_TO_SELF,
									0f,
									Animation.RELATIVE_TO_PARENT,
									0f,
									Animation.RELATIVE_TO_PARENT,
									1f);

	        animation.setDuration(duration);
            animation.setInterpolator(new AccelerateInterpolator(1f));
	        
	        mControlsLayout.startAnimation(animation);
	        mControlsLayout.setVisibility(visibility);
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
					
                    stopSockets(true);
					setupScreenControls(device);
					
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
     * 
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mSpoofer = BluetoothAdapterSpooferFactory.getInstance(getApplicationContext(), mBluetoothAdapter);
        
        if (mBluetoothAdapter.getScanMode() == BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            mSpoofer.tearUpSpoofing(Settings.getPrefEmulationMode(this));
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
            startActivity(new Intent(this, Settings.class));
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
        
        if (mEchoEditText != null && (keyCode == KeyEvent.KEYCODE_VOLUME_UP || keyCode == KeyEvent.KEYCODE_VOLUME_DOWN)) {
            KeyListener keyListener = mEchoEditText.getKeyListener();
            keyListener.onKeyDown(mEchoEditText, mEchoEditText.getEditableText(), keyCode, event);
            return true;
        }
        
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 
     */
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {

        if (mEchoEditText != null && (keyCode == KeyEvent.KEYCODE_VOLUME_UP || keyCode == KeyEvent.KEYCODE_VOLUME_DOWN)) {
            KeyListener keyListener = mEchoEditText.getKeyListener();
            keyListener.onKeyUp(mEchoEditText, mEchoEditText.getEditableText(), keyCode, event);
            return true;
        }
        
        return super.onKeyUp(keyCode, event);
    }

    /**
	 *
	 */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        
        if (requestCode == Settings.BLUETOOTH_REQUEST_OK && resultCode == RESULT_OK) { // bt enabled
	        
	        ProgressDialog btEnableDialog = ProgressDialog.show(this, null, getResources().getString(R.string.msg_dialog_enabling_bluetooth));
	        Message msg = Message.obtain(mMainHandler, HANDLER_BLUETOOTH_ENABLED, btEnableDialog);
	        mMainHandler.sendMessageDelayed(msg, 5000 /* ms */);

	    } else if (requestCode == Settings.BLUETOOTH_REQUEST_OK && resultCode == RESULT_CANCELED) { // request cancelled
	        finish();
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
            
            setControlListeners(false);

    	} else if (sm.checkState(SocketManager.STATE_WAITING)) {

    	    mMainHandler.sendEmptyMessageDelayed(HANDLER_MONITOR_SOCKET, 1000 /*ms */);

    	} else if (sm.checkState(SocketManager.STATE_DROPPED)) {
            
            setControlListeners(false);

            if (mStatusState != StatusIconStates.INTERMEDIATE) {
                setStatusIconState(StatusIconStates.INTERMEDIATE);
            }
            
            mMainHandler.sendEmptyMessageDelayed(HANDLER_CONNECT, 5000 /*ms */);
    	
    	} else if (sm.checkState(SocketManager.STATE_ACCEPTED)) {

    	    if (mStatusState != StatusIconStates.ON) { 
    	        setStatusIconState(StatusIconStates.ON);
    	        
    	        setControlListeners(true);
    		
    	    }
    		
    		mMainHandler.sendEmptyMessageDelayed(HANDLER_MONITOR_SOCKET, 200 /*ms */);
    	}
    }

    /**
     * 
     * @param enable
     */
    private void setControlListeners(boolean enable) {

        if (enable) {
            if (mHidPayload == null) {
                mHidPayload = new HidPointerPayload();
            }
            if (mTouchpadListener == null) {
                mTouchpadListener = new TouchpadListener(getApplicationContext(), mSocketManager, mLeftClickImageView, mHidPayload);                
            }
            if (mTouchpadImageView != null) mTouchpadImageView.setOnTouchListener(mTouchpadListener);
            
            if (mLeftClickListener == null) {
                mLeftClickListener = new ButtonClickListener(getApplicationContext(), mSocketManager, HidPointerPayload.MOUSE_BUTTON_1, true, mHidPayload);
            }
            if (mLeftClickImageView != null) {
                mLeftClickImageView.setOnClickListener(mLeftClickListener);
                mLeftClickImageView.setOnLongClickListener(mLeftClickListener);
            }
            if (mRightClickListener == null) {
                mRightClickListener = new ButtonClickListener(getApplicationContext(), mSocketManager, HidPointerPayload.MOUSE_BUTTON_2, false, mHidPayload);
            }
            if (mRightClickImageView != null) {
                mRightClickImageView.setOnClickListener(mRightClickListener);
                mRightClickImageView.setOnLongClickListener(mRightClickListener);
            }
            
            /*
             * EchoEditText needs both listeners below:
             * KeyboardKeyListener is used to intercept a couple of key events - enter and backspace.
             * KeyboardTextWatcher is used to intercept regular text keys.
             * 
             * I would love to only use one of them, but unfortunately, it's not reliable.
             * 
             */
            if (mKeyboardKeyListener == null) {
                mKeyboardKeyListener = new KeyboardKeyListener(mSocketManager);
            }
            if (mEchoEditText != null) mEchoEditText.setKeyListener(mKeyboardKeyListener);
            if (mKeyboardTextWatcher == null) {
                mKeyboardTextWatcher = new KeyboardTextWatcher(mSocketManager);
            }
            if (mEchoEditText != null) mEchoEditText.addTextChangedListener(mKeyboardTextWatcher);
            
            
        } else {
            mTouchpadImageView.setOnTouchListener(null);
            mLeftClickImageView.setOnClickListener(null);
            mLeftClickImageView.setOnLongClickListener(null);
            mRightClickImageView.setOnClickListener(null);
            mRightClickImageView.setOnLongClickListener(null);
            mEchoEditText.setKeyListener(null);
            mEchoEditText.removeTextChangedListener(mKeyboardTextWatcher);
        }
        
    }
    
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
    	            
    	            mSocketManager.startSockets(mBluetoothAdapter, deviceView.getBluetoothDevice());
                    mMainHandler.sendEmptyMessageDelayed(HANDLER_MONITOR_SOCKET, 200);
    	        }
    	        break;
    		}
    	}
    };
       	
}
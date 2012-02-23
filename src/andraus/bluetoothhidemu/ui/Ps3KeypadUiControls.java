package andraus.bluetoothhidemu.ui;

import andraus.bluetoothhidemu.ButtonClickListener;
import andraus.bluetoothhidemu.KeyboardKeyListener;
import andraus.bluetoothhidemu.KeyboardTextWatcher;
import andraus.bluetoothhidemu.R;
import andraus.bluetoothhidemu.SpecialKeyListener;
import andraus.bluetoothhidemu.TouchpadListener;
import andraus.bluetoothhidemu.sock.SocketManager;
import andraus.bluetoothhidemu.sock.payload.HidPointerPayload;
import andraus.bluetoothhidemu.spoof.Spoof.SpoofMode;
import andraus.bluetoothhidemu.view.EchoEditText;
import android.content.Context;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.ViewFlipper;

/**
 * Controls layout for PS3 wireless keypad emulation. 
 * Contains tabs for touchpad, navigation keys, etc.
 */
public class Ps3KeypadUiControls extends UiControls {
    
    private RadioGroup mTabsRadioGroup = null;
    private ViewFlipper mViewFlipper = null;
    
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

    private KeyboardKeyListener mKeyboardKeyListener = null;
    private KeyboardTextWatcher mKeyboardTextWatcher = null;
    private TouchpadListener mTouchpadListener = null;
    private ButtonClickListener mLeftClickListener = null;
    private ButtonClickListener mRightClickListener = null;
    private HidPointerPayload mHidPayload;
    

    /**
     * Constructor
     * 
     * @param context
     * @param socketManager
     * @param mode
     * @param mainLayout
     */
    protected Ps3KeypadUiControls(Context context, SocketManager socketManager, SpoofMode mode, ViewGroup mainLayout) {
        super(context, socketManager, mode, mainLayout);
        
        mTouchpadImageView = (ImageView) mControlsLayout.findViewById(R.id.TouchpadImageView);
        mLeftClickImageView = (ImageView) mControlsLayout.findViewById(R.id.LeftButtonImageView);
        mRightClickImageView = (ImageView) mControlsLayout.findViewById(R.id.RightButtonImageView);
        
        mEchoEditText = (EchoEditText) mControlsLayout.findViewById(R.id.EchoEditText);
        mEchoEditText.setGravity(Gravity.CENTER);
        mEchoEditText.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);
        
        setupTabs();
        setupButtons(true);
        
    }
    
    /**
     * cleanup()
     */
    @Override
    public void cleanup(ViewGroup mainLayout) {
        setupButtons(false);
        setControlsListeners(false);
        
        super.cleanup(mainLayout);
    }
    
    /**
     * animate()
     */
    @Override
    public void animate(int visibility) {
        super.animate(visibility);
        
        if (visibility == View.VISIBLE) {
            if (mEchoEditText != null) mEchoEditText.requestFocus();
        }
        
    }

    /**
     * onKeyDown()
     */
    @Override
    public boolean processKeyDown(int keyCode, KeyEvent event) {
        
        return false;
    }

    /**
     * onKeyUp()
     */
    @Override
    public boolean processKeyUp(int keyCode, KeyEvent event) {

        return false;
    }

    /**
     * setControlsListeners()
     */
    @Override
    public void setControlsListeners(boolean enable) {
        if (enable) {
            if (mHidPayload == null) {
                mHidPayload = new HidPointerPayload();
            }
            if (mTouchpadListener == null) {
                mTouchpadListener = new TouchpadListener(mContext, mSocketManager, mLeftClickImageView, mHidPayload);                
            }
            if (mTouchpadImageView != null) mTouchpadImageView.setOnTouchListener(mTouchpadListener);
            
            if (mLeftClickListener == null) {
                mLeftClickListener = new ButtonClickListener(mContext, mSocketManager, HidPointerPayload.MOUSE_BUTTON_1, true, mHidPayload);
            }
            if (mLeftClickImageView != null) {
                mLeftClickImageView.setOnClickListener(mLeftClickListener);
                mLeftClickImageView.setOnLongClickListener(mLeftClickListener);
            }
            if (mRightClickListener == null) {
                mRightClickListener = new ButtonClickListener(mContext, mSocketManager, HidPointerPayload.MOUSE_BUTTON_2, false, mHidPayload);
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
     * setupTabs()
     */
    private void setupTabs() {
        mTabsRadioGroup = (RadioGroup) mControlsLayout.findViewById(R.id.NavRadioGroup);
        mViewFlipper = (ViewFlipper) mControlsLayout.findViewById(R.id.MainViewFlipper);
        
        mTabsRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                
                switch (checkedId) {
                case R.id.TouchpadRadioButton:
                    mViewFlipper.setDisplayedChild(0);
                    break;
                case R.id.NavKeysRadioButton:
                    mViewFlipper.setDisplayedChild(1);
                    break;
                }
                
            }
        });
    }
    
    /**
     * setupButtons()
     * 
     * @param enable
     */
    private void setupButtons(boolean enable) {
        SpecialKeyListener specialKeyListener = new SpecialKeyListener(mContext, mSocketManager);
        
        if (enable) {
            mUpButton = (View) mControlsLayout.findViewById(R.id.UpButton);
            mUpButton.setOnTouchListener(specialKeyListener);
            mDownButton = (View) mControlsLayout.findViewById(R.id.DownButton);
            mDownButton.setOnTouchListener(specialKeyListener);
            mLeftButton = (View) mControlsLayout.findViewById(R.id.LeftButton);
            mLeftButton.setOnTouchListener(specialKeyListener);
            mRightButton = (View) mControlsLayout.findViewById(R.id.RightButton);
            mRightButton.setOnTouchListener(specialKeyListener);
            
            mEnterButton = (View) mControlsLayout.findViewById(R.id.EnterButton);
            mEnterButton.setOnTouchListener(specialKeyListener);
            mEscButton = (View) mControlsLayout.findViewById(R.id.EscButton);
            mEscButton.setOnTouchListener(specialKeyListener);
            
        } else {
            mUpButton.setOnTouchListener(null);
            mUpButton = null;
            mDownButton.setOnTouchListener(null);
            mDownButton = null;
            mLeftButton.setOnTouchListener(null);
            mLeftButton = null;
            mRightButton.setOnTouchListener(null);
            mRightButton = null;

            mEnterButton.setOnTouchListener(null);
            mEnterButton = null;
            mEscButton.setOnTouchListener(null);
            mEscButton = null;
            
        }
        
    }
    
}

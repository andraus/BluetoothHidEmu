package andraus.bluetoothhidemu.ui;

import andraus.bluetoothhidemu.BluetoothHidEmuActivity;
import andraus.bluetoothhidemu.R;
import andraus.bluetoothhidemu.sock.SocketManager;
import andraus.bluetoothhidemu.spoof.Spoof.SpoofMode;
import andraus.bluetoothhidemu.util.DoLog;
import android.content.Context;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;

/**
 * Abstract class to represent UI controls for a SpoofMode (emulation mode)
 * 
 * Specializing classes must be implemented for particular controls.
 */
public abstract class UiControls {
    
    private static final String TAG = BluetoothHidEmuActivity.TAG;
    
    private static UiControls mInstance = null;
    
    protected Context mContext = null;
    protected SocketManager mSocketManager = null;
    protected LinearLayout mControlsLayout = null;
    protected SpoofMode mSpoofMode = null;
    
    /**
     * setupInstance()
     * 
     * @param context - application context
     * @param socketManager
     * @param mode - emulation mode
     * @param mainLayout - layout to bind the controls to.
     * @return
     */
    public static UiControls setupInstance(Context context, SocketManager socketManager, SpoofMode mode, ViewGroup mainLayout) {
        
        DoLog.d(TAG, "UiControls: setupInstance()");
        
        if (mInstance != null) {
            mInstance.cleanup(mainLayout);
        }
        mInstance = null;

        switch (mode) {
        case HID_GENERIC:
            mInstance = new GenericUiControls(context, socketManager, mode, mainLayout);
            break;
        case HID_PS3KEYPAD:
            mInstance = new Ps3KeypadUiControls(context, socketManager, mode, mainLayout);
            break;
        default:
            throw new IllegalStateException("Invalid emulation mode");
        }
        
        return mInstance;
    }
    
    /**
     * Constructor. 
     * 
     * Provides basic setup; extending classes must construct the specifics.
     * 
     * @param context
     * @param socketManager
     * @param mode
     * @param mainLayout
     */
    protected UiControls(Context context, SocketManager socketManager, SpoofMode mode, ViewGroup mainLayout) {
        super();
        
        mContext = context;
        mSocketManager = socketManager;
        mSpoofMode = mode;
        
        final LayoutInflater inflaterService = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        
        final int res;
        switch (mode) {
        case HID_GENERIC:
            res = R.layout.generic_controls_layout;
            break;
        case HID_PS3KEYPAD:
            res = R.layout.ps3keypad_controls_layout;
            break;
        default:
            throw new IllegalStateException("Invalid emulation mode");
        }
        mControlsLayout = (LinearLayout) inflaterService.inflate(res, null);
        mControlsLayout.setVisibility(View.INVISIBLE);
        mainLayout.addView(mControlsLayout);
    }

    /**
     * cleanup()
     * 
     * Provides basic cleanup; extending classes should also perform their cleanup 
     * overriding this method.
     * 
     * @param mainLayout
     */
    public void cleanup(ViewGroup mainLayout) {
        
        DoLog.d(TAG, "UiControls: cleanup()");
        
        if (mControlsLayout != null) {
            if (mControlsLayout.getVisibility() == View.VISIBLE) {
                animate(View.INVISIBLE);
            }
            mainLayout.removeView(mControlsLayout);
        }
    }

    /**
     * animate()
     * 
     * @param visibility
     */
    public void animate(int visibility) {
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
     * Override to process key down events
     * 
     * @param keyCode
     * @param event
     * @return
     */
    public abstract boolean processKeyDown(int keyCode, KeyEvent event);
    
    /**
     * Override to process key up events
     * 
     * @param keyCode
     * @param event
     * @return
     */
    public abstract boolean processKeyUp(int keyCode, KeyEvent event);
    
    /**
     * Override to setup (enable/disable) the control listeners
     * 
     * @param enable
     */
    public abstract void setControlsListeners(boolean enable);

}

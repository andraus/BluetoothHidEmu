package andraus.bluetoothhidemu;

import andraus.bluetoothhidemu.sock.SocketManager;
import andraus.bluetoothhidemu.sock.payload.HidPointerPayload;
import andraus.bluetoothhidemu.util.DoLog;
import andraus.bluetoothhidemu.view.ViewUtils;
import android.content.Context;
import android.graphics.LightingColorFilter;
import android.os.Vibrator;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;

/**
 * 
 */
public class ButtonClickListener implements OnClickListener, OnLongClickListener {
    
    private static final String TAG = BluetoothHidEmuActivity.TAG;
    
    private SocketManager mSocketManager = null;
    
    private HidPointerPayload mHidPayload = null;
    
    private Vibrator mVibrator;
    
    private int mButton = HidPointerPayload.MOUSE_BUTTON_NONE;
    private boolean mIsLockable = true;
    
    private boolean mIsButtonLocked = false;

    /**
     * 
     * @param context
     * @param socketManager
     * @param button
     * @param isLockable
     */
    public ButtonClickListener(Context context, SocketManager socketManager, int button, boolean isLockable, HidPointerPayload hidPayload) {
        super();

        mSocketManager = socketManager;
        mHidPayload = hidPayload;
        mButton = button;
        mIsLockable = isLockable;
        
        mVibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        
    }
    

    /**
     * 
     */
    @Override
    public void onClick(View view) {
        drawButton(view,  false);
        
        view.startAnimation(ViewUtils.getClickAnimation());
        if (mVibrator != null) {
            mVibrator.vibrate(Constants.CLICK_VIBRATE_MS);
        }
        
        if (!mIsButtonLocked) {
            mHidPayload.movePointer(0, 0);
            mHidPayload.clickButton(mButton);
            mSocketManager.sendPayload(mHidPayload);
        } else {
            mIsButtonLocked = false;
        }
        mHidPayload.movePointer(0, 0);
        mHidPayload.releaseButton(mButton);
        mSocketManager.sendPayload(mHidPayload);

    }

    /**
     * 
     */
    @Override
    public boolean onLongClick(View view) {
        
        if (mIsLockable) {
            if (mIsButtonLocked) {
                onClick(view);
            } else {
                drawButton(view, true);
                mIsButtonLocked = true;
                DoLog.d(TAG, "set button locked to " + mIsButtonLocked);
                mHidPayload.movePointer(0, 0);
                mHidPayload.clickButton(mButton);
                mSocketManager.sendPayload(mHidPayload);
            }
        } else {
            onClick(view);
        }
        
        return true;
    }

    /**
     * Set properties for the button to be drawn as normal or "on hold".
     * @param view
     * @param isHold
     */
    private void drawButton(View view, boolean isHold) {
        ImageView imgView = (ImageView) view;
        if (isHold) {
            imgView.setColorFilter(new LightingColorFilter(0xff4a6c9b, 0xff000055));
        } else {
            imgView.clearColorFilter();
        }
    }

}

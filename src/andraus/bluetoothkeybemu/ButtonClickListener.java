package andraus.bluetoothkeybemu;

import andraus.bluetoothkeybemu.sock.HidProtocolManager;
import andraus.bluetoothkeybemu.sock.SocketManager;
import android.content.Context;
import android.graphics.PorterDuff.Mode;
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
    
    private static final String TAG = BluetoothKeybEmuActivity.TAG;
    
    private SocketManager mSocketManager = null;
    private int mButton = HidProtocolManager.MOUSE_BUTTON_NONE;
    
    private AnimationSet mClickAnimation = null;
    private Vibrator mVibrator;
    
    private boolean mIsButtonLocked = false;
   
    /**
     * 
     * @param socketManager
     */
    public ButtonClickListener(Context context, SocketManager socketManager, int button) {
        mSocketManager = socketManager;
        mButton = button;
        
        mClickAnimation = new AnimationSet(true);
        mClickAnimation.addAnimation(new ScaleAnimation(1f, 0.9f, 1f, 0.9f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f));
        mClickAnimation.setInterpolator(new DecelerateInterpolator(10f));
        mClickAnimation.setDuration(Constants.CLICK_VIBRATE_MS);
        
        mVibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        
    }
    

    /**
     * 
     */
    @Override
    public void onClick(View view) {
        
        ((ImageView) view).clearColorFilter();
        
        view.startAnimation(mClickAnimation);
        if (mVibrator != null) {
            mVibrator.vibrate(Constants.CLICK_VIBRATE_MS);
        }
        
        if (!mIsButtonLocked) {
            mSocketManager.sendPointerButton(mButton);
        } else {
            mIsButtonLocked = false;
        }
        mSocketManager.sendPointerButton(HidProtocolManager.MOUSE_BUTTON_NONE);

    }


    @Override
    public boolean onLongClick(View view) {
        
        if (mIsButtonLocked) {
            onClick(view);
        } else {
            ((ImageView) view).setColorFilter(0xff0000ff, Mode.MULTIPLY);
            mIsButtonLocked = true;
            mSocketManager.sendPointerButton(mButton);
        }
        
        return true;
    }

}

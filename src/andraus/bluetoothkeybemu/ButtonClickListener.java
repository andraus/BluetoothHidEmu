package andraus.bluetoothkeybemu;

import andraus.bluetoothkeybemu.sock.HidProtocolManager;
import andraus.bluetoothkeybemu.sock.SocketManager;
import android.content.Context;
import android.os.Vibrator;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.ScaleAnimation;

/**
 * 
 */
public class ButtonClickListener implements OnClickListener {
    
    private static final String TAG = BluetoothKeybEmuActivity.TAG;
    
    private SocketManager mSocketManager = null;
    private int mButton = HidProtocolManager.MOUSE_BUTTON_NONE;
    
    private AnimationSet mClickAnimation = null;
    private Vibrator mVibrator;
   
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
        
        view.startAnimation(mClickAnimation);
        if (mVibrator != null) {
            mVibrator.vibrate(Constants.CLICK_VIBRATE_MS);
        }
        mSocketManager.sendPointerEvent(mButton, 0, 0);
        mSocketManager.sendPointerEvent(HidProtocolManager.MOUSE_BUTTON_NONE, 0, 0);

    }

}

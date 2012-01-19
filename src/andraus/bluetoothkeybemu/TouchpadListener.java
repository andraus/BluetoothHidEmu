package andraus.bluetoothkeybemu;

import andraus.bluetoothkeybemu.sock.HidProtocolManager;
import andraus.bluetoothkeybemu.sock.SocketManager;
import andraus.bluetoothkeybemu.util.DoLog;
import android.content.Context;
import android.os.Vibrator;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

/**
 * 
 */
public class TouchpadListener implements OnTouchListener {
    
    private static final String TAG = BluetoothKeybEmuActivity.TAG;
    
    private float mPointerMultiplier = 1.5f;
    
    private GestureDetector mGestureDetector = null;
    private SocketManager mSocketManager = null;
    private Vibrator mVibrator = null;

    /**
     * 
     */
    private class LocalGestureDetector extends GestureDetector.SimpleOnGestureListener {
        

        @Override
        public boolean onDown(MotionEvent e) {

            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2,
                float distanceX, float distanceY) {
            
            if (e2.getX() != -distanceX || e2.getY() != -distanceY) {
                distanceX = -1 * mPointerMultiplier * distanceX;
                distanceY = -1 * mPointerMultiplier * distanceY;
                
                if (Math.abs(distanceX) > HidProtocolManager.MAX_POINTER_MOVE) {
                    distanceX = distanceX > 0 ? HidProtocolManager.MAX_POINTER_MOVE : -HidProtocolManager.MAX_POINTER_MOVE;
                }
                if (Math.abs(distanceY) > HidProtocolManager.MAX_POINTER_MOVE) {
                    distanceY = distanceY > 0 ? HidProtocolManager.MAX_POINTER_MOVE : -HidProtocolManager.MAX_POINTER_MOVE;
                }
                DoLog.d(TAG, String.format("moving(%d, %d)", (int)distanceX, (int)distanceY));
                mSocketManager.sendPointerEvent(HidProtocolManager.MOUSE_BUTTON_NONE, (int)distanceX, (int)distanceY);
                
            } else {
                return true;
            }

            return false;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            
            if (mVibrator != null) {
                mVibrator.vibrate(Constants.CLICK_VIBRATE_MS);
            }
            
            mSocketManager.sendPointerEvent(HidProtocolManager.MOUSE_BUTTON_1, 0, 0);
            mSocketManager.sendPointerEvent(HidProtocolManager.MOUSE_BUTTON_NONE, 0, 0);
            
            return super.onSingleTapConfirmed(e);
        }
        
        

    }
    
    /**
     * 
     * @param context
     * @param socketManager
     */
    public TouchpadListener(Context context, SocketManager socketManager) {
        super();
        mGestureDetector = new GestureDetector(context, new LocalGestureDetector());
        mSocketManager = socketManager;
        mVibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
    }
    
    /**
     * 
     */
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return mGestureDetector.onTouchEvent(event);
    }

}

package andraus.bluetoothhidemu;

import andraus.bluetoothhidemu.sock.SocketManager;
import andraus.bluetoothhidemu.sock.payload.HidPointerPayload;
import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

/**
 * 
 */
public class TouchpadListener implements OnTouchListener {
    
    private static final String TAG = BluetoothHidEmuActivity.TAG;
    
    private float mPointerMultiplier = 1.5f;
    
    private GestureDetector mGestureDetector = null;
    private SocketManager mSocketManager = null;
    private View mButtonView = null;
    private HidPointerPayload mHidPayload = null;

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
                
                if (Math.abs(distanceX) > HidPointerPayload.MAX_POINTER_MOVE) {
                    distanceX = distanceX > 0 ? HidPointerPayload.MAX_POINTER_MOVE : -HidPointerPayload.MAX_POINTER_MOVE;
                }
                if (Math.abs(distanceY) > HidPointerPayload.MAX_POINTER_MOVE) {
                    distanceY = distanceY > 0 ? HidPointerPayload.MAX_POINTER_MOVE : -HidPointerPayload.MAX_POINTER_MOVE;
                }
                //DoLog.d(TAG, String.format("moving(%d, %d)", (int)distanceX, (int)distanceY));
                mHidPayload.movePointer((int)distanceX, (int)distanceY);
                mSocketManager.sendPayload(mHidPayload);
                
            } else {
                return true;
            }

            return false;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            
            return super.onSingleTapConfirmed(e);
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            
            mButtonView.performClick();
            return super.onSingleTapUp(e);
        }

    }
    
    /**
     * 
     * @param context
     * @param socketManager
     */
    public TouchpadListener(Context context, SocketManager socketManager, View view, HidPointerPayload hidPayload) {
        super();
        mGestureDetector = new GestureDetector(context, new LocalGestureDetector());
        mSocketManager = socketManager;
        mButtonView = view;
        mHidPayload = hidPayload;
    }
    
    /**
     * 
     */
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return mGestureDetector.onTouchEvent(event);
    }

}

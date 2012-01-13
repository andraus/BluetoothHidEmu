package andraus.bluetoothkeybemu;

import andraus.bluetoothkeybemu.sock.SocketManager;
import andraus.bluetoothkeybemu.util.DoLog;
import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;

public class TouchpadListener implements OnTouchListener, OnClickListener {
    
    private static final String TAG = BluetoothKeybEmuActivity.TAG;

    private GestureDetector mGestureDetector = null;
    private SocketManager mSocketManager = null;
    
    private class LocalGestureDetector extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2,
                float distanceX, float distanceY) {
            DoLog.d(TAG, String.format("onScroll(%s,%s,%f, %f)", e1, e2, distanceX, distanceY));
            
            mSocketManager.sendPointerEvent((int) distanceX, (int) distanceY);
            
            return super.onScroll(e1, e2, distanceX, distanceY);
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            DoLog.d(TAG, String.format("onSingleTapConfirmed(%s)", e));
            return super.onSingleTapConfirmed(e);
        }
        
    }
    
    public TouchpadListener(Context context, SocketManager socketManager) {
        super();
        mGestureDetector = new GestureDetector(context, new LocalGestureDetector());
        mSocketManager = socketManager;
    }
    
    
    
    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        mGestureDetector.onTouchEvent(event);
        return true;
    }

}

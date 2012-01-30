package andraus.bluetoothhidemu;

import andraus.bluetoothhidemu.sock.SocketManager;
import andraus.bluetoothhidemu.sock.payload.HidKeyboardPayload;
import andraus.bluetoothhidemu.view.ViewUtils;
import android.content.Context;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class SpecialKeyListener implements OnTouchListener {

	private static final String TAG = BluetoothHidEmuActivity.TAG;
	
	private SocketManager mSocketManager = null;
	private HidKeyboardPayload mHidPayload = null;
	
	/**
	 * 
	 * @param socketManager
	 */
	public SpecialKeyListener(Context context, SocketManager socketManager) {
		
		mSocketManager = socketManager;
		mHidPayload = new HidKeyboardPayload();
		
	}

	/**
	 * 
	 */
	@Override
	public boolean onTouch(View view, MotionEvent event) {
		
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
		    view.startAnimation(ViewUtils.getClickAnimation());
			view.setPressed(true);
			mHidPayload.assemblePayload(getKeyCode(view.getId()));
			mSocketManager.sendPayload(mHidPayload);
			return true;
		case MotionEvent.ACTION_UP:
			mHidPayload.resetBytes();
			mSocketManager.sendPayload(mHidPayload);
			view.setPressed(false);
			return true;
		}
		
		return false;
		
	}
	
	/**
	 * Get the correct keycode for a given button resource id
	 * 
	 * @param resourceId
	 * @return
	 */
	private int getKeyCode(int resourceId) {
	    switch (resourceId) {
	    case R.id.UpButton:
	        return KeyEvent.KEYCODE_DPAD_UP;
	    case R.id.DownButton:
	        return KeyEvent.KEYCODE_DPAD_DOWN;
	    case R.id.LeftButton:
	        return KeyEvent.KEYCODE_DPAD_LEFT;
	    case R.id.RightButton:
	        return KeyEvent.KEYCODE_DPAD_RIGHT;
	        
	    case R.id.EnterButton:
	        return KeyEvent.KEYCODE_ENTER;
	    case R.id.EscButton:
	        return KeyEvent.KEYCODE_BACK;
	        
	    default:
	        return 0;
	    }
	    
	}

}

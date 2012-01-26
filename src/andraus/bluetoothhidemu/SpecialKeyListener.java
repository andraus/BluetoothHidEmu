package andraus.bluetoothhidemu;

import andraus.bluetoothhidemu.sock.SocketManager;
import andraus.bluetoothhidemu.sock.payload.HidKeyboardPayload;
import andraus.bluetoothhidemu.view.ArrowButton;
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
	public boolean onTouch(View v, MotionEvent event) {
		
		ArrowButton button = (ArrowButton) v;
		
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			button.setPressed(true);
			mHidPayload.assemblePayload(getKeyCode(v.getId()));
			mSocketManager.sendPayload(mHidPayload);
			return true;
		case MotionEvent.ACTION_UP:
			mHidPayload.resetBytes();
			mSocketManager.sendPayload(mHidPayload);
			button.setPressed(false);
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
	    default:
	        return 0;
	    }
	    
	}

}

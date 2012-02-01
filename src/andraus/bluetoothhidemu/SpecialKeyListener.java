package andraus.bluetoothhidemu;

import andraus.bluetoothhidemu.sock.SocketManager;
import andraus.bluetoothhidemu.sock.payload.HidConsumerPayload;
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
	
	private HidKeyboardPayload mHidKeyboardPayload = null;
	private HidConsumerPayload mHidMediaPayload = null;
	
	/**
	 * 
	 * @param socketManager
	 */
	public SpecialKeyListener(Context context, SocketManager socketManager) {
		
		mSocketManager = socketManager;
		mHidKeyboardPayload = new HidKeyboardPayload();
		mHidMediaPayload = new HidConsumerPayload();
		
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
			sendPayload(getKeyCode(view.getId()), false);
			return true;
		case MotionEvent.ACTION_UP:
		    sendPayload(getKeyCode(view.getId()), true);
			view.setPressed(false);
			return true;
		}
		
		return false;
		
	}
	
	/**
	 * 
	 * @param keyCode
	 * @param isCleanup
	 */
	private void sendPayload(int keyCode, boolean isCleanup) {
	    
	    switch (keyCode) {
	    
	    case KeyEvent.KEYCODE_DPAD_UP:
	    case KeyEvent.KEYCODE_DPAD_DOWN:
	    case KeyEvent.KEYCODE_DPAD_LEFT:
	    case KeyEvent.KEYCODE_DPAD_RIGHT:
	    case KeyEvent.KEYCODE_ENTER:
	    case KeyEvent.KEYCODE_BACK:
	        if (!isCleanup) {
	            mHidKeyboardPayload.assemblePayload(keyCode);
	        } else {
	            mHidKeyboardPayload.resetBytes();
	        }
	        mSocketManager.sendPayload(mHidKeyboardPayload);
	        break;
	        
	    case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
	        if (!isCleanup) {
	            mHidMediaPayload.set(HidConsumerPayload.USAGE_MEDIA_PLAY);
	        } else {
	            mHidMediaPayload.resetBytes();
	        }
	        mSocketManager.sendPayload(mHidMediaPayload);
	        break;
	    }
	    
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
	        
	    case R.id.PlayMediaButton:
	        return KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE;
	        
	    default:
	        return 0;
	    }
	    
	}

}

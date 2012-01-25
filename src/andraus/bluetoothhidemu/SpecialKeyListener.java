package andraus.bluetoothhidemu;

import andraus.bluetoothhidemu.sock.SocketManager;
import andraus.bluetoothhidemu.sock.payload.HidKeyboardPayload;
import android.content.Context;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;

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
		
		Button button = (Button) v;
		
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			button.setPressed(true);
			mHidPayload.assemblePayload(KeyEvent.KEYCODE_DPAD_UP);
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

}

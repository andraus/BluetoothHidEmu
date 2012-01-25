package andraus.bluetoothhidemu;

import andraus.bluetoothhidemu.sock.SocketManager;
import andraus.bluetoothhidemu.sock.payload.HidKeyboardPayload;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;

public class SpecialKeyListener implements OnClickListener {

	private SocketManager mSocketManager = null;
	private HidKeyboardPayload mHidPayload = null;
	/**
	 * 
	 * @param socketManager
	 */
	public SpecialKeyListener(SocketManager socketManager) {
		mSocketManager = socketManager;
		mHidPayload = new HidKeyboardPayload();
		
	}
	
	@Override
	public void onClick(View v) {
		mHidPayload.assemblePayload(KeyEvent.KEYCODE_DPAD_UP);
		mSocketManager.sendPayload(mHidPayload);
		mHidPayload.resetBytes();
		mSocketManager.sendPayload(mHidPayload);

	}

}

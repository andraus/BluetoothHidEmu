package andraus.bluetoothhidemu;

import andraus.bluetoothhidemu.sock.SocketManager;
import andraus.bluetoothhidemu.sock.payload.HidSixaxisPayload;
import android.view.View;
import android.view.View.OnClickListener;

public class SpecialKeyListener implements OnClickListener {

	private SocketManager mSocketManager = null;
	private HidSixaxisPayload mHidPayload = null;
	/**
	 * 
	 * @param socketManager
	 */
	public SpecialKeyListener(SocketManager socketManager) {
		mSocketManager = socketManager;
		mHidPayload = new HidSixaxisPayload();
		
	}
	
	@Override
	public void onClick(View v) {
		mHidPayload.pressCircle();
		mSocketManager.sendPayload(mHidPayload);
		mHidPayload.releaseCircle();
		mSocketManager.sendPayload(mHidPayload);

	}

}

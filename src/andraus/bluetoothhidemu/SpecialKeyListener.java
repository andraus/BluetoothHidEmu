package andraus.bluetoothhidemu;

import andraus.bluetoothhidemu.sock.SocketManager;
import andraus.bluetoothhidemu.sock.payload.HidConsumerPayload;
import andraus.bluetoothhidemu.sock.payload.HidKeyboardPayload;
import andraus.bluetoothhidemu.sock.payload.HidPayload;
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
		
	    HidPayload hidPayload = null;
		
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
		    view.startAnimation(ViewUtils.getClickAnimation());
			view.setPressed(true);
			hidPayload = getPayloadDown(view.getId());
			mSocketManager.sendPayload(hidPayload);
			return true;
		case MotionEvent.ACTION_UP:
		    hidPayload = getPayloadUp(view.getId());
            mSocketManager.sendPayload(hidPayload);
			view.setPressed(false);
			return true;
		}
		
		return false;
		
	}
	
	/**
	 * Get the correct payload for a touch down event
	 * 
	 * @param resourceId
	 * @return
	 */
	private HidPayload getPayloadDown(int resourceId) {
	    
	    switch (resourceId) {
	    case R.id.UpButton:
	        mHidKeyboardPayload.assemblePayload(KeyEvent.KEYCODE_DPAD_UP);
	        return mHidKeyboardPayload;
	    case R.id.DownButton:
	        mHidKeyboardPayload.assemblePayload(KeyEvent.KEYCODE_DPAD_DOWN);
	        return mHidKeyboardPayload;
	    case R.id.LeftButton:
	        mHidKeyboardPayload.assemblePayload(KeyEvent.KEYCODE_DPAD_LEFT);
	        return mHidKeyboardPayload;
	    case R.id.RightButton:
	        mHidKeyboardPayload.assemblePayload(KeyEvent.KEYCODE_DPAD_RIGHT);
	        return  mHidKeyboardPayload;
	        
	    case R.id.EnterButton:
	        mHidKeyboardPayload.assemblePayload(KeyEvent.KEYCODE_ENTER);
	        return mHidKeyboardPayload;
	    case R.id.EscButton:
	        mHidKeyboardPayload.assemblePayload(KeyEvent.KEYCODE_BACK);
	        return mHidKeyboardPayload;
	        
	    case R.id.PlayMediaButton:
	        mHidMediaPayload.assemble(HidConsumerPayload.USAGE_MEDIA_PLAY_PAUSE);
	        return mHidMediaPayload;
	    case R.id.PrevMediaButton:
	        mHidMediaPayload.assemble(HidConsumerPayload.USAGE_MEDIA_PREV);
	        return mHidMediaPayload;
	    case R.id.ForwardMediaButton:
	        mHidMediaPayload.assemble(HidConsumerPayload.USAGE_MEDIA_NEXT);
	        return mHidMediaPayload;
	    default:
	        return null;
	    }
	    
	}

    /**
     * Get the correct payload for a touch up
     * 
     * @param resourceId
     * @return
     */
    private HidPayload getPayloadUp(int resourceId) {
        
        switch (resourceId) {
        case R.id.UpButton:
        case R.id.DownButton:
        case R.id.LeftButton:
        case R.id.RightButton:
        case R.id.EnterButton:
        case R.id.EscButton:
            mHidKeyboardPayload.resetBytes();
            return mHidKeyboardPayload;
            
        case R.id.PlayMediaButton:
        case R.id.PrevMediaButton:
        case R.id.ForwardMediaButton:
            mHidMediaPayload.resetBytes();
            return mHidMediaPayload;
            
        default:
            return null;
        }
        
    }
	
}

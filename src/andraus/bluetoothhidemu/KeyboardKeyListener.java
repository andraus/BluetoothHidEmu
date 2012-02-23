package andraus.bluetoothhidemu;

import andraus.bluetoothhidemu.sock.SocketManager;
import andraus.bluetoothhidemu.sock.payload.HidKeyPair;
import andraus.bluetoothhidemu.sock.payload.HidKeyboardPayload;
import android.text.Editable;
import android.text.InputType;
import android.text.method.KeyListener;
import android.text.method.TextKeyListener;
import android.text.method.TextKeyListener.Capitalize;
import android.view.KeyEvent;
import android.view.View;

/**
 * KeyListener to monitor keypresses
 */
public class KeyboardKeyListener implements KeyListener {
    
    //private static final String TAG = BluetoothHidEmuActivity.TAG;
    
    private SocketManager mSocketManager = null;
    
    private TextKeyListener mTextKeyListener = null;
    
    private HidKeyboardPayload mHidPayload = new HidKeyboardPayload();
    
    public KeyboardKeyListener(SocketManager socketManager) {
        super();
        
        mTextKeyListener = new TextKeyListener(Capitalize.NONE, false);
        mSocketManager = socketManager;
        
    }

    @Override
    public void clearMetaKeyState(View view, Editable content, int states) {
        mTextKeyListener.clearMetaKeyState(view, content, states);

    }

    @Override
    public int getInputType() {
        return InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS;
    }

    @Override
    public boolean onKeyDown(View view, Editable content, int keyCode, KeyEvent event) {
        switch (keyCode) {
        
        case KeyEvent.KEYCODE_ENTER:
            TextKeyListener.clear(content);
            mHidPayload.assemblePayload(HidKeyPair.ENTER);
            mSocketManager.sendPayload(mHidPayload);
            return true;
        case KeyEvent.KEYCODE_DEL:
        	mHidPayload.assemblePayload(HidKeyPair.DEL);
            mSocketManager.sendPayload(mHidPayload);
        case KeyEvent.KEYCODE_VOLUME_UP:
        case KeyEvent.KEYCODE_VOLUME_DOWN:
            mHidPayload.assemblePayload(keyCode);
            mSocketManager.sendPayload(mHidPayload);
        default:
            return mTextKeyListener.onKeyDown(view, content, keyCode, event);
        }
    }

    @Override
    public boolean onKeyOther(View view, Editable content, KeyEvent event) {
        return mTextKeyListener.onKeyOther(view, content, event);
    }

    @Override
    public boolean onKeyUp(View view, Editable content, int keyCode, KeyEvent event) {
        
        if (keyCode == KeyEvent.KEYCODE_ENTER
                || keyCode == KeyEvent.KEYCODE_DEL
                || keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
        	mHidPayload.assemblePayload(Character.MIN_VALUE);
            mSocketManager.sendPayload(mHidPayload);
        }
        return mTextKeyListener.onKeyUp(view, content, keyCode, event);
    }

}

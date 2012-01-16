package andraus.bluetoothkeybemu;

import andraus.bluetoothkeybemu.util.DoLog;
import android.text.Editable;
import android.text.InputType;
import android.text.method.KeyListener;
import android.text.method.TextKeyListener;
import android.text.method.TextKeyListener.Capitalize;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;

public class KeyboardKeyListener implements KeyListener {
    
    private static final String TAG = BluetoothKeybEmuActivity.TAG;
    
    private TextKeyListener mTextKeyListener = null;
    
    public KeyboardKeyListener() {
        super();
        
        mTextKeyListener = new TextKeyListener(Capitalize.NONE, false);
        
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
        DoLog.d(TAG, String.format("onkeyDown(%d)",keyCode));
        switch (keyCode) {
        
        case KeyEvent.KEYCODE_ENTER:
            TextKeyListener.clear(content);
            return false;
        default:
            return mTextKeyListener.onKeyDown(view, content, keyCode, event);
        }
    }

    @Override
    public boolean onKeyOther(View view, Editable content, KeyEvent event) {
        DoLog.d(TAG, "onkeyOther()");
        return mTextKeyListener.onKeyOther(view, content, event);
    }

    @Override
    public boolean onKeyUp(View view, Editable content, int keyCode, KeyEvent event) {
        DoLog.d(TAG, "onkeyUp()");
        return mTextKeyListener.onKeyUp(view, content, keyCode, event);
    }

}

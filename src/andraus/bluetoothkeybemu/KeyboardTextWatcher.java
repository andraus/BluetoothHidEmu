package andraus.bluetoothkeybemu;

import andraus.bluetoothkeybemu.util.DoLog;
import android.text.Editable;
import android.text.TextWatcher;

public class KeyboardTextWatcher implements TextWatcher {
    
    private static final String TAG = BluetoothKeybEmuActivity.TAG;

    @Override
    public void afterTextChanged(Editable content) {
        // TODO Auto-generated method stub
        DoLog.d(TAG, String.format("afterTextChanged(%s)",content));

    }

    @Override
    public void beforeTextChanged(CharSequence charSeq, int start, int count, int after) {
        // TODO Auto-generated method stub
        DoLog.d(TAG, String.format("beforeTextChanged(%s, %d, %d, %d)", charSeq, start, count, after));

    }

    @Override
    public void onTextChanged(CharSequence charSeq, int start, int before, int count) {
        // TODO Auto-generated method stub
        DoLog.d(TAG, String.format("onTextChanged(%s, %d, %d, %d)", charSeq, start, before, count));

    }

}

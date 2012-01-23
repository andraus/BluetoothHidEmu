package andraus.bluetoothhidemu;

import andraus.bluetoothhidemu.sock.SocketManager;
import andraus.bluetoothhidemu.util.DoLog;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;

/**
 * TextWatcher to monitor text events.
 */
public class KeyboardTextWatcher implements TextWatcher {
    
    private static final String TAG = BluetoothKeybEmuActivity.TAG;
    
    private static final int MSG_START_COUNT = 0;
    
    private SocketManager mSocketManager = null;

    /**
     * 
     * @param socketManager
     */
    public KeyboardTextWatcher(SocketManager socketManager) {
        super();
        mSocketManager = socketManager;
    }
    
    @Override
    public void afterTextChanged(Editable content) {
        // TODO Auto-generated method stub
        DoLog.d(TAG, String.format("afterTextChanged(%s)",content));
        
        mHandler.removeMessages(MSG_START_COUNT);
        
        Message msg = Message.obtain(mHandler);
        msg.obj = content;
        msg.what = MSG_START_COUNT;
        
        mHandler.sendMessageDelayed(msg, 10000 /* ms */ );

    }

    @Override
    public void beforeTextChanged(CharSequence charSeq, int start, int count, int after) {
        // TODO Auto-generated method stub
        DoLog.d(TAG, String.format("beforeTextChanged(%s, %d, %d, %d)", charSeq, start, count, after));

    }

    @Override
    public void onTextChanged(CharSequence charSeq, int start, int before, int count) {
        DoLog.d(TAG, String.format("onTextChanged(%s, %d, %d, %d)", charSeq, start, before, count));
        
        if (count > 0) {
            // character added
            char character = charSeq.charAt(start);
            mSocketManager.sendChar(character);
            mSocketManager.sendChar(Character.MIN_VALUE);
        }

    }
    
    /**
     * Handler used to clean-up the local echo EditText, after some time of innactivity
     */
    private final Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            
            switch (msg.what) {
            case MSG_START_COUNT:
                
                Editable content = (Editable) msg.obj;
                content.clear();
                
                break;
                
            }
            
        }
        
    };

}

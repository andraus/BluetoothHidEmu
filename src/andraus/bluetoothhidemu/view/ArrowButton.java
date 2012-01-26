package andraus.bluetoothhidemu.view;

import andraus.bluetoothhidemu.BluetoothHidEmuActivity;
import andraus.bluetoothhidemu.R;
import andraus.bluetoothhidemu.util.DoLog;
import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.ImageButton;

public class ArrowButton extends ImageButton {
    
    public static String TAG = BluetoothHidEmuActivity.TAG;

    public ArrowButton(Context context) {
        super(context);
        
    }
    
    public ArrowButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public ArrowButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int degrees = 0;
        
        switch (getId()) {
        
        case R.id.UpButton:
            DoLog.d(TAG, "up");
            degrees = 270;
            break;
        case R.id.LeftButton:
            DoLog.d(TAG, "left");
            degrees = 180;
            break;
        case R.id.DownButton:
            DoLog.d(TAG, "down");
            degrees = 90;
            break;
        default:
            DoLog.d(TAG, "no rotation");
            break;
        
        }
        
        if (degrees != 0) {
            canvas.rotate(degrees, getWidth()/2, getHeight()/2);
            super.onDraw(canvas);
        } else {
            super.onDraw(canvas);
        }
    }

}

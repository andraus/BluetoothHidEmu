package andraus.bluetoothhidemu.view;

import andraus.bluetoothhidemu.BluetoothHidEmuActivity;
import andraus.bluetoothhidemu.R;
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
        float transX = 0;
        float transY = 0;
        
        switch (getId()) {
        
        case R.id.UpButton:
            degrees = 90;
            break;
        case R.id.RightButton:
            degrees = 180;
            break;
        case R.id.DownButton:
            degrees = 270;
            transX = -getHeight()/8;
            break;
        }
        
        if (degrees != 0) {
            canvas.rotate(degrees, getWidth()/2, getHeight()/2);
            canvas.translate(transX, transY);
            super.onDraw(canvas);
        } else {
            super.onDraw(canvas);
        }
    }

}

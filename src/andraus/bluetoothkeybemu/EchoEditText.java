package andraus.bluetoothkeybemu;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;

/**
 * Specialization of EditText. This class is used to prevent user from moving the cursor position.
 */
public class EchoEditText extends EditText {

    public EchoEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public EchoEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EchoEditText(Context context) {
        super(context);
    }

    protected void onSelectionChanged(int start, int end) {
        
        setSelection(getText().length());
    }
    
    
    
    

}

package andraus.bluetoothhidemu.view;

import andraus.bluetoothhidemu.Constants;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.ScaleAnimation;

public class ViewUtils {
    
    private static AnimationSet mClickAnimation = null;
    
    public static AnimationSet getClickAnimation() {
        
        if (mClickAnimation == null) {
            mClickAnimation = new AnimationSet(true);
            mClickAnimation.addAnimation(new ScaleAnimation(1f, 0.9f, 1f, 0.9f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f));
            mClickAnimation.setInterpolator(new DecelerateInterpolator(10f));
            mClickAnimation.setDuration(Constants.CLICK_VIBRATE_MS);
            mClickAnimation.setRepeatCount(1);
            mClickAnimation.setRepeatMode(Animation.REVERSE);

        }
        
        return mClickAnimation;
    }

}

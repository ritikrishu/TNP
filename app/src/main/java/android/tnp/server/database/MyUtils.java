package android.tnp.server.database;

import android.animation.ObjectAnimator;

/**
 * Created by ritik on 4/23/2016.
 */
public class MyUtils {
    public static void homeActivityList(HomeActivityDataAdapter.DataObjectHolder holder, boolean isDown){
        ObjectAnimator oa1 = ObjectAnimator.ofFloat(holder.itemView,"translationY",isDown?50:-50,0);
        oa1.setDuration(1000);
        oa1.start();
    }
}

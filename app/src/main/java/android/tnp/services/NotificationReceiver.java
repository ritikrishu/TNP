package android.tnp.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.g38.tnp.R;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.NotificationCompat;
import android.tnp.DAO.BeanPlacementData;
import android.tnp.DAO.CreateDB;
import android.tnp.activities.StartUp;
import android.tnp.server.database.FetchPlacementData;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by SANYAM TYAGI on 4/16/2016.
 */
public class NotificationReceiver extends BroadcastReceiver {

    Context context;
    @Override
    public void onReceive(final Context context, final Intent intent) {
        this.context=context;
        if (isNetworkAvailable()){
            storeContent();
            context.startService(new Intent(context,GetChat.class));
        }
    }

   private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager
                .getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }

    public void storeContent(){

        ArrayList<BeanPlacementData> list= new ArrayList<>();
        CreateDB obj = new CreateDB(context);
        FetchPlacementData theTask = new FetchPlacementData(list,obj.getLastId());
        Thread t = new Thread(theTask);
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if(list.size()>0){
            String msg,title;
            if(list.size()>1)
                title="New Job Posts !";
            else
                title=list.get(0).getSubject().replace("Fwd: ","");
            msg="Tap To View.";
            obj.insertData(list);
            buildNotification(title,msg);
        }
    }

    private void buildNotification(String title,String msg){
        Intent notificationIntent = new Intent(context,StartUp.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,notificationIntent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context).setSmallIcon(R.drawable.applogo)
                .setContentTitle(title).setContentText(msg).setAutoCancel(true).setContentIntent(pendingIntent)
                .setDefaults(Notification.DEFAULT_SOUND);


        NotificationManager notificationmanager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        notificationmanager.notify(0, builder.build());
    }
}
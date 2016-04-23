package android.tnp.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.g38.tnp.R;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.tnp.DAO.BeanPlacementData;
import android.tnp.DAO.CreateDB;
import android.tnp.chat.ChatMessage;
import android.tnp.server.database.FetchPlacementData;
import android.tnp.services.GetChat;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

public class StartUp extends AppCompatActivity{
    SharedPreferences sp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_up);
        sp = getSharedPreferences("CreateTable", Context.MODE_PRIVATE);
        if (!isNetworkAvailable(getApplicationContext())) {
            Toast.makeText(getApplicationContext(),"No Internet Connection.",Toast.LENGTH_LONG).show();
        }else{
            SharedPreferences sharedPreferences=getSharedPreferences("caller",MODE_PRIVATE);
            SharedPreferences.Editor editor=sharedPreferences.edit();
//            editor.putBoolean("flag",true);
//            editor.commit();
           // startService(new Intent(getApplicationContext(),GetChat.class));
            if(!(sp.getBoolean("created",false))){

//                //delete
//        ChatMessage chatMessage = new ChatMessage();
//        chatMessage.setMessage("testing");
//        chatMessage.setDate(DateFormat.getDateTimeInstance().format(new Date()));
//        chatMessage.setMe(false);
//        CreateDB createDB = new CreateDB(this);
//        createDB.insertChatData(chatMessage);
                //delete

                storeContent();
                editor = sp.edit();
                editor.putBoolean("created",true);
                editor.commit();
            }
        }
        new Thread(){
            public void run(){
                try {
                    Thread.sleep(1000);
                    if(loadData()){

                        startActivity(new Intent(StartUp.this,HomeActivity.class));
                    }

                    else
                        startActivity(new Intent(StartUp.this,Login.class));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();



    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

    private boolean loadData() {
        SharedPreferences sp = getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        boolean state=false;
        state=sp.getBoolean("login",state);
        return state;
    }

    public void storeContent(){

        ArrayList<BeanPlacementData> list= new ArrayList<>();
        CreateDB obj = new CreateDB(this);
        FetchPlacementData theTask = new FetchPlacementData(list,obj.getLastId());
        Thread t = new Thread(theTask);
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //obj.delete();
        obj.insertData(list);
    }


    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager
                .getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }

}



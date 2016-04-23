package android.tnp.activities;

import android.accounts.AccountManager;
import android.app.Dialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.g38.tnp.R;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.WorkerThread;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.tnp.DAO.BeanPlacementData;
import android.tnp.DAO.CreateDB;
import android.tnp.chat.ChatActivity;
import android.tnp.server.database.FetchPlacementData;
import android.tnp.server.database.HomeActivityDataAdapter;
import android.tnp.services.GetChat;
import android.util.Log;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.gmail.GmailScopes;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;


public class HomeActivity  extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener  , Serializable {
    GoogleAccountCredential mCredential;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    private static final String[] SCOPES = {GmailScopes.MAIL_GOOGLE_COM};
    private static final String PREF_ACCOUNT_NAME = "accountName";
    static final int REQUEST_ACCOUNT_PICKER = 1000;
    static final int REQUEST_AUTHORIZATION = 1001;
public static ImageView ivNewMsg;
    RecyclerView listView;
    SearchView searchView;
    DrawerLayout drawer;
    SharedPreferences sp;
    View headerView;
    private SwipeRefreshLayout swipeContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.inflateMenu(R.menu.home);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ivNewMsg=(ImageView)findViewById(R.id.ivNewMsg);

        //request gmail account
        SharedPreferences settings = getPreferences(Context.MODE_PRIVATE);
        mCredential = GoogleAccountCredential.usingOAuth2(
                getApplicationContext(), Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff())
                .setSelectedAccountName(settings.getString(PREF_ACCOUNT_NAME, null));
        if(settings.getString(PREF_ACCOUNT_NAME,null) == null)
            new Thread() {
                @Override
                public void run() {
                    chooseAccount();
                }
            }.start();


        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

       headerView = navigationView.inflateHeaderView(R.layout.nav_header_home);

        listView = (RecyclerView)findViewById(R.id.lvData);
        listView.setLayoutManager(new LinearLayoutManager(HomeActivity.this));
        loadData();
        final SharedPreferences sharedPreferences=getSharedPreferences("caller",MODE_PRIVATE);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ivNewMsg.setVisibility(View.GONE);
                SharedPreferences.Editor editor=sharedPreferences.edit();
                editor.putBoolean("image",false);
                editor.commit();
                startActivity(new Intent(HomeActivity.this, ChatActivity.class));
            }
        });


        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override

            public void onRefresh() {
                if (!StartUp.isNetworkAvailable(getApplicationContext())) {
                    Toast.makeText(getApplicationContext(),"No Internet Connection.",Toast.LENGTH_LONG).show();
                    swipeContainer.setRefreshing(false);
                }else {
                    storeContent();
                }


            }

        });

        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright, android.R.color.holo_green_light,
                android.R.color.holo_orange_light, android.R.color.holo_red_light);

        sp = getSharedPreferences("CreateTable", Context.MODE_PRIVATE);
        if(!(sp.getBoolean("created",false))){
            storeContent();
            SharedPreferences.Editor editor = sp.edit();
            editor.putBoolean("created",true);
            editor.commit();
        }else{
            displayData();


        }
        Log.e("activity", ""+sharedPreferences.getBoolean("image",false));




    }
    @Override
    protected void onResume() {
        super.onResume();
        LoadNewMsg loadNewMsg = new LoadNewMsg();
        loadNewMsg.execute();
        if(!(sp.getBoolean("created",false))){
            storeContent();
            SharedPreferences.Editor editor = sp.edit();
            editor.putBoolean("created",true);
            editor.commit();
        }else{
            displayData();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        else
            super.onBackPressed();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_search) {
            startActivity(new Intent(HomeActivity.this, SearchActivity.class));

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Intent newIntent=new Intent(HomeActivity.this,HomeActivity.class);
        if (id == R.id.nav_star) {
            newIntent = new Intent(HomeActivity.this,StarredActivity.class);
            startActivity(newIntent);
        }
        else  if (id == R.id.nav_home) {

            drawer.closeDrawer(Gravity.LEFT);
        }
        else  if (id == R.id.nav_query) {

            newIntent = new Intent(HomeActivity.this,ChatActivity.class);
            startActivity(newIntent);
        }
        else  if (id == R.id.nav_logout) {
            SharedPreferences sp = getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.clear().commit();
            newIntent = new Intent(HomeActivity.this,Login.class);
            startActivity(newIntent);
        }
        // startActivity(newIntent);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void loadData() {
        String name = "";
        String rollno="";
        String email="";
        SharedPreferences sp = getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        TextView tvname = (TextView)headerView.findViewById(R.id.tvName);
        tvname.setText(sp.getString("name", name));
        TextView tvroll = (TextView)headerView.findViewById(R.id.tvRoll);
        tvroll.setText(sp.getString("rollno", rollno));
        TextView tvemail = (TextView)headerView.findViewById(R.id.tvEmail);
        tvemail.setText(sp.getString("email", email));
    }

    private void storeContent(){
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
        if(list.size()>0){
            obj.insertData(list);
            displayData();
        }
        else
            swipeContainer.setRefreshing(false);

        //displayList(list);



    }
    private void displayData() {
        listView.setAdapter(new HomeActivityDataAdapter(HomeActivity.this,getLayoutInflater()));
    }
//    private void displayData() {
//        CreateDB obj = new CreateDB(this);
//        final Cursor cursor=obj.getData();
//        if(cursor.getCount()>0){
//            cursor.moveToFirst();
////            Toast.makeText(MainActivity.this,"s"+result.getString(0)+result.getString(1),Toast.LENGTH_LONG).show();
//
//            ArrayList<String> textString = new ArrayList<String>();
//            do{
//                textString.add(cursor.getString(cursor.getColumnIndex(CreateDB.SUBJECT)));
//            }while (cursor.moveToNext());
//            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
//                    android.R.layout.simple_list_item_1, android.R.id.text1, textString);
//            listView.setAdapter(adapter);
//            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                @Override
//                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                    cursor.moveToFirst();
//                    cursor.move(position);
//                    Intent intent = new Intent(HomeActivity.this, DetailsActivity.class);
//                    intent.putExtra("data", cursor.getString(cursor.getColumnIndex(CreateDB.DATA)));
//                    intent.putExtra("subject", cursor.getString(cursor.getColumnIndex(CreateDB.SUBJECT)));
//                    startActivity(intent);
//                }
//            });
//
//            swipeContainer.setRefreshing(false);
//
//
//        }
//
//    }



    protected void onActivityResult(
            int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode != RESULT_OK) {
                    isGooglePlayServicesAvailable();
                }
                break;
            case REQUEST_ACCOUNT_PICKER:
                if (resultCode == RESULT_OK && data != null &&
                        data.getExtras() != null) {
                    String accountName =
                            data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        mCredential.setSelectedAccountName(accountName);
                        SharedPreferences sharedPreferences = getSharedPreferences(PREF_ACCOUNT_NAME,MODE_PRIVATE);
                        SharedPreferences settings =
                                getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(PREF_ACCOUNT_NAME, accountName);
                        editor.apply();
                        editor = sharedPreferences.edit();
                        editor.putString(PREF_ACCOUNT_NAME, accountName);
                        editor.apply();

                    }
                } else if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(HomeActivity.this,"Account unspecified.",Toast.LENGTH_LONG).show();
                }
                break;
            case REQUEST_AUTHORIZATION:
                if (resultCode != RESULT_OK) {
                    chooseAccount();
                }
                break;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void chooseAccount(){
        startActivityForResult(
                mCredential.newChooseAccountIntent(), REQUEST_ACCOUNT_PICKER);
    }

    private boolean isGooglePlayServicesAvailable() {
        final int connectionStatusCode =
                GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (GooglePlayServicesUtil.isUserRecoverableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
            return false;
        } else if (connectionStatusCode != ConnectionResult.SUCCESS) {
            return false;
        }
        return true;
    }
    void showGooglePlayServicesAvailabilityErrorDialog(
            final int connectionStatusCode) {
        Dialog dialog = GooglePlayServicesUtil.getErrorDialog(
                connectionStatusCode,
                HomeActivity.this,
                REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();
    }
    class LoadNewMsg extends AsyncTask<Void , Void, Void>{
        SharedPreferences sharedPreferences=getSharedPreferences("caller",MODE_PRIVATE);

        @Override
        protected Void doInBackground(Void... params) {


            while(!sharedPreferences.getBoolean("servicedone",false)){
                try {
                    Thread.sleep(2000);
                    Log.e("activity", "while");
                } catch (InterruptedException e) {
                    //e.printStackTrace();
                }
            }
            //ImageView view = (ImageView)HomeActivity.this.findViewById(R.id.ivNewMsg);

            return null;
        }

        @Override
        protected void onPreExecute() {
            SharedPreferences.Editor editor=sharedPreferences.edit();
            editor.putBoolean("flag",true);
            editor.putBoolean("servicedone",false);
            editor.commit();
            startService(new Intent(HomeActivity.this, GetChat.class));
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(sharedPreferences.getBoolean("image",false)){
                ivNewMsg.setVisibility(View.VISIBLE);
            }
            else
                ivNewMsg.setVisibility(View.GONE);
            SharedPreferences.Editor editor=sharedPreferences.edit();
            editor.putBoolean("servicedone",false);
            editor.apply();
        }
    }
}
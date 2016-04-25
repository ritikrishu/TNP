package android.tnp.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.tnp.DAO.BeanPlacementData;
import android.tnp.chat.ChatActivity;
import android.tnp.server.database.StarredList;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.g38.tnp.R;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;

public class StarredActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener  , Serializable {
    SearchView searchView;
    DrawerLayout drawer;
    View headerView;
    RecyclerView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.inflateMenu(R.menu.home);



        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        listView = (RecyclerView)findViewById(R.id.lvData);
        listView.setLayoutManager(new LinearLayoutManager(StarredActivity.this));
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        headerView = navigationView.inflateHeaderView(R.layout.nav_header_home);
        loadData();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(StarredActivity.this,ChatActivity.class));
            }
        });

        final SwipeRefreshLayout swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override

            public void onRefresh() {
                swipeContainer.setRefreshing(false);
            }
        });

//        displayContent();
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            Toast.makeText(this.getBaseContext(), "Search Disabled", Toast.LENGTH_LONG).show();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Intent newIntent=new Intent(StarredActivity.this,StarredActivity.class);
        if (id == R.id.nav_star) {
            drawer.closeDrawer(Gravity.LEFT);
        }
        else  if (id == R.id.nav_home) {
            drawer.closeDrawer(Gravity.LEFT);
            newIntent = new Intent(StarredActivity.this,HomeActivity.class);
            startActivity(newIntent);
        }
        else  if (id == R.id.nav_query) {

            newIntent = new Intent(StarredActivity.this,ChatActivity.class);
            startActivity(newIntent);
        }
        else  if (id == R.id.nav_logout) {
            SharedPreferences sp = getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.clear().commit();
            newIntent = new Intent(StarredActivity.this,Login.class);
            startActivity(newIntent);
        }

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

    private void displayContent(){
        ArrayList<BeanPlacementData> list= new ArrayList<>();
        BeanPlacementData obj = new BeanPlacementData();
        SharedPreferences sp = getSharedPreferences("starred", Context.MODE_PRIVATE);
        String subject="";
        String data="";
        int i=0;
        i=sp.getInt("count",i);
        for(int k=0;k<=i;k++){
            if(!(sp.getString("subject"+k,subject).equals(""))){
                obj=new BeanPlacementData();
                obj.setSubject(sp.getString("subject"+k,subject));
                obj.setData(sp.getString("data" + k, data));
                list.add(obj);
                //str.append(sp.getString("subject"+k,subject)+"\n");
            }
        }

        displayList(list);
    }

    private void displayList(final ArrayList<BeanPlacementData> list){
        final ListView listView = (ListView) findViewById(R.id.lvData);
        ArrayList<String> textString = new ArrayList<String>();
        if(list.size()==0)
            Toast.makeText(StarredActivity.this,"Nothing Starred",Toast.LENGTH_LONG).show();
        for(int i=0;i<list.size();i++){
            textString.add(list.get(i).getSubject());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, textString);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String str = list.get(position).getData();
                Intent intent = new Intent(StarredActivity.this, DetailsActivity.class);
                intent.putExtra("id","1");
                intent.putExtra("data", str);
                intent.putExtra("subject", list.get(position).getSubject());
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
//        displayContent();
        listView.setAdapter(new StarredList(StarredActivity.this, getLayoutInflater()));
    }
}

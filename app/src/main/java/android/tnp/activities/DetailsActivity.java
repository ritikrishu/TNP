package android.tnp.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.g38.tnp.R;
import android.widget.ImageButton;
import android.widget.TextView;

public class DetailsActivity extends AppCompatActivity {


    boolean flag=true;
    Bundle extras;
    String s,sub;
    MenuItem itemStar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        TextView tvDetails =(TextView) findViewById(R.id.tvDetails);
        Intent i= getIntent();
        extras =  i.getExtras();
        s = (String) extras.get("data");
        sub = (String) extras.get("subject");
        TextView tvsubject =(TextView) findViewById(R.id.tvsubject);
        tvsubject.setText(sub);
        tvDetails.setText(s);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.details, menu);
        itemStar=menu.findItem(R.id.star);
        itemStar.setIcon(getResources().getDrawable(R.drawable.blank_star));
        loadStar();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.star) {
            checkStar();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void loadStar(){
        int c=0;
        String subject="bb";
        SharedPreferences sp = getSharedPreferences("starred", Context.MODE_PRIVATE);
        c=(sp.getInt("count",c));
        while(c>=0){

            if(sub.trim().equals(sp.getString("subject"+c,subject))){
                itemStar.setIcon(getResources().getDrawable(R.drawable.favyes));
                flag=false;
                break;
            }

            c--;
        }
    }


    public void checkStar(){

        if(flag){
            itemStar.setIcon(getResources().getDrawable(R.drawable.favyes));
            flag=false;
            addStar();
        }
        else{
            itemStar.setIcon(getResources().getDrawable(R.drawable.blank_star));
            flag=true;
            removeStar();
        }

    }

    public void addStar(){
        int i=0;
        SharedPreferences sp = getSharedPreferences("starred", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        i=sp.getInt("count",i);
        i++;
        editor.putInt("count", i);

        editor.putString("subject" + i, sub);
        editor.putString("data" + i, s);
        editor.commit();

    }

    public void removeStar(){
        int i=0;
        String subject="in";

        SharedPreferences sp = getSharedPreferences("starred", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        i=sp.getInt("count",i);
        for(int k=0;k<=i;k++){

            if(sub.trim().equals(sp.getString("subject"+k,subject))){

                editor.remove("subject"+k);
                editor.remove("data"+k);

                editor.apply();

                break;
            }
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        startActivity(intent);
        finish();

    }
}

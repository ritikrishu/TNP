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
import android.view.View;
import android.g38.tnp.R;
import android.widget.ImageButton;
import android.widget.TextView;

public class DetailsActivity extends AppCompatActivity {

    ImageButton img;
    boolean flag=true;
    Bundle extras;
    String s,sub;
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

        img= (ImageButton)findViewById(R.id.ibstar);
        img.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    checkStar();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


        int c=0;
        String subject="bb";
        SharedPreferences sp = getSharedPreferences("starred", Context.MODE_PRIVATE);
        c=(sp.getInt("count",c));
        while(c>=0){
            Log.i("remove111",subject);

            if(sub.trim().equals(sp.getString("subject"+c,subject))){
                Log.d("remove", subject);
                img.setImageResource(R.drawable.favyes);
                flag=false;
                break;
            }

            c--;
        }

    }

    private String filterContent(String str) {
        String[] data = str.split("\n");
        boolean flag = false;
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < data.length; i++) {
            if (data[i].contains("Cc:")) {
                flag = true;
                continue;
            }

            if (flag) {
                if (data[i].trim().equalsIgnoreCase("::Disclaimer::")) {
                    break;
                }
                result.append(data[i]);
                result.append("\n");
            }

        }
        return result.toString();

    }
    public void checkStar(){

        if(flag){
            img.setImageResource(R.drawable.favyes);
            flag=false;
            addStar();
        }
        else{
            img.setImageResource(R.drawable.blank_star);
            flag=true;
            removeStar();
        }

    }

    public void addStar(){
        int i=0;
        String testStr="";
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
        Log.i("remove111",subject);
        SharedPreferences sp = getSharedPreferences("starred", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        Log.i("remove111",sp.getString("subject",subject));
        i=sp.getInt("count",i);
        for(int k=0;k<=i;k++){
            Log.i("remove111",sp.getString("subject"+k,subject));
            if(sub.trim().equals(sp.getString("subject"+k,subject))){
                Log.i("subjectdel", sp.getString("subject" + k, subject));
                editor.remove("subject"+k);
                editor.remove("data"+k);

                editor.apply();
//
//                editor.clear();
//                editor.commit();

                break;
            }
        }
    }

}

package android.tnp.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.tnp.DAO.BeanUserData;
import android.tnp.services.LoginCheck;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.g38.tnp.R;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

public class Login extends AppCompatActivity  {

    private EditText rollno;
    private EditText password;
    private Button login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        login=(Button)findViewById(R.id.bLogin);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rollno=(EditText)findViewById(R.id.tRollno);
                password=(EditText)findViewById(R.id.tPassword);
                if(rollno.getText().toString().length()==0||password.getText().toString().length()==0)
                    Toast.makeText(Login.this, "Can not be Empty", Toast.LENGTH_SHORT).show();
                else {
                    if(check(rollno.getText().toString(),password.getText().toString())){
                        startActivity(new Intent("android.tnp.activities.HomeActivity"));
                    }
                    else{
                        rollno=(EditText)findViewById(R.id.tRollno);
                        rollno.setText("");
                        password=(EditText)findViewById(R.id.tPassword);
                        password.setText("");
                    }
                }
            }
        });
    }

    private boolean check(String rollno,String password){
        BeanUserData obj = new BeanUserData();
        ArrayList<BeanUserData> list= new ArrayList<>();
        obj.setRollno(rollno);
        obj.setPassword(password);
        LoginCheck lc = new LoginCheck(obj,list);
        Thread t = new Thread(lc);
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if(list.size()>0){
            setCredentials(list);
            return true;
        }

        else {
            Toast.makeText(Login.this, "Invalid Details.", Toast.LENGTH_LONG).show();

        }
        return false;
    }

    void setCredentials(ArrayList<BeanUserData> list){
        BeanUserData obj=list.get(0);
        SharedPreferences sp = getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("login",true);
        editor.putString("name", obj.getName());
        editor.putString("rollno", obj.getRollno());
        editor.putString("email", obj.getEmail());
        editor.commit();

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater mymenu = getMenuInflater();
        mymenu.inflate(R.menu.signup, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        startActivity(new Intent("android.tnp.activities.SignUp"));
        return false;
    }

}
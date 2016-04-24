package android.tnp.activities;

import android.content.Intent;
import android.g38.tnp.R;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.tnp.DAO.BeanUserData;
import android.tnp.server.database.CheckForRegisteredUser;
import android.tnp.server.database.Insert;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

public class SignUp extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private EditText name;
    private EditText email;
    private EditText phone;
    private EditText rollno;
    private EditText password;
    private EditText confirmPassword;
    private Spinner branch;
    private Spinner sec;
    private Button submit;
    private String sBranch, sSec;
    private BeanUserData obj = new BeanUserData();
    private ImageView image;
    private boolean submitFlag=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);


        branch = (Spinner) findViewById(R.id.sBranch);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.branch_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        branch.setAdapter(adapter);
        branch.setOnItemSelectedListener(this);

        sec = (Spinner) findViewById(R.id.sSec);
        ArrayAdapter<CharSequence> adapterSec = ArrayAdapter.createFromResource(this,
                R.array.sec_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sec.setAdapter(adapterSec);
        sec.setOnItemSelectedListener(this);

        name = (EditText) findViewById(R.id.etName);
        email = (EditText) findViewById(R.id.etEmail);
        phone = (EditText) findViewById(R.id.etPhone);
        rollno = (EditText) findViewById(R.id.etRollno);
        password = (EditText) findViewById(R.id.etPassword);
        confirmPassword = (EditText) findViewById(R.id.etConfirmPassword);
        submit = (Button) findViewById(R.id.bSubmit);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!(StartUp.isNetworkAvailable(getApplicationContext()))){
                    Toast.makeText(getApplicationContext(),"No Internet Access.",Toast.LENGTH_LONG).show();
                    return;
                }
                validatePhone();
                if(submitFlag){

                    try {

                        addData();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(SignUp.this, "Registration sucessful.\nContinue By Loging In", Toast.LENGTH_LONG).show();
                    Thread thread = new Thread() {
                        public void run() {
                            try {
                                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }

                    };
                    thread.start();

                }
                else
                    Toast.makeText(SignUp.this, "Complete The Form", Toast.LENGTH_LONG).show();
            }

        });


        //focusListensers

        name.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    validateName();

                }

            }
        });

        rollno.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    validateRollno();

                }
            }
        });

        email.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    validateEmail();

                }
            }
        });

        password.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    validatePassword();

                }
            }
        });

        confirmPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    validateConfirmPassword();

                }
            }
        });

        phone.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    validatePhone();

                }
            }
        });


    }


    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        if(parent.getItemAtPosition(pos).toString().contains("E"))
            sBranch=parent.getItemAtPosition(pos).toString();
        else
            sSec=parent.getItemAtPosition(pos).toString();
    }

    public void onNothingSelected(AdapterView<?> parent) {
        if(parent.getItemAtPosition(0).toString().contains("E"))
            sBranch=parent.getItemAtPosition(0).toString();
        else
            sSec=parent.getItemAtPosition(0).toString();
    }

    public void addData() throws InterruptedException {
        obj.setName(name.getText().toString());
        obj.setEmail(email.getText().toString());
        obj.setPhone(phone.getText().toString());
        obj.setRollno(rollno.getText().toString());
        obj.setPassword(password.getText().toString());
        obj.setBranch(sBranch);
        obj.setSec(sSec);
        Insert insert = new Insert(obj);
        Thread t = new Thread(insert);
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // TODO Auto-generated method stub
        super.onCreateOptionsMenu(menu);
        MenuInflater mymenu = getMenuInflater();
        mymenu.inflate(R.menu.login, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        startActivity(new Intent("android.tnp.activities.Login"));
        return false;
    }

//    validations

    void validateName(){
        image=(ImageView)findViewById(R.id.ivName);

        if(name.getText().toString().length()==0){
            image.setImageResource(R.drawable.error);
            submitFlag=false;
            return;
        }
        char[] arr = name.getText().toString().toCharArray();
        for (int i = 0; i < arr.length; i++)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                if (!(Character.isAlphabetic(arr[i]))){
                    image.setImageResource(R.drawable.error);
                    submitFlag=false;
                    return;
                }
            }
        image.setImageResource(R.drawable.ok);
        submitFlag=true;
    }

    void validateRollno(){
        image=(ImageView)findViewById(R.id.ivRollno);
        if(!(rollno.getText().toString().length()==9)){
            image.setImageResource(R.drawable.error);
            submitFlag=false;
            return;
        }
        if(duplicateRoll()){
            image.setImageResource(R.drawable.error);
            submitFlag=false;
            Toast.makeText(SignUp.this, "Roll Number Already Registered.", Toast.LENGTH_LONG).show();
            return;
        }
        image.setImageResource(R.drawable.ok);
        submitFlag=true;
    }

    void validateEmail(){
        image=(ImageView)findViewById(R.id.ivEmail);
        if(!(email.getText().toString().contains("@")&&email.getText().toString().contains(".com"))){
            image.setImageResource(R.drawable.error);
            submitFlag=false;
            return;
        }
        image.setImageResource(R.drawable.ok);
        submitFlag=true;
    }

    void validatePhone(){
        image=(ImageView)findViewById(R.id.ivPhone);
        if(!(phone.getText().toString().length()==10)){
            image.setImageResource(R.drawable.error);
            submitFlag=false;
            return;
        }
        image.setImageResource(R.drawable.ok);
        submitFlag=true;
    }

    void validatePassword(){
        image=(ImageView)findViewById(R.id.ivPassword);
        if(!(password.getText().toString().length()>5)){
            image.setImageResource(R.drawable.error);
            submitFlag=false;
            return;
        }
        image.setImageResource(R.drawable.ok);
        submitFlag=true;
    }

    void validateConfirmPassword(){
        image=(ImageView)findViewById(R.id.ivConfirmPassword);
        if(!(confirmPassword.getText().toString().equals(password.getText().toString())&&confirmPassword.getText().toString().length()>0)){
            image.setImageResource(R.drawable.error);
            submitFlag=false;
            return;
        }
        image.setImageResource(R.drawable.ok);
        submitFlag=true;
    }

    boolean duplicateRoll(){
        BeanUserData obj = new BeanUserData();
        ArrayList<BeanUserData> list= new ArrayList<>();
        obj.setRollno(rollno.getText().toString());
        CheckForRegisteredUser lc = new CheckForRegisteredUser(obj,list);
        Thread t = new Thread(lc);
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if(list.size()>0){
            return true;
        }

        return false;
    }

}



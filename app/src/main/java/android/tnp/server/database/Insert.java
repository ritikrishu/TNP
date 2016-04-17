package android.tnp.server.database;

import android.tnp.DAO.BeanUserData;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by SANYAM TYAGI on 4/15/2016.
 */
public class Insert extends Thread{

    BeanUserData obj=new BeanUserData();
    public  Insert(BeanUserData obj){
        this.obj=obj;
    }
    @Override
    public void run() {
        super.run();
        insert();
    }

    public void insert() {
        {
            try {
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost("http://ritikrishu.com/insert.php"); //YOUR PHP SCRIPT ADDRESS
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                nameValuePairs.add(new BasicNameValuePair("name", obj.getName()));
                nameValuePairs.add(new BasicNameValuePair("rollno", obj.getRollno()));
                nameValuePairs.add(new BasicNameValuePair("email", obj.getEmail()));
                nameValuePairs.add(new BasicNameValuePair("phone", obj.getPhone()));
                nameValuePairs.add(new BasicNameValuePair("branch", obj.getBranch()));
                nameValuePairs.add(new BasicNameValuePair("sec", obj.getSec()));
                nameValuePairs.add(new BasicNameValuePair("password", obj.getPassword()));

                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                // httppost.setEntity(new UrlEncodedFormEntity());
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity entity = response.getEntity();

            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
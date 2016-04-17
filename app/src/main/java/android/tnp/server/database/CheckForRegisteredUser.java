package android.tnp.server.database;

import android.tnp.DAO.BeanUserData;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by SANYAM TYAGI on 2/7/2016.
 */
public class CheckForRegisteredUser extends Thread{


    BeanUserData obj = new BeanUserData();
    boolean flag;
    ArrayList<BeanUserData> list = new ArrayList<>();

    public CheckForRegisteredUser(BeanUserData obj, ArrayList<BeanUserData> list){
        this.obj=obj;
        this.list=list;
    }

    @Override
    public void run() {
        super.run();
        Log.d("hello", "run called");
        getData();
    }

    private void getData(){
        InputStream isr = null;
        String result="";
        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://ritikrishu.com/fetchrollnum.php"); //YOUR PHP SCRIPT ADDRESS
            List<NameValuePair> nameValuePairs = new ArrayList<>();
            nameValuePairs.add(new BasicNameValuePair("rollno", obj.getRollno()));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();
            isr = entity.getContent();
        } catch (Exception e) {
            Log.e("log_tag", "Error in http connection " + e.toString());

        }

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(isr, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            isr.close();
            Log.d("hello", "isr close");

            result = sb.toString();
            if(result.length()==0||result.contains("null"))
                return;

        } catch (Exception e) {
            Log.e("log_tag", "Error converting result " + e.toString());
        }
        Log.e("log_tag",""+result.length()+result+"in");
        //parse json data
        try {
            JSONArray jArray = new JSONArray(result);
            for (int i = 0; i < jArray.length(); i++) {
                JSONObject json = jArray.getJSONObject(i);
                obj.setName(json.getString("name"));
                obj.setRollno(json.getString("rollno"));
                obj.setEmail(json.getString("email"));
                list.add(obj);
            }


        } catch (Exception e) {

            Log.e("log_tag", "Error Parsing Data " + e.toString());
        }
    }
}


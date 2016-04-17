package android.tnp.server.database;

import android.tnp.DAO.BeanPlacementData;
import android.tnp.DAO.CreateDB;
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
 *  Created by SANYAM TYAGI on 2/10/2016.
 */

public class FetchPlacementData extends Thread{
    ArrayList<BeanPlacementData> list=new ArrayList<>();
    String id;

    public FetchPlacementData(ArrayList<BeanPlacementData> list,String id) {
        this.list = list;
        this.id=id;
    }

    @Override
    public void run() {
        super.run();
        Log.d("hello", "run called");
        getData();
    }

    public void getData() {
        Log.d("hello","debug called");
        String result = "";
        InputStream isr = null;
        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://ritikrishu.com/fetchplacementdata.php"); //YOUR PHP SCRIPT ADDRESS
            List<NameValuePair> nameValuePairs = new ArrayList<>();
            nameValuePairs.add(new BasicNameValuePair("id", ""+id));
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
        } catch (Exception e) {
            Log.e("log_tag", "Error converting result " + e.toString());
        }

        try {
            JSONArray jArray = new JSONArray(result);
            BeanPlacementData obj= new BeanPlacementData();
            for (int i = 0; i < jArray.length(); i++) {
                JSONObject json = jArray.getJSONObject(i);
                obj= new BeanPlacementData();
                obj.setNum(json.getInt("num"));
                obj.setData(json.getString("data"));
                obj.setSubject(json.getString("subject"));
                list.add(obj);
                Log.d("check", obj.getData());
            }

            Log.d("s",list.get(0).getData());


        } catch (Exception e) {

            Log.e("log_tag", "Error Parsing Data " + e.toString());
        }
    }
}


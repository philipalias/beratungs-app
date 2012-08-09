package com.pages.beratungsapp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;

public class DBconnection extends Activity {
	
	TextView tv;
	String text;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        if (android.os.Build.VERSION.SDK_INT > 9) {
        	StrictMode.ThreadPolicy policy = 
        		new StrictMode.ThreadPolicy.Builder().permitAll().build();
        	StrictMode.setThreadPolicy(policy);
        }
        
        //tv 	= (TextView)findViewById(R.id.textView1);
		text 	= "";
        
		postData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    
    public void postData() {  
		// Create a new HttpClient and Post Header
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost("http://jd.mazebert.com/dbService.php");
		//HttpPost httppost = new HttpPost("http://jaydee85.ja.funpic.de/app/dbService.php");
		//HttpPost httppost = new HttpPost("http://jaydee.bplaced.net/app/dbService.php");
		//HttpPost httppost = new HttpPost("http://jaydee.site11.com/app/dbService.php");
		
		
		
		JSONObject json = new JSONObject();

		try {
			// JSON data:
			json.put("method", "klientRequest");
			json.put("name", "Max");

			
			//JSONArray postjson=new JSONArray();
			//postjson.put(json);
			
			
			// Create a NameValuePair out of the JSONObject + a name
            List<NameValuePair> nVP = new ArrayList<NameValuePair>();  
            nVP.add(new BasicNameValuePair("jsonPost", json.toString()));  

            // Hand the NVP to the POST
            httppost.setEntity(new UrlEncodedFormEntity(nVP));
            Log.i("main", "TestPOST - nVP = "+nVP.toString());

			

			// Post the data:
			//httppost.setHeader("json",json.toString());
			//httppost.getParams().setParameter("jparam",postjson);

			// Execute HTTP Post Request
			System.out.print(json);
			HttpResponse response = httpclient.execute(httppost);
			Log.e("HTTPresponse", response.toString());

			// for JSON:
			if(response != null)
			{
				Log.e("RESPONSE", "RESPONSE not NULL");
				InputStream is = response.getEntity().getContent();
				Log.e("InputSTREAM is", is.toString());

				text = convertStreamToString(is);
			}
			tv.setText(text);
			Log.e("TEXT View", "ENDE");

		}catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
		} catch (IOException e) {
    		// TODO Auto-generated catch block
    	} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public static String convertStreamToString(InputStream is) {
        /*
         * To convert the InputStream to String we use the BufferedReader.readLine()
         * method. We iterate until the BufferedReader return null which means
         * there's no more data to read. Each line will appended to a StringBuilder
         * and returned as String.
         */

        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }    
}
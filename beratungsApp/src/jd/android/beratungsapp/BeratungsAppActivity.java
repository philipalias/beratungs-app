package jd.android.beratungsapp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.net.ParseException;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

public class BeratungsAppActivity extends Activity {
    /** Called when the activity is first created. */
	
	int TIMEOUT_MILLISEC = 10000; // = 10 seconds
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        //clickbuttonRecieve();
        clickbuttonRecieve2();
    }
    
    
    public void clickbuttonRecieve() {
			
    		JSONObject data = new JSONObject();
			//JSONObject jsonParams = new JSONObject();
			//jsonParams.put("klientId", "1");
			try {
				data.put("sa", "ssa");
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			//jsonMethod.put("params",jsonParams);
			Log.e("beratungAPP", data.toString());
			
			
			/*HttpParams httpParams = new BasicHttpParams();
			httpParams.setParameter("data", jsonMethod.toString());
			HttpConnectionParams.setConnectionTimeout(httpParams,TIMEOUT_MILLISEC);
			HttpConnectionParams.setSoTimeout(httpParams, TIMEOUT_MILLISEC);
			HttpClient client = new DefaultHttpClient(httpParams);
			Log.e("beratungAPP", httpParams.toString());*/
			//
			//String url = "http://10.0.2.2:8080/sample1/webservice2.php?json={\"UserName\":1,\"FullName\":2}";
			String url = "http://jaydee85.ja.funpic.de/app/dbService.php";
			Log.e("beratungAPP", url);
			
			// Build the JSON object to pass parameters
			// Create the POST object and add the parameters
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(url);
			StringEntity entity = null;
			
			try {
				entity = new StringEntity(data.toString(), HTTP.UTF_8);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			entity.setContentType("application/json");
			httpPost.setEntity(entity);
			
			// Execute HTTP Post Request
	        HttpResponse response = null;
			try {
				response = httpclient.execute(httpPost);
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        Log.e("beratungAPP: Response", response.toString());
    }
			
			/* // Create a new HttpClient and Post Header
		    HttpClient httpclient = new DefaultHttpClient();
		    HttpPost httppost = new HttpPost(url);

		    try {
		        // Add your data
		    	HashMap<String, String> params = new HashMap<String, String>();

		        // adding each child node to HashMap key => value
		        params.put("method", "klientRequest");

		        // Execute HTTP Post Request
		        HttpResponse response = httpclient.execute(httppost);
		        Log.e("beratungAPP", response.toString());
		        
		    } catch (ClientProtocolException e) {
		        // TODO Auto-generated catch block
		    } catch (IOException e) {
		        // TODO Auto-generated catch block
		    }
	}*/
    
    public void clickbuttonRecieve2() {
        try {
            JSONObject data = new JSONObject();
            data.put("method", "klientRequest");
            data.put("params", "klientId");
            HttpParams httpParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParams,TIMEOUT_MILLISEC);
            HttpConnectionParams.setSoTimeout(httpParams, TIMEOUT_MILLISEC);
            HttpClient client = new DefaultHttpClient(httpParams);
            
            String url = "http://jaydee85.ja.funpic.de/app/dbService.php";

            HttpPost request = new HttpPost(url);
            request.setEntity(new ByteArrayEntity(data.toString().getBytes("UTF8")));
            request.setHeader("data", data.toString());
            HttpResponse response = client.execute(request);
            HttpEntity entity = response.getEntity();
            
            
            // If the response does not enclose an entity, there is no need
            if (entity != null) {
                InputStream instream = entity.getContent();

                String result = convertStreamToString(instream);
                Log.e("Read from server", result);
                Toast.makeText(this,  result, Toast.LENGTH_LONG).show();
            }
        } catch (Throwable t) {
            Toast.makeText(this, "Request failed: " + t.toString(), Toast.LENGTH_LONG).show();
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
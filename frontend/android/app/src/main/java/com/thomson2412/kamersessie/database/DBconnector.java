package com.thomson2412.kamersessie.database;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;

/**
 * Created by Thomas on 4-10-2015.
 */
public class DBconnector {
    public JSONObject postUrlResponse(String myurl, String params) throws IOException {
        String urlParameters = params;
        //URLEncoder.encode(urlParameters, "UTF-8");
        byte[] postData = urlParameters.getBytes( Charset.forName("UTF-8") );
        int postDataLength = postData.length;

        InputStream is = null;
        // Only display the first 500 characters of the retrieved
        // web page content.
        int len = 500;


        try {
            URL url = new URL(myurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("charset", "utf-8");
            conn.setRequestProperty("Content-Length", Integer.toString(postDataLength));
            conn.setUseCaches(false);
            try{
                DataOutputStream wr = new DataOutputStream( conn.getOutputStream());
                wr.write( postData );
            } catch (EOFException e) {
                e.printStackTrace();
            }
            // Starts the query
            conn.connect();
            int response = conn.getResponseCode();
            Log.d("Response", "The responsecode is: " + response);
            is = conn.getInputStream();

            // Convert the InputStream into a string
            String result = readIt(is, len);
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(result);
            } catch (JSONException e){
                e.printStackTrace();
            }

            return jsonObject;


            // Makes sure that the InputStream is closed after the app is
            // finished using it.
        } catch (ConnectException e) {
            Log.d("DBConnector", "No connection");
            return null;
        }
        finally {
            if (is != null) {
                is.close();
            }
        }

    }
    public String readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException {
        Reader reader = null;
        reader = new InputStreamReader(stream, "UTF-8");
        char[] buffer = new char[len];
        reader.read(buffer);
        return new String(buffer);
    }
}

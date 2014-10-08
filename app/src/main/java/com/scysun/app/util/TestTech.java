package com.scysun.app.util;


import android.util.Log;

import com.scysun.app.core.Constants;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.DefaultHttpRequestFactory;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static com.scysun.app.core.Constants.Http.HEADER_PARSE_APP_ID;
import static com.scysun.app.core.Constants.Http.HEADER_PARSE_REST_API_KEY;
import static com.scysun.app.core.Constants.Http.PARSE_APP_ID;
import static com.scysun.app.core.Constants.Http.PARSE_REST_API_KEY;

public class TestTech
{
    public static void testParseAPIAuth() throws Exception
    {
        String url = Constants.Http.URL_AUTH;
        HttpClient httpClient = new DefaultHttpClient();

        // Prepare a request object
        HttpGet httpget = new HttpGet(url);
        httpget.addHeader(HEADER_PARSE_REST_API_KEY, PARSE_REST_API_KEY);
        httpget.addHeader(HEADER_PARSE_APP_ID, PARSE_APP_ID);

        httpget.getParams().setParameter(Constants.Http.PARAM_USERNAME, "demo@androidbootstrap.com");
        httpget.getParams().setParameter(Constants.Http.PARAM_PASSWORD, "android");

        // Execute the request
        HttpResponse response;
        try {
            response = httpClient.execute(httpget);
            // Examine the response status
            Log.i("Praeda", response.getStatusLine().toString());

            // Get hold of the response entity
            HttpEntity entity = response.getEntity();
            // If the response does not enclose an entity, there is no need
            // to worry about connection release

            if (entity != null) {

                // A Simple JSON Response Read
                InputStream instream = entity.getContent();
                String result= convertStreamToString(instream);
                // now you have the string representation of the HTML request
                instream.close();
            }


        } catch (Exception e) {
            throw(e);
        }
    }
    private static String convertStreamToString(InputStream is) {
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

    public static void main(String[] args)
    {
        try {
            testParseAPIAuth();
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
    }
}

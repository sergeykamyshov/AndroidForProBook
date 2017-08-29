package com.kamyshovcorp.photogallery;

import android.net.Uri;
import android.util.Log;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class FlickrFetchr {

    public static final String TAG = "FlickrFetchr";
    public static final String API_KEY = "14013a8302dbb9fe20d2662c538f09eb";

    public byte[] getUrlBytes(String urlSpec) throws IOException {
        URL url = new URL(urlSpec);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        try {
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new IOException(connection.getResponseMessage() + ": with " + urlSpec);
            }

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder builder = new StringBuilder();
            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
                builder.append(line);
            }
            return builder.toString().getBytes();

//            ByteArrayOutputStream out = new ByteArrayOutputStream();
//            InputStream in = connection.getInputStream();
//            int bytesRead = 0;
//            byte[] buffer = new byte[1024];
//            while ((bytesRead = in.read()) > 0) {
//                out.write(buffer, 0, bytesRead);
//            }
//            out.close();
//            return out.toByteArray();
        } finally {
            connection.disconnect();
        }
    }

    public String getUrlString(String urlSpec) throws IOException {
        return new String(getUrlBytes(urlSpec));
    }

    public void fetchItems() {
        try {
            String url = Uri.parse("https://api.flickr.com/services/rest/")
                    .buildUpon()
                    .appendQueryParameter("method", "flickr.photos.getRecent")
                    .appendQueryParameter("api_key", API_KEY)
                    .appendQueryParameter("format", "json")
                    .appendQueryParameter("nojsoncallback", "1")
                    .appendQueryParameter("extras", "url_s")
                    .build().toString();
            String jsonString = getUrlString(url);
            Log.i(TAG, "Received from JSON: " + jsonString);
        } catch (IOException e) {
            Log.e(TAG, "Failed to fetch items", e);
        }
    }
}

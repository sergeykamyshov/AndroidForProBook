package com.kamyshovcorp.photogallery;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class FlickrFetchr {

    public static final String TAG = "FlickrFetchr";
    // TODO: Вставить свой API ключ от Flickr
    public static final String API_KEY = "";

    List<GalleryItem> items = new ArrayList<>();

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

    public List<GalleryItem> fetchItems() {
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
            JSONObject jsonObject = new JSONObject(jsonString);
            parseItems(items, jsonObject);
        } catch (JSONException je) {
            Log.e(TAG, "Failed to parse JSON", je);
        } catch (IOException e) {
            Log.e(TAG, "Failed to fetch items", e);
        }
        return items;
    }

    private void parseItems(List<GalleryItem> list, JSONObject jsonBody) throws JSONException {
        JSONObject photosJsonObject = jsonBody.getJSONObject("photos");
        JSONArray photoJsonArray = photosJsonObject.getJSONArray("photo");

        for (int i = 0; i < photoJsonArray.length(); i++) {
            JSONObject photoJsonObject = photoJsonArray.getJSONObject(i);

            GalleryItem item = new GalleryItem();

            item.setId(photoJsonObject.getString("id"));
            item.setCaption(photoJsonObject.getString("title"));

            if (!photoJsonObject.has("url_s")) {
                continue;
            }

            item.setUrl(photoJsonObject.getString("url_s"));
            list.add(item);
        }
    }
}

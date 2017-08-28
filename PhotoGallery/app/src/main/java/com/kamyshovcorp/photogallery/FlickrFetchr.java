package com.kamyshovcorp.photogallery;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class FlickrFetchr {

    public byte[] getUrlBytes(String urlSpec) throws IOException {
        URL url = new URL(urlSpec);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        try {
//            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
//
//            StringBuilder builder = new StringBuilder();
//            String line = "";
//            while ((line = bufferedReader.readLine()) != null) {
//                builder.append(line);
//            }


            ByteArrayOutputStream out = new ByteArrayOutputStream();
//            connection.setRequestProperty("Accept-Encoding", "identity");
//            connection.setRequestProperty("Accept-Charset", "UTF-8");
            InputStream in = connection.getInputStream();

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new IOException(connection.getResponseMessage() + ": with " + urlSpec);
            }

            int bytesRead = 0;
            byte[] buffer = new byte[1024];
            while ((bytesRead = in.read()) > 0) {
                out.write(buffer, 0, bytesRead);
            }
            out.close();
            return out.toByteArray();
        } finally {
            connection.disconnect();
        }
    }

    public String getUrlString(String urlSpec) throws IOException {
//        return new String(getUrlBytes(urlSpec), "UTF-8");
        return new String(getUrlBytes(urlSpec));
    }
}

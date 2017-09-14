package com.example.rash1k.locatr;

import android.location.Location;
import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class FlickFetchr {


    private static final String TAG = "FlickFetchr";
    private static final String API_KEY = "5c65790f9b03e0d80d1a0c878ef20ef6";
    private static final String FETCH_RECENT_METHOD = "flickr.photos.getRecent";
    private static final String FETCH_SEARCH_METHOD = "flickr.photos.search";
    private static final Uri ENDPOINT = Uri.parse("https://api.flickr.com/services/rest/")
            .buildUpon()
            .appendQueryParameter("api_key", API_KEY)
            .appendQueryParameter("format", "json")
            .appendQueryParameter("nojsoncallback", "1")
            .appendQueryParameter("extras", "url_s,geo")
            .build();

    byte[] getUrlBytes(String urlSpec) {

        long leadTime = System.currentTimeMillis();

        byte[] buffer = new byte[1024];
        HttpURLConnection connection = null;
        try {
            URL url = new URL(urlSpec);
            connection = (HttpURLConnection) url.openConnection();
            BufferedInputStream bi = new BufferedInputStream(connection.getInputStream());
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new IOException(connection.getResponseMessage() +
                        ": with " +
                        urlSpec + "Error");
            }
            int bytesRead;

            while ((bytesRead = bi.read(buffer)) > 0) {
                out.write(buffer, 0, bytesRead);
            }

            out.close();
            return out.toByteArray();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return buffer;
    }


    private String getStringUrl(String urlSpec) {
        return new String(getUrlBytes(urlSpec));
    }

    private String buildUrl(Location location) {
        Uri.Builder builder = ENDPOINT.buildUpon()
                .appendQueryParameter("method", FETCH_SEARCH_METHOD)
                .appendQueryParameter("lat", "" + location.getLatitude())
                .appendQueryParameter("lon", "" + location.getLongitude());

        return builder.build().toString();
    }

    public List<GalleryItem> searchPhotos(Location location) {
        String url = buildUrl(location);

        return downloadGalleryItems(url);
    }


    private List<GalleryItem> downloadGalleryItems(String url) {

        List<GalleryItem> items = new ArrayList<>();

        String jsonString = getStringUrl(url);
        try {
            JSONObject jsonBody = new JSONObject(jsonString);


            parseItems(items, jsonBody);

        } catch (JSONException e) {
            e.printStackTrace();
        }
//        Log.i(TAG, "Received JSON: " + jsonString);

        for (GalleryItem item:items) {
            Log.i(TAG, "downloadGalleryItems: " + item.getLat() + " " +item.getLon());
        }

        return items;

    }

    private void parseItems(List<GalleryItem> items, JSONObject jsonBody) {

        try {
            JSONObject photosJsonObject = jsonBody.getJSONObject("photos");
            JSONArray photoJsonArray = photosJsonObject.getJSONArray("photo");

            for (int i = 0; i < photoJsonArray.length(); i++) {
                JSONObject photoJsonObject = photoJsonArray.getJSONObject(i);

                if (!photoJsonObject.has("url_s")) {
                    continue;
                }
                GalleryItem item = new GalleryItem();

                item.setId(photoJsonObject.getString("id"));
                item.setCaption(photoJsonObject.getString("title"));
                item.setUrl(photoJsonObject.getString("url_s"));
                item.setLat(photoJsonObject.getDouble("latitude"));
                item.setLon(photoJsonObject.getDouble("longitude"));

                items.add(item);
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}

package com.example.jheron.instagramclient;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class PhotosActivity extends ActionBarActivity {

    public static final String CLIENT_ID = "7134f9a92742453fb221c2ceba65a6c5";
    private ArrayList<InstagramPhoto> photos;
    private InstragramPhotosAdapter aPhotos;
    private SwipeRefreshLayout swipeContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photos);

        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // your code to refresh the list here
                // make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                fetchPopularPhotos();
            }
        });

        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        photos = new ArrayList<>();
        // create adapater linking it to source
        aPhotos = new InstragramPhotosAdapter(this, photos);
        // Find the list view from the layout
        ListView lvPhotos = (ListView) findViewById(R.id.lvPhotos);
        // set the adapter binding it to the listView
        lvPhotos.setAdapter(aPhotos);
        // SEND OUT API REQUEST to POPULAR PHOTOS
        fetchPopularPhotos();
    }


    // Trigger API request
    public void fetchPopularPhotos() {
        /*

        Client-ID: 7134f9a92742453fb221c2ceba65a6c5
        Popular: https://api.instagram.com/v1/media/popular?access_token=ACCESS-TOKEN

        */

        String url = "https://api.instagram.com/v1/media/popular?client_id=" + CLIENT_ID;

        AsyncHttpClient client = new AsyncHttpClient();

        client.get(url, null, new JsonHttpResponseHandler() {
            // onSuccess (worked, 200)

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // Expecting a JSON object
                // Iterate each of the photo items and decode each of the items into a java object
                JSONArray photosJSON = null;
                aPhotos.clear();
                try {
                    photosJSON = response.getJSONArray("data");
                    for (int i = 0; i < photosJSON.length(); i++) {
                        // get the json object at that position
                        JSONObject photoJSON = photosJSON.getJSONObject(i);
                        // decode the attributes of the json into a data model
                        InstagramPhoto photo = new InstagramPhoto();
                        // Author Name { “data”: [x] => “user”=> “username” }
                        photo.username = photoJSON.getJSONObject("user").getString("username");
                        // Author Photo URL { “data”: [x] => “user”=> "profile_picture" }
                        photo.userPhotoURL = photoJSON.getJSONObject("user").getString("profile_picture");
                        // Caption: { “data”: [x] => “caption” => “text”}
                        photo.caption = attemptGetCaption(photoJSON);
                        // if (photoJSON.optJSONObject("caption") != null) {
                        //    photo.caption = photoJSON.getJSONObject("caption").getString("text");
                        //}
                        // URL: { “data”: [x] => “images” => “standard resolution” => “url” }
                        photo.imageURL = photoJSON.getJSONObject("images").getJSONObject("standard_resolution").getString("url");
                        // Type:{ "data": [x] => "caption" => "created_time" }
                        photo.datetime = photoJSON.getJSONObject("caption").getInt("created_time");
                        // Type:{ “data”: [x] => “type" } (“image or “video”)
                        photo.type = photoJSON.getString("type");
                        // Comment:{ "data": [x] => "comments" => "data": [y] => "text"}
                        photo.comment = attemptGetComment(photoJSON);
                        // Commenter:{ "data": [x] => "comments" => "data": [y] => "text"}
                        photo.commenter = attemptGetCommenter(photoJSON);
                        // Image Height:{ "data": [x] => "images" => "standard_resolution" => "height" }
                        photo.imageHeight = photoJSON.getJSONObject("images").getJSONObject("standard_resolution").getInt("height");
                        photo.likesCount = photoJSON.getJSONObject("likes").getInt("count");
                        // Add each object to photos array
                        photos.add(photo);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                // callback
                aPhotos.notifyDataSetChanged();
                swipeContainer.setRefreshing(false);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.e("DEBUG", "Fetch popular photos error: " + throwable.toString());
            }
        });
    }

    private String attemptGetCaption(JSONObject photoJSON){
        String caption = "";

        try {
            // Comment:{ "data": [x] => "comments" => "data": [y] => "text"}
            caption = photoJSON.getJSONObject("caption").getString("text");
        } catch (Exception e) {
            caption = "";
        }

        return caption;

    }

    private String attemptGetComment(JSONObject photoJSON){
        String comment = "";

        try {
            // Comment:{ "data": [x] => "comments" => "data": [y] => "text"}
            comment = photoJSON.getJSONObject("comments").getJSONArray("data").getJSONObject(0).getString("text");
        } catch (Exception e) {
            comment = "";
        }

        return comment;

    }

    private String attemptGetCommenter(JSONObject photoJSON) {
        String commenter = "";

        try {
            // Commenter:{ "data": [x] => "comments" => "data": [y] => "text"}
            commenter = photoJSON.getJSONObject("comments").getJSONArray("data").getJSONObject(0).getJSONObject("from").getString("username");
        } catch (Exception e) {
            commenter = "";
        }

        return commenter;

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_photos, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

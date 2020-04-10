package com.example.coronatracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.GeoPoint;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class NewsActivity extends AppCompatActivity {
ArrayList<NewsClass> myArray=new ArrayList<>();
    RecyclerViewAdapter adapter;
    RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        getSupportActionBar().hide();
        getHttpResponse();
        initRecyclerView();

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(myArray.get(position).getUrl()));
                startActivity(browserIntent);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
    }

            private void getHttpResponse() {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("https://newsapi.org/v1/articles?source=bbc-news&sortBy=top&apiKey=9f4d8830236b49d98beb9d83df36b1a8")
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                String mMessage = e.getMessage().toString();
                Log.i("failure Response", mMessage);
            }

            @Override
            public void onResponse(Response response) throws IOException {
                String mMessage = response.body().string();
                try {
                    JSONObject jsonObject = new JSONObject(mMessage);
                    JSONArray jsonArray = (JSONArray) jsonObject.get("articles");
                    for(int i=0;i<jsonArray.length();i++){
                        JSONObject j=(JSONObject)jsonArray.get(i);
                        NewsClass tempClass=new NewsClass(j.get("title").toString(),j.get("description").toString(),j.get("url").toString(),j.get("urlToImage").toString());
                        myArray.add(tempClass);
                    }
                    recyclerView.post(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyDataSetChanged();
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    private void initRecyclerView() {
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        recyclerView=findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter=new RecyclerViewAdapter(myArray,this);
        recyclerView.setAdapter(adapter);
    }
}

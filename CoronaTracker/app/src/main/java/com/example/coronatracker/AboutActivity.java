package com.example.coronatracker;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

public class AboutActivity extends AppCompatActivity {
ImageView i1,i2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        getSupportActionBar().hide();
        i1=findViewById(R.id.text1);
        i2=findViewById(R.id.text2);
        MobileAds.initialize(this);

        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }
    public void tone(View view){
        if(i1.getVisibility()==View.VISIBLE)
            i1.setVisibility(View.GONE);
        else
            i1.setVisibility(View.VISIBLE);
    }
    public void ttwo(View view){
        if(i2.getVisibility()==View.VISIBLE)
            i2.setVisibility(View.GONE);
        else
            i2.setVisibility(View.VISIBLE);
    }
}

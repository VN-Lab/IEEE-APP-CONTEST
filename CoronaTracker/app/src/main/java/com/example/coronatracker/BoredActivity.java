package com.example.coronatracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

public class BoredActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bored);
        getSupportActionBar().hide();
    }
    public void one(View view){
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.goodhousekeeping.com/home/gardening/advice/g495/small-garden-ideas/"));
        startActivity(browserIntent);
    }
    public void two(View view){
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://openlibrary.org/"));
        startActivity(browserIntent);
    }
    public void three(View view){
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.cookingchanneltv.com/"));
        startActivity(browserIntent);
    }
    public void four(View view){
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.gaiam.com/blogs/discover/meditation-101-techniques-benefits-and-a-beginner-s-how-to"));
        startActivity(browserIntent);
    }
    public void five(View view){
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.openculture.com/freelanguagelessons"));
        startActivity(browserIntent);
    }
    public void six(View view){
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.udemy.com"));
        startActivity(browserIntent);
    }
    public void back(View view){
        finish();
    }
}

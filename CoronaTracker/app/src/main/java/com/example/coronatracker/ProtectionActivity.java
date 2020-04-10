package com.example.coronatracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class ProtectionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_protection);
        getSupportActionBar().hide();
    }
    public void click1(View view){
        Intent intent=new Intent(getApplicationContext(),ProtectionImageActivity.class);
        intent.putExtra("Age",1);
        startActivity(intent);
    }
    public void click2(View view){
        Intent intent=new Intent(getApplicationContext(),ProtectionImageActivity.class);
        intent.putExtra("Age",2);
        startActivity(intent);
    }
    public void click3(View view){
        Intent intent=new Intent(getApplicationContext(),ProtectionImageActivity.class);
        intent.putExtra("Age",3);
        startActivity(intent);
    }
    public void click4(View view){
        Intent intent=new Intent(getApplicationContext(),ProtectionImageActivity.class);
        intent.putExtra("Age",4);
        startActivity(intent);
    }
    public void back(View view){
        finish();
    }
}

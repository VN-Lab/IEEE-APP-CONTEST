package com.example.coronatracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class ProtectionImageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_protection_image);
        getSupportActionBar().hide();
        ImageView imageView=findViewById(R.id.image);
        Intent recievied=getIntent();//Returns the Intent that got us there.
        int age=recievied.getIntExtra("Age",1);
        switch (age){
            case 1: Glide.with(getApplicationContext()).load(R.drawable.basic2).into(imageView);break;
            case 2: Glide.with(getApplicationContext()).load(R.drawable.guidance).into(imageView);break;
            case 3: Glide.with(getApplicationContext()).load(R.drawable.long_name).into(imageView);break;
            case 4: Glide.with(getApplicationContext()).load(R.drawable.other_tips).into(imageView);break;
        }
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.who.int/emergencies/diseases/novel-coronavirus-2019/advice-for-public"));
                startActivity(browserIntent);
            }
        });
    }
}

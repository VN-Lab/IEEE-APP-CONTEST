package com.example.coronatracker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import androidx.exifinterface.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class ProfileDetailsActivity extends AppCompatActivity {
    TextView textView,WarningTextview;
    String KEY_NAME="Username",imageUrl;
    FirebaseFirestore db=FirebaseFirestore.getInstance();
    EditText name;
    ProgressBar progressBar,SavingProgressBar;
    ImageView imageView;
    boolean imageChanged=false;
    FirebaseUser currentUser=FirebaseAuth.getInstance().getCurrentUser();
    String phoneNumber=currentUser.getPhoneNumber();
    StorageReference imageRef= FirebaseStorage.getInstance().getReference().child("ProfilePhoto/"+phoneNumber);
    DocumentReference nameRef=db.document("Users/"+phoneNumber);
    Bitmap rotatedBitmap,bitmap;
    Button SaveChanges,NextButton;
    Uri uploadUri;
    public void getphoto() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        Toast.makeText(this, "Loading of image takes a while\nDo not select the image twice!", Toast.LENGTH_LONG).show();
        startActivityForResult(intent, 1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                getphoto();
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_details);
        setTitle("Profile Details");
        textView=findViewById(R.id.PhoneNumberTextview);
        progressBar=findViewById(R.id.ImageProgressBar);
        SavingProgressBar=findViewById(R.id.SavingProgressBar);
        SaveChanges=findViewById(R.id.button);
        NextButton=findViewById(R.id.NextButton);
        NextButton.setVisibility(View.INVISIBLE);
        SavingProgressBar.setVisibility(View.INVISIBLE);
        WarningTextview=findViewById(R.id.WarningTextView);
        WarningTextview.setVisibility(View.INVISIBLE);
        imageView=findViewById(R.id.imageView);
        name=findViewById(R.id.nameEditText);
        textView.setText("Phone Number: "+phoneNumber);
        progressBar.setVisibility(View.VISIBLE);
        imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                uploadUri= uri;
                Glide.with(getApplicationContext()).load(uploadUri).into(imageView);
                progressBar.setVisibility(View.INVISIBLE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressBar.setVisibility(View.INVISIBLE);
                try{
                    Toast.makeText(ProfileDetailsActivity.this, "Display Picture has not been set!", Toast.LENGTH_LONG).show();}catch (Exception el){
                    //el.printStackTrace();
                }
            }
        });
        nameRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        HashMap<String,String> note=new HashMap<>();
                        nameRef.set(note, SetOptions.merge());
                        if(documentSnapshot.exists()){
                            String title=documentSnapshot.getString(KEY_NAME);
                            name.setText(title);
                        }else{
                            name.setText("");
                        }
                    }
                });
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePictureIntent();
            }
        });
    }
    public void takePictureIntent () {
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }
        else
            getphoto();
    }
    @Override
    protected void onActivityResult ( int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        try {
            Uri selectedImage=data.getData();
            if(requestCode==1 && resultCode==RESULT_OK && data!=null){
                bitmap=MediaStore.Images.Media.getBitmap(this.getContentResolver(),selectedImage);
                InputStream input = getContentResolver().openInputStream(selectedImage);
                ExifInterface ei;
                if (Build.VERSION.SDK_INT > 23)
                    ei = new ExifInterface(input);
                else
                    ei = new ExifInterface(selectedImage.getPath());
                int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_UNDEFINED);


                switch(orientation) {

                    case ExifInterface.ORIENTATION_ROTATE_90:
                        rotatedBitmap = rotateImage(bitmap, 90);
                        break;

                    case ExifInterface.ORIENTATION_ROTATE_180:
                        rotatedBitmap = rotateImage(bitmap, 180);
                        break;

                    case ExifInterface.ORIENTATION_ROTATE_270:
                        rotatedBitmap = rotateImage(bitmap, 270);
                        break;

                    case ExifInterface.ORIENTATION_NORMAL:
                    default:
                        rotatedBitmap = bitmap;
                }
                imageView.setImageBitmap(rotatedBitmap);
                imageChanged=true;
            }
            else{
                imageUrl=uploadUri.toString();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public void uploadImageAndSaveUri(Bitmap imageBitmap){
        SaveChanges.setVisibility(View.INVISIBLE);
        SavingProgressBar.setVisibility(View.VISIBLE);
        WarningTextview.setVisibility(View.VISIBLE);
        ByteArrayOutputStream baos=new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos);
        byte[] data = baos.toByteArray();
        UploadTask upload=imageRef.putBytes(data);
        upload.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                SavingProgressBar.setVisibility(View.INVISIBLE);
                WarningTextview.setVisibility(View.INVISIBLE);
                NextButton.setVisibility(View.VISIBLE);
                if(task.isSuccessful()){
                    imageRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> uriTask) {
                            if(uriTask.getResult()!=null){
                                imageUrl = uriTask.getResult().toString();
                                }
                            else
                                imageUrl="";
                        }
                    });
                }
                else{
                    Toast.makeText(getApplicationContext(),task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    public void saveChanges(View view)  {
        if(name.getText().length()==0){
            name.setError("Enter name");
            name.requestFocus();
            return;
        }
        SavingProgressBar.setVisibility(View.INVISIBLE);
        WarningTextview.setVisibility(View.INVISIBLE);
        SaveChanges.setVisibility(View.INVISIBLE);
        Map<String,Object> note=new HashMap<>();
        note.put(KEY_NAME,name.getText().toString());

        if(imageChanged)
            uploadImageAndSaveUri(rotatedBitmap);
        else{
            if(uploadUri!=null)
                imageUrl=uploadUri.toString();
            else
                imageUrl=null;

        }
            note.put("url",imageUrl);
            nameRef.set(note, SetOptions.merge());
            Toast.makeText(getApplicationContext(), "Saved changes Successfully!", Toast.LENGTH_SHORT).show();
            SavingProgressBar.setVisibility(View.INVISIBLE);SaveChanges.setVisibility(View.INVISIBLE);
            WarningTextview.setVisibility(View.INVISIBLE);NextButton.setVisibility(View.VISIBLE);
        }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }
    public void onNext(View view){
        Intent intent=new Intent(getApplicationContext(),HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}

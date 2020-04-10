package com.example.coronatracker;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.hbb20.CountryCodePicker;

import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {
    long min,sec,dur=90*1000;String time;
    LinearLayout phoneLayout, verificationLayout;
    EditText phoneEditText, codeEditText;
    CountryCodePicker ccp;
    String verificationId=null;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks phoneAuthProvider;
    //FirebaseFirestore db=FirebaseFirestore.getInstance();
    String phonenumber="";
    public void funSend(View view) {
        String phone = phoneEditText.getText().toString();
        if (phone.trim().isEmpty() || phone.trim().length() < 10) {
            phoneEditText.setError("Enter valid Phone No.");
            phoneEditText.requestFocus();
            return;
        }
        if(phone.contains("+")){
            phoneEditText.setError("Remove '+' and the country code");
            phoneEditText.requestFocus();
            return;
        }
        phonenumber = "+" + ccp.getSelectedCountryCode() + phone;
        phoneLayout.setVisibility(View.GONE);
        verificationLayout.setVisibility(View.VISIBLE);
        timer();
        PhoneAuthProvider.getInstance().verifyPhoneNumber(phonenumber, 90, TimeUnit.SECONDS, this,
                phoneAuthProvider);
    }

    public void funVerify(View view) {
        String code = codeEditText.getText().toString();
        if(code.isEmpty()){
            codeEditText.setError("Enter the OTP");
            codeEditText.requestFocus();
            return;
        }
        if(verificationId!=null){
            PhoneAuthCredential credential=PhoneAuthProvider.getCredential(verificationId,code);
            signInWithPhoneAuthCredential(credential);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();
        phoneLayout = findViewById(R.id.layoutPhone);verificationLayout = findViewById(R.id.layoutVerification);
        phoneLayout.setVisibility(View.VISIBLE);verificationLayout.setVisibility(View.GONE);
        ccp = findViewById(R.id.ccp);
        codeEditText = findViewById(R.id.edit_text_code);phoneEditText = findViewById(R.id.PhoneEditText);
        phoneAuthProvider = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Toast.makeText(getApplicationContext(), "Update Your Google PlayServices and try Again after a few hours!", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                verificationId = s;

            }

            @Override
            public void onCodeAutoRetrievalTimeOut(String s) {
                Toast.makeText(getApplicationContext(), "TIME OUT!", Toast.LENGTH_LONG).show();
                phoneLayout.setVisibility(View.VISIBLE);
                verificationLayout.setVisibility(View.GONE);
                verificationId=s;
            }
        };
    }
    private void signInWithPhoneAuthCredential (PhoneAuthCredential credential){
        FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(),"SignIn Successful!",Toast.LENGTH_LONG).show();
                            Intent intent=new Intent(getApplicationContext(),HomeActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        } else {
                            Toast.makeText(getApplicationContext(),"Incorrect OTP",Toast.LENGTH_LONG).show();
                            phoneLayout.setVisibility(View.VISIBLE);
                            verificationLayout.setVisibility(View.GONE);
                        }
                    }
                });
    }
    public void timer(){
        final TextView textView=findViewById(R.id.textView45);
        long interval=1000;
        new CountDownTimer(dur,interval){
            public void onTick(long milliSecondsUntilDone){
                min=milliSecondsUntilDone/(1000*60);
                sec=milliSecondsUntilDone%(1000*60);
                sec=sec/1000;
                dur=milliSecondsUntilDone;
                if(min<10)
                    time="0"+min+":";
                else
                    time=min+":";
                if(sec<10)
                    time=time+"0"+sec;
                else
                    time=time+sec;
                textView.setText(time);
            }
            public void onFinish(){
                phoneLayout.setVisibility(View.VISIBLE);verificationLayout.setVisibility(View.GONE);
                Toast.makeText(LoginActivity.this, "Timeout!", Toast.LENGTH_SHORT).show();
            }
        }.start();
    }
    public void cancel(View view){
        phoneLayout.setVisibility(View.VISIBLE);
        verificationLayout.setVisibility(View.GONE);
    }
}
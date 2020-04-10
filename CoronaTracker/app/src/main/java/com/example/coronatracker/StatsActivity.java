package com.example.coronatracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.ybs.countrypicker.CountryPicker;
import com.ybs.countrypicker.CountryPickerListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class StatsActivity extends AppCompatActivity {
    JSONObject jsonObject,jstate;
    JSONArray jsonArray;
    TextView Ideath,Kdeath,Irecov,Krecov,Iconf,Kconf,Opdeath,Opconfirm,date;
    AutoCompleteTextView autoCompleteTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);
        getSupportActionBar().hide();
        Ideath=findViewById(R.id.death1);Kdeath=findViewById(R.id.death2);
        Iconf=findViewById(R.id.confirmed1);Kconf=findViewById(R.id.confirmed2);
        Irecov=findViewById(R.id.recoveries1);Krecov=findViewById(R.id.recoveries2);
        Opdeath=findViewById(R.id.death3);Opconfirm=findViewById(R.id.confirmed3);
        date=findViewById(R.id.date);
        autoCompleteTextView=findViewById(R.id.autocomplete);
        final ArrayAdapter<String> myAdapter=new ArrayAdapter(this,android.R.layout.simple_list_item_1,Statenames.state);
        autoCompleteTextView.setAdapter(myAdapter);
        autoCompleteTextView.setThreshold(1);
        autoCompleteTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                autoCompleteTextView.setText("");
            }
        });
        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                try {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                    JSONObject particularState=(JSONObject)jstate.get(autoCompleteTextView.getAdapter().getItem(i).toString());
                    Opconfirm.setText("Confirmed\n"+particularState.get("confirmed"));
                    Opdeath.setText("Deaths\n"+particularState.get("deaths"));

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
        getHttpResponse();
        CountryHTTP();
    }
    public void countrypick(View view){
        final CountryPicker picker = CountryPicker.newInstance("Select Country");  // dialog title
        final ConstraintLayout cl=findViewById(R.id.state_picker);
        picker.setListener(new CountryPickerListener() {
            @Override
            public void onSelectCountry(String name, String code, String dialCode, int flagDrawableResID) {
                // Implement your code here
                if(!name.equalsIgnoreCase("India")){
                    cl.setVisibility(View.INVISIBLE);
                }
                else
                    cl.setVisibility(View.VISIBLE);
                TextView tv=findViewById(R.id.countrytv);tv.setText(name);
                try {
                    jsonArray=jsonObject.getJSONArray(name);
                        JSONObject toDisplay=(JSONObject)jsonArray.get(jsonArray.length()-1);
                        Opdeath.setText("Deaths\n" + toDisplay.get("deaths"));
                        Opconfirm.setText("Confirmed\n" + toDisplay.get("confirmed"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                picker.dismiss();
            }
        });
        picker.show(getSupportFragmentManager(), "COUNTRY_PICKER");
    }

    private void CountryHTTP(){
        OkHttpClient client = new OkHttpClient();
        Request r2=new Request.Builder().url("https://pomber.github.io/covid19/timeseries.json").get().build();
        client.newCall(r2).enqueue(new Callback() {
            @Override
            public void onResponse(Response response) throws IOException {
                String mMessage = response.body().string();
                try {
                    jsonObject=new JSONObject(mMessage);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Request request, IOException e) {
                String mMessage = e.getMessage().toString();

            }
        });
    }
    private void getHttpResponse(){
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("https://corona-virus-world-and-india-data.p.rapidapi.com/api_india")
                .get()
                .addHeader("x-rapidapi-host", "corona-virus-world-and-india-data.p.rapidapi.com")
                .addHeader("x-rapidapi-key", "06796047dbmsh0d7dcec5600d00fp16c301jsn6f2e1b685fa7")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                String mMessage = e.getMessage().toString();
            }

            @Override
            public void onResponse(Response response) throws IOException {
                String mMessage = response.body().string();
                try {
                    JSONObject jsonObject=new JSONObject(mMessage);
                    jstate=(JSONObject)jsonObject.get("state_wise");
                    final JSONObject particularState=(JSONObject)jstate.get("Karnataka");
                    final JSONObject allStates= (JSONObject) jsonObject.get("total_values");
                    date.post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                date.setText("Last Updated: "+allStates.get("lastupdatedtime").toString());
                                Ideath.setText("Deaths\n"+allStates.get("deaths").toString());Kdeath.setText("Deaths\n"+particularState.get("deaths").toString());
                                Iconf.setText("Confirmed\n"+allStates.get("confirmed").toString());Kconf.setText("Confirmed\n"+particularState.get("confirmed").toString());
                                Irecov.setText("Recovered\n"+allStates.get("recovered").toString());Krecov.setText("Recovered\n"+particularState.get("recovered").toString());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        Request r2=new Request.Builder().url("https://pomber.github.io/covid19/timeseries.json").get().build();
        client.newCall(r2).enqueue(new Callback() {
            @Override
            public void onResponse(Response response) throws IOException {
                String mMessage = response.body().string();
                try {
                    jsonObject=new JSONObject(mMessage);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Request request, IOException e) {
                String mMessage = e.getMessage().toString();
            }
        });
    }
    public void fun(View view){
        finish();
    }
   }
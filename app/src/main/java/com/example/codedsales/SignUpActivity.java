package com.example.codedsales;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.codedsales.models.User;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class SignUpActivity extends AppCompatActivity {
    Context context;
    OkHttpClient client;
    JSONObject jo;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        context = this;

    }

    public String validateInputs(String firstname){
        return "Hello";
    }

    //<editor-fold defaultstate= "collapsed" desc= "Get Api Object">
    public void getAPIObject(String endpoint, String ... pv) {
        client = new OkHttpClient();
        FormBody.Builder newBody = new FormBody.Builder();
        for(int i=0;i<pv.length;i++){
            newBody.add("p"+i, pv[i]);
        }
        RequestBody formBody = newBody.build();
        Request request = newAPIRequest(formBody, endpoint);
        Thread thread = new Thread(() -> {
            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    runOnUiThread(() -> {
                        Toast.makeText(context, "Some issues were encountered", Toast.LENGTH_LONG).show();
                    });
                    return;
                }
                String rs = response.body().string();
                runOnUiThread(() -> {
                    try {
                        jo = new JSONObject(rs);
                        if(endpoint.equals("user")){
                            String msg;
                            String type = jo.getString("type");
                            switch (type){
                                case "success":
                                    Gson gson = new Gson();
                                    String us = jo.getString("user");
                                    user = gson.fromJson(us, User.class);
                                    Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    break;
                                case "false":
                                case "failed":
                                    msg = jo.getString("msg");
                                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                                    break;
                                default:
                                    Toast.makeText(context, "Unknown Error.", Toast.LENGTH_SHORT).show();
                                    break;
                            }
                        }
                    }catch (JSONException e) {
                        e.printStackTrace();
                    }
                });
            } catch (IOException e) {
                //progressBar.getDialog().dismiss();
                e.printStackTrace();
            }
        });
        thread.start();
    }
    //</editor-fold>

    //<editor-fold defaultstate= "collapsed" desc= "Api Connection">
    public static Request newAPIRequest(RequestBody formBody, String endpoint){
        String fullPath = "http://8.208.96.127:8080/CodeSales/shop/"+endpoint;
        Request request = new Request.Builder()
                .url(fullPath)
                .post(formBody)
                .build();
        return request;
    }
    //</editor-fold>

}
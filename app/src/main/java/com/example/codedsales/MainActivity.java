package com.example.codedsales;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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

public class MainActivity extends AppCompatActivity {
    Context context;
    OkHttpClient client;
    JSONObject jo;
    User user;
    EditText txtPhone;
    EditText txtPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;

        if(getSupportActionBar() != null){
            getSupportActionBar().hide();
       }
        txtPhone = findViewById(R.id.txtLoginPhone);
        txtPass = findViewById(R.id.txtLoginPassword);
        Button btnLogin =  findViewById(R.id.btnSignin);
        TextView txtRegister = findViewById(R.id.txtRegister);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phone = txtPhone.getText().toString();
                String password = txtPass.getText().toString();
                String checkRes = checkInputs(phone, password);
                switch(checkRes){
                    case "empty":
                        Toast.makeText(context,"Fields can't be empty", Toast.LENGTH_SHORT).show();
                        break;
                    case "not phone":
                        Toast.makeText(context,"Please enter a valid phone number", Toast.LENGTH_SHORT).show();
                        break;
                    case "short password":
                        Toast.makeText(context,"Password length can't be less than 4", Toast.LENGTH_SHORT).show();
                        break;
                    case "valid":
                        String pv[] = new String[2];
                        pv[1] = password;
                        pv[0] = phone;
                        getAPIObject("login",pv);
                        break;
                    default:
                        Toast.makeText(context,"Moku!! oh!!!", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });

        txtRegister.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, SignUpActivity.class);
            startActivity(intent);
        });

    }

    //<editor-fold defaultstate="collapsed" desc="Validate Inputs">
    private String checkInputs(String phone, String password) {
        String msg = "";
        //check if inputs fields are empty
        if(phone.isEmpty() || password.isEmpty()){
            msg = "empty";
        }//check the length of both fields
        else if(password.length() < 4){
            msg = "short password";
        }
        else if(phone.length() != 11){
            msg = "not phone";
        }
//        else if(!phone.startsWith("080") || !phone.startsWith("081") || !phone.startsWith("070") || !phone.startsWith("090") || !phone.startsWith("091")) {
//            msg = "not phone";
//        }
        else{
            msg = "valid";
        }
        return msg;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Validate User">

    //</editor-fold>

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
                        if(endpoint.equals("login")){
                            String msg;
                            String type = jo.getString("type");
                            switch (type.toLowerCase()){
                                case "success":
                                    Gson gson = new Gson();
                                    String us = jo.getString("user");
                                    user = gson.fromJson(us, User.class);
                                    String [] data = new String [3];
                                    Intent intent = new Intent(MainActivity.this, DashboardActivity.class);
                                    data[1] = user.getPhone();
                                    data[0] = user.getFirstName();
                                    data[2] = user.getBusiness();
                                    intent.putExtra("user", data);
                                    clearText();
                                    Toast.makeText(context, "Successful", Toast.LENGTH_SHORT).show();
                                    startActivity(intent);
                                    break;
                                case "false":
                                case "failed":
                                    msg = jo.getString("msg");
                                    clearText();
                                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                                    break;
                                default:
                                    clearText();
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

    public void clearText(){
        txtPhone.setText("");
        txtPass.setText("");
    }

}
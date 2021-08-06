package com.example.codedsales;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.codedsales.models.Item;
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

public class UpdateItemActivity extends AppCompatActivity {
    OkHttpClient client;
    JSONObject jo;
    Context context;
    EditText txtName, txtCode, txtDesc, txtPrice;
    Button btnUpdate, btnClear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_item);
        context = this;
        Bundle data = getIntent().getExtras();
        String [] userData = data.getStringArray("user");

        txtName = findViewById(R.id.TxtName);
        txtCode = findViewById(R.id.TxtItemCode);
        txtDesc = findViewById(R.id.TxtDescription);
        txtPrice = findViewById(R.id.TxtPrice);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnClear = findViewById(R.id.btnClear);

        String [] qv ={"76853212", userData[2]};
        getAPIObject("getitem",qv);

        btnUpdate.setOnClickListener(view -> {
            Log.i("loggy", "btnUpdate called");
            String name = txtName.getText().toString().trim();
            String code = txtCode.getText().toString().trim();
            String desc = txtDesc.getText().toString().trim();
            String price = txtPrice.getText().toString().trim();
            String [] pv = {code, name, price, userData[2], desc , userData[1]};
            getAPIObject("updateitem", pv);
        });

        btnClear.setOnClickListener(view -> {
            Log.i("loggy", "Clear called");
            clearText();
        });
    }


    //<editor-fold defaultstate= "collapsed" desc= "Get Api Object">
    public void getAPIObject(String endpoint, String ... pv) {
        Log.i("loggy", "getAPIObject called");
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
                        if(endpoint.equals("updateitem")){
                            String msg;
                            String type = jo.getString("type");
                            switch (type.toLowerCase()){
                                case "success":
                                case "false":
                                case "failed":
                                case "failure":
                                    msg = jo.getString("msg");
                                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                                    clearText();
                                    break;
                                default:
                                    Toast.makeText(context, "Moku!!! Oh!!!!", Toast.LENGTH_SHORT).show();
                                    break;
                            }
                        }else if(endpoint.equals("getitem")){
                            String msg;
                            String type = jo.getString("type");
                            switch (type.toLowerCase()){
                                case "success":
                                    Gson gson = new Gson();
                                    String us = jo.getString("item");
                                    Item item = gson.fromJson(us, Item.class);
                                    txtName.setText(item.getName());
                                    txtCode.setText(item.getCode());
                                    txtDesc.setText(item.getDescription());
                                    txtPrice.setText(item.getPrice().toString());
                                    break;
                                case "false":
                                case "failed":
                                case "failure":
                                    msg = jo.getString("msg");
                                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                                    break;
                                default:
                                    Toast.makeText(context, "Moku!!! Oh!!!!", Toast.LENGTH_SHORT).show();
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
        Log.i("loggy", "newAPIRequest Called");
        String fullPath = "http://8.208.96.127:8080/CodeSales/shop/"+endpoint;
        Request request = new Request.Builder()
                .url(fullPath)
                .post(formBody)
                .build();
        return request;
    }
    //</editor-fold>

    public void clearText(){
        Log.i("loggy", "Clear called");
        txtPrice.setText("");
        txtName.setText("");
        txtCode.setText("");
        txtDesc.setText("");
    }


}
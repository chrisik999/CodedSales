package com.example.codedsales;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.codedsales.models.Item;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ViewItemActivity extends AppCompatActivity {

    OkHttpClient client;
    JSONObject jo;
    Context context;


    RecyclerView recyclerView;
    String [] userData;
    ArrayList<Item> items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_item);

        Bundle data = getIntent().getExtras();
        userData = data.getStringArray("user");

        recyclerView = findViewById(R.id.recyclerview);

        getAPIObject("items");

    }

    //<editor-fold defaultstate= "collapsed" desc= "Get Api Object">
    public void getAPIObject(String endpoint) {
        Log.i("loggy", "getApi called");
        client = new OkHttpClient();
//        FormBody.Builder newBody = new FormBody.Builder();
//        for(int i=0;i<pv.length;i++){
//            newBody.add("p"+i, pv[i]);
//        }
//        RequestBody formBody = newBody.build();
        Request request = newAPIRequest(endpoint);
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
                        if(endpoint.equals("items")){
                            String msg;
                            String type = jo.getString("type");
                            switch (type.toLowerCase()){
                                case "success":
                                    Gson gson = new Gson();
                                    String us = jo.getString("itemList");
                                    TypeToken<ArrayList<Item>> token = new TypeToken<ArrayList<Item>>(){};
                                    items = gson.fromJson(us, token.getType());
                                    Log.i("items", items.toString());
                                    ViewItemAdapter viewItemAdapter = new ViewItemAdapter(items);
                                    recyclerView.setAdapter(viewItemAdapter);
                                    recyclerView.setLayoutManager(new LinearLayoutManager(this));
                                    RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this,DividerItemDecoration.VERTICAL);
                                    recyclerView.addItemDecoration(itemDecoration);
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
    public static Request newAPIRequest( String endpoint){
        Log.i("loggy", "newAPIRequest Called");
        String fullPath = "http://8.208.96.127:8080/CodeSales/shop/"+endpoint;
        Request request = new Request.Builder()
                .url(fullPath)
                .get()
                .build();
        return request;
    }
    //</editor-fold>

}
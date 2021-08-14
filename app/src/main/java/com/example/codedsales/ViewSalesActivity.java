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
import com.example.codedsales.models.Sale;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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

public class ViewSalesActivity extends AppCompatActivity {

    OkHttpClient client;
    JSONObject jo;
    Context context;


    RecyclerView recyclerView;
    String [] userData;
    ArrayList<Sale> sales;
    String [] qv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_sales);
        Bundle data = getIntent().getExtras();
        userData = data.getStringArray("user");
        context = this;

        recyclerView = findViewById(R.id.recyclerview);

        qv = new String[2];
        qv[0] = userData[1];
        qv[1] = userData[2];
        getAPIObject("getmonthlysales", qv);
    }

    //<editor-fold defaultstate= "collapsed" desc= "Get Api Object">
    public void getAPIObject(String endpoint, String ... pv) {
        client = new OkHttpClient();
        FormBody.Builder newBody = new FormBody.Builder();
        for(int i=0;i<pv.length;i++){
            newBody.add("p"+i, pv[i]);
        }
        RequestBody formBody = newBody.build();
        Request request = newAPIRequest(formBody,endpoint);
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
                        if(endpoint.equals("getmonthlysales")){
                            String msg;
                            String type = jo.getString("type");
                            switch (type.toLowerCase()){
                                case "success":
                                    GsonBuilder gson = new GsonBuilder();
                                    String us = jo.getString("saleList");
                                    TypeToken<ArrayList<Sale>> token = new TypeToken<ArrayList<Sale>>(){};
                                    sales = gson.setDateFormat("yyyy-MM-dd hh:mm:ss").create().fromJson(us, token.getType());
                                    Log.i("sales", sales.toString());
                                    setSalesList(sales);
                                    Toast.makeText(context, "Successful", Toast.LENGTH_SHORT).show();
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


    //<editor-fold defaultstate="collapsed" desc="Setup Daily Sales History">
    void setSalesList(ArrayList<Sale> salesList){
        ViewSalesAdapter viewSalesAdapter = new ViewSalesAdapter(salesList);
        recyclerView.setAdapter(viewSalesAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this,DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(itemDecoration);
    }
    //</editor-fold>
}
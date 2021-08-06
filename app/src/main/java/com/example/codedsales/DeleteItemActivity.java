package com.example.codedsales;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class DeleteItemActivity extends AppCompatActivity {

    JSONObject jo;
    Context context;
    OkHttpClient client;

    EditText txtName;
    EditText txtCode;
    Button btnRemove;
    Button btnClear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("loggy", "onCreate called");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_item);
        context = this;

        Bundle data = getIntent().getExtras();
        String [] userData = data.getStringArray("user");
        //txtName = findViewById(R.id.TxtName);
        //txtCode = findViewById(R.id.TxtItemCode);

        btnRemove = findViewById(R.id.btnRemoveItem);
        btnClear = findViewById(R.id.btnClear);
        btnRemove.setOnClickListener(view -> {
            Log.i("loggy", "btnRemove called");
           String code = "218649998";//txtCode.getText().toString();
           String [] pv = {code,userData[2], userData[1]};
           getAPIObject("deleteitems",pv);

        });

        btnClear.setOnClickListener(view -> {
            Log.i("loggy", "btnClear called");

            txtName.setText("");
            txtCode.setText("");
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
                        if(endpoint.equals("deleteitems")){
                            String msg;
                            String type = jo.getString("type");
                            switch (type.toLowerCase()){
                                case "success":
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


}
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

public class CreateItemActivity extends AppCompatActivity {
    OkHttpClient client;
    JSONObject jo;
    Context context;
    EditText txtName, txtPrice, txtDesc, txtMQty;//, txtCode ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_item);
        context = this;
        Bundle data = getIntent().getExtras();
        String [] userData = data.getStringArray("user");
        Log.i("loggy", userData[0] + userData[1] + userData[2]);


        txtName = findViewById(R.id.createItemTxtName);
        //txtCode = findViewById(R.id.createItemTxtStockCode);
        txtPrice = findViewById(R.id.createItemTxtPrice);
        txtDesc = findViewById(R.id.createItemTxtDescription);
        txtMQty = findViewById(R.id.createItemTxtAddMQty);
        Button btnCreate = findViewById(R.id.btnCreateItem);
        Button btnClear = findViewById(R.id.createItemBtnClear);

        btnCreate.setOnClickListener(view -> {
            Log.i("logg", "btnCreate called");
            String phone, business, code, name, price, desc, mqty;
            phone = userData[1];
            business = userData[2];
            //code = txtCode.getText().toString();
            code = "154WBQT7";
            Log.i("loggy", "log code" + code);
            desc = txtDesc.getText().toString();
            mqty = txtMQty.getText().toString();
            name = txtName.getText().toString();
            price = txtPrice.getText().toString();
            String checkInputs = checkInputs(phone, business, code, price, desc, mqty, name);
            Log.i("loggy", "log response " +checkInputs);
            double dPrice = 0;
                switch(checkInputs){
                    case "is empty":
                        Toast.makeText(context, "Enter Item details", Toast.LENGTH_SHORT);
                        break;
                    case "empty":
                        Toast.makeText(context, "Fill in all information", Toast.LENGTH_SHORT);
                        break;
                    case "Invalid User":
                        Toast.makeText(context, "User Credentials is not valid", Toast.LENGTH_SHORT);
                        break;
                    case "Invalid Item":
                        Toast.makeText(context, "Item details is not valid", Toast.LENGTH_SHORT);
                        break;
                    case "valid":
                        dPrice= Double.valueOf(price);
                        if(dPrice < 5){
                            Log.i("loggy", "Price is not valid");
                            Toast.makeText(context, "Price can't be less than five", Toast.LENGTH_SHORT);
                        }else {
                            String pv[] = {code, name, price, business, desc, phone};
                            getAPIObject("items", pv);
                        }
                        break;
                    default:
                        Toast.makeText(context, "Mogbe!!!!", Toast.LENGTH_SHORT);
                        break;
                }

        });

        btnClear.setOnClickListener(view -> {
            //txtCode.setText("");
            txtName.setText("");
            txtMQty.setText("");
            txtPrice.setText("");
            txtDesc.setText("");
        });

    }

    //<editor-fold defaultstate="collapsed" desc="Validate Inputs">
    String checkInputs( String phone, String business, String code, String price, String desc, String mqty, String name){
        String msg = "";
        if (phone.isEmpty() || business.isEmpty() || code.isEmpty()){
            msg = "is empty";
        }
        else if(price.isEmpty() || desc.isEmpty() || mqty.isEmpty()|| name.isEmpty()){
            msg = "empty";
        }
        else if(phone.length() != 11 || business.length() != 7 ) {
            msg = "invalid User";
        }
        else if(name.length() < 3 & desc.length() < 3 & code.length() != 8){
            msg = "invalid Item";
        }
        else {
            msg = "valid";
        }
        return msg;
    }
    //</editor-fold>


    //<editor-fold defaultstate= "collapsed" desc= "Get Api Object">
    public void getAPIObject(String endpoint, String ... pv) {
        Log.i("loggy", "getApi called");
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
                        if(endpoint.equals("items")){
                            String msg;
                            String type = jo.getString("type");
                            Log.i("loggy", type);
                            switch (type.toLowerCase()){
                                case "success":
                                case "false":
                                case "failed":
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
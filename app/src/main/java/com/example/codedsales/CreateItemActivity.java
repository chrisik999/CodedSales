package com.example.codedsales;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.codedsales.models.Item;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CreateItemActivity extends AppCompatActivity {

    SurfaceView surfaceView;
    TextView txtBarcodeValue;
    private BarcodeDetector barcodeDetector;
    private CameraSource cameraSource;
    private static final int REQUEST_CAMERA_PERMISSION = 201;
    String intentData = "";
    FloatingActionButton fab;
    String [] userData;

    OkHttpClient client;
    JSONObject jo;
    Context context;
    EditText txtName, txtPrice, txtDesc, txtMQty, txtCode ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_item);
        context = this;
        Bundle data = getIntent().getExtras();
        userData = data.getStringArray("user");
        Log.i("loggy", userData[0] + userData[1] + userData[2]);


        txtName = findViewById(R.id.createItemTxtName);
        txtCode = findViewById(R.id.createItemTxtStockCode);
        txtPrice = findViewById(R.id.createItemTxtPrice);
        txtDesc = findViewById(R.id.createItemTxtDescription);
        txtMQty = findViewById(R.id.createItemTxtAddMQty);
        Button btnCreate = findViewById(R.id.btnCreateItem);
        Button btnClear = findViewById(R.id.createItemBtnClear);

        txtBarcodeValue = findViewById(R.id.txtBarcodeValue);
        surfaceView = findViewById(R.id.surfaceView);
        fab = findViewById(R.id.fab);

        btnCreate.setOnClickListener(view -> {
            Log.i("logg", "btnCreate called");
            String phone, business, code, name, price, desc, mqty;
            phone = userData[1];
            business = userData[2];
            code = txtCode.getText().toString();
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
            clearText();
        });

        fab.setOnClickListener(view -> {
            clean();
        });

    }

    private void initialiseDetectorsAndSources() {

        barcodeDetector = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.ALL_FORMATS)
                .build();
        cameraSource = new CameraSource.Builder(this, barcodeDetector)
                .setRequestedPreviewSize(1920, 1080)
                .setAutoFocusEnabled(true) //you should add this feature
                .build();
        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    if (ActivityCompat.checkSelfPermission(CreateItemActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        cameraSource.start(surfaceView.getHolder());
                    } else {
                        ActivityCompat.requestPermissions(CreateItemActivity.this, new
                                String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }
            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }
        });
        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {}

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                String [] qv = new String[2];
                final SparseArray<Barcode> barCodes = detections.getDetectedItems();
                if (barCodes.size()!=0) {
                    txtCode.post(() -> {
                        intentData = barCodes.valueAt(0).displayValue;
                        cameraSource.stop();
                        if(intentData.length()==12)intentData = "0"+intentData;
                        txtBarcodeValue.setText("Code Captured");
                        txtCode.setText(intentData);
                    });
                }
            }
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
        else if(name.length() < 3 & desc.length() < 3 & code.length() < 8){
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
                                    clearText();
                                    clean();
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

    void clearText(){
        txtCode.setText("");
        txtName.setText("");
        txtMQty.setText("");
        txtPrice.setText("");
        txtDesc.setText("");
    }

    @Override
    protected void onPause() {
        super.onPause();
        cameraSource.release();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initialiseDetectorsAndSources();
    }

    void clean(){
        try {
            if (ActivityCompat.checkSelfPermission(CreateItemActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                cameraSource.start(surfaceView.getHolder());
            } else {
                ActivityCompat.requestPermissions(CreateItemActivity.this, new
                        String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
            }
            txtBarcodeValue.setText("No BarCode Detected");
            txtCode.setText("");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
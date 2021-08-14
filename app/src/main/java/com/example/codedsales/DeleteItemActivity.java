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

public class DeleteItemActivity extends AppCompatActivity {

    SurfaceView surfaceView;
    TextView txtBarcodeValue;
    private BarcodeDetector barcodeDetector;
    private CameraSource cameraSource;
    private static final int REQUEST_CAMERA_PERMISSION = 201;
    String intentData = "";
    FloatingActionButton fab;
    String [] userData;

    JSONObject jo;
    Context context;
    OkHttpClient client;

    EditText txtName;
    EditText txtCode;
    Button btnRemove;
    Button btnClear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_item);
        context = this;

        Bundle data = getIntent().getExtras();
        userData = data.getStringArray("user");

        txtName = findViewById(R.id.TxtName);
        txtCode = findViewById(R.id.TxtItemCode);

        btnRemove = findViewById(R.id.btnRemoveItem);
        btnClear = findViewById(R.id.btnClear);

        txtBarcodeValue = findViewById(R.id.txtBarcodeValue);
        surfaceView = findViewById(R.id.surfaceView);
        fab = findViewById(R.id.fab);

        btnRemove.setOnClickListener(view -> {
            Log.i("loggy", "btnRemove called");
           String code = txtCode.getText().toString();
           String [] pv = {code,userData[2], userData[1]};
           getAPIObject("deleteitems",pv);
        });

        btnClear.setOnClickListener(view -> {
            Log.i("loggy", "btnClear called");
            clearText();
        });

        fab.setOnClickListener(view -> {
            try {
                if (ActivityCompat.checkSelfPermission(DeleteItemActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    cameraSource.start(surfaceView.getHolder());
                } else {
                    ActivityCompat.requestPermissions(DeleteItemActivity.this, new
                            String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
                }
                txtBarcodeValue.setText("No BarCode Detected");
            } catch (IOException e) {
                e.printStackTrace();
            }
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
                    if (ActivityCompat.checkSelfPermission(DeleteItemActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        cameraSource.start(surfaceView.getHolder());
                    } else {
                        ActivityCompat.requestPermissions(DeleteItemActivity.this, new
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
                final SparseArray<Barcode> barCodes = detections.getDetectedItems();
                if (barCodes.size()!=0) {
                    txtCode.post(() -> {
                        intentData = barCodes.valueAt(0).displayValue;
                        if(intentData.length()==12)intentData = "0"+intentData;
                        cameraSource.stop();
                        txtBarcodeValue.setText("Code Captured");
                    });
                    String [] qv ={intentData, userData[2]};
                    getAPIObject("getitem",qv);
                }
            }
        });
    }

    //<editor-fold defaultstate="collapsed" desc="Get Api Object">
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
                                    clearText();
                                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                                    break;
                                default:
                                    clearText();
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

    void clearText(){
        txtName.setText("");
        txtCode.setText("");
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
}
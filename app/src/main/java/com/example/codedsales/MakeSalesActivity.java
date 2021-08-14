package com.example.codedsales;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;
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
import java.util.ArrayList;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MakeSalesActivity extends AppCompatActivity {

    //barcode scanner entities
    SurfaceView surfaceView;
    TextView txtBarcodeValue;
    private BarcodeDetector barcodeDetector;
    private CameraSource cameraSource;
    private static final int REQUEST_CAMERA_PERMISSION = 201;
    String intentData = "";
    FloatingActionButton fab;

    //Okhttp entities
    OkHttpClient client;
    JSONObject jo;

    Context context;
    String [] userData, qv;
    TextView txtItemName, txtPrice, txtTotal;
    EditText txtCode;
    Button btnAdd, btnRemove, btnFetch, btnPay, btnClear;
    Item scannedItem;
    RecyclerView recyclerView;
    ArrayList<Item> cart;
    double total = 0;
    int discount = 0;
    StringBuilder description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_sales);

        //<editor-fold defaultstate="collapsed" desc ="Initilization">
        context = this;
        Bundle data = getIntent().getExtras();
        userData = data.getStringArray("user");
        Log.i("loggy", userData[0] + userData[1] + userData[2]);

        cart = new ArrayList<Item>();

        txtItemName = findViewById(R.id.txtItemName);
        txtCode = findViewById(R.id.txtCode);
        txtPrice = findViewById(R.id.txtPrice);
        txtTotal = findViewById(R.id.txtTotalSales);

        btnAdd = findViewById(R.id.btnAdd);
        btnRemove = findViewById(R.id.btnRemove);

        txtBarcodeValue = findViewById(R.id.txtBarcodeValue);
        surfaceView = findViewById(R.id.surfaceView);
        fab = findViewById(R.id.fab);
        recyclerView = findViewById(R.id.linear_sales_layout_1);
        btnFetch = findViewById(R.id.btnFetch);
        btnPay = findViewById(R.id.btnPay);
        btnClear = findViewById(R.id.btnClear);
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc ="On Click Listerners">
        fab.setOnClickListener(view -> {
            try {
                if (ActivityCompat.checkSelfPermission(MakeSalesActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    cameraSource.start(surfaceView.getHolder());
                } else {
                    ActivityCompat.requestPermissions(MakeSalesActivity.this, new
                            String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
                }
                txtBarcodeValue.setText("No BarCode Detected");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        btnFetch.setOnClickListener(view -> {
            qv = new String[2];
            qv[0] = intentData;
            qv[1] = userData[2];
            getAPIObject("getitem", qv);
        });

        btnRemove.setOnClickListener(view -> {
            clearText();
        });

        btnAdd.setOnClickListener(view -> {
                String itemStatus = "empty";
                total = total + scannedItem.getPrice();
                if (cart.size() > 0) {
                    for (int i = 0; i < cart.size(); i++) {
                        Item loopItem = cart.get(i);
                        if (loopItem.getCode().equals(scannedItem.getCode())) {
                            Double lAmt = loopItem.getAmount() + scannedItem.getPrice();
                            loopItem.setAmount(lAmt);
                            double tnumber = loopItem.getQuantity() + 1;
                            loopItem.setQuantity(tnumber);
                            itemStatus = "exist";
                            break;
                        }
                    }
                    if (itemStatus.equals("empty")) {
                        scannedItem.setQuantity(1D);
                        scannedItem.setAmount(scannedItem.getPrice());
                        cart.add(scannedItem);
                    }
                } else {
                    scannedItem.setQuantity(1D);
                    scannedItem.setAmount(scannedItem.getPrice());
                    cart.add(scannedItem);
                }
                txtTotal.setText(String.valueOf(total));
                MakeSalesAdapter makeSalesAdapter = new MakeSalesAdapter(cart);
                recyclerView.setAdapter(makeSalesAdapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(this));
                RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
                recyclerView.addItemDecoration(itemDecoration);
                intentData = "";
                clean();
                clearText();
        });

        btnPay.setOnClickListener(view -> {
            description = new StringBuilder();
            double calcDiscount = (discount/100) * total;
            JSONObject json = new JSONObject();
            try{
                String Sitems = new Gson().toJson(cart);
                json.put("items", Sitems);
            } catch (JSONException e){
                e.printStackTrace();
            }
            double finalTotal = total - calcDiscount;
            for (Item item : cart){
                description.append(item.getName() + "\t" + item.getQuantity() + "piece" + "\t" + item.getAmount() + "\n");
            }
            Log.i("desc", description.toString());
            qv = new String[7];
            qv[0] = description.toString();
            qv[1] = userData[1];
            qv[2] = userData[2];
            qv[3] = json.toString();
            qv[4] = txtTotal.getText().toString();
            qv[5] = String.valueOf(discount);
            qv[6] = txtTotal.getText().toString();
            getAPIObject("sales", qv);
            clearAll();
            Toast.makeText(context, "Successful",Toast.LENGTH_SHORT).show();
        });

        btnClear.setOnClickListener(view -> {
            clearAll();
        });
        //</editor-fold>
    }

    //<editor-fold defaultstate="collapsed" desc="Barcode camera">
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
                    if (ActivityCompat.checkSelfPermission(MakeSalesActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        cameraSource.start(surfaceView.getHolder());
                    } else {
                        ActivityCompat.requestPermissions(MakeSalesActivity.this, new
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
                        Log.i("Logg", "txtCode.post was called");
                        intentData = barCodes.valueAt(0).displayValue;
                        cameraSource.stop();
                        Log.i("Logg", "cameraSource.stop was called");
                        if(intentData.length()==12)intentData = "0"+intentData;
                        txtBarcodeValue.setText("Code Captured");
                        txtCode.setText(intentData);
                    });
                }
            }
        });
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
                        if(endpoint.equals("sales")){
                            String msg;
                            String type = jo.getString("type");
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
                        }else if(endpoint.equals("getitem")){
                            String msg;
                            String type = jo.getString("type");
                            switch (type.toLowerCase()){
                                case "success":
                                    Gson gson = new Gson();
                                    String us = jo.getString("item");
                                    scannedItem = gson.fromJson(us, Item.class);
                                    txtItemName.setText(scannedItem.getName());
                                    txtCode.setText(scannedItem.getCode());
                                    txtPrice.setText(scannedItem.getPrice().toString());
                                    Toast.makeText(context, "Item found", Toast.LENGTH_SHORT).show();
                                    break;
                                case "false":
                                    Toast.makeText(context, "Item not found", Toast.LENGTH_SHORT).show();
                                    break;
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

    //<editor-fold defaultstate="collapsed" desc="Clean">
    void clearText(){
        txtItemName.setText("Item Name");
        txtCode.setText("ItemCode");
        txtPrice.setText("Price");
    }

    void clean(){
        clearText();
        try {
            if (ActivityCompat.checkSelfPermission(MakeSalesActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                cameraSource.start(surfaceView.getHolder());
            } else {
                ActivityCompat.requestPermissions(MakeSalesActivity.this, new
                        String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
            }
            txtBarcodeValue.setText("No BarCode Detected");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void clearAll(){
        clean();
        clearText();
        txtTotal.setText("");
        recyclerView.setAdapter(new RecyclerView.Adapter() {
            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return null;
            }

            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

            }

            @Override
            public int getItemCount() {
                return 0;
            }
        });
    }
    //</editor-fold>
}
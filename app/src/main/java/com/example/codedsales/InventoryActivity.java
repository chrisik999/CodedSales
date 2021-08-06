package com.example.codedsales;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.example.codedsales.models.User;

public class InventoryActivity extends AppCompatActivity {

    User user;
    String [] userData;
    Context context;
    Intent intent;
    CardView createItemCard;
    CardView addItemCard;
    CardView updateItemCard;
    CardView viewItemCard;
    CardView removeItemCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);
        context = this;
        Bundle data =  getIntent().getExtras();
        userData = data.getStringArray("user");

        createItemCard = findViewById(R.id.createItemCard);
        createItemCard.setOnClickListener(view -> {
            intent = new Intent(InventoryActivity.this, CreateItemActivity.class);
            intent.putExtra("user", userData);
            startActivity(intent);
        });

        addItemCard = findViewById(R.id.addItemCard);
        addItemCard.setOnClickListener(view -> {
            intent = new Intent(InventoryActivity.this, AddStockActivity.class);
            intent.putExtra("user", userData);
            startActivity(intent);
        });

        updateItemCard = findViewById(R.id.updateItemCard);
        updateItemCard.setOnClickListener(view -> {
            intent = new Intent(InventoryActivity.this, UpdateItemActivity.class);
            intent.putExtra("user", userData);
            startActivity(intent);
        });

        viewItemCard = findViewById(R.id.viewItemCard);
        viewItemCard.setOnClickListener(view -> {
            intent = new Intent(InventoryActivity.this, ViewItemActivity.class);
            intent.putExtra("user", userData);
            startActivity(intent);
        });

        removeItemCard = findViewById(R.id.removeItemCard);
        removeItemCard.setOnClickListener(view -> {
            intent = new Intent(InventoryActivity.this, DeleteItemActivity.class);
            intent.putExtra("user", userData);
            startActivity(intent);
        });
    }
}
package com.example.codedsales;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.example.codedsales.models.User;

public class DashboardActivity extends AppCompatActivity {
    User user;
    String [] userData;
    Context context;
    Intent intent;
    CardView salesCard;
    CardView stockCard;
    CardView historyCard;
    CardView profileCard;
    CardView settingsCard;
    CardView salesHistoryCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        Bundle data =  getIntent().getExtras();
        context = this;
         userData = data.getStringArray("user");

        TextView txtDashboard = findViewById(R.id.txtDashboard);
        txtDashboard.setText("hello, " + userData[0]);

        salesCard = findViewById(R.id.salesCard);
        salesCard.setOnClickListener(view -> {
            intent = new Intent(DashboardActivity.this, SalesActivity.class);
            intent.putExtra("user",  userData);
            startActivity(intent);
        });

        stockCard = findViewById(R.id.stockCard);
        stockCard.setOnClickListener(view -> {
            intent = new Intent(DashboardActivity.this, InventoryActivity.class);
            intent.putExtra("user",  userData);
            startActivity(intent);
        });

        historyCard = findViewById(R.id.historyCard);
        historyCard.setOnClickListener(view -> {
            intent = new Intent(DashboardActivity.this, ActivitiesHistoryActivity.class);
            intent.putExtra("user",  userData);
            startActivity(intent);
        });

        profileCard = findViewById(R.id.profileCard);
        profileCard.setOnClickListener(view -> {
            intent = new Intent(DashboardActivity.this, ProfileActivity.class);
            intent.putExtra("user",  userData);
            startActivity(intent);
        });

        settingsCard = findViewById(R.id.settingsCard);
        settingsCard.setOnClickListener(view -> {
            intent = new Intent(DashboardActivity.this, SettingsActivity.class);
            intent.putExtra("user",  userData);
            startActivity(intent);
        });

        salesHistoryCard = findViewById(R.id.saleHistoryCard);
        salesHistoryCard.setOnClickListener(view -> {
            intent = new Intent(DashboardActivity.this, ViewSalesActivity.class);
            intent.putExtra("user",  userData);
            startActivity(intent);
        });

    }
}
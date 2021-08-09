package com.example.codedsales;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.codedsales.models.StockActivity;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class StockActivityAdapter extends RecyclerView.Adapter<StockActivityAdapter.StockActivityHolder> {

    ArrayList<StockActivity> activities;

    public StockActivityAdapter(ArrayList<StockActivity> activities) {
        this.activities = activities;
    }

    @NonNull
    @Override
    public StockActivityAdapter.StockActivityHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater =LayoutInflater.from(context);

        View activityView = inflater.inflate(R.layout.stock_activity_card,parent,false);

        StockActivityAdapter.StockActivityHolder viewHolder = new StockActivityAdapter.StockActivityHolder(activityView);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull StockActivityAdapter.StockActivityHolder holder, int position) {
        StockActivity currentItem = activities.get(position);
        holder.txtUser.setText(currentItem.getInitiator());
        holder.txtCode.setText(currentItem.getItem());
        holder.txtActivity.setText(currentItem.getType());
        holder.txtTime.setText(currentItem.getDate().toString());
    }

    @Override
    public int getItemCount() {
        return activities.size();
    }


    public class StockActivityHolder extends RecyclerView.ViewHolder{
        private TextView txtUser;
        private TextView txtCode;
        private TextView txtActivity;
        private TextView txtTime;

        public StockActivityHolder(@NonNull View itemView) {
            super(itemView);
            txtUser = itemView.findViewById(R.id.txtUser);
            txtActivity  =itemView.findViewById(R.id.txtActivity);
            txtCode = itemView.findViewById(R.id.txtCode);
            txtTime = itemView.findViewById(R.id.txtTime);
        }
    }


}

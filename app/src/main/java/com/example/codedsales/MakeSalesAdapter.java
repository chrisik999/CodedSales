package com.example.codedsales;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.codedsales.models.Item;
import com.example.codedsales.models.StockActivity;

import java.util.ArrayList;

public class MakeSalesAdapter extends RecyclerView.Adapter<MakeSalesAdapter.MakeSalesHolder> {

    ArrayList<Item> cart;

    public MakeSalesAdapter(ArrayList<Item> cart) {
        this.cart = cart;
    }

    @NonNull
    @Override
    public MakeSalesAdapter.MakeSalesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater =LayoutInflater.from(context);

        View activityView = inflater.inflate(R.layout.card_sales,parent,false);

        MakeSalesAdapter.MakeSalesHolder viewHolder = new MakeSalesAdapter.MakeSalesHolder(activityView);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MakeSalesAdapter.MakeSalesHolder holder, int position) {
        Item currentItem = cart.get(position);
        holder.txtName.setText(currentItem.getName());
        holder.txtQty.setText(currentItem.getQuantity().toString());
        holder.txtAmt.setText(currentItem.getAmount().toString());
    }

    @Override
    public int getItemCount() {
        return cart.size();
    }


    public class MakeSalesHolder extends RecyclerView.ViewHolder{
        private TextView txtName;
        private TextView txtQty;
        private TextView txtAmt;

        public MakeSalesHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txtName);
            txtQty  =itemView.findViewById(R.id.txtQty);
            txtAmt = itemView.findViewById(R.id.txtAmount);
        }
    }



}

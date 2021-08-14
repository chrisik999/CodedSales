package com.example.codedsales;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.codedsales.models.Item;
import com.example.codedsales.models.Sale;

import java.util.ArrayList;

public class ViewSalesAdapter extends RecyclerView.Adapter<ViewSalesAdapter.SaleViewHolder> {

    ArrayList<Sale> sales;

    public ViewSalesAdapter(ArrayList<Sale> sales) {
        this.sales = sales;
    }

    @NonNull
    @Override
    public ViewSalesAdapter.SaleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater =LayoutInflater.from(context);

        View saleView = inflater.inflate(R.layout.card_history,parent,false);

        ViewSalesAdapter.SaleViewHolder viewHolder = new ViewSalesAdapter.SaleViewHolder(saleView);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewSalesAdapter.SaleViewHolder holder, int position) {
        Sale currentSale = sales.get(position);
        holder.txtTotal.setText(currentSale.getTotal().toString());
        holder.txtCode.setText(currentSale.getCode());
        holder.txtTime.setText(currentSale.getDate().toString());
    }

    @Override
    public int getItemCount() {
        return sales.size();
    }

    public class SaleViewHolder extends RecyclerView.ViewHolder{
        private TextView txtTime;
        private TextView txtCode;
        private TextView txtTotal;

        public SaleViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTotal = itemView.findViewById(R.id.txtTotal);
            txtTime  =itemView.findViewById(R.id.txtTime);
            txtCode = itemView.findViewById(R.id.txtCode);
        }
    }

}

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

import java.util.ArrayList;

public class ViewItemAdapter extends RecyclerView.Adapter<ViewItemAdapter.ItemViewHolder> {

    ArrayList<Item> items;

    public ViewItemAdapter(ArrayList<Item> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater =LayoutInflater.from(context);

        View itemView = inflater.inflate(R.layout.view_item_card,parent,false);

        ItemViewHolder viewHolder = new ItemViewHolder(itemView);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        Item currentItem = items.get(position);
        holder.txtName.setText(currentItem.getName());
        holder.txtCode.setText(currentItem.getCode());
        holder.txtAmount.setText(currentItem.getPrice().toString());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }


    public class ItemViewHolder extends RecyclerView.ViewHolder{
        private TextView txtName;
        private TextView txtAmount;
        private TextView txtCode;
        private EditText txtDescription;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txtName);
            txtAmount  =itemView.findViewById(R.id.txtPrice);
            txtCode = itemView.findViewById(R.id.txtCode);
        }
    }


}

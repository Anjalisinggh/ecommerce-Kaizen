package com.example.kaizenarts.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kaizenarts.R;
import com.example.kaizenarts.models.MyCartModel;

import java.util.List;
import java.util.Objects;

public class MyCartAdapter extends RecyclerView.Adapter<MyCartAdapter.ViewHolder> {
    private final Context context;
    private final List<MyCartModel> list;

    public MyCartAdapter(Context context, List<MyCartModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_cart_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MyCartModel model = list.get(position);

        holder.date.setText(model.getCurrentDate() != null ? model.getCurrentDate() : "N/A");
        holder.time.setText(model.getCurrentTime() != null ? model.getCurrentTime() : "N/A");
        holder.price.setText(String.format("%s Rs", model.getProductPrice() != null ? model.getProductPrice() : "N/A"));
        holder.name.setText(model.getProductName() != null ? model.getProductName() : "N/A");
        holder.totalQuantity.setText(model.getTotalQuantity() != null ? model.getTotalQuantity() : "0");

        // ✅ Fixed null issue using Objects.requireNonNullElse
        int totalPrice = Objects.requireNonNullElse(model.getTotalPrice(), 0);
        holder.totalPrice.setText(String.format("%d Rs", totalPrice));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, price, date, time, totalQuantity, totalPrice;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.product_name);
            price = itemView.findViewById(R.id.product_price);
            date = itemView.findViewById(R.id.current_date);
            time = itemView.findViewById(R.id.current_time);
            totalQuantity = itemView.findViewById(R.id.total_quantity);
            totalPrice = itemView.findViewById(R.id.total_price);
        }
    }

    // ✅ Optimized: Call this only when dataset changes
    public void calculateTotalAmount() {
        int totalAmount = 0;
        for (MyCartModel item : list) {
            totalAmount += Objects.requireNonNullElse(item.getTotalPrice(), 0);
        }

        Intent intent = new Intent("MyTotalAmount");
        intent.putExtra("totalAmount", totalAmount);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    // ✅ New Method: Update list safely
    public void updateList(List<MyCartModel> newList) {
        this.list.clear();
        this.list.addAll(newList);
        notifyDataSetChanged();
        calculateTotalAmount(); // ✅ Update total when data changes
    }
}

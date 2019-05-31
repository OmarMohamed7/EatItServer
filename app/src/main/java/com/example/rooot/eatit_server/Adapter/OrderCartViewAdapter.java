package com.example.rooot.eatit_server.Adapter;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import com.example.rooot.eatit_server.Common.CurrentUser;
import com.example.rooot.eatit_server.Interface.ItemClickListener;
import com.example.rooot.eatit_server.Model.Request;
import com.example.rooot.eatit_server.R;
import com.example.rooot.eatit_server.TrackingOrder;
import com.example.rooot.eatit_server.ViewHolder.OrderCartViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

import java.text.NumberFormat;
import java.util.Locale;

public class OrderCartViewAdapter extends FirebaseRecyclerAdapter<Request , OrderCartViewHolder>  {

    public static int total = 0;

    Locale local = new Locale("en", "US");
    NumberFormat fmt = NumberFormat.getCurrencyInstance(local);

    public OrderCartViewAdapter(@NonNull FirebaseRecyclerOptions<Request> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull final OrderCartViewHolder holder, int position, @NonNull final Request request) {
        holder.txtOrderId.setText(request.getID());
        holder.txtOrderName.setText(request.getName());
        holder.txtOrderAddress.setText(request.getAddress());
        holder.txtOrderPhone.setText(request.getPhone());
        holder.txtOrderStatus.setText(CurrentUser.convertCodeToStatus(request.getStatus()));
        holder.txtOrderTotalCash.setText(fmt.format(Integer.valueOf(request.getTotal())));

        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                CurrentUser.current_Request =request;
                view.getContext().startActivity(new Intent(view.getContext() , TrackingOrder.class));
            }
        });
    }

    @NonNull
    @Override
    public OrderCartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new OrderCartViewHolder(LayoutInflater.from(parent.getContext()).
                inflate(R.layout.order_cart_view_layout, parent, false));
    }

}

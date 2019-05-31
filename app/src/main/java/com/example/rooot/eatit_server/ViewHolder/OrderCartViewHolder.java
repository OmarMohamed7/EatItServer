package com.example.rooot.eatit_server.ViewHolder;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.View;
import android.widget.TextView;

import com.example.rooot.eatit_server.Interface.ItemClickListener;
import com.example.rooot.eatit_server.R;
import com.example.rooot.eatit_server.TrackingOrder;

public class OrderCartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener ,View.OnCreateContextMenuListener{

    public TextView txtOrderId , txtOrderName, txtOrderStatus , txtOrderPhone , txtOrderAddress , txtOrderTotalCash;
    private ItemClickListener itemClickListner;

    public OrderCartViewHolder(@NonNull final View itemView) {
        super(itemView);

        txtOrderId = itemView.findViewById(R.id.order_id);
        txtOrderName = itemView.findViewById(R.id.order_name);
        txtOrderStatus = itemView.findViewById(R.id.order_status);
        txtOrderPhone = itemView.findViewById(R.id.order_phone);
        txtOrderAddress = itemView.findViewById(R.id.order_address);
        txtOrderTotalCash = itemView.findViewById(R.id.order_total_cash);


        itemView.setOnCreateContextMenuListener(this);
        itemView.setOnClickListener(this);

    }

    public void setItemClickListener(ItemClickListener itemClickListner) {
        this.itemClickListner = itemClickListner;

    }


    @Override
    public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
        contextMenu.setHeaderTitle("Select Header : ");

        contextMenu.add(0,0,getAdapterPosition(),"Update");
        contextMenu.add(0,0,getAdapterPosition(),"Delete");

    }

    @Override
    public void onClick(View view) {
        itemClickListner.onClick(view , getAdapterPosition() , false);
    }
}

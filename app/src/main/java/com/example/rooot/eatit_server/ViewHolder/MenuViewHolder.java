package com.example.rooot.eatit_server.ViewHolder;

import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.rooot.eatit_server.Common.CurrentUser;
import com.example.rooot.eatit_server.Interface.ItemClickListener;
import com.example.rooot.eatit_server.R;

public class MenuViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener , View.OnCreateContextMenuListener{

    Typeface face;
    public TextView txtMenuName , txtMenuHint , txtMenuPrice;
    public ImageView imgMenu;
    private ItemClickListener itemClickListener;
    public FloatingActionButton addToCart;
    public ElegantNumberButton quantityNumberButton;

    public MenuViewHolder(@NonNull View itemView) {
        super(itemView);

        txtMenuName = itemView.findViewById(R.id.txtMenuName);
        txtMenuHint = itemView.findViewById(R.id.txtMenuHint);
        txtMenuPrice = itemView.findViewById(R.id.txtMenuPrice);
//        addToCart = itemView.findViewById(R.id.add_to_cart);
//        quantityNumberButton = itemView.findViewById(R.id.counter);
//        quantityNumberButton.setNumber("1");
//        quantityNumberButton.setRange(1,10);
        imgMenu = itemView.findViewById(R.id.imgMenu);

//        face = Typeface.createFromAsset(itemView.getContext().getAssets(), "fonts/Insomnia.ttf");
//        txtMenuName.setTypeface(face);

        itemView.setOnClickListener(this);
        itemView.setOnCreateContextMenuListener(this);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View view) {
        itemClickListener.onClick(view , getAdapterPosition(),false);
    }

    @Override
    public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
        contextMenu.setHeaderTitle("Select : ");

        contextMenu.add(0,0,getAdapterPosition() , CurrentUser.UPDATE);
        contextMenu.add(0,1,getAdapterPosition() , CurrentUser.DELETE_CATEGORY);
        contextMenu.add(0,2,getAdapterPosition() , CurrentUser.DELETE_ITEM);
    }
}

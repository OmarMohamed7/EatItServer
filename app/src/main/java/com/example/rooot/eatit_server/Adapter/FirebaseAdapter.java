package com.example.rooot.eatit_server.Adapter;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.rooot.eatit_server.Interface.ItemClickListener;
import com.example.rooot.eatit_server.Model.Category;
import com.example.rooot.eatit_server.R;
import com.example.rooot.eatit_server.ViewHolder.MenuViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.squareup.picasso.Picasso;

public class FirebaseAdapter extends FirebaseRecyclerAdapter<Category,MenuViewHolder> {

    public FirebaseAdapter(@NonNull FirebaseRecyclerOptions<Category> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull MenuViewHolder holder, int position, @NonNull Category model) {
        holder.txtMenuName.setText(model.getName());
        holder.txtMenuHint.setText(model.getHint());
        holder.txtMenuPrice.setText(model.getPrice());

        //Picasso
        Picasso.with(holder.imgMenu.getContext()).load(model.getLink()).into(holder.imgMenu);

        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {

            }
        });


    }

    @NonNull
    @Override
    public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MenuViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.menu_item , parent , false));
    }


}

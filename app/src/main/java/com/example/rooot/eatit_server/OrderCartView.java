package com.example.rooot.eatit_server;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rooot.eatit_server.Adapter.OrderCartViewAdapter;
import com.example.rooot.eatit_server.Common.CurrentUser;
import com.example.rooot.eatit_server.Model.Order;
import com.example.rooot.eatit_server.Model.Request;
import com.example.rooot.eatit_server.ViewHolder.OrderCartViewHolder;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.jaredrummler.materialspinner.MaterialSpinner;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class OrderCartView extends AppCompatActivity {

    public RecyclerView recyclerView;

    FirebaseDatabase database;
    DatabaseReference requests;

    OrderCartViewAdapter adapter;

    //for total cash
    TextView totalCash;
    int total = 0;

    MaterialSpinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_cart_view);

        database = FirebaseDatabase.getInstance();
        requests = database.getReference("Requests");

        recyclerView = findViewById(R.id.listCart);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        totalCash = findViewById(R.id.totalCash);


        loadOrders();

    }

    private void loadOrders() {

        Query query = FirebaseDatabase.getInstance().getReference("Requests");

        FirebaseRecyclerOptions<Request> options = new FirebaseRecyclerOptions.Builder<Request>()
                .setQuery(query , Request.class)
                .build();

        adapter = new OrderCartViewAdapter(options);
        recyclerView.setAdapter(adapter);


    }


    @Override
    public boolean onContextItemSelected(MenuItem item) {

        if(item.getTitle().equals(CurrentUser.UPDATE))
            showUpdateDialog(adapter.getRef(item.getOrder()).getKey(), adapter.getItem(item.getOrder()));

        if(item.getTitle().equals("Delete"))
            DeleteOrder(adapter.getRef(item.getOrder()).getKey());


        return super.onContextItemSelected(item);
    }


    private void showUpdateDialog(final String key, final Request request) {

        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Update status");
        dialog.setMessage("Choose status : ");

        LayoutInflater layoutInflater = this.getLayoutInflater();
        final View view = layoutInflater.inflate(R.layout.update_order_layout , null);

        spinner = view.findViewById(R.id.statusSpinner);
        spinner.setItems("Placed" , "On the way" , "Delivered");

        dialog.setView(view);

        final String localKey = key;
        dialog.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                request.setStatus(String.valueOf(spinner.getSelectedIndex()));

                requests.child(key).setValue(request);

                Toast.makeText(OrderCartView.this, "Request updated successfully", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        dialog.show();

    }

    private void DeleteOrder(String key) {

        requests.child(key).removeValue();
        Toast.makeText(this, "Request deleted", Toast.LENGTH_SHORT).show();
    }


    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

//    public void updateTotalCash(){
//
//        for(Order order : cart){
//            total += (Integer.parseInt(order.getPrice())) * (Integer.parseInt(order.getQuantity()));
//        }
//
//        total2 = total;
//
//        Locale local = new Locale("en" , "US");
//        NumberFormat fmt = NumberFormat.getCurrencyInstance(local);
//
//        txtTotalPrice.setText(fmt.format(total));
//    }
}


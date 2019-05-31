package com.example.rooot.eatit_server;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rooot.eatit_server.Adapter.FirebaseAdapter;
import com.example.rooot.eatit_server.Common.CurrentUser;
import com.example.rooot.eatit_server.Interface.FirebaseCallBack;
import com.example.rooot.eatit_server.Model.Category;
import com.example.rooot.eatit_server.Service.ListenOrder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import info.hoang8f.widget.FButton;

public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener , FirebaseCallBack , AdapterView.OnItemSelectedListener {

    TextView txtName,txtDisc,txtMenuString;

    FirebaseDatabase database;
    DatabaseReference categories;
    FirebaseStorage storage;
    StorageReference storageReference;


    FirebaseRecyclerAdapter firebaseAdapter;


    RecyclerView recycler_menu;

    Spinner spinner;
    ArrayList<String> LIST_CATEGORIES, SAVEDLIST_CATEGORIES , SAVEDLIST_ID ;
    ArrayAdapter<String> adapterSpinner;

    boolean flag = false;

    Uri photoUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //Init Database
        database = FirebaseDatabase.getInstance();
        categories = database.getReference("Category");
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        //ArrayLists For saved Category
        //txtMenuString = findViewById(R.id.txtMenu);
        SAVEDLIST_CATEGORIES = new ArrayList<>();
        SAVEDLIST_ID = new ArrayList<>();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setLogo(R.drawable.ic_restaurant_menu_black_24dp);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                startActivity(new Intent(Home.this , AddNewCateogry.class));
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //get the header view
        View HeaderView = navigationView.getHeaderView(0);
        txtName = HeaderView.findViewById(R.id.nav_header_txtName);
        txtDisc = HeaderView.findViewById(R.id.nav_header_txtDisc);
        txtName.setText(CurrentUser.current_User.getName());
        txtName.setTypeface(Typeface.createFromAsset(getAssets() , "fonts/NABILA.TTF"));


        recycler_menu = findViewById(R.id.recycler_menu);
        recycler_menu.setLayoutManager(new LinearLayoutManager(this));
        recycler_menu.setHasFixedSize(true);

        loadCategories(this);

        //Spinner
        this.spinner = findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(this);
//        adapterSpinner = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, SAVEDLIST_CATEGORIES);
//        spinner.setVisibility(View.VISIBLE);
//        spinner.setPrompt("Choose category");
//        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//
//        spinner.setAdapter(adapterSpinner);

        //Register Service
        Intent intent = new Intent(Home.this , ListenOrder.class);
        startService(intent);

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        firebaseAdapter.stopListening();
        //adapterSpinner.notifyDataSetChanged();
    }

    @Override
    protected void onPause() {
        super.onPause();

        SharedPreferences sharedPreferences = getSharedPreferences("SPINNER_ITEM" , MODE_PRIVATE);
        sharedPreferences.edit().putInt("SPINNER_ITEM_INDEX" , spinner.getSelectedItemPosition()).apply();

        firebaseAdapter.stopListening();
        //adapterSpinner.clear();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("onResume" , "function called");

        if(flag) {

            firebaseAdapter.startListening();
            adapterSpinner.notifyDataSetChanged();

            SharedPreferences sharedPreferences = getSharedPreferences("SPINNER_ITEM", MODE_PRIVATE);

            int indx;

            if (sharedPreferences != null) {
                indx = sharedPreferences.getInt("SPINNER_ITEM_INDEX", 0);

                // We must set the adapter again to spinner
                // index to return to the category we were selected

//                loadCategories(this);
                //adapterSpinner.notifyDataSetChanged();
                spinner.setSelection(indx);


            }
        }

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_menu) {
            // Handle the camera action
        } else if (id == R.id.nav_cart) {

            startActivity(new Intent(this , OrderCartView.class));

        } else if (id == R.id.nav_orders) {

        } else if (id == R.id.nav_logout) {
            Intent intent = new Intent(Home.this , SignIn.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void loadCategories(final FirebaseCallBack firebaseCallBack) {

        categories = FirebaseDatabase.getInstance().getReference("Category");

        categories.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                LIST_CATEGORIES = new ArrayList<>();
                for (DataSnapshot postSnapshot1 : dataSnapshot.getChildren()) {
                    LIST_CATEGORIES.add(postSnapshot1.getKey());

                }

                firebaseCallBack.saveCategories(LIST_CATEGORIES); // we made an interface becuase LIST_CATEGORIES outside onDataChange
                // is always null so we need a custom call to save data
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void saveCategories(List<String> list) {

        SAVEDLIST_CATEGORIES = new ArrayList<>();

        for(int i=0; i<list.size();i++)
            SAVEDLIST_CATEGORIES.add(i,list.get(i));

        adapterSpinner = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, SAVEDLIST_CATEGORIES);
        spinner.setVisibility(View.VISIBLE);
        spinner.setPrompt("Choose category");
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        adapterSpinner.notifyDataSetChanged();

        spinner.setAdapter(adapterSpinner);


    }


    private void loadMenu(String selectedItem) {

        Query query = FirebaseDatabase.getInstance().getReference("Category/"+selectedItem);

        FirebaseRecyclerOptions<Category> options =
                new FirebaseRecyclerOptions.Builder<Category>()
                        .setQuery(query, Category.class)
                        .build();

        firebaseAdapter = new FirebaseAdapter(options);

        recycler_menu.setAdapter(firebaseAdapter);

    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String name = adapterView.getSelectedItem().toString();

        loadMenu(name);
        firebaseAdapter.startListening();

        // this flag to work with onResume
        // so the first time of application we want onResume to do anything so w put default value false
        // then after getting Category we will put flag true so if we transfered to second activity
        // it will call onPaused then when we hit back it will call onResume with flag true ^ ^
        flag = true;
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    //Update / Delete
    // onContextItemSelected for Context menu ( Context Menu that the menu which appear
                                            //  when long click on the item )

    @Override
    public boolean onContextItemSelected(MenuItem item) {


        if(item.getTitle().equals(CurrentUser.UPDATE)){
            //this is for key
            Log.i("test key : " , firebaseAdapter.getRef(item.getOrder()).getKey());

            //this is for refrence
            Log.i("test  : " ,  firebaseAdapter.getRef(item.getOrder()).toString());

            // we will send the key to know where we will update
            // and the item tob be updated
            showUpdateDialog(firebaseAdapter.getRef(item.getOrder()).getKey() , (Category) firebaseAdapter.getItem(item.getOrder()));
        }

        if(item.getTitle().equals(CurrentUser.DELETE_ITEM)){

            deleteItem(firebaseAdapter.getRef(item.getOrder()).getKey());
        }

        if(item.getTitle().equals(CurrentUser.DELETE_CATEGORY)){

            deleteCategory();
        }

        return super.onContextItemSelected(item);
    }

    private void showUpdateDialog(final String key, final Category item) {

        //get position of adapter
        final String child = spinner.getSelectedItem().toString();

        View view = LayoutInflater.from(this).inflate(R.layout.update_item,null);

        //Definitions
        final MaterialEditText edtItemName = view.findViewById(R.id.edtItemName);
        final MaterialEditText edtItemHint = view.findViewById(R.id.edtItemHint);
        final MaterialEditText edtItemPrice = view.findViewById(R.id.edtItemPrice);

        final FButton btnSelect = view.findViewById(R.id.btnSelect);
        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectPhoto();
                btnSelect.setText("Image Selected");
            }
        });

        FButton btnUpload = view.findViewById(R.id.btnUpload);
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadPhoto(item);
            }
        });

        //set default values
        edtItemName.setText(item.getName());
        edtItemHint.setText(item.getHint());
        edtItemPrice.setText(item.getPrice());

        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Update item : "+item.getName());
        dialog.setView(view);

        dialog.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();

                item.setName(edtItemName.getText().toString());
                item.setHint(edtItemHint.getText().toString());
                item.setPrice(edtItemPrice.getText().toString());
                categories.child(child).child(key).setValue(item);
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



    private void selectPhoto() {
        Intent gallery = new Intent();
        gallery.setType("image/*");
        gallery.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(gallery,"Select Photo") , 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null){
            photoUri = data.getData();

        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void uploadPhoto(final Category item) {

        if(photoUri != null){

            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Uploading image...");
            progressDialog.show();

            String imageName = UUID.randomUUID().toString();
            final StorageReference imageFolder = storageReference.child("images/"+imageName);

            imageFolder.putFile(photoUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    progressDialog.dismiss();
                    Toast.makeText(Home.this, "Image Uploaded Successfully", Toast.LENGTH_SHORT).show();

                    // set value for download link if we want to download
                    imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            item.setLink(uri.toString());
                        }

                    });



                }

            });

            imageFolder.putFile(photoUri).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(Home.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            progressDialog.setMessage("Uploaded : "+progress+"%");

                        }
                    });
        }

    }

    private void deleteItem(String key) {

        categories.child(spinner.getSelectedItem().toString()).child(key).removeValue();

    }


    private void deleteCategory() {
        categories.child(spinner.getSelectedItem().toString()).removeValue();
        adapterSpinner.notifyDataSetChanged();
        firebaseAdapter.notifyDataSetChanged();
    }

    
}

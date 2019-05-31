package com.example.rooot.eatit_server;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.rooot.eatit_server.Model.Category;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.UUID;

import info.hoang8f.widget.FButton;

public class AddNewCateogry extends AppCompatActivity {

    FirebaseDatabase database;
    DatabaseReference categories;
    FirebaseStorage storage;
    StorageReference storageReference;

    //Add new menu layout
    MaterialEditText edtCategoryName , edtItemId;
    MaterialEditText edtItemName , edtItemHint;
    MaterialEditText edtItemPrice;

    FButton btnSelect ,btnUpload, btnCancel , btnAddToDatabase;
    Category newCateogry;
    Uri savedUri;
    private final int PICK_IMAGE_REQUEST = 71;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_cateogry);

        //Init Database
        database = FirebaseDatabase.getInstance();
        categories = database.getReference("Category");
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();


        edtCategoryName= findViewById(R.id.edtCategoryName);
        edtItemId = findViewById(R.id.edtItemID);
        edtItemName = findViewById(R.id.edtItemName);
        edtItemHint = findViewById(R.id.edtItemHint);
        edtItemPrice = findViewById(R.id.edtItemPrice);

        btnSelect = findViewById(R.id.btnSelect);
        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage();
            }
        });

        btnCancel = findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btnUpload = findViewById(R.id.btnUpload);
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    uploadPhoto();

            }
        });

        btnAddToDatabase = findViewById(R.id.btnAddToDatabase);
        btnAddToDatabase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkEditTexts()){

                    if(newCateogry != null){

                        categories.child(edtCategoryName.getText().toString())
                                  .child(edtItemId.getText().toString()).setValue(newCateogry);

                        Toast.makeText(AddNewCateogry.this, "Data Saved Successfully", Toast.LENGTH_SHORT).show();

                        finish();

                    }else{
                        Toast.makeText(AddNewCateogry.this, "Error", Toast.LENGTH_SHORT).show();
                    }
                }else
                    Toast.makeText(AddNewCateogry.this, "One or more fields are empty", Toast.LENGTH_SHORT).show();

            }
        });
    }



    private void chooseImage() {

        Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*"); // show only  images not videos
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(galleryIntent , "Select Picture"),PICK_IMAGE_REQUEST);
        //show the chooser (if there are multiple options available)
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data !=null && data.getData() != null){

            savedUri = data.getData();
            btnSelect.setText("Image Selected");

        }
    }

    private boolean checkEditTexts() {
        if((edtCategoryName.getText().toString().equals(""))
                && (edtItemId.getText().toString().equals(""))
                && (edtItemName.getText().toString().equals(""))
                && (edtItemHint.getText().toString().equals(""))
                && (edtItemPrice.getText().toString().equals(""))){

            return false;


        }


        return true;
    }

    private void uploadPhoto() {

        // First Upload the photo
        if(savedUri != null){

            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Uploading image...");
            progressDialog.show();

            String imageName = UUID.randomUUID().toString();
            final StorageReference imageFolder = storageReference.child("images/"+imageName);

            imageFolder.putFile(savedUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    progressDialog.dismiss();
                    Toast.makeText(AddNewCateogry.this, "Image Uploaded Successfully", Toast.LENGTH_SHORT).show();

                    // set value for download link if we want to download
                    imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            newCateogry = new Category(
                                    edtItemHint.getText().toString(),
                                    uri.toString() ,
                                    edtItemName.getText().toString(),
                                    edtItemPrice.getText().toString()
                            );
                        }

                    });



                }

            });

                imageFolder.putFile(savedUri).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(AddNewCateogry.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
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
}

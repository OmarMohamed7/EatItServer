package com.example.rooot.eatit_server;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.rooot.eatit_server.Common.CurrentUser;
import com.example.rooot.eatit_server.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

public class SignIn extends AppCompatActivity {

    private EditText edtName , edtPassword;
    private Button btnSignIn;

    FirebaseDatabase database;
    DatabaseReference users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        edtName = (MaterialEditText) findViewById(R.id.edtName);
        edtPassword = (MaterialEditText) findViewById(R.id.edtPassword);

        btnSignIn = findViewById(R.id.btnSignIn);

        //Init Database
        database = FirebaseDatabase.getInstance();
        users = database.getReference("Users");

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(edtName.getText().equals("") || edtPassword.getText().equals("")){
                    Toast toast = Toast.makeText(SignIn.this , "Name|Password is empty" , Toast.LENGTH_LONG);
                    if(toast != null) toast.show();
                }else {
                    signInUser(edtName.getText().toString(), edtPassword.getText().toString());
                }
            }
        });

    }

    private void signInUser(String name, String password) {

        final ProgressDialog dialog = new ProgressDialog(SignIn.this);
        dialog.setMessage("Please wait...");
        dialog.show();

        final String localName = name;
        final String localPassword = password;
        users.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dialog.dismiss();

                if(dataSnapshot.child(localName).exists()){
                    User user = dataSnapshot.child(localName).getValue(User.class);
                    user.setName(localName);
                    if(Boolean.parseBoolean(user.getIsStaff())){

                        if(user.getPassword().equals(localPassword)){
                            // Login Ok
                            CurrentUser.current_User = user;
                            Toast.makeText(SignIn.this, "Login successful", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(SignIn.this , Home.class));

                        }else{

                            Toast.makeText(SignIn.this, "Wrong username/password.\nTry again !", Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(SignIn.this, "Wrong username/password.\nTry again !", Toast.LENGTH_SHORT).show();

                    }
                }else{
                    Toast.makeText(SignIn.this, "Wrong username/password.\nTry again !", Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}

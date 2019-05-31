package com.example.rooot.eatit_server;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    Button btnSignIn;
    TextView slogan;
    Typeface typeface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        InitUi();

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(MainActivity.this , SignIn.class));
            }
        });
    }

    private void InitUi() {

        btnSignIn = findViewById(R.id.signInbtn);
        slogan = findViewById(R.id.txtSlogan);

        typeface = Typeface.createFromAsset(getAssets() , "fonts/NABILA.TTF");

        slogan.setTypeface(typeface);

    }
}

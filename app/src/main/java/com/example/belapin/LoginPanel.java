package com.example.belapin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class LoginPanel extends AppCompatActivity {

    private ImageView adminlogin, penggunalogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_panel);

        penggunalogin = findViewById(R.id.penggunalogin);
        adminlogin = findViewById(R.id.adminlogin);

        penggunalogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginPanel.this, Login.class);
                startActivity(intent);
            }
        });

        adminlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginPanel.this, Login.class);
                startActivity(intent);
            }
        });

    }
}
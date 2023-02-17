package com.example.belapin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SplashScreen extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // fullscreen
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        firebaseAuth = FirebaseAuth.getInstance();

        setContentView(R.layout.splash_screen);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user==null) {
                    // user not logged
                    Intent intent = new Intent(SplashScreen.this, Login.class);
                    startActivity(intent);
                }
                else {
                    // check user type
                    CheckUserType();
                }
            }
        }, 2000);
    }

    private void CheckUserType() {
        // check if user is seller, start seller page, otherwise start user page
        DatabaseReference reference = FirebaseDatabase.getInstance("https://belapin2-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Users");
        reference.orderByChild("uid").equalTo(firebaseAuth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot s: snapshot.getChildren()) {
                    String tipeAkun = ""+s.child("tipeAkun").getValue();
                    if (tipeAkun.equals("Admin")) {
                        // login as admin
                        Intent intent = new Intent(SplashScreen.this, AdminPage.class);
                        startActivity(intent);
                        finish();
                    }
                    else {
                        // login as user
                        Intent intent = new Intent(SplashScreen.this, UserPage.class);
                        startActivity(intent);
                        finish();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
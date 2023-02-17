package com.example.belapin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class UserPage extends AppCompatActivity {

    private TextView namaAkun;
    private ImageButton tombolKeluar;

    private FirebaseAuth firebaseAuth;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_page);

        namaAkun = findViewById(R.id.namaAkun);
        tombolKeluar = findViewById(R.id.tombolKeluar);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Mohon Tunggu");
        progressDialog.setCanceledOnTouchOutside(false);

        firebaseAuth = FirebaseAuth.getInstance();
        checkUser();

        tombolKeluar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signOutUser();
            }
        });
    }

    private void signOutUser() {
        // logout
        progressDialog.setMessage("Keluar");

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("online","false");

        //update value to database
        DatabaseReference reference = FirebaseDatabase.getInstance("https://belapin2-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Users");
        reference.child(firebaseAuth.getUid()).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                // Update sukses
                firebaseAuth.signOut();
                checkUser();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // gagal update
                progressDialog.dismiss();
                Toast.makeText(UserPage.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void checkUser() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user==null) {
            startActivity(new Intent(UserPage.this, Login.class));
            finish();
        }
        else {
            loadDataUser();
        }
    }

    private void loadDataUser() {
        DatabaseReference reference = FirebaseDatabase.getInstance("https://belapin2-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Users");
        reference.orderByChild("uid").equalTo(firebaseAuth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot s: snapshot.getChildren()){
                    String name = ""+s.child("name").getValue();
                    String tipeAkun = ""+s.child("tipeAkun").getValue();

                    namaAkun.setText(name + " ("+tipeAkun+")");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
package com.example.belapin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class Login extends AppCompatActivity {

    private EditText emailAkun, passwordAkun;
    private TextView blmPunya, lupaPass;
    private Button tmblMasuk;

    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailAkun = findViewById(R.id.emailAkun);
        passwordAkun = findViewById(R.id.passwordAkun);
        blmPunya = findViewById(R.id.blmPunya);
        tmblMasuk = findViewById(R.id.tmblMasuk);
//        lupaPass = findViewById(R.id.lupaPass);

        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Mohon tunggu");
        progressDialog.setCanceledOnTouchOutside(false);


        blmPunya.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Login.this, DaftarPengguna.class);
                startActivity(intent);
            }
        });

//        lupaPass.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(Login.this, LupaPassword.class);
//                startActivity(intent);
//            }
//        });

        tmblMasuk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                masukUser();
            }
        });
    }

    private String email, password;
    private void masukUser() {

        email = emailAkun.getText().toString().trim();
        password = passwordAkun.getText().toString().trim();

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Email salah", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(password)){
            Toast.makeText(this, "Masukan password", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.setMessage("Masukkk");
        progressDialog.show();

        firebaseAuth.signInWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                berhasilMasuk();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Login.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void berhasilMasuk() {
        // after login, make user online
        progressDialog.setMessage("Sedang mengecek");

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("online","true");

        //update value to database
        DatabaseReference reference = FirebaseDatabase.getInstance("https://belapin2-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Users");
        reference.child(firebaseAuth.getUid()).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                // Update sukses
                CheckUserType();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // gagal update
                progressDialog.dismiss();
                Toast.makeText(Login.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
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
                        progressDialog.dismiss();
                        Intent intent = new Intent(Login.this, AdminPageActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    else {
                        // login as user
                        progressDialog.dismiss();
                        Intent intent = new Intent(Login.this, UserPage.class);
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
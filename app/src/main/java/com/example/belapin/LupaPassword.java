package com.example.belapin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

public class LupaPassword extends AppCompatActivity {

    private ImageButton tmblKembali;
    private EditText emailAkun;
    private Button tmblKonfirmasi;

    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lupa_password);

        emailAkun = findViewById(R.id.emailAkun);
        tmblKembali = findViewById(R.id.tmblKembali);

        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Mohon tunggu");

        tmblKembali.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                onBackPressed();
            }
        });

        tmblKonfirmasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ubahPassword();
            }
        });
    }

    private String email;
    private void ubahPassword() {
        email = emailAkun.getText().toString().trim();
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Email salah", Toast.LENGTH_SHORT).show();
            finish();
        }
        progressDialog.setMessage("Mengirim instruksi untuk reset password");
        progressDialog.show();

        firebaseAuth.sendPasswordResetEmail(email).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                // instruksi dikirim
                progressDialog.dismiss();
                Toast.makeText(LupaPassword.this, "Instruksi ganti passowrd dikirim ke email anda", Toast.LENGTH_SHORT).show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // gagal mengirim instruksi
                progressDialog.dismiss();
                Toast.makeText(LupaPassword.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }
}
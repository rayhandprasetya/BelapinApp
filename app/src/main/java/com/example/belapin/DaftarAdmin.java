package com.example.belapin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class DaftarAdmin extends AppCompatActivity {

    private ImageButton tmblKembali;
    private EditText namaAkun, emailAkun, passwordAkun, passwordKonfirmasi, alamatLengkap, namaToko;

    private Button tmblDaftar;

    private Uri image_uri;

    // permission constants
    private static final int LOCATION_REQUEST_CODE = 100;

    // permission arrays
    private String[] locationPermissions;

    private LocationManager locationManager;
    private double latitude, longitude;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daftar_admin);

        tmblKembali = findViewById(R.id.tmblKembali);
        tmblDaftar = findViewById(R.id.tmblDaftar);

        // EditText
        namaAkun = findViewById(R.id.namaAkun);
        emailAkun = findViewById(R.id.emailAkun);
        passwordAkun = findViewById(R.id.passwordAkun);
        passwordKonfirmasi = findViewById(R.id.passwordKonfirmasi);
        alamatLengkap = findViewById(R.id.alamatLengkap);

        namaToko = findViewById(R.id.namaToko);

        // init permission array
        locationPermissions = new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION};

        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Mohon Tunggu");
        progressDialog.setCanceledOnTouchOutside(false);

        tmblKembali.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        tmblDaftar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // tombol daftar
                inputData();
            }
        });
    }

    private String namaLengkap, toko, alamat, email, password, pasKon;
    private void inputData() {
        namaLengkap = namaAkun.getText().toString().trim();
        toko = namaToko.getText().toString().trim();
        alamat = alamatLengkap.getText().toString().trim();
        email = emailAkun.getText().toString().trim();
        password = passwordAkun.getText().toString().trim();
        pasKon = passwordKonfirmasi.getText().toString().trim();

        if (TextUtils.isEmpty(namaLengkap)){
            Toast.makeText(this, "Masukkan nama", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(toko)){
            Toast.makeText(this, "Masukkan nama Toko", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(alamat)){
            Toast.makeText(this, "Masukkan alamat", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            Toast.makeText(this, "Email tidak valid", Toast.LENGTH_SHORT).show();
            return;
        }
        if (password.length()<8){
            Toast.makeText(this, "Password minimal 8 karakter", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!password.equals(pasKon)){
            Toast.makeText(this, "Password harus sama", Toast.LENGTH_SHORT).show();
            return;
        }
        buatAkun();
    }

    private void buatAkun() {
        progressDialog.setMessage("Membuat akun....");
        progressDialog.show();

        //buat akun

        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                //akun terbuat
                saveDataFirebase();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(DaftarAdmin.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void saveDataFirebase() {
        progressDialog.setMessage("Menyimpan Akun");
        String timestamp = ""+System.currentTimeMillis();
        if (image_uri==null) {

            // setup data to save
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("uid",""+firebaseAuth.getUid());
            hashMap.put("email",""+email);
            hashMap.put("name",""+namaLengkap);
            hashMap.put("namaToko",""+toko);
            hashMap.put("alamat",""+alamat);
            hashMap.put("timestamp",""+timestamp);
            hashMap.put("tipeAkun","Admin");
            hashMap.put("online","true");
            hashMap.put("tokoBuka","true");

            DatabaseReference reference = FirebaseDatabase.getInstance("https://belapin2-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Users");
            reference.child(firebaseAuth.getUid()).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    // database updated
                    progressDialog.dismiss();
                    Intent intent = new Intent(DaftarAdmin.this, AdminPageActivity.class);
                    startActivity(intent);
                    finish();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // failed update database
                    progressDialog.dismiss();
                    Intent intent = new Intent(DaftarAdmin.this, AdminPageActivity.class);
                    startActivity(intent);
                    finish();
                }
            });
        }
    }

}
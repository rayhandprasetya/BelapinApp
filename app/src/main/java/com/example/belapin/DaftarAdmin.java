package com.example.belapin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
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
import java.util.List;
import java.util.Locale;

public class DaftarAdmin extends AppCompatActivity implements LocationListener {

    private ImageButton tmblKembali, tmblGps;
    private EditText namaAkun, emailAkun, passwordAkun, passwordKonfirmasi,
            negaraAkun, provinsiAkun, kotaAkun, alamatLengkap,
            nohp, namaToko, hargaOngkir;

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
        tmblGps = findViewById(R.id.tmblGps);
        tmblDaftar = findViewById(R.id.tmblDaftar);

        // EditText
        namaAkun = findViewById(R.id.namaAkun);
        emailAkun = findViewById(R.id.emailAkun);
        passwordAkun = findViewById(R.id.passwordAkun);
        passwordKonfirmasi = findViewById(R.id.passwordKonfirmasi);
        negaraAkun = findViewById(R.id.negaraAkun);
        provinsiAkun = findViewById(R.id.provinsiAkun);
        kotaAkun = findViewById(R.id.kotaAkun);
        alamatLengkap = findViewById(R.id.alamatLengkap);
        nohp = findViewById(R.id.nohp);
        hargaOngkir = findViewById(R.id.hargaOngkir);
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

        tmblGps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // deteksi lokasi sekarang
                if (checkLocationPermission()) {
                    // already allowed
                    detectLocation();
                }
                else {
                    // not allowed
                    requestLocationPermission();
                }
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

    private String namaLengkap, toko, nohandphone, ongkirfee, negara, kota, provinsi, alamat, email, password, pasKon;
    private void inputData() {
        namaLengkap = namaAkun.getText().toString().trim();
        toko = namaToko.getText().toString().trim();
        nohandphone = nohp.getText().toString().trim();
        ongkirfee = hargaOngkir.getText().toString().trim();
        negara = negaraAkun.getText().toString().trim();
        kota = kotaAkun.getText().toString().trim();
        provinsi = provinsiAkun.getText().toString().trim();
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
        if (TextUtils.isEmpty(nohandphone)){
            Toast.makeText(this, "Masukkan NO HP", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(ongkirfee)){
            Toast.makeText(this, "Masukkan jumlah free ongkir", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(negara)){
            Toast.makeText(this, "Masukkan negara", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(kota)){
            Toast.makeText(this, "Masukkan kota", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(provinsi)){
            Toast.makeText(this, "Masukkan provinsi", Toast.LENGTH_SHORT).show();
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
        if (latitude==0.0 || longitude==0.0){
            Toast.makeText(this, "Klik gps untuk lokasi", Toast.LENGTH_SHORT).show();
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
            hashMap.put("phone",""+nohandphone);
            hashMap.put("ongkir",""+ongkirfee);
            hashMap.put("negara",""+negara);
            hashMap.put("kota",""+kota);
            hashMap.put("provinsi",""+provinsi);
            hashMap.put("alamat",""+alamat);
            hashMap.put("latitude",""+latitude);
            hashMap.put("longitude",""+longitude);
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
                    Intent intent = new Intent(DaftarAdmin.this, AdminPage.class);
                    startActivity(intent);
                    finish();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // failed update database
                    progressDialog.dismiss();
                    Intent intent = new Intent(DaftarAdmin.this, AdminPage.class);
                    startActivity(intent);
                    finish();
                }
            });
        }
    }

    private void detectLocation() {
        Toast.makeText(this, "Please wait...", Toast.LENGTH_SHORT).show();

        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
    }

    private boolean checkLocationPermission() {
        boolean result = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(this, locationPermissions, LOCATION_REQUEST_CODE);
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        // location detected

        latitude = location.getLatitude();
        longitude = location.getLongitude();

        findAddress(); 
    }

    private void findAddress() {
        // find address, country, state

        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
            String alamat = addresses.get(0).getAddressLine(0);
            String kota = addresses.get(0).getLocality();
            String provinsi = addresses.get(0).getAdminArea();
            String negara = addresses.get(0).getCountryName();

            // set address
            negaraAkun.setText(negara);
            provinsiAkun.setText(provinsi);
            kotaAkun.setText(kota);
        }
        catch (Exception e){
            Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        LocationListener.super.onStatusChanged(provider, status, extras);
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {
//        Toast.makeText(this, "Please ", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {
        Toast.makeText(this, "Please turn on location", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case LOCATION_REQUEST_CODE: {
                if (grantResults.length>0) {
                    boolean locationAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (locationAccepted) {
                        // permission allowed
                        detectLocation();

                    }
                    else {
                        Toast.makeText(this, "Akftifkan lokasi di hp anda!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
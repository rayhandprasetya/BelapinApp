package com.example.belapin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

public class TambahBarang extends AppCompatActivity {

    private ImageButton tmblKembali;
    private ImageView tambahGambar;
    private EditText judul, deskripsi;
    private TextView kategori, kuantiti, harga, hargaDiskon, hargaDiskonNote;
    private SwitchCompat diskon;
    private Button tombolTambah;
    //permission constants
    private static final int CAMERA_REQUEST_CODE = 200;
    private static final int STORAGE_REQUEST_CODE = 300;
    //gambar constants
    private static final int IMAGE_PICK_GALLERY_CODE = 400;
    private static final int IMAGE_PICK_CAMERA_CODE = 500;
    //array permission
    private String[] cameraPermissions;
    private String[] storagePermissions;
    private Uri image_uri;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_barang);

        // init
        tmblKembali = findViewById(R.id.tmblKembali);
        tambahGambar = findViewById(R.id.tambahGambar);
        judul = findViewById(R.id.judul);
        deskripsi = findViewById(R.id.deskripsi);
        kategori = findViewById(R.id.kategori);
        kuantiti = findViewById(R.id.kuantiti);
        harga = findViewById(R.id.harga);
        diskon = findViewById(R.id.diskon);
        hargaDiskon = findViewById(R.id.hargaDiskon);
        hargaDiskonNote = findViewById(R.id.hargaDiskonNote);
        tombolTambah = findViewById(R.id.tombolTambah);

        // unchecked, hide
        hargaDiskon.setVisibility(View.GONE);
        hargaDiskonNote.setVisibility(View.GONE);

        firebaseAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Mohon Tunggu");
        progressDialog.setCanceledOnTouchOutside(false);

        cameraPermissions = new String[]{android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE};

        // if diskon is switched: bla bla
        diskon.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (checked) {
                    // show diskon harga and note
                    hargaDiskon.setVisibility(View.VISIBLE);
                    hargaDiskonNote.setVisibility(View.VISIBLE);
                }
                else {
                    // unchecked, hide
                    hargaDiskon.setVisibility(View.GONE);
                    hargaDiskonNote.setVisibility(View.GONE);
                }
            }
        });

        tambahGambar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // text show iamge
                showImageDialog();
            }
        });

        kategori.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                categoryDialog();
            }
        });

        tombolTambah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // input, validate, add
                inputData();
            }
        });

        tmblKembali.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private String barangJudul, barangDesc, barangKategori, barangKuantiti, hargaAsli, diskonHarga, diskonNote;
    private boolean diskonTersedia = false;
    private void inputData() {

        // input data
        barangJudul = judul.getText().toString().trim();
        barangDesc = deskripsi.getText().toString().trim();
        barangKategori = kategori.getText().toString().trim();
        barangKuantiti = kuantiti.getText().toString().trim();
        hargaAsli = harga.getText().toString().trim();
        diskonTersedia = diskon.isChecked();

        // validate data
        if (TextUtils.isEmpty(barangJudul)) {
            Toast.makeText(this, "Judul harus diisi", Toast.LENGTH_SHORT).show();
            return; // dont proceed further
        }
        if (TextUtils.isEmpty(barangDesc)) {
            Toast.makeText(this, "Deskripsi harus diisi", Toast.LENGTH_SHORT).show();
            return; // dont proceed further
        }
        if (TextUtils.isEmpty(barangKategori)) {
            Toast.makeText(this, "Kategori harus diisi", Toast.LENGTH_SHORT).show();
            return; // dont proceed further
        }
        if (diskonTersedia) {
            diskonHarga = hargaDiskon.getText().toString().trim();
            diskonNote = hargaDiskonNote.getText().toString().trim();
            if (TextUtils.isEmpty(diskonHarga)) {
                Toast.makeText(this, "Diskon Harga harus diisi", Toast.LENGTH_SHORT).show();
                return; // dont proceed further
            }
        }
        else {
            diskonHarga = "0";
            diskonNote = "";
        }

        tambahBarang();
    }

    private void tambahBarang() {
        // Data added to database
        progressDialog.setMessage("Menambah barang");
        progressDialog.show();

        final String timestamp = ""+System.currentTimeMillis();
        if (image_uri == null) {
            // upload without image
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("barangId", ""+timestamp);
            hashMap.put("barangJudul", ""+barangJudul);
            hashMap.put("barangDeskripsi", ""+barangDesc);
            hashMap.put("barangKategori", ""+barangKategori);
            hashMap.put("barangKuantiti", ""+barangKuantiti);
            hashMap.put("hargaAsli", ""+hargaAsli);
            hashMap.put("barangIcon", ""); // no image, set empty
            hashMap.put("hargaDiskon", ""+hargaDiskon);
            hashMap.put("hargaDiskonNote", ""+hargaDiskonNote);
            hashMap.put("diskonTersedia", ""+diskonTersedia);
            hashMap.put("timestamp", ""+timestamp);
            hashMap.put("uid", ""+firebaseAuth.getUid());

            // add to database
            DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://belapin2-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Users");
            databaseReference.child(firebaseAuth.getUid()).child("Barang").child(timestamp).setValue(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            // success add data to db
                            progressDialog.dismiss();
                            Toast.makeText(TambahBarang.this, "Barang berhasil ditambahkan", Toast.LENGTH_SHORT).show();
                            clearData();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // fail add data to db
                            progressDialog.dismiss();
                            Toast.makeText(TambahBarang.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    });

        }
        else {
            // upload with image
            // up image to storage, name and path of image uploaded
            String filePath = "barang_gambar/" + "" + timestamp;
            StorageReference storageReference = FirebaseStorage.getInstance().getReference(filePath);
            storageReference.putFile(image_uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // success uplaod image and get url upload
                    Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                    while (!uriTask.isSuccessful());
                    Uri downloadImageUri = uriTask.getResult();

                    if (uriTask.isSuccessful()) {
                        // receive image url and upload to db
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("barangId", ""+timestamp);
                        hashMap.put("barangJudul", ""+barangJudul);
                        hashMap.put("barangDeskripsi", ""+barangDesc);
                        hashMap.put("barangKategori", ""+barangKategori);
                        hashMap.put("barangKuantiti", ""+barangKuantiti);
                        hashMap.put("hargaAsli", ""+hargaAsli);
                        hashMap.put("barangIcon", ""+downloadImageUri);
                        hashMap.put("hargaDiskon", ""+hargaDiskon);
                        hashMap.put("hargaDiskonNote", ""+hargaDiskonNote);
                        hashMap.put("diskonTersedia", ""+diskonTersedia);
                        hashMap.put("timestamp", ""+timestamp);
                        hashMap.put("uid", ""+firebaseAuth.getUid());

                        // add to database
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
                        databaseReference.child(firebaseAuth.getUid()).child("Barang").child(timestamp).setValue(hashMap)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        // success add data to db
                                        progressDialog.dismiss();
                                        Toast.makeText(TambahBarang.this, "Barang berhasil ditambahkan", Toast.LENGTH_SHORT).show();
                                        clearData();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // fail add data to db
                                        progressDialog.dismiss();
                                        Toast.makeText(TambahBarang.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

                                    }
                                });


                    }

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // failed upload image
                    progressDialog.dismiss();
                    Toast.makeText(TambahBarang.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

                }
            });
        }

    }

    private void clearData() {
        // clear data after uploading barang
        judul.setText("");
        deskripsi.setText("");
        kategori.setText("");
        kuantiti.setText("");
        harga.setText("");
        hargaDiskon.setText("");
        hargaDiskonNote.setText("");
        tambahGambar.setImageResource(R.drawable.ic_add);
        image_uri = null;
    }

    private void categoryDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Kategori Barang").setItems(Constants.kategoriBarang, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                // get category
                String category = Constants.kategoriBarang[i];

                kategori.setText(category);

            }
        }).show();
    }

    private void showImageDialog() {
        String[] options = {"Kamera", "Galeri"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pilih gambar").setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == 0) {
                    // kamera diklik
                    if (cameraChecking()){
                        // permission granted
                        imageCamera();
                    }
                    else {
                        cameraRequest();
                    }
                }
                else {
                    // galeri diklik
                    if (storageChecking()){
                        // permission granted
                        imageGallery();
                    }
                    else {
                        storageRequest();
                    }
                }
            }
        }).show();
    }

    private void imageGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/+");
        startActivityForResult(intent, IMAGE_PICK_GALLERY_CODE);
    }

    private void imageCamera() {

        // media store to pick image
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE, "Gambar sementara");
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, "Gambar desc semetanra");

        image_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(intent, IMAGE_PICK_CAMERA_CODE);

    }

    private boolean storageChecking() {
        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private void storageRequest() {
        ActivityCompat.requestPermissions(this, storagePermissions, STORAGE_REQUEST_CODE);
    }

    private boolean cameraChecking() {
        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) ==
                (PackageManager.PERMISSION_GRANTED);

        boolean result2 = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) ==
                (PackageManager.PERMISSION_GRANTED);

        return result && result2;
    }

    private void cameraRequest() {
        ActivityCompat.requestPermissions(this, cameraPermissions, CAMERA_REQUEST_CODE);
    }

    // permission result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case CAMERA_REQUEST_CODE: {
                if (grantResults.length>0) {
                    boolean cameraAccapted  = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean storageAccapted  = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccapted && storageAccapted) {
                        // permission granted
                        imageCamera();
                    }
                    else {
                        // permission one or both not granted
                        Toast.makeText(this, "Izinkan aplikasi untuk mengakses kamera dan storage", Toast.LENGTH_SHORT).show();
                    }

                }
            }
            case STORAGE_REQUEST_CODE: {
                if (grantResults.length>0) {
                    boolean storageAccapted  = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (storageAccapted) {
                        // app has permission to access gallery
                        imageGallery();
                    }
                    else {
                        // dont have permission acces to storage
                        Toast.makeText(this, "Izinkan aplikasi untuk mengakses storage", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    // image pick
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == IMAGE_PICK_GALLERY_CODE) {

                // save data
                image_uri = data.getData();

                // set image
                tambahGambar.setImageURI(image_uri);
            }
            else if (requestCode == IMAGE_PICK_CAMERA_CODE) {
                tambahGambar.setImageURI(image_uri);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
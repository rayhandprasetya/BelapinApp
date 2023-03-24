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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class EditBarang extends AppCompatActivity {


    private ImageButton tmblKembali;
    private ImageView tambahGambar;
    private EditText judul, deskripsi;
    private TextView kategori, kuantiti, harga, hargaDiskon, hargaDiskonNote;
    private SwitchCompat diskon;
    private Button tombolEdit;
    private String barangId;
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
        setContentView(R.layout.activity_edit_barang);

        tmblKembali = findViewById(R.id.tmblKembali);
        tambahGambar = findViewById(R.id.tambahGambar);
        judul = findViewById(R.id.judul);
        deskripsi = findViewById(R.id.deskripsi);
        kategori = findViewById(R.id.kategori);
//        kuantiti = findViewById(R.id.kuantiti);
        harga = findViewById(R.id.harga);
//        diskon = findViewById(R.id.diskon);
//        hargaDiskon = findViewById(R.id.hargaDiskon);
//        hargaDiskonNote = findViewById(R.id.hargaDiskonNote);
        tombolEdit = findViewById(R.id.tombolEdit);

        // get id of barang
        barangId = getIntent().getStringExtra("barangId");

        // unchecked, hide
//        hargaDiskon.setVisibility(View.GONE);
//        hargaDiskonNote.setVisibility(View.GONE);

        firebaseAuth = FirebaseAuth.getInstance();

        loadBarangDetail(); // to set on views
        

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Mohon Tunggu");
        progressDialog.setCanceledOnTouchOutside(false);

        cameraPermissions = new String[]{android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE};

//        diskon.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
//                if (checked) {
//                    // show diskon harga and note
//                    hargaDiskon.setVisibility(View.VISIBLE);
//                    hargaDiskonNote.setVisibility(View.VISIBLE);
//                }
//                else {
//                    // unchecked, hide
//                    hargaDiskon.setVisibility(View.GONE);
//                    hargaDiskonNote.setVisibility(View.GONE);
//                }
//            }
//        });

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

        tombolEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // input, validate, update
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

    private void loadBarangDetail() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://belapin2-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Users");
        databaseReference.child((firebaseAuth.getUid())).child("Barang").child(barangId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // get data
                String barangId = ""+snapshot.child("barangId").getValue();
                String barangJudul = ""+snapshot.child("barangJudul").getValue();
                String barangDeskripsi = ""+snapshot.child("barangDeskripsi").getValue();
                String barangKategori = ""+snapshot.child("barangKategori").getValue();
//                String barangKuantiti = ""+snapshot.child("barangKuantiti").getValue();
                String hargaAsli = ""+snapshot.child("hargaAsli").getValue();
                String barangIcon = ""+snapshot.child("barangIcon").getValue();
                String hargaDiskonLain = ""+snapshot.child("hargaDiskon").getValue();
                String hargaDiskonNoteLain = ""+snapshot.child("hargaDiskonNote").getValue();
                String diskonTersedia = ""+snapshot.child("diskonTersedia").getValue();
                String timestamp = ""+snapshot.child("timestamp").getValue();
                String uid = ""+snapshot.child("uid").getValue();

                // set data to views
//                if (diskonTersedia.equals("true")) {
//                    diskon.setChecked(true);
//                    hargaDiskon.setVisibility(View.VISIBLE);
//                    hargaDiskonNote.setVisibility(View.VISIBLE);
//                }
//                else {
//                    diskon.setChecked(false);
//                    hargaDiskon.setVisibility(View.GONE);
//                    hargaDiskonNote.setVisibility(View.GONE);
//
//                }
                judul.setText(barangJudul);
                deskripsi.setText(barangDeskripsi);
                kategori.setText(barangKategori);
//                hargaDiskon.setText(hargaDiskonLain);
//                hargaDiskonNote.setText(hargaDiskonNoteLain);
//                kuantiti.setText(barangKuantiti);
                harga.setText(hargaAsli);

                try {
                    Picasso.get().load(barangIcon).placeholder(R.drawable.ic_add).into(tambahGambar);
                }
                catch (Exception e) {
                    tambahGambar.setImageResource(R.drawable.ic_add);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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
//        barangKuantiti = kuantiti.getText().toString().trim();
        hargaAsli = harga.getText().toString().trim();
//        diskonTersedia = diskon.isChecked();

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
//        if (diskonTersedia) {
//            diskonHarga = hargaDiskon.getText().toString().trim();
//            diskonNote = hargaDiskonNote.getText().toString().trim();
//            if (TextUtils.isEmpty(diskonHarga)) {
//                Toast.makeText(this, "Diskon Harga harus diisi", Toast.LENGTH_SHORT).show();
//                return; // dont proceed further
//            }
//        }
//        else {
//            diskonHarga = "0";
//            diskonNote = "";
//        }

        updateBarang();

    }

    private void updateBarang() {
        // show progress
        progressDialog.setMessage("Mengupdate barang");
        progressDialog.show();

        if (image_uri == null) {

            // update without image

            // setup update in hashmap
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("barangJudul", "" + barangJudul);
            hashMap.put("barangDeskripsi", "" + barangDesc);
            hashMap.put("barangKategori", "" + barangKategori);
//            hashMap.put("barangKuantiti", "" + barangKuantiti);
            hashMap.put("hargaAsli", "" + hargaAsli);
//            hashMap.put("diskonTersedia", "" + diskonTersedia);
//            hashMap.put("diskonNote", "" + diskonNote);
//            hashMap.put("hargaDiskonLain", "" + diskonHarga);

            // update to db
            DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://belapin2-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Users");
            databaseReference.child(firebaseAuth.getUid()).child("Barang").child(barangId).updateChildren(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            // update success
                            progressDialog.dismiss();
                            Toast.makeText(EditBarang.this, "Edit berhasil", Toast.LENGTH_SHORT).show();

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // update fail
                            progressDialog.dismiss();
                            Toast.makeText(EditBarang.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
        else {
            // update with image
            // first upload image
            // image name and path on firebase storage
            String filePath = "barang_gambar/" + "" + barangId; // overide previous image using same id

            //update images
            StorageReference storageReference = FirebaseStorage.getInstance().getReference(filePath);
            storageReference.putFile(image_uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // image uploaded, get url of uploaded image
                    Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                    while (!uriTask.isSuccessful());
                    Uri downloadImageUri = uriTask.getResult();

                    if (uriTask.isSuccessful()) {
                        // setup update in hashmap
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("barangJudul", "" + barangJudul);
                        hashMap.put("barangDeskripsi", "" + barangDesc);
                        hashMap.put("barangKategori", "" + barangKategori);
                        hashMap.put("tambahGambar", "" + downloadImageUri);
//                        hashMap.put("barangKuantiti", "" + barangKuantiti);
                        hashMap.put("hargaAsli", "" + hargaAsli);
//                        hashMap.put("diskonTersedia", "" + diskonTersedia);
//                        hashMap.put("diskonNote", "" + diskonNote);
//                        hashMap.put("hargaDiskonLain", "" + diskonHarga);

                        // update to db
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://belapin2-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Users");
                        databaseReference.child(firebaseAuth.getUid()).child("Barang").child(barangId).updateChildren(hashMap)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        // update success
                                        progressDialog.dismiss();
                                        Toast.makeText(EditBarang.this, "Edit berhasil", Toast.LENGTH_SHORT).show();

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // update fail
                                        progressDialog.dismiss();
                                        Toast.makeText(EditBarang.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });

                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // upload failed
                    progressDialog.dismiss();
                    Toast.makeText(EditBarang.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        }
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
        intent.setType("image/*");
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
        boolean result = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private void storageRequest() {
        ActivityCompat.requestPermissions(this, storagePermissions, STORAGE_REQUEST_CODE);
    }
    private boolean cameraChecking() {
        boolean result = ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) ==
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
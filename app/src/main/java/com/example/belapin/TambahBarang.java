package com.example.belapin;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
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
import java.util.Map;

public class TambahBarang extends AppCompatActivity {

    private ImageButton tmblKembali;
    private ImageView tambahGambar;
    private EditText judul, deskripsi;
    private TextView kategori, harga, hargaDiskon, hargaDiskonNote;
    private SwitchCompat diskon;
    private Button tombolTambah;

    private static final String TAG = "PRODUCT_ADD_TAG";
    private Uri imageUri;
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
//        kuantiti = findViewById(R.id.kuantiti);
        harga = findViewById(R.id.harga);
//        diskon = findViewById(R.id.diskon);
//        hargaDiskon = findViewById(R.id.hargaDiskon);
//        hargaDiskonNote = findViewById(R.id.hargaDiskonNote);
        tombolTambah = findViewById(R.id.tombolTambah);

        // unchecked, hide
//        hargaDiskon.setVisibility(View.GONE);
//        hargaDiskonNote.setVisibility(View.GONE);

        firebaseAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Mohon Tunggu");
        progressDialog.setCanceledOnTouchOutside(false);


        // if diskon is switched: bla bla
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

        tambahBarang();
    }

    private void tambahBarang() {
        Log.d(TAG, "tambahBarang: ");
        // Data added to database
        progressDialog.setMessage("Menambah barang");
        progressDialog.show();

        final String timestamp = "" + System.currentTimeMillis();
        if (imageUri == null) {
            // upload without image
            Log.d(TAG, "tambahBarang: uploading without image");

            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("barangId", "" + timestamp);
            hashMap.put("barangJudul", "" + barangJudul);
            hashMap.put("barangDeskripsi", "" + barangDesc);
            hashMap.put("barangKategori", "" + barangKategori);
            hashMap.put("barangKuantiti", "" + barangKuantiti);
            hashMap.put("hargaAsli", "" + hargaAsli);
            hashMap.put("barangIcon", ""); // no image, set empty
            hashMap.put("timestamp", "" + timestamp);
            hashMap.put("uid", "" + firebaseAuth.getUid());
//            hashMap.put("hargaDiskon", ""+hargaDiskon);
//            hashMap.put("hargaDiskonNote", ""+hargaDiskonNote);
//            hashMap.put("diskonTersedia", ""+diskonTersedia);

            // add to database
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
            databaseReference
                    .child("" + firebaseAuth.getUid())
                    .child("Barang")
                    .child(timestamp)
                    .setValue(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            // success add data to db
                            Log.d(TAG, "onSuccess: uploaded");
                            progressDialog.dismiss();
                            Toast.makeText(TambahBarang.this, "Barang berhasil ditambahkan", Toast.LENGTH_SHORT).show();
                            clearData();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // fail add data to db
                            Log.e(TAG, "onFailure: ", e);
                            progressDialog.dismiss();
                            Toast.makeText(TambahBarang.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

        } else {
            Log.d(TAG, "tambahBarang: uploading with image...");

            // upload with image
            // up image to storage, name and path of image uploaded
            String filePathAndName = "barang_gambar/" + timestamp;
            StorageReference ref = FirebaseStorage.getInstance().getReference().child(filePathAndName);
            ref.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // success uplaod image and get url upload
                            Log.d(TAG, "onSuccess: image uploaded");
                            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                            while (!uriTask.isSuccessful()) ;
                            String imageUrl = uriTask.getResult().toString();

                            if (uriTask.isSuccessful()) {
                                // receive image url and upload to db
                                HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put("barangId", "" + timestamp);
                                hashMap.put("barangJudul", "" + barangJudul);
                                hashMap.put("barangDeskripsi", "" + barangDesc);
                                hashMap.put("barangKategori", "" + barangKategori);
                                hashMap.put("barangKuantiti", "" + barangKuantiti);
                                hashMap.put("hargaAsli", "" + hargaAsli);
                                hashMap.put("barangIcon", "" + imageUrl);
                                hashMap.put("timestamp", "" + timestamp);
                                hashMap.put("uid", "" + firebaseAuth.getUid());
//                        hashMap.put("hargaDiskon", ""+hargaDiskon);
//                        hashMap.put("hargaDiskonNote", ""+hargaDiskonNote);
//                        hashMap.put("diskonTersedia", ""+diskonTersedia);

                                // add to database
                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
                                databaseReference
                                        .child("" + firebaseAuth.getUid())
                                        .child("Barang")
                                        .child(timestamp)
                                        .setValue(hashMap)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                // success add data to db
                                                Log.d(TAG, "onSuccess: uploaded");
                                                progressDialog.dismiss();
                                                Toast.makeText(TambahBarang.this, "Barang berhasil ditambahkan", Toast.LENGTH_SHORT).show();
                                                clearData();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                // fail add data to db
                                                Log.e(TAG, "onFailure: ", e);
                                                progressDialog.dismiss();
                                                Toast.makeText(TambahBarang.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();

                                            }
                                        });
                            }

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // failed upload image
                            Log.e(TAG, "onFailure: ", e);
                            progressDialog.dismiss();
                            Toast.makeText(TambahBarang.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    });
        }

    }

    private void clearData() {
        // clear data after uploading barang
        judul.setText("");
        deskripsi.setText("");
        kategori.setText("");
//        kuantiti.setText("");
        harga.setText("");
//        hargaDiskon.setText("");
//        hargaDiskonNote.setText("");
        tambahGambar.setImageResource(R.drawable.ic_add);
        imageUri = null;
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
                    //Camera is clicked
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        //android version is 13 or above, only camera permission required
                        requestCameraPermissions.launch(new String[]{Manifest.permission.CAMERA});
                    } else {
                        //android version is below 13, need camera & storage permission
                        requestCameraPermissions.launch(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE});
                    }
                } else if (i == 1) {
                    //Gallery is clicked
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        //android version is 13 or above, no storage permission
                        pickImageGallery();
                    } else {
                        //android version is below 13, need storage permission
                        requestStoragePermission.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                    }

                }
            }
        }).show();
    }

    private void pickImageGallery() {
        Log.d(TAG, "pickImageGallery: ");
        //intent to pick image from gallery, will show all resources from where we can pick the image
        Intent intent = new Intent(Intent.ACTION_PICK);
        //set type of file we want to pick i.e. image
        intent.setType("image/*");
        galleryActivityResultLauncher.launch(intent);
    }

    private ActivityResultLauncher<Intent> galleryActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    //here we will receive the image, if picked
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        //image picked
                        Intent data = result.getData();
                        //get uri of the image picked
                        imageUri = data.getData();
                        Log.d(TAG, "onActivityResult: Picked image gallery: " + imageUri);

                        //set to imageview
                        Glide.with(TambahBarang.this)
                                .load(imageUri)
                                .placeholder(R.drawable.ic_add)
                                .into(tambahGambar);
                    } else {
                        //Cancelled
                        Toast.makeText(TambahBarang.this, "Cancelled...", Toast.LENGTH_SHORT).show();
                    }

                }
            }
    );

    private void pickImageCamera() {
        Log.d(TAG, "pickImageCamera: ");
        //get ready the image data to store in MediaStore
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE, "TEMP IMAGE TITLE");
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, "TEMP IMAGE DESCRIPTION");
        //store the camera image in imageUri variable
        imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

        //Intent to launch camera
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        cameraActivityResultLauncher.launch(intent);
    }

    private ActivityResultLauncher<Intent> cameraActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    //here we will receive the image, if taken from camera
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        //image is taken from camera
                        //we already have the image in imageUri using function pickImageCamera()
                        //save the picked image
                        Log.d(TAG, "onActivityResult: Picked image camera: " + imageUri);

                        //set to imageview
                        Glide.with(TambahBarang.this)
                                .load(imageUri)
                                .placeholder(R.drawable.ic_add)
                                .into(tambahGambar);
                    } else {
                        //Cancelled
                        Toast.makeText(TambahBarang.this, "Cancelled...", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

    private ActivityResultLauncher<String> requestStoragePermission = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            new ActivityResultCallback<Boolean>() {
                @Override
                public void onActivityResult(Boolean isGranted) {
                    Log.d(TAG, "onActivityResult: isGranted: " + isGranted);
                    //lets check if from permission dialog user have granted the permission or denied the result is in isGranted as true/false
                    if (isGranted) {
                        //user has granted permission so we can pick image from gallery
                        pickImageGallery();
                    } else {
                        //user denied permission so we can't pick image from gallery
                        Toast.makeText(TambahBarang.this, "Permission denied...", Toast.LENGTH_SHORT).show();

                    }
                }
            }
    );

    private ActivityResultLauncher<String[]> requestCameraPermissions = registerForActivityResult(
            new ActivityResultContracts.RequestMultiplePermissions(), new ActivityResultCallback<Map<String, Boolean>>() {
                @Override
                public void onActivityResult(Map<String, Boolean> result) {
                    Log.d(TAG, "onActivityResult: ");
                    Log.d(TAG, "onActivityResult: " + result.toString());
                    //let's check if Camera or Storage or both permissions granted or not from permission dialog
                    boolean areAllGranted = true;
                    for (Boolean isGranted : result.values()) {
                        Log.d(TAG, "onActivityResult: isGranted: " + isGranted);
                        areAllGranted = areAllGranted && isGranted;
                    }


                    if (areAllGranted) {
                        //Camera & Storage both permissions are granted, we can launch camera to take image
                        Log.d(TAG, "onActivityResult: All Granted e.g. Camera & Storage...");
                        pickImageCamera();
                    } else {
                        //Camera or Storage or both permissions denied
                        Log.d(TAG, "onActivityResult: Camera or Storage or both denied...");
                        Toast.makeText(TambahBarang.this, "Camera or Storage or both permissions denied...", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );
}
package com.example.belapin;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import java.util.Map;

public class EditBarang extends AppCompatActivity {


    private ImageButton tmblKembali;
    private ImageView tambahGambar;
    private EditText judul, deskripsi;
    private TextView kategori, kuantiti, harga, hargaDiskon, hargaDiskonNote;
    private SwitchCompat diskon;
    private Button tombolEdit;

    private static final String TAG = "EDIT_PRODUCT_TAG";
    private String barangId;
    private Uri imageUri = null;
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
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);

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
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.child((firebaseAuth.getUid())).child("Barang").child(barangId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // get data
                String barangId = "" + snapshot.child("barangId").getValue();
                String barangJudul = "" + snapshot.child("barangJudul").getValue();
                String barangDeskripsi = "" + snapshot.child("barangDeskripsi").getValue();
                String barangKategori = "" + snapshot.child("barangKategori").getValue();
//                String barangKuantiti = ""+snapshot.child("barangKuantiti").getValue();
                String hargaAsli = "" + snapshot.child("hargaAsli").getValue();
                String barangIcon = "" + snapshot.child("barangIcon").getValue();
                String hargaDiskonLain = "" + snapshot.child("hargaDiskon").getValue();
                String hargaDiskonNoteLain = "" + snapshot.child("hargaDiskonNote").getValue();
                String diskonTersedia = "" + snapshot.child("diskonTersedia").getValue();
                String timestamp = "" + snapshot.child("timestamp").getValue();
                String uid = "" + snapshot.child("uid").getValue();

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
                } catch (Exception e) {
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
            Toast.makeText(this, "Title is required", Toast.LENGTH_SHORT).show();
            return; // dont proceed further
        }
        if (TextUtils.isEmpty(barangDesc)) {
            Toast.makeText(this, "Description is required", Toast.LENGTH_SHORT).show();
            return; // dont proceed further
        }
        if (TextUtils.isEmpty(barangKategori)) {
            Toast.makeText(this, "Category is required", Toast.LENGTH_SHORT).show();
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
        progressDialog.setMessage("Updating product");
        progressDialog.show();

        if (imageUri == null) {

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
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
            databaseReference.child(firebaseAuth.getUid()).child("Barang").child(barangId).updateChildren(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            // update success
                            progressDialog.dismiss();
                            Toast.makeText(EditBarang.this, "Edit was successful", Toast.LENGTH_SHORT).show();

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // update fail
                            progressDialog.dismiss();
                            Toast.makeText(EditBarang.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            // update with image
            // first upload image
            // image name and path on firebase storage
            String filePath = "barang_gambar/" + "" + barangId; // overide previous image using same id

            //update images
            StorageReference storageReference = FirebaseStorage.getInstance().getReference(filePath);
            storageReference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // image uploaded, get url of uploaded image
                    Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                    while (!uriTask.isSuccessful()) ;
                    Uri downloadImageUri = uriTask.getResult();

                    if (uriTask.isSuccessful()) {
                        // setup update in hashmap
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("barangJudul", "" + barangJudul);
                        hashMap.put("barangDeskripsi", "" + barangDesc);
                        hashMap.put("barangKategori", "" + barangKategori);
                        hashMap.put("tambahGambar", "" + downloadImageUri);
                        hashMap.put("hargaAsli", "" + hargaAsli);
                        hashMap.put("barangIcon", "" + downloadImageUri);
//                        hashMap.put("barangKuantiti", "" + barangKuantiti);
//                        hashMap.put("diskonTersedia", "" + diskonTersedia);
//                        hashMap.put("diskonNote", "" + diskonNote);
//                        hashMap.put("hargaDiskonLain", "" + diskonHarga);

                        // update to db
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
                        databaseReference.child(firebaseAuth.getUid()).child("Barang").child(barangId).updateChildren(hashMap)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        // update success
                                        progressDialog.dismiss();
                                        Toast.makeText(EditBarang.this, "Edit was successful", Toast.LENGTH_SHORT).show();

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // update fail
                                        progressDialog.dismiss();
                                        Toast.makeText(EditBarang.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });

                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // upload failed
                    progressDialog.dismiss();
                    Toast.makeText(EditBarang.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        }
    }

    private void categoryDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Product Category").setItems(Constants.kategoriBarang, new DialogInterface.OnClickListener() {
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
        builder.setTitle("Choose image").setItems(options, new DialogInterface.OnClickListener() {
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
                        try {
                            Glide.with(EditBarang.this)
                                    .load(imageUri)
                                    .placeholder(R.drawable.ic_add)
                                    .into(tambahGambar);
                        } catch (Exception e) {
                            Log.e(TAG, "onActivityResult: ", e);
                        }
                    } else {
                        //Cancelled
                        Toast.makeText(EditBarang.this, "Cancelled...", Toast.LENGTH_SHORT).show();
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
                        try {
                            Glide.with(EditBarang.this)
                                    .load(imageUri)
                                    .placeholder(R.drawable.ic_add)
                                    .into(tambahGambar);
                        } catch (Exception e) {
                            Log.e(TAG, "onActivityResult: ", e);
                        }
                    } else {
                        //Cancelled
                        Toast.makeText(EditBarang.this, "Cancelled...", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(EditBarang.this, "Permission denied...", Toast.LENGTH_SHORT).show();

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
                        Toast.makeText(EditBarang.this, "Camera or Storage or both permissions denied...", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

}
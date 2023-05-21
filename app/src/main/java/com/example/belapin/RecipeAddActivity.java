package com.example.belapin;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RecipeAddActivity extends AppCompatActivity {

    private ImageView recipeIv;
    private EditText recipeNameEt;
    private EditText personCountEt;
    private EditText timeEt;
    private EditText ingredientsEt;
    private EditText howToEt;
    private RecyclerView productsRv;
    private Button submitBtn;

    private static final String TAG = "RECIPE_TAG";

    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    private ArrayList<ModelRecipeProductAdmin> recipeProductAdminArrayList;
    private AdapterRecipeProductAdmin adapterRecipeProductAdmin;

    private Uri imageUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_add);

        recipeIv = findViewById(R.id.recipeIv);
        recipeNameEt = findViewById(R.id.recipeNameEt);
        personCountEt = findViewById(R.id.personCountEt);
        timeEt = findViewById(R.id.timeEt);
        ingredientsEt = findViewById(R.id.ingredientsEt);
        howToEt = findViewById(R.id.howToEt);
        productsRv = findViewById(R.id.productsRv);
        submitBtn = findViewById(R.id.submitBtn);

        firebaseAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);

        loadProducts();

        recipeIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // text show iamge
                showImageDialog();
            }
        });

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateData();
            }
        });

    }

    private void loadProducts() {
        recipeProductAdminArrayList = new ArrayList<>();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child("" + firebaseAuth.getUid())
                .child("Barang")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        recipeProductAdminArrayList.clear();
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            ModelRecipeProductAdmin modelRecipeProductAdmin = ds.getValue(ModelRecipeProductAdmin.class);
                            recipeProductAdminArrayList.add(modelRecipeProductAdmin);
                        }
                        adapterRecipeProductAdmin = new AdapterRecipeProductAdmin(RecipeAddActivity.this, recipeProductAdminArrayList, new RvListenerRecipeProductAdmin() {
                            @Override
                            public void onProductCheckChangeListener(ModelRecipeProductAdmin modelRecipeProductAdmin, boolean isChecked) {

                            }
                        });
                        productsRv.setAdapter(adapterRecipeProductAdmin);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    String timestamp = "";
    private String recipeName = "";
    private String personCount = "";
    private String time = "";
    private String ingredients = "";
    private String howTo = "";

    private void validateData() {
        timestamp = "" + System.currentTimeMillis();
        recipeName = recipeNameEt.getText().toString().trim();
        personCount = personCountEt.getText().toString().trim();
        time = timeEt.getText().toString().trim();
        ingredients = ingredientsEt.getText().toString().trim();
        howTo = howToEt.getText().toString().trim();

        if (recipeName.isEmpty()) {
            Toast.makeText(this, "Enter recipe name", Toast.LENGTH_SHORT).show();
        } else if (personCount.isEmpty()) {
            Toast.makeText(this, "Enter person count", Toast.LENGTH_SHORT).show();
        } else if (time.isEmpty()) {
            Toast.makeText(this, "Enter time to cock", Toast.LENGTH_SHORT).show();
        } else if (ingredients.isEmpty()) {
            Toast.makeText(this, "Enter ingredients", Toast.LENGTH_SHORT).show();
        } else if (howTo.isEmpty()) {
            Toast.makeText(this, "Enter how to cock", Toast.LENGTH_SHORT).show();
        } else if (recipeProductAdminArrayList.isEmpty()) {
            Toast.makeText(this, "Choose at-least one product for recipe", Toast.LENGTH_SHORT).show();
        } else {
            if (imageUri == null) {
                addRecipeDb("");
            } else {
                uploadProfileImageStorage();
            }
        }
    }

    private void uploadProfileImageStorage() {
        progressDialog.setMessage("Updating image...");
        progressDialog.show();
        Log.d(TAG, "uploadProfileImageStorage: Updating image...");

        String filePathAndName = "RecipeImages/" + "recipe_" + timestamp;

        StorageReference ref = FirebaseStorage.getInstance().getReference().child(filePathAndName);
        ref.putFile(imageUri)
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                        double progress = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                        progressDialog.setMessage("Uploading image...");
                    }
                })
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        //image is uploaded to firebase storage, now get it's url
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isSuccessful()) ;

                        String imageUrl = uriTask.getResult().toString();

                        if (uriTask.isSuccessful()) {
                            addRecipeDb(imageUrl);
                        }

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "onFailure: ", e);
                        Toast.makeText(RecipeAddActivity.this, "Failed due to " + e.getMessage(), Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                    }
                });

    }

    private void addRecipeDb(String imageUrl) {
        progressDialog.setMessage("Adding recipe info...");
        progressDialog.show();
        Log.d(TAG, "updateProfileDb: Adding recipe info...");

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("recipeId", "" + timestamp);
        hashMap.put("timestamp", "" + timestamp);
        hashMap.put("recipeName", "" + recipeName);
        hashMap.put("personCount", "" + personCount);
        hashMap.put("time", "" + time);
        hashMap.put("ingredients", "" + ingredients);
        hashMap.put("howTo", "" + howTo);
        hashMap.put("imageUrl", "" + imageUrl);
        hashMap.put("uid", "" + firebaseAuth.getUid());

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child("" + firebaseAuth.getUid())
                .child("Recipes")
                .child(timestamp)
                .setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                        for (int i = 0; i < recipeProductAdminArrayList.size(); i++) {
                            boolean isChecked = recipeProductAdminArrayList.get(i).isChecked();

                            if (isChecked) {
                                String productId = recipeProductAdminArrayList.get(i).barangId;

                                HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put("productId", "" + productId);

                                DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference("Users");
                                ref1.child("" + firebaseAuth.getUid())
                                        .child("Recipes")
                                        .child(timestamp)
                                        .child("Barang")
                                        .child(productId)
                                        .setValue(hashMap);
                            }

                        }

                        Log.d(TAG, "onSuccess: Recipe added");
                        Toast.makeText(RecipeAddActivity.this, "Recipe added", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "onFailure: ", e);
                        Toast.makeText(RecipeAddActivity.this, "Failed due to " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                });
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
                        requestCameraPermissions.launch(new String[]{android.Manifest.permission.CAMERA});
                    } else {
                        //android version is below 13, need camera & storage permission
                        requestCameraPermissions.launch(new String[]{android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE});
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
                        Glide.with(RecipeAddActivity.this)
                                .load(imageUri)
                                .placeholder(R.drawable.ic_add)
                                .into(recipeIv);
                    } else {
                        //Cancelled
                        Toast.makeText(RecipeAddActivity.this, "Cancelled...", Toast.LENGTH_SHORT).show();
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
                        Glide.with(RecipeAddActivity.this)
                                .load(imageUri)
                                .placeholder(R.drawable.ic_add)
                                .into(recipeIv);
                    } else {
                        //Cancelled
                        Toast.makeText(RecipeAddActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(RecipeAddActivity.this, "Permission denied", Toast.LENGTH_SHORT).show();

                    }
                }
            }
    );

    private ActivityResultLauncher<String[]> requestCameraPermissions = registerForActivityResult(
            new ActivityResultContracts.RequestMultiplePermissions(),
            new ActivityResultCallback<Map<String, Boolean>>() {
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
                        Toast.makeText(RecipeAddActivity.this, "Camera or Storage or both permissions denied...", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );
}
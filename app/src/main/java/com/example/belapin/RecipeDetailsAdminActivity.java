package com.example.belapin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class RecipeDetailsAdminActivity extends AppCompatActivity {

    private ImageButton toolbarBackBtn, toolbarDeleteBtn;
    private ImageView recipeIv;
    private TextView recipeNameTv, recipePersonCountTv, recipeTimeTv, ingredientsTv, howToTv;
    private TextView tabMaterialsTv, tabIngredientsTv;
    private LinearLayout tabIngredientsLl, materialProductLl;
    private RecyclerView materialProductRv;

    private static final String TAG = "RECIPE_DETAILS_TAG";

    private FirebaseAuth firebaseAuth;

    String recipeId = "";

    private ArrayList<ModelBarang> barangList;
    private AdapterBarangAdmin adapterBarangAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_admin_details);

        toolbarBackBtn = findViewById(R.id.toolbarBackBtn);
        toolbarDeleteBtn = findViewById(R.id.toolbarDeleteBtn);
        recipeIv = findViewById(R.id.recipeIv);
        recipeNameTv = findViewById(R.id.recipeNameTv);
        recipePersonCountTv = findViewById(R.id.recipePersonCountTv);
        recipeTimeTv = findViewById(R.id.recipeTimeTv);
        ingredientsTv = findViewById(R.id.ingredientsTv);
        howToTv = findViewById(R.id.howToTv);
        tabMaterialsTv = findViewById(R.id.tabMaterialsTv);
        tabIngredientsTv = findViewById(R.id.tabIngredientsTv);
        tabIngredientsLl = findViewById(R.id.tabIngredientsLl);
        materialProductLl = findViewById(R.id.materialProductLl);
        materialProductRv = findViewById(R.id.materialProductRv);

        recipeNameTv.setText("");
        recipePersonCountTv.setText("");
        recipeTimeTv.setText("");
        ingredientsTv.setText("");
        howToTv.setText("");

        firebaseAuth = FirebaseAuth.getInstance();

        recipeId = getIntent().getStringExtra("recipeId");

        tabMaterialProducts();
        loadRecipeDetails();
        loadRecipeProducts();

        toolbarBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        toolbarDeleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(RecipeDetailsAdminActivity.this);
                alertDialog.setTitle("Delete Recipe!")
                        .setMessage("Are you sure you want to delete this recipe?")
                        .setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deleteRecipe();
                            }
                        })
                        .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show();
            }
        });

        tabMaterialsTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tabMaterialProducts();
            }
        });

        tabIngredientsTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tabIngredients();
            }
        });

    }

    private void loadRecipeDetails() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child("" + firebaseAuth.getUid())
                .child("Recipes")
                .child(recipeId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String recipeName = "" + snapshot.child("recipeName").getValue();
                        String personCount = "" + snapshot.child("personCount").getValue();
                        String time = "" + snapshot.child("time").getValue();
                        String ingredients = "" + snapshot.child("ingredients").getValue();
                        String howTo = "" + snapshot.child("howTo").getValue();
                        String imageUrl = "" + snapshot.child("imageUrl").getValue();
                        String timestamp = "" + snapshot.child("timestamp").getValue();
                        String uid = "" + snapshot.child("uid").getValue();

                        recipeNameTv.setText(recipeName);
                        recipePersonCountTv.setText(personCount);
                        recipeTimeTv.setText(time);
                        ingredientsTv.setText(ingredients);
                        howToTv.setText(howTo);

                        try {
                            Glide.with(RecipeDetailsAdminActivity.this)
                                    .load(imageUrl)
                                    .placeholder(R.drawable.ic_add)
                                    .into(recipeIv);
                        } catch (Exception e) {
                            Log.e(TAG, "onDataChange: ", e);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void loadRecipeProducts() {
        barangList = new ArrayList<>();
        // get all barang

        // setup adapter
        adapterBarangAdmin = new AdapterBarangAdmin(RecipeDetailsAdminActivity.this, barangList);
        // set adapter
        materialProductRv.setAdapter(adapterBarangAdmin);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child("" + firebaseAuth.getUid())
                .child("Recipes")
                .child(recipeId)
                .child("Barang")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        barangList.clear();

                        for (DataSnapshot ds : snapshot.getChildren()) {
                            String productId = "" + ds.child("productId").getValue();

                            DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference("Users");
                            ref1.child("" + firebaseAuth.getUid())
                                    .child("Barang")
                                    .child(productId)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            try {
                                                ModelBarang modelBarang = snapshot.getValue(ModelBarang.class);
                                                barangList.add(modelBarang);

                                                adapterBarangAdmin.notifyItemChanged(barangList.size() - 1);
                                            } catch (Exception e) {
                                                Log.e(TAG, "onDataChange: ", e);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void deleteRecipe() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child("" + firebaseAuth.getUid())
                .child("Recipes")
                .child(recipeId)
                .removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(RecipeDetailsAdminActivity.this, "Deleted...", Toast.LENGTH_SHORT).show();
                        onBackPressed();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(RecipeDetailsAdminActivity.this, "Failed to delete due to " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void tabMaterialProducts() {
        tabIngredientsLl.setVisibility(View.GONE);
        materialProductLl.setVisibility(View.VISIBLE);

        tabMaterialsTv.setTextColor(getResources().getColor(R.color.black));
        tabMaterialsTv.setBackgroundResource(R.drawable.shape_rect04);

        tabIngredientsTv.setTextColor(getResources().getColor(R.color.white));
        tabIngredientsTv.setBackgroundColor(getResources().getColor(android.R.color.transparent));
    }

    private void tabIngredients() {
        tabIngredientsLl.setVisibility(View.VISIBLE);
        materialProductLl.setVisibility(View.GONE);

        tabMaterialsTv.setTextColor(getResources().getColor(R.color.white));
        tabMaterialsTv.setBackgroundColor(getResources().getColor(android.R.color.transparent));

        tabIngredientsTv.setTextColor(getResources().getColor(R.color.black));
        tabIngredientsTv.setBackgroundResource(R.drawable.shape_rect04);
    }

}
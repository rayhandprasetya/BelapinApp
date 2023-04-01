package com.example.belapin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class AdminPageActivity extends AppCompatActivity {

    private TextView namaAkun, tabProductsTv, tabRecipesTv, tabOrdersTv;
    private ImageButton tombolKeluar, tambahBarang;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_page);

        namaAkun = findViewById(R.id.namaAkun);
//        emailAkun = findViewById(R.id.emailAkun);
        tombolKeluar = findViewById(R.id.tombolKeluar);
        tambahBarang = findViewById(R.id.tambahBarang);
        tabProductsTv = findViewById(R.id.tabProductsTv);
        tabRecipesTv = findViewById(R.id.tabRecipesTv);
//        tabOrdersTv = findViewById(R.id.tabOrdersTv);


        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);

        firebaseAuth = FirebaseAuth.getInstance();
        checkUser();

//        fragmentProducts();

        tombolKeluar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // sign out to login panel
                signOutAdmin();
            }
        });

        tambahBarang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNewOptionsDialog();
            }
        });

        tabProductsTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // load barang
                fragmentProducts();

            }
        });

        tabRecipesTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // load order
                fragmentRecipes();

            }
        });

//        tabOrdersTv.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                // load order
//                fragmentOrders();
//
//            }
//        });

    }

    private void addNewOptionsDialog() {
        PopupMenu popupMenu = new PopupMenu(this, tambahBarang);

        popupMenu.getMenu().add(Menu.NONE, 1, 1, "Product");
        popupMenu.getMenu().add(Menu.NONE, 2, 2, "Recipe");

        popupMenu.show();

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == 1) {
                    // add product
                    Intent intent = new Intent(AdminPageActivity.this, TambahBarang.class);
                    startActivity(intent);
                } else if (itemId == 2) {
                    // add Recipe
                    Intent intent = new Intent(AdminPageActivity.this, RecipeAddActivity.class);
                    startActivity(intent);
                }
                return false;
            }
        });
    }

//    private void showBarang() {
//        // show barang menu and hide order menu
//        barang.setVisibility(View.VISIBLE);
//        order.setVisibility(View.GONE);
//
//        tabBarang.setTextColor(getResources().getColor(R.color.black));
//        tabBarang.setBackgroundResource(R.drawable.shape_rect4);
//
//        tabOrder.setTextColor(getResources().getColor(R.color.white));
//        tabOrder.setBackgroundColor(getResources().getColor(android.R.color.transparent));
//    }

//    private void showOrder() {
//        // show order menu and hide barang menu
//        order.setVisibility(View.VISIBLE);
//        barang.setVisibility(View.GONE);
//
//        tabOrder.setTextColor(getResources().getColor(R.color.black));
//        tabOrder.setBackgroundResource(R.drawable.shape_rect4);
//
//        tabBarang.setTextColor(getResources().getColor(R.color.white));
//        tabBarang.setBackgroundColor(getResources().getColor(android.R.color.transparent));
//    }


    private void signOutAdmin() {
        // logout
        progressDialog.setMessage("Sign out");

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("online", "false");

        //update value to database
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                // Update sukses
                firebaseAuth.signOut();
                checkUser();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // gagal update
                progressDialog.dismiss();
                Toast.makeText(AdminPageActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void checkUser() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user == null) {
            startActivity(new Intent(AdminPageActivity.this, Login.class));
            finish();
        } else {
            loadDataUser();
        }
    }

    private void loadDataUser() {
        DatabaseReference reference = FirebaseDatabase.getInstance("https://belapin2-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Users");
        reference.orderByChild("uid").equalTo(firebaseAuth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot s : snapshot.getChildren()) {

                    // get data from database
                    String name = "" + s.child("name").getValue();
                    String tipeAkun = "" + s.child("tipeAkun").getValue();
                    String email = "" + s.child("email").getValue();
                    String toko = "" + s.child("namaToko").getValue();

                    // show data to screen
                    namaAkun.setText(name);
//                    namaAkun.setText(email);
                    namaAkun.setText(toko);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void fragmentProducts(){
        tabProductsTv.setTextColor(getResources().getColor(R.color.black));
        tabProductsTv.setBackgroundResource(R.drawable.shape_rect04);

        tabRecipesTv.setTextColor(getResources().getColor(R.color.white));
        tabRecipesTv.setBackgroundColor(getResources().getColor(android.R.color.transparent));

//        tabOrdersTv.setTextColor(getResources().getColor(R.color.white));
//        tabOrdersTv.setBackgroundColor(getResources().getColor(android.R.color.transparent));

        AdminProductsFragment fragment = new AdminProductsFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment, "AdminProductsFragment");  //create first framelayout with id fram in the activity where fragments will be displayed
        fragmentTransaction.commit();
    }
    private void fragmentRecipes(){
        tabProductsTv.setTextColor(getResources().getColor(R.color.white));
        tabProductsTv.setBackgroundColor(getResources().getColor(android.R.color.transparent));

        tabRecipesTv.setTextColor(getResources().getColor(R.color.black));
        tabRecipesTv.setBackgroundResource(R.drawable.shape_rect04);

//        tabOrdersTv.setTextColor(getResources().getColor(R.color.white));
//        tabOrdersTv.setBackgroundColor(getResources().getColor(android.R.color.transparent));

        AdminRecipesFragment fragment = new AdminRecipesFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment, "AdminRecipesFragment");  //create first framelayout with id fram in the activity where fragments will be displayed
        fragmentTransaction.commit();
    }

    private void fragmentOrders(){
        tabProductsTv.setTextColor(getResources().getColor(R.color.white));
        tabProductsTv.setBackgroundColor(getResources().getColor(android.R.color.transparent));

        tabRecipesTv.setTextColor(getResources().getColor(R.color.white));
        tabRecipesTv.setBackgroundColor(getResources().getColor(android.R.color.transparent));

//        tabOrdersTv.setTextColor(getResources().getColor(R.color.black));
//        tabOrdersTv.setBackgroundResource(R.drawable.shape_rect04);

        AdminOrdersFragment fragment = new AdminOrdersFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment, "AdminOrdersFragment");  //create first framelayout with id fram in the activity where fragments will be displayed
        fragmentTransaction.commit();
    }
}
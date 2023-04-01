package com.example.belapin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class UserPage extends AppCompatActivity {

    private TextView namaAkun, emailAkun;
    private ImageView tabToko, tabOrder, foodMenu;
    private ImageButton tombolKeluar;
    private RelativeLayout catalog, riwayat;
    private RecyclerView tokoTampilan, belanjaanHistory;

    private static final String TAG = "USER_TAG";

    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    private ArrayList<ModelPasar> tokoList;
    private AdapterPasar adapterToko;

    private ArrayList<ModelBelanjaan> belanjaanArrayList;
    private AdapterBelanjaanUser adapterBelanjaanUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_page);

        namaAkun = findViewById(R.id.namaAkun);
        emailAkun = findViewById(R.id.emailAkun);
        tabToko = findViewById(R.id.tabToko);
        tabOrder = findViewById(R.id.tabOrder);
        tombolKeluar = findViewById(R.id.tombolKeluar);
        catalog = findViewById(R.id.catalog);
        riwayat = findViewById(R.id.riwayat);
        tokoTampilan = findViewById(R.id.tokoTampilan);
//        foodMenu = findViewById(R.id.foodMenu);
        belanjaanHistory = findViewById(R.id.belanjaanHistory);
//        belanjaanHistory.setLayoutManager(new LinearLayoutManager(UserPage.this));

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);

        firebaseAuth = FirebaseAuth.getInstance();
        checkUser();

        showCatalog();

        tombolKeluar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signOutUser();
            }
        });

        tabToko.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // show toko
                showCatalog();
            }
        });

//        foodMenu.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(UserPage.this, FoodMenu.class);
//                startActivity(intent);
//            }
//        });

        tabOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // show pesanan
                showRiwayat();
            }
        });
    }

    private void showCatalog() {
        // show daftar barang, hide daftar barang
        catalog.setVisibility(View.VISIBLE);
        riwayat.setVisibility(View.GONE);

//        tabToko.setTextColor(getResources().getColor(R.color.black));
//        tabToko.setBackgroundResource(R.drawable.shape_rect4);
//
////        tabOrder.setTextColor(getResources().getColor(R.color.white));
//        tabOrder.setBackgroundColor(getResources().getColor(android.R.color.transparent));

//        belanjaanList = new ArrayList<>();
//        loadBelanjaan();
//        // set adapter to recycler view
//        adapterBelanjaanUser = new AdapterBelanjaanUser(this, belanjaanList);
//        belanjaanHistory.setAdapter(adapterBelanjaanUser);
    }

    private void showRiwayat() {
        // show riwayat order, hide riwayat order
        catalog.setVisibility(View.GONE);
        riwayat.setVisibility(View.VISIBLE);

//        tabToko.setTextColor(getResources().getColor(R.color.white));
//        tabToko.setBackgroundColor(getResources().getColor(android.R.color.transparent));
//
//        tabOrder.setTextColor(getResources().getColor(R.color.black));
//        tabOrder.setBackgroundResource(R.drawable.shape_rect4);
    }

    private void signOutUser() {
        // logout
        progressDialog.setMessage("Sign out");

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("online", "false");

        //update value to database
        DatabaseReference reference = FirebaseDatabase
                .getInstance("https://belapin2-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("Users");
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
                Toast.makeText(UserPage.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkUser() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user == null) {
            startActivity(new Intent(UserPage.this, Login.class));
            finish();
        } else {
            loadDataUser();
        }
    }

    private void loadDataUser() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.orderByChild("uid").equalTo(firebaseAuth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot s : snapshot.getChildren()) {

                    // get user data
                    String name = "" + s.child("name").getValue();
                    String email = "" + s.child("email").getValue();
                    String phone = "" + s.child("phone").getValue();
                    String kota = "" + s.child("kota").getValue();
                    String tipeAkun = "" + s.child("tipeAkun").getValue();

                    // set user data
                    namaAkun.setText(name);
                    emailAkun.setText(email);

                    loadToko(kota);
                    loadBelanjaan();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadBelanjaan() {
        // init belanjaan list
        belanjaanArrayList = new ArrayList<>();

        // get belanjaan
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                belanjaanArrayList.clear();
                for (DataSnapshot s : snapshot.getChildren()) {
                    String uid = "" + s.getRef().getKey();
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(uid).child("Belanjaan");
                    databaseReference.orderByChild("yangOrder").equalTo(firebaseAuth.getUid())
                            .addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()) {
                                        for (DataSnapshot s : snapshot.getChildren()) {
                                            try {
                                                ModelBelanjaan modelBelanjaan = s.getValue(ModelBelanjaan.class);
                                                // add to list
                                                belanjaanArrayList.add(modelBelanjaan);
                                            } catch (Exception e) {
                                                Log.e(TAG, "onDataChange: ", e);
                                            }
                                        }

                                        // setup adapter
                                        adapterBelanjaanUser = new AdapterBelanjaanUser(UserPage.this, belanjaanArrayList);

                                        // set to recycleview
                                        belanjaanHistory.setAdapter(adapterBelanjaanUser);
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

//    private void loadBelanjaan() {
//        // init belanjaan list
//        belanjaanList = new ArrayList<>();
//
//        // setup adapter
//        adapterBelanjaanUser = new AdapterBelanjaanUser(UserPage.this, belanjaanList);
//
//        // set adapter to RecyclerView
//        belanjaanHistory.setAdapter(adapterBelanjaanUser);
//
//        // get belanjaan
//        DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://belapin2-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Users");
//        databaseReference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                belanjaanList.clear();
//                for (DataSnapshot s: snapshot.getChildren()) {
//                    String uid = ""+s.getRef().getKey();
//                    DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://belapin2-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Users").child(uid).child("Belanjaan");
//                    databaseReference.orderByChild("yangOrder").equalTo(firebaseAuth.getUid()).addValueEventListener(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(@NonNull DataSnapshot snapshot) {
//                            if (snapshot.exists()) {
//                                for (DataSnapshot s: snapshot.getChildren()) {
//                                    ModelBelanjaan modelBelanjaan = s.getValue(ModelBelanjaan.class);
//
//                                    // add to list
//                                    belanjaanList.add(modelBelanjaan);
//                                }
//
//                                // update adapter data
//                                adapterBelanjaanUser.notifyDataSetChanged();
//                            }
//                        }
//                        @Override
//                        public void onCancelled(@NonNull DatabaseError error) {
//
//                        }
//                    });
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//    }

    private void loadToko(final String kotaSaya) {

        tokoList = new ArrayList<>();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.orderByChild("tipeAkun").equalTo("Admin")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        // clear list befor add
                        tokoList.clear();
                        for (DataSnapshot s : snapshot.getChildren()) {
                            try {
                                ModelPasar modelToko = s.getValue(ModelPasar.class);

//                    String tokoKota = ""+s.child("kota").getValue();
//
//                    // show only kota user
//                    if (tokoKota.equals(kotaSaya)) {
//                        tokoList.add(modelToko);
//                    }

//                     if want to display all shop, skip if statment and add this
                                tokoList.add(modelToko);
                            } catch (Exception e) {
                                Log.e(TAG, "onDataChange: ", e);
                            }
                        }

                        // setup adapter
                        adapterToko = new AdapterPasar(UserPage.this, tokoList);
                        // set adapter for recyclerview
                        tokoTampilan.setAdapter(adapterToko);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}
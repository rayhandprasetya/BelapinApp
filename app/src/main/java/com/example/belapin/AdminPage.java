package com.example.belapin;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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

public class AdminPage extends AppCompatActivity {

    private TextView namaAkun, emailAkun, namaToko, tabBarang, tabOrder, filteredBarang;
    private EditText searchBarang;
    private ImageButton tombolKeluar, tambahBarang, filterBarang;
    private RelativeLayout barang, order;
    private RecyclerView banyakBarang;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    private ArrayList<ModelBarang> barangList;
    private AdapterBarangAdmin adapterBarangAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_page);

        namaAkun = findViewById(R.id.namaAkun);
        emailAkun = findViewById(R.id.emailAkun);
        namaToko = findViewById(R.id.namaToko);
        tombolKeluar = findViewById(R.id.tombolKeluar);
        tambahBarang = findViewById(R.id.tambahBarang);
        tabBarang = findViewById(R.id.tabBarang);
        tabOrder = findViewById(R.id.tabOrder);
        barang = findViewById(R.id.barang);
        order = findViewById(R.id.order);
        searchBarang = findViewById(R.id.searchBarang);
        filterBarang = findViewById(R.id.filterBarang);
        filteredBarang = findViewById(R.id.filteredBarang);
        banyakBarang = findViewById(R.id.banyakBarang);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Mohon Tunggu");
        progressDialog.setCanceledOnTouchOutside(false);

        firebaseAuth = FirebaseAuth.getInstance();
        checkUser();

        loadAllBarang();

        showBarang();

        // search
        searchBarang.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try {
                    adapterBarangAdmin.getFilter().filter(charSequence);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

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
                // add product
                Intent intent = new Intent(AdminPage.this, TambahBarang.class);
                startActivity(intent);
            }
        });

        tabBarang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // load barang
                showBarang();

            }
        });

        tabOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // load order
                showOrder();

            }
        });
        filterBarang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(AdminPage.this);
                builder.setTitle("Pilih kategori").setItems(Constants.kategoriBarang1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // get selected item
                        String selected = Constants.kategoriBarang1[i];
                        filteredBarang.setText(selected);

                        if (selected.equals("All")) {
                            // load all barang
                            loadAllBarang();

                        }
                        else {
                            // load selected barang
                            loadFilteredBarang(selected);
                        }

                    }
                }).show();
            }
        });

    }

    private void loadFilteredBarang(String selected) {
        barangList = new ArrayList<>();
        // get all barang

        DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://belapin2-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Users");
        databaseReference.child(firebaseAuth.getUid()).child("Barang").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                // before getting list reset
                for (DataSnapshot s: snapshot.getChildren()) {
                    String barangKategori = ""+s.child("barangKategori").getValue();

                    // if selected category matches barang category the add it to list
                    if (selected.equals(barangKategori)) {
                        ModelBarang modelBarang = s.getValue(ModelBarang.class);
                        barangList.add(modelBarang);
                    }
                }

                // setup adapter
                adapterBarangAdmin = new AdapterBarangAdmin(AdminPage.this, barangList);

                // set adapter
                banyakBarang.setAdapter(adapterBarangAdmin);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadAllBarang() {
        barangList = new ArrayList<>();
        // get all barang

        DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://belapin2-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Users");
        databaseReference.child(firebaseAuth.getUid()).child("Barang").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                // before getting list reset
                for (DataSnapshot s: snapshot.getChildren()) {
                    ModelBarang modelBarang = s.getValue(ModelBarang.class);
                    barangList.add(modelBarang);
                }

                // setup adapter
                adapterBarangAdmin = new AdapterBarangAdmin(AdminPage.this, barangList);

                // set adapter
                banyakBarang.setAdapter(adapterBarangAdmin);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void showBarang() {
        // show barang menu and hide order menu
        barang.setVisibility(View.VISIBLE);
        order.setVisibility(View.GONE);

        tabBarang.setTextColor(getResources().getColor(R.color.black));
        tabBarang.setBackgroundResource(R.drawable.shape_rect4);

        tabOrder.setTextColor(getResources().getColor(R.color.white));
        tabOrder.setBackgroundColor(getResources().getColor(android.R.color.transparent));
    }

    private void showOrder() {
        // show order menu and hide barang menu
        order.setVisibility(View.VISIBLE);
        barang.setVisibility(View.GONE);

        tabOrder.setTextColor(getResources().getColor(R.color.black));
        tabOrder.setBackgroundResource(R.drawable.shape_rect4);

        tabBarang.setTextColor(getResources().getColor(R.color.white));
        tabBarang.setBackgroundColor(getResources().getColor(android.R.color.transparent));
    }


    private void signOutAdmin() {
        // logout
        progressDialog.setMessage("Keluar");

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("online","false");

        //update value to database
        DatabaseReference reference = FirebaseDatabase.getInstance("https://belapin2-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Users");
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
                Toast.makeText(AdminPage.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void checkUser() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user==null) {
            startActivity(new Intent(AdminPage.this, Login.class));
            finish();
        }
        else {
            loadDataUser();
        }
    }

    private void loadDataUser() {
        DatabaseReference reference = FirebaseDatabase.getInstance("https://belapin2-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Users");
        reference.orderByChild("uid").equalTo(firebaseAuth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot s: snapshot.getChildren()){

                    // get data from database
                    String name = ""+s.child("name").getValue();
                    String tipeAkun = ""+s.child("tipeAkun").getValue();
                    String email = ""+s.child("email").getValue();
                    String toko = ""+s.child("namaToko").getValue();

                    // show data to screen
                    namaAkun.setText(name);
                    namaAkun.setText(email);
                    namaAkun.setText(toko);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
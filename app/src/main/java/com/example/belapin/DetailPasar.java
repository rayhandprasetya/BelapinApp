package com.example.belapin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DetailPasar extends AppCompatActivity {

    private TextView namaPasar, alamatPasar, filteredBarang;
    private ImageButton tmblTambah, tmblKembali, filterBarang;
    private EditText searchBarang;
    private String tokoUid, namaPasarLain, alamatPasarLain;
    private RecyclerView banyakBarang;
    private FirebaseAuth firebaseAuth;
    private ArrayList<ModelBarang> barangList;
    private AdapterBarangUser adapterBarangUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_pasar);

        namaPasar = findViewById(R.id.namaPasar);
        alamatPasar = findViewById(R.id.alamatPasar);
        filteredBarang = findViewById(R.id.filteredBarang);
        tmblTambah = findViewById(R.id.tmblTambah);
        tmblKembali = findViewById(R.id.tmblKembali);
        filterBarang = findViewById(R.id.filterBarang);
        searchBarang = findViewById(R.id.searchBarang);
        banyakBarang = findViewById(R.id.banyakBarang);

        // get pasar uid from intent
        tokoUid = getIntent().getStringExtra("tokoUid");
        firebaseAuth = FirebaseAuth.getInstance();
        loadUser();
        loadPasar();
        loadBarang();

        tmblTambah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        tmblKembali.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        searchBarang.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try {
                    adapterBarangUser.getFilter().filter(charSequence);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        filterBarang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(DetailPasar.this);
                builder.setTitle("Pilih kategori").setItems(Constants.kategoriBarang1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // get selected item
                        String selected = Constants.kategoriBarang1[i];
                        filteredBarang.setText(selected);

                        if (selected.equals("All")) {
                            // load all barang
                            loadBarang();

                        }
                        else {
                            // load selected barang
                            adapterBarangUser.getFilter().filter(selected);
                        }

                    }
                }).show();

            }
        });

    }

    private void loadUser() {
        DatabaseReference reference = FirebaseDatabase.getInstance("https://belapin2-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Users");
        reference.orderByChild("uid").equalTo(firebaseAuth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot s: snapshot.getChildren()){

                    // get user data
                    String name = ""+s.child("name").getValue();
                    String email = ""+s.child("email").getValue();
                    String phone = ""+s.child("phone").getValue();
                    String kota = ""+s.child("kota").getValue();
                    String tipeAkun = ""+s.child("tipeAkun").getValue();

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void loadPasar() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://belapin2-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Users");
        databaseReference.child(tokoUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name = ""+snapshot.child("name").getValue();
                namaPasarLain = ""+snapshot.child("namaToko").getValue();
                alamatPasarLain = ""+snapshot.child("alamat").getValue();

                // set data
                namaPasar.setText(namaPasarLain);
                alamatPasar.setText(alamatPasarLain);
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }
    private void loadBarang() {
        // init list barang
        barangList = new ArrayList<>();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://belapin2-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Users");
        databaseReference.child(tokoUid).child("Barang").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // clear list before adding
                barangList.clear();
                for (DataSnapshot s: snapshot.getChildren()) {
                    ModelBarang modelBarang = s.getValue(ModelBarang.class);
                    barangList.add(modelBarang);
                }

                adapterBarangUser = new AdapterBarangUser(DetailPasar.this, barangList);
                banyakBarang.setAdapter(adapterBarangUser);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }




}
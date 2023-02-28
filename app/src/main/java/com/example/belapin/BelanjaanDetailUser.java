package com.example.belapin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;

public class BelanjaanDetailUser extends AppCompatActivity {

    private String pesananId, keOrder;
    private ImageButton tmblKembali;
    private RecyclerView barangRV;
    private TextView belanjaanIdTv, tanggalBelanja, statusBelanjaan, namaPasar, totalBarang, totalHarga;
    private FirebaseAuth firebaseAuth;
    private ArrayList<ModelBelanjaanDetail> belanjaanDetailArrayList;
    private AdapterPesananUser adapterPesananUser;

    private void setupRecyclerView() {
        // init list
        belanjaanDetailArrayList = new ArrayList<>();
        // setup adapter
        adapterPesananUser = new AdapterPesananUser(this, belanjaanDetailArrayList);
        barangRV.setAdapter(adapterPesananUser);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_belanjaan_detail_user);

        Intent intent = getIntent();

        keOrder = intent.getStringExtra("keOrder"); // contains uid of pasar where we place pesanan
        pesananId = intent.getStringExtra("pesananId");

        tmblKembali = findViewById(R.id.tmblKembali);
        belanjaanIdTv = findViewById(R.id.belanjaanIdTv);
        tanggalBelanja = findViewById(R.id.tanggalBelanja);
        statusBelanjaan = findViewById(R.id.statusBelanjaan);
        namaPasar = findViewById(R.id.namaPasar);
        totalBarang = findViewById(R.id.totalBarang);
        totalHarga = findViewById(R.id.totalHarga);
        barangRV = findViewById(R.id.barangRV);
        barangRV.setLayoutManager(new LinearLayoutManager(this));
        setupRecyclerView();


        firebaseAuth = FirebaseAuth.getInstance();
        loadPasarInfo();
        loadBelanjaan();
        loadBelanjaanDetail();


        tmblKembali.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }

    private void loadBelanjaanDetail() {
        // init list
        belanjaanDetailArrayList = new ArrayList<>();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://belapin2-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Users");
        databaseReference.child(keOrder).child("Belanjaan").child(pesananId).child("Items")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        belanjaanDetailArrayList.clear(); // clear list before load item
                        for (DataSnapshot s: snapshot.getChildren()) {
                            ModelBelanjaanDetail modelBelanjaanDetail = s.getValue(ModelBelanjaanDetail.class);

                            // add to list

                            belanjaanDetailArrayList.add(modelBelanjaanDetail);
                        }
                        // all items added to list
                        // setup adapter
                        adapterPesananUser.notifyDataSetChanged();
//                        adapterPesananUser = new AdapterPesananUser(BelanjaanDetailUser.this, belanjaanDetailArrayList);
//                        barangRV.setAdapter(adapterPesananUser);
//
//                        // set hitung barang
//                        totalBarang.setText(""+snapshot.getChildrenCount());

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void loadBelanjaan() {

        // load belanjaan detail
        DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://belapin2-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Users");
        databaseReference.child("Belanjaan").child(pesananId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                // get data
                String yangBelanja = ""+snapshot.child("yangOrder").getValue();
                String costBelanja = ""+snapshot.child("cost").getValue();
                String idBelanja = ""+snapshot.child("pesananId").getValue();
                String statusBelanja = ""+snapshot.child("statusOrder").getValue();
                String belanjaKe = ""+snapshot.child("orderKe").getValue();
                String waktuBelanja = ""+snapshot.child("waktoOrder").getValue();

                // convert timestamp to date format
                Calendar calendar = Calendar.getInstance();

                if (waktuBelanja != null && !"null".equals(waktuBelanja) && !waktuBelanja.isEmpty()) {
                    calendar.setTimeInMillis(Long.parseLong(waktuBelanja));
                }
//                calendar.setTimeInMillis(Long.parseLong(waktuBelanja));
                String formatWaktu = DateFormat.format("dd/MM/yyyy", calendar).toString();

                belanjaanIdTv.setText(idBelanja);
                statusBelanjaan.setText(statusBelanja);
                tanggalBelanja.setText(formatWaktu);
                totalHarga.setText("Rp"+costBelanja);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadPasarInfo() {
        // get pasar info
        DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://belapin2-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Users");
        databaseReference.child(keOrder).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String pasarNama = ""+snapshot.child("namaToko").getValue();
                namaPasar.setText(pasarNama);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}
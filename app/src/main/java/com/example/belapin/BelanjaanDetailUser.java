package com.example.belapin;

import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

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

    private static final String TAG ="ORDER_DETAILS_TAG";

    private FirebaseAuth firebaseAuth;
    private ArrayList<ModelBelanjaanDetail> belanjaanDetailArrayList;
    private AdapterPesananUser adapterPesananUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_belanjaan_detail_user);

        tmblKembali = findViewById(R.id.tmblKembali);
        belanjaanIdTv = findViewById(R.id.belanjaanIdTv);
        tanggalBelanja = findViewById(R.id.tanggalBelanja);
        namaPasar = findViewById(R.id.namaPasar);
        totalBarang = findViewById(R.id.totalBarang);
        totalHarga = findViewById(R.id.totalHarga);
        barangRV = findViewById(R.id.barangRV);
        //statusBelanjaan = findViewById(R.id.statusBelanjaan);

        Intent intent = getIntent();

        keOrder = intent.getStringExtra("keOrder"); // contains uid of pasar where we place pesanan
        pesananId = intent.getStringExtra("pesananId");

        Log.d(TAG, "onCreate: keOrder: " + keOrder);
        Log.d(TAG, "onCreate: pesananId: " + pesananId);

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
        adapterPesananUser = new AdapterPesananUser(this, belanjaanDetailArrayList);
        barangRV.setAdapter(adapterPesananUser);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.child(keOrder) //orderToUid
                .child("Belanjaan")
                .child(pesananId) //orderId
                .child("Items")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Log.d(TAG, "onDataChange: Exists "+snapshot.exists());
                        Log.d(TAG, "onDataChange: Count "+snapshot.getChildrenCount());
                        // clear list before load item
                        belanjaanDetailArrayList.clear();

                        for (DataSnapshot ds : snapshot.getChildren()) {
                            Log.d(TAG, "onDataChange: Item Found");
                            ModelBelanjaanDetail modelBelanjaanDetail = ds.getValue(ModelBelanjaanDetail.class);
                            // add to list
                            belanjaanDetailArrayList.add(modelBelanjaanDetail);

                            adapterPesananUser.notifyItemInserted(belanjaanDetailArrayList.size() - 1);
                        }

                        // set hitung barang
                        totalBarang.setText("" + snapshot.getChildrenCount());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void loadBelanjaan() {

        // load belanjaan detail
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.child(keOrder)
                .child("Belanjaan")
                .child(pesananId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        // get data
                        String yangBelanja = "" + snapshot.child("yangOrder").getValue();
                        String costBelanja = "" + snapshot.child("cost").getValue();
                        String idBelanja = "" + snapshot.child("pesananId").getValue();
                        String statusBelanja = "" + snapshot.child("statusOrder").getValue();
                        String belanjaKe = "" + snapshot.child("orderKe").getValue();
                        String waktuBelanja = "" + snapshot.child("waktoOrder").getValue();

                        // convert timestamp to date format
                        Calendar calendar = Calendar.getInstance();

                        if (waktuBelanja != null && !"null".equals(waktuBelanja) && !waktuBelanja.isEmpty()) {
                            calendar.setTimeInMillis(Long.parseLong(waktuBelanja));
                        }
//                calendar.setTimeInMillis(Long.parseLong(waktuBelanja));
                        String formatWaktu = DateFormat.format("dd/MM/yyyy", calendar).toString();

                        belanjaanIdTv.setText(idBelanja);
//                statusBelanjaan.setText(statusBelanja);
                        tanggalBelanja.setText(formatWaktu);
                        totalHarga.setText("Rp" + costBelanja);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void loadPasarInfo() {
        // get pasar info
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.child(keOrder).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String pasarNama = "" + snapshot.child("namaToko").getValue();
                namaPasar.setText(pasarNama);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}
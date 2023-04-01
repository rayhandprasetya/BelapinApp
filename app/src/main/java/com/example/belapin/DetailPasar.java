package com.example.belapin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

import p32929.androideasysql_library.Column;
import p32929.androideasysql_library.EasyDB;

public class DetailPasar extends AppCompatActivity {

    private ImageButton tmblTambah, tmblKembali;
    private TextView totalBarangKeranjang, namaPasar, alamatPasar;

    private static final String TAG = "SHOP_DETAILS_TAG";

    private ProgressDialog progressDialog;

    private FirebaseAuth firebaseAuth;

    private String namaPasarLain = "";
    private String alamatPasarLain = "";
    private String phoneKu = "";
    public String tokoUid = "";

    EasyDB easyDB;

    private TextView tabProductsTv, tabRecipesTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_pasar);

        tabProductsTv = findViewById(R.id.tabProductsTv);
        tabRecipesTv = findViewById(R.id.tabRecipesTv);
        tmblKembali = findViewById(R.id.tmblKembali);
        tmblTambah = findViewById(R.id.tmblTambah);
        namaPasar = findViewById(R.id.namaPasar);
        alamatPasar = findViewById(R.id.alamatPasar);
        totalBarangKeranjang = findViewById(R.id.totalBarangKeranjang);

        // init dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);

        // get pasar uid from intent
        tokoUid = getIntent().getStringExtra("tokoUid");
        firebaseAuth = FirebaseAuth.getInstance();

        loadUser();
        loadPasar();
        fragmentProducts();

        // declere it to class level and init in on create
        easyDB = EasyDB.init(this, "DB_ITEMS")
                .setTableName("TABLE_BARANG")
                .addColumn(new Column("Barang_Id", new String[] {"text", "unique"}))
                .addColumn(new Column("Barang_BID", new String[] {"text", "not null"}))
                .addColumn(new Column("Barang_Nama", new String[] {"text", "not null"}))
                .addColumn(new Column("Barang_HargaSatuan", new String[] {"text", "not null"}))
                .addColumn(new Column("Barang_Harga", new String[] {"text", "not null"}))
                .addColumn(new Column("Barang_Kuantiti", new String[] {"text", "not null"}))
                .doneTableColumn();

        // each of pasar has its own product, if user add barang in different market
        // delete data whenever user open this activity
        hapusDataKeranjang();
        hitungKeranjang();

        tmblTambah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // show list barang di keranjang
                showKeranjangBarang();
            }
        });

        tmblKembali.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
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
    }

    private void fragmentProducts() {
        tabProductsTv.setTextColor(getResources().getColor(R.color.black));
        tabProductsTv.setBackgroundResource(R.drawable.shape_rect04);

        tabRecipesTv.setTextColor(getResources().getColor(R.color.white));
        tabRecipesTv.setBackgroundColor(getResources().getColor(android.R.color.transparent));

        UserProductsFragment fragment = new UserProductsFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment, "UserProductsFragment");  //create first framelayout with id fram in the activity where fragments will be displayed
        fragmentTransaction.commit();
    }

    private void fragmentRecipes() {
        tabProductsTv.setTextColor(getResources().getColor(R.color.white));
        tabProductsTv.setBackgroundColor(getResources().getColor(android.R.color.transparent));

        tabRecipesTv.setTextColor(getResources().getColor(R.color.black));
        tabRecipesTv.setBackgroundResource(R.drawable.shape_rect04);

        UserRecipesFragment fragment = new UserRecipesFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment, "UserRecipesFragment");  //create first framelayout with id fram in the activity where fragments will be displayed
        fragmentTransaction.commit();
    }

    private void hapusDataKeranjang() {
        // delete all records
        easyDB.deleteAllDataFromTable();
    }

    @Override
    protected void onResume() {
        super.onResume();
        hitungKeranjang();
    }

    public void hitungKeranjang() {
        // keep it public so can be accessed in adapter
        // get hitung keranjang
        int hitung = easyDB.getAllData().getCount();
        if (hitung <= 0) {
            // no barang in keranjang, then hide hitung keranjang textview
            totalBarangKeranjang.setVisibility(View.GONE);
        }
        else {
            // have barang in keranjang, then show hitung keranjang textview and set hitung
            totalBarangKeranjang.setVisibility(View.VISIBLE);
            totalBarangKeranjang.setText(""+hitung); // concatenate with string because cant set integer in textview
        }
    }

    public double totalSemuaHarga = 0.00;
    // access these views in adapter and making public
    public TextView totalSemua;
    private void showKeranjangBarang() {

        // init list
        keranjangBarangList = new ArrayList<>();

        // inflate keranjang layout
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_keranjang, null);

        // init views
        TextView namaPasar = view.findViewById(R.id.namaPasar);
        totalSemua = view.findViewById(R.id.totalHarga);
        RecyclerView keranjangBarang = view.findViewById(R.id.keranjangBarang);
        Button tombolCheckout = view.findViewById(R.id.tombolCheckout);

        // dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // set view to dialog
        builder.setView(view);

        namaPasar.setText(namaPasarLain);

        EasyDB easyDB = EasyDB.init(this, "DB_ITEMS")
                .setTableName("TABLE_BARANG")
                .addColumn(new Column("Barang_Id", new String[] {"text", "unique"}))
                .addColumn(new Column("Barang_BID", new String[] {"text", "not null"}))
                .addColumn(new Column("Barang_Nama", new String[] {"text", "not null"}))
                .addColumn(new Column("Barang_HargaSatuan", new String[] {"text", "not null"}))
                .addColumn(new Column("Barang_Harga", new String[] {"text", "not null"}))
                .addColumn(new Column("Barang_Kuantiti", new String[] {"text", "not null"}))
                .doneTableColumn();

        // get all records from database
        Cursor cursor = easyDB.getAllData();
        while (cursor.moveToNext()) {
            String id = cursor.getString(1);
            String bId = cursor.getString(2);
            String name = cursor.getString(3);
            String harga = cursor.getString(4);
            String biaya = cursor.getString(5);
            String kuantiti = cursor.getString(6);

            totalSemuaHarga = totalSemuaHarga + Double.parseDouble(biaya);

            ModelKeranjang modelKeranjang = new ModelKeranjang(""+id,
                    ""+bId,
                    ""+name,
                    ""+harga,
                    ""+biaya,
                    ""+kuantiti);
            keranjangBarangList.add(modelKeranjang);
        }

        // setup adapter
        adapterKeranjang = new AdapterKeranjang(this, keranjangBarangList);

        // set to recycle view
        keranjangBarang.setAdapter(adapterKeranjang);

        totalSemua.setText("Rp"+String.format("%.2f", totalSemuaHarga));

        // show dialog
        AlertDialog dialog = builder.create();
        dialog.show();

        // reset dialog total harga
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                totalSemuaHarga = 0.00;
            }
        });

        // list pesanan
        tombolCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (keranjangBarangList.size() == 0) {
                    Toast.makeText(DetailPasar.this, "No product in cart", Toast.LENGTH_SHORT).show();
                    return; // dont proceed further
                }
                confirmOrder();
            }
        });
    }

    private ArrayList<ModelKeranjang> keranjangBarangList;
    private AdapterKeranjang adapterKeranjang;
    private void confirmOrder() {
        // show progress dialog
        progressDialog.setMessage("Creating list of order...");
        progressDialog.show();

        // for order id and order date
        final String timestamp = ""+System.currentTimeMillis();
        String biaya = totalSemua.getText().toString().trim().replace("Rp", ""); // remove Rp if contains

        // setup order data
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("pesananId", ""+timestamp);
        hashMap.put("waktoOrder", ""+timestamp);
        hashMap.put("statusOrder", "Dalam proses...");
        hashMap.put("cost", ""+biaya);
        hashMap.put("yangOrder", ""+firebaseAuth.getUid());
        hashMap.put("orderKe", ""+tokoUid);

        // add to database
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(tokoUid).child("Belanjaan");
        databaseReference.child(timestamp).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                // order info added to order items
                for (int i = 0; i < keranjangBarangList.size(); i++) {
                    String bId = keranjangBarangList.get(i).getbId();
                    String name = keranjangBarangList.get(i).getName();
                    String cost = keranjangBarangList.get(i).getCost();
                    String harga = keranjangBarangList.get(i).getHarga();
                    String kuantiti = keranjangBarangList.get(i).getKuantiti();

                    HashMap<String, String> hashMap1 = new HashMap<>();
                    hashMap1.put("bId", bId);
                    hashMap1.put("name", name);
                    hashMap1.put("cost", cost);
                    hashMap1.put("harga", harga);
                    hashMap1.put("kuantiti", kuantiti);

                    databaseReference.child(timestamp).child("Items").child(bId).setValue(hashMap1);
                }

                progressDialog.dismiss();
                Toast.makeText(DetailPasar.this, "List of order has been saved", Toast.LENGTH_SHORT).show();

                // open belanjaan detail after placing pesanan
                Intent intent = new Intent(DetailPasar.this, BelanjaanDetailUser.class);
                intent.putExtra("pesananId", timestamp);
                intent.putExtra("keOrder", tokoUid);
                startActivity(intent);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                // failed save belanjaan
                progressDialog.dismiss();
                Toast.makeText(DetailPasar.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadUser() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.orderByChild("uid").equalTo(firebaseAuth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot s: snapshot.getChildren()){

                    // get user data
                    String name = ""+s.child("name").getValue();
                    String email = ""+s.child("email").getValue();
                    phoneKu = ""+s.child("phone").getValue();
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
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
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

}
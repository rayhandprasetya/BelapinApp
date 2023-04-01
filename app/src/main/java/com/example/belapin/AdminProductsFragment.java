package com.example.belapin;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AdminProductsFragment extends Fragment {

    private TextView emailAkun, filteredBarang;
    private EditText searchBarang;
    private ImageButton filterBarang;
    private RelativeLayout barang, order;
    private RecyclerView banyakBarang;

    private static final String TAG = "ADMIN_PRODUCTS_TAG";

    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    private ArrayList<ModelBarang> barangList;
    private AdapterBarangAdmin adapterBarangAdmin;

    private Context mContext;

    @Override
    public void onAttach(@NonNull Context context) {
        this.mContext = context;
        super.onAttach(context);
    }

    public AdminProductsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_admin_products, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


//        emailAkun = view.findViewById(R.id.emailAkun);
//        tabBarang = view.findViewById(R.id.tabBarang);
//        tabOrder = view.findViewById(R.id.tabOrder);
        barang = view.findViewById(R.id.barang);
        order = view.findViewById(R.id.order);
        searchBarang = view.findViewById(R.id.searchBarang);
        filterBarang = view.findViewById(R.id.filterBarang);
        filteredBarang = view.findViewById(R.id.filteredBarang);
        banyakBarang = view.findViewById(R.id.banyakBarang);

        progressDialog = new ProgressDialog(mContext);
        progressDialog.setTitle("Please Wait");
        progressDialog.setCanceledOnTouchOutside(false);

        firebaseAuth = FirebaseAuth.getInstance();
        checkUser();

        loadAllBarang();

//        showBarang();

        // search
        searchBarang.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try {
                    adapterBarangAdmin.getFilter().filter(charSequence);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

//        tabBarang.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                // load barang
//                showBarang();
//
//            }
//        });

//        tabOrder.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                // load order
//                showOrder();
//
//            }
//        });
        filterBarang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("Choose Category").setItems(Constants.kategoriBarang1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // get selected item
                        String selected = Constants.kategoriBarang1[i];
                        filteredBarang.setText(selected);

                        if (selected.equals("All")) {
                            // load all barang
                            loadAllBarang();

                        } else {
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

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.child(firebaseAuth.getUid()).child("Barang").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                // before getting list reset
                for (DataSnapshot s : snapshot.getChildren()) {
                    String barangKategori = "" + s.child("barangKategori").getValue();

                    // if selected category matches barang category the add it to list
                    if (selected.equals(barangKategori)) {
                        try {
                            ModelBarang modelBarang = s.getValue(ModelBarang.class);
                            barangList.add(modelBarang);
                        } catch (Exception e) {
                            Log.e(TAG, "onDataChange: ", e);
                        }
                    }
                }

                // setup adapter
                adapterBarangAdmin = new AdapterBarangAdmin(mContext, barangList);

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

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference
                .child("" + firebaseAuth.getUid())
                .child("Barang")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        // before getting list reset
                        barangList.clear();

                        for (DataSnapshot s : snapshot.getChildren()) {
                            try {
                                ModelBarang modelBarang = s.getValue(ModelBarang.class);
                                barangList.add(modelBarang);
                            } catch (Exception e) {
                                Log.e(TAG, "onDataChange: ", e);
                            }
                        }

                        // setup adapter
                        adapterBarangAdmin = new AdapterBarangAdmin(mContext, barangList);
                        // set adapter
                        banyakBarang.setAdapter(adapterBarangAdmin);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

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

    private void checkUser() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user == null) {
            startActivity(new Intent(mContext, Login.class));
            getActivity().finish();
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
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
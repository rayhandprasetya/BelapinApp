package com.example.belapin;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class UserProductsFragment extends Fragment {

    private RecyclerView productsRv;
    private EditText searchBarang;
    private ImageButton filterBarang;
    private TextView filteredBarang;

    private static final String TAG = "RECIPES_TAG";

    private Context mContext;

    private FirebaseAuth firebaseAuth;

    private ArrayList<ModelBarang> barangArrayList;

    private AdapterBarangUser adapterBarangUser;

    @Override
    public void onAttach(@NonNull Context context) {
        this.mContext = context;
        super.onAttach(context);
    }

    public UserProductsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_products, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        productsRv = view.findViewById(R.id.productsRv);
        searchBarang = view.findViewById(R.id.searchBarang);
        filterBarang = view.findViewById(R.id.filterBarang);
        filteredBarang = view.findViewById(R.id.filteredBarang);

        firebaseAuth = FirebaseAuth.getInstance();

        loadAllBarang();

        searchBarang.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try {
                    adapterBarangUser.getFilter().filter(charSequence);
                } catch (Exception e) {
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
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("Choose category").setItems(Constants.kategoriBarang1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // get selected item
                        String selected = Constants.kategoriBarang1[i];
                        filteredBarang.setText(selected);

                        if (selected.equals("All")) {
                            // load all products
                            loadAllBarang();
                        } else if (selected.equalsIgnoreCase("Price Lowest")) {
                            loadAllBarangPriceLowest();
                        } else if (selected.equalsIgnoreCase("Price Highest")) {
                            loadAllBarangPriceHighest();
                        } else {
                            // load selected products
                            loadFilteredBarang(selected);
                        }

                    }
                }).show();
            }
        });
    }


    private void loadAllBarang() {
        barangArrayList = new ArrayList<>();
        // get all barang

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference
                .child("" + ((DetailPasar) mContext).tokoUid)
                .child("Barang")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        // before getting list reset
                        barangArrayList.clear();

                        for (DataSnapshot s : snapshot.getChildren()) {
                            try {
                                ModelBarang modelBarang = s.getValue(ModelBarang.class);
                                barangArrayList.add(modelBarang);
                            } catch (Exception e) {
                                Log.e(TAG, "onDataChange: ", e);
                            }
                        }

                        // setup adapter
                        adapterBarangUser = new AdapterBarangUser(mContext, barangArrayList);
                        // set adapter
                        productsRv.setAdapter(adapterBarangUser);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
    private void loadAllBarangPriceLowest() {
        barangArrayList = new ArrayList<>();
        // get all barang

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference
                .child("" + ((DetailPasar) mContext).tokoUid)
                .child("Barang")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        // before getting list reset
                        barangArrayList.clear();

                        for (DataSnapshot s : snapshot.getChildren()) {
                            try {
                                ModelBarang modelBarang = s.getValue(ModelBarang.class);
                                barangArrayList.add(modelBarang);
                            } catch (Exception e) {
                                Log.e(TAG, "onDataChange: ", e);
                            }
                        }

                        // Bubble sort implementation for price lowest -> highest
                        for (int i = 0; i < barangArrayList.size() - 1; i++) {
                            for (int j = 0; j < barangArrayList.size() - i - 1; j++) {
                                if (Double.parseDouble(barangArrayList.get(j).getHargaAsli()) > Double.parseDouble(barangArrayList.get(j + 1).getHargaAsli())) {
                                    // Swap the elements
                                    ModelBarang temp = barangArrayList.get(j);
                                    barangArrayList.set(j, barangArrayList.get(j + 1));
                                    barangArrayList.set(j + 1, temp);
                                }
                            }
                        }

                        // setup adapter
                        adapterBarangUser = new AdapterBarangUser(mContext, barangArrayList);
                        // set adapter
                        productsRv.setAdapter(adapterBarangUser);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
    private void loadAllBarangPriceHighest() {
        barangArrayList = new ArrayList<>();
        // get all barang

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference
                .child("" + ((DetailPasar) mContext).tokoUid)
                .child("Barang")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        // before getting list reset
                        barangArrayList.clear();

                        for (DataSnapshot s : snapshot.getChildren()) {
                            try {
                                ModelBarang modelBarang = s.getValue(ModelBarang.class);
                                barangArrayList.add(modelBarang);
                            } catch (Exception e) {
                                Log.e(TAG, "onDataChange: ", e);
                            }
                        }

                        // Bubble sort implementation for price highest -> lowest
                        for (int i = 0; i < barangArrayList.size() - 1; i++) {
                            for (int j = 0; j < barangArrayList.size() - i - 1; j++) {
                                if (Double.parseDouble(barangArrayList.get(j).getHargaAsli()) < Double.parseDouble(barangArrayList.get(j + 1).getHargaAsli())) {
                                    // Swap the elements
                                    ModelBarang temp = barangArrayList.get(j);
                                    barangArrayList.set(j, barangArrayList.get(j + 1));
                                    barangArrayList.set(j + 1, temp);
                                }
                            }
                        }

                        // setup adapter
                        adapterBarangUser = new AdapterBarangUser(mContext, barangArrayList);
                        // set adapter
                        productsRv.setAdapter(adapterBarangUser);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void loadFilteredBarang(String selected) {
        barangArrayList = new ArrayList<>();
        // get all barang

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.child("" + ((DetailPasar) mContext).tokoUid).
                child("Barang").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        // before getting list reset
                        for (DataSnapshot s : snapshot.getChildren()) {
                            String barangKategori = "" + s.child("barangKategori").getValue();

                            // if selected category matches barang category the add it to list
                            if (selected.equals(barangKategori)) {
                                try {
                                    ModelBarang modelBarang = s.getValue(ModelBarang.class);
                                    barangArrayList.add(modelBarang);
                                } catch (Exception e) {
                                    Log.e(TAG, "onDataChange: ", e);
                                }
                            }
                        }

                        // setup adapter
                        adapterBarangUser = new AdapterBarangUser(mContext, barangArrayList);

                        // set adapter
                        productsRv.setAdapter(adapterBarangUser);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}
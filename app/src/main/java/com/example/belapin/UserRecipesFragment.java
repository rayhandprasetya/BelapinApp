package com.example.belapin;

import android.content.Context;
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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class UserRecipesFragment extends Fragment {

    private EditText searchEt;
    private RecyclerView recipesRv;
    private static final String TAG = "RECIPES_TAG";
    private Context mContext;

    private ArrayList<ModelRecipeUser> recipeAdminArrayList;
    private AdapterRecipeUser adapterRecipeAdmin;

    @Override
    public void onAttach(@NonNull Context context) {
        this.mContext = context;
        super.onAttach(context);
    }

    public UserRecipesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_recipes, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        searchEt = view.findViewById(R.id.searchEt);
        recipesRv = view.findViewById(R.id.recipesRv);

        loadRecipes();

        searchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try {
                    adapterRecipeAdmin.getFilter().filter(charSequence);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void loadRecipes() {
        recipeAdminArrayList = new ArrayList<>();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference
                .child("" + ((DetailPasar) mContext).tokoUid)
                .child("Recipes")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        // before getting list reset
                        recipeAdminArrayList.clear();

                        for (DataSnapshot s : snapshot.getChildren()) {
                            try {
                                ModelRecipeUser modelRecipeAdmin = s.getValue(ModelRecipeUser.class);
                                recipeAdminArrayList.add(modelRecipeAdmin);
                            } catch (Exception e) {
                                Log.e(TAG, "onDataChange: ", e);
                            }
                        }

                        // setup adapter
                        adapterRecipeAdmin = new AdapterRecipeUser(mContext, recipeAdminArrayList);
                        // set adapter
                        recipesRv.setAdapter(adapterRecipeAdmin);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}
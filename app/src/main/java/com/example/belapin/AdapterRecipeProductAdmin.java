package com.example.belapin;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class AdapterRecipeProductAdmin extends RecyclerView.Adapter<AdapterRecipeProductAdmin.HolderRecipeProductAdmin> {

    private Context context;
    private ArrayList<ModelRecipeProductAdmin> recipeProductAdminArrayList;
    private RvListenerRecipeProductAdmin rvListenerRecipeProductAdmin;

    private static final String TAG = "PRODUCTS_TAG";

    public AdapterRecipeProductAdmin(Context context, ArrayList<ModelRecipeProductAdmin> recipeProductAdminArrayList, RvListenerRecipeProductAdmin rvListenerRecipeProductAdmin) {
        this.context = context;
        this.recipeProductAdminArrayList = recipeProductAdminArrayList;
        this.rvListenerRecipeProductAdmin = rvListenerRecipeProductAdmin;
    }

    @NonNull
    @Override
    public HolderRecipeProductAdmin onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.baris_product_recipe_admin, parent, false);
        return new HolderRecipeProductAdmin(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderRecipeProductAdmin holder, int position) {
        ModelRecipeProductAdmin modelRecipeProductAdmin = recipeProductAdminArrayList.get(position);

        String barangIcon = modelRecipeProductAdmin.getBarangIcon();
        String barangId = modelRecipeProductAdmin.getBarangId();
        String barangJudul = modelRecipeProductAdmin.getBarangJudul();
        String uid = modelRecipeProductAdmin.getUid();
        String timestamp = modelRecipeProductAdmin.getTimestamp();
        String hargaAsli = modelRecipeProductAdmin.getHargaAsli();

        holder.judul.setText(barangJudul);
        holder.hargaAsli.setText(hargaAsli);

        try {
            Glide.with(context)
                    .load(barangIcon)
                    .placeholder(R.drawable.ic_add)
                    .into(holder.gambarBarang);
        }
        catch (Exception e){
            Log.e(TAG, "onBindViewHolder: ", e);
        }

        holder.productCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                modelRecipeProductAdmin.setChecked(isChecked);
                rvListenerRecipeProductAdmin.onProductCheckChangeListener(modelRecipeProductAdmin, isChecked);
            }
        });
    }

    @Override
    public int getItemCount() {
        return recipeProductAdminArrayList.size();
    }

    class HolderRecipeProductAdmin extends RecyclerView.ViewHolder{

        private CheckBox productCb;
        private ImageView gambarBarang;
        private TextView judul;
        private TextView hargaAsli;

        public HolderRecipeProductAdmin(@NonNull View itemView) {
            super(itemView);

            productCb = itemView.findViewById(R.id.productCb);
            gambarBarang = itemView.findViewById(R.id.gambarBarang);
            judul = itemView.findViewById(R.id.judul);
            hargaAsli = itemView.findViewById(R.id.hargaAsli);
        }
    }
}

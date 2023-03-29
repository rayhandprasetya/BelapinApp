package com.example.belapin;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class AdapterRecipeAdmin extends RecyclerView.Adapter<AdapterRecipeAdmin.HolderRecipeAdmin> implements Filterable {

    private Context context;
    public ArrayList<ModelRecipeAdmin> recipeAdminArrayList;
    private ArrayList<ModelRecipeAdmin> filterList;
    private FilterRecipeAdmin filter;


    private static final String TAG = "PRODUCTS_TAG";

    public AdapterRecipeAdmin(Context context, ArrayList<ModelRecipeAdmin> recipeAdminArrayList) {
        this.context = context;
        this.recipeAdminArrayList = recipeAdminArrayList;
        this.filterList = recipeAdminArrayList;
    }

    @NonNull
    @Override
    public HolderRecipeAdmin onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_recipe_admin, parent, false);
        return new HolderRecipeAdmin(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderRecipeAdmin holder, int position) {
        ModelRecipeAdmin modelRecipeAdmin = recipeAdminArrayList.get(position);

        String recipeId = modelRecipeAdmin.getRecipeId();
        String howTo = modelRecipeAdmin.getHowTo();
        String uid = modelRecipeAdmin.getUid();
        String timestamp = modelRecipeAdmin.getTimestamp();
        String recipeName = modelRecipeAdmin.getRecipeName();
        String personCount = modelRecipeAdmin.getPersonCount();
        String time = modelRecipeAdmin.getTime();
        String imageUrl = modelRecipeAdmin.getImageUrl();

        holder.recipeNameTv.setText(recipeName);
        holder.recipePersonCountTv.setText(personCount);
        holder.recipeTimeTv.setText(time);

        try {
            Glide.with(context)
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_add)
                    .into(holder.recipeIv);
        } catch (Exception e) {
            Log.e(TAG, "onBindViewHolder: ", e);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, RecipeDetailsAdminActivity.class);
                intent.putExtra("recipeId", modelRecipeAdmin.getRecipeId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return recipeAdminArrayList.size();
    }

    @Override
    public Filter getFilter() {
        if (filter == null){
            filter = new FilterRecipeAdmin(this, filterList);
        }
        return filter;
    }

    class HolderRecipeAdmin extends RecyclerView.ViewHolder {

        private ImageView recipeIv;
        private TextView recipeNameTv;
        private TextView recipeTimeTv;
        private TextView recipePersonCountTv;

        public HolderRecipeAdmin(@NonNull View itemView) {
            super(itemView);

            recipeIv = itemView.findViewById(R.id.recipeIv);
            recipeNameTv = itemView.findViewById(R.id.recipeNameTv);
            recipeTimeTv = itemView.findViewById(R.id.recipeTimeTv);
            recipePersonCountTv = itemView.findViewById(R.id.recipePersonCountTv);
        }
    }
}
